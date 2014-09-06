package org.craft.atom.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

import lombok.ToString;

import org.craft.atom.io.Channel;
import org.craft.atom.io.IoConnectorX;
import org.craft.atom.io.IoHandler;
import org.craft.atom.io.IoProcessorX;
import org.craft.atom.io.IoProtocol;
import org.craft.atom.nio.api.NioConnectorConfig;
import org.craft.atom.nio.spi.NioBufferSizePredictorFactory;
import org.craft.atom.nio.spi.NioChannelEventDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connects to server based TCP.
 * 
 * @author mindwind
 * @version 1.0, Feb 24, 2013
 */
@ToString(callSuper = true, of = { "connectQueue",  "cancelQueue" })
public class NioTcpConnector extends NioConnector {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(NioTcpConnector.class);
	
	
	private final Queue<ConnectionCall>          connectQueue     = new ConcurrentLinkedQueue<ConnectionCall>();
	private final Queue<ConnectionCall>          cancelQueue      = new ConcurrentLinkedQueue<ConnectionCall>();
	private final AtomicReference<ConnectThread> connectThreadRef = new AtomicReference<ConnectThread>()       ;
	
	
	// ~ ------------------------------------------------------------------------------------------------------------

	
	public NioTcpConnector(IoHandler handler) {
		super(handler);
	}
	
	public NioTcpConnector(IoHandler handler, NioConnectorConfig config) {
		super(handler, config);
	}
	
	public NioTcpConnector(IoHandler handler, NioConnectorConfig config, NioChannelEventDispatcher dispatcher) {
		super(handler, config, dispatcher);
	}

	public NioTcpConnector(IoHandler handler, NioConnectorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory) {
		super(handler, config, dispatcher, predictorFactory);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	@Override
	protected Future<Channel<byte[]>> connectByProtocol(SocketAddress remoteAddress, SocketAddress localAddress) throws IOException {
		SocketChannel sc = null;
		boolean success = false;
		try {
            sc = newSocketChannel(localAddress);
            if (sc.connect(remoteAddress)) {
                // return true immediately, as established a local connection,
            	Future<Channel<byte[]>> future = executorService.submit(new ConnectionCall(sc));
            	success = true;
            	LOG.debug("[CRAFT-ATOM-NIO] Established local connection");
                return future;
            }
            success = true;
        } finally {
            if (!success && sc != null) {
                try {
                    close(sc);
                } catch (IOException e) {
                	LOG.warn("[CRAFT-ATOM-NIO] Close exception", e);
                }
            }
        }
        
        ConnectionCall cc = new ConnectionCall(sc);
        FutureTask<Channel<byte[]>> futureTask = new FutureTask<Channel<byte[]>>(cc);
        cc.setFutureTask(futureTask);
        connectQueue.add(cc);
        
        startup();
        selector.wakeup();
        
		return futureTask;
	}
	
	private SocketChannel newSocketChannel(SocketAddress localAddress) throws IOException {
		SocketChannel sc = SocketChannel.open();
		
		// if size > 64K, for client sockets, setReceiveBufferSize() must be called before connecting the socket to its remote peer.
        int receiveBufferSize = config.getDefaultReadBufferSize();
        if (receiveBufferSize > 65535) {
            sc.socket().setReceiveBufferSize(receiveBufferSize);
        }

        if (localAddress != null) {
            sc.socket().bind(localAddress);
        }
        sc.configureBlocking(false);
        return sc;
	}
	
	private void startup() {
		ConnectThread ct = connectThreadRef.get();
		if (ct == null) {
			ct = new ConnectThread();
			if (connectThreadRef.compareAndSet(null, ct)) {
				executorService.execute(ct);
			}
		}
	}
	
	private void close(SocketChannel sc) throws IOException {
		LOG.debug("[CRAFT-ATOM-NIO] Close socket channel={}", sc);

		SelectionKey key = sc.keyFor(selector);

		if (key != null) {
			key.cancel();
		}

		sc.close();
	}
	
	private int register() throws IOException {
		int n = 0;
		for (;;) {
			ConnectionCall cc = connectQueue.poll();
			if (cc == null) {
				break;
			}

			SocketChannel sc = cc.getSocketChannel();
			try {
				sc.register(selector, SelectionKey.OP_CONNECT, cc);
				n++;
			} catch (Exception e) {
				close(sc);
				LOG.warn("[CRAFT-ATOM-NIO] Register connect event with exception", e);
			}
		}
		return n;
	}
	
	private int process() throws IOException {
		int n = 0;
		Iterator<SelectionKey> it = selector.selectedKeys().iterator();
		while (it.hasNext()) {
			SelectionKey key = it.next();
			ConnectionCall cc = (ConnectionCall) key.attachment();
			it.remove();

			boolean success = false;
			try {
				if (cc.getSocketChannel().finishConnect()) {
					// cancel finished key
					key.cancel();
					
					executorService.execute(cc.getFutureTask());
					n++;
				}
				success = true;
			} finally {
				if (!success) {
					// Connect failed, we have to cancel it.
					cancelQueue.offer(cc);
				}
			}
		}
		return n;
	}
	
	private void checkTimeout() {
		long now = System.currentTimeMillis();
		
		Iterator<SelectionKey> it = selector.keys().iterator();
		while (it.hasNext()) {
			ConnectionCall cc = (ConnectionCall) it.next().attachment();
			if (cc != null && now > cc.getDeadline()) {
				cancelQueue.offer(cc);
			}
		}
	}
	
	private int cancel() throws IOException {
		int n = 0;

		for (;;) {
			ConnectionCall cc = cancelQueue.poll();
			if (cc == null) {
				break;
			}

			SocketChannel sc = cc.getSocketChannel();

			try {
				close(sc);
			} finally {
				n++;
			}
		}

		if (n > 0) {
			selector.wakeup();
		}

		return n;
	}
	
	private void shutdown0() throws IOException {
		// clear queues
		this.connectQueue.clear();
		this.cancelQueue.clear();
		
		// close acceptor selector
		this.selector.close();
		
		// shutdown all the processor in the pool
		pool.shutdown();

		LOG.debug("[CRAFT-ATOM-NIO] Shutdown connector successful");
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	private class ConnectThread implements Runnable {
		public void run() {
			int num = 0;
			while (selectable) {
				try {
					// the timeout for select shall be smaller of the connect timeout or 1 second
					int timeout = (int) Math.min(config.getConnectTimeoutInMillis(), 1000);
					int selected = selector.select(timeout);

					// register new connect request
					num += register();

					// process connect event
					if (selected > 0) {
						num -= process();
					}

					// check connection timeout
					checkTimeout();

					// cancel
					num -= cancel();

					// last get a chance to exit infinite loop
					if (num == 0) {
						connectThreadRef.set(null);
						if (connectQueue.isEmpty()) {
							break;
						}

						if (!connectThreadRef.compareAndSet(null, this)) {
							break;
						}
					}
				} catch (Exception e) {
					LOG.error("[CRAFT-ATOM-NIO] Connect exception", e);
				}
			}

			// if shutdown == true, shutdown the connector
			if (shutdown) {
				try {
					shutdown0();
				} catch (Exception e) {
					LOG.error("[CRAFT-ATOM-NIO] Shutdown exception", e);
				}
			}
		}
	}
	
	private class ConnectionCall implements Callable<Channel<byte[]>> {
		
		
		private FutureTask<Channel<byte[]>> futureTask;
		private SocketChannel socketChannel;
		private long deadline;

		
		public ConnectionCall(SocketChannel socketChannel) {
			super();
			this.socketChannel = socketChannel;
			this.deadline = System.currentTimeMillis() + config.getConnectTimeoutInMillis();
		}

		@Override
		public Channel<byte[]> call() throws Exception {
			NioByteChannel channel = new NioTcpByteChannel(socketChannel, config, predictorFactory.newPredictor(config.getMinReadBufferSize(), config.getDefaultReadBufferSize(), config.getMaxReadBufferSize()), dispatcher);
			NioProcessor processor = pool.pick(channel);
			processor.setProtocol(IoProtocol.TCP);
			channel.setProcessor(processor);
			processor.add(channel);
			
			// finish connect, fire channel opened event
//			processor.fireChannelOpened(channel);
			return channel;
		}

		public SocketChannel getSocketChannel() {
			return socketChannel;
		}

		public long getDeadline() {
			return deadline;
		}

		public FutureTask<Channel<byte[]>> getFutureTask() {
			return futureTask;
		}

		public void setFutureTask(FutureTask<Channel<byte[]>> futureTask) {
			this.futureTask = futureTask;
		}
	}
	
	@Override
	public IoConnectorX x() {
		IoConnectorX icx = new IoConnectorX();
		icx.setSelectable(selectable);
		icx.setAliveChannels(new HashSet<Channel<byte[]>>(pool.getIdleTimer().aliveChannels()));
		for (ConnectionCall cc : connectQueue) {
			icx.addConnectingChannel(cc.getSocketChannel());
		}
		for (ConnectionCall cc : cancelQueue) {
			icx.addDisconnectingChannel(cc.getSocketChannel());
		}
		NioProcessor[] nps = pool.getPool();
		for (NioProcessor np : nps) {
			IoProcessorX ipx = np.x();
			icx.add(ipx);
		}
		return icx;
	}

}

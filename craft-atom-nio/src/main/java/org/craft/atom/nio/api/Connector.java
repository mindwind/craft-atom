package org.craft.atom.nio.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.nio.AbstractSession;
import org.craft.atom.nio.Abstractor;
import org.craft.atom.nio.PartialOrderedEventDispatcher;
import org.craft.atom.nio.Processor;
import org.craft.atom.nio.ProcessorPool;
import org.craft.atom.nio.TcpSession;
import org.craft.atom.nio.spi.Handler;

/**
 * Connects to endpoint, communicates with the server, and fires events.
 *
 * @author Hu Feng
 * @version 1.0, 2011-11-29
 */
public class Connector extends Abstractor {
	
	private static final Log LOG = LogFactory.getLog(ConnectThread.class);
	
	private Handler handler;
	private ConnectorConfig config;
	private ExecutorService executorService = Executors.newCachedThreadPool();
	private final Queue<ConnectionCall> connectQueue = new ConcurrentLinkedQueue<ConnectionCall>();
	private final Queue<ConnectionCall> cancelQueue = new ConcurrentLinkedQueue<ConnectionCall>();
	private final AtomicReference<ConnectThread> connectThreadRef = new AtomicReference<ConnectThread>();
	private volatile Selector selector;
	private volatile boolean selectable = false;
	private volatile boolean shutdown = false;
	private final ProcessorPool pool;
	
	// ~ --------------------------------------------------------------------------------------------------------------
	
	/**
	 *  Constructs a new connector default configuration.
	 *  
	 *  @param handler
	 */
	public Connector(Handler handler) {
		this(new ConnectorConfig(), handler);
	}
	
	/**
	 *  Constructs a new connector the specified configuration.
	 *  
	 * @param config
	 * @param handler
	 */
	public Connector(ConnectorConfig config, Handler handler) {
		this.config = (config == null ? new ConnectorConfig() : config);
		this.handler = handler;
		this.eventDispatcher = new PartialOrderedEventDispatcher(this.config.getExecutorSize());
		this.pool = new ProcessorPool(config, handler, eventDispatcher);
		try {
			init();
		} catch (IOException e) {
			throw new RuntimeException("Failed to construct.", e);
		} finally {
			if (!selectable) {
				if (selector != null) {
					try {
						selector.close();
					} catch (IOException e) {
						LOG.warn("unexpected exception", e);
					}
				}
			}
		}
	}
	
	// ~ --------------------------------------------------------------------------------------------------------------
	
	/**
	 * Asynchronous connects to the specified remote address
	 * 
	 * @param ip
	 * @param port
	 * @return The <code>Future</code> instance which is completed when the connection attempt initiated by this call succeeds or fails.
	 */
	public final Future<Session> connect(String ip, int port) {
		SocketAddress remoteAddress = new InetSocketAddress(ip, port);
		return connect(remoteAddress);
	}
	
	/**
	 * Asynchronous connects to the specified remote address.
	 * 
	 * @param remoteAddress
	 * @return The <code>Future</code> instance which is completed when the connection attempt initiated by this call succeeds or fails.
	 */
	public final Future<Session> connect(SocketAddress remoteAddress) {
        return connect(remoteAddress, null);
    }
	
	/**
	 * Asynchronous connects to the specified remote address and bindins to the specified local address.
	 * 
	 * @param remoteAddress
	 * @param localAddress
	 * @return The <code>Future</code> instance which is completed when the connection attempt initiated by this call succeeds or fails.
	 */
	public Future<Session> connect(SocketAddress remoteAddress, SocketAddress localAddress) {
		if (!this.selectable) {
			throw new IllegalStateException("The connector is already shutdown.");
		}

		if (remoteAddress == null) {
			throw new IllegalArgumentException("Remote address is null.");
		}
		
		if (getHandler() == null) {
			throw new IllegalStateException("Handler is not set!");
		}
		
		return connect0(remoteAddress, localAddress);
	}
	
	private Future<Session> connect0(SocketAddress remoteAddress, SocketAddress localAddress) {
		SocketChannel sc = null;
		boolean success = false;
		try {
            sc = newSocketChannel(localAddress);
            if (sc.connect(remoteAddress)) {
                // return true immediately, as established a local connection,
            	Future<Session> future = executorService.submit(new ConnectionCall(sc));
            	success = true;
            	
            	if (LOG.isDebugEnabled()) {
            		LOG.debug("Established local connection");
            	}
            	
                return future;
            }

            success = true;
        } catch (IOException e) {
            LOG.error("Connect error", e);
            throw new RuntimeException(e);
        } finally {
            if (!success && sc != null) {
                try {
                    close(sc);
                } catch (IOException e) {
                	LOG.warn("Unexpected exception caught", e);
                }
            }
        }
        
        ConnectionCall cc = new ConnectionCall(sc);
        FutureTask<Session> futureTask = new FutureTask<Session>(cc);
        cc.setFutureTask(futureTask);
        connectQueue.add(cc);
        
        startup();
        selector.wakeup();
        
		return futureTask;
	}
	
	public void shutdown() {
		this.selectable = false;
		this.shutdown = true;
		this.selector.wakeup();
	}
	
	private void shutdown0() throws IOException {
		// clear queues
		this.connectQueue.clear();
		this.cancelQueue.clear();
		
		// close acceptor selector
		this.selector.close();
		
		// shutdown all the processor in the pool
		pool.shutdown();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Shutdown connector successful!");
		}
		
		// TODO fire acceptor shutdown event
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
	
	 protected void close(SocketChannel sc) throws IOException {
		 	LOG.info("close channel = " + sc);
		 
	        SelectionKey key = sc.keyFor(selector);
	        
	        if (key != null) {
	            key.cancel();
	        }
	        
	        sc.close();
	    }
	
	private SocketChannel newSocketChannel(SocketAddress localAddress) throws IOException {
		SocketChannel sc = SocketChannel.open();
		
		// if size > 64K, for client sockets, setReceiveBufferSize() must be called before connecting the socket to its remote peer.
        int receiveBufferSize = config.getReadBufferSize();
        if (receiveBufferSize > 65535) {
            sc.socket().setReceiveBufferSize(receiveBufferSize);
        }

        if (localAddress != null) {
            sc.socket().bind(localAddress);
        }
        sc.configureBlocking(false);
        return sc;
	}
	
	private void init() throws IOException {
		selectable = true;
		selector = Selector.open();
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
				LOG.warn("Register connect event with exception", e);
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
					// The connection failed, we have to cancel it.
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
	
	// ~ ---------------------------------------------------------------------------------------------------------------
	
    private class ConnectThread implements Runnable {
        public void run() {
            int num = 0;
            while(selectable) {
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
					LOG.error("Unexpected exception caught while connect", e);
				}
            }
            
			// if shutdown == true, shutdown the connector
			if (shutdown) {
				try {
					shutdown0();
				} catch (Exception e) {
					LOG.error("Unexpected exception caught while shutdown", e);
				}
			}
        }
    }
	
	private class ConnectionCall implements Callable<Session> {
		
		private FutureTask<Session> futureTask;
		private SocketChannel channel;
		private long deadline;

		public ConnectionCall(SocketChannel socketChannel) {
			super();
			this.channel = socketChannel;
			this.deadline = System.currentTimeMillis() + getConfig().getConnectTimeoutInMillis();
		}

		@Override
		public Session call() throws Exception {
			AbstractSession session = new TcpSession(channel, getConfig());
			Processor processor = pool.get(session);
			session.setProcessor(processor);
			processor.add(session);
			
			return session;
		}

		public SocketChannel getSocketChannel() {
			return channel;
		}

		public long getDeadline() {
			return deadline;
		}

		public FutureTask<Session> getFutureTask() {
			return futureTask;
		}

		public void setFutureTask(FutureTask<Session> futureTask) {
			this.futureTask = futureTask;
		}
	}
	
	// ~ ---------------------------------------------------------------------------------------------------------------

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public ConnectorConfig getConfig() {
		return config;
	}

	public void setConfig(ConnectorConfig config) {
		this.config = config;
	}

}

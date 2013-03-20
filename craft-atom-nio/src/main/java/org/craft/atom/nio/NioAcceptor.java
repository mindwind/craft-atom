package org.craft.atom.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.io.IoAcceptor;
import org.craft.atom.io.IoHandler;
import org.craft.atom.io.IoProtocol;
import org.craft.atom.nio.api.NioAcceptorConfig;
import org.craft.atom.nio.api.NioTcpAcceptor;
import org.craft.atom.nio.api.NioUdpAcceptor;
import org.craft.atom.nio.spi.NioBufferSizePredictorFactory;
import org.craft.atom.nio.spi.NioChannelEventDispatcher;

/**
 * Accepts incoming connection based TCP or datagram based UDP, communicates with clients, and fires events.
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 * @see NioTcpAcceptor
 * @see NioUdpAcceptor
 */
abstract public class NioAcceptor extends NioReactor implements IoAcceptor {
	
	private static final Log LOG = LogFactory.getLog(NioAcceptor.class);
	
	protected Selector selector;
	protected volatile boolean selectable = false;
	protected NioAcceptorConfig config;
	protected final NioProcessorPool pool;
	
	/** Lock object for bind/unbind */
	protected final Object lock = new Object();
	
	/** End flag for bind/unbind operation */
	protected volatile boolean endFlag = false;
	
	/** Bind/unbind exception reference */
	protected IOException exception;
	
	/** Wait for bindding addresses */
	protected final Set<SocketAddress> bindAddresses = new HashSet<SocketAddress>();
	
	/** Wait for unbinding addresses */
	protected final Set<SocketAddress> unbindAddresses = new HashSet<SocketAddress>();
	
	/** Already bound addresses and the server socket channel */
	protected final Map<SocketAddress, SelectableChannel> boundmap = new ConcurrentHashMap<SocketAddress, SelectableChannel>();
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	/**
	 * Constructs a new nio acceptor with default configuration, binds to the specified local address port.
	 * 
	 * @param handler
	 * @param port
	 */
	public NioAcceptor(IoHandler handler, int port) {
		this(handler, new NioAcceptorConfig(), port);
	}
	
	/**
	 * Constructs a new nio acceptor with specified configuration, binds to the specified local address port.
	 * 
	 * @param handler
	 * @param config
	 * @param port
	 */
	public NioAcceptor(IoHandler handler, NioAcceptorConfig config, int port) {
		this(handler, config, new InetSocketAddress(port));
	}
	
	/**
	 * Constructs a new nio acceptor with specified configuration and dispatcher, binds to the specified local address port.
	 * 
	 * @param handler
	 * @param config
	 * @param dispatcher
	 * @param port
	 */
	public NioAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, int port) {
		this(handler, config, dispatcher, new InetSocketAddress(port));
	}
	
	/**
	 * Constructs a new nio acceptor with specified configuration, dispatcher and predictor factory, binds to the specified local address port.
	 * 
	 * @param handler
	 * @param config
	 * @param dispatcher
	 * @param predictorFactory
	 * @param port
	 */
	public NioAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory, int port) {
		this(handler, config, dispatcher, predictorFactory, new InetSocketAddress(port));
	}
	
	/**
	 * Constructs a new nio acceptor with default configuration, and binds the specified socket addresses.
	 * 
	 * @param handler
	 * @param firstLocalAddress
	 * @param otherLocalAddresses
	 */
	public NioAcceptor(IoHandler handler, SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
		this(handler, new NioAcceptorConfig(), firstLocalAddress, otherLocalAddresses);
	}
	
	/**
	 * Constructs a new acceptor the specified configuration, and binds the specified socket addresses.
	 * 
	 * @param handler
	 * @param config
	 * @param firstLocalAddress
	 * @param otherLocalAddresses
	 */
	public NioAcceptor(IoHandler handler, NioAcceptorConfig config, SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
		this(handler, config, new NioOrderedThreadPoolChannelEventDispatcher(config.getExecutorSize(), config.getTotalEventSize()), new NioAdaptiveBufferSizePredictorFactory(), firstLocalAddress, otherLocalAddresses);
	}
	
	/**
	 * Constructs a new acceptor the specified configuration and dispatcher, binds the specified socket addresses.
	 * 
	 * @param handler
	 * @param config
	 * @param dispatcher
	 * @param firstLocalAddress
	 * @param otherLocalAddresses
	 */
	public NioAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
		this(handler, config, dispatcher, new NioAdaptiveBufferSizePredictorFactory(), firstLocalAddress, otherLocalAddresses);
	}
	
	/**
	 * Constructs a new acceptor the specified configuration, dispatcher and predictor factory, binds the specified socket addresses.
	 * 
	 * @param handler
	 * @param config
	 * @param dispatcher
	 * @param predictor
	 * @param firstLocalAddress
	 * @param otherLocalAddresses
	 */
	public NioAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory, SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
		this(handler, config, dispatcher, predictorFactory);
		
		try {
			bind(firstLocalAddress, otherLocalAddresses);
		} catch (IOException e) {
			throw new RuntimeException("Failed to construct.", e);
		} finally {
			if (!selectable && selector != null) {
				try {
					selector.close();
				} catch (IOException e) {
					LOG.warn("Unexpected exception caught", e);
				}
			}
		}
	}
	
	/**
	 * Constructs a new nio acceptor with default configuration, but not binds to any address.
	 * 
	 * @param handler
	 */
	public NioAcceptor(IoHandler handler) {
		this(handler, new NioAcceptorConfig(), new NioOrderedThreadPoolChannelEventDispatcher(), new NioAdaptiveBufferSizePredictorFactory());
	}
	
	/**
	 * Constructs a new nio acceptor with specified configuration,  but not binds to any address.
	 * 
	 * @param handler
	 * @param config
	 */
	public NioAcceptor(IoHandler handler, NioAcceptorConfig config) {
		this(handler, config, new NioOrderedThreadPoolChannelEventDispatcher(config.getExecutorSize(), config.getTotalEventSize()), new NioAdaptiveBufferSizePredictorFactory());
	}
	
	/**
	 * Constructs a new nio acceptor with specified configuration and dispatcher, but not binds to any address.
	 * 
	 * @param handler
	 * @param config
	 * @param dispatcher
	 */
	public NioAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher) {
		this(handler, config, dispatcher, new NioAdaptiveBufferSizePredictorFactory());
	}
	
	/**
	 * Constructs a new acceptor the specified configuration, dispatcher and predictor, but not binds to any address.
	 * 
	 * @param handler
	 * @param config
	 * @param dispatcher
	 * @param predictor
	 */
	public NioAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory) {
		if (handler == null) {
			throw new IllegalArgumentException("Handler should not be null!");
		}
		
		this.handler = handler;
		this.config = (config == null ? new NioAcceptorConfig() : config);
		this.dispatcher = dispatcher;
		this.predictorFactory = predictorFactory;
		this.pool = new NioProcessorPool(config, handler, dispatcher);
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	/**
	 * Init nio acceptor to ready state for bind socket address.
	 * 
	 * @throws IOException
	 */
	public void init() throws IOException {
		selector = Selector.open();
		selectable = true;
		new AcceptThread().start();
	}
	
	/**
	 * Binds to specified local port with any local address and start to accept incoming connections.
	 * 
	 * @param port
	 * @throws throw if bind failed.
	 */
	synchronized public void bind(int port) throws IOException {
		bind(new InetSocketAddress(port));
	}
	
	/**
	 * Binds to the specified local addresses and start to accept incoming connections. 
	 * If any address binding failed then rollback the already bound addresses. 
	 * Bind operation is fail fast, if encounter the first bind exception then throw it immediately.
	 * 
	 * @param firstLocalAddress
	 * @param otherLocalAddresses
	 * @throws throw if bind failed.
	 */
	synchronized public void bind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) throws IOException {
		if (!this.selectable) {
            init();
        }
		
		if (firstLocalAddress == null) {
			throw new IllegalArgumentException("Need a local address to bind");
		}

		List<SocketAddress> localAddresses = new ArrayList<SocketAddress>(2);
		localAddresses.add(firstLocalAddress);

		if (otherLocalAddresses != null) {
			for (SocketAddress address : otherLocalAddresses) {
				localAddresses.add(address);
			}
		}
		
		bindAddresses.addAll(localAddresses);
		
		if (!bindAddresses.isEmpty()) {
			synchronized (lock) {
				// wake up for unblocking the select() to process binding addresses
				selector.wakeup();

				// wait for bind result
				wait0();
			}
		}
	}
	
	private void wait0() throws IOException {
		while (!this.endFlag) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				throw new IOException(e);
			}
		}

		// reset end flag
		this.endFlag = false;

		if (this.exception != null) {
			IOException e = exception;
			this.exception = null;
			throw e;
		}
	}
	
	private void bind0() {
		if (!bindAddresses.isEmpty()) {
			for (SocketAddress address : bindAddresses) {
				boolean success = false;
				try {
					bindByProtocol(address);
					success = true;

					if (LOG.isDebugEnabled()) { LOG.debug("Bind address = " + address); }
				} catch (IOException e) {
					exception = e;
				} finally {
					if (!success) {
						rollback();
						break;
					}
				}
			}
			
			bindAddresses.clear();
			
			// notify bind end
			synchronized (lock) {
				endFlag = true;
				lock.notifyAll();
			}
		}
	}
	
	/**
	 * Rollback already bound address
	 */
	protected void rollback() {
		 Iterator<Entry<SocketAddress, SelectableChannel>> it = boundmap.entrySet().iterator();
		 while(it.hasNext()) {
			 Entry<SocketAddress, SelectableChannel> entry = it.next();
			 try {
				 close(entry.getValue());
			 } catch (IOException e) {
				 LOG.warn("Unexpected exception caught when rollback bind operation!", e);
			 } finally {
				 it.remove();
			 }
		 }
	}
	
	private void close(SelectableChannel sc) throws IOException {
		if (sc != null) {
			SelectionKey key = sc.keyFor(selector);
			if (key != null) {
				key.cancel();
			}
			sc.close();
		}
	}
	
	/** 
	 * Shutdown the acceptor, once do it the acceptor should be disposed because it is useless 
	 */
	public void shutdown() {
		this.selectable = false;
		this.selector.wakeup();
	}
	
	private void shutdown0() throws IOException {
		// clear bind/unbind addresses cache
		this.bindAddresses.clear();
		this.unbindAddresses.clear();
		
		// close all opened server socket channel
		for (SelectableChannel sc : boundmap.values()) {
			close(sc);
		}
		
		// close acceptor selector
		this.selector.close();
		
		// shutdown all the processor in the pool
		pool.shutdown();

		if (LOG.isDebugEnabled()) { LOG.debug("Shutdown acceptor successful!"); }
	}
	
	/**
	 * Get bound addresses
	 * 
	 * @return
	 */
	public Set<SocketAddress> getBoundAddresses() {
		return boundmap.keySet();
	}
	
	/**
	 * Unbinds specified local addresses that is already bound to and stops to accept incoming connections at the port. 
	 * 
	 * @param port
	 * @throws IOException throw if unbind failed
	 */
	synchronized public final void unbind(int port) throws IOException {
		unbind(new InetSocketAddress(port));
	}
	
	/**
	 * Unbinds specified local addresses that is already bound to and stops to accept incoming connections at the specified addresses. 
	 * All connections with these addresses will be closed.
	 * 
	 * <p><b>NOTE:</b> This method returns silently if no local address is bound yet.
	 * 
	 * @param firstLocalAddress
	 * @param otherLocalAddresses
	 * @throws IOException throw if unbind failed
	 */
	synchronized public final void unbind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) throws IOException {
		if (firstLocalAddress == null) {
			return;
		}

		List<SocketAddress> localAddresses = new ArrayList<SocketAddress>(2);
		if (boundmap.containsKey(firstLocalAddress)) {
			localAddresses.add(firstLocalAddress);
		}

		if (otherLocalAddresses != null) {
			for (SocketAddress address : otherLocalAddresses) {
				if (boundmap.containsKey(address)) {
					localAddresses.add(address);
				}
			}
		}
		
		unbindAddresses.addAll(localAddresses);
		
		if(!unbindAddresses.isEmpty()) {
			synchronized (lock) {
				// wake up for unblocking the select() to process unbinded addresses
				selector.wakeup();
				
				// wait for unbind result
				wait0();
			}
		}
	}
	
	/**
	 * Unbind at once according to specified type.
	 */
	private void unbind0() {
		if (!unbindAddresses.isEmpty()) {
			for (SocketAddress address : unbindAddresses) {
				try {
					if (boundmap.containsKey(address)) {
						SelectableChannel sc = boundmap.get(address);
						close(sc);
						boundmap.remove(address);
					}

					if (LOG.isDebugEnabled()) { LOG.debug("Unbind address = " + address); }
				} catch (IOException e) {
					exception = e;
				} 
			}
			
			unbindAddresses.clear();
			
			// notify bind end
			synchronized (lock) {
				endFlag = true;
				lock.notifyAll();
			}
		}
	}
	
	private void accept() {
		Iterator<SelectionKey> it = selector.selectedKeys().iterator();
		while (it.hasNext()) {
			SelectionKey key = it.next();
			it.remove();
			NioByteChannel channel = acceptByProtocol(key);
			if (channel != null) {
				NioProcessor processor = pool.pick(channel);
				processor.setProtocol(IoProtocol.TCP);
				channel.setProcessor(processor);
				processor.add(channel);
			}
		}
	}
	
	/**
	 * Bind at once according to protocol type.
	 * 
	 * @param address
	 */
	protected abstract void bindByProtocol(SocketAddress address) throws IOException;	
	
	/**
	 * Accept at once according to protocol type.
	 * 
	 * @param key
	 * @return
	 */
	protected abstract NioByteChannel acceptByProtocol(SelectionKey key);
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	private class AcceptThread extends Thread {
		public void run() {
			while (selectable) {
				try {
					int selected = selector.select();
					
					if (selected > 0) {
						accept();
					}
					
					// bind addresses to listen
					bind0();
					
					// unbind canceled addresses
					unbind0();
				} catch (Exception e) {
					LOG.error("Unexpected exception caught while accept", e);
				}
			}
			
			// if selectable == false, shutdown the acceptor
			try {
				shutdown0();
			} catch (Exception e) {
				LOG.error("Unexpected exception caught while shutdown", e);
			}
		}
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return String
				.format("NioAcceptor [selector=%s, selectable=%s, config=%s, pool=%s, lock=%s, endFlag=%s, exception=%s, bindAddresses=%s, unbindAddresses=%s, boundmap=%s, handler=%s, dispatcher=%s, predictorFactory=%s]",
						selector, selectable, config, pool, lock, endFlag,
						exception, bindAddresses, unbindAddresses, boundmap,
						handler, dispatcher, predictorFactory);
	}

}

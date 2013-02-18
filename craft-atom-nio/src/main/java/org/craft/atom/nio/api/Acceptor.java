package org.craft.atom.nio.api;

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
import org.craft.atom.nio.AbstractSession;
import org.craft.atom.nio.Abstractor;
import org.craft.atom.nio.PartialOrderedEventDispatcher;
import org.craft.atom.nio.Processor;
import org.craft.atom.nio.ProcessorPool;
import org.craft.atom.nio.spi.Handler;

/**
 * Accepts incoming connection based TCP or datagram based UDP, communicates with clients, and fires events.
 * 
 * @author Hu Feng
 * @version 1.0, 2011-11-7
 */
public abstract class Acceptor extends Abstractor {

	private static final Log LOG = LogFactory.getLog(Acceptor.class);
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	protected Selector selector;
	protected volatile boolean selectable = false;
	protected AcceptorConfig config;
	protected final ProcessorPool pool;
	
	/** Lock object for bind/unbind */
	protected final Object lock = new Object();
	
	/** End flag for bind/unbind operation */
	protected volatile boolean endFlag = false;
	
	/** Bind/unbind exception reference */
	protected IOException exception;
	
	/** Wait for bindding addresses */
	protected final Set<SocketAddress> bindAddresses = new HashSet<SocketAddress>();
	
	/** Wait for unbinding addresses */
	private final Set<SocketAddress> unbindAddresses = new HashSet<SocketAddress>();
	
	/** Already bound addresses and the server socket channel */
	protected final Map<SocketAddress, SelectableChannel> boundmap = new ConcurrentHashMap<SocketAddress, SelectableChannel>();

	// ~ -------------------------------------------------------------------------------------------------------------
	
	/**
	 * Constructs a new acceptor with default configuration, and binds to the specified local address port.
	 * 
	 * @param handler
	 * @param prot
	 */
	public Acceptor(Handler handler, int port) {
		this(handler, new AcceptorConfig(), new InetSocketAddress(port));
	}
	
	/**
	 * Constructs a new acceptor with specified configuration, and binds to the specified local address port.
	 * 
	 * @param handler
	 * @param config
	 * @param prot
	 */
	public Acceptor(Handler handler, AcceptorConfig config, int port) {
		this(handler, config, new InetSocketAddress(port));
	}

	/**
	 * Constructs a new acceptor with default configuration, and binds the socoket addresses.
	 * 
	 * @param handler
	 * @param firstLocalAddress
	 * @param otherLocalAddresses
	 */
	public Acceptor(Handler handler, SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
		this(handler, new AcceptorConfig(), firstLocalAddress, otherLocalAddresses);
	}

	/**
	 * Constructs a new acceptor the specified configuration
	 * 
	 * @param handler
	 * @param config
	 * @param firstLocalAddress
	 * @param otherLocalAddresses
	 */
	public Acceptor(Handler handler, AcceptorConfig config, SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
		if (handler == null) {
			throw new IllegalArgumentException("handler should not be null!");
		}
		
		this.config = (config == null ? new AcceptorConfig() : config);
		this.handler = handler;
		this.eventDispatcher = new PartialOrderedEventDispatcher(this.config.getExecutorSize());
		this.pool = new ProcessorPool(config, handler, eventDispatcher);
		
		try {
			init();
			bind(firstLocalAddress, otherLocalAddresses);
		} catch (IOException e) {
			throw new RuntimeException("Failed to construct.", e);
		} finally {
			if (!selectable) {
				if (selector != null) {
					try {
						selector.close();
					} catch (IOException e) {
						LOG.warn("Unexpected exception caught", e);
					}
				}
			}
		}
	}

	// ~ -------------------------------------------------------------------------------------------------------------
	
	/** 
	 * shutdown the acceptor, once do it the acceptor should be disposed because it is useless 
	 */
	public void shutdown() {
		this.selectable = false;
		this.selector.wakeup();
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
	 * Binds to specified local port with any local address and start to accept incoming connections.
	 * 
	 * @param port
	 * @throws throw if bind failed.
	 */
	synchronized public void bind(int port) throws IOException {
		bind(new InetSocketAddress(port));
	}
	
	/**
	 * Binds to the specified local addresses and start to accept incoming connections. If any address binding failed then
	 * rollback the already binding addresses. Bind is fail fast, if encounter the first bind exception then throw it immediately.
	 * 
	 * @param firstLocalAddress
	 * @param otherLocalAddresses
	 * @throws throw if bind failed.
	 */
	synchronized public void bind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) throws IOException {
		if (!this.selectable) {
            throw new IllegalStateException("The acceptor is already shutdown.");
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
	
	/**
	 * Unbinds specified local addresses that this service is bound to and stops to accept incoming connections. 
	 * 
	 * @param port
	 * @throws IOException throw if unbind failed
	 */
	synchronized public final void unbind(int port) throws IOException {
		unbind(new InetSocketAddress(port));
	}
	
	/**
	 * Unbinds specified local addresses that this service is bound to and stops to accept incoming connections. 
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

	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	private void accept() {
		Iterator<SelectionKey> it = selector.selectedKeys().iterator();
		while (it.hasNext()) {
			SelectionKey key = it.next();
			it.remove();
			AbstractSession session = (AbstractSession) acceptByProtocol(key);
			
			if (session != null) {
				Processor processor = pool.get(session);
				session.setProcessor(processor);
				processor.add(session);
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

					if (LOG.isDebugEnabled()) {
						LOG.debug("Unbind address = " + address);
					}
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
	
	private void close(SelectableChannel sc) throws IOException {
		if(sc != null) {
			SelectionKey key = sc.keyFor(selector);
			if (key != null) {
				key.cancel();
			}
			sc.close();
		}
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

		if (LOG.isDebugEnabled()) {
			LOG.debug("Shutdown acceptor successful!");
		}
	}
	
	private void init() throws IOException {
		selectable = true;
		selector = Selector.open();
		new AcceptThread().start();
	}
	
	private void bind0() {
		if (!bindAddresses.isEmpty()) {
			for (SocketAddress address : bindAddresses) {
				boolean success = false;
				try {
					bindByProtocol(address);
					success = true;

					if (LOG.isDebugEnabled()) {
						LOG.debug("Bind address = " + address);
					}
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
	
	// ~ -------------------------------------------------------------------------------------------------------------
		
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
	protected abstract Session acceptByProtocol(SelectionKey key);
	
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
	
	// ~ ---------------------------------------------------------------------------------------------------------------
	
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
	
	// ~ ---------------------------------------------------------------------------------------------------------------

	public AcceptorConfig getConfig() {
		return config;
	}

	public void setConfig(AcceptorConfig config) {
		this.config = config;
	}

}

package org.craft.atom.nio;

import lombok.ToString;

import org.craft.atom.io.IoHandler;
import org.craft.atom.nio.spi.NioChannelEventDispatcher;

/**
 * A processor pool, use this pool internally to perform better in a multi-core environment.
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
@ToString(of = { "pool", "config" })
public class NioProcessorPool {
	
	private final NioProcessor[] pool;
	private final NioConfig config;
	private final NioChannelEventDispatcher dispatcher;
	private final IoHandler handler;
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	public NioProcessorPool(NioConfig config, IoHandler handler, NioChannelEventDispatcher dispatcher) {
		if (config == null) {
			throw new IllegalArgumentException("config is null!");
		}
		
		int size = config.getProcessorPoolSize();
		if (size < 1) {
			size = 1;
		}
		
		this.pool = new NioProcessor[size];
		this.config = config;
		this.handler = handler;
		this.dispatcher = dispatcher;
		NioChannelIdleTimer.getInstance().init(dispatcher, handler, config.getIoTimeoutInMillis());
		fill(pool);
	}
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	private void fill(NioProcessor[] pool) {
		if (pool == null) {
			return;
		}

		for (int i = 0; i < pool.length; i++) {
			pool[i] = new NioProcessor(config, handler, dispatcher);
		}
	}
	
	/**
	 * shutdown the pool
	 */
	public void shutdown() {
		for (int i = 0; i < pool.length; i++) {
			pool[i].shutdown();
		}
	}
	
	/**
	 * Pick a nio processor object.
	 * 
	 * @param channel
	 * @return a nio processor.
	 */
	public NioProcessor pick(NioByteChannel channel) {
		return pool[Math.abs((int) (channel.getId() % pool.length))];
	}
	
}

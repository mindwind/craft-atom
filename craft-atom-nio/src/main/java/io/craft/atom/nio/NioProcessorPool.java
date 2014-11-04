package io.craft.atom.nio;

import io.craft.atom.io.IoHandler;
import io.craft.atom.nio.spi.NioChannelEventDispatcher;
import lombok.Getter;
import lombok.ToString;


/**
 * A processor pool, use this pool internally to perform better in a multi-core environment.
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
@ToString(of = { "pool", "config" })
public class NioProcessorPool {
	
	
	@Getter private final NioProcessor[]            pool      ;
	@Getter private final NioConfig                 config    ;
	@Getter private final NioChannelEventDispatcher dispatcher;
	@Getter private final IoHandler                 handler   ;
	@Getter private final NioChannelIdleTimer       idleTimer ;
	
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	
	public NioProcessorPool(NioConfig config, IoHandler handler, NioChannelEventDispatcher dispatcher) {
		if (config == null) {
			throw new IllegalArgumentException("config is null!");
		}
		
		int size = config.getProcessorPoolSize();
		if (size < 1) {
			size = 1;
		}
		
		this.pool       = new NioProcessor[size];
		this.config     = config;
		this.handler    = handler;
		this.dispatcher = dispatcher;
		this.idleTimer  = new NioChannelIdleTimer(dispatcher, handler, config.getIoTimeoutInMillis());
		fill(pool);
	}
	
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	
	private void fill(NioProcessor[] pool) {
		if (pool == null) {
			return;
		}

		for (int i = 0; i < pool.length; i++) {
			pool[i] = new NioProcessor(config, handler, dispatcher, idleTimer);
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

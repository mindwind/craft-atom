package org.craft.atom.nio;

import java.util.Random;

import org.craft.atom.nio.api.AbstractConfig;
import org.craft.atom.nio.api.Session;
import org.craft.atom.nio.spi.EventDispatcher;
import org.craft.atom.nio.spi.Handler;

/**
 * A processor pool, use this pool internally to perform better in a multi-core
 * environment.
 * 
 * @author Hu Feng
 * @version 1.0, 2011-12-3
 */
public class ProcessorPool {

	private final Processor[] pool;
	private final AbstractConfig config;
	private final Handler handler;
	private final EventDispatcher eventDispatcher;
	private final Random random = new Random();

	public ProcessorPool(AbstractConfig config, Handler handler, EventDispatcher eventDispatcher) {
		if (config == null) {
			throw new IllegalArgumentException("config is null!");
		}
		
		int size = config.getProcessorPoolSize();
		if (size < 1) {
			size = 1;
		}
		this.pool = new Processor[size];
		this.config = config;
		this.handler = handler;
		this.eventDispatcher = eventDispatcher;
		fill(pool);
	}
	
	/**
	 * Get a processor.
	 * 
	 * @param session
	 * @return
	 */
	public Processor get(Session session) {
		return pool[Math.abs((int) session.getId()) % pool.length];
	}
	
	/**
	 * Get a processor by protocol
	 * 
	 * @param protocol
	 * @return
	 */
	public Processor get(Protocol protocol) {
		Processor processor = pool[random.nextInt(pool.length) % pool.length];
		processor.setProtocol(protocol);
		return processor;
	}
	
	/**
	 * shutdown the pool
	 */
	public void shutdown() {
		for (int i = 0; i < pool.length; i++) {
			pool[i].shutdown();
		}
	}

	private void fill(Processor[] pool) {
		if (pool == null) {
			return;
		}

		for (int i = 0; i < pool.length; i++) {
			pool[i] = new Processor(config, handler, eventDispatcher);
		}
	}

}

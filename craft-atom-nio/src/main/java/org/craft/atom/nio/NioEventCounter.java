package org.craft.atom.nio;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Counter of total event
 * 
 * @author mindwind
 * @version 1.0, Feb 25, 2013
 */
public class NioEventCounter {
	
	private static final NioEventCounter INSTANCE = new NioEventCounter();
	private final AtomicInteger counter = new AtomicInteger(0);
	
	private NioEventCounter() {}
	
	public static NioEventCounter getInstance() {
		return INSTANCE;
	}
	
	int increse() {
		return counter.incrementAndGet();
	}
	
	int decrese() {
		return counter.decrementAndGet();
	}
	
	int current() {
		return counter.get();
	}

	@Override
	public String toString() {
		return String.format("NioEventCounter [counter=%s]", counter);
	}
	
}

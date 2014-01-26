package org.craft.atom.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.ToString;

/**
 * Named thread factory.<br>
 * When using thread pool with your own named thread is a better practice.
 * 
 * @author Hu Feng
 * @version 1.0, Nov 19, 2012
 */
@ToString
public class NamedThreadFactory implements ThreadFactory {
	
	
	private static final AtomicInteger threadNumber = new AtomicInteger(1);
	
	
	private final String  name  ;
	private final boolean daemon;
	
	
	public NamedThreadFactory(String prefix) {
		this(prefix, false);
	}

	public NamedThreadFactory(String prefix, boolean daemon) {
		this.name = prefix + "-pool-thread-";
		this.daemon = daemon;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r, name + threadNumber.getAndIncrement());
		t.setDaemon(daemon);
		return t;
	}

}

package org.craft.atom.nio.spi;

import java.util.concurrent.Semaphore;

import lombok.ToString;

import org.craft.atom.nio.NioByteChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Base implementation of {@link NioChannelEventDispatcher}
 * 
 * @author mindwind
 * @version 1.0, Feb 27, 2013
 */
@ToString(of = { "semaphore" })
abstract public class AbstractNioChannelEventDispatcher implements NioChannelEventDispatcher {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractNioChannelEventDispatcher.class);
	
	
	protected final Semaphore semaphore;
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	public AbstractNioChannelEventDispatcher() {
		this(Integer.MAX_VALUE);
	}

	public AbstractNioChannelEventDispatcher(int totalEventSize) {
		if (totalEventSize <= 0) {
			totalEventSize = Integer.MAX_VALUE;
		}
		this.semaphore = new Semaphore(totalEventSize, false);
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	protected void beforeDispatch(NioByteChannel channel) {
		boolean b = channel.tryAcquire();
		if (!b) {
			channel.pause();
			LOG.warn("[CRAFT-ATOM-NIO]] Pause |channel={}, availablePermits={}|", channel, channel.availablePermits());
		}
		
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			LOG.warn("[CRAFT-ATOM-NIO] Semaphore acquire interrupt", e);
		}
	}
	
	protected void afterDispatch(NioByteChannel channel) {
		channel.release();
		if (channel.isPaused()) {
			channel.resume();
			LOG.warn("[CRAFT-ATOM-NIO]] Resume |channel={} availablePermits={}|", channel, channel.availablePermits());
		}
		
		semaphore.release();
	}

}

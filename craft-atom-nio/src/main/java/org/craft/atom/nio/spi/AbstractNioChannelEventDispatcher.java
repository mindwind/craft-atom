package org.craft.atom.nio.spi;

import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.nio.NioByteChannel;


/**
 * Base implementation of {@link NioChannelEventDispatcher}
 * 
 * @author mindwind
 * @version 1.0, Feb 27, 2013
 */
abstract public class AbstractNioChannelEventDispatcher implements NioChannelEventDispatcher {
	
	private static final Log LOG = LogFactory.getLog(AbstractNioChannelEventDispatcher.class);
	
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
			LOG.warn("Pause channel=" + channel + ", availablePermits=" + channel.availablePermits());
		}
		
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			LOG.warn(e.getMessage(), e);
		}
	}
	
	protected void afterDispatch(NioByteChannel channel) {
		channel.release();
		if (channel.isPaused()) {
			channel.resume();
			LOG.warn("Resume channel=" + channel + ", availablePermits=" + channel.availablePermits());
		}
		
		semaphore.release();
	}

}

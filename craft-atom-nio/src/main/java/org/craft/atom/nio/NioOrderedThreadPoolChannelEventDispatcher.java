package org.craft.atom.nio;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.io.ChannelEvent;
import org.craft.atom.nio.spi.NioChannelEventDispatcher;
import org.craft.atom.util.NamedThreadFactory;

/**
 * An {@link NioOrderedThreadPoolChannelEventDispatcher} that maintains order of {@link NioHandlerByteChannelEvent} in the same channel.
 * It use a new thread pool for event dispatch, isolate io process thread and event process thread.
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
public class NioOrderedThreadPoolChannelEventDispatcher implements NioChannelEventDispatcher {
	
	private static final Log LOG = LogFactory.getLog(NioOrderedThreadPoolChannelEventDispatcher.class);
	
	private final BlockingQueue<NioByteChannel> channelQueue;
	private final Executor executor;
	private final Semaphore semaphore;

	// ~ ------------------------------------------------------------------------------------------------------------
	
	NioOrderedThreadPoolChannelEventDispatcher() {
		this(Runtime.getRuntime().availableProcessors() * 8, Integer.MAX_VALUE);
	}
	
	NioOrderedThreadPoolChannelEventDispatcher(int executorSize, int totalEventSize) {
		if (executorSize <= 0) {
			executorSize = Runtime.getRuntime().availableProcessors() * 8;
		}
		
		if (totalEventSize <= 0) {
			totalEventSize = Integer.MAX_VALUE;
		}
		
		this.semaphore = new Semaphore(totalEventSize, false);
		this.channelQueue = new LinkedBlockingQueue<NioByteChannel>();
		this.executor = Executors.newFixedThreadPool(executorSize, new NamedThreadFactory("craft-atom-nio-executor"));
		for (int i = 0; i < executorSize; i++) {
			executor.execute(new Worker());
		}
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	@Override
	public void dispatch(ChannelEvent<byte[]> event) {
		NioByteChannel channel = (NioByteChannel) event.getChannel();
		beforeDispatch(channel);
		dispatch0(event, channel);
	}
	
	private void dispatch0(ChannelEvent<byte[]> event, NioByteChannel channel) {
		channel.add(event);
		if (!channel.isEventProcessing()) {
			channelQueue.offer(channel);
		}
	}
	
	private void beforeDispatch(NioByteChannel channel) {
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
	
	private void afterDispatch(NioByteChannel channel) {
		channel.release();
		if (channel.isPaused()) {
			channel.resume();
			LOG.warn("Resume channel=" + channel + ", availablePermits=" + channel.availablePermits());
		}
		
		semaphore.release();
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	private class Worker implements Runnable {
		
		private static final int SPIN_COUNT = 256;
		
		private void fire(NioByteChannel channel) {
			int count = 0;
			Queue<ChannelEvent<byte[]>> q = channel.getEventQueue();
			for (ChannelEvent<byte[]> event = q.poll(); event != null; event = q.poll()) {
				event.fire();
				afterDispatch(channel);
				count++;
				if (count > SPIN_COUNT) {
					// quit loop to avoid stick same worker thread by same session
					break;
				}
			}
		}

		@Override
		public void run() {
			try {
				for (NioByteChannel channel = channelQueue.take(); channel != null; channel = channelQueue.take()) {					
					// first check any worker is processing this channel? if any other worker thread is processing this event with same channel, just ignore it.
					synchronized (channel) {
						if (!channel.isEventProcessing()) {
							channel.setEventProcessing(true);
						} else {
							continue;
						}
					}
					
					// fire events with same channel
					fire(channel);
					
					// last reset processing flag and quit current thread processing
					channel.setEventProcessing(false);
					
					// if remaining events, so re-insert to channel queue
					if (channel.getEventQueue().size() > 0 && !channel.isEventProcessing()) {
						channelQueue.offer(channel);
					}
				}
			} catch (Throwable t) {
				LOG.warn(t.getMessage(), t);
			}
		}
	}

}

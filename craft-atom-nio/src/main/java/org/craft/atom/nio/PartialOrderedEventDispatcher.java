package org.craft.atom.nio;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.nio.api.Session;
import org.craft.atom.nio.spi.EventDispatcher;

/**
 * An {@link EventDispatcher} that maintains order of {@link Event} with same session.
 * 
 * @author Hu Feng
 * @version 1.0, 2011-12-15
 */
public class PartialOrderedEventDispatcher implements EventDispatcher {
	
	private static final Log LOG = LogFactory.getLog(PartialOrderedEventDispatcher.class);
	
	private BlockingQueue<AbstractSession> squeue = new LinkedBlockingQueue<AbstractSession>();
	private Executor executor;
	
	public PartialOrderedEventDispatcher(int executorSize) {
		super();
		if (executorSize <= 0) {
			throw new IllegalArgumentException("executor size <= 0");
		}
		
		executor = Executors.newFixedThreadPool(executorSize);
		for (int i = 0; i < executorSize; i++) {
			executor.execute(new Worker());
		}
	}
	
	// ~ ---------------------------------------------------------------------------------------------------------------
	
	@Override
	public void dispatch(Event event) {
		AbstractSession s = (AbstractSession) event.getSession();
		s.add(event);
		if (!s.isEventProcessing()) {
			squeue.offer(s);
		}
	}
	
	// ~ ---------------------------------------------------------------------------------------------------------------
	
	private class Worker implements Runnable {
		
		private static final int SPIN_COUNT = 256;
		
		private void fire(Session s) {
			int count = 0;
			Queue<Event> q = s.getEventQueue();
			for (Event event = q.poll(); event != null; event = q.poll()) {
				event.fire();
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
				for (AbstractSession s = squeue.take(); s != null; s = squeue.take()) {					
					// first check any worker is processing this session? if any other worker thread is processing this event with same session, just ignore it.
					synchronized (s) {
						if (!s.isEventProcessing()) {
							s.setEventProcessing(true);
						} else {
							continue;
						}
					}
					
					// fire events with same session
					fire(s);
					
					// last reset processing flag and quit current thread processing
					s.setEventProcessing(false);
					
					// if remaining events, so re-insert to session queue
					if (s.getEventQueue().size() > 0 && !s.isEventProcessing()) {
						squeue.offer(s);
					}
				}
			} catch (Throwable t) {
				LOG.warn(t.getMessage(), t);
			}
		}
	}
	
	public static void main(String[] args) {
		new PartialOrderedEventDispatcher(3);
	}

}

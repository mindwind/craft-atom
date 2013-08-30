package org.craft.atom.util.schedule;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A timing-wheel optimized for approximated I/O timeout scheduling.<br>
 * {@link TimingWheel} creates a new thread whenever it is instantiated and started, so don't create many instances.
 * <p>
 * <b>The classic usage as follows:</b><br>
 * <li>using timing-wheel manage any object timeout</li>
 * <pre>
 *    // Create a timing-wheel with 60 ticks, and every tick is 1 second.
 *    private static final TimingWheel<CometChannel> TIMING_WHEEL = new TimingWheel<CometChannel>(1, 60, TimeUnit.SECONDS);
 *    
 *    // Add expiration listener and start the timing-wheel.
 *    static {
 *    	TIMING_WHEEL.addExpirationListener(new YourExpirationListener());
 *    	TIMING_WHEEL.start();
 *    }
 *    
 *    // Add one element to be timeout approximated after 60 seconds
 *    TIMING_WHEEL.add(e);
 *    
 *    // Anytime you can cancel count down timer for element e like this
 *    TIMING_WHEEL.remove(e);
 * </pre>
 * 
 * After expiration occurs, the {@link ExpirationListener} interface will be invoked and the expired object will be 
 * the argument for callback method {@link ExpirationListener#expired(Object)}
 * <p>
 * As timing-wheel use map structure internal, so any element added to timing-wheel should implement 
 * its own <tt>equals(o)</tt> and <tt>hashCode()</tt> method.
 * <p>
 * {@link TimingWheel} is based on <a href="http://cseweb.ucsd.edu/users/varghese/">George Varghese</a> and Tony Lauck's paper,
 * <a href="http://cseweb.ucsd.edu/users/varghese/PAPERS/twheel.ps.Z">'Hashed and Hierarchical Timing Wheels: data structures 
 * to efficiently implement a timer facility'</a>.  More comprehensive slides are located <a href="http://www.cse.wustl.edu/~cdgill/courses/cs6874/TimingWheels.ppt">here</a>.
 * 
 * @author mindwind
 * @version 1.0, Sep 20, 2012
 */
@ToString(of = { "tickDuration", "ticksPerWheel", "currentTickIndex", "wheel", "indicator"})
public class TimingWheel<E> {
	
	private static final Log LOG = LogFactory.getLog(TimingWheel.class);
	
	private final long tickDuration;
	private final int ticksPerWheel;
	private volatile int currentTickIndex = 0;
	
	private final CopyOnWriteArrayList<ExpirationListener<E>> expirationListeners = new CopyOnWriteArrayList<ExpirationListener<E>>();
	private final ArrayList<Slot<E>> wheel;
	private final Map<E, Slot<E>> indicator = new ConcurrentHashMap<E, Slot<E>>();
	
	private final AtomicBoolean shutdown = new AtomicBoolean(false);
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private Thread workerThread;
	
	// ~ -------------------------------------------------------------------------------------------------------------

	/**
	 * Construct a timing wheel.
	 * 
	 * @param tickDuration   tick duration with specified time unit.
	 * @param ticksPerWheel
	 * @param timeUnit
	 */
	public TimingWheel(int tickDuration, int ticksPerWheel, TimeUnit timeUnit) {
		if (timeUnit == null) {
            throw new NullPointerException("unit");
        }
		if (tickDuration <= 0) {
            throw new IllegalArgumentException("tickDuration must be greater than 0: " + tickDuration);
        }
        if (ticksPerWheel <= 0) {
            throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + ticksPerWheel);
        }
		
        this.wheel = new ArrayList<Slot<E>>();
		this.tickDuration = TimeUnit.MILLISECONDS.convert(tickDuration, timeUnit);
		this.ticksPerWheel = ticksPerWheel + 1;
		
		for (int i = 0; i < this.ticksPerWheel; i++) {
			wheel.add(new Slot<E>(i));
		}
		wheel.trimToSize();
		
		workerThread = new Thread(new TickWorker(), "Timing-Wheel");
	}
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	public void start() {
		if (shutdown.get()) {
            throw new IllegalStateException("Cannot be started once stopped");
        }

        if (!workerThread.isAlive()) {
            workerThread.start();
        }
	}
	
	public boolean stop() {
		if (!shutdown.compareAndSet(false, true)) {
			return false;
		}
		
		boolean interrupted = false;
		while (workerThread.isAlive()) {
			workerThread.interrupt();
			try {
				workerThread.join(100);
			} catch (InterruptedException e) {
				interrupted = true;
			}
		}
		if (interrupted) {
            Thread.currentThread().interrupt();
        }
		
		return true;
	}
	
	public void addExpirationListener(ExpirationListener<E> listener) {
        expirationListeners.add(listener);
    }
	
	public void removeExpirationListener(ExpirationListener<E> listener) {
        expirationListeners.remove(listener);
    }
	
	/**
	 * Add a element to {@link TimingWheel} and start to count down its life-time.
	 * 
	 * @param e
	 * @return remain time to be expired in millisecond.
	 */
	public long add(E e) {
		// at any time just only one e(element) in the timing-wheel, all operations(add,remove,put) on this element should be synchronized.
		synchronized(e) {
			checkAdd(e);
			
			int previousTickIndex = getPreviousTickIndex();
			Slot<E> slot = wheel.get(previousTickIndex);
			slot.add(e);
			indicator.put(e, slot);
			
			return (ticksPerWheel - 1) * tickDuration;
		}
	}
	
	private void checkAdd(E e) {
		Slot<E> slot = indicator.get(e);
		if (slot != null) {
			slot.remove(e);
		}
	}
	
	private int getPreviousTickIndex() {
		lock.readLock().lock();
		try {
			int cti = currentTickIndex;
			if (cti == 0) {
				return ticksPerWheel - 1;
			}
			
			return cti - 1;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Removes the specified element from timing wheel.
	 * 
	 * @param e
	 * @return <tt>true</tt> if this timing wheel contained the specified
	 *         element
	 */
	public boolean remove(E e) {
		synchronized (e) {
			Slot<E> slot = indicator.get(e);
			if (slot == null) {
				return false;
			}

			indicator.remove(e);
			return slot.remove(e) != null;
		}
	}

	private void notifyExpired(int idx) {
		Slot<E> slot = wheel.get(idx);
		Set<E> elements = slot.elements();
		for (E e : elements) {
			slot.remove(e);
			synchronized (e) {
				Slot<E> latestSlot = indicator.get(e);
				if (slot.equals(latestSlot)) {
					indicator.remove(e);
				}
			}
			for (ExpirationListener<E> listener : expirationListeners) {
				listener.expired(e);
			}
		}
	}
	
	/**
	 * Returns the number of elements within timing wheel.
	 * 
	 * @return
	 */
	public int size() {
		return indicator.size();
	}
	
	// ~ -------------------------------------------------------------------------------------------------------------
	 
	private class TickWorker implements Runnable {

		private long startTime;
		private long tick;

		@Override
		public void run() {
			startTime = System.currentTimeMillis();
			tick = 1;

			for (int i = 0; !shutdown.get(); i++) {
				if (i == wheel.size()) {
					i = 0;
				}
				lock.writeLock().lock();
				try {
					currentTickIndex = i;
				} finally {
					lock.writeLock().unlock();
				}
				notifyExpired(currentTickIndex);
				waitForNextTick();
			}
		}

		private void waitForNextTick() {
			for (;;) {
                long currentTime = System.currentTimeMillis();
                long sleepTime = tickDuration * tick - (currentTime - startTime);
                if (LOG.isDebugEnabled()) { LOG.debug("wait for next tick sleep time=" + sleepTime); }
                
                if (sleepTime <= 0) {
                    break;
                }

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    return;
                }
            }
			
			tick++;
		}
	}
	
	@ToString
	@EqualsAndHashCode(of = "id")
	private static class Slot<E> {
		
		private int id;
		private Map<E, E> elements = new ConcurrentHashMap<E, E>();
		
		public Slot(int id) {
			this.id = id;
		}

		public void add(E e) {
			elements.put(e, e);
		}
		
		public E remove(E e) {
			return elements.remove(e);
		}
		
		public Set<E> elements() {
			return elements.keySet();
		}
	}
}

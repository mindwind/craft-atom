package org.craft.atom.lock;

import java.util.concurrent.TimeUnit;

/**
 * {@code DLock} is the abbreviation of distributed lock.
 * <p>
 * A distributed lock is a tool for controlling access to a shared resource by multiple threads or processes.
 * Commonly, a distributed lock provides exclusive access to a shared resource: only one thread at a time can acquire the lock and all access to the shared resource requires that the lock be acquired first.
 * 
 * @author mindwind
 * @version 1.0, Nov 19, 2012
 */
public interface DLock {
	
	/**
	 * Acquires the lock with specified lock key if it is free within the given TTL time.
	 * <p>
	 * If the lock is available this method returns immediately with the value <code>true</code>, otherwise <code>false</code>
	 * 
	 * @param lockKey	lock key resource, which associated with a shared resource
	 * @param ttl		lock time to live
	 * @param unit		time unit for ttl
	 * @return          <code>true</code> if the lock was acquired and <code>false</code> if lock acquired failed.
	 */
	boolean tryLock(String lockKey, int ttl, TimeUnit unit);
	
	/**
	 * Releases the lock with specified lock key.
	 * <p>
	 * <b>Tip:</b> make sure you hold the lock, you release lock by invoking {@link #unlock(String)}
	 *  
	 * @param  lockKey
	 * @return <code>true</code> if the lock was released and <code>false</code> if lock released failed.
	 */
	boolean unlock(String lockKey);
}

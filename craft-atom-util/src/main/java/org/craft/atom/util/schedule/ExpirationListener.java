package org.craft.atom.util.schedule;

/**
 * A listener for expired object events.
 * 
 * @author Hu Feng
 * @version 1.0, Sep 20, 2012
 * @see TimingWheel
 */
public interface ExpirationListener<E> {
	
	/**
	 * Invoking when a expired event occurs.
	 * 
	 * @param expiredObject
	 */
	void expired(E expiredObject);
	
}

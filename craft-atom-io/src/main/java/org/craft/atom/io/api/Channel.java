package org.craft.atom.io.api;

/**
 * A nexus for I/O operations.
 *
 * <p> A channel represents an open connection to an entity such as a hardware
 * device, a file, a network socket, or a program component that is capable of
 * performing one or more distinct I/O operations, for example reading or
 * writing.
 *
 * <p> A channel is either open or closed.  A channel is open upon creation,
 * and once closed it remains closed.  Once a channel is closed, any attempt to
 * invoke an I/O operation upon it will cause a exception to be thrown.
 *
 * <p> Channels are, in general, intended to be safe for multi-threaded access
 * as described in the specifications of the interfaces and classes that extend
 * and implement this interface.
 * 
 * @author mindwind
 * @version 1.0, Feb 20, 2013
 */
public interface Channel<D> {
	
	/**
	 * Return a unique identifier for this channel.
	 * Every channel has its own id which is different from each other.
	 * 
	 * @return
	 */
	String getId();
	
	/**
	 * Closes this channel.
	 * 
	 * <p> After a channel is closed, any further attempt to invoke I/O
     * operations upon it will cause a exception to be thrown.
     *
     * <p> If this channel is already closed then invoking this method has no
     * effect.
     *
     * <p> This method may be invoked at any time.  If some other thread has
     * already invoked it, however, then another invocation will block until
     * the first invocation is complete, after which it will return without
     * effect.
	 */
	void close();
	
	/**
	 * Write some data to another peer of the channel.
	 * 
	 * @param data
	 * @return <tt>true</tt> once data write successful.
	 */
	boolean write(D data);
	
	/**
     * Tells whether or not this channel is open.  </p>
     *
     * @return <tt>true</tt> if, and only if, this channel is open
     */
	boolean isOpen();
	
	/**
	 * Returns the value of the user-defined attribute of this session.
	 * 
	 * @param key
	 *            the key of the attribute, can not be <tt>null</tt>
	 * @return <tt>null</tt> if there is no attribute with the specified key
	 */
	Object getAttribute(Object key);
    
	/**
	 * Sets a user-defined attribute.
	 * 
	 * @param key
	 *            the key of the attribute , can not be <tt>null</tt>
	 * @param value
	 *            the value of the attribute, can not be <tt>null</tt>
	 * @return The old value of the attribute. <tt>null</tt> if it is new.
	 */
	Object setAttribute(Object key, Object value);

	/**
	 * Remove a user-defined attribute.
	 * 
	 * @param key
	 *            the key of the attribute , can not be <tt>null</tt>
	 */
	void removeAttribute(Object key);
    
	/**
	 * Returns <tt>true</tt> if this session contains the attribute with the specified <tt>key</tt>.
	 * 
	 * @param key
	 *            the key of the attribute , can not be <tt>null</tt>
	 * @return
	 */
	boolean containsAttribute(Object key);
}

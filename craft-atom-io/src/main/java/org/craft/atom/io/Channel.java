package org.craft.atom.io;

import java.net.SocketAddress;
import java.util.Queue;

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
	 * @return channel id
	 */
	long getId();
	
	/**
	 * Close this channel.
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
	 * Pause this channel.
	 * 
	 * <p> After a channel is paused, any further attempt to invoke I/O 
	 * operation upon it will return <tt>null</tt> or <tt>false</tt> indicate operation fail, 
	 * instead throw exception.
	 * 
	 * <p> If this channel is already paused then invoking this method has no
     * effect.
     *
     * <p> This method may be invoked at any time.  If some other thread has
     * already invoked it, however, then another invocation will block until
     * the first invocation is complete, after which it will return without
     * effect.
	 */
	void pause();
	
	/**
	 * Resume this channel from paused state.
	 * 
	 * <p> If this channel is already paused then invoking this method has no
     * effect.
     * 
     * <p> This method may be invoked at any time.  If some other thread has
     * already invoked it, however, then another invocation will block until
     * the first invocation is complete, after which it will return without
     * effect.
	 */
	void resume();
	
	/**
	 * Write some data to another peer of the channel.
	 * 
	 * @param data
	 * @return <tt>true</tt> once data write successful.
	 * @throws IllegalChannelStateException If channel state is not open.
	 */
	boolean write(D data) throws IllegalChannelStateException;
	
	/**
     * Tells whether or not this channel is open. 
     *
     * @return <tt>true</tt> if, and only if, this channel is open
     */
	boolean isOpen();
	
	/**
	 * Tells whether or not this channel is in closing.
	 * 
	 * @return <tt>true</tt> if, and only if, this channel is in closing
	 */
	boolean isClosing();
	
	/**
	 * Tells whether or not this channel is closed.
	 * 
	 * @return <tt>true</tt> if, and only if, this channel is closed
	 */
	boolean isClosed();
	
	/**
	 * Tells whether or not this channel is paused.
	 * 
	 * @return <tt>true</tt> if, and only if, this channel is paused
	 */
	boolean isPaused();
	
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
	 * @return the previous value associated with key, or <tt>null</tt> if there was no mapping for key.
	 */
	Object removeAttribute(Object key);
    
	/**
	 * Returns <tt>true</tt> if this session contains the attribute with the specified <tt>key</tt>.
	 * 
	 * @param key
	 *            the key of the attribute , can not be <tt>null</tt>
	 * @return <tt>true</tt> if contains a mapping for the specified key
	 */
	boolean containsAttribute(Object key);
	
	/**
     * @return the socket address of remote peer which is associated with this channel.
     */
    SocketAddress getRemoteAddress();

    /**
     * @return the socket address of local machine which is associated with this channel.
     */
    SocketAddress getLocalAddress();
    
    /**
     * Get the channel inner write queue.
     * The queue is used by the channel, so changes to the queue are reflected in the channel, and vice-versa.
     * 
     * @return the write queue if the channel implementor has a inner queue.
     */
    Queue<D> getWriteQueue();
}

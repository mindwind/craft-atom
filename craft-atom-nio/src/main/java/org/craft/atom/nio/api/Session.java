package org.craft.atom.nio.api;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.Queue;

import org.craft.atom.nio.Event;

/**
 * A session represents an interaction between two endpoints.
 * 
 * @author Hu Feng
 * @version 1.0, 2011-11-10
 */
public interface Session {

	/**
	 * Return a unique identifier for this session. Every session has its own ID which is different from each other.
	 */
	long getId();
	
	/**
     * Returns the socket address of remote peer.
     */
    SocketAddress getRemoteAddress();

    /**
     * Returns the socket address of local machine which is associated with this session.
     */
    SocketAddress getLocalAddress();
    
    /**
     * Returns the channel associated with this session.
     */
    SelectableChannel getChannel();
    
    /** 
     * Sets  the {@link SelectionKey} 
     */
    void setSelectionKey(SelectionKey key);
    
    /**
     * Returns the {@link SelectionKey}
     */
    SelectionKey getSelectionKey();
    
    /**
     * Sets max read buffer size.<br>
     * maxReadBufferSize >= readBufferSize >= minReadBufferSize > 0
     */
    void setMaxReadBufferSize(int size);
    
    /**
     * Returns max read buffer size
     */
    int getMaxReadBufferSize();
    
    /**
     * Sets min read buffer size<br>
     * maxReadBufferSize >= readBufferSize >= minReadBufferSize > 0
     */
    void setMinReadBufferSize(int size);
    
    /**
     * Returns min read buffer size
     */
    int getMinReadBufferSize();
    
    /**
     * Sets read buffer size, buffer size between min read buffer size and max read buffer size.<br>
     * if size < minReadBufferSize then set size = minReadBufferSize.<br>
     * if size > maxReadBufferSize then set size = maxReadBufferSize.
     */
    void setReadBufferSize(int size);
    
    /**
     * Returns read buffer size
     */
    int getReadBufferSize();
    
    /**
     * Sets max write buffer size.<br>
     * maxWriteBufferSize >= maxReadBufferSize.<br>
     * if maxWriteBufferSize < maxReadBufferSize set maxWriteBufferSize = maxReadBufferSize
     */
    void setMaxWriteBufferSize(int size);
    
    /**
     * Returns max write buffer size
     */
    int getMaxWriteBufferSize();
    
    /**
     * Returns write buffer queue
     */
    Queue<ByteBuffer> getWriteBufferQueue();
    
    /**
     * Returns event queue
     */
    Queue<Event> getEventQueue();
    
    /**
     * Close itself
     */
    void close();
    
    /**
     * Asynchronous write some bytes to remote peer.
     * 
     * @param bytes to be written
     */
    void write(byte[] bytes);
    
    /**
     *  Returns last io time with this session.
     */
    long getLastIoTime();
    
    /**
     *  Set last io time with this session.
     */
    void setLastIoTime(long lastIoTime);
    
    /**
     * Returns the value of the user-defined attribute of this session.
     *
     * @param key the key of the attribute
     * @return <tt>null</tt> if there is no attribute with the specified key
     */
    Object getAttribute(Object key);
    
    /**
     * Sets a user-defined attribute, if value is <tt>null</tt> remove this key.
     *
     * @param key   the key of the attribute
     * @param value the value of the attribute
     * @return The old value of the attribute.  <tt>null</tt> if it is new.
     */
    Object setAttribute(Object key, Object value);
    
    /**
     * Returns <tt>true</tt> if this session contains the attribute with the specified <tt>key</tt>.
     */
    boolean containsAttribute(Object key);
    
    /**
     * Represents session is in closing
     * 
     * @return
     */
    boolean isClosing(); 
    
    /**
     * Represents session is valid or not, if session is in closing or be closed it's invalid.
     * 
     * @return
     */
    boolean isValid();

}

package org.craft.atom.nio.spi;

import java.io.IOException;

import org.craft.atom.nio.api.Session;

/**
 * Handles all I/O events.
 *
 * @author Hu Feng
 * @version 1.0, 2011-11-29
 */
public interface Handler {
	
	/**
	 * Invoked when a connection has been opened.
	 */
	void sessionOpened(Session session);
	
	/**
	 * Invoked when a connection is closed.
	 */
	void sessionClosed(Session session);
	
	/**
	 * Invoked when a connection is idle, idle means there is no data transmission.
	 */
	void sessionIdle(Session session);
	
	/**
     * Invoked when some bytes is received.
     */
    void messageReceived(Session session, byte[] bytes);
    
    /**
     * Invoked when some bytes is sent out, which written by {@link Session#write(byte[])}.
     */
    void messageSent(Session session, byte[] bytes);
    
    /**
     * Invoked when any exception is thrown. If <code>cause</code> is an instance of
     * {@link IOException}, framework will close the connection automatically.
     */
    void exceptionCaught(Session session, Throwable cause);
}

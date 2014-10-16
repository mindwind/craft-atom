package org.craft.atom.io;

import java.io.IOException;

/**
 * Handles I/O events fired by <tt>craft-atom-io</tt> series component.
 * 
 * @author mindwind
 * @version 1.0, Feb 20, 2013
 */
public interface IoHandler {

	/**
	 * Invoked when channel opened.
	 * 
	 * @param channel
	 */
	void channelOpened(Channel<byte[]> channel);

	/**
	 * Invoked when channel closed.
	 * 
	 * @param channel
	 */
	void channelClosed(Channel<byte[]> channel);
	
	
	/**
	 * Invoked when channel is idle, idle means there is no data transmission (read or write).
	 * 
	 * @param channel
	 */
	void channelIdle(Channel<byte[]> channel);
	
	/**
	 * Invoked when channel has read some bytes.
	 * 
	 * @param channel
	 * @param bytes
	 */
	void channelRead(Channel<byte[]> channel, byte[] bytes);
	
	/**
	 * Invoked when channel would flush out some bytes.
	 * 
	 * @param channel
	 * @param bytes
	 */
	void channelFlush(Channel<byte[]> channel, byte[] bytes);
	
	/**
	 * Invoked when channel has written some bytes.
	 * 
	 * @param channel
	 * @param bytes
	 */
	void channelWritten(Channel<byte[]> channel, byte[] bytes);
	
	/**
	 * Invoked when any exception is thrown. 
	 * If <code>cause</code> is an instance of {@link IOException} channel should be close.
	 * 
	 * @param channel
	 * @param cause
	 */
	void channelThrown(Channel<byte[]> channel, Throwable cause);
}

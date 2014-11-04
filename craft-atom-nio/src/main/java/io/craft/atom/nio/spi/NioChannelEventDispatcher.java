package io.craft.atom.nio.spi;

import io.craft.atom.io.ChannelEvent;
import io.craft.atom.io.IoHandler;
import io.craft.atom.nio.NioOrderedDirectChannelEventDispatcher;
import io.craft.atom.nio.NioOrderedThreadPoolChannelEventDispatcher;


/**
 * Nio channel event dispatcher, dispatch all channel I/O events to {@link IoHandler}
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 * @see NioOrderedThreadPoolChannelEventDispatcher
 * @see NioOrderedDirectChannelEventDispatcher
 */
public interface NioChannelEventDispatcher {
	
	/**
	 * Dispatch event to handle.
	 * 
	 * @param event
	 */
	void dispatch(ChannelEvent<byte[]> event);
	
	/**
	 * Shutdown the dispatcher and dispose holden resources.
	 */
	void shutdown();
	
}

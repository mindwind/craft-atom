package org.craft.atom.nio;

import org.craft.atom.io.ChannelEvent;
import org.craft.atom.nio.spi.NioChannelEventDispatcher;

/**
 * An {@link NioChannelEventDispatcher} that maintains order of {@link NioHandlerByteChannelEvent} in the same channel.
 * It use io process thread pool to dispatch event.
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
public class NioOrderedDirectChannelEventDispatcher implements NioChannelEventDispatcher {

	@Override
	public void dispatch(ChannelEvent<byte[]> event) {
		event.fire();
	}

}

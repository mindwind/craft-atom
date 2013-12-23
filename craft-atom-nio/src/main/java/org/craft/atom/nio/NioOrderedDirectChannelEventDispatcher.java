package org.craft.atom.nio;

import lombok.ToString;

import org.craft.atom.io.ChannelEvent;
import org.craft.atom.nio.spi.AbstractNioChannelEventDispatcher;
import org.craft.atom.nio.spi.NioChannelEventDispatcher;

/**
 * An {@link NioChannelEventDispatcher} that maintains order of {@link NioByteChannelEvent} in the same channel.
 * It use io process thread pool to dispatch event.
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
@ToString(callSuper = true)
public class NioOrderedDirectChannelEventDispatcher extends AbstractNioChannelEventDispatcher {
	
	
	public NioOrderedDirectChannelEventDispatcher() {
		super();
	}

	public NioOrderedDirectChannelEventDispatcher(int totalEventSize) {
		super(totalEventSize);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------------

	
	@Override
	public void dispatch(ChannelEvent<byte[]> event) {
		NioByteChannel channel = (NioByteChannel) event.getChannel();
		beforeDispatch(channel);
		try {
			event.fire();
		} finally {
			afterDispatch(channel);
		}
	}

}

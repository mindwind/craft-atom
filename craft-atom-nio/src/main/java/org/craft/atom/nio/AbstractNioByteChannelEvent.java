package org.craft.atom.nio;

import lombok.ToString;

import org.craft.atom.io.AbstractChannelEvent;
import org.craft.atom.io.Channel;
import org.craft.atom.io.ChannelEvent;
import org.craft.atom.io.ChannelEventType;

/**
 * @author mindwind
 * @version 1.0, Feb 26, 2013
 */
@ToString(callSuper = true, of = "channel")
abstract public class AbstractNioByteChannelEvent extends AbstractChannelEvent implements ChannelEvent<byte[]> {
	
	
	protected final NioByteChannel channel;
	
	
	// ~ --------------------------------------------------------------------------------------------------------------

	
	AbstractNioByteChannelEvent(ChannelEventType type, NioByteChannel channel) {
		super(type);
		if (channel == null) {
            throw new IllegalArgumentException("channel == null");
        }
		this.channel = channel;
	}
	
	
	// ~ --------------------------------------------------------------------------------------------------------------
	

	@Override
	public Channel<byte[]> getChannel() {
		return channel;
	}

}

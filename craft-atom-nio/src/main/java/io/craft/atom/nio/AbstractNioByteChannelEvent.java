package io.craft.atom.nio;

import io.craft.atom.io.AbstractChannelEvent;
import io.craft.atom.io.Channel;
import io.craft.atom.io.ChannelEvent;
import io.craft.atom.io.ChannelEventType;
import lombok.ToString;


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

package org.craft.atom.io;

import lombok.Getter;
import lombok.ToString;

/**
 * Base implementation class for common concept of channel event.
 * 
 * @author mindwind
 * @version 1.0, Feb 26, 2013
 */
@ToString(of = "type")
abstract public class AbstractChannelEvent {
	
	
	@Getter protected final ChannelEventType type;
	
	
	// ~ -----------------------------------------------------------------------------------------------------------
	
	
	public AbstractChannelEvent(ChannelEventType type) {
		if (type == null) {
            throw new IllegalArgumentException("type == null");
        }
		this.type = type;
	}

}

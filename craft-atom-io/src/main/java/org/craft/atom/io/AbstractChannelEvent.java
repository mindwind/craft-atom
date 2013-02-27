package org.craft.atom.io;

/**
 * Base implementation class for common concept of channel event.
 * 
 * @author mindwind
 * @version 1.0, Feb 26, 2013
 */
abstract public class AbstractChannelEvent {
	
	protected final ChannelEventType type;
	
	public AbstractChannelEvent(ChannelEventType type) {
		if (type == null) {
            throw new IllegalArgumentException("type == null");
        }
		this.type = type;
	}

	public ChannelEventType getType() {
		return type;
	}

}

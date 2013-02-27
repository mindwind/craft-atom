package org.craft.atom.io;

/**
 * An enumeration that represents the type of {@link ChannelEvent}.
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 */
public enum ChannelEventType {
	
	/** When channel has been opened, fire this event */
	CHANNEL_OPENED,
	
	/** When channel has been closed, fire this event */
	CHANNEL_CLOSED,
	
	/** When channel has read some data, fire this event */
	CHANNEL_READ,
	
	/** When channel has written some data, fire this event */
	CHANNEL_WRITTEN,
	
	/** When channel has no data transmit for a while, fire this event */
	CHANNEL_IDLE,
	
	/** When channel operation throw exception, fire this event */
	CHANNEL_THROWN

}

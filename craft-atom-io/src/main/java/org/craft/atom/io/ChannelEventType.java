package org.craft.atom.io;

/**
 * An enumeration that represents the type of {@link ChannelEvent}.
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 */
public enum ChannelEventType {
	
	CHANNEL_OPENED,
	CHANNEL_CLOSED,
	CHANNEL_READ,
	CHANNEL_WRITTEN,
	CHANNEL_IDLE,
	CHANNEL_THROWN

}

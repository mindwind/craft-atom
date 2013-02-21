package org.craft.atom.nio;

/**
 * An enumeration that represents the type of {@link NioChannelEvent}.
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 */
public enum NioChannelEventType {
	
	CHANNEL_OPENED,
	CHANNEL_CLOSED,
	CHANNEL_RECEIVED,
	CHANNEL_SENT,
	CHANNEL_IDLE,
	CHANNEL_THROWN

}

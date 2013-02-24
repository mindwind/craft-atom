package org.craft.atom.io;

/**
 * The state of a {@link Channel}.
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 */
public enum ChannelState {
	
	/** Channel in OPEN state once a channel is created.  */
	OPEN,
	
	/** Channel is in CLOSING state for async close operation, means in closing process. */
	CLOSING,
	
	/** Channel ClOSED state, in this state channel can not be used. */
	CLOSED,
	
}

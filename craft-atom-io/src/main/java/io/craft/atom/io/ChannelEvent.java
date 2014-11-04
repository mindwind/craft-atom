package io.craft.atom.io;

/**
 * An I/O event associated with a {@link Channel}.
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
public interface ChannelEvent<D> {
	
	/**
     * @return the {@link Channel} which is associated with this event.
     */
    Channel<D> getChannel();
    
    /**
     * @return the event type.
     */
    ChannelEventType getType();
    
    /**
     * Fire the event.
     */
    void fire();

}

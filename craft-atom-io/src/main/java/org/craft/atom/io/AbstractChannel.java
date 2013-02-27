package org.craft.atom.io;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Base implementation class for common concept of channel.
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 */
abstract public class AbstractChannel {
	
	private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
	
	protected long id;
	protected Map<Object, Object> attributes = new ConcurrentHashMap<Object, Object>();
	protected volatile ChannelState state = ChannelState.OPEN;
	
	// ~ -----------------------------------------------------------------------------------------------------------
	
	public AbstractChannel() {
		id = ID_GENERATOR.incrementAndGet();
	}
	
	public AbstractChannel(long id) {
		this.id = id;
	}

	// ~ -----------------------------------------------------------------------------------------------------------
	
	public long getId() {
		return id;
	}
	
	public boolean isOpen() {
		return state == ChannelState.OPEN;
	}
	
	public boolean isClosing() {
		return state == ChannelState.CLOSING;
	}
	
	public boolean isClosed() {
		return state == ChannelState.CLOSED;
	}
	
	public boolean isPaused() {
		return state == ChannelState.PAUSED;
	}
	
	public void pause() {
		state = ChannelState.PAUSED;
	}
	
	public void resume() {
		state = ChannelState.OPEN;
	}
	
	public void close() {
		state = ChannelState.CLOSED;
	}
	
	public Object getAttribute(Object key) {
		if (key == null) {
            throw new IllegalArgumentException("key can not be null");
        }

        return attributes.get(key);
	}
	
	public Object setAttribute(Object key, Object value) {
		if (key == null || value == null) {
			throw new IllegalArgumentException("key & value can not be null");
        }
        
        return attributes.put(key, value);
	}
	
	public boolean containsAttribute(Object key) {
		if (key == null) {
            throw new IllegalArgumentException("key can not be null");
        }
		
		return attributes.containsKey(key);
	}
	
	public Object removeAttribute(Object key) {
		if (key == null) {
            throw new IllegalArgumentException("key can not be null");
        }
		
		return attributes.remove(key);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractChannel other = (AbstractChannel) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("AbstractChannel [id=%s, attributes=%s, state=%s]", id, attributes, state);
	}
	
}

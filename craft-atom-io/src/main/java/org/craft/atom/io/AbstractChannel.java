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
	public String toString() {
		return String.format("AbstractChannel [id=%s, attributes=%s, state=%s]", id, attributes, state);
	}
	
}

package org.craft.atom.io.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Base implementation class for {@link Channel}
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 */
abstract public class AbstractChannel {
	
	private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
	
	protected String id;
	protected volatile ChannelState state = ChannelState.OPEN;
	protected Map<Object, Object> attributes = new ConcurrentHashMap<Object, Object>();
	
	// ~ -----------------------------------------------------------------------------------------------------------
	
	public AbstractChannel() {
		id = Long.toString(ID_GENERATOR.incrementAndGet());
	}
	
	public AbstractChannel(String id) {
		this.id = id;
	}

	// ~ -----------------------------------------------------------------------------------------------------------
	
	public String getId() {
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
	
}

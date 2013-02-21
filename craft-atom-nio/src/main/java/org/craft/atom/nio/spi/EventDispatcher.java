package org.craft.atom.nio.spi;

import org.craft.atom.nio.Event;
import org.craft.atom.nio.PartialOrderedEventDispatcher;

/**
 * Event dispatcher dispatch all I/O events to {@link Handler}
 *
 * @author mindwind
 * @version 1.0, 2011-12-15
 * @see PartialOrderedEventDispatcher
 */
public interface EventDispatcher {
	
	/**
	 * Dispatch event to handle
	 * 
	 * @param event
	 */
	void dispatch(Event event);
	
}

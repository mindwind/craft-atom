package org.craft.atom.nio;

import org.craft.atom.nio.spi.EventDispatcher;
import org.craft.atom.nio.spi.Handler;

/**
 * An abstract object to be extended. 
 *
 * @author Hu Feng
 * @version 1.0, 2011-12-16
 */
public abstract class Abstractor {
	
	protected Handler handler;
	protected EventDispatcher eventDispatcher;
	
	public Handler getHandler() {
		return handler;
	}
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}
	public void setEventDispatcher(EventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}
	
}

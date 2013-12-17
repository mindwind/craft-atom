package org.craft.atom.io;



/**
 * Accepts I/O incoming request base on specific implementation. 
 * 
 * @author mindwind
 * @version 1.0, Mar 12, 2013
 */
public interface IoReactor {
	
	
	/**
     * Releases any resources allocated by this reactor.
     */
	void shutdown();
	
	/**
     * Returns the handler associates with the reactor
     */
	IoHandler getHandler();
	
}

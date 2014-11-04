package io.craft.atom.io;


/**
 * Abstracts reactor model, base api interface.
 * 
 * @author mindwind
 * @version 1.0, Mar 12, 2013
 * @see IoAcceptor
 * @see IoConnector
 * @see IoProcessor
 */
public interface IoReactor {
	
	
	/**
     * Releases any resources allocated by this reactor.
     */
	void shutdown();
	
	/**
     * Returns the io handler associates with the reactor
     */
	IoHandler getHandler();
	
}

package org.craft.atom.io;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * Accepts I/O incoming request base on specific implementation. 
 * 
 * @author mindwind
 * @version 1.0, Mar 12, 2013
 */
public interface IoAcceptor {
	
	/**
	 * Binds to specified local port with any local address and start to accept incoming request.
	 * 
	 * @param port
	 * @throws IOException
	 */
	void bind(int port) throws IOException;
	
	/**
	 * Binds to the specified local addresses and start to accept incoming request. 
	 * If any address binding failed then rollback the already bound addresses. 
	 * Bind operation is fail fast, if encounter the first bind exception then throw it immediately.
	 * 
	 * @param firstLocalAddress
	 * @param otherLocalAddresses
	 * @throws throw if bind failed.
	 */
	void bind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) throws IOException;
	
}

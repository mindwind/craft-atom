package org.craft.atom.io;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Set;

/**
 * Accepts I/O incoming request base on specific implementation. 
 * 
 * @author mindwind
 * @version 1.0, Mar 12, 2013
 * @see NioAcceptor
 */
public interface IoAcceptor extends IoReactor, IoAcceptorMBean {
	
	/**
	 * Binds to specified local port with any local address and start to accept incoming request.
	 * 
	 * @param  port
	 * @throws IOException
	 */
	void bind(int port) throws IOException;
	
	/**
	 * Binds to the specified local addresses and start to accept incoming request. 
	 * If any address binding failed then rollback the already bound addresses. 
	 * Bind operation is fail fast, if encounter the first bind exception then throw it immediately.
	 * 
	 * @param  firstLocalAddress
	 * @param  otherLocalAddresses
	 * @throws throw if bind failed.
	 */
	void bind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) throws IOException;
	
	/**
	 * Unbinds specified local addresses that is already bound to and stops to accept incoming connections at the port. 
	 * 
	 * @param  port
	 * @throws IOException throw if unbind failed
	 */
	void unbind(int port) throws IOException;
	
	/**
	 * Unbinds specified local addresses that is already bound to and stops to accept incoming connections at the specified addresses. 
	 * All connections with these addresses will be closed.
	 * 
	 * <p><b>NOTE:</b> This method returns silently if no local address is bound yet.
	 * 
	 * @param  firstLocalAddress
	 * @param  otherLocalAddresses
	 * @throws IOException throw if unbind failed
	 */
	void unbind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) throws IOException;
	
	
	/**
	 * Get currently bound addresses
	 * 
	 * @return bound addresses
	 */
	Set<SocketAddress> getBoundAddresses();

}

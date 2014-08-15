package org.craft.atom.rpc.spi;

import java.io.IOException;
import java.net.SocketAddress;


/**
 * RPC connector
 * 
 * @author mindwind
 * @version 1.0, Aug 14, 2014
 */
public interface RpcConnector {
	
	/**
	 * Connects to rpc server
	 *
	 * @return connection id
	 * @throws IOException If some other I/O error occurs
	 */
	long connect() throws IOException;
	
	/**
	 * Disconnects the connection with specified id.
	 *  
	 * @param connectionId
	 * @throws IOException If some other I/O error occurs
	 */
	void disconnect(long connectionId) throws IOException;
	
	/**
	 * Set address to connect.
	 * 
	 * @param address
	 */
	void setAddress(SocketAddress address);
	
}

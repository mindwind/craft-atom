package org.craft.atom.rpc.api;

import java.io.IOException;

/**
 * RPC client
 * 
 * @author mindwind
 * @version 1.0, Aug 4, 2014
 */
public interface RpcClient {

	/**
	 * Refer the rpc api proxy class.
	 * 
	 * @param  interfaceClass
	 * @return a proxy.
	 */
	<T> T refer(Class<T> interfaceClass);
	
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
}

package org.craft.atom.rpc.api;

import org.craft.atom.rpc.RpcException;

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
	 * @param  rpcInterface
	 * @return a proxy.
	 */
	<T> T refer(Class<T> rpcInterface);
	
	/**
	 * Open the client, connect to rpc server for communicating.
	 *
	 * @throws RpcException If some other rpc error occurs
	 */
	void open() throws RpcException;
	
	/**
	 * Close the client, disconnect all the connections.
	 */
	void close();
}

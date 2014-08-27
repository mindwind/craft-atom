package org.craft.atom.rpc.api;

import org.craft.atom.rpc.RpcException;

/**
 * RPC client.
 * <p>
 * Use {@link RpcFactory} creates a rpc client and invoke remote method by refer a remote api proxy instance.
 * Open the client before launch any remote invocation.
 * 
 * @author mindwind
 * @version 1.0, Aug 4, 2014
 */
public interface RpcClient {

	/**
	 * Refer the rpc api proxy instance which implements the specific rpc interface.
	 * 
	 * @param  rpcInterface
	 * @return a proxy instance.
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

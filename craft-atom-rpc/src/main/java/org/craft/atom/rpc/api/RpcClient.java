package org.craft.atom.rpc.api;

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
	 * @param clazz interface class
	 * @return a proxy.
	 */
	<T> T refer(Class<T> clazz);
	
}

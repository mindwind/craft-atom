package org.craft.atom.rpc.api;

import java.lang.reflect.Method;

/**
 * RPC server
 * 
 * @author mindwind
 * @version 1.0, Jul 30, 2014
 */
public interface RpcServer {
	
	
	/**
	 * Starts the rpc server and gets things going.
	 */
	void serve();
	
	/**
	 * Expose rpc interface class with the implementor object.
	 * All the declared method in the interface are exposed.
	 * 
	 * @param rpcInterface 
	 * @param rpcObject
	 * @param rpcTimeoutInMillis
	 */
	void expose(Class<?> rpcInterface, Object rpcObject, int rpcTimeoutInMillis);
	
	/**
	 * Expose rpc interface class with the implementor object.
	 * Only the specific method in the interface is exposed.
	 * 
	 * @param rpcInterface
	 * @param rpcMethod
	 * @param rpcObject
	 * @param rpcTimeoutInMillis
	 */
	void expose(Class<?> rpcInterface, Method rpcMethod, Object rpcObject, int rpcTimeoutInMillis);

}

package org.craft.atom.rpc.api;

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
	 * Register rpc interface class with the implementor object.
	 * 
	 * @param rpcInterface 
	 * @param rpcObject
	 */
	void register(Class<?> rpcInterface, Object rpcObject);

}

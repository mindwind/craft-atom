package org.craft.atom.rpc.api;

import org.craft.atom.protocol.rpc.model.RpcMethod;
import org.craft.atom.protocol.rpc.model.RpcOption;

/**
 * RPC server
 * 
 * @author mindwind
 * @version 1.0, Jul 30, 2014
 */
public interface RpcServer {
	
	
	/**
	 * Start the rpc server and get things going.
	 * Just invoke this once.
	 */
	void serve();
	
	/**
	 * Expose rpc interface class with the implementor object.
	 * All the declared method in the interface are exposed.
	 * 
	 * @param rpcInterface 
	 * @param rpcObject
	 * @param rpcOption
	 */
	void expose(Class<?> rpcInterface, Object rpcObject, RpcOption rpcOption);
	
	/**
	 * Expose rpc interface class with the implementor object.
	 * Only the specific method in the interface is exposed.
	 * 
	 * @param rpcInterface
	 * @param rpcMethod
	 * @param rpcObject
	 * @param rpcOption
	 */
	void expose(Class<?> rpcInterface, RpcMethod rpcMethod, Object rpcObject, RpcOption rpcOption);

}

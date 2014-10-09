package org.craft.atom.rpc.api;

import org.craft.atom.protocol.rpc.model.RpcMethod;

/**
 * RPC server.
 * <p>
 * Use {@link RpcFactory} creates a rpc server and expose the remote interfaces or methods.
 * Last invoke {@link RpcServer#open()} to start the server to serve the rpc request from {@link RpcClient}.
 *  
 * @author mindwind
 * @version 1.0, Jul 30, 2014
 */
public interface RpcServer {
	
	/**
	 * Open the rpc server and get things going.
	 * Just invoke this once.
	 */
	void open();
	
	/**
	 * Export rpc interface class. All the declared method in the interface are exposed.
	 * 
	 * @param rpcInterface  exported rpc interface
	 * @param rpcObject     implementor object of rpc interface
	 * @param rpcParameter  behavioral parameter
	 */
	void export(Class<?> rpcInterface, Object rpcObject, RpcParameter rpcParameter);
	
	/**
	 * Export rpc interface class. Only the specific method in the interface is exposed.
	 * 
	 * @param rpcInterface exported rpc interface
	 * @param rpcMethod    exported rpc method
	 * @param rpcObject    implementor object of rpc interface
	 * @param rpcParameter behavioral parameter
	 */
	void export(Class<?> rpcInterface, RpcMethod rpcMethod, Object rpcObject, RpcParameter rpcParameter);
	
	/**
	 * Export rpc interface class with specific id. All the declared method in the interface are exposed.
	 * 
	 * @param rpcId         identifier for rpc interface, if the rpc interface has multiple implementor object, set different rpc id.
	 * @param rpcInterface  exported rpc interface
	 * @param rpcObject     implementor object of rpc interface
	 * @param rpcParameter  behavioral parameter
	 */
	void export(String rcpId, Class<?> rpcInterface, Object rpcObject, RpcParameter rpcParameter);
	
	/**
	 * Export rpc interface class with specific id. Only the specific method in the interface is exposed.
	 * 
	 * @param rpcId        identifier for rpc interface, if the rpc interface has multiple implementor object, set different rpc id.
	 * @param rpcInterface exported rpc interface
	 * @param rpcMethod    exported rpc method
	 * @param rpcObject    implementor object of rpc interface
	 * @param rpcParameter behavioral parameter
	 */
	void export(String rpcId, Class<?> rpcInterface, RpcMethod rpcMethod, Object rpcObject, RpcParameter rpcParameter);

}

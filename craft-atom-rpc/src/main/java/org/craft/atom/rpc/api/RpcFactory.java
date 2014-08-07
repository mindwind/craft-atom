package org.craft.atom.rpc.api;

/**
 * RPC factory.
 * 
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public class RpcFactory {

	
	public static RpcServer newRpcServer(int port) {
		return newRpcServerBuilder(port).build();
	}
	
	public static RpcServerBuilder newRpcServerBuilder(int port) {
		return new RpcServerBuilder().port(port);
	}
	
}

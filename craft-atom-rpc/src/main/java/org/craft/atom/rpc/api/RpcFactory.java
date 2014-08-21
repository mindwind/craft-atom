package org.craft.atom.rpc.api;

/**
 * RPC factory.
 * 
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public class RpcFactory {
	
	
	// ~ --------------------------------------------------------------------------------------------------- rpc server

	
	public static RpcServer newRpcServer(int port) {
		return newRpcServerBuilder(port).build();
	}
	
	public static RpcServerBuilder newRpcServerBuilder(int port) {
		return new RpcServerBuilder().port(port);
	}
	
	
	// ~ --------------------------------------------------------------------------------------------------- rpc client
	
	
	public static RpcClient newRpcClient(String host, int port) {
		return newRpcClientBuilder(host, port).build();
	}
	
	public static RpcClientBuilder newRpcClientBuilder(String host, int port) {
		return new RpcClientBuilder().host(host).port(port);
	}
	
}

package io.craft.atom.rpc.api;

import io.craft.atom.rpc.spi.RpcApi;

import java.util.Set;


/**
 * The x-ray of {@link RpcServer}
 * 
 * @author mindwind
 * @version 1.0, Oct 13, 2014
 */
public interface RpcServerX {
	
	/**
	 * @return current connection count of the rpc server.
	 */
	int connectionCount();
	
	/**
	 * @return rpc api collection.
	 */
	Set<RpcApi> apis();
	
	/**
	 * @return the approximate wait to be handle request count of the rpc api.
	 */
	int waitCount(RpcApi api);
	
	/**
	 * @return the approximate processing request count of the rpc api.
	 */
	int processingCount(RpcApi api);
	
	/**
	 * @return the approximate complete request count of the rpc api.
	 */
	long completeCount(RpcApi api);
}

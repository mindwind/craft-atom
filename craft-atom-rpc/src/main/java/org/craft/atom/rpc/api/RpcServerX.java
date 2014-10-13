package org.craft.atom.rpc.api;

import java.util.Set;

import org.craft.atom.rpc.spi.RpcApi;

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
	 * @return rpc api set.
	 */
	Set<RpcApi> apis();
	
	/**
	 * @return wait request count of the rpc api.
	 */
	int waitCount(RpcApi api);
	
	/**
	 * @return processing request count of the rpc api.
	 */
	int processingCount(RpcApi api);
	
	/**
	 * @return complete request count of the rpc api.
	 */
	int completeCount(RpcApi api);
}

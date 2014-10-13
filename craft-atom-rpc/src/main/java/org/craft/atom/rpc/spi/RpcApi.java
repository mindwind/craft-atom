package org.craft.atom.rpc.spi;

import org.craft.atom.rpc.api.RpcParameter;

/**
 * RPC api object, used by {@link RpcRegistry} to encapsulate exported remote API.
 * 
 * @author mindwind
 * @version 1.0, Oct 11, 2014
 */
public interface RpcApi {

	/**
	 * @return rpc api key.
	 */
	String getKey();
	
	/**
	 * @return rpc api name.
	 */
	String getName();
	
	/**
	 * @return implementor object of rpc interface
	 */
	Object getRpcObject();
	
	/**
	 * @return rpc behavioral parameter
	 */
	RpcParameter getRpcParameter();
	
}

package org.craft.atom.rpc.spi;

import org.craft.atom.rpc.api.RpcParameter;

/**
 * RPC entry, used by {@link RpcRegistry} to encapsulate exported remote API.
 * 
 * @author mindwind
 * @version 1.0, Oct 11, 2014
 */
public interface RpcEntry {

	/**
	 * @return rpc entry key.
	 */
	String getKey();
	
	/**
	 * @return rpc entry name.
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

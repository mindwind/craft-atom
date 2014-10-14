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
	 * @return rpc api key for unique mapping.
	 */
	String getKey();
	
	/**
	 * @return rpc api name for human read.
	 */
	String getName();
	
	/**
	 * @return rpc api id for distinguish different implementor.
	 */
	String getId();
	
	/**
	 * @return rpc api interface.
	 */
	Class<?> getInterface();
	
	/**
	 * @return rpc api method name.
	 */
	String getMethodName();
	
	/**
	 * @return rpc api method parameter types.
	 */
	Class<?>[] getMethodParameterTypes();
	
	/**
	 * @return implementor object of rpc interface
	 */
	Object getRpcObject();
	
	/**
	 * @return rpc behavioral parameter
	 */
	RpcParameter getRpcParameter();
	
}

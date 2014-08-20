package org.craft.atom.rpc.spi;

/**
 * RPC proxy factory provides method for getting or creating dynamic proxy instance.
 * 
 * @author mindwind
 * @version 1.0, Aug 20, 2014
 */
public interface RpcProxyFactory {
	
	
	/**
	 * Get an instance of a proxy class for the specified interface.
	 * 
	 * @param  interfaceClass
	 * @return a proxy instance that implements the specified interface.
	 */
	<T> T getProxy(Class<T> interfaceClass);

}

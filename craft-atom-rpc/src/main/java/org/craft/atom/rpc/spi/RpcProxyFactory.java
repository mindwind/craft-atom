package org.craft.atom.rpc.spi;

/**
 * RPC proxy factory provides method for getting or creating dynamic proxy instance.
 * 
 * @see RpcInvoker
 * @author mindwind
 * @version 1.0, Aug 20, 2014
 */
public interface RpcProxyFactory {
	
	/**
	 * Get an instance of proxy class for the specified interface.
	 * 
	 * @param  rpcInterface
	 * @return a proxy instance that implements the specified interface.
	 */
	<T> T getProxy(Class<T> rpcInterface);
	
	/**
	 * Set rpc invoker. Proxy instance created by factory would use {@link RpcInvoker} to launch a rpc invocation.
	 * 
	 * @param invoker
	 */
	void setInvoker(RpcInvoker invoker);

}

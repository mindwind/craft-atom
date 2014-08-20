package org.craft.atom.rpc;

import java.lang.reflect.Proxy;

import org.craft.atom.rpc.spi.RpcProxyFactory;

/**
 * @author mindwind
 * @version 1.0, Aug 20, 2014
 */
public class DefaultRpcProxyFactory implements RpcProxyFactory {

	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getProxy(Class<T> interfaceClass) {
		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { interfaceClass }, new RpcInvocationHandler());
	}

}

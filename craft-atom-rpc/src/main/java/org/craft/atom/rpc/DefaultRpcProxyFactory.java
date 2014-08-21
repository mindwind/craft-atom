package org.craft.atom.rpc;

import java.lang.reflect.Proxy;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.rpc.spi.RpcInvoker;
import org.craft.atom.rpc.spi.RpcProxyFactory;

/**
 * @author mindwind
 * @version 1.0, Aug 20, 2014
 */
public class DefaultRpcProxyFactory implements RpcProxyFactory {
	
	
	@Getter @Setter private RpcInvoker invoker;

	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getProxy(Class<T> rpcInterface) {
		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { rpcInterface }, new RpcInvocationHandler(invoker));
	}

}

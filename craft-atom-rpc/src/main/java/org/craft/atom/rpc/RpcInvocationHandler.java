package org.craft.atom.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.spi.RpcInvoker;

/**
 * @author mindwind
 * @version 1.0, Aug 20, 2014
 */
public class RpcInvocationHandler implements InvocationHandler {
	
	
	@Getter @Setter private RpcInvoker invoker;
	
	
	public RpcInvocationHandler(RpcInvoker invoker) {
		this.invoker = invoker;
	}
	

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Class<?>   rpcInterface   = method.getDeclaringClass();
		String     methodName     = method.getName();
		Class<?>[] parameterTypes = method.getParameterTypes();
		Object[]   parameters     = args;
		RpcMessage req = RpcMessages.newRequestRpcMessage(rpcInterface, methodName, parameterTypes, parameters);
		RpcMessage rsp = invoker.invoke(req);
		return rsp.getReturnObject();
	}

}

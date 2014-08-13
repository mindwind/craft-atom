package org.craft.atom.rpc;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.protocol.rpc.model.RpcMethod;
import org.craft.atom.rpc.spi.RpcInvoker;

import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public class DefaultRpcInvoker implements RpcInvoker {
	
	
	private RpcRegistry registry = RpcRegistry.getInstance();

	
	@Override
	public RpcMessage invoke(RpcMessage req) throws RpcException {
		Class<?>   rpcInterface = req.getBody().getRpcInterface()      ;
		RpcMethod  rpcMethod    = req.getBody().getRpcMethod()         ;
		Class<?>[] paramTypes   = rpcMethod.getParameterTypes()        ;
		Object[]   params       = rpcMethod.getParameters()            ;
		String     methodName   = rpcMethod.getName()                  ;
		String     key          = registry.key(rpcInterface, rpcMethod);
		RpcEntry   entry        = registry.lookup(key)                 ;
		Object     rpcObject    = entry.getRpcObject()                 ;
		
		
		try {
			MethodAccess ma = MethodAccess.get(rpcInterface);
			int methodIndex = ma.getIndex(methodName, paramTypes);
			Object returnObject = ma.invoke(rpcObject, methodIndex, params);
			return RpcMessages.newRsponseRpcMessage(returnObject);
		} catch (Exception e) {
			throw new RpcException(RpcException.SERVER_ERROR, e);
		}
	}
	
}

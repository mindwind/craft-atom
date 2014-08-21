package org.craft.atom.rpc;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.protocol.rpc.model.RpcMethod;
import org.craft.atom.rpc.spi.RpcConnector;
import org.craft.atom.rpc.spi.RpcInvoker;

import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public class DefaultRpcServerInvoker implements RpcInvoker {
	
	
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
			return RpcMessages.newRsponseRpcMessage(req.getHeader().getId(), returnObject);
		} catch (Exception e) {
			throw new RpcException(RpcException.SERVER_ERROR, e);
		}
	}

	@Override
	public void setConnector(RpcConnector connector) {}
	
}

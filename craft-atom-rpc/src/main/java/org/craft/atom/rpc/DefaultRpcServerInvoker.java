package org.craft.atom.rpc;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.protocol.rpc.model.RpcMethod;
import org.craft.atom.rpc.api.RpcContext;
import org.craft.atom.rpc.spi.RpcConnector;
import org.craft.atom.rpc.spi.RpcInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public class DefaultRpcServerInvoker implements RpcInvoker {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRpcServerInvoker.class);
	
	
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
			// Set rpc context
			RpcContext ctx = RpcContext.getContext();
			ctx.setClientAddress(req.getClientAddress());
			ctx.setServerAddress(req.getServerAddress());
			ctx.setAttachments(req.getAttachments());
			LOG.debug("[CRAFT-ATOM-RPC] Rpc server invoker is invoking, |rpcContext={}|", ctx);
			
			// Reflect invoke
			MethodAccess ma = MethodAccess.get(rpcInterface);
			int methodIndex = ma.getIndex(methodName, paramTypes);
			try {
				Object returnObject = ma.invoke(rpcObject, methodIndex, params);
				return RpcMessages.newRsponseRpcMessage(req.getId(), returnObject);
			} catch (Exception e) {
				return RpcMessages.newRsponseRpcMessage(req.getId(), e);
			}
		} catch (Exception e) {
			throw new RpcException(RpcException.SERVER_ERROR, e);
		} finally {
			RpcContext.removeContext();
		}
	}

	@Override
	public void setConnector(RpcConnector connector) {}
	
}

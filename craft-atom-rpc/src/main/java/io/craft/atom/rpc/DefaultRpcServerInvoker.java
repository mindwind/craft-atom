package io.craft.atom.rpc;

import io.craft.atom.protocol.rpc.model.RpcMessage;
import io.craft.atom.protocol.rpc.model.RpcMethod;
import io.craft.atom.rpc.api.RpcContext;
import io.craft.atom.rpc.spi.RpcApi;
import io.craft.atom.rpc.spi.RpcConnector;
import io.craft.atom.rpc.spi.RpcInvoker;
import io.craft.atom.rpc.spi.RpcRegistry;

import java.lang.reflect.Method;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public class DefaultRpcServerInvoker implements RpcInvoker {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRpcServerInvoker.class);
	
	
	@Getter @Setter private RpcRegistry registry;

	
	@Override
	public RpcMessage invoke(RpcMessage req) throws RpcException {
		String     rpcId        = req.getBody().getRpcId();
		Class<?>   rpcInterface = req.getBody().getRpcInterface();
		RpcMethod  rpcMethod    = req.getBody().getRpcMethod();
		Class<?>[] paramTypes   = rpcMethod.getParameterTypes();
		Object[]   params       = rpcMethod.getParameters();
		String     methodName   = rpcMethod.getName();
		
		RpcApi api = registry.lookup(new DefaultRpcApi(rpcId, rpcInterface, rpcMethod));
		if (api == null) { throw new RpcException(RpcException.SERVER_ERROR, "No exported api mapping"); } 
		Object rpcObject = api.getRpcObject();
		
		
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
			Object returnObject = ma.invoke(rpcObject, methodIndex, params);
			return RpcMessages.newRsponseRpcMessage(req.getId(), returnObject);
		} catch (Exception e) {
			LOG.warn("[CRAFT-ATOM-RPC] Rpc server invoker error", e);
			if (isDeclaredException(e, rpcInterface, methodName, paramTypes)) {
				return RpcMessages.newRsponseRpcMessage(req.getId(), e);
			} else {
				throw new RpcException(RpcException.SERVER_ERROR, "server error");
			}
		} finally {
			RpcContext.removeContext();
		}
	}
	
	private boolean isDeclaredException(Exception e, Class<?> rpcInterface, String methodName, Class<?>[] parameterTypes) {
		try {
			Method method = rpcInterface.getMethod(methodName, parameterTypes);
			Class<?>[] etypes = method.getExceptionTypes();
			LOG.debug("[CRAFT-ATOM-RPC] Rpc server invoker throw exception, |declaredExceptions={}, thrownException={}|", etypes, e);
			for (Class<?> et : etypes) {
				if (et.equals(e.getClass())) return true;
			}
		} catch (Exception ex) {
			return false;
		}
		return false;
	}

	@Override
	public void setConnector(RpcConnector connector) {}
	
}

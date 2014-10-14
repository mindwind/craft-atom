package org.craft.atom.rpc;

import java.util.concurrent.atomic.AtomicLong;

import org.craft.atom.protocol.rpc.model.RpcBody;
import org.craft.atom.protocol.rpc.model.RpcHeader;
import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.protocol.rpc.model.RpcMethod;
import org.craft.atom.protocol.rpc.model.RpcOption;

/**
 * Factory and utility methods for {@link RpcMessage}
 * 
 * @author mindwind
 * @version 1.0, Aug 12, 2014
 */
public class RpcMessages {
	
	
	private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
	
	
	private static RpcMessage newRpcMessage() {
		RpcMessage rm = new RpcMessage();
		RpcHeader  rh = new RpcHeader();
		RpcBody    rb = new RpcBody();
		rm.setHeader(rh);
		rm.setBody(rb);
		return rm;
	}
	
	
	// ~ ---------------------------------------------------------------------------------------------- rpc req message
	
	
	public static RpcMessage newHbRequestRpcMessage() {
		RpcMessage req = newRpcMessage();
		RpcBody body = req.getBody();
		body.setRpcOption(new RpcOption());
		req.setId(ID_GENERATOR.incrementAndGet());
		req.setHeartbeat(true);
		return req;
	}
	
	public static RpcMessage newRequestRpcMessage(Class<?> rpcInterface, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
		RpcMessage req = newRpcMessage();
		req.setId(ID_GENERATOR.incrementAndGet());
		RpcBody body = req.getBody();
		body.setRpcInterface(rpcInterface);
		body.setRpcOption(new RpcOption());
		RpcMethod method = new RpcMethod();
		method.setName(methodName);
		method.setParameterTypes(parameterTypes);
		method.setParameters(parameters);
		body.setRpcMethod(method);
		return req;
	}
	
	
	// ~ ---------------------------------------------------------------------------------------------- rpc rsp message
	
	
	public static RpcMessage newHbResponseRpcMessage(long id) {
		RpcMessage rsp = newRpcMessage();
		rsp.setId(id);
		rsp.setHeartbeat(true);
		rsp.setResponse(true);
		return rsp;
	}
	
	public static RpcMessage newRsponseRpcMessage(long id, Exception e) {
		// Clear server side exception stack trace to avoid propagating to client side
		e.setStackTrace(new StackTraceElement[] {});
					
		RpcMessage rsp = newRpcMessage();
		rsp.setId(id);
		rsp.setResponse(true);
		rsp.setException(e);
		return rsp;
	}
	
	public static RpcMessage newRsponseRpcMessage(long id, Object returnObject) {
		RpcMessage rsp = newRpcMessage();
		rsp.setId(id);
		rsp.setResponse(true);
		rsp.setReturnObject(returnObject);
		return rsp;
	}
	
	public static Object unpackResponseMessage(RpcMessage rsp) throws Exception {
		// void
		if (rsp == null) { return null; }

		// exception
		Exception e = rsp.getException();
		if (e != null) { throw e; }

		// return object
		return rsp.getReturnObject();
	}
	
}

package org.craft.atom.rpc;

import java.util.concurrent.atomic.AtomicLong;

import org.craft.atom.protocol.rpc.model.RpcBody;
import org.craft.atom.protocol.rpc.model.RpcHeader;
import org.craft.atom.protocol.rpc.model.RpcMessage;

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
		req.getHeader().setId(ID_GENERATOR.incrementAndGet());
		req.getHeader().setHb();
		return req;
		
	}
	
	
	// ~ ---------------------------------------------------------------------------------------------- rpc rsp message
	
	
	public static RpcMessage newRsponseRpcMessage(long id, RpcException e) {
		RpcMessage rsp = newRpcMessage();
		rsp.getHeader().setRp();
		rsp.getBody().setThrownObject(e);
		return rsp;
	}
	
	public static RpcMessage newRsponseRpcMessage(long id, Object returnObject) {
		RpcMessage rsp = newRpcMessage();
		rsp.getHeader().setRp();
		rsp.getBody().setReturnObject(returnObject);
		return rsp;
	}
	
}

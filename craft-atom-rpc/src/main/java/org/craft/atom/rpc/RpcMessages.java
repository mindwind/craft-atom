package org.craft.atom.rpc;

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
	
	
	public static RpcMessage newRsponseRpcMessage(long id, RpcException e) {
		RpcMessage rm = newRpcMessage();
		rm.getHeader().setRp();
		rm.getBody().setThrownObject(e);
		return rm;
	}
	
	public static RpcMessage newRsponseRpcMessage(long id, Object returnObject) {
		RpcMessage rm = newRpcMessage();
		rm.getHeader().setRp();
		rm.getBody().setReturnObject(returnObject);
		return rm;
	}
	
	private static RpcMessage newRpcMessage() {
		RpcMessage rm = new RpcMessage();
		RpcHeader  rh = new RpcHeader();
		RpcBody    rb = new RpcBody();
		rm.setHeader(rh);
		rm.setBody(rb);
		return rm;
	}
	
}

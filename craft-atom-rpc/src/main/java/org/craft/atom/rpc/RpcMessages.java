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
	
	
	public static RpcMessage newRsponseRpcMessage(RpcException e) {
		RpcMessage rm = new RpcMessage();
		RpcHeader  rh = new RpcHeader();
		RpcBody    rb = new RpcBody();
		
		rh.setRp();
		rb.setThrownObject(e);
		
		rm.setHeader(rh);
		rm.setBody(rb);
		return rm;
	}
	
}

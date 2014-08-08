package org.craft.atom.rpc;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.spi.RpcInvoker;
import org.craft.atom.rpc.spi.RpcProcessor;

/**
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public class DefaultRpcProcessor implements RpcProcessor {
		
	
	@Getter @Setter private RpcInvoker  invoker ;
	
	
	@Override
	public RpcMessage process(RpcMessage req) {
		RpcMessage rsp;
		try {
			rsp = invoker.invoke(req);
		} catch (RpcException e) {
			rsp = null; // TODO
		}
		
		return rsp;
	}
	

}

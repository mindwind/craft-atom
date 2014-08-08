package org.craft.atom.rpc.spi;

import org.craft.atom.protocol.rpc.model.RpcMessage;

/**
 * RPC processor
 * 
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public interface RpcProcessor {

	
	/**
	 * Process rpc request.
	 * 
	 * @param req  rpc request
	 * @return rpc response can not be null, any request must has response.
	 */
	RpcMessage process(RpcMessage req);
	
	/**
	 * Set rpc invoker.
	 * 
	 * @param invoker
	 */
	void setInvoker(RpcInvoker invoker);
	
}

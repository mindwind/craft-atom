package org.craft.atom.rpc.spi;

import org.craft.atom.protocol.rpc.model.RpcMessage;

/**
 * RPC invoker
 * 
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public interface RpcInvoker {
	
	
	/**
	 * Invoke. TODO javadoc
	 * 
	 * @param rpcmsg
	 * @return response message.
	 */
	RpcMessage invoke(RpcMessage req);
	
	
}

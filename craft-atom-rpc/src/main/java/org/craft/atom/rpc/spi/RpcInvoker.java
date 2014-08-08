package org.craft.atom.rpc.spi;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.RpcException;

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
	 * @throws RpcException
	 */
	RpcMessage invoke(RpcMessage req) throws RpcException;
	
	
}

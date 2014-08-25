package org.craft.atom.rpc.spi;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.RpcException;

/**
 * RPC invoker, launch the rpc invocation.
 * 
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public interface RpcInvoker {
	
	
	/**
	 * Invoke on the client or server side.
	 * <p>
	 * <b>Client Side</b><br>
	 * Encode the rpc request and send to the rpc server, wait the server resonse.
	 * <p>
	 * <b>Server Side</b><br>
	 * Decode the rpc request and invoke the right method of rpc api implementor.
	 *  
	 * @param  req request  message
	 * @return rpc response message
	 * @throws RpcException if any rpc error occurs.
	 */
	RpcMessage invoke(RpcMessage req) throws RpcException;
	
	/**
	 * Set rpc connector. Only client side invoker need implement this method.
	 * 
	 * @param connector
	 */
	void setConnector(RpcConnector connector);
}

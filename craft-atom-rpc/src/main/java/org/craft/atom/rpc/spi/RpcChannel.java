package org.craft.atom.rpc.spi;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.RpcException;

/**
 * RPC channel, provides ability to write <code>RpcMessage</code> to remote peer.
 * 
 * @see RpcAcceptor
 * @author mindwind
 * @version 1.0, Aug 22, 2014
 */
public interface RpcChannel {
	
	
	/**
	 * Write msg to remote peer of the channel.
	 * 
	 * @param msg
	 * @throws RpcException if any other error occurs
	 */
	void write(RpcMessage msg) throws RpcException;
	
}

package org.craft.atom.rpc.spi;

import java.util.List;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.RpcException;

/**
 * RPC channel, provides abilities: <br>
 * - Write <code>RpcMessage</code> to remote peer.<br>
 * - Read <code>RpcMessage</code> from remote peer.
 * 
 * @see RpcAcceptor
 * @author mindwind
 * @version 1.0, Aug 22, 2014
 */
public interface RpcChannel {
	
	/**
	 * Write rpc message and encode it to bytes to remote peer of the channel.
	 * 
	 * @param msg
	 * @throws RpcException if any other error occurs
	 */
	void write(RpcMessage msg) throws RpcException;
	
	/**
	 * Read bytes and decode it to rcp messages from remote peer of the channel.
	 * 
	 * @param bytes
	 * @return rpc message list
	 */
	List<RpcMessage> read(byte[] bytes);
	
}

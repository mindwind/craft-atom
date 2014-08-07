package org.craft.atom.rpc.spi;

import org.craft.atom.protocol.ProtocolDecoder;
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
	 * @param req     rpc request bytes
	 * @param decoder rpc protocol decoder
	 * @return rpc response bytes
	 */
	byte[] process(byte[] bytes, ProtocolDecoder<RpcMessage> decoder);
	
	/**
	 * Set rpc invoker.
	 * 
	 * @param invoker
	 */
	void setInvoker(RpcInvoker invoker);
	
	/**
	 * Set rpc protocol.
	 * 
	 * @param protocol
	 */
	void setProtocol(RpcProtocol protocol);
	
}

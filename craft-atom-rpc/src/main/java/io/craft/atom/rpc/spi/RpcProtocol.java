package io.craft.atom.rpc.spi;

import io.craft.atom.protocol.ProtocolDecoder;
import io.craft.atom.protocol.ProtocolEncoder;
import io.craft.atom.protocol.rpc.model.RpcMessage;


/**
 * RPC protocol object, provide protocol encoder and decoder.
 * 
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public interface RpcProtocol {
	
	/**
	 * @return a new (or reusable) instance of {@link ProtocolEncoder}
	 */
	ProtocolEncoder<RpcMessage> getRpcEncoder();
	
	
	/**
	 * @return a new (or reusable) instance of {@link ProtocolDecoder}
	 */
	ProtocolDecoder<RpcMessage> getRpcDecoder();
	
}

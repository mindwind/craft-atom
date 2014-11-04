package io.craft.atom.protocol.rpc.api;

import io.craft.atom.protocol.ProtocolDecoder;
import io.craft.atom.protocol.ProtocolEncoder;
import io.craft.atom.protocol.rpc.RpcDecoder;
import io.craft.atom.protocol.rpc.RpcEncoder;
import io.craft.atom.protocol.rpc.model.RpcMessage;


/**
 * RPC codec factory, which provides static factory method to create {@link ProtocolEncoder<RpcMessage>} and {@link ProtocolDecoder<RpcMessage>} instance.
 * 
 * @author mindwind
 * @version 1.0, Aug 5, 2014
 */
public class RpcCodecFactory {
	
	
	public static ProtocolEncoder<RpcMessage> newRpcEncoder() {
		return new RpcEncoder();
	}
	
	public static ProtocolDecoder<RpcMessage> newRpcDecoder() {
		return new RpcDecoder();
	}
	
}

package org.craft.atom.protocol.rpc.api;

import org.craft.atom.protocol.ProtocolDecoder;
import org.craft.atom.protocol.ProtocolEncoder;
import org.craft.atom.protocol.rpc.RpcDecoder;
import org.craft.atom.protocol.rpc.RpcEncoder;
import org.craft.atom.protocol.rpc.model.RpcMessage;

/**
 * RPC codec factory
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

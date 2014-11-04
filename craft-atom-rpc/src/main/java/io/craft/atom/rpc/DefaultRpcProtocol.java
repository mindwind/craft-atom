package io.craft.atom.rpc;

import io.craft.atom.protocol.ProtocolDecoder;
import io.craft.atom.protocol.ProtocolEncoder;
import io.craft.atom.protocol.rpc.api.RpcCodecFactory;
import io.craft.atom.protocol.rpc.model.RpcMessage;
import io.craft.atom.rpc.spi.RpcProtocol;


/**
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public class DefaultRpcProtocol implements RpcProtocol {
	
	
	private ProtocolEncoder<RpcMessage> encoder;
	
	
	public DefaultRpcProtocol() {
		this.encoder = RpcCodecFactory.newRpcEncoder();
	}
	

	@Override
	public ProtocolEncoder<RpcMessage> getRpcEncoder() {
		return encoder;
	}

	@Override
	public ProtocolDecoder<RpcMessage> getRpcDecoder() {
		return RpcCodecFactory.newRpcDecoder();
	}

}

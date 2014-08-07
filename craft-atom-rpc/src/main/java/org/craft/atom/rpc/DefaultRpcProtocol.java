package org.craft.atom.rpc;

import org.craft.atom.protocol.ProtocolDecoder;
import org.craft.atom.protocol.ProtocolEncoder;
import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.spi.RpcProtocol;

/**
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public class DefaultRpcProtocol implements RpcProtocol {

	@Override
	public ProtocolEncoder<RpcMessage> getRpcEncoder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProtocolDecoder<RpcMessage> getRpcDecoder() {
		// TODO Auto-generated method stub
		return null;
	}

}

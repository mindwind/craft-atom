package org.craft.atom.rpc;

import lombok.ToString;

import org.craft.atom.io.Channel;
import org.craft.atom.io.IllegalChannelStateException;
import org.craft.atom.protocol.ProtocolEncoder;
import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.spi.RpcChannel;

/**
 * @author mindwind
 * @version 1.0, Aug 22, 2014
 */
@ToString(of = "channel")
public class DefaultRpcChannel implements RpcChannel {
	
	
	private ProtocolEncoder<RpcMessage> encoder;
	private Channel<byte[]>             channel;
	
	
	DefaultRpcChannel(Channel<byte[]> channel, ProtocolEncoder<RpcMessage> encoder) {
		this.channel = channel;
		this.encoder = encoder;
	}
	

	@Override
	public void write(RpcMessage msg) throws RpcException {
		try {
			byte[] bytes = encoder.encode(msg);
			channel.write(bytes);
		} catch (IllegalChannelStateException e) {
			throw new RpcException(RpcException.NETWORK, "broken connection");
		}
	}

}

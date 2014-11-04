package io.craft.atom.rpc;

import io.craft.atom.io.Channel;
import io.craft.atom.io.IllegalChannelStateException;
import io.craft.atom.protocol.ProtocolDecoder;
import io.craft.atom.protocol.ProtocolEncoder;
import io.craft.atom.protocol.rpc.model.RpcMessage;
import io.craft.atom.rpc.spi.RpcChannel;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Aug 22, 2014
 */
@ToString(of = "channel")
public class DefaultRpcChannel implements RpcChannel {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRpcChannel.class);
	
	
	@Getter @Setter private ProtocolEncoder<RpcMessage> encoder;
	@Getter @Setter private ProtocolDecoder<RpcMessage> decoder;
	@Getter @Setter private Channel<byte[]>             channel;
	@Getter @Setter private Map<Long, RpcFuture<?>>     futures;
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	DefaultRpcChannel(Channel<byte[]> channel, ProtocolEncoder<RpcMessage> encoder, ProtocolDecoder<RpcMessage> decoder) {
		this.channel = channel;
		this.encoder = encoder;
		this.decoder = decoder;
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public void write(RpcMessage msg) throws RpcException {
		try {
			byte[] bytes = encoder.encode(msg);
			LOG.debug("[CRAFT-ATOM-RPC] Rpc channel write bytes, |length={}, bytes={}, channel={}|", bytes.length, bytes, channel);
			channel.write(bytes);
		} catch (IllegalChannelStateException e) {
			throw new RpcException(RpcException.NETWORK, "broken connection");
		}
	}
	
	@Override
	public List<RpcMessage> read(byte[] bytes) {
		LOG.debug("[CRAFT-ATOM-RPC] Rpc channel read bytes, |length={}, bytes={}, channel={}|", bytes.length, bytes, channel);
		List<RpcMessage> msgs = decoder.decode(bytes);
		return msgs;
	}

	void close() {
		channel.close();
	}

	boolean isOpen() {
		return channel.isOpen();
	}
	
	long getId() {
		return channel.getId();
	}
	
	void setRpcFuture(long mid, RpcFuture<?> future) {
		futures.put(mid, future);
	}
	
	void notifyRpcMessage(RpcMessage msg) {
		RpcFuture<?> future = futures.remove(msg.getId());
		if (future == null) return;
		future.setResponse(msg);
	}
	
	void notifyRpcException(Exception e) {
		for (RpcFuture<?> future : futures.values()) {
			future.setException(e);
		}
	}
	
	int waitCount() {
		return channel.getWriteQueue().size();
	}

}

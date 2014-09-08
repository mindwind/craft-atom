package org.craft.atom.rpc;

import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.Map;

import org.craft.atom.io.Channel;
import org.craft.atom.io.IoHandler;
import org.craft.atom.protocol.ProtocolDecoder;
import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.spi.RpcProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Aug 15, 2014
 */
public class RpcClientIoHandler implements IoHandler {
	
	
	private static final Logger LOG                = LoggerFactory.getLogger(RpcClientIoHandler.class);
	private static final String RPC_DECODER        = "rpc.decoder"                                    ;
	public  static final String RPC_FUTURE_CHANNEL = "rpc.future.channel"                             ;
	
	
	private RpcProtocol         protocol ;
	private DefaultRpcConnector connector;
	

	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public RpcClientIoHandler(RpcProtocol protocol, DefaultRpcConnector connector) {
		this.protocol  = protocol;
		this.connector = connector;
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	

	@Override
	public void channelOpened(Channel<byte[]> channel) {
		ProtocolDecoder<RpcMessage> decoder = protocol.getRpcDecoder();
		channel.setAttribute(RPC_DECODER, decoder);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void channelRead(Channel<byte[]> channel, byte[] bytes) {
		ProtocolDecoder<RpcMessage> decoder = (ProtocolDecoder<RpcMessage>) channel.getAttribute(RPC_DECODER);
		List<RpcMessage> rsps = decoder.decode(bytes);
		for (RpcMessage rsp : rsps) {
			Map<Long, RpcFuture> map = (Map<Long, RpcFuture>) channel.getAttribute(RPC_FUTURE_CHANNEL);
			RpcFuture future = map.remove(rsp.getId());
			if (future != null) {
				future.setResponse(rsp);
			}
		}
	}

	@Override
	public void channelClosed(Channel<byte[]> channel) {
		LOG.debug("[CRAFT-ATOM-RPC] Channel closed, |channel={}|", channel);
		channelThrown0(channel, new ClosedChannelException());
		connector.reconnect(channel.getId());
	}
	
	@Override
	public void channelThrown(Channel<byte[]> channel, Exception cause) {
		LOG.warn("[CRAFT-ATOM-RPC] Channel thrown, |channel={}|", channel, cause);
		channelThrown0(channel, cause);
		channel.close();
	}
	
	@SuppressWarnings("unchecked")
	private void channelThrown0(Channel<byte[]> channel, Exception cause) {
		Map<Long, RpcFuture> map = (Map<Long, RpcFuture>) channel.getAttribute(RPC_FUTURE_CHANNEL);
		for (RpcFuture future : map.values()) {
			future.setException(cause);
		}
	}

	@Override
	public void channelFlush(Channel<byte[]> channel, byte[] bytes) {}

	@Override
	public void channelWritten(Channel<byte[]> channel, byte[] bytes) {}
	
	@Override
	public void channelIdle(Channel<byte[]> channel) {}

	
}

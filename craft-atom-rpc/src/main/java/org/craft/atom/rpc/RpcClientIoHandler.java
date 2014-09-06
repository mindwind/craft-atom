package org.craft.atom.rpc;

import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	
	
	private RpcProtocol  protocol;
	

	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public RpcClientIoHandler(RpcProtocol protocol) {
		this.protocol = protocol;
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	

	@Override
	public void channelOpened(Channel<byte[]> channel) {
		LOG.debug("[CRAFT-ATOM-RPC] Channel opened, |Channel={}|", channel);
		ProtocolDecoder<RpcMessage> decoder = protocol.getRpcDecoder();
		channel.setAttribute(RPC_DECODER, decoder);
		channel.setAttribute(RPC_FUTURE_CHANNEL, new ConcurrentHashMap<Long, RpcFuture>());
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
		LOG.debug("[CRAFT-ATOM-RPC] Channel closed, |Channel={}|", channel);
		channelThrown0(channel, new ClosedChannelException());
	}
	
	@Override
	public void channelThrown(Channel<byte[]> channel, Exception cause) {
		LOG.warn("[CRAFT-ATOM-RPC] Channel thrown, |Channel={}|", channel, cause);
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
	public void channelFlush(Channel<byte[]> channel, byte[] bytes) {
		LOG.debug("[CRAFT-ATOM-RPC] Channel flush, |Channel={}|", channel);
	}

	@Override
	public void channelWritten(Channel<byte[]> channel, byte[] bytes) {
		LOG.debug("[CRAFT-ATOM-RPC] Channel written, |Channel={}|", channel);
	}
	
	@Override
	public void channelIdle(Channel<byte[]> channel) {
		LOG.debug("[CRAFT-ATOM-RPC] Channel idle, |Channel={}|", channel);
	}

	
}

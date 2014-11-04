package io.craft.atom.rpc;

import io.craft.atom.io.Channel;
import io.craft.atom.protocol.rpc.model.RpcMessage;

import java.nio.channels.ClosedChannelException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Aug 15, 2014
 */
public class RpcClientIoHandler extends RpcIoHandler {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(RpcClientIoHandler.class);
	private DefaultRpcConnector connector;
	

	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public RpcClientIoHandler(DefaultRpcConnector connector) {
		this.connector = connector;
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public void channelRead(Channel<byte[]> channel, byte[] bytes) {
		DefaultRpcChannel rpcChannel = (DefaultRpcChannel) channel.getAttribute(RpcIoHandler.RPC_CHANNEL);
		List<RpcMessage> rsps = rpcChannel.read(bytes);
		for (RpcMessage rsp : rsps) {
			rpcChannel.notifyRpcMessage(rsp);
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
	
	private void channelThrown0(Channel<byte[]> channel, Exception cause) {
		DefaultRpcChannel rpcChannel = (DefaultRpcChannel) channel.getAttribute(RpcIoHandler.RPC_CHANNEL);
		rpcChannel.notifyRpcException(cause);
	}

}

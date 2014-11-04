package io.craft.atom.rpc;

import io.craft.atom.io.Channel;
import io.craft.atom.protocol.rpc.model.RpcMessage;
import io.craft.atom.rpc.spi.RpcProcessor;
import io.craft.atom.rpc.spi.RpcProtocol;

import java.net.InetSocketAddress;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public class RpcServerIoHandler extends RpcIoHandler {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(RpcServerIoHandler.class);
	
	private RpcProtocol  protocol ;
	private RpcProcessor processor;
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public RpcServerIoHandler(RpcProtocol protocol, RpcProcessor processor) {
		this.protocol  = protocol;
		this.processor = processor;
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------

	
	@Override
	public void channelOpened(Channel<byte[]> channel) {
		DefaultRpcChannel rpcChannel = new DefaultRpcChannel(channel, protocol.getRpcEncoder(), protocol.getRpcDecoder());
		channel.setAttribute(RpcIoHandler.RPC_CHANNEL, rpcChannel);
	}

	@Override
	public void channelRead(Channel<byte[]> channel, byte[] bytes) {
		DefaultRpcChannel rpcChannel = (DefaultRpcChannel) channel.getAttribute(RpcIoHandler.RPC_CHANNEL);
		List<RpcMessage> reqs = rpcChannel.read(bytes);
		for (RpcMessage req : reqs) {
			req.setServerAddress((InetSocketAddress) channel.getLocalAddress());
			req.setClientAddress((InetSocketAddress) channel.getRemoteAddress());
			processor.process(req, rpcChannel);
		}
	}
	
	@Override
	public void channelIdle(Channel<byte[]> channel) {
		channel.close();
	}
	
	@Override
	public void channelThrown(Channel<byte[]> channel, Exception cause) {
		LOG.warn("[CRAFT-ATOM-RPC] Channel thrown, |channel={}|", channel, cause);
		channel.close();
	}
	
	@Override
	public void channelClosed(Channel<byte[]> channel) {
		LOG.debug("[CRAFT-ATOM-RPC] Channel closed, |channel={}|", channel);
	}
	
}

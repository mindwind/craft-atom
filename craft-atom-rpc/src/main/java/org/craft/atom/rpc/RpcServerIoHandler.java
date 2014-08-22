package org.craft.atom.rpc;

import java.util.List;

import org.craft.atom.io.Channel;
import org.craft.atom.io.IoHandler;
import org.craft.atom.protocol.ProtocolDecoder;
import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.spi.RpcProcessor;
import org.craft.atom.rpc.spi.RpcProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public class RpcServerIoHandler implements IoHandler {
	
	
	private static final Logger LOG         = LoggerFactory.getLogger(RpcServerIoHandler.class);
	private static final String RPC_DECODER = "rpc.decoder"                                    ;
	
	
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
		ProtocolDecoder<RpcMessage> decoder = protocol.getRpcDecoder();
		channel.setAttribute(RPC_DECODER, decoder);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void channelRead(Channel<byte[]> channel, byte[] bytes) {
		ProtocolDecoder<RpcMessage> decoder = (ProtocolDecoder<RpcMessage>) channel.getAttribute(RPC_DECODER);
		List<RpcMessage> reqs = decoder.decode(bytes);
		for (RpcMessage req : reqs) {
			processor.process(req, new DefaultRpcChannel(channel, protocol.getRpcEncoder()));
		}
	}
	
	@Override
	public void channelIdle(Channel<byte[]> channel) {
		LOG.info("[CRAFT-ATOM-RPC] Rpc server handler idle, |Channel={}|", channel);
		channel.close();
	}
	
	@Override
	public void channelThrown(Channel<byte[]> channel, Exception cause) {
		LOG.info("[CRAFT-ATOM-RPC] Rpc server handler thrown, |Channel={}, thrown={}|", channel, cause);
		channel.close();
	}
	
	@Override
	public void channelClosed(Channel<byte[]> channel) {}

	@Override
	public void channelFlush(Channel<byte[]> channel, byte[] bytes) {}

	@Override
	public void channelWritten(Channel<byte[]> channel, byte[] bytes) {}
	
}

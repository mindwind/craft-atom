package io.craft.atom.rpc;

import io.craft.atom.io.Channel;
import io.craft.atom.io.IoHandler;

/**
 * @author mindwind
 * @version 1.0, Sep 22, 2014
 */
public abstract class RpcIoHandler implements IoHandler {

	
	static final String RPC_CHANNEL = "rpc.channel";

	
	@Override
	public void channelOpened(Channel<byte[]> channel) {}
	@Override
	public void channelClosed(Channel<byte[]> channel) {}
	@Override
	public void channelIdle(Channel<byte[]> channel) {}
	@Override
	public void channelRead(Channel<byte[]> channel, byte[] bytes) {}
	@Override
	public void channelFlush(Channel<byte[]> channel, byte[] bytes) {}
	@Override
	public void channelWritten(Channel<byte[]> channel, byte[] bytes) {}
	@Override
	public void channelThrown(Channel<byte[]> channel, Exception cause) {}
	
}

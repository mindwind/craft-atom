package io.craft.atom.nio;

import io.craft.atom.io.AbstractIoHandler;
import io.craft.atom.io.Channel;

/**
 * @author mindwind
 * @version 1.0, Mar 15, 2014
 */
public class NioEchoServerHandler extends AbstractIoHandler {
	
	@Override
	public void channelRead(Channel<byte[]> channel, byte[] bytes) {
		channel.write(bytes);
	}
	
}

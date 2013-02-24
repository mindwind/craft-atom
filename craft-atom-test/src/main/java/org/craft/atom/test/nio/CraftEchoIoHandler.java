package org.craft.atom.test.nio;

import org.craft.atom.io.AbstractIoHandler;
import org.craft.atom.io.Channel;

/**
 * @author mindwind
 * @version 1.0, Feb 24, 2013
 */
public class CraftEchoIoHandler extends AbstractIoHandler {

	@Override
	public void channelRead(Channel<byte[]> channel, byte[] bytes) {
		channel.write(bytes);
	}
	
}

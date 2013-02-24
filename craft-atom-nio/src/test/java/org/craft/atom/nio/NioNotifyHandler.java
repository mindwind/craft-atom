package org.craft.atom.nio;

import org.craft.atom.io.AbstractIoHandler;
import org.craft.atom.io.Channel;

/**
 * @author mindwind
 * @version 1.0, 2011-12-19
 */
public class NioNotifyHandler extends AbstractIoHandler {
	
	private static final byte EXCLAMATION_MARK = 33;   // char - !
	
	@Override
	public void channelRead(Channel<byte[]> channel, byte[] bytes) {
		System.out.println("Channel read bytes size=" + bytes.length);
		
		if (bytes[bytes.length - 1] == EXCLAMATION_MARK) {
			synchronized(channel) {
				channel.notifyAll();
			}
		}
	}

	
}

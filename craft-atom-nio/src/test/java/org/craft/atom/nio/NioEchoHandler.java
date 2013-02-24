package org.craft.atom.nio;

import org.craft.atom.io.AbstractIoHandler;
import org.craft.atom.io.Channel;

/**
 * @author mindwind
 * @version 1.0, 2011-12-19
 */
public class NioEchoHandler extends AbstractIoHandler {
	
	private static final byte EXCLAMATION_MARK = 33;   // char - !
	private StringBuilder buf = new StringBuilder();

	@Override
	public void channelRead(Channel<byte[]> channel, byte[] bytes) {
		System.out.println("Channel read bytes size=" + bytes.length);
		
		for (byte b : bytes) {
			buf.append((char) b);
		}
		
		if (bytes[bytes.length - 1] == EXCLAMATION_MARK) {
			byte[] echoBytes = buf.toString().getBytes();
			System.out.println("Echo bytes size=" + echoBytes.length);
			channel.write(bytes);
			buf = new StringBuilder();
		}
	}
	
}

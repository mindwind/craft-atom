package org.craft.atom.nio;

import org.craft.atom.io.AbstractIoHandler;
import org.craft.atom.io.Channel;

/**
 * @author mindwind
 * @version 1.0, 2011-12-19
 */
public class NioAcceptorNapHandler extends AbstractIoHandler {
	
	private static final byte LF = 10; 
	private StringBuilder buf = new StringBuilder();

	@Override
	public void channelRead(Channel<byte[]> channel, byte[] bytes) {
		System.out.println("[Nio Acceptor Handler] channel read bytes size=" + bytes.length + ", channle-paused=" + channel.isPaused());
		
		for (byte b : bytes) {
			buf.append((char) b);
		}
		
		nap();
		
		if (bytes[bytes.length - 1] == LF) {
			byte[] echoBytes = buf.toString().getBytes();
			System.out.println("Echo bytes size=" + echoBytes.length + ", take a nap \n");
			boolean b = channel.write(echoBytes);
			System.out.println("Channel.write()=" + b);
			buf = new StringBuilder();
		}
	}
	
	private void nap() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}

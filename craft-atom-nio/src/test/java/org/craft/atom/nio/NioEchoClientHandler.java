package org.craft.atom.nio;

import java.util.concurrent.atomic.AtomicInteger;

import org.craft.atom.io.AbstractIoHandler;
import org.craft.atom.io.Channel;

/**
 * @author mindwind
 * @version 1.0, Mar 15, 2014
 */
public class NioEchoClientHandler extends AbstractIoHandler {
	
	private AtomicInteger counter = new AtomicInteger();
	
	@Override
	public void channelOpened(Channel<byte[]> channel) {
		send(channel);
	}
	
	@Override
	public void channelRead(Channel<byte[]> channel, byte[] bytes) {
		System.out.println("ECHO-RECV: " + new String(bytes));
		try { Thread.sleep(1000); } catch (InterruptedException e) {}
		send(channel);
	}
	
	private void send(Channel<byte[]> channel) {
		String toSend = Integer.toString(counter.incrementAndGet());
		channel.write(toSend.getBytes());
		System.out.println("ECHO-SENT: " + toSend);
	}
	
}

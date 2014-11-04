package io.craft.atom.nio;

import io.craft.atom.io.AbstractIoHandler;
import io.craft.atom.io.Channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, 2011-12-19
 */
public class NioAcceptorHandler extends AbstractIoHandler {
	
	private static final Logger  LOG = LoggerFactory.getLogger(NioAcceptorHandler.class);
	private static final byte LF  = 10                                         ;
	
	
	private StringBuilder buf = new StringBuilder();

	
	@Override
	public void channelRead(Channel<byte[]> channel, byte[] bytes) {
		LOG.debug("[CRAFT-ATOM-NIO] Channel read bytes size={}", bytes.length);
		
		for (byte b : bytes) {
			buf.append((char) b);
		}
		
		if (bytes[bytes.length - 1] == LF) {
			byte[] echoBytes = buf.toString().getBytes();
			LOG.debug("[CRAFT-ATOM-NIO] Echo bytes size={} \n", echoBytes.length);
			channel.write(echoBytes);
			buf = new StringBuilder();
		}
	}
	
}

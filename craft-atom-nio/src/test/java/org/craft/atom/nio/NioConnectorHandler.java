package org.craft.atom.nio;

import lombok.Getter;

import org.craft.atom.io.AbstractIoHandler;
import org.craft.atom.io.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, 2011-12-19
 */
public class NioConnectorHandler extends AbstractIoHandler {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(NioConnectorHandler.class);
	private static final byte   LF  = 10                                                ;
	
	
	@Getter private StringBuilder buf = new StringBuilder();
	@Getter private String        rcv = null               ;
	
	
	@Override
	public void channelRead(Channel<byte[]> channel, byte[] bytes) {
		LOG.debug("[CRAFT-ATOM-NIO] Channel read bytes size={}", bytes.length);
		
		for (byte b : bytes) {
			buf.append((char) b);
		}
		
		if (bytes[bytes.length - 1] == LF) {
			rcv = buf.toString();
			byte[] echoBytes = buf.toString().getBytes();
			LOG.debug("[CRAFT-ATOM-NIO] Echo received bytes size={} \n", echoBytes.length);
			buf = new StringBuilder();
			synchronized(channel) {
				channel.notifyAll();
			}
		}
	}

}

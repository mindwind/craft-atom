package org.craft.atom.nio;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.io.AbstractIoHandler;
import org.craft.atom.io.Channel;

/**
 * @author mindwind
 * @version 1.0, 2011-12-19
 */
public class NioAcceptorHandler extends AbstractIoHandler {
	
	private static final Log  LOG = LogFactory.getLog(NioAcceptorHandler.class);
	private static final byte LF  = 10                                         ;
	
	
	private StringBuilder buf = new StringBuilder();

	
	@Override
	public void channelRead(Channel<byte[]> channel, byte[] bytes) {
		LOG.debug("[CRAFT-ATOM-NIO] Channel read bytes size=" + bytes.length);
		
		for (byte b : bytes) {
			buf.append((char) b);
		}
		
		if (bytes[bytes.length - 1] == LF) {
			byte[] echoBytes = buf.toString().getBytes();
			LOG.debug("[CRAFT-ATOM-NIO] Echo bytes size=" + echoBytes.length + "\n");
			channel.write(echoBytes);
			buf = new StringBuilder();
		}
	}
	
}

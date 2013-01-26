package org.craft.atom.nio;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.nio.api.Session;

/**
 * Test handler
 * 
 * @author Hu Feng
 * @version 1.0, 2011-12-19
 */
public class TestHandler extends AbstractHandler {
	
	private static final Log LOG = LogFactory.getLog(TestHandler.class);
	
	private StringBuilder buf = new StringBuilder();

	@Override
	public void messageReceived(Session session, byte[] message) {
//		LOG.debug("message received: size=" + message.length + "  " + new String(message));
		LOG.debug("message received: size=" + message.length);
		
		for (byte b : message) {
			buf.append((char) b);
		}
		
		if (message[message.length - 1] == 33) {
			byte[] bytes = buf.toString().getBytes();
			LOG.debug("echo message size=" + bytes.length);
			session.write(bytes);
			buf = new StringBuilder();
		}
	}
	
}

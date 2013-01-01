package org.craft.atom.nio;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.nio.api.Session;

/**
 * Do nothing handler
 *
 * @author Hu Feng
 * @version 1.0, 2011-12-19
 */
public class NothingHandler extends AbstractHandler {
	
	private static final Log LOG = LogFactory.getLog(NothingHandler.class);
	
	@Override
	public void messageReceived(Session session, byte[] message) {
		LOG.debug("message received: size=" + message.length);
		
		if (message[message.length - 1] == 33) {
			synchronized(session) {
				session.notifyAll();
			}
		}
	}
	
}

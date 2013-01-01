package org.craft.atom.nio;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.nio.api.Session;
import org.craft.atom.nio.spi.Handler;

/**
 * Abstract Handler
 *
 * @author Hu Feng
 * @version 1.0, 2011-12-19
 */
public abstract class AbstractHandler implements Handler {
	
	private static final Log LOG = LogFactory.getLog(AbstractHandler.class);

	@Override
	public void sessionOpened(Session session) {
		LOG.debug("session opened.");
	}

	@Override
	public void sessionClosed(Session session) {
		LOG.debug("session closed.");
	}

	@Override
	public void sessionIdle(Session session) {
		LOG.debug("session idle.");
	}

	@Override
	public void messageReceived(Session session, byte[] message) {
		LOG.debug("message received: size=" + message.length + "  " + new String(message));
	}

	@Override
	public void messageSent(Session session, byte[] message) {
		LOG.debug("message sent: size=" + message.length + "  " + new String(message));
	}

	@Override
	public void exceptionCaught(Session session, Throwable cause) {
		LOG.debug("exception caught :" + cause, cause);
	}

}

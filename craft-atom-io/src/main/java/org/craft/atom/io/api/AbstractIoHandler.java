package org.craft.atom.io.api;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An super abstract class for {@link IoHandler}.
 * Can be extended and selectively override required event handler methods only.
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 */
abstract public class AbstractIoHandler implements IoHandler {
	
	private static final Log LOG = LogFactory.getLog(AbstractIoHandler.class);

	@Override
	public void channelOpened(Channel<byte[]> channel) {
		if (LOG.isDebugEnabled()) { LOG.debug("Opened channel=" + channel); }
	}

	@Override
	public void channelClosed(Channel<byte[]> channel) {
		if (LOG.isDebugEnabled()) { LOG.debug("Closed channel=" + channel); }
	}

	@Override
	public void channelIdle(Channel<byte[]> channel) {
		if (LOG.isDebugEnabled()) { LOG.debug("Idle channel=" + channel); }
	}

	@Override
	public void channelReceived(Channel<byte[]> channel, byte[] bytes) {
		if (LOG.isDebugEnabled()) { LOG.debug("Received channel=" + channel + ", bytes=" + Arrays.toString(bytes)); }
	}

	@Override
	public void channelSent(Channel<byte[]> channel, byte[] bytes) {
		if (LOG.isDebugEnabled()) { LOG.debug("Sent channel=" + channel + ", bytes=" + Arrays.toString(bytes)); }	
	}

	@Override
	public void channelThrown(Channel<byte[]> channel, Throwable cause) {
		LOG.warn("Exception channel=" + channel, cause);
	}
	
}

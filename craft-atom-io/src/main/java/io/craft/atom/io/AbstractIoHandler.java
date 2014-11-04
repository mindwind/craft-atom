package io.craft.atom.io;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An super abstract class for {@link IoHandler}.
 * Can be extended and selectively override required event handler methods only.
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 */
abstract public class AbstractIoHandler implements IoHandler {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractIoHandler.class);

	
	@Override
	public void channelOpened(Channel<byte[]> channel) {
		LOG.debug("[CRAFT-ATOM-IO] Opened |channel={}|", channel);
	}

	@Override
	public void channelClosed(Channel<byte[]> channel) {
		LOG.debug("[CRAFT-ATOM-IO] Closed |channel={}|", channel); 
	}

	@Override
	public void channelIdle(Channel<byte[]> channel) {
		LOG.debug("[CRAFT-ATOM-IO] Idle |channel={}|", channel);
	}

	@Override
	public void channelRead(Channel<byte[]> channel, byte[] bytes) {
		LOG.debug("[CRAFT-ATOM-IO] Read |channel={}, bytes={}|", channel, Arrays.toString(bytes));
	}

	@Override
	public void channelFlush(Channel<byte[]> channel, byte[] bytes) {
		LOG.debug("[CRAFT-ATOM-IO] Flush |channel={}, bytes={}|", channel, Arrays.toString(bytes));
	}

	@Override
	public void channelWritten(Channel<byte[]> channel, byte[] bytes) {
		LOG.debug("[CRAFT-ATOM-IO] Written |channel={}, bytes={}|", channel, Arrays.toString(bytes));
	}

	@Override
	public void channelThrown(Channel<byte[]> channel, Exception cause) {
		LOG.warn("[CRAFT-ATOM-IO] Thrown |channel={}|", channel, cause);
	}
	
}

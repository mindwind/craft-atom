package org.craft.atom.protocol.ssl;

import org.craft.atom.io.Channel;

/**
 * @author mindwind
 * @version 1.0, Mar 26, 2014
 */
public class ChannelSslHandshakeHandler implements SslHandshakeHandler {

	private Channel<byte[]> channel;
	
	public ChannelSslHandshakeHandler(Channel<byte[]> channel) {
		this.channel = channel;
	}

	@Override
	public void needWrite(byte[] bytes) {
		channel.write(bytes);
	}

}

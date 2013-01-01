package org.craft.atom.nio;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

import org.craft.atom.nio.api.AbstractConfig;

/**
 * @author Hu Feng
 * @version 1.0, 2012-2-26
 */
public class TcpSession extends AbstractSession {
	
	private SocketChannel socketChannel;

	public TcpSession(SocketChannel socketChannel, AbstractConfig config) {
		super(config);
		this.socketChannel = socketChannel;
		this.localAddress = socketChannel.socket().getLocalSocketAddress();
		this.remoteAddress = socketChannel.socket().getRemoteSocketAddress();
	}

	@Override
	public SelectableChannel getChannel() {
		return socketChannel;
	}

}

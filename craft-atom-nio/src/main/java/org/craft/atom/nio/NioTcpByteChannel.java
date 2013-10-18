package org.craft.atom.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.text.DecimalFormat;

import org.craft.atom.nio.spi.NioBufferSizePredictor;
import org.craft.atom.nio.spi.NioChannelEventDispatcher;

/**
 * A nio channel for stream-oriented connecting sockets.
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
public class NioTcpByteChannel extends NioByteChannel {
	
	private SocketChannel socketChannel;
	
	public NioTcpByteChannel(SocketChannel socketChannel, NioConfig config, NioBufferSizePredictor predictor, NioChannelEventDispatcher dispatcher) {
		super(config, predictor, dispatcher);
		
		if (socketChannel == null) {
			throw new IllegalArgumentException("SocketChannel can not be null.");
		}
		
		this.socketChannel = socketChannel;
		this.localAddress = socketChannel.socket().getLocalSocketAddress();
		this.remoteAddress = socketChannel.socket().getRemoteSocketAddress();
	}

	@Override
	protected SelectableChannel innerChannel() {
		return socketChannel;
	}

	@Override
	protected int readTcp(ByteBuffer buf) throws IOException {
		return socketChannel.read(buf);
	}
	
	@Override
	protected int writeTcp(ByteBuffer buf) throws IOException {
		return socketChannel.write(buf);
	}
	
	@Override
	protected void close0() throws IOException {
		SelectionKey key = getSelectionKey();
		
		if (key != null) {
			key.cancel();
		}
		
		socketChannel.close();
	}
	
	private String getIdAsString() {
		String format = "0000000000";
		DecimalFormat df = new DecimalFormat(format);
		return df.format(id);
	}

	@Override
	public String toString() {
		return String.format("{id=%s: <%s - %s>}", getIdAsString(), localAddress, remoteAddress, state);
	}

}

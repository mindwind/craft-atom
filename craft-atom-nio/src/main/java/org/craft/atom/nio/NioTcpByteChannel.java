package org.craft.atom.nio;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.craft.atom.nio.spi.NioBufferSizePredictor;

/**
 * A nio channel for stream-oriented connecting sockets.
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
public class NioTcpByteChannel extends NioByteChannel {
	
	private SocketChannel socketChannel;
	
	public NioTcpByteChannel(SocketChannel socketChannel, NioConfig config, NioBufferSizePredictor predictor) {
		super(config, predictor);
		
		if (socketChannel == null) {
			throw new IllegalArgumentException("SocketChannel can not be null.");
		}
		
		this.socketChannel = socketChannel;
		this.localAddress = socketChannel.socket().getLocalSocketAddress();
		this.remoteAddress = socketChannel.socket().getRemoteSocketAddress();
		this.predictor = predictor;
		try {
			this.socketChannel.socket().setReceiveBufferSize(config.getDefaultReadBufferSize());
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
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
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	public String toFullString() {
		return String
				.format("NioTcpByteChannel [socketChannel=%s, localAddress=%s, remoteAddress=%s, selectionKey=%s, predictor=%s, lastIoTime=%s, minReadBufferSize=%s, defaultReadBufferSize=%s, maxReadBufferSize=%s, maxWriteBufferSize=%s, id=%s, attributes=%s, state=%s]",
						socketChannel, localAddress, remoteAddress,
						selectionKey, predictor, lastIoTime, minReadBufferSize,
						defaultReadBufferSize, maxReadBufferSize,
						maxWriteBufferSize, id, attributes, state);
	}

}

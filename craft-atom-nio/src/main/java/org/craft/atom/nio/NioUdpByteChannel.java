package org.craft.atom.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;

import org.craft.atom.nio.spi.NioBufferSizePredictor;

/**
 * A nio channel for datagram-oriented sockets.
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
public class NioUdpByteChannel extends NioByteChannel {
	
	private DatagramChannel datagramChannel; 

	public NioUdpByteChannel(DatagramChannel datagramChannel, NioConfig config, NioBufferSizePredictor predictor) {
		super(config, predictor);
		
		if (datagramChannel == null) {
			throw new IllegalArgumentException("DatagramChannel can not be null.");
		}
		
		this.datagramChannel = datagramChannel;
		this.localAddress = datagramChannel.socket().getLocalSocketAddress();
		this.predictor = predictor;
		try {
			this.datagramChannel.socket().setReceiveBufferSize(config.getDefaultReadBufferSize());
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected SocketAddress readUdp(ByteBuffer buf) throws IOException {
		return datagramChannel.receive(buf);
	}

	@Override
	protected int writeUdp(ByteBuffer buf, SocketAddress target) throws IOException {
		return datagramChannel.send(buf, target);
	}

	@Override
	protected SelectableChannel innerChannel() {
		return datagramChannel;
	}

}

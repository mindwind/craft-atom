package org.craft.atom.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;

import lombok.ToString;

import org.craft.atom.nio.spi.NioBufferSizePredictor;
import org.craft.atom.nio.spi.NioChannelEventDispatcher;

/**
 * A nio channel for datagram-oriented sockets.
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
@ToString(callSuper = true)
public class NioUdpByteChannel extends NioByteChannel {
	
	private DatagramChannel datagramChannel; 

	public NioUdpByteChannel(DatagramChannel datagramChannel, NioConfig config, NioBufferSizePredictor predictor, NioChannelEventDispatcher dispatcher) {
		super(config, predictor, dispatcher);
		
		if (datagramChannel == null) {
			throw new IllegalArgumentException("DatagramChannel can not be null.");
		}
		
		this.datagramChannel = datagramChannel;
		this.localAddress = datagramChannel.socket().getLocalSocketAddress();
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

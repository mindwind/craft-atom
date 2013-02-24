package org.craft.atom.nio.api;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

import org.craft.atom.io.IoHandler;
import org.craft.atom.io.IoProtocol;
import org.craft.atom.nio.NioAcceptor;
import org.craft.atom.nio.NioByteChannel;
import org.craft.atom.nio.NioProcessor;
import org.craft.atom.nio.NioUdpByteChannel;
import org.craft.atom.nio.spi.NioBufferSizePredictor;
import org.craft.atom.nio.spi.NioChannelEventDispatcher;

/**
 * Acceptor for datagram based on UDP.
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
public class NioUdpAcceptor extends NioAcceptor {
	
	public NioUdpAcceptor(IoHandler handler, int port) {
		super(handler, port);
	}

	public NioUdpAcceptor(IoHandler handler, NioAcceptorConfig config, int port) {
		super(handler, config, port);
	}
	
	public NioUdpAcceptor(IoHandler handler, SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
		super(handler, firstLocalAddress, otherLocalAddresses);
	}
	
	public NioUdpAcceptor(IoHandler handler, NioAcceptorConfig config, SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
		super(handler, config, firstLocalAddress, otherLocalAddresses);
	}

	public NioUdpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictor predictor, SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
		super(handler, config, dispatcher, predictor, firstLocalAddress, otherLocalAddresses);
	}
	
	public NioUdpAcceptor(IoHandler handler) {
		super(handler);
	}
	
	public NioUdpAcceptor(IoHandler handler, NioAcceptorConfig config) {
		super(handler, config);
	}
	
	public NioUdpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher) {
		super(handler, config, dispatcher);
	}

	public NioUdpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictor predictor) {
		super(handler, config, dispatcher, predictor);
	}
	
	// ~ -------------------------------------------------------------------------------------------------------------

	@Override
	protected void bindByProtocol(SocketAddress address) throws IOException {
		DatagramChannel dc = DatagramChannel.open();
		dc.configureBlocking(false);
		dc.socket().setReuseAddress(config.isReuseAddress());
		dc.socket().bind(address);
		boundmap.put(address, dc);
		
		NioByteChannel channel = new NioUdpByteChannel(dc, config, predictor);
		NioProcessor processor = pool.pick(channel);
		processor.setProtocol(IoProtocol.UDP);
		channel.setProcessor(processor);
		processor.add(channel);
	}

	@Override
	protected NioByteChannel acceptByProtocol(SelectionKey key) {
		// UDP has no accept event, so we return null
		return null;
	}

}

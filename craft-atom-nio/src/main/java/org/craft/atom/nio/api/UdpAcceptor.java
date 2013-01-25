package org.craft.atom.nio.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

import org.craft.atom.nio.AbstractSession;
import org.craft.atom.nio.Processor;
import org.craft.atom.nio.Protocol;
import org.craft.atom.nio.UdpSession;
import org.craft.atom.nio.spi.Handler;

/**
 * Acceptor for datagram based UDP.
 * 
 * @author Hu Feng
 * @version 1.0, 2012-2-25
 */
public class UdpAcceptor extends Acceptor {
	
	//	private static final Log LOG = LogFactory.getLog(UdpAcceptor.class);
	
	// ~ -------------------------------------------------------------------------------------------------------------

	/**
	 * Constructs a new acceptor with default configuration, and binds to the
	 * specified local address port.
	 * 
	 * @param handler
	 * @param prot
	 */
	public UdpAcceptor(Handler handler, int port) {
		super(handler, new AcceptorConfig(), new InetSocketAddress(port));
	}

	/**
	 * Constructs a new acceptor with specified configuration, and binds to the
	 * specified local address port.
	 * 
	 * @param handler
	 * @param config
	 * @param prot
	 */
	public UdpAcceptor(Handler handler, AcceptorConfig config, int port) {
		super(handler, config, new InetSocketAddress(port));
	}

	/**
	 * Constructs a new acceptor with default configuration, and binds the
	 * socoket addresses.
	 * 
	 * @param handler
	 * @param firstLocalAddress
	 * @param otherLocalAddresses
	 */
	public UdpAcceptor(Handler handler, SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
		super(handler, new AcceptorConfig(), firstLocalAddress, otherLocalAddresses);
	}

	/**
	 * Constructs a new acceptor the specified configuration
	 * 
	 * @param handler
	 * @param config
	 * @param firstLocalAddress
	 * @param otherLocalAddresses
	 */
	public UdpAcceptor(Handler handler, AcceptorConfig config, SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
		super(handler, config, firstLocalAddress, otherLocalAddresses);
	}
 
	// ~ -------------------------------------------------------------------------------------------------------------

	@Override
	protected Session acceptByProtocol(SelectionKey key) {
		// UDP has no accept event, so we return null
		return null;
	}

	@Override
	protected void bindByProtocol(SocketAddress address) throws IOException {
		final DatagramChannel dc = DatagramChannel.open();
		dc.configureBlocking(false);
		dc.socket().setReuseAddress(config.isReuseAddress());
		dc.socket().bind(address);
		boundmap.put(address, dc);
		
		Processor processor = pool.get(Protocol.UDP);
		AbstractSession session = new UdpSession(dc, config, sizePredictorFactory.getPredictor());
		session.setProcessor(processor);
		processor.add(session);
		
	}

}

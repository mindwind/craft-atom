package org.craft.atom.nio.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.nio.TcpSession;
import org.craft.atom.nio.spi.Handler;

/**
 * Acceptor for incoming connection based TCP/IP.
 * 
 * @author Hu Feng
 * @version 1.0, 2012-2-25
 */
public final class TcpAcceptor extends Acceptor {
	
	private static final Log LOG = LogFactory.getLog(TcpAcceptor.class);
	
	// ~ -------------------------------------------------------------------------------------------------------------

	/**
	 * Constructs a new acceptor with default configuration, and binds to the
	 * specified local address port.
	 * 
	 * @param handler
	 * @param prot
	 */
	public TcpAcceptor(Handler handler, int port) {
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
	public TcpAcceptor(Handler handler, AcceptorConfig config, int port) {
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
	public TcpAcceptor(Handler handler, SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
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
	public TcpAcceptor(Handler handler, AcceptorConfig config, SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
		super(handler, config, firstLocalAddress, otherLocalAddresses);
	}

	// ~ -------------------------------------------------------------------------------------------------------------
	
	protected Session acceptByProtocol(SelectionKey key) {
		if (key == null || !key.isValid() || !key.isAcceptable()) {
            return null;
        }
		
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		SocketChannel sc = null;
		try {
			sc = ssc.accept();
			if(sc == null) {
				return null;
			}
			sc.configureBlocking(false);
		} catch (IOException e) {
			LOG.warn(e.getMessage(), e);
			if(sc != null) {
				try {
					sc.close();
				} catch (IOException ex) {
					LOG.warn(ex.getMessage(), ex);
				}
			}
		}
		
		Session session = new TcpSession(sc, config, this.sizePredictorFactory.getPredictor());
		
		return session;
	}
	
	protected void bindByProtocol(SocketAddress address) throws IOException {
		ServerSocketChannel ssc = ServerSocketChannel.open();

		ssc.configureBlocking(false);
		ServerSocket ss = ssc.socket();
		ss.setReuseAddress(config.isReuseAddress());
		ss.bind(address, config.getBacklog());
		ssc.register(selector, SelectionKey.OP_ACCEPT);
		boundmap.put(address, ssc);
	}
	
	// ~ -------------------------------------------------------------------------------------------------------------

}

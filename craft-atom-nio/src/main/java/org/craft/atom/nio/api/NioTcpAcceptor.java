package org.craft.atom.nio.api;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import lombok.ToString;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.io.IoHandler;
import org.craft.atom.nio.NioAcceptor;
import org.craft.atom.nio.NioByteChannel;
import org.craft.atom.nio.NioTcpByteChannel;
import org.craft.atom.nio.spi.NioBufferSizePredictorFactory;
import org.craft.atom.nio.spi.NioChannelEventDispatcher;

/**
 * Acceptor for incoming connection based on TCP.
 *  
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
@ToString(callSuper = true)
public class NioTcpAcceptor extends NioAcceptor {
	
	private static final Log LOG = LogFactory.getLog(NioTcpAcceptor.class);
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	public NioTcpAcceptor(IoHandler handler, int port) {
		super(handler, port);
	}

	public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, int port) {
		super(handler, config, port);
	}
	
	public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, int port) {
		super(handler, config, dispatcher, port);
	}
	
	public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory, int port) {
		super(handler, config, dispatcher, predictorFactory, port);
	}
	
	public NioTcpAcceptor(IoHandler handler, SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
		super(handler, firstLocalAddress, otherLocalAddresses);
	}
	
	public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
		super(handler, config, firstLocalAddress, otherLocalAddresses);
	}
	
	public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
		super(handler, config, dispatcher, firstLocalAddress, otherLocalAddresses);
	}

	public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory, SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
		super(handler, config, dispatcher, predictorFactory, firstLocalAddress, otherLocalAddresses);
	}
	
	public NioTcpAcceptor(IoHandler handler) {
		super(handler);
	}
	
	public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config) {
		super(handler, config);
	}
	
	public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher) {
		super(handler, config, dispatcher);
	}

	public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory) {
		super(handler, config, dispatcher, predictorFactory);
	}
	
	// ~ -------------------------------------------------------------------------------------------------------------

	@Override
	protected void bindByProtocol(SocketAddress address) throws IOException {
		ServerSocketChannel ssc = ServerSocketChannel.open();

		ssc.configureBlocking(false);
		ServerSocket ss = ssc.socket();
		ss.setReuseAddress(config.isReuseAddress());
		ss.bind(address, config.getBacklog());
		ssc.register(selector, SelectionKey.OP_ACCEPT);
		boundmap.put(address, ssc);
	}

	@Override
	protected NioByteChannel acceptByProtocol(SelectionKey key) throws IOException {
		if (key == null || !key.isValid() || !key.isAcceptable()) {
            return null;
        }
		
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		SocketChannel sc = null;
		try {
			sc = ssc.accept();
			if (sc == null) {
				return null;
			}
			sc.configureBlocking(false);
			NioByteChannel channel = new NioTcpByteChannel(sc, config, predictorFactory.newPredictor(config.getMinReadBufferSize(), config.getDefaultReadBufferSize(), config.getMaxReadBufferSize()), dispatcher);
			return channel;
		} catch (IOException e) {
			if (sc != null) {
				try {
					sc.close();
				} catch (IOException ex) {
					LOG.error(ex.getMessage(), ex);
				}
			}
			throw e;
		}
	}

}

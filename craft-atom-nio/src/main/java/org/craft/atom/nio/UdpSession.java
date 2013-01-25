package org.craft.atom.nio;

import java.net.SocketException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.nio.api.AbstractConfig;
import org.craft.atom.nio.spi.SizePredictor;

/**
 * @author Hu Feng
 * @version 1.0, 2012-2-26
 */
public class UdpSession extends AbstractSession {
	
	private static final Log LOG = LogFactory.getLog(UdpSession.class);
	
	private DatagramChannel datagramChannel; 

	public UdpSession(DatagramChannel datagramChannel, AbstractConfig config, SizePredictor sizePredictor) {
		super(config, sizePredictor);
		this.datagramChannel = datagramChannel;
		this.localAddress = datagramChannel.socket().getLocalSocketAddress();
		
		// override read buffer size using default OS configuration.
		try {
			super.setReadBufferSize(datagramChannel.socket().getReceiveBufferSize());
		} catch (SocketException e) {
			LOG.warn("Setting udp session read buffer size error!", e);
		}
	}
	
	public UdpSession(UdpSession session) {
		super(session);
		this.datagramChannel = session.getDatagramChannel();
	}

	@Override
	public SelectableChannel getChannel() {
		return datagramChannel;
	}

	public DatagramChannel getDatagramChannel() {
		return datagramChannel;
	}

	public void setDatagramChannel(DatagramChannel datagramChannel) {
		this.datagramChannel = datagramChannel;
	}

}

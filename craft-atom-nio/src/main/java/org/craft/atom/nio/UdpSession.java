package org.craft.atom.nio;

import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;

import org.craft.atom.nio.api.AbstractConfig;
import org.craft.atom.nio.spi.SizePredictor;

/**
 * @author Hu Feng
 * @version 1.0, 2012-2-26
 */
public class UdpSession extends AbstractSession {
	
	private DatagramChannel datagramChannel; 

	public UdpSession(DatagramChannel datagramChannel, AbstractConfig config, SizePredictor sizePredictor) {
		super(config, sizePredictor);
		this.datagramChannel = datagramChannel;
		this.localAddress = datagramChannel.socket().getLocalSocketAddress();
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

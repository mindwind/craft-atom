package org.craft.atom.nio;

import org.craft.atom.io.api.AbstractChannel;
import org.craft.atom.io.api.Channel;

/**
 * Nio channel transmit bytes.
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 * @see NioTcpChannel
 * @see NioUdpChannel
 */
abstract public class NioChannel extends AbstractChannel implements Channel<byte[]> {

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean write(byte[] data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeAttribute(Object key) {
		// TODO Auto-generated method stub

	}

}

package org.craft.atom.io;

import java.net.SocketAddress;
import java.util.concurrent.Future;

/**
 * Connects to server based on specific implementation.
 * 
 * @author mindwind
 * @version 1.0, Mar 12, 2013
 */
public interface IoConnector {
	
	/**
	 * Connects to the specified ip and port.
	 * 
	 * @param ip
	 * @param port
	 * @return <code>Future</code> instance which is completed when the channel initiated by this call succeeds or fails.
	 */
	Future<Channel<byte[]>> connect(String ip, int port);
	
	/**
	 * Connects to the specified remote address.
	 * 
	 * @param remoteAddress
	 * @return <code>Future</code> instance which is completed when the channel initiated by this call succeeds or fails.
	 */
	Future<Channel<byte[]>> connect(SocketAddress remoteAddress);
	
	
	/**
	 * Connects to the specified remote address and binds to the specified local address.
	 * 
	 * @param remoteAddress
	 * @param localAddress
	 * @return <code>Future</code> instance which is completed when the channel initiated by this call succeeds or fails.
	 */
	Future<Channel<byte[]>> connect(SocketAddress remoteAddress, SocketAddress localAddress);
}

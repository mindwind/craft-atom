package io.craft.atom.io;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.Future;

/**
 * Connects to server based on specific implementation.
 * 
 * @author mindwind
 * @version 1.0, Mar 12, 2013
 */
public interface IoConnector extends IoReactor, IoConnectorMBean {
	
	/**
	 * Connects to the specified ip and port.
	 * 
	 * @param ip
	 * @param port
	 * @return <code>Future</code> instance which is completed when the channel initiated by this call succeeds or fails.
	 * @throws IOException If some other I/O error occurs
	 */
	Future<Channel<byte[]>> connect(String ip, int port) throws IOException;
	
	/**
	 * Connects to the specified remote address.
	 * 
	 * @param remoteAddress
	 * @return <code>Future</code> instance which is completed when the channel initiated by this call succeeds or fails.
	 * @throws IOException If some other I/O error occurs
	 */
	Future<Channel<byte[]>> connect(SocketAddress remoteAddress) throws IOException;
	
	
	/**
	 * Connects to the specified remote address and binds to the specified local address.
	 * 
	 * @param remoteAddress
	 * @param localAddress
	 * @return <code>Future</code> instance which is completed when the channel initiated by this call succeeds or fails.
	 * @throws IOException If some other I/O error occurs
	 */
	Future<Channel<byte[]>> connect(SocketAddress remoteAddress, SocketAddress localAddress) throws IOException;
}

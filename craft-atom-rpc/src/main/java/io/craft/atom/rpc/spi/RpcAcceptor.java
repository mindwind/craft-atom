package io.craft.atom.rpc.spi;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * RPC acceptor.
 * <p>
 * Accepts incoming rpc requests, use {@link RpcChannel} to communicate with rpc client.
 * 
 * @see RpcChannel
 * @author mindwind
 * @version 1.0, Aug 6, 2014
 */
public interface RpcAcceptor {
	
	/**
	 * Bind to settled local address.
	 * 
	 * @throws IOException If some other I/O error occurs
	 */
	void bind() throws IOException;
	
	/**
	 * Close itself and release all the resources.
	 */
	void close();
	
	/**
	 * Set rpc processor
	 * 
	 * @param processor
	 */
	void setProcessor(RpcProcessor processor);
	
	/**
	 * Set rpc protocol
	 * 
	 * @param protocol
	 */
	void setProtocol(RpcProtocol protocol);
	
	/**
	 * Set address to bind.
	 * 
	 * @param address
	 */
	void setAddress(SocketAddress address);
	
	/**
	 * Set io timeout in millisecond
	 * 
	 * @param ioTimeoutInMillis
	 */
	void setIoTimeoutInMillis(int ioTimeoutInMillis);
	
	/**
	 * Set max accepted connection size
	 * 
	 * @param connections
	 */
	void setConnections(int connections);
	
	/**
	 * @return current connection count of the acceptor.
	 */
	int connectionCount();
	
}

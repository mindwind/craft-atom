package org.craft.atom.rpc.spi;

import java.io.IOException;

/**
 * RPC acceptor.
 * 
 * 
 * @author mindwind
 * @version 1.0, Aug 6, 2014
 */
public interface RpcAcceptor {
	
	
	/**
	 * Bind to specific host and port.
	 * 
	 * @param host
	 * @param port
	 * @param ioTimeoutInMillis in millis
	 * @throws IOException, thrown while bind failed.
	 */
	void bind(String host, int port, int ioTimeoutInMillis) throws IOException;
	
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
	
}

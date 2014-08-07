package org.craft.atom.rpc.spi;

import java.io.IOException;

/**
 * RPC transporter.
 * 
 * 
 * @author mindwind
 * @version 1.0, Aug 6, 2014
 */
public interface RpcServerTransporter {
	
	
	/**
	 * Bind transporter to specific host and port.
	 * 
	 * @param host
	 * @param port
	 * @param ioTimeoutInMillis in millis
	 * @throws IOException, thrown while bind failed.
	 */
	void bind(String host, int port, int ioTimeoutInMillis) throws IOException;
	
}

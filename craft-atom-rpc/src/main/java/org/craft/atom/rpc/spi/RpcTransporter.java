package org.craft.atom.rpc.spi;

/**
 * RPC transporter.
 * 
 * 
 * @author mindwind
 * @version 1.0, Aug 6, 2014
 */
public interface RpcTransporter {
	
	
	/**
	 * Bind to specific ip and port.
	 * 
	 * @param host
	 * @param port
	 */
	void bind(String ip, int port);
	
}

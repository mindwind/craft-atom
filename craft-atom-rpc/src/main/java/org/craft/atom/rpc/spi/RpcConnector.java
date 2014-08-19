package org.craft.atom.rpc.spi;

import java.io.IOException;
import java.net.SocketAddress;

import org.craft.atom.protocol.rpc.model.RpcMessage;


/**
 * RPC connector connects to RPC server, communicates with the server.
 * 
 * @author mindwind
 * @version 1.0, Aug 14, 2014
 */
public interface RpcConnector {
	
	/**
	 * Connect to rpc server.
	 *
	 * @return connection id
	 * @throws IOException If some other I/O error occurs
	 */
	long connect() throws IOException;
	
	/**
	 * Disconnect the connection with specified id.
	 *  
	 * @param connectionId
	 */
	void disconnect(long connectionId);
	
	/**
	 * Send rpc request message and return rpc response message.
	 * 
	 * @param req  rpc req msg
	 * @return     rpc rsp msg
	 * @throws IOException If some other I/O error occurs
	 */
	RpcMessage send(RpcMessage req) throws IOException;
	
	/**
	 * Set address to connect.
	 * 
	 * @param address
	 */
	void setAddress(SocketAddress address);
	
	/**
	 * Set heartbeat in millisecond
	 * 
	 * @param heartbeatInMillis
	 */
	void setHeartbeatInMillis(int heartbeatInMillis);
	
	/**
	 * Set connect timeout in millisecond
	 * 
	 * @param connectTimeoutInMillis
	 */
	void setConnectTimeoutInMillis(int connectTimeoutInMillis);
	
}

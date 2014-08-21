package org.craft.atom.rpc.spi;

import java.io.IOException;
import java.net.SocketAddress;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.RpcException;


/**
 * RPC connector connects to RPC server, communicates with the server.
 * Hold and keep the heartbeat for all connections.
 * 
 * @author mindwind
 * @version 1.0, Aug 14, 2014
 */
public interface RpcConnector {
	
	/**
	 * Connect to rpc server.
	 *
	 * @return connection id
	 * @throws RpcException If some other rpc error occurs
	 */
	long connect() throws RpcException;
	
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
	 * @throws IOException If some other rpc error occurs
	 */
	RpcMessage send(RpcMessage req) throws RpcException;
	
	/**
	 * Set rpc protocol
	 * 
	 * @param protocol
	 */
	void setProtocol(RpcProtocol protocol);
	
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

package org.craft.atom.rpc.spi;

import java.net.SocketAddress;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.RpcException;


/**
 * RPC connector.
 * <p>
 * Connects to rpc server, communicates with the server.
 * Hold and keep long-lived connections.
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
	 * @return <code>false</code> if connection id is not exist, otherwise <code>true</code>
	 */
	boolean disconnect(long connectionId);
	
	/**
	 * Close itself and release all the resources.
	 */
	void close();
	
	/**
	 * Send rpc request message and wait return rpc response message.
	 * If 'async' flag is set and return <tt>null</tt>.
	 * 
	 * @param req   rpc req msg
	 * @param async flag for asynchronous request.
	 * @return      rpc rsp msg
	 * @throws RpcException If some other rpc error occurs
	 */
	RpcMessage send(RpcMessage req, boolean async) throws RpcException;
	
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
	
	/**
	 * Set global rpc timeout in millisecond
	 * 
	 * @param rpcTimeoutInMillis
	 */
	void setRpcTimeoutInMillis(int rpcTimeoutInMillis);
	
	/**
	 * @return global rpc timeout in millisecond
	 */
	int getRpcTimeoutInMillis();
	
}

package org.craft.atom.protocol.rpc.model;

import java.io.Serializable;
import java.net.InetSocketAddress;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * RPC messages use generic message format for transferring data.
 * <pre>
 *      rpc-message = rpc-header + [ rpc-body ]
 * </pre>
 * 
 * @param <T> the type of the interface class.
 * 
 * @author mindwind
 * @version 1.0, Jul 17, 2014
 */
@ToString
@EqualsAndHashCode(of = { "header", "body" })
public class RpcMessage implements Serializable {

	
	private static final long serialVersionUID = 5138100956693144357L;
	
	
	@Getter @Setter private RpcHeader  header;
	@Getter @Setter private RpcBody    body  ;

	
	public boolean isOneWay() {
		return header.isOw();
	}
	
	public boolean isHeartBeat() {
		return header.isHb();
	}
	
	public long getId() {
		return header.getId();
	}
	
	public long getRpcTimeoutInMillis() {
		return body.getRpcOption().getRpcTimeoutInMillis();
	}
	
	public void setRpcTimeoutInMillis(int rpcTimeoutInMillis) {
		body.getRpcOption().setRpcTimeoutInMillis(rpcTimeoutInMillis);
	}
	
	public void setServerAddress(InetSocketAddress serverAddress) {
		body.getRpcOption().setServerAddress(serverAddress);
	}
	
	public void setClientAddress(InetSocketAddress clientAddress) {
		body.getRpcOption().setClientAddress(clientAddress);
	}
	
	public Object getReturnObject() {
		return body.getReturnObject();
	}
}

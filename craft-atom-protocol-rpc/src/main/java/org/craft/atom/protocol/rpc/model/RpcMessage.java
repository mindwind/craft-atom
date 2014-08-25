package org.craft.atom.protocol.rpc.model;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

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
	
	public void setLocalAddress(SocketAddress localAddress) {
		body.getRpcOption().setLocalAddress(localAddress);
	}
	
	public void setRemoteAddress(SocketAddress remoteAddress) {
		body.getRpcOption().setRemoteAddress(remoteAddress);
	}
	
	public Object getReturnObject() {
		return body.getReturnObject();
	}
}

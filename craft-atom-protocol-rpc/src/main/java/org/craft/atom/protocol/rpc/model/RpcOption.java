package org.craft.atom.protocol.rpc.model;

import java.net.InetSocketAddress;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A <code>RpcOption</code> provides optional information about rpc invocation.
 * 
 * @author mindwind
 * @version 1.0, Aug 8, 2014
 */
@ToString
public class RpcOption {
	
	
	@Getter @Setter transient private InetSocketAddress serverAddress                         ;
	@Getter @Setter transient private InetSocketAddress clientAddress                         ;
	@Getter @Setter           private int               rpcTimeoutInMillis = Integer.MAX_VALUE;
	
	
}

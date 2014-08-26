package org.craft.atom.protocol.rpc.model;

import java.net.SocketAddress;

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
	
	
	@Getter @Setter private int               rpcTimeoutInMillis = Integer.MAX_VALUE;
	@Getter @Setter private int               rpcThreads         = 1                ;
	@Getter @Setter private int               rpcQueues          = 10               ;
	@Getter @Setter private SocketAddress     serverAddress                         ;
	@Getter @Setter private SocketAddress     clientAddress                         ;
	
	
}

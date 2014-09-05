package org.craft.atom.rpc;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.protocol.rpc.model.RpcMethod;
import org.craft.atom.rpc.api.RpcParameter;

/**
 * @author mindwind
 * @version 1.0, Aug 12, 2014
 */
public class RpcEntry {
	
	
	@Getter @Setter private Class<?>     rpcInterface;
	@Getter @Setter private RpcMethod    rpcMethod   ;
	@Getter @Setter private Object       rpcObject   ;
	@Getter @Setter private RpcParameter rpcParameter;

	
}

package org.craft.atom.rpc;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.api.RpcContext;
import org.craft.atom.rpc.spi.RpcConnector;
import org.craft.atom.rpc.spi.RpcInvoker;

/**
 * @author mindwind
 * @version 1.0, Aug 21, 2014
 */
public class DefaultRpcClientInvoker implements RpcInvoker {
	
	
	@Getter @Setter private RpcConnector connector;

	
	@Override
	public RpcMessage invoke(RpcMessage req) throws RpcException {		
		req.setRpcTimeoutInMillis(rpcTimeoutInMillis());
		return connector.send(req);
	}
	
	private int rpcTimeoutInMillis() {
		// Get timeout with this invocation from RpcContext
		int timeout = RpcContext.getContext().getRpcTimeoutInMillis();
		if (timeout > 0) return timeout;
		
		// if not set timeout for this invocation, get global setting.
		timeout = connector.getRpcTimeoutInMillis();
		if (timeout > 0) return timeout;
		
		// default
		timeout = Integer.MAX_VALUE;
		return timeout;
	}

	
}

package org.craft.atom.rpc;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.api.RpcContext;
import org.craft.atom.rpc.spi.RpcConnector;
import org.craft.atom.rpc.spi.RpcInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Aug 21, 2014
 */
public class DefaultRpcClientInvoker implements RpcInvoker {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRpcClientInvoker.class);
	
	
	@Getter @Setter private RpcConnector connector;

	
	@Override
	public RpcMessage invoke(RpcMessage req) throws RpcException {
		try {
			RpcContext ctx = RpcContext.getContext();
			req.setRpcTimeoutInMillis(rpcTimeoutInMillis(ctx));
			req.setOneway(ctx.isOneway());
			req.setAttachments(ctx.getAttachments());
			LOG.debug("[CRAFT-ATOM-RPC] Rpc client invoker is invoking, |rpcContext={}|", ctx);
			
			return connector.send(req);
		} finally {
			RpcContext.removeContext();
		}
		
	}
	
	private int rpcTimeoutInMillis(RpcContext ctx) {
		// Get timeout with this invocation from RpcContext
		int timeout = ctx.getRpcTimeoutInMillis();
		if (timeout > 0) return timeout;
		
		// if does not set timeout for this invocation, get global setting.
		timeout = connector.getRpcTimeoutInMillis();
		if (timeout > 0) return timeout;
		
		// default
		timeout = Integer.MAX_VALUE;
		return timeout;
	}

	
}

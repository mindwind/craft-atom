package io.craft.atom.rpc;

import io.craft.atom.protocol.rpc.model.RpcMessage;
import io.craft.atom.rpc.api.RpcContext;
import io.craft.atom.rpc.spi.RpcConnector;
import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Aug 21, 2014
 */
public class DefaultRpcClientInvoker extends AbstractRpcInvoker {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRpcClientInvoker.class);
	
	
	@Getter @Setter private RpcConnector connector;

	
	@Override
	public RpcMessage invoke(RpcMessage req) throws RpcException {
		try {
			RpcContext ctx = RpcContext.getContext();
			req.setRpcTimeoutInMillis(rpcTimeoutInMillis(ctx));
			req.setOneway(ctx.isOneway());
			req.setAttachments(ctx.getAttachments());
			req.setRpcId(ctx.getRpcId());
			boolean async = ctx.isAsync();
			LOG.debug("[CRAFT-ATOM-RPC] Rpc client invoker is invoking, |req={}, async={}|", req, async);
			return connector.send(req, async);
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

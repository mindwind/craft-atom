package org.craft.atom.rpc;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.protocol.rpc.model.RpcMessage;
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
		// one way request, client does not expect response
		if (req.isOneWay()) return null;
		
		// TODO set rpc timeout
		return connector.send(req);
	}

	
}

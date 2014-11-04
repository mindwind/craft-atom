package io.craft.atom.rpc;

import io.craft.atom.rpc.spi.RpcConnector;
import io.craft.atom.rpc.spi.RpcInvoker;
import io.craft.atom.rpc.spi.RpcRegistry;

/**
 * @author mindwind
 * @version 1.0, Oct 11, 2014
 */
public abstract class AbstractRpcInvoker implements RpcInvoker {


	@Override
	public void setConnector(RpcConnector connector) {}

	@Override
	public void setRegistry(RpcRegistry registry) {}

}

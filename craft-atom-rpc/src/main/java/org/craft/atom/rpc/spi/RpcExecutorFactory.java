package org.craft.atom.rpc.spi;

import java.util.concurrent.Executor;

import org.craft.atom.protocol.rpc.model.RpcMethod;

/**
 * RPC executor factory
 * 
 * @author mindwind
 * @version 1.0, Aug 11, 2014
 */
public interface RpcExecutorFactory {
	
	/**
	 * Get a new (or reusable) executor.
	 * 
	 * @param rpcInterface
	 * @param rpcMethod
	 * @return executor
	 */
	Executor getExecutor(Class<?> rpcInterface, RpcMethod rpcMethod);
	
}

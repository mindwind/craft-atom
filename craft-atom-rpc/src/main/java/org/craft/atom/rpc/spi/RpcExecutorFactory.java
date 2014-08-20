package org.craft.atom.rpc.spi;

import java.util.concurrent.ExecutorService;

import org.craft.atom.protocol.rpc.model.RpcMessage;

/**
 * RPC executor factory provides method for getting or creating <code>ExecutorService</code> instance.
 * 
 * @author mindwind
 * @version 1.0, Aug 11, 2014
 */
public interface RpcExecutorFactory {
	
	/**
	 * Get a new (or reusable) executor service.
	 * 
	 * @param  msg
	 * @return executor
	 */
	ExecutorService getExecutor(RpcMessage msg);
	
}

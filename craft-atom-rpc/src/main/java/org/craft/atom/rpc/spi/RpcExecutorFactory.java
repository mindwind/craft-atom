package org.craft.atom.rpc.spi;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.util.thread.MonitoringExecutorService;

/**
 * RPC executor factory provides method for getting or creating <code>ExecutorService</code> instance.
 * 
 * @see RpcProcessor
 * @author mindwind
 * @version 1.0, Aug 11, 2014
 */
public interface RpcExecutorFactory {
	
	/**
	 * Get a new (or reusable) executor service.
	 * 
	 * @param  api
	 * @return executor
	 */
	MonitoringExecutorService getExecutor(RpcApi api);
	
	/**
	 * Set rpc registry
	 * 
	 * @param registry
	 */
	void setRegistry(RpcRegistry registry);
	
}

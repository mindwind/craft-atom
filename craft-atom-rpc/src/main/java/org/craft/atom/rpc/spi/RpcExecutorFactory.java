package org.craft.atom.rpc.spi;

import org.craft.atom.rpc.RpcException;
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
	 * Get a new (or reusable) monitoring executor service.
	 * 
	 * @param  api
	 * @return executor
	 * @throws RpcException if any rpc error occurs.
	 */
	MonitoringExecutorService getExecutor(RpcApi api) throws RpcException;
	
	/**
	 * Set rpc registry
	 * 
	 * @param registry
	 */
	void setRegistry(RpcRegistry registry);
	
	/**
	 * Shutdown the factory
	 */
	void shutdown();
	
}

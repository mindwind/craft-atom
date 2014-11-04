package io.craft.atom.rpc.spi;

import io.craft.atom.protocol.rpc.model.RpcMessage;

/**
 * RPC processor.
 * <p>
 * Processor in charge of process rpc request, it delegates {@link RpcExecutorFactory} to implement special thread execute strategy 
 * and delegates {@link RpcInvoker} to implement special reflected invocation. {@code RpcProcessor } itself manages execute timeout and handle exception.
 * 
 * @see RpcInvoker
 * @see RpcExecutorFactory
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public interface RpcProcessor {
	
	/**
	 * Process rpc request.
	 * 
	 * @param req     rpc request
	 * @param channel used to send back rpc response message.
	 */
	void process(RpcMessage req, RpcChannel channel);
	
	/**
	 * Close itself and release all the resources.
	 */
	void close();
	
	/**
	 * Set rpc invoker.
	 * 
	 * @param invoker
	 */
	void setInvoker(RpcInvoker invoker);
	
	/**
	 * set rpc executor factory.
	 * 
	 * @param executorFactory
	 */
	void setExecutorFactory(RpcExecutorFactory executorFactory);
	
	/**
	 * @return the approximate wait request count of the rpc api.
	 */
	int waitCount(RpcApi api);
	
	/**
	 * @return the approximate processing request count of the rpc api.
	 */
	int processingCount(RpcApi api);
	
	/**
	 * @return the approximate complete request count of the rpc api.
	 */
	long completeCount(RpcApi api);
	
}

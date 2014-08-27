package org.craft.atom.rpc.spi;

import org.craft.atom.protocol.rpc.model.RpcMessage;

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
	 * @return rpc response can not be null, any request must has response.
	 */
	void process(RpcMessage req, RpcChannel channel);
	
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
	
}

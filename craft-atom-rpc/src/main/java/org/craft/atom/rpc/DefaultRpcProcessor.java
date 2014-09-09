package org.craft.atom.rpc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.spi.RpcChannel;
import org.craft.atom.rpc.spi.RpcExecutorFactory;
import org.craft.atom.rpc.spi.RpcInvoker;
import org.craft.atom.rpc.spi.RpcProcessor;
import org.craft.atom.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public class DefaultRpcProcessor implements RpcProcessor {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRpcProcessor.class);
		
	
	@Getter @Setter private RpcInvoker         invoker        ;
	@Getter @Setter private RpcExecutorFactory executorFactory;
	@Getter @Setter private ExecutorService    timeoutExecutor;
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public DefaultRpcProcessor() {
		this.timeoutExecutor = Executors.newCachedThreadPool(new NamedThreadFactory("craft-atom-rpc-timeout"));
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public void process(RpcMessage req, RpcChannel channel) {
		if (req == null) return;
		if (req.isHeartbeat()) { 
			RpcMessage rsp = RpcMessages.newHbResponseRpcMessage(req.getId());
			channel.write(rsp);
			LOG.debug("[CRAFT-ATOM-RPC] Rpc process heartbeat, |hbreq={}, hbrsp={}, channel={}|", req, rsp, channel);
			return;
		}
		
		ExecutorService executor = executor(req);
		executor.execute(new ProcessTask(req, channel));
		LOG.debug("[CRAFT-ATOM-RPC] Rpc process, |req={}, channel={}, executor={}|", req, channel, executor);
	}

	private RpcMessage process0(RpcMessage req) {
		RpcMessage rsp;
		try {
			rsp = invoker.invoke(req);
		} catch (RpcException e) {
			rsp = RpcMessages.newRsponseRpcMessage(req.getId(), e);
		}
		return rsp;
	}
	
	private int rpcTimeoutInMillis(RpcMessage req) {
		int timeout = req.getRpcTimeoutInMillis();
		if (timeout == 0) { timeout = Integer.MAX_VALUE; }
		return timeout;
	}
	
	private ExecutorService executor(RpcMessage msg) {
		return executorFactory.getExecutor(msg);
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	private class ProcessTask implements Runnable {
		
		
		private RpcMessage req;
		private RpcChannel channel;
		
		
		public ProcessTask(RpcMessage req, RpcChannel channel) {
			this.req     = req;
			this.channel = channel;
		}
		

		@Override
		public void run() {
			RpcMessage rsp;
			try {
				Future<RpcMessage> future = timeoutExecutor.submit(new Callable<RpcMessage>() {
					@Override
					public RpcMessage call() throws Exception {
						return process0(req);
					}
				});
				// One way request
				if (req.isOneway()) return;
				
				// Wait response
				rsp = future.get(rpcTimeoutInMillis(req), TimeUnit.MILLISECONDS);
			} catch (ExecutionException e) {
				rsp = RpcMessages.newRsponseRpcMessage(req.getId(), new RpcException(RpcException.SERVER_ERROR, e));
			} catch (TimeoutException e) {
				rsp = RpcMessages.newRsponseRpcMessage(req.getId(), new RpcException(RpcException.SERVER_TIMEOUT, e));
			} catch (RejectedExecutionException e) {
				rsp = RpcMessages.newRsponseRpcMessage(req.getId(), new RpcException(RpcException.SERVER_OVERLOAD, e));
			} catch (RpcException e) {
				rsp = RpcMessages.newRsponseRpcMessage(req.getId(), e);
			} catch (Exception e) {
				rsp = RpcMessages.newRsponseRpcMessage(req.getId(), new RpcException(RpcException.UNKNOWN, e));
			}
			
			try {
				channel.write(rsp);
			} catch (Exception e) {
				LOG.warn("[CRAFT-ATOM-RPC] Write back rpc response fail", e);
			}
			
		}
	}

}

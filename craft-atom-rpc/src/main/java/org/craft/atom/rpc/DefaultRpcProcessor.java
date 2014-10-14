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
import org.craft.atom.protocol.rpc.model.RpcMethod;
import org.craft.atom.rpc.spi.RpcApi;
import org.craft.atom.rpc.spi.RpcChannel;
import org.craft.atom.rpc.spi.RpcExecutorFactory;
import org.craft.atom.rpc.spi.RpcInvoker;
import org.craft.atom.rpc.spi.RpcProcessor;
import org.craft.atom.util.thread.MonitoringExecutorService;
import org.craft.atom.util.thread.NamedThreadFactory;
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
			LOG.debug("[CRAFT-ATOM-RPC] Rpc server processor process heartbeat, |hbreq={}, hbrsp={}, channel={}|", req, rsp, channel);
			return;
		}
		
		RpcApi api = api(req);
		MonitoringExecutorService executor = null;
		try {
			executor = executor(api);
			executor.execute(new ProcessTask(req, channel));
		} catch (RejectedExecutionException e) {
			LOG.warn("[CRAFT-ATOM-RPC] Rpc server processor overload, |executor={}|", executor);
			channel.write(RpcMessages.newRsponseRpcMessage(req.getId(), new RpcException(RpcException.SERVER_OVERLOAD, "server overload")));
		} catch (RpcException e) {
			LOG.warn("[CRAFT-ATOM-RPC] Rpc server processor error", e);
			channel.write(RpcMessages.newRsponseRpcMessage(req.getId(), e));
		}
		LOG.debug("[CRAFT-ATOM-RPC] Rpc server processor process request, |req={}, channel={}, executor={}|", req, channel, executor);
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
	
	private MonitoringExecutorService executor(RpcApi api) {
		return executorFactory.getExecutor(api);
	}
	
	private RpcApi api(RpcMessage msg) {
		String          rpcId        = msg.getBody().getRpcId(); 
		RpcMethod       rpcMethod    = msg.getBody().getRpcMethod();
		Class<?>        rpcInterface = msg.getBody().getRpcInterface();
		DefaultRpcApi   api          = new DefaultRpcApi(rpcId, rpcInterface, rpcMethod);
		return api;
	}
	
	@Override
	public int waitCount(RpcApi api) {
		return executor(api).waitCount();
	}


	@Override
	public int processingCount(RpcApi api) {
		return executor(api).executingCount();
	}


	@Override
	public long completeCount(RpcApi api) {
		return executor(api).completeCount();
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
				LOG.warn("[CRAFT-ATOM-RPC] Rpc server processor execute error", e);
				rsp = RpcMessages.newRsponseRpcMessage(req.getId(), new RpcException(RpcException.SERVER_ERROR, "server error"));
			} catch (TimeoutException e) {
				LOG.warn("[CRAFT-ATOM-RPC] Rpc server processor execute timeout", e);
				rsp = RpcMessages.newRsponseRpcMessage(req.getId(), new RpcException(RpcException.SERVER_TIMEOUT, "server timeout"));
			} catch (Exception e) {
				LOG.warn("[CRAFT-ATOM-RPC] Rpc server processor execute unknown error", e);
				rsp = RpcMessages.newRsponseRpcMessage(req.getId(), new RpcException(RpcException.UNKNOWN, "unknown error"));
			}
			
			try {
				channel.write(rsp);
				LOG.debug("[CRAFT-ATOM-RPC] Rpc server processor process response, |rsp={}, channel={}|", rsp, channel);
			} catch (Exception e) {
				LOG.warn("[CRAFT-ATOM-RPC] Rpc server processor write back rpc response fail", e);
			}
		}
	}


}

package org.craft.atom.rpc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.spi.RpcExecutorFactory;
import org.craft.atom.rpc.spi.RpcInvoker;
import org.craft.atom.rpc.spi.RpcProcessor;

/**
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public class DefaultRpcProcessor implements RpcProcessor {
		
	
	@Getter @Setter private RpcInvoker         invoker        ;
	@Getter @Setter private RpcExecutorFactory executorFactory;
	
	
	@Override
	public RpcMessage process(final RpcMessage req) {
		try {
			check(req);
			ExecutorService executor = executor(req);
			Future<RpcMessage> future = executor.submit(new Callable<RpcMessage>() {
				@Override
				public RpcMessage call() throws Exception {
					return process0(req);
				}
			});
			return future.get(rpcTimeoutInMillis(req), TimeUnit.MILLISECONDS);
		} catch (ExecutionException e) {
			return RpcMessages.newRsponseRpcMessage(req.getHeader().getId(), new RpcException(RpcException.SERVER_ERROR, e));
		} catch (TimeoutException e) {
			return RpcMessages.newRsponseRpcMessage(req.getHeader().getId(), new RpcException(RpcException.SERVER_TIMEOUT, e));
		} catch (RejectedExecutionException e) {
			return RpcMessages.newRsponseRpcMessage(req.getHeader().getId(), new RpcException(RpcException.SERVER_OVERLOAD, e));
		} catch (RpcException e) {
			return RpcMessages.newRsponseRpcMessage(req.getHeader().getId(), e);
		} catch (Exception e) {
			return RpcMessages.newRsponseRpcMessage(req.getHeader().getId(), new RpcException(RpcException.UNKNOWN, e));
		}
	}
	
	private void check(RpcMessage rm) {
		if (rm == null || rm.getBody() == null) {
			throw new RpcException(RpcException.CLIENT_BAD_REQ);
		}
	}
	
	private RpcMessage process0(RpcMessage req) {
		RpcMessage rsp;
		try {
			rsp = invoker.invoke(req);
		} catch (RpcException e) {
			rsp = RpcMessages.newRsponseRpcMessage(req.getHeader().getId(), e);
		}
		return rsp;
	}
	
	private long rpcTimeoutInMillis(RpcMessage req) {
		long timeout = req.getBody().getRpcOption().getRpcTimeoutInMillis();
		if (timeout == 0) { timeout = Long.MAX_VALUE; }
		return timeout;
	}
	
	private ExecutorService executor(RpcMessage rm) {
		return executorFactory.getExecutor(rm);
	}

}

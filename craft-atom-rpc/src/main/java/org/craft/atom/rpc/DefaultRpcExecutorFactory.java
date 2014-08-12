package org.craft.atom.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.protocol.rpc.model.RpcMethod;
import org.craft.atom.rpc.spi.RpcExecutorFactory;
import org.craft.atom.util.NamedThreadFactory;

/**
 * @author mindwind
 * @version 1.0, Aug 12, 2014
 */
public class DefaultRpcExecutorFactory implements RpcExecutorFactory {

	
	private Map<String, ExecutorService> pool = new ConcurrentHashMap<String, ExecutorService>();
	
	
	@Override
	public ExecutorService getExecutor(RpcMessage rm) {
		Class<?> rpcInterface = rm.getBody().getRpcInterface();
		RpcMethod rpcMethod = rm.getBody().getRpcMethod();
		String key = Integer.toString(rpcInterface.hashCode()) + Integer.toString(rpcMethod.hashCode());
		return getExecutor(key);
	}
	
	private ExecutorService getExecutor(String key) {
		ExecutorService es = pool.get(key);
		if (es == null) {
			synchronized (this) {
				if (es == null) {
					// TODO thread parameter config
					ThreadPoolExecutor tpe = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("craft-atom-rpc"));
					tpe.allowCoreThreadTimeOut(true);
					es = tpe;
					pool.put(key, tpe);
				}
			}
		}
		return es;
	}

}

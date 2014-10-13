package org.craft.atom.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.craft.atom.rpc.spi.RpcApi;
import org.craft.atom.rpc.spi.RpcRegistry;

/**
 * RPC registry
 * 
 * @author mindwind
 * @version 1.0, Aug 12, 2014
 */
public class DefaultRpcRegistry implements RpcRegistry {
	
	
	private Map<String, RpcApi> registry = new ConcurrentHashMap<String, RpcApi>();
	

	@Override
	public void register(RpcApi api) {
		registry.put(api.getKey(), api);
	}

	@Override
	public void unregister(RpcApi api) {
		registry.remove(api.getKey());
	}

	@Override
	public RpcApi lookup(RpcApi api) {
		return registry.get(api.getKey());
	}
	
}

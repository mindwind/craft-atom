package io.craft.atom.rpc;

import io.craft.atom.rpc.spi.RpcApi;
import io.craft.atom.rpc.spi.RpcRegistry;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;


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

	@Override
	public Set<RpcApi> apis() {
		Set<RpcApi> apis = new TreeSet<RpcApi>(registry.values());
		return Collections.unmodifiableSet(apis);
	}
	
}

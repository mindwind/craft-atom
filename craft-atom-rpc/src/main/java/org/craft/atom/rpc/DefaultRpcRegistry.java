package org.craft.atom.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.craft.atom.rpc.spi.RpcEntry;
import org.craft.atom.rpc.spi.RpcRegistry;

/**
 * RPC registry
 * 
 * @author mindwind
 * @version 1.0, Aug 12, 2014
 */
public class DefaultRpcRegistry implements RpcRegistry {
	
	
	private Map<String, RpcEntry> registry = new ConcurrentHashMap<String, RpcEntry>();
	

	@Override
	public void register(RpcEntry entry) {
		registry.put(entry.getKey(), entry);
	}

	@Override
	public void unregister(RpcEntry entry) {
		registry.remove(entry.getKey());
	}

	@Override
	public RpcEntry lookup(RpcEntry entry) {
		return registry.get(entry.getKey());
	}
	
}

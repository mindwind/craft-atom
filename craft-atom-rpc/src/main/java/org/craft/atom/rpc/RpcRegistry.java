package org.craft.atom.rpc;

import java.util.HashMap;
import java.util.Map;

import org.craft.atom.protocol.rpc.model.RpcMethod;

/**
 * RPC registry
 * 
 * @author mindwind
 * @version 1.0, Aug 12, 2014
 */
public class RpcRegistry {
	
	
	// singleton
	private static final RpcRegistry INSTANCE = new RpcRegistry();
	public static RpcRegistry getInstance() { return INSTANCE; }
	
	
	private Map<String, RpcEntry> registry = new HashMap<String, RpcEntry>();
	
	
	public void register(String key, RpcEntry entry) {
		registry.put(key, entry);
	}
	
	public RpcEntry lookup(String key) {
		return registry.get(key);
	}
	
	public String key(String rpcId, Class<?> rpcInterface, RpcMethod rpcMethod) {
		if (rpcId == null) {
			return Integer.toString(rpcInterface.hashCode()) + Integer.toString(rpcMethod.hashCode());
		} else {
			return rpcId + "-" + Integer.toString(rpcInterface.hashCode()) + Integer.toString(rpcMethod.hashCode());
		}
	}
	
}

package org.craft.atom.protocol.rpc;

import java.util.HashMap;
import java.util.Map;

import org.craft.atom.protocol.rpc.model.RpcBody;
import org.craft.atom.protocol.rpc.spi.Serialization;


/**
 * Serialization registry contains the {@code Serialization} and its type mapping.
 * 
 * @author mindwind
 * @version 1.0, Jul 23, 2014
 */
public class SerializationRegistry {
	
	
	// singleton
	private static final SerializationRegistry INSTNACE = new SerializationRegistry();
	public static SerializationRegistry getInstance() { return INSTNACE; } 
	private SerializationRegistry() {
		registry.put(KryoSerialization.getInstance().type(), KryoSerialization.getInstance());
	}
	
	
	private Map<Byte, Serialization<RpcBody>> registry = new HashMap<Byte, Serialization<RpcBody>>();
	
	
	// ~ --------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * Lookup by type 
	 * 
	 * @param type
	 * @return mapping serialization.
	 */
	public Serialization<RpcBody> lookup(byte type) {
		return registry.get(type);
	}
	
	
	/**
	 * Register a serialization.
	 * 
	 * @param type
	 * @param serialization
	 */
	public void register(byte type, Serialization<RpcBody> serialization) {
		if (registry.containsKey(type)) {
			throw new IllegalArgumentException("Serialization `type` is conflict!");
		}
		registry.put(type, serialization);
	}
	
	/**
	 * Unregister a serialization.
	 * 
	 * @param type
	 */
	public void unregister(byte type) {
		registry.remove(type);
	}
}

package org.craft.atom.protocol.rpc;

import org.craft.atom.protocol.rpc.model.RpcBody;
import org.craft.atom.protocol.rpc.spi.Serialization;
import org.craft.atom.protocol.rpc.spi.SerializationFactory;

/**
 * @author mindwind
 * @version 1.0, Jul 23, 2014
 */
public class KryoSerializationFactory implements SerializationFactory<RpcBody> {
	
	
	// singleton
	private static final KryoSerializationFactory INSTNACE = new KryoSerializationFactory();
	private KryoSerializationFactory() {}
	public static KryoSerializationFactory getInstance() { return INSTNACE; } 
	
	
	// thread local cache
    private static final ThreadLocal<KryoSerialization> CACHE = new ThreadLocal<KryoSerialization>() {
    	@Override
    	protected KryoSerialization initialValue() {
            return new KryoSerialization();
        }
    };
	

	@Override
	public Serialization<RpcBody> newSerialization() {
		return CACHE.get();
	}
	
}

package org.craft.atom.protocol.rpc;

import org.craft.atom.protocol.rpc.model.RpcBody;
import org.craft.atom.protocol.rpc.spi.Serialization;
import org.craft.atom.protocol.rpc.spi.SerializationFactory;

/**
 * @author mindwind
 * @version 1.0, Jul 23, 2014
 */
public class KryoSerializationFactory implements SerializationFactory<RpcBody> {
	
	
	private static final KryoSerializationFactory INSTNACE = new KryoSerializationFactory();
	private KryoSerializationFactory() {}
	public static KryoSerializationFactory getInstance() { return INSTNACE; } 
    

	@Override
	public Serialization<RpcBody> newSerialization() {
		return new KryoSerialization();
	}
	
}

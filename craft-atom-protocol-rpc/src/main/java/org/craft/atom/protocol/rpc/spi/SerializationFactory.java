package org.craft.atom.protocol.rpc.spi;

/**
 * An object that creates new {code Serialization} on demand.
 * 
 * @author mindwind
 * @version 1.0, Jul 23, 2014
 */
public interface SerializationFactory<T> {

	/**
	 * Constructs a new {@code Serialization}
	 * 
	 * @return constructed serialization
	 */
	Serialization<T> newSerialization();
	
}

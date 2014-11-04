package io.craft.atom.protocol.rpc.spi;

import io.craft.atom.protocol.ProtocolException;

/**
 * Serialize object to bytes or deserialize object from bytes.
 * 
 * @author mindwind
 * @version 1.0, Jul 23, 2014
 */
public interface Serialization<T> {
	
	/**
	 * @return serialization type.
	 */
	byte type();
	
	/**
	 * Serialize object to bytes.
	 * 
	 * @param object
	 * @return serialized bytes
	 */
	byte[] serialize(T object) throws ProtocolException;
	
	/**
	 * Deserialize object from bytes.
	 * 
	 * @param bytes byte array
	 * @return deserialized object.
	 */
	T deserialize(byte[] bytes) throws ProtocolException;
	
	/**
	 * Deserialize object from bytes at specific offset .
	 * 
	 * @param bytes byte array
	 * @param off   offset
	 * @return deserialized object.
	 */
	T deserialize(byte[] bytes, int off) throws ProtocolException;
	
}

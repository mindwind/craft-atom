package org.craft.atom.protocol.rpc.spi;

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
	byte[] serialize(T object);
	
	/**
	 * Deserialize object from bytes.
	 * 
	 * @param bytes byte array
	 * @return deserialized object.
	 */
	T deserialize(byte[] bytes);
	
	/**
	 * Deserialize object from bytes at specific offset .
	 * 
	 * @param bytes byte array
	 * @param off   offset
	 * @return deserialized object.
	 */
	T deserialize(byte[] bytes, int off);
	
}

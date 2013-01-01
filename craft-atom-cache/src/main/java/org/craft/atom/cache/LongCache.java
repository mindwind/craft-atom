package org.craft.atom.cache;

/**
 * @author Hu Feng
 * @version 1.0, Sep 29, 2012
 */
public interface LongCache extends NumberCache {
	
	/**
	 * Decrements the number stored at key by one. If the key does not exist, it is set to 0 before performing the operation. 
	 * An error is returned if the key contains a value of the wrong type or contains a string that can not be represented as integer. 
	 * This operation is limited to 64 bit signed integers.
	 * 
	 * @param key
	 * @return the value of key after the decrement
	 */
	Long decr(String key);
	
	/**
	 * Decrements the number stored at key by decrement. If the key does not exist, it is set to 0 before performing the operation. 
	 * An error is returned if the key contains a value of the wrong type or contains a string that can not be represented as integer. 
	 * This operation is limited to 64 bit signed integers.
	 * 
	 * @param key
	 * @param num
	 * @return the value of key after the decrement
	 */
	Long decrBy(String key, long num);
	
	/**
	 * increments the number stored at key by one. If the key does not exist, it is set to 0 before performing the operation. 
	 * An error is returned if the key contains a value of the wrong type or contains a string that can not be represented as integer. 
	 * This operation is limited to 64 bit signed integers.
	 * <p>
	 * Note: this is a string operation because cache does not have a dedicated integer type. 
	 * The string stored at the key is interpreted as a base-10 64 bit signed integer to execute the operation.
	 * cache stores integers in their integer representation, so for string values that actually hold an integer, 
	 * there is no overhead for storing the string representation of the integer.
	 * 
	 * @param key
	 * @return the value of key after the increment
	 */
	Long incr(String key);
	
	/**
	 * Increments the number stored at key by increment. If the key does not exist, it is set to 0 before performing the operation. 
	 * An error is returned if the key contains a value of the wrong type or contains a string that can not be represented as integer. 
	 * This operation is limited to 64 bit signed integers.
	 * 
	 * @param key
	 * @param num
	 * @return the value of key after the increment
	 */
	Long incrBy(String key, long num);
	
}

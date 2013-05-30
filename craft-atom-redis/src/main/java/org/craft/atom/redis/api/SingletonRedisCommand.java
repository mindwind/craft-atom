package org.craft.atom.redis.api;

import java.util.List;
import java.util.Map;

/**
 * The atomic commands supported by singleton Redis.
 * 
 * @author mindwind
 * @version 1.0, May 4, 2013
 */
public interface SingletonRedisCommand extends RedisCommand {
	
	// ~ --------------------------------------------------------------------------------------------------------- Keys
	
	
	/**
	 * @see {@link #del(String)}
	 * @param keys
	 * @return
	 */
	long del(String... keys);
	long del(byte[]... keys);
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Strings
	
	
	/**
	 * @see {@link #bitnot(String, String)}
	 * @param destKey
	 * @param keys
	 * @return
	 */
	long bitand(String destKey, String... keys);
	long bitand(byte[] destKey, byte[]... keys);
	long bitor(String destKey, String... keys);
	long bitor(byte[] destKey, byte[]... keys);
	long bitxor(String destKey, String... keys);
	long bitxor(byte[] destKey, byte[]... keys);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(N) where N is the number of keys to retrieve.
	 * 
	 * <p>
	 * Returns the values of all specified keys. For every key that does not hold a string value or does not exist, 
	 * the special value null is returned. Because of this, the operation never fails.
	 * 
	 * @param keys
	 * @return
	 */
	List<String> mget(String... keys);
	List<byte[]> mget(byte[]... keys);
	
	/**
	 * Available since 1.0.1
	 * Time complexity: O(N) where N is the number of keys to set.
	 * 
	 * <p>
	 * Sets the given keys to their respective values. MSET replaces existing values with new values, just as regular SET. 
	 * See MSETNX if you don't want to overwrite existing values.
	 * MSET is atomic, so all given keys are set at once. It is not possible for clients to see that some of the keys were updated while others are unchanged.
	 * 
	 * @param keysvalues
	 * @return
	 */
	String mset(String... keysvalues);
	byte[] mset(byte[]... keysvalues);
	
	/**
	 * Available since 1.0.1.
	 * Time complexity: O(N) where N is the number of keys to set.
	 * 
	 * <p>
	 * Sets the given keys to their respective values. MSETNX will not perform any operation at all even if just a single key already exists.
	 * Because of this semantic MSETNX can be used in order to set different keys representing different fields of an unique logic object 
	 * in a way that ensures that either all the fields or none at all are set.
	 * MSETNX is atomic, so all given keys are set at once. It is not possible for clients to see that some of the keys were updated while others are unchanged.
	 * 
	 * @param keysvalues
	 * @return
	 */
	String msetnx(String... keysvalues);
	byte[] msetnx(byte[]... keysvalues);
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Lists
	
	/**
	 * @see {@link RedisCommand#blpop(String)}
	 * @param timeout
	 * @param keys
	 * @return A empty map(nil multi-bulk) when no element could be popped and the timeout expired.
	 *         A map (two-element multi-bulk) with the key (first element) being the name of the key where an element was popped 
	 *         and the value (second element) being the value of the popped element.
	 */
	Map<String, String> blpop(String... keys);
	Map<byte[], byte[]> blpop(byte[]... keys);
	Map<String, String> blpop(int timeout, String... keys);
	Map<byte[], byte[]> blpop(int timeout, byte[]... keys);
	
	/**
	 * @see {@link RedisCommand#brpop(String)}
	 * @param timeout
	 * @param keys
	 * @return A empty map(nil multi-bulk) when no element could be popped and the timeout expired.
	 *         A map (two-element multi-bulk) with the key (first element) being the name of the key where an element was popped 
	 *         and the value (second element) being the value of the popped element.
	 */
	Map<String, String> brpop(String... keys);
	Map<byte[], byte[]> brpop(byte[]... keys);
	Map<String, String> brpop(int timeout, String... keys);
	Map<byte[], byte[]> brpop(int timeout, byte[]... keys);
}

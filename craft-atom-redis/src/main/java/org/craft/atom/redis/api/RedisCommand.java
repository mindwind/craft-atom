package org.craft.atom.redis.api;

/**
 * The basic atomic commands supported by Redis.
 * 
 * @author mindwind
 * @version 1.0, May 3, 2013
 */
public interface RedisCommand {

	// ~ --------------------------------------------------------------------------------------------------------- Keys

	/**
	 * Available since 1.0.0
	 * <p>
	 * 
	 * Time complexity:<br>
	 * O(N) where N is the number of keys that will be removed. 
	 * When a key toremove holds a value other than a string, 
	 * the individual complexity for this key is O(M) 
	 * where M is the number of elements in the list, set, sorted set or hash.
	 * Removing a single key that holds a string value is O(1).
	 * <p>
	 * 
	 * Removes the specified keys. A key is ignored if it does not exist.
	 * <p>
	 * 
	 * @param key
	 * @return The number of keys that were removed.
	 */
	long del(String key);
	long del(byte[] key);
	
	/**
	 * Available since 2.6.0
	 * <p>
	 * 
	 * Time complexity: <br>
	 * O(1) to access the key and additional O(N*M) to serialized it, 
	 * where N is the number of Redis objects composing the value and M their average size. 
	 * For small string values the time complexity is thus O(1)+O(1*M) where M is small, so simply O(1).
	 * <p>
	 * Serialize the value stored at key in a Redis-specific format and return it to the user. 
	 * The returned value can be synthesized back into a Redis key using the RESTORE command.
	 * <p>
	 * The serialization format is opaque and non-standard, however it has a few semantical characteristics:
	 * - It contains a 64-bit checksum that is used to make sure errors will be detected.
	 *   The RESTORE command makes sure to check the checksum before synthesizing a key using the serialized value.
	 * - Values are encoded in the same format used by RDB.
     * - An RDB version is encoded inside the serialized value, 
     *   so that different Redis versions with incompatible RDB formats will refuse to process the serialized value.
     * <p>
     * The serialized value does NOT contain expire information. 
     * In order to capture the time to live of the current value the PTTL command should be used.
     * <p>
     * If key does not exist a nil bulk reply is returned.
	 * 
	 * @param key
	 * @return the serialized value
	 */
	String dump(String key);
	String dump(byte[] key);
	
	/**
	 * Available since 1.0.0
	 * <p>
	 * Time complexity: O(1)<br>
	 * <p>
	 * Returns <tt>true</tt>if key exists.
	 * 
	 * @param key
	 * @return
	 */
	boolean exists(String key);
	boolean exists(byte[] key);
	
	/**
	 * Available since 1.0.0
	 * <p>
	 * Time complexity: O(1)<br>
	 * <p>
	 * Set a timeout on key. After the timeout has expired, the key will automatically be deleted. 
	 * A key with an associated timeout is often said to be volatile in Redis terminology.
	 * <p>
	 * The timeout is cleared only when the key is removed using the DEL command or 
	 * overwritten using the SET or GETSET commands. 
	 * This means that all the operations that conceptually alter the value stored at the key 
	 * without replacing it with a new one will leave the timeout untouched. 
	 * For instance, incrementing the value of a key with INCR, pushing a new value into a list with LPUSH, 
	 * or altering the field value of a hash with HSET are all operations that will leave the timeout untouched.
	 * <p>
	 * The timeout can also be cleared, turning the key back into a persistent key, using the PERSIST command.
	 * If a key is renamed with RENAME, the associated time to live is transferred to the new key name.
	 * If a key is overwritten by RENAME, like in the case of an existing key Key_A that is overwritten 
	 * by a call like RENAME Key_B Key_A, it does not matter if the original Key_A had a timeout associated or not, 
	 * the new key Key_A will inherit all the characteristics of Key_B.
	 * <p>
	 * It is possible to call EXPIRE using as argument a key that already has an existing expire set. 
	 * In this case the time to live of a key is updated to the new value. There are many useful applications for this.
	 * 
	 * @param key
	 * @param seconds
	 * @return 1 if the timeout was set.
     *         0 if key does not exist or the timeout could not be set.
	 */
	long expire(String key, int seconds);
	long expire(byte[] key, int seconds);
	
	
}

package org.craft.atom.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hu Feng
 * @version 1.0, Sep 8, 2012
 */
public interface HashCache extends Cache {

	/**
	 * Returns the value associated with field in the hash stored at key.
	 * 
	 * @param key
	 * @param field
	 * @return the value associated with field, or <tt>null</tt> when field is
	 *         not present in the hash or key does not exist.
	 */
	String hget(String key, String field);

	/**
	 * Sets field in the hash stored at key to value. If key does not exist, a
	 * new key holding a hash is created. If field already exists in the hash,
	 * it is overwritten.
	 * 
	 * @param key
	 * @param field
	 * @return 1 if field is a new field in the hash and value was set. <br>
	 *         0 if field already exists in the hash and the value was updated.
	 */
	Long hset(String key, String field, String value);
	
	/**
	 * Sets field in the hash stored at key to value, only if field does not yet exist. If key does not exist, 
	 * a new key holding a hash is created. If field already exists, this operation has no effect.
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	Long hsetnx(String key, String field, String value);

	/**
	 * Time complexity: O(N) where N is the number of fields to be removed.
	 * <p>
	 * Removes the specified fields from the hash stored at key. Specified
	 * fields that do not exist within this hash are ignored. If key does not
	 * exist, it is treated as an empty hash and this command returns 0.
	 * <p>
	 * 
	 * <b>NOTE:</b><br>
	 * Transaction unsupported now!
	 * 
	 * @param key
	 * @param fields
	 * @return the number of fields that were removed from the hash, not
	 *         including specified but non existing fields.
	 */
	Long hdel(String key, String... fields);
	
	/**
	 * Time complexity: O(N) where N is the number of fields being requested.
	 * <p>
	 * Returns the values associated with the specified fields in the hash stored at key.<br>
	 * For every field that does not exist in the hash, a <tt>null</tt> value is returned.
	 * 
	 * @param key
	 * @param fields
	 * @return list of values associated with the given fields, in the same order as they are requested.
	 */
	List<String> hmget(String key, String... fields);
	
	/**
	 * Time complexity: O(N) where N is the number of fields being set.
	 * <p>
	 * 
	 * Sets the specified fields to their respective values in the hash stored at key. This command overwrites any 
	 * existing fields in the hash. If key does not exist, a new key holding a hash is created.
	 * 
	 * @param key
	 * @param hash
	 */
	void hmset(String key, Map<String, String> hash);
	
	/**
	 * Increments the number stored at field in the hash stored at key by increment. 
	 * If key does not exist, a new key holding a hash is created. If field does not exist the value is set to 0 before the operation is performed.
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return the value at field after the increment operation.
	 */
	Long hincrBy(String key, String field, long value);
	
	/**
	 * Returns if field is an existing field in the hash stored at key.
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	Boolean hexists(String key, String field);
	
	/**
	 * Time complexity: O(N) where N is the size of the hash.
	 * <p>
	 * Returns all fields and values of the hash stored at key.
	 * 
	 * @param key
	 * @return
	 */
	Map<String, String> hgetAll(String key);
	
	/**
	 * Time complexity: O(N) where N is the size of the hash.
	 * <p>
	 * Returns all field names in the hash stored at key.
	 * 
	 * @param key
	 * @return
	 */
	Set<String> hkeys(String key);
	
	/**
	 * Returns the number of fields contained in the hash stored at key.
	 * 
	 * @param key
	 * @return
	 */
	Long hlen(String key);
	
	/**
	 * Time complexity: O(N) where N is the size of the hash.
	 * <p>
	 * Returns all values in the hash stored at key.
	 * 
	 * @param key
	 * @return
	 */
	List<String> hvals(String key);

}

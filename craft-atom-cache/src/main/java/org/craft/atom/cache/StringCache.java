package org.craft.atom.cache;

import java.util.List;

/**
 * @author Hu Feng
 * @version 1.0, Sep 8, 2012
 */
public interface StringCache extends Cache {
	
	/**
	 * Atomically sets key to value and returns the old value stored at key.
	 * Returns an error when key exists but does not hold a string value.
	 * 
	 * @param key
	 * @param value
	 * @return the old value stored at key, or <tt>null</tt> when key did not
	 *         exist.
	 */
	String getset(String key, String value);

	/**
	 * Get the value of key. If the key does not exist <tt>null</tt> is
	 * returned. An error is returned if the value stored at key is not a
	 * string, because get only handles string values.
	 * 
	 * @param key
	 * @return
	 */
	String get(String key);

	/**
	 * Set key to hold the string value. If key already holds a value, it is
	 * overwritten, regardless of its type
	 * 
	 * @param key
	 * @param value
	 */
	void set(String key, String value);

	/**
	 * Set key to hold the string value and set key to timeout after a given
	 * number of seconds. This command is equivalent to executing the following
	 * commands: 
	 * <pre>
	 * {@link #set(k, v)}
	 * {@link #expire(k, ttl)}
	 * </pre>
	 * 
	 * @param key
	 * @param ttl a positive number, don't accept the negative number or zero .
	 * @param value
	 */
	void setex(String key, int ttl, String value);
	
	/**
	 * Set key to hold string value if key does not exist. In that case, it is equal to {@link #set(String, String)}. 
	 * When key already holds a value, no operation is performed. SETNX is short for "SET if Not eXists".
	 * 
	 * @param key
	 * @param value
	 * @return 1 if the key was set, 0 if the key was not set
	 */
	Long setnx(String key, String value);
	
	/**
	 * Time complexity: O(N) where N is the number of keys to retrieve.
	 * <p>
	 * Returns the values of all specified keys. For every key that does not hold a string value or does not exist, 
	 * <tt>null</tt> returned. 
	 * <br>
	 * <b>NOTE:</b><br>
	 * This is not atomic operation in a sharded cache deployment environment.<br>
	 * It's not supported transaction in a sharded cache deployment environment.<br>
	 * 
	 * @param keys
	 * @return list of values at the specified keys.
	 */
	List<String> mget(String... keys);
	
	/**
	 * Time complexity: O(N) where N is the number of keys to set.
	 * <p>
	 * Sets the given keys to their respective values. <code>mset()</code> replaces existing values with new values, 
	 * just as regular {@link #set(String, String)}
	 * <br>
	 * <b>NOTE:</b><br>
	 * This is not atomic operation in a sharded cache deployment environment.<br>
	 * It's not supported transaction in a sharded cache deployment environment.<br>
	 * 
	 * @param keysvalues
	 * @return successfully set keys.
	 */
	List<String> mset(String... keysvalues);
	
	/**
	 * Time complexity: O(N) where N is the number of keys to set.
	 * <p>
	 * Sets the given keys to their respective values. <code>msetnx()</code> will not perform any operation at all 
	 * even if just a single key already exists.
	 * <br>
	 * <b>NOTE:</b><br>
	 * It's not supported under a sharded cache deployment environment.<br>
	 * 
	 * @param keysvalues
	 * @return 1 if the all the keys were set.<br>
	 *         0 if no key was set (at least one key already existed).
	 */
	Long msetnx(String... keysvalues);
	
}

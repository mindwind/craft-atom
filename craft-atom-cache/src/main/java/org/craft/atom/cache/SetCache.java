package org.craft.atom.cache;

import java.util.Set;

/**
 * @author Hu Feng
 * @version 1.0, Sep 8, 2012
 */
public interface SetCache extends Cache {

	/**
	 * Time complexity: O(N) where N is the number of members to be added.
	 * <p>
	 * Add the specified members to the set stored at key. Specified members
	 * that are already a member of this set are ignored. If key does not exist,
	 * a new set is created before adding the specified members. An error is
	 * returned when the value stored at key is not a set.
	 * <p>
	 * <b>NOTE:</b><br>
	 * Transaction unsupported now!
	 * 
	 * @param key
	 * @param values
	 * @return the number of elements that were added to the set, not including
	 *         all the elements already present into the set.
	 */
	Long sadd(String key, String... values);
	
	/**
	 * Returns the set cardinality (number of elements) of the set stored at key.
	 * 
	 * @param key
	 * @return the cardinality (number of elements) of the set, or 0 if key does not exist.
	 */
	Long scard(String key);

	/**
	 * Time complexity: O(N) where N is the number of members to be removed.
	 * <p>
	 * Remove the specified members from the set stored at key. Specified
	 * members that are not a member of this set are ignored. If key does not
	 * exist, it is treated as an empty set and this command returns 0. An error
	 * is returned when the value stored at key is not a set.
	 * <p>
	 * <b>NOTE:</b><br>
	 * Transaction unsupported now!
	 * 
	 * @param key
	 * @param values
	 * @return the number of members that were removed from the set, not
	 *         including non existing members.
	 */
	Long srem(String key, String... values);
	
	/**
	 * Returns if member is a member of the set stored at key.
	 * 
	 * @param key
	 * @param member
	 * @return true  - if the element is a member of the set.<br>
	 *         false - if the element is not a member of the set, or if key does not exist.
	 */
	Boolean sismember(String key, String member);

	/**
	 * Time complexity: O(N) where N is the set cardinality.
	 * <p>
	 * Returns all the members of the set value stored at key. This has the same
	 * effect as running {@link #sinter(keys)} with one argument key.
	 * 
	 * @param key
	 * @return all elements of the set.
	 */
	Set<String> smembers(String key);

	/**
	 * Time complexity: O(N*M) worst case where N is the cardinality of the smallest set and M is the number of sets.
	 * <p>
	 * 
	 * Returns the members of the set resulting from the intersection of all the
	 * given sets.<br>
	 * For example:
	 * 
	 * <pre>
	 * key1 = {a,b,c,d} 
	 * key2 = {c} 
	 * key3 = {a,c,e}
	 * sinter key1 key2 key3 = {c}
	 * </pre>
	 * 
	 * Keys that do not exist are considered to be empty sets. With one of the
	 * keys being an empty set, the resulting set is also empty (since set
	 * intersection with an empty set always results in an empty set).
	 * 
	 * <p>
	 * <b>NOTE: </b><br>
	 * it's not supported in a sharded cache deployment.
	 * 
	 * @param keys
	 * @return list with members of the resulting set.
	 */
	Set<String> sinter(String... keys);
	
	/**
	 * Removes and returns a random element from the set value stored at key.
	 * 
	 * @param key
	 * @return the removed element, or <tt>null</tt> when key does not exist.
	 */
	String spop(String key);

}

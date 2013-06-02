package org.craft.atom.redis.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
	long bitand(String destkey, String... keys);
	long bitand(byte[] destkey, byte[]... keys);
	long bitor(String destkey, String... keys);
	long bitor(byte[] destkey, byte[]... keys);
	long bitxor(String destkey, String... keys);
	long bitxor(byte[] destkey, byte[]... keys);
	
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
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Sets
	
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(N) where N is the total number of elements in all given sets.
	 * 
	 * <p>
	 * Returns the members of the set resulting from the difference between the first set and all the successive sets.
	 * For example:
	 * <pre>
	 * key1 = {a,b,c,d}
	 * key2 = {c}
	 * key3 = {a,c,e}
	 * SDIFF key1 key2 key3 = {b,d}
	 * </pre>
	 * Keys that do not exist are considered to be empty sets.
	 * 
	 * @param keys
	 * @return list with members of the resulting set.
	 */
	Set<String> sdiff(String... keys);
	Set<byte[]> sdiff(byte[]... keys);
	
	/**
	 * Available since 1.0.0.
	 * Time complexity: O(N) where N is the total number of elements in all given sets.
	 * 
	 * <p>
	 * This command is equal to SDIFF, but instead of returning the resulting set, it is stored in destination.
	 * If destination already exists, it is overwritten.
	 * 
	 * @param destination
	 * @param keys
	 * @return
	 */
	long sdiffstore(String destination, String... keys);
	long sdiffstore(byte[] destination, byte[]... keys);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(N*M) worst case where N is the cardinality of the smallest set and M is the number of sets.
	 * 
	 * <p>
	 * Returns the members of the set resulting from the intersection of all the given sets.
	 * For example:
	 * <pre>
	 * key1 = {a,b,c,d}
	 * key2 = {c}
	 * key3 = {a,c,e}
	 * SINTER key1 key2 key3 = {c}
	 * </pre>
	 * Keys that do not exist are considered to be empty sets. With one of the keys being an empty set, 
	 * the resulting set is also empty (since set intersection with an empty set always results in an empty set).
	 * 
	 * @param keys
	 * @return list with members of the resulting set.
	 */
	Set<String> sinter(String... keys);
	Set<byte[]> sinter(byte[]... keys);
	
	/**
	 * Available since 1.0.0.
	 * Time complexity: O(N*M) worst case where N is the cardinality of the smallest set and M is the number of sets.
	 * 
	 * <p>
	 * This command is equal to SINTER, but instead of returning the resulting set, it is stored in destination.
	 * If destination already exists, it is overwritten.
	 * 
	 * @param destination
	 * @param keys
	 * @return the number of elements in the resulting set.
	 */
	long sinterstore(String destination, String... keys);
	long sinterstore(byte[] destination, byte[]... keys);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Move member from the set at source to the set at destination. This operation is atomic. 
	 * In every given moment the element will appear to be a member of source or destination for other clients.
	 * If the source set does not exist or does not contain the specified element, no operation is performed and 0 is returned. 
	 * Otherwise, the element is removed from the source set and added to the destination set. 
	 * When the specified element already exists in the destination set, it is only removed from the source set.
	 * An error is returned if source or destination does not hold a set value.
	 * 
	 * @param source
	 * @param destination
	 * @param member
	 * @return 1 if the element is moved.
	 *         0 if the element is not a member of source and no operation was performed.
	 */
	long smove(String source, String destination, String member);
	long smove(byte[] source, byte[] destination, byte[] member);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(N) where N is the total number of elements in all given sets.
	 * 
	 * <p>
	 * Returns the members of the set resulting from the union of all the given sets.
	 * For example:
	 * <pre>
	 * key1 = {a,b,c,d}
	 * key2 = {c}
	 * key3 = {a,c,e}
	 * SUNION key1 key2 key3 = {a,b,c,d,e}
	 * </pre>
	 * Keys that do not exist are considered to be empty sets.
	 * 
	 * @param keys
	 * @return list with members of the resulting set.
	 */
	Set<String> sunion(String... keys);
	Set<byte[]> sunion(byte[]... keys);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(N) where N is the total number of elements in all given sets.
	 * 
	 * <p>
	 * This command is equal to SUNION, but instead of returning the resulting set, it is stored in destination.
	 * If destination already exists, it is overwritten.
	 * 
	 * @param destination
	 * @param keys
	 * @return the number of elements in the resulting set
	 */
	Set<String> sunionstore(String destination, String... keys);
	Set<byte[]> sunionstore(byte[] destination, byte[]... keys);
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(N*K)+O(M*log(M)) worst case with N being the smallest input sorted set, 
	 * K being the number of input sorted sets and M being the number of elements in the resulting sorted set.
	 * 
	 * <p>
	 * Computes the intersection of sorted sets given by the specified keys, and stores the result in destination. 
	 * By default, the resulting score of an element is the sum of its scores in the sorted sets where it exists. 
	 * Because intersection requires an element to be a member of every given sorted set, 
	 * this results in the score of every element in the resulting sorted set to be equal to the number of input sorted sets.
	 * For a description of the WEIGHTS and AGGREGATE options, see ZUNIONSTORE.
	 * If destination already exists, it is overwritten.
	 * 
	 * @param destination
	 * @param keys
	 * @return the number of elements in the resulting sorted set at destination.
	 */
	long zinterstore(String destination, String... keys);
	long zinterstore(byte[] destination, byte[]... keys);
	long zinterstoremax(String destination, String... keys);
	long zinterstoremax(byte[] destination, byte[]... keys);
	long zinterstoremin(String destination, String... keys);
	long zinterstoremin(byte[] destination, byte[]... keys);
	long zinterstore(String destination, Map<String, Integer> weightkeys);
	long zinterstore(byte[] destination, Map<String, Integer> weightkeys);
	long zinterstoremax(String destination, Map<String, Integer> weightkeys);
	long zinterstoremax(byte[] destination, Map<String, Integer> weightkeys);
	long zinterstoremin(String destination, Map<String, Integer> weightkeys);
	long zinterstoremin(byte[] destination, Map<String, Integer> weightkeys);
	
	/**
	 * Available since 2.0.0.
	 * Time complexity: O(N)+O(M log(M)) with N being the sum of the sizes of the input sorted sets, and M being the number of elements in the resulting sorted set.
	 * 
	 * <p>
	 * Computes the union of sorted sets given by the specified keys, and stores the result in destination. 
	 * By default, the resulting score of an element is the sum of its scores in the sorted sets where it exists.
	 * Using the WEIGHTS option, it is possible to specify a multiplication factor for each input sorted set. 
	 * This means that the score of every element in every input sorted set is multiplied by this factor before being 
	 * passed to the aggregation function. When WEIGHTS is not given, the multiplication factors default to 1.
	 * With the AGGREGATE option, it is possible to specify how the results of the union are aggregated. 
	 * This option defaults to SUM, where the score of an element is summed across the inputs where it exists. 
	 * When this option is set to either MIN or MAX, the resulting set will contain the minimum or maximum score of an 
	 * element across the inputs where it exists.
	 * If destination already exists, it is overwritten.
	 * 
	 * @param destination
	 * @param keys
	 * @return the number of elements in the resulting sorted set at destination.
	 */
	long zunionstore(String destination, String... keys);
	long zunionstore(byte[] destination, byte[]... keys);
	long zunionstoremax(String destination, String... keys);
	long zunionstoremax(byte[] destination, byte[]... keys);
	long zunionstoremin(String destination, String... keys);
	long zunionstoremin(byte[] destination, byte[]... keys);
	long zunionstore(String destination, Map<String, Integer> weightkeys);
	long zunionstore(byte[] destination, Map<String, Integer> weightkeys);
	long zunionstoremax(String destination, Map<String, Integer> weightkeys);
	long zunionstoremax(byte[] destination, Map<String, Integer> weightkeys);
	long zunionstoremin(String destination, Map<String, Integer> weightkeys);
	long zunionstoremin(byte[] destination, Map<String, Integer> weightkeys);
}

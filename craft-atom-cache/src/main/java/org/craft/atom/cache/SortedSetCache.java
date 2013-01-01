package org.craft.atom.cache;

import java.util.Map;
import java.util.Set;

/**
 * <code>SortedSetCache</code> are, similarly to <code>SetCache</code>, non repeating collections of Strings.
 * <br>
 * The difference is that every member of a <code>SortedSetCache</code> is associated with score, 
 * that is used in order to take the sorted set ordered, from the smallest to the greatest score. 
 * While members are unique, scores may be repeated.
 * <br>
 * With <code>SortedSetCache</code> you can add, remove, or update elements in a very fast way (in a time proportional to the logarithm of the number of elements).
 * Since elements are taken in order and not ordered afterwards, you can also get ranges by score or by rank (position) in a very fast way. 
 * Accessing the middle of a sorted set is also very fast, so you can use <code>SortedSetCache</code> as a smart list of non repeating elements 
 * where you can quickly access everything you need: elements in order, fast existence test, fast access to elements in the middle!
 * 
 * @author Hu Feng
 * @version 1.0, Oct 31, 2012
 */
public interface SortedSetCache extends Cache {
	
	/**
	 * Time complexity: O(log(N)) where N is the number of elements in the sorted set.
	 * <br>
	 * It is possible to specify multiple score/member pairs. If a specified member is already a member of the sorted set, 
	 * the score is updated and the element reinserted at the right position to ensure the correct ordering. 
	 * If key does not exist, a new sorted set with the specified members as sole members is created, like if the sorted set was empty. 
	 * If the key exists but does not hold a sorted set, an error is returned.The score values should be the string 
	 * representation of a numeric value, and accepts double precision floating point numbers.
	 * <p>
	 * <b>NOTE:</b><br>
	 * Transaction unsupported now!
	 * 
	 * @param key
	 * @param scoreMembers
	 * @return The number of elements added to the sorted sets, not including elements already existing for which the score was updated.
	 */
	Long zadd(String key, Map<Double, String> scoreMembers);
	
	/**
	 * @see #zadd(String, Map)
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	Long zadd(String key, double score, String member);
	
	/**
	 * Time complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements returned.
	 * <br>
	 * Returns the specified range of elements in the sorted set stored at key. The elements are considered to be ordered from the lowest to the highest score. 
	 * Lexicographical order is used for elements with equal score.
	 * <br>
	 * Both start and stop are zero-based indexes, where 0 is the first element, 1 is the next element and so on. 
	 * They can also be negative numbers indicating offsets from the end of the sorted set, with -1 being the last element of the sorted set, 
	 * -2 the penultimate element and so on.Out of range indexes will not produce an error. 
	 * If start is larger than the largest index in the sorted set, or start > stop, an empty list is returned. 
	 * If stop is larger than the end of the sorted set which will be treated it like it is the last element of the sorted set.
	 * <p>
	 * <b>TIP:</b><br>
	 * start and end should not overflow integer scope in transaction contex.
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return list of elements in the specified range or <tt>null</tt> if has no element in the range.
	 */
	Set<String> zrange(String key, long start, long end);
	
	/**
	 * Time complexity: O(M*log(N)) with N being the number of elements in the sorted set and M the number of elements to be removed.
	 * <br>
	 * Removes the specified members from the sorted set stored at key. Non existing members are ignored.<br>
	 * An error is returned when key exists and does not hold a sorted set.
	 * 
	 * @param key
	 * @param members
	 * @return The number of members removed from the sorted set, not including non existing members.
	 */
	Long zrem(String key, String... members);
	
	/**
	 * Returns the sorted set cardinality (number of elements) of the sorted set stored at key.
	 * 
	 * @param key
	 * @return he cardinality (number of elements) of the sorted set, or 0 if key does not exist.
	 */
	Long zcard(String key);
}

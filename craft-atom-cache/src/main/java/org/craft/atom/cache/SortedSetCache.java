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
	 * start and end should not overflow integer scope in transaction context.
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return list of elements in the specified range or empty set if has no element in the range.
	 */
	Set<String> zrange(String key, long start, long end);
	
	/**
	 * Time complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements returned.
	 * <br>
	 * 
	 * <b>TIP:</b><br>
	 * start and end should not overflow integer scope in transaction context.
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return map object, the key is element and value is the score, empty map if has no element in the range. 
	 * @return map object, the key is element and value is the score, empty map if has no element int the range. 
	 * @see #zrange(String, long, long)
	 */
	Map<String, Double> zrangeWithScores(String key, long start, long end);
	
	/**
	 * @see #zrangeByScore(String, String, String, int, int)
	 * @param key
	 * @param min
	 * @param max
	 * @return set of elements in the specified score range
	 */
	Set<String> zrangeByScore(String key, double min, double max);
	
	/**
	 * <b>NOTE:</b><br>
	 * Transaction not supported now!
	 * 
	 * @see #zrangeByScore(String, String, String, int, int)
	 * @param key
	 * @param min
	 * @param max
	 * @param offset inclusive
	 * @param count
	 * @return set of elements in the specified score range
	 */
	Set<String> zrangeByScore(String key, double min, double max, int offset, int count);
	
	/**
	 * @see #zrangeByScore(String, String, String, int, int)
	 * @param key
	 * @param min
	 * @param max
	 * @return set of elements in the specified score range
	 */
	Set<String> zrangeByScore(String key, String min, String max);
	
	/**
	 * Time complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements being returned. 
	 * If M is constant (e.g. always asking for the first 10 elements with LIMIT), you can consider it O(log(N)).
	 * <br>
	 * Returns all the elements in the sorted set at key with a score between min and max (including elements with score equal to min or max). 
	 * The elements are considered to be ordered from low to high scores.The elements having the same score are returned in lexicographical order.
	 * <br>
	 * min and max can be -inf and +inf, so that you are not required to know the highest or lowest score in the sorted set to get all elements from or up to a certain score.
	 * By default, the interval specified by min and max is closed (inclusive). 
	 * It is possible to specify an open interval (exclusive) by prefixing the score with the character '('
	 * for example:
	 * {@link #zrangeByScore("test", "(5", "(10")}
	 * Will return all the elements with 5 < score < 10 (5 and 10 excluded).
	 * <br>
	 * The optional LIMIT argument can be used to only get a range of the matching elements (similar to SELECT LIMIT offset, count in SQL). 
	 * Keep in mind that if offset is large, the sorted set needs to be traversed for offset elements before getting to the elements to return, which can add up to O(N) time complexity.
	 * <br>
	 * <b>NOTE:</b><br>
	 * Transaction not supported now!
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @param offset inclusive
	 * @param count
	 * @return set of elements in the specified score range
	 */
	Set<String> zrangeByScore(String key, String min, String max, int offset, int count);
	
	/**
	 * @see #zrangeByScore(String, String, String, int, int)
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return map object, the key is element and value is the score, empty map if has no element in the range. 
	 */
	Map<String, Double> zrangeByScoreWithScores(String key, String min, String max, int offset, int count);
	
	/**
	 * <b>NOTE:</b><br>
	 * Transaction not supported now!
	 * 
	 * @see #zrangeByScore(String, String, String, int, int)
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return map object, the key is element and value is the score, empty map if has no element in the range. 
	 */
	Map<String, Double> zrangeByScoreWithScores(String key, String min, String max);
	
	/**
	 * @see #zrangeByScore(String, String, String, int, int)
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return map object, the key is element and value is the score, empty map if has no element in the range. 
	 */
	Map<String, Double> zrangeByScoreWithScores(String key, double min, double max);
	
	/**
	 * @see #zrangeByScore(String, String, String, int, int)
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return map object, the key is element and value is the score, empty map if has no element in the range. 
	 */
	Map<String, Double> zrangeByScoreWithScores(String key, double min, double max, int offset, int count);
	
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
	
	/**
	 * Time complexity: O(log(N)+M) with N being the number of elements in the sorted set and M being the number of elements between min and max.
	 * <br>
	 * The min and max arguments have the same semantic as described for {@link #zcount(String, String, String)}
	 * 
	 * @param key
	 * @param min min score inclusive
	 * @param max max score inclusive
	 * @return  the number of elements in the specified score range.
	 * @see #zcount(String, String, String)
	 */
	Long zcount(String key, double min, double max);
	
	/**
	 * Time complexity: O(log(N)+M) with N being the number of elements in the sorted set and M being the number of elements between min and max.
	 * <br>
	 * min and max can be -inf and +inf, so that you are not required to know the highest or lowest score in the sorted set to get all elements from or up to a certain score.
	 * By default, the interval specified by min and max is closed (inclusive). 
	 * It is possible to specify an open interval (exclusive) by prefixing the score with the character '('.
	 * <br>
	 * For example: 
	 * {@link #zcount("test", "(5", "(10")}
	 * Will return all the elements with 5 < score < 10 (5 and 10 excluded).
	 * 
	 * <b>NOTE<b><br>
	 * Transaction unsupported now!
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return the number of elements in the specified score range.
	 */
	Long zcount(String key, String min, String max);
	
	/**
	 * Returns the score of member in the sorted set at key.
	 * If member does not exist in the sorted set, or key does not exist, <tt>null</tt> is returned.
	 * 
	 * @param key
	 * @param member
	 * @return the score of member (a double precision floating point number).
	 */
	Double zscore(String key, String member);
	
	/**
	 * Time complexity: O(log(N))
	 * <br>
	 * Returns the rank of member in the sorted set stored at key, with the scores ordered from low to high. 
	 * The rank (or index) is 0-based, which means that the member with the lowest score has rank 0.
	 * 
	 * @param key
	 * @param member
	 * @return If member exists in the sorted set, return the rank of member.<br>
	 *         If member does not exist in the sorted set or key does not exist, return <tt>null</tt>.
	 */
	Long zrank(String key, String member);
	
	/**
	 * {@link #zremrangeByScore(String, String, String)}
	 * @param key
	 * @param start
	 * @param end
	 * @return the number of elements removed.
	 */
	Long zremrangeByScore(String key, double start, double end);
	
	/**
	 * Time complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements removed by the operation.
	 * <br>
	 * Removes all elements in the sorted set stored at key with a score between start and end (inclusive).
	 * 
	 * @param key
	 * @param start inclusive
	 * @param end   inclusive
	 * @return the number of elements removed.
	 */
	Long zremrangeByScore(String key, String start, String end);
}

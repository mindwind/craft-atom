package org.craft.atom.cache;

import java.util.List;

/**
 * @author Hu Feng
 * @version 1.0, Sep 6, 2012
 */
public interface ListCache extends Cache {

	/**
	 * Insert all the specified values at the head of the list stored at key. If
	 * key does not exist, it is created as empty list before performing the
	 * push operations. When key holds a value that is not a list, an error is
	 * returned. It is possible to push multiple elements using a single command
	 * call just specifying multiple arguments at the end of the command.
	 * Elements are inserted one after the other to the head of the list, from
	 * the leftmost element to the rightmost element. So for instance the
	 * command LPUSH mylist a b c will result into a list containing c as first
	 * element, b as second element and a as third element.
	 * <p>
	 * <b>NOTE:</b><br>
	 * Transaction unsupported now!
	 * 
	 * @param key
	 * @param values
	 * @return the length of the list after the push operations
	 */
	Long lpush(String key, String... values);

	/**
	 * Insert all the specified values at the tail of the list stored at key. If
	 * key does not exist, it is created as empty list before performing the
	 * push operation. When key holds a value that is not a list, an error is
	 * returned. It is possible to push multiple elements using a single command
	 * call just specifying multiple arguments at the end of the command.
	 * Elements are inserted one after the other to the tail of the list, from
	 * the leftmost element to the rightmost element. So for instance the
	 * command RPUSH mylist a b c will result into a list containing a as first
	 * element, b as second element and c as third element.
	 * <p>
	 * <b>NOTE:</b><br>
	 * Transaction unsupported now!
	 * 
	 * @param key
	 * @param values
	 * @return the length of the list after the push operations
	 */
	Long rpush(String key, String... values);

	/**
	 * Removes and returns the first element of the list stored at key.
	 * 
	 * @param key
	 * @return the value of the first element, or null when key does not exist.
	 */
	String lpop(String key);

	/**
	 * Removes and returns the last element of the list stored at key.
	 * 
	 * @param key
	 * @return the value of the last element, or null when key does not exist.
	 */
	String rpop(String key);

	/**
	 * Time complexity: O(S+N) where S is the start offset and N is the number of elements in the specified range.
	 * <p>
	 * 
	 * Returns the specified elements of the list stored at key. The offsets
	 * start and stop are zero-based indexes, with 0 being the first element of
	 * the list (the head of the list), 1 being the next element and so on. <br>
	 * These offsets can also be negative numbers indicating offsets starting at
	 * the end of the list. For example, -1 is the last element of the list, -2
	 * the penultimate, and so on.
	 * <p>
	 * Note that if you have a list of numbers from 0 to 100,
	 * {@link #lrange(String, long, long)} list 0 10 will return 11 elements,
	 * that is, the rightmost item is included. <br>
	 * Out of range indexes will not produce an error. If start is larger than
	 * the end of the list, an empty list is returned. If stop is larger than
	 * the actual end of the list, Redis will treat it like the last element of
	 * the list.
	 * 
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return list of elements in the specified range or empty list if has no element in the range.
	 */
	List<String> lrange(String key, long start, long end);

	/**
	 * Time complexity: O(N) where N is the number of elements to be removed by the operation.
	 * <p>
	 * 
	 * Trim an existing list so that it will contain only the specified range of
	 * elements specified. Both start and stop are zero-based indexes, where 0
	 * is the first element of the list (the head), 1 the next element and so
	 * on.<br>
	 * For example: {@link #ltrim(String, long, long)} will modify the list
	 * stored at foobar so that only the first three elements of the list will
	 * remain.<br>
	 * start and end can also be negative numbers indicating offsets from the
	 * end of the list, where -1 is the last element of the list, -2 the
	 * penultimate element and so on.<br>
	 * Out of range indexes will not produce an error: if start is larger than
	 * the end of the list, or start > end, the result will be an empty list
	 * (which causes key to be removed). If end is larger than the end of the
	 * list, will treat it like the last element of the list.
	 * <p>
	 * A common use of {@link #ltrim(String, long, long)}, for example:
	 * 
	 * <pre>
	 * lpush(mylist, someelement);
	 * ltrim(mylist, 0, 99);
	 * </pre>
	 * 
	 * This pair of commands will push a new element on the list, while making
	 * sure that the list will not grow larger than 100 elements.
	 * 
	 * @param key
	 * @param start
	 * @param end
	 */
	void ltrim(String key, long start, long end);
	
	/**
	 * Returns the length of the list stored at key. If key does not exist, it is interpreted as an empty list and 0 is returned. 
	 * An error is returned when the value stored at key is not a list.
	 * 
	 * @param key
	 * @return the length of the list at key.
	 */
	Long llen(String key);
	
	/**
	 * Time complexity: O(N) where N is the number of elements to traverse to get to the element at index. 
	 * This makes asking for the first or the last element of the list O(1).
	 * <p>
	 * Returns the element at index in the list stored at key. The index is zero-based, so 0 means the first element, 
	 * 1 the second element and so on. Negative indices can be used to designate elements starting at the tail of the list. 
	 * Here, -1 means the last element, -2 means the penultimate and so forth.When the value at key is not a list, an error is returned.
	 * 
	 * @param key
	 * @param index
	 * @return the requested element, or <tt>null</tt> when index is out of range.
	 */
	String lindex(String key, long index);
	
	/**
	 * Time complexity: O(N) where N is the length of the list.
	 * <p>
	 * Removes the first count occurrences of elements equal to value from the list stored at key. 
	 * The count argument influences the operation in the following ways:
	 * <li>count > 0: Remove elements equal to value moving from head to tail.</li>
	 * <li>count < 0: Remove elements equal to value moving from tail to head.</li>
	 * <li>count = 0: Remove all elements equal to value.</li>
	 * <p>
	 * For example, {@link #lrem(list, -2, "hello")} will remove the last two occurrences of "hello" in the list stored at list.
	 * Note that non-existing keys are treated like empty lists, so when key does not exist, the command will always return 0.
	 * 
	 * @param key
	 * @param count
	 * @param value
	 * @return the number of removed elements.
	 */
	Long lrem(String key, long count, String value);
}

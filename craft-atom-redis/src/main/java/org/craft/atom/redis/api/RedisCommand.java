package org.craft.atom.redis.api;

import java.util.List;
import java.util.Set;

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
	 * Time complexity: O(1) to access the key and additional O(N*M) to serialized it, 
	 * where N is the number of Redis objects composing the value and M their average size. 
	 * For small string values the time complexity is thus O(1)+O(1*M) where M is small, so simply O(1).
	 * 
	 * <p>
	 * Serialize the value stored at key in a Redis-specific format and return it to the user. 
	 * The returned value can be synthesized back into a Redis key using the RESTORE command.
	 * 
	 * <p>
	 * The serialization format is opaque and non-standard, however it has a few semantical characteristics:
	 * - It contains a 64-bit checksum that is used to make sure errors will be detected.
	 *   The RESTORE command makes sure to check the checksum before synthesizing a key using the serialized value.
	 * - Values are encoded in the same format used by RDB.
     * - An RDB version is encoded inside the serialized value, 
     *   so that different Redis versions with incompatible RDB formats will refuse to process the serialized value.
     *   
     * <p>
     * The serialized value does NOT contain expire information. 
     * In order to capture the time to live of the current value the PTTL command should be used.
     * 
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
	 * Time complexity: O(1)
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
	 * Time complexity: O(1)
	 * <p>
	 * 
	 * Set a timeout on key. After the timeout has expired, the key will automatically be deleted. 
	 * A key with an associated timeout is often said to be volatile in Redis terminology.
	 * <p>
	 * 
	 * The timeout is cleared only when the key is removed using the DEL command or 
	 * overwritten using the SET or GETSET commands. 
	 * This means that all the operations that conceptually alter the value stored at the key 
	 * without replacing it with a new one will leave the timeout untouched. 
	 * For instance, incrementing the value of a key with INCR, pushing a new value into a list with LPUSH, 
	 * or altering the field value of a hash with HSET are all operations that will leave the timeout untouched.
	 * 
	 * <p>
	 * The timeout can also be cleared, turning the key back into a persistent key, using the PERSIST command.
	 * If a key is renamed with RENAME, the associated time to live is transferred to the new key name.
	 * If a key is overwritten by RENAME, like in the case of an existing key Key_A that is overwritten 
	 * by a call like RENAME Key_B Key_A, it does not matter if the original Key_A had a timeout associated or not, 
	 * the new key Key_A will inherit all the characteristics of Key_B.
	 * 
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
	
	/**
	 * Available since 1.2.0
	 * <p>
	 * Time complexity: O(1)
	 * <p>
	 * 
	 * EXPIREAT has the same effect and semantic as EXPIRE, but instead of specifying the number of seconds representing 
	 * the TTL (time to live), it takes an absolute Unix timestamp (seconds since January 1, 1970).
	 * <p>
	 * 
	 * EXPIREAT was introduced in order to convert relative timeouts to absolute timeouts for the AOF persistence mode. 
	 * Of course, it can be used directly to specify that a given key should expire at a given time in the future.
	 * 
	 * @param key
	 * @param unixTime
	 * @return 1 if the timeout was set.
	 *         0 if key does not exist or the timeout could not be set 
	 */
	long expireAt(String key, long unixTime);
	long expireAt(byte[] key, long unixTime);
	
	/**
	 * Available since 1.0.0
	 * <p>
	 * 
	 * Time complexity: O(N) with N being the number of keys in the database, 
	 * under the assumption that the key names in the database and the given pattern have limited length.
	 * <p>
	 * 
	 * Returns all keys matching pattern.
	 * While the time complexity for this operation is O(N), the constant times are fairly low. 
	 * For example, Redis running on an entry level laptop can scan a 1 million key database in 40 milliseconds.
	 * Warning: consider KEYS as a command that should only be used in production environments with extreme care. 
	 * It may ruin performance when it is executed against large databases. 
	 * This command is intended for debugging and special operations, such as changing your keyspace layout. 
	 * Don't use KEYS in your regular application code. If you're looking for a way to find keys in a subset of your keyspace, consider using sets.
	 * Supported glob-style patterns:
	 * <pre>
	 * h?llo matches hello, hallo and hxllo
	 * h*llo matches hllo and heeeello
	 * h[ae]llo matches hello and hallo, but not hillo
	 * Use \ to escape special characters if you want to match them verbatim.
	 * </pre>
	 * 
	 * @param pattern
	 * @return ist of keys matching pattern.
	 */
	Set<String> keys(String pattern);
	Set<String> keys(byte[] pattern);
	
	/**
	 * Available since 2.6.0
	 * <p>
	 * 
	 * Time complexity: This command actually executes a DUMP+DEL in the source instance, and a RESTORE in the target instance. 
	 * See the pages of these commands for time complexity. Also an O(N) data transfer between the two instances is performed.
	 * <p>
	 * 
	 * Atomically transfer a key from a source Redis instance to a destination Redis instance. 
	 * On success the key is deleted from the original instance and is guaranteed to exist in the target instance.
	 * The command is atomic and blocks the two instances for the time required to transfer the key, 
	 * at any given time the key will appear to exist in a given instance or in the other instance, unless a timeout error occurs.
	 * 
	 * The command internally uses DUMP to generate the serialized version of the key value, and RESTORE in order 
	 * to synthesize the key in the target instance. The source instance acts as a client for the target instance. 
	 * If the target instance returns OK to the RESTORE command, the source instance deletes the key using DEL.
	 * 
	 * The timeout specifies the maximum idle time in any moment of the communication with the destination instance in milliseconds. 
	 * This means that the operation does not need to be completed within the specified amount of milliseconds, 
	 * but that the transfer should make progresses without blocking for more than the specified amount of milliseconds.
	 * 
	 * MIGRATE needs to perform I/O operations and to honor the specified timeout. 
	 * When there is an I/O error during the transfer or if the timeout is reached the operation is aborted and the special error - IOERR returned. 
	 * When this happens the following two cases are possible:
	 * - The key may be on both the instances.
	 * - The key may be only in the source instance.
	 * It is not possible for the key to get lost in the event of a timeout, but the client calling MIGRATE, 
	 * in the event of a timeout error, should check if the key is also present in the target instance and act accordingly.
	 * When any other error is returned (starting with ERR) MIGRATE guarantees that the key is still only present in the 
	 * originating instance (unless a key with the same name was also already present on the target instance).
	 * 
	 * On success OK is returned.
	 * 
	 * @param host
	 * @param port
	 * @param key
	 * @param destinationDb
	 * @param timeoutInMillis
	 * @return OK
	 */
	String migrate(String host, int port, String key, int destinationDb, int timeoutInMillis);
	String migrate(String host, int port, byte[] key, int destinationDb, int timeoutInMillis);
	
	/**
	 * Available since 1.0.0
	 * <p>
	 * Time complexity: O(1)
	 * <p>
	 * 
	 * Move key from the currently selected database (see SELECT) to the specified destination database. 
	 * When key already exists in the destination database, or it does not exist in the source database, it does nothing. 
	 * It is possible to use MOVE as a locking primitive because of this.
	 * 
	 * @param key
	 * @param db
	 * @return 1 if key was moved.
	 *         0 if key was not moved.
	 */
	long move(String key, int db);
	long move(byte[] key, int db);
	
	/**
	 * Available since 2.2.3
	 * <p>
	 * Time complexity: O(1)
	 * <p>
	 * 
	 * The OBJECT command allows to inspect the internals of Redis Objects associated with keys. 
	 * It is useful for debugging or to understand if your keys are using the specially encoded data types to save space. 
	 * Your application may also use the information reported by the OBJECT command to implement application level 
	 * key eviction policies when using Redis as a Cache.
	 * <p>
	 * 
	 * The OBJECT command supports multiple sub commands:
	 * <pre>
	 * OBJECT REFCOUNT key 
	 *   returns the number of references of the value associated with the specified key. 
	 *   This command is mainly useful for debugging.
	 *   
	 * OBJECT ENCODING key 
	 *   returns the kind of internal representation used in order to store the value associated with a key.
	 *   
	 * OBJECT IDLETIME key 
	 *   returns the number of seconds since the object stored at the specified key is idle (not requested by read or write operations). 
	 *   While the value is returned in seconds the actual resolution of this timer is 10 seconds, but may vary in future implementations.
	 * </pre>
	 * 
	 * Objects can be encoded in different ways:
	 * <pre>
	 * - Strings can be encoded as raw (normal string encoding) or int (strings representing integers in a 64 bit signed 
	 *   interval are encoded in this way in order to save space).
     * - Lists can be encoded as ziplist or linkedlist. The ziplist is the special representation that is used to save space for small lists.
	 * - Sets can be encoded as intset or hashtable. The intset is a special encoding used for small sets composed solely of integers.
     * - Hashes can be encoded as zipmap or hashtable. The zipmap is a special encoding used for small hashes.
     * - Sorted Sets can be encoded as ziplist or skiplist format. As for the List type small sorted sets can be specially 
     * - encoded using ziplist, while the skiplist encoding is the one that works with sorted sets of any size.
	 * <pre>
	 * All the specially encoded types are automatically converted to the general type once you perform an operation 
	 * that makes it no possible for Redis to retain the space saving encoding.
	 * <p>
	 * 
	 * @param key
	 * @return
	 */
	long objectRefcount(String key);
	long objectRefcount(byte[] key);
	String objectEncoding(String key);
	String objectEncoding(byte[] key);
	long objectIdletime(String key);
	long objectIdletime(byte[] key);
	
	/**
	 * Available since 2.2.0
	 * <p>
	 * Time complexity: O(1)
	 * <p>
	 * Remove the existing timeout on key, turning the key from volatile (a key with an expire set) 
	 * to persistent (a key that will never expire as no timeout is associated).
	 * 
	 * @param key
	 * @return 1 if the timeout was removed.
	 *         0 if key does not exist or does not have an associated timeout.
	 */
	long persist(String key);
	long persist(byte[] key);
	
	/**
	 * Available since 2.6.0
	 * <p>
	 * Time complexity: O(1)
	 * <p>
	 * This command works exactly like EXPIRE but the time to live of the key is specified in milliseconds instead of seconds.

	 * @param key
	 * @param milliseconds
	 * @return 1 if the timeout was set.
	 *         0 if key does not exist or the timeout could not be set.
	 */
	long pexpire(String key, int milliseconds);
	long pexpire(byte[] key, int milliseconds);
	
	/**
	 * Available since 2.6.0
	 * <p>
	 * Time complexity: O(1)
	 * <p>
	 * PEXPIREAT has the same effect and semantic as EXPIREAT, 
	 * but the Unix time at which the key will expire is specified in milliseconds instead of seconds.
	 * 
	 * @param key
	 * @param unixTime
	 * @return 1 if the timeout was set.
	 *         0 if key does not exist or the timeout could not be set 
	 */
	long pexpireAt(String key, long unixTime);
	long pexpireAt(byte[] key, long unixTime);
	
	/**
	 * Available since 2.6.0
	 * <p>
	 * Time complexity: O(1)
	 * <p>
	 * Like TTL this command returns the remaining time to live of a key that has an expire set, 
	 * with the sole difference that TTL returns the amount of remaining time in seconds while PTTL returns it in milliseconds.
	 * 
	 * @param key
	 * @return Time to live in milliseconds or -1 when key does not exist or does not have a timeout.
	 */
	long pttl(String key);
	long pttl(byte[] key);
	
	/**
	 * Available since 1.0.0
	 * <p>
	 * Time complexity: O(1)
	 * <p>
	 * Return a random key from the currently selected database.
	 * 
	 * @return
	 */
	String randomKey();
	byte[] randomBinaryKey();
	
	/**
	 * Available since 1.0.0
	 * <p>
	 * Time complexity: O(1)
	 * <p>
	 * Renames key to newkey. It returns an error when the source and destination names are the same, or when key does not exist. 
	 * If newkey already exists it is overwritten.
	 * 
	 * @param key
	 * @param newkey
	 * @return The command returns OK on success.
	 */
	String rename(String key, String newkey);
	String rename(byte[] key, byte[] newkey);
	
	/**
	 * Available since 1.0.0
	 * <p>
	 * Time complexity: O(1)
	 * <p>
	 * Renames key to newkey if newkey does not yet exist. It returns an error under the same conditions as RENAME.
	 * 
	 * @param key
	 * @param newkey
	 * @return 1 if key was renamed to newkey.
	 *         0 if newkey already exists.
	 */
	long renamenx(String key, String newkey);
	long renamenx(byte[] key, byte[] newkey);
	
	/**
	 * Available since 2.6.0
	 * <p>
	 * Time complexity: O(1) to create the new key and additional O(N*M) to recostruct the serialized value, 
	 * where N is the number of Redis objects composing the value and M their average size. 
	 * For small string values the time complexity is thus O(1)+O(1*M) where M is small, so simply O(1). 
	 * However for sorted set values the complexity is O(N*M*log(N)) because inserting values into sorted sets is O(log(N)).
	 * <p>
	 * 
	 * Create a key associated with a value that is obtained by deserializing the provided serialized value (obtained via DUMP).
	 * If ttl is 0 the key is created without any expire, otherwise the specified expire time (in milliseconds) is set.
	 * RESTORE checks the RDB version and data checksum. If they don't match an error is returned.
	 * 
	 * @param key
	 * @param ttl in milliseconds
	 * @param serializedValue
	 * @return The command returns OK on success.
	 */
	String restore(String key, long ttl, String serializedValue);
	String restore(byte[] key, long ttl, String serializedValue);
	
	/**
	 * Available since 1.0.0
	 * <p>
	 * Time complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. 
	 * When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
	 * <p>
	 * Returns or stores the elements contained in the list, set or sorted set at key. 
	 * By default, sorting is numeric and elements are compared by their value interpreted as double precision floating point number. 
	 * This is SORT in its simplest form:
	 * <pre>
	 * SORT mylist
	 * </pre>
	 * 
	 * Assuming mylist is a list of numbers, this command will return the same list with the elements sorted from small to large. 
	 * In order to sort the numbers from large to small, use the DESC modifier:
	 * <pre>
	 * SORT mylist DESC
	 * </pre>
	 * 
	 * When mylist contains string values and you want to sort them lexicographically, use the ALPHA modifier:
	 * <pre>
	 * SORT mylist ALPHA
	 * </pre>
	 * 
	 * Redis is UTF-8 aware, assuming you correctly set the !LC_COLLATE environment variable.
	 * The number of returned elements can be limited using the LIMIT modifier. 
	 * This modifier takes the offset argument, specifying the number of elements to skip and the count argument, 
	 * specifying the number of elements to return from starting at offset. 
	 * The following example will return 10 elements of the sorted version of mylist, starting at element 0 (offset is zero-based):
	 * <pre>
	 * SORT mylist LIMIT 0 10
	 * </pre>
	 * 
	 * Almost all modifiers can be used together. The following example will return the first 5 elements, lexicographically sorted in descending order:
	 * <pre>
	 * SORT mylist LIMIT 0 5 ALPHA DESC
	 * </pre>
	 * 
	 * Sometimes you want to sort elements using external keys as weights to compare instead of comparing the actual elements in the list, 
	 * set or sorted set. Let's say the list mylist contains the elements 1, 2 and 3 representing unique IDs of objects 
	 * stored in object_1, object_2 and object_3. When these objects have associated weights stored in weight_1, weight_2 and weight_3, 
	 * SORT can be instructed to use these weights to sort mylist with the following statement:
	 * <pre>
	 * SORT mylist BY weight_*
	 * </pre>
	 * The BY option takes a pattern (equal to weight_* in this example) that is used to generate the keys that are used for sorting. 
	 * These key names are obtained substituting the first occurrence of * with the actual value of the element in the list (1, 2 and 3 in this example).
	 * <p>
	 * 
	 * The BY option can also take a non-existent key, which causes SORT to skip the sorting operation. This is useful if you want to retrieve external keys (see the GET option below) without the overhead of sorting.
	 * <pre>
	 * SORT mylist BY nosort
	 * </pre>
	 * 
	 * Our previous example returns just the sorted IDs. In some cases, it is more useful to get the actual objects 
	 * instead of their IDs (object_1, object_2 and object_3). Retrieving external keys based on the elements in a list, 
	 * set or sorted set can be done with the following command:
	 * <pre>
	 * SORT mylist BY weight_* GET object_*
	 * </pre>
	 * 
	 * The GET option can be used multiple times in order to get more keys for every element of the original list, set or sorted set.
	 * It is also possible to GET the element itself using the special pattern #:
	 * <pre>
     * SORT mylist BY weight_* GET object_* GET #
     * </pre>
     * 
     * By default, SORT returns the sorted elements to the client. With the STORE option, the result will be stored as 
     * a list at the specified key instead of being returned to the client.
	 * <pre>
	 * SORT mylist BY weight_* STORE resultkey
	 * </pre>
	 * An interesting pattern using SORT ... STORE consists in associating an EXPIRE timeout to the resulting key 
	 * so that in applications where the result of a SORT operation can be cached for some time. 
	 * Other clients will use the cached list instead of calling SORT for every request. When the key will timeout, 
	 * an updated version of the cache can be created by calling SORT ... STORE again.
	 * Note that for correctly implementing this pattern it is important to avoid multiple clients rebuilding the cache 
	 * at the same time. Some kind of locking is needed here (for instance using SETNX).
	 * <p>
	 * 
	 * It is possible to use BY and GET options against hash fields with the following syntax:
	 * <pre>
	 * SORT mylist BY weight_*->fieldname GET object_*->fieldname
	 * </pre>
	 * The string -> is used to separate the key name from the hash field name. 
	 * The key is substituted as documented above, and the hash stored at the resulting key is accessed to retrieve the specified hash field.
	 * 
	 * <p>
	 * For example:
	 * <pre>
	 * SORT mylist
	 * SORT mylist DESC
	 * SORT mylist ALPHA
	 * SORT mylist ALPHA DESC
	 * SORT mylist LIMIT 0 10
	 * SORT mylist LIMIT 0 10 ALPHA DESC
	 * 
	 * SORT mylist BY weight_*
	 * SORT mylist BY nosort
	 * SORT mylist BY weight_* DESC
	 * SORT mylist BY weight_* ALPHA DESC
	 * SORT mylist BY weight_* LIMIT 0 10
	 * SORT mylist BY weight_* LIMIT 0 10 ALPHA DESC
	 * 
	 * SORT mylist BY weight_* GET object_*
	 * SORT mylist BY weight_*->fieldname GET object_*->fieldname
	 * SORT mylist BY weight_* GET object_* GET #
	 * SORT mylist BY weight_* GET object_* GET # DESC
	 * SORT mylist BY weight_* GET object_* GET # ALPHA DESC
	 * SORT mylist BY weight_* GET object_* GET # LIMIT 0 10
	 * SORT mylist BY weight_* GET object_* GET # LIMIT 0 10 ALPHA DESC
	 * 
	 * SORT mylist BY weight_* STORE resultkey
	 * SORT mylist BY weight_* DESC STORE resultkey
	 * SORT mylist BY weight_* ALPHA DESC STORE resultkey
	 * SORT mylist BY weight_* LIMIT 0 10 STORE resultkey
	 * SORT mylist BY weight_* LIMIT 0 10 ALPHA DESC STORE resultkey
	 * 
	 * SORT mylist BY weight_* GET object_* GET # STORE resultkey
	 * SORT mylist BY weight_* GET object_* GET # DESC STORE resultkey
	 * SORT mylist BY weight_* GET object_* GET # ALPHA DESC STORE resultkey
	 * SORT mylist BY weight_* GET object_* GET # LIMIT 0 10 STORE resultkey
	 * SORT mylist BY weight_* GET object_* GET # LIMIT 0 10 ALPHA DESC STORE resultkey
	 * </pre>
	 * 
	 * @param key
	 * @return list of sorted elements.
	 */
	List<String> sort(String key);
	List<String> sort(String key, boolean desc);
	List<String> sort(String key, boolean alpha, boolean desc);
	List<String> sort(String key, int offset, int count);
	List<String> sort(String key, int offset, int count, boolean alpha, boolean desc);
	List<String> sort(String key, String byPattern, String... getPatterns);
	List<String> sort(String key, String byPattern, boolean desc, String... getPatterns);
	List<String> sort(String key, String byPattern, boolean alpha, boolean desc, String... getPatterns);
	List<String> sort(String key, String byPattern, int offset, int count, String... getPatterns);
	List<String> sort(String key, String byPattern, int offset, int count, boolean alpha, boolean desc, String... getPatterns);
	List<String> sort(String key, String byPattern, String destination);
	List<String> sort(String key, String byPattern, boolean desc, String destination, String... getPatterns);
	List<String> sort(String key, String byPattern, boolean alpha, boolean desc, String destination, String... getPatterns);
	List<String> sort(String key, String byPattern, int offset, int count, String destination, String... getPatterns);
	List<String> sort(String key, String byPattern, int offset, int count, boolean alpha, boolean desc, String destination, String... getPatterns);

	/**
	 * Available since 1.0.0
	 * <p>
	 * Time complexity: O(1)
	 * <p>
	 * 
	 * Returns the remaining time to live of a key that has a timeout. 
	 * This introspection capability allows a Redis client to check how many seconds a given key will continue to be part of the dataset.
	 * 
	 * @param key
	 * @return TTL in seconds, -2 when key does not exist or -1 when key does not have a timeout.
	 */
	long ttl(String key);
	long ttl(byte[] key);
	
	/**
	 * Available since 1.0.0
	 * <p>
	 * Time complexity: O(1)
	 * <p>
	 * Returns the string representation of the type of the value stored at key. 
	 * The different types that can be returned are: string, list, set, zset and hash.
	 * 
	 * @param key
	 * @return type of key, or none when key does not exist.
	 */
	String type(String key);
	String type(byte[] key);
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Strings
	
	
}

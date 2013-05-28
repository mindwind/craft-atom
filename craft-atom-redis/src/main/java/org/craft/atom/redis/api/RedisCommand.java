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
	 * Time complexity: O(N) where N is the number of keys that will be removed. 
	 * 
	 * <p>
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
	 * Time complexity: O(1) to access the key and additional O(N*M) to serialized it, 
	 * where N is the number of Redis objects composing the value and M their average size. 
	 * For small string values the time complexity is thus O(1)+O(1*M) where M is small, so simply O(1).
	 * 
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
     * If key does not exist a null bulk reply is returned.
	 * 
	 * @param key
	 * @return the serialized value
	 */
	String dump(String key);
	byte[] dump(byte[] key);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
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
	 * Time complexity: O(1)
	 * 
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
	
	/**
	 * Available since 1.2.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * EXPIREAT has the same effect and semantic as EXPIRE, but instead of specifying the number of seconds representing 
	 * the TTL (time to live), it takes an absolute Unix timestamp (seconds since January 1, 1970).
	 * <p>
	 * EXPIREAT was introduced in order to convert relative timeouts to absolute timeouts for the AOF persistence mode. 
	 * Of course, it can be used directly to specify that a given key should expire at a given time in the future.
	 * 
	 * @param key
	 * @param unixTime
	 * @return 1 if the timeout was set.
	 *         0 if key does not exist or the timeout could not be set 
	 */
	long expireat(String key, long unixTime);
	long expireat(byte[] key, long unixTime);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(N) with N being the number of keys in the database, 
	 * under the assumption that the key names in the database and the given pattern have limited length.
	 * 
	 * <p>
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
	Set<byte[]> keys(byte[] pattern);
	
	/**
	 * Available since 2.6.0 
	 * Time complexity: This command actually executes a DUMP+DEL in the source instance, and a RESTORE in the target instance. 
	 * See the pages of these commands for time complexity. Also an O(N) data transfer between the two instances is performed.
	 * 
	 * <p>
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
	byte[] migrate(String host, int port, byte[] key, int destinationDb, int timeoutInMillis);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
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
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * The OBJECT command allows to inspect the internals of Redis Objects associated with keys. 
	 * It is useful for debugging or to understand if your keys are using the specially encoded data types to save space. 
	 * Your application may also use the information reported by the OBJECT command to implement application level 
	 * key eviction policies when using Redis as a Cache.
	 * <p>
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
	 * 
	 * @param key
	 * @return
	 */
	long objectrefcount(String key);
	long objectrefcount(byte[] key);
	String objectencoding(String key);
	byte[] objectencoding(byte[] key);
	long objectidletime(String key);
	long objectidletime(byte[] key);
	
	/**
	 * Available since 2.2.0
	 * Time complexity: O(1)
	 * 
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
	 * Time complexity: O(1)
	 * 
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
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * PEXPIREAT has the same effect and semantic as EXPIREAT, 
	 * but the Unix time at which the key will expire is specified in milliseconds instead of seconds.
	 * 
	 * @param key
	 * @param unixTime
	 * @return 1 if the timeout was set.
	 *         0 if key does not exist or the timeout could not be set 
	 */
	long pexpireat(String key, long unixTime);
	long pexpireat(byte[] key, long unixTime);
	
	/**
	 * Available since 2.6.0
	 * Time complexity: O(1)
	 * 
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
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Return a random key from the currently selected database.
	 * 
	 * @return
	 */
	String randomkey();
	byte[] randombinarykey();
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Renames key to newkey. It returns an error when the source and destination names are the same, or when key does not exist. 
	 * If newkey already exists it is overwritten.
	 * 
	 * @param key
	 * @param newkey
	 * @return The command returns OK on success.
	 */
	String rename(String key, String newkey);
	byte[] rename(byte[] key, byte[] newkey);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
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
	 * Time complexity: O(1) to create the new key and additional O(N*M) to recostruct the serialized value, 
	 * where N is the number of Redis objects composing the value and M their average size. 
	 * For small string values the time complexity is thus O(1)+O(1*M) where M is small, so simply O(1). 
	 * However for sorted set values the complexity is O(N*M*log(N)) because inserting values into sorted sets is O(log(N)).
	 * 
	 * <p>
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
	byte[] restore(byte[] key, long ttl, String serializedValue);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. 
	 * When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
	 * 
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
	List<byte[]> sort(byte[] key);
	List<byte[]> sort(byte[] key, boolean desc);
	List<byte[]> sort(byte[] key, boolean alpha, boolean desc);
	List<byte[]> sort(byte[] key, int offset, int count);
	List<byte[]> sort(byte[] key, int offset, int count, boolean alpha, boolean desc);
	List<byte[]> sort(byte[] key, String byPattern, String... getPatterns);
	List<byte[]> sort(byte[] key, String byPattern, boolean desc, String... getPatterns);
	List<byte[]> sort(byte[] key, String byPattern, boolean alpha, boolean desc, String... getPatterns);
	List<byte[]> sort(byte[] key, String byPattern, int offset, int count, String... getPatterns);
	List<byte[]> sort(byte[] key, String byPattern, int offset, int count, boolean alpha, boolean desc, String... getPatterns);
	List<byte[]> sort(byte[] key, String byPattern, byte[] destination);
	List<byte[]> sort(byte[] key, String byPattern, boolean desc, byte[] destination, String... getPatterns);
	List<byte[]> sort(byte[] key, String byPattern, boolean alpha, boolean desc, byte[] destination, String... getPatterns);
	List<byte[]> sort(byte[] key, String byPattern, int offset, int count, byte[] destination, String... getPatterns);
	List<byte[]> sort(byte[] key, String byPattern, int offset, int count, boolean alpha, boolean desc, byte[] destination, String... getPatterns);

	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
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
	 * Time complexity: O(1)
	 * 
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
	
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(1). The amortized time complexity is O(1) assuming the appended value is small 
	 * and the already present value is of any size, since the dynamic string library used by Redis will double the 
	 * free space available on every reallocation.
	 * 
	 * <p>
	 * If key already exists and is a string, this command appends the value at the end of the string. 
	 * If key does not exist it is created and set as an empty string, so APPEND will be similar to SET in this special case.
	 * 
	 * @param key
	 * @param value
	 * @return the length of the string after the append operation.
	 */
	long append(String key, String value);
	long append(byte[] key, String value);
	
	/**
	 * Available since 2.6.0
	 * Time complexity: O(N)
	 * 
	 * <p>
	 * Count the number of set bits (population counting) in a string.
	 * By default all the bytes contained in the string are examined. It is possible to specify the counting operation 
	 * only in an interval passing the additional arguments start and end.Like for the GETRANGE command start and end 
	 * can contain negative values in order to index bytes starting from the end of the string, where -1 is the last byte, 
	 * -2 is the penultimate, and so forth.
	 * Non-existent keys are treated as empty strings, so the command will return zero.
	 * 
	 * @param key
	 * @return The number of bits set to 1.
	 */
	long bitcount(String key);
	long bitcount(byte[] key);
	long bitcount(String key, long start, long end);
	long bitcount(byte[] key, long start, long end);
	
	/**
	 * Available since 2.6.0
	 * Time complexity: O(N)
	 * 
	 * <p>
	 * Perform a bitwise operation between multiple keys (containing string values) and store the result in the destination key.
	 * The BITOP command supports four bitwise operations: AND, OR, XOR and NOT, thus the valid forms to call the command are:
	 * BITOP AND destkey srckey1 srckey2 srckey3 ... srckeyN
	 * BITOP OR destkey srckey1 srckey2 srckey3 ... srckeyN
	 * BITOP XOR destkey srckey1 srckey2 srckey3 ... srckeyN
	 * BITOP NOT destkey srckey
	 * As you can see NOT is special as it only takes an input key, because it performs inversion of bits so it only makes sense as an unary operator.
	 * The result of the operation is always stored at destkey.
	 * <p>
	 * Handling of strings with different lengths
	 * When an operation is performed between strings having different lengths, all the strings shorter than the longest string 
	 * in the set are treated as if they were zero-padded up to the length of the longest string.
	 * The same holds true for non-existent keys, that are considered as a stream of zero bytes up to the length of the longest string.
	 * 
	 * @param destKey
	 * @param keys
	 * @return The size of the string stored in the destination key, that is equal to the size of the longest input string.
	 */
	long bitnot(String destKey, String key);
	long bitnot(byte[] destKey, byte[] key);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Decrements the number stored at key by one. If the key does not exist, it is set to 0 before performing the operation. 
	 * An error is returned if the key contains a value of the wrong type or contains a string that can not be represented as integer. 
	 * This operation is limited to 64 bit signed integers.
	 * See INCR for extra information on increment/decrement operations.
	 * 
	 * @param key
	 * @return the value of key after the decrement
	 */
	long decr(String key);
	long decr(byte[] key);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Decrements the number stored at key by decrement. If the key does not exist, it is set to 0 before performing the operation. 
	 * An error is returned if the key contains a value of the wrong type or contains a string that can not be represented as integer. 
	 * This operation is limited to 64 bit signed integers.
	 * See INCR for extra information on increment/decrement operations.
	 * 
	 * @param key
	 * @param decrement
	 * @return the value of key after the decrement
	 */
	long decrby(String key, long decrement);
	long decrby(byte[] key, long decrement);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Get the value of key. If the key does not exist the special value null is returned. 
	 * An error is returned if the value stored at key is not a string, because GET only handles string values.
	 * 
	 * @param key
	 * @return the value of key, or nil when key does not exist.
	 */
	String get(String key);
	byte[] get(byte[] key);
	
	/**
	 * Available since 2.2.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Returns the bit value at offset in the string value stored at key.
	 * When offset is beyond the string length, the string is assumed to be a contiguous space with 0 bits. 
	 * When key does not exist it is assumed to be an empty string, so offset is always out of range and the value is also 
	 * assumed to be a contiguous space with 0 bits.
	 * 
	 * @param key
	 * @param offset
	 * @return the bit value stored at offset.
	 */
	boolean getbit(String key, long offset);
	boolean getbit(byte[] key, long offset);
	
	/**
	 * Available since 2.4.0
	 * Time complexity: O(N) where N is the length of the returned string. The complexity is ultimately determined by the returned length, 
	 * but because creating a substring from an existing string is very cheap, it can be considered O(1) for small strings.
	 * 
	 * <p>
	 * Warning: this command was renamed to GETRANGE, it is called SUBSTR in Redis versions <= 2.0.
	 * Returns the substring of the string value stored at key, determined by the offsets start and end (both are inclusive). 
	 * Negative offsets can be used in order to provide an offset starting from the end of the string. So -1 means the last character, 
	 * -2 the penultimate and so forth.
	 * The function handles out of range requests by limiting the resulting range to the actual length of the string.
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	String getrange(String key, long start, long end);
	byte[] getrange(byte[] key, long start, long end);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Atomically sets key to value and returns the old value stored at key. 
	 * Returns an error when key exists but does not hold a string value.
	 * 
	 * @param key
	 * @param value
	 * @return the old value stored at key, or null when key did not exist.
	 */
	String getset(String key, String value);
	byte[] getset(byte[] key, String value);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Increments the number stored at key by one. If the key does not exist, it is set to 0 before performing the operation. 
	 * An error is returned if the key contains a value of the wrong type or contains a string that can not be represented as integer. 
	 * This operation is limited to 64 bit signed integers.
	 * 
	 * Note: this is a string operation because Redis does not have a dedicated integer type. 
	 * The string stored at the key is interpreted as a base-10 64 bit signed integer to execute the operation.
	 * Redis stores integers in their integer representation, so for string values that actually hold an integer, 
	 * there is no overhead for storing the string representation of the integer.
	 * 
	 * @param key
	 * @return the value of key after the increment
	 */
	long incr(String key);
	long incr(byte[] key);
	
	/**
	 * available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Increments the number stored at key by increment. If the key does not exist, it is set to 0 before performing the operation. 
	 * An error is returned if the key contains a value of the wrong type or contains a string that can not be represented as integer. 
	 * This operation is limited to 64 bit signed integers. 
	 * See INCR for extra information on increment/decrement operations.
	 * 
	 * @param key
	 * @param increment
	 * @return  the value of key after the increment
	 */
	long incrby(String key, long increment);
	long incrby(byte[] key, long increment);
	
	/**
	 * Available since 2.6.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Increment the string representing a floating point number stored at key by the specified increment. 
	 * If the key does not exist, it is set to 0 before performing the operation. 
	 * An error is returned if one of the following conditions occur:
	 * - The key contains a value of the wrong type (not a string).
	 * - The current key content or the specified increment are not parsable as a double precision floating point number.
	 * 
	 * If the command is successful the new incremented value is stored as the new value of the key (replacing the old one), 
	 * and returned to the caller as a string.
	 * Both the value already contained in the string key and the increment argument can be optionally provided in exponential notation, 
	 * however the value computed after the increment is stored consistently in the same format, that is, 
	 * an integer number followed (if needed) by a dot, and a variable number of digits representing the decimal part of the number. 
	 * Trailing zeroes are always removed.
	 * The precision of the output is fixed at 17 digits after the decimal point regardless of the actual internal precision of the computation.
	 * 
	 * @param key
	 * @param increment
	 * @return the value of key after the increment.
	 */
	double incrbyfloat(String key, double increment);
	double incrbyfloat(byte[] key, double increment);
	
	/**
	 * Available since 2.6.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * PSETEX works exactly like SETEX with the sole difference that the expire time is specified in milliseconds instead of seconds.
	 * 
	 * @param key
	 * @param milliseconds
	 * @param value
	 * @return
	 */
	String psetex(String key, int milliseconds, String value);
	byte[] psetex(byte[] key, int milliseconds, String value);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Set key to hold the string value. If key already holds a value, it is overwritten, regardless of its type. 
	 * Any previous time to live associated with the key is discarded on successful SET operation.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	String set(String key, String value);
	byte[] set(byte[] key, String value);
	
	/**
	 * Available since 2.6.12
	 * 
	 * <p>
	 * Options
	 * Starting with Redis 2.6.12 SET supports a set of options that modify its behavior:
	 * SET key value [EX seconds] [PX milliseconds] [NX|XX]
	 * EX seconds -- Set the specified expire time, in seconds.
	 * PX milliseconds -- Set the specified expire time, in milliseconds.
	 * NX -- Only set the key if it does not already exist.
	 * XX -- Only set the key if it already exist.
	 * Note: Since the SET command options can replace SETNX, SETEX, PSETEX, it is possible that in future versions of 
	 *       Redis these three commands will be deprecated and finally removed.
	 * 
	 * @param key
	 * @param value
	 * @return OK if SET was executed correctly. 
	 *         Null multi-bulk reply: a Null Bulk Reply is returned if the SET operation was not performed becase the 
	 *         user specified the NX or XX option but the condition was not met.
	 */
	String setxx(String key, String value);
	byte[] setxx(byte[] key, byte[] value);
	String setnxex(String key, String value, int seconds);
	byte[] setnxex(byte[] key, byte[] value, int seconds);
	String setnxpx(String key, String value, int milliseconds);
	byte[] setnxpx(byte[] key, byte[] value, int milliseconds);
	String setxxex(String key, String value, int seconds);
	byte[] setxxex(byte[] key, byte[] value, int seconds);
	String setxxpx(String key, String value, int milliseconds);
	byte[] setxxpx(byte[] key, byte[] value, int milliseconds);
	
	/**
	 * Available since 2.2.0
	 * Time complexity: O(1)
	 * <p>
	 * 
	 * Sets or clears the bit at offset in the string value stored at key.
	 * The bit is either set or cleared depending on value, which can be either 0 or 1. When key does not exist, 
	 * a new string value is created. The string is grown to make sure it can hold a bit at offset. 
	 * The offset argument is required to be greater than or equal to 0, and smaller than 232 (this limits bitmaps to 512MB). 
	 * When the string at key is grown, added bits are set to 0.
	 * <p>
	 * Warning: 
	 * When setting the last possible bit (offset equal to 232 -1) and the string value stored at key does not yet hold a string value, 
	 * or holds a small string value, Redis needs to allocate all intermediate memory which can block the server for some time. 
	 * On a 2010 MacBook Pro, setting bit number 232 -1 (512MB allocation) takes ~300ms, setting bit number 230 -1 (128MB allocation) takes ~80ms, 
	 * setting bit number 228 -1 (32MB allocation) takes ~30ms and setting bit number 226 -1 (8MB allocation) takes ~8ms. 
	 * Note that once this first allocation is done, subsequent calls to SETBIT for the same key will not have the allocation overhead.
	 * 
	 * @param key
	 * @param offset
	 * @param value true means bit is set 1, otherwise 0
	 * @return the original bit value stored at offset
	 */
	boolean setbit(String key, long offset, boolean value);
	boolean setbit(byte[] key, long offset, boolean value);
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Set key to hold the string value and set key to timeout after a given number of seconds. This command is equivalent to executing the following commands:
	 * <pre>
	 * SET mykey value
	 * EXPIRE mykey seconds
	 * </pre>
	 * SETEX is atomic, and can be reproduced by using the previous two commands inside an MULTI / EXEC block. 
	 * It is provided as a faster alternative to the given sequence of operations, because this operation is very common when Redis is used as a cache.
	 * An error is returned when seconds is invalid.
	 * 
	 * @param key
	 * @param seconds
	 * @param value
	 * @return Status code reply, e.g. OK
	 */
	String setex(String key, int seconds, String value);
	byte[] setex(byte[] key, int seconds, byte[] value);
	
	/**
	 * Available since 1.0.0.
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Set key to hold string value if key does not exist. In that case, it is equal to SET. 
	 * When key already holds a value, no operation is performed. SETNX is short for "SET if N ot e X ists".
	 * 
	 * @param key
	 * @param value
	 * @return 1 if the key was set
	 *         0 if the key was not set
	 */
	long setnx(String key, String value);
	long setnx(byte[] key, byte[] value);
	
	/**
	 * Available since 2.2.0.
	 * Time complexity: O(1)
	 * not counting the time taken to copy the new string in place. Usually, 
	 * this string is very small so the amortized complexity is O(1). Otherwise, complexity is O(M) with M being the length of the value argument.
	 * 
	 * <p>
	 * Overwrites part of the string stored at key, starting at the specified offset, for the entire length of value. 
	 * If the offset is larger than the current length of the string at key, the string is padded with zero-bytes to make offset fit. 
	 * Non-existing keys are considered as empty strings, so this command will make sure it holds a string large enough to be able to set value at offset.
	 * Note that the maximum offset that you can set is 229 -1 (536870911), as Redis Strings are limited to 512 megabytes. 
	 * If you need to grow beyond this size, you can use multiple keys.
	 * <p>
	 * Warning: When setting the last possible byte and the string value stored at key does not yet hold a string value, 
	 * or holds a small string value, Redis needs to allocate all intermediate memory which can block the server for some time. 
	 * On a 2010 MacBook Pro, setting byte number 536870911 (512MB allocation) takes ~300ms, 
	 * setting byte number 134217728 (128MB allocation) takes ~80ms, setting bit number 33554432 (32MB allocation) takes ~30ms 
	 * and setting bit number 8388608 (8MB allocation) takes ~8ms. Note that once this first allocation is done, 
	 * subsequent calls to SETRANGE for the same key will not have the allocation overhead.
	 * 
	 * @param key
	 * @param offset
	 * @param value
	 * @return
	 */
	long setrange(String key, long offset, String value);
	long setrange(byte[] key, long offset, byte[] value);
	
	/**
	 * Available since 2.2.0.
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Returns the length of the string value stored at key. An error is returned when key holds a non-string value.
	 * 
	 * @param key
	 * @return the length of the string at key, or 0 when key does not exist.
	 */
	long strlen(String key);
	long strlen(byte[] key);
}

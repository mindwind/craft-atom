package org.craft.atom.redis.api;

import java.util.List;
import java.util.Map;
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
	 * @return true if the key exists.
	 *         false if the key does not exist.
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
	 * @param timestamp a unix timestamp
	 * @return 1 if the timeout was set.
	 *         0 if key does not exist or the timeout could not be set 
	 */
	long expireat(String key, long timestamp);
	long expireat(byte[] key, long timestamp);
	
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
	 * @param destinationdb
	 * @param timeout in milliseconds
	 * @return OK
	 */
	String migrate(String host, int port, String key, int destinationdb, int timeout);
	byte[] migrate(String host, int port, byte[] key, int destinationdb, int timeout);
	
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
	 * @param millisecondstimestamp
	 * @return 1 if the timeout was set.
	 *         0 if key does not exist or the timeout could not be set 
	 */
	long pexpireat(String key, long millisecondstimestamp);
	long pexpireat(byte[] key, long millisecondstimestamp);
	
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
	 * @param serializedvalue
	 * @return The command returns OK on success.
	 */
	String restore(String key, long ttl, String serializedvalue);
	byte[] restore(byte[] key, long ttl, byte[] serializedvalue);
	
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
	List<String> sort(String key, String bypattern, String... getpatterns);
	List<String> sort(String key, String bypattern, boolean desc, String... getpatterns);
	List<String> sort(String key, String bypattern, boolean alpha, boolean desc, String... getpatterns);
	List<String> sort(String key, String bypattern, int offset, int count, String... getpatterns);
	List<String> sort(String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String... getpatterns);
	List<String> sort(String key, String bypattern, String destination);
	List<String> sort(String key, String bypattern, boolean desc, String destination, String... getpatterns);
	List<String> sort(String key, String bypattern, boolean alpha, boolean desc, String destination, String... getpatterns);
	List<String> sort(String key, String bypattern, int offset, int count, String destination, String... getpatterns);
	List<String> sort(String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String destination, String... getpatterns);
	List<byte[]> sort(byte[] key);
	List<byte[]> sort(byte[] key, boolean desc);
	List<byte[]> sort(byte[] key, boolean alpha, boolean desc);
	List<byte[]> sort(byte[] key, int offset, int count);
	List<byte[]> sort(byte[] key, int offset, int count, boolean alpha, boolean desc);
	List<byte[]> sort(byte[] key, byte[] bypattern, byte[]... getpatterns);
	List<byte[]> sort(byte[] key, byte[] bypattern, boolean desc, byte[]... getpatterns);
	List<byte[]> sort(byte[] key, byte[] bypattern, boolean alpha, boolean desc, byte[]... getpatterns);
	List<byte[]> sort(byte[] key, byte[] bypattern, int offset, int count, byte[]... getpatterns);
	List<byte[]> sort(byte[] key, byte[] bypattern, int offset, int count, boolean alpha, boolean desc, byte[]... getpatterns);
	List<byte[]> sort(byte[] key, byte[] bypattern, byte[] destination);
	List<byte[]> sort(byte[] key, byte[] bypattern, boolean desc, byte[] destination, byte[]... getpatterns);
	List<byte[]> sort(byte[] key, byte[] bypattern, boolean alpha, boolean desc, byte[] destination, byte[]... getpatterns);
	List<byte[]> sort(byte[] key, byte[] bypattern, int offset, int count, byte[] destination, byte[]... getpatterns);
	List<byte[]> sort(byte[] key, byte[] bypattern, int offset, int count, boolean alpha, boolean desc, byte[] destination, byte[]... getpatterns);

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
	 * @param destkey
	 * @param keys
	 * @return The size of the string stored in the destination key, that is equal to the size of the longest input string.
	 */
	long bitnot(String destkey, String key);
	long bitnot(byte[] destkey, byte[] key);
	
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
	 * @return the value of key, or null when key does not exist.
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
	byte[] getset(byte[] key, byte[] value);
	
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
	byte[] psetex(byte[] key, int milliseconds, byte[] value);
	
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
	byte[] set(byte[] key, byte[] value);
	
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
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Hashes
	
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(N) 
	 * where N is the number of fields to be removed.
	 * 
	 * <p>
	 * Removes the specified fields from the hash stored at key. 
	 * Specified fields that do not exist within this hash are ignored. 
	 * If key does not exist, it is treated as an empty hash and this command returns 0.
	 * 
	 * History
	 * >= 2.4: Accepts multiple field arguments. Redis versions older than 2.4 can only remove a field per call.
	 * To remove multiple fields from a hash in an atomic fashion in earlier versions, use a MULTI / EXEC block.
	 * 
	 * @param key
	 * @param fields
	 * @return the number of fields that were removed from the hash, not including specified but non existing fields.
	 */
	long hdel(String key, String... fields);
	long hdel(byte[] key, String... fields);
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Returns if field is an existing field in the hash stored at key.
	 * 
	 * @param key
	 * @param field
	 * @return true if the hash contains field.
	 *         false if the hash does not contain field, or key does not exist.
	 */
	boolean hexists(String key, String field);
	boolean hexists(byte[] key, byte[] field);
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Returns the value associated with field in the hash stored at key.
	 * 
	 * @param key
	 * @param field
	 * @return the value associated with field, or null when field is not present in the hash or key does not exist.
	 */
	String hget(String key, String field);
	byte[] hget(byte[] key, byte[] field);
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(N) 
	 * where N is the size of the hash.
	 * 
	 * <p>
	 * Returns all fields and values of the hash stored at key. 
	 * In the returned value, every field name is followed by its value, so the length of the reply is twice the size of the hash.
	 * 
	 * @param key
	 * @return list of fields and their values stored in the hash, or an empty list when key does not exist.
	 */
	Map<String, String> hgetall(String key);
	Map<byte[], byte[]> hgetall(byte[] key);
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Increments the number stored at field in the hash stored at key by increment. If key does not exist, 
	 * a new key holding a hash is created. If field does not exist the value is set to 0 before the operation is performed.
	 * The range of values supported by HINCRBY is limited to 64 bit signed integers.
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return the value at field after the increment operation.
	 */
	long hincrby(String key, String field, long value);
	long hincrby(byte[] key, byte[] field, long value);
	
	/**
	 * Available since 2.6.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Increment the specified field of an hash stored at key, and representing a floating point number, by the specified increment. 
	 * If the field does not exist, it is set to 0 before performing the operation. 
	 * An error is returned if one of the following conditions occur:
	 * - The field contains a value of the wrong type (not a string).
	 * - The current field content or the specified increment are not parsable as a double precision floating point number.
	 * The exact behavior of this command is identical to the one of the INCRBYFLOAT command, 
	 * please refer to the documentation of INCRBYFLOAT for further information.
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return the value of field after the increment.
	 */
	double hincrbyfloat(String key, String field, double value);
	double hincrbyfloat(byte[] key, byte[] field, double value);
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(N) where N is the size of the hash.
	 * 
	 * <p>
	 * Returns all field names in the hash stored at key.
	 * 
	 * @param key
	 * @return list of fields in the hash, or an empty list when key does not exist.
	 */
	Set<String> hkeys(String key);
	Set<byte[]> hkeys(byte[] key);
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Returns the number of fields contained in the hash stored at key.
	 * 
	 * @param key
	 * @return number of fields in the hash, or 0 when key does not exist.
	 */
	long hlen(String key);
	long hlen(byte[] key);
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(N) where N is the number of fields being requested.
	 * 
	 * <p>
	 * Returns the values associated with the specified fields in the hash stored at key.
	 * For every field that does not exist in the hash, a null value is returned. 
	 * Because a non-existing keys are treated as empty hashes, running HMGET against a non-existing key will return a list of nil values.
	 * 
	 * @param key
	 * @param fields
	 * @return list of values associated with the given fields, in the same order as they are requested.
	 */
	List<String> hmget(String key, String... fields);
	List<byte[]> hmget(byte[] key, byte[]... fields);
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(N) where N is the number of fields being set.
	 * 
	 * <p>
	 * Sets the specified fields to their respective values in the hash stored at key. 
	 * This command overwrites any existing fields in the hash. If key does not exist, a new key holding a hash is created.
	 * 
	 * @param key
	 * @param fieldvalues
	 * @return Status code reply, e.g. OK
	 */
	String hmset(String key, Map<String, String> fieldvalues);
	byte[] hmset(byte[] key, Map<byte[], byte[]> fieldvalues);
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Sets field in the hash stored at key to value. If key does not exist, a new key holding a hash is created. 
	 * If field already exists in the hash, it is overwritten.
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return 1 if field is a new field in the hash and value was set.
	 *         0 if field already exists in the hash and the value was updated.
	 */
	long hset(String key, String field, String value);
	long hset(byte[] key, byte[] field, byte[] value);
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Sets field in the hash stored at key to value, only if field does not yet exist. If key does not exist, 
	 * a new key holding a hash is created. If field already exists, this operation has no effect.
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return 1 if field is a new field in the hash and value was set.
	 *         0 if field already exists in the hash and no operation was performed.
	 */
	long hsetnx(String key, String field, String value);
	long hsetnx(byte[] key, byte[] field, byte[] value);
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(N) where N is the size of the hash.
	 * 
	 * <p>
	 * Returns all values in the hash stored at key.
	 * 
	 * @param key
	 * @return list of values in the hash, or an empty list when key does not exist.
	 */
	List<String> hvals(String key);
	List<byte[]> hvals(byte[] key);
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Lists
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * BLPOP is a blocking list pop primitive. It is the blocking version of LPOP because it blocks the connection when 
	 * there are no elements to pop from any of the given lists. An element is popped from the head of the first list that is non-empty, 
	 * with the given keys being checked in the order that they are given.
	 * 
	 * <p>
	 * Non-blocking behavior
	 * When BLPOP is called, if at least one of the specified keys contains a non-empty list, 
	 * an element is popped from the head of the list and returned to the caller together with the key it was popped from.
	 * Keys are checked in the order that they are given. Let's say that the key list1 doesn't exist and list2 and list3 hold non-empty lists. 
	 * Consider the following command:
	 * <pre>
	 * BLPOP list1 list2 list3 0
	 * </pre>
	 * BLPOP guarantees to return an element from the list stored at list2 (since it is the first non empty list when 
	 * checking list1, list2 and list3 in that order).
	 * 
	 * <p>
	 * Blocking behavior
	 * If none of the specified keys exist, BLPOP blocks the connection until another client performs an LPUSH or RPUSH
	 * operation against one of the keys.
	 * Once new data is present on one of the lists, the client returns with the name of the key unblocking it and the popped value.
	 * When BLPOP causes a client to block and a non-zero timeout is specified, the client will unblock returning a nil 
	 * multi-bulk value when the specified timeout has expired without a push operation against at least one of the specified keys.
	 * The timeout argument is interpreted as an integer value specifying the maximum number of seconds to block. 
	 * A timeout of zero can be used to block infinitely.
	 * 
	 * <p>
	 * What key is served first? What client? What element? Priority ordering details.
	 * If the client tries to blocks for multiple keys, but at least one key contains elements, 
	 * the returned key / element pair is the first key from left to right that has one or more elements. 
	 * In this case the client is not blocked. So for instance BLPOP key1 key2 key3 key4 0, assuming that both key2 and key4 are non-empty, 
	 * will always return an element from key2.
	 * If multiple clients are blocked for the same key, the first client to be served is the one that was waiting for 
	 * more time (the first that blocked for the key). Once a client is unblocked it does not retain any priority, 
	 * when it blocks again with the next call to BLPOP it will be served accordingly to the number of clients already 
	 * blocked for the same key, that will all be served before it (from the first to the last that blocked).
	 * When a client is blocking for multiple keys at the same time, and elements are available at the same time in 
	 * multiple keys (because of a transaction or a Lua script added elements to multiple lists), 
	 * the client will be unblocked using the first key that received a push operation (assuming it has enough elements to 
	 * serve our client, as there may be other clients as well waiting for this key). Basically after the execution of every 
	 * command Redis will run a list of all the keys that received data AND that have at least a client blocked. 
	 * The list is ordered by new element arrival time, from the first key that received data to the last. 
	 * For every key processed, Redis will serve all the clients waiting for that key in a FIFO fashion, as long as there 
	 * are elements in this key. When the key is empty or there are no longer clients waiting for this key, 
	 * the next key that received new data in the previous command / transaction / script is processed, and so forth.
	 * 
	 * <p>
	 * Behavior of BLPOP when multiple elements are pushed inside a list.
	 * There are times when a list can receive multiple elements in the context of the same conceptual command:
	 * Variadic push operations such as LPUSH mylist a b c.
	 * After an EXEC of a MULTI block with multiple push operations against the same list.
	 * Executing a Lua Script with Redis 2.6 or newer.
	 * When multiple elements are pushed inside a list where there are clients blocking, the behavior is different for Redis 2.4 and Redis 2.6 or newer.
	 * For Redis 2.6 what happens is that the command performing multiple pushes is executed, 
	 * and only after the execution of the command the blocked clients are served. Consider this sequence of commands.
	 * <pre>
	 * Client A:   BLPOP foo 0
	 * Client B:   LPUSH foo a b c
	 * </pre>
	 * If the above condition happens using a Redis 2.6 server or greater, Client A will be served with the c element, 
	 * because after the LPUSH command the list contains c,b,a, so taking an element from the left means to return c.
	 * Instead Redis 2.4 works in a different way: clients are served in the context of the push operation, so as long as 
	 * LPUSH foo a b c starts pushing the first element to the list, it will be delivered to the Client A, that will receive a (the first element pushed).
	 * The behavior of Redis 2.4 creates a lot of problems when replicating or persisting data into the AOF file, 
	 * so the much more generic and semantically simpler behaviour was introduced into Redis 2.6 to prevent problems.
	 * Note that for the same reason a Lua script or a MULTI/EXEC block may push elements into a list and afterward delete the list. 
	 * In this case the blocked clients will not be served at all and will continue to be blocked as long as no data is 
	 * present on the list after the execution of a single command, transaction, or script.
	 * 
	 * <p>
	 * BLPOP inside a MULTI / EXEC transaction
	 * BLPOP can be used with pipelining (sending multiple commands and reading the replies in batch), 
	 * however this setup makes sense almost solely when it is the last command of the pipeline.
	 * Using BLPOP inside a MULTI / EXEC block does not make a lot of sense as it would require blocking the entire 
	 * server in order to execute the block atomically, which in turn does not allow other clients to perform a push operation. 
	 * For this reason the behavior of BLPOP inside MULTI / EXEC when the list is empty is to return a nil multi-bulk reply, 
	 * which is the same thing that happens when the timeout is reached.
	 * If you like science fiction, think of time flowing at infinite speed inside a MULTI / EXEC block...
	 * 
	 * @param key
	 * @return A null when no element could be popped and the timeout expired.
	 *         A popped element.
	 */
	String blpop(String key);
	byte[] blpop(byte[] key);
	String blpop(String key, int timeout);
	byte[] blpop(byte[] key, int timeout);
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * BRPOP is a blocking list pop primitive. It is the blocking version of RPOP because it blocks the connection 
	 * when there are no elements to pop from any of the given lists. An element is popped from the tail of the first list that is non-empty, 
	 * with the given keys being checked in the order that they are given.
	 * See the BLPOP documentation for the exact semantics, since BRPOP is identical to BLPOP with the only difference being that 
	 * it pops elements from the tail of a list instead of popping from the head.
	 * 
	 * @see #blpop(String)
	 * @param key
	 * @return A null when no element could be popped and the timeout expired.
	 *         A popped element.
	 */
	String brpop(String key);
	byte[] brpop(byte[] key);
	String brpop(String key, int timeout);
	byte[] brpop(byte[] key, int timeout);
	
	/**
	 * Available since 2.2.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * BRPOPLPUSH is the blocking variant of RPOPLPUSH. When source contains elements, this command behaves exactly like RPOPLPUSH. 
	 * When source is empty, Redis will block the connection until another client pushes to it or until timeout is reached. 
	 * A timeout of zero can be used to block infinitely.
	 * See RPOPLPUSH for more information.
	 * 
	 * @param source
	 * @param destination
	 * @param timeout
	 * @return the element being popped from source and pushed to destination. 
	 * 		   If timeout is reached, a null reply is returned.
	 */
	String brpoplpush(String source, String destination, int timeout);
	byte[] brpoplpush(byte[] source, byte[] destination, int timeout);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(N) where N is the number of elements to traverse to get to the element at index. 
	 * This makes asking for the first or the last element of the list O(1).
	 * 
	 * <p>
	 * Returns the element at index index in the list stored at key. The index is zero-based, so 0 means the first element, 
	 * 1 the second element and so on. Negative indices can be used to designate elements starting at the tail of the list. 
	 * Here, -1 means the last element, -2 means the penultimate and so forth.
	 * When the value at key is not a list, an error is returned.
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	String lindex(String key, long index);
	byte[] lindex(byte[] key, long index);
	
	/**
	 * Available since 2.2.0
	 * Time complexity: O(N) where N is the number of elements to traverse before seeing the value pivot. T
	 * his means that inserting somewhere on the left end on the list (head) can be considered O(1) 
	 * and inserting somewhere on the right end (tail) is O(N).
	 * 
	 * <p>
	 * Inserts value in the list stored at key either before or after the reference value pivot.
	 * When key does not exist, it is considered an empty list and no operation is performed.
	 * When pivot can not be found and no operation is performed.
	 * An error is returned when key exists but does not hold a list value.
	 * 
	 * @param key
	 * @param where
	 * @param pivot
	 * @param value
	 * @return the length of the list after the insert operation, or -1 when the value pivot was not found.
	 */
	long linsertbefore(String key, String pivot, String value);
	long linsertbefore(byte[] key, byte[] pivot, byte[] value);
	long linsertafter(String key, String pivot, String value);
	long linsertafter(byte[] key, byte[] pivot, byte[] value);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Returns the length of the list stored at key. If key does not exist, it is interpreted as an empty list and 0 is returned. 
	 * An error is returned when the value stored at key is not a list.
	 * 
	 * @param key
	 * @return the length of the list at key.
	 */
	long llen(String key);
	long llen(byte[] key);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Removes and returns the first element of the list stored at key.
	 * 
	 * @param key
	 * @return the value of the first element, or null when key does not exist.
	 */
	String lpop(String key);
	byte[] lpop(byte[] key);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Insert all the specified values at the head of the list stored at key. If key does not exist, 
	 * it is created as empty list before performing the push operations. When key holds a value that is not a list, an error is returned.
	 * It is possible to push multiple elements using a single command call just specifying multiple arguments at the end of the command. 
	 * Elements are inserted one after the other to the head of the list, from the leftmost element to the rightmost element. 
	 * So for instance the command LPUSH mylist a b c will result into a list containing c as first element, b as second element and a as third element.
	 * 
	 * <p>
	 * History
	 * >= 2.4: Accepts multiple value arguments. In Redis versions older than 2.4 it was possible to push a single value per command.
	 * 
	 * @param key
	 * @param values
	 * @return the length of the list after the push operations.
	 */
	long lpush(String key, String... values);
	long lpush(byte[] key, byte[]... values);
	
	/**
	 * Available since 2.2.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Inserts value at the head of the list stored at key, only if key already exists and holds a list. 
	 * In contrary to LPUSH, no operation will be performed when key does not yet exist.
	 * 
	 * @param key
	 * @param value
	 * @return the length of the list after the push operation.
	 */
	long lpushx(String key, String value);
	long lpushx(byte[] key, byte[] value);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(S+N) where S is the start offset and N is the number of elements in the specified range.
	 * 
	 * <p>
	 * Returns the specified elements of the list stored at key. The offsets start and stop are zero-based indexes,
	 * with 0 being the first element of the list (the head of the list), 1 being the next element and so on.
	 * These offsets can also be negative numbers indicating offsets starting at the end of the list. 
	 * For example, -1 is the last element of the list, -2 the penultimate, and so on.
	 * 
	 * <p>
	 * Consistency with range functions in various programming languages
	 * Note that if you have a list of numbers from 0 to 100, LRANGE list 0 10 will return 11 elements, that is, the rightmost item is included. 
	 * This may or may not be consistent with behavior of range-related functions in your programming language of choice 
	 * (think Ruby's Range.new, Array#slice or Python's range() function).
	 * 
	 * <p>
	 * Out-of-range indexes
	 * Out of range indexes will not produce an error. If start is larger than the end of the list, an empty list is returned. 
	 * If stop is larger than the actual end of the list, Redis will treat it like the last element of the list.
	 * 
	 * @param key
	 * @param start
	 * @param stop
	 * @return list of elements in the specified range.
	 */
	List<String> lrange(String key, long start, long stop);
	List<byte[]> lrange(byte[] key, long start, long stop);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(N) where N is the length of the list.
	 * 
	 * <p>
	 * Removes the first count occurrences of elements equal to value from the list stored at key. 
	 * The count argument influences the operation in the following ways:
	 * count > 0: Remove elements equal to value moving from head to tail.
	 * count < 0: Remove elements equal to value moving from tail to head.
	 * count = 0: Remove all elements equal to value.
	 * For example, LREM list -2 "hello" will remove the last two occurrences of "hello" in the list stored at list.
	 * Note that non-existing keys are treated like empty lists, so when key does not exist, the command will always return 0.
	 * 
	 * @param key
	 * @param count
	 * @param value
	 * @return the number of removed elements.
	 */
	long lrem(String key, long count, String value);
	long lrem(byte[] key, long count, byte[] value);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(N) where N is the length of the list. Setting either the first or the last element of the list is O(1).
	 * 
	 * <p>
	 * Sets the list element at index to value. For more information on the index argument, see LINDEX.
	 * An error is returned for out of range indexes.
	 * 
	 * @param key
	 * @param index
	 * @param value
	 * @return Status code reply, e.g. OK
	 */
	String lset(String key, long index, String value);
	byte[] lset(byte[] key, long index, byte[] value);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(N) where N is the number of elements to be removed by the operation.
	 * Trim an existing list so that it will contain only the specified range of elements specified. 
	 * Both start and stop are zero-based indexes, where 0 is the first element of the list (the head), 1 the next element and so on.
	 * For example: LTRIM foobar 0 2 will modify the list stored at foobar so that only the first three elements of the list will remain.
	 * start and end can also be negative numbers indicating offsets from the end of the list, where -1 is the last element of the list, 
	 * -2 the penultimate element and so on.
	 * Out of range indexes will not produce an error: if start is larger than the end of the list, or start > end, 
	 * the result will be an empty list (which causes key to be removed). 
	 * If end is larger than the end of the list, Redis will treat it like the last element of the list.
	 * A common use of LTRIM is together with LPUSH / RPUSH. For example:
	 * <pre>
	 * LPUSH mylist someelement
	 * LTRIM mylist 0 99
	 * </pre>
	 * This pair of commands will push a new element on the list, while making sure that the list will not grow larger than 100 elements. 
	 * This is very useful when using Redis to store logs for example. 
	 * It is important to note that when used in this way LTRIM is an O(1) operation because in the average case just one element 
	 * is removed from the tail of the list.
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return Status code reply, e.g. OK
	 */
	String ltrim(String key, long start, long stop);
	byte[] ltrim(byte[] key, long start, long stop);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Removes and returns the last element of the list stored at key.
	 * 
	 * @param key
	 * @return the value of the last element, or null when key does not exist.
	 */
	String rpop(String key);
	byte[] rpop(byte[] key);
	
	/**
	 * Available since 1.2.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Atomically returns and removes the last element (tail) of the list stored at source, 
	 * and pushes the element at the first element (head) of the list stored at destination.
	 * For example: consider source holding the list a,b,c, and destination holding the list x,y,z. 
	 * Executing RPOPLPUSH results in source holding a,b and destination holding c,x,y,z.
	 * If source does not exist, the value null is returned and no operation is performed. 
	 * If source and destination are the same, the operation is equivalent to removing the last element from the list and 
	 * pushing it as first element of the list, so it can be considered as a list rotation command.
	 * 
	 * @param source
	 * @param destination
	 * @return the element being popped and pushed.
	 */
	String rpoplpush(String source, String destination); 
	String rpoplpush(byte[] source, byte[] destination); 
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Insert all the specified values at the tail of the list stored at key. 
	 * If key does not exist, it is created as empty list before performing the push operation. 
	 * When key holds a value that is not a list, an error is returned.
	 * It is possible to push multiple elements using a single command call just specifying multiple arguments at the end of the command. 
	 * Elements are inserted one after the other to the tail of the list, from the leftmost element to the rightmost element. 
	 * So for instance the command RPUSH mylist a b c will result into a list containing a as first element, b as second element and c as third element.
	 * 
	 * <p>
	 * History
	 * >= 2.4: Accepts multiple value arguments. In Redis versions older than 2.4 it was possible to push a single value per command.
	 * 
	 * @param key
	 * @param values
	 * @return the length of the list after the push operation.
	 */
	long rpush(String key, String... values);
	long rpush(byte[] key, byte[]... values);
	
	/**
	 * Available since 2.2.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Inserts value at the tail of the list stored at key, only if key already exists and holds a list. 
	 * In contrary to RPUSH, no operation will be performed when key does not yet exist.
	 * 
	 * @param key
	 * @param value
	 * @return the length of the list after the push operation.
	 */
	long rpushx(String key, String value);
	long rpushx(byte[] key, byte[] value);
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Sets
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(N) where N is the number of members to be added.
	 * 
	 * <p>
	 * Add the specified members to the set stored at key. Specified members that are already a member of this set are ignored. 
	 * If key does not exist, a new set is created before adding the specified members.
	 * An error is returned when the value stored at key is not a set.
	 * 
	 * <p>
	 * History
	 * >= 2.4: Accepts multiple member arguments. Redis versions before 2.4 are only able to add a single member per call.
	 * 
	 * @param key
	 * @param members
	 * @return the number of elements that were added to the set, not including all the elements already present into the set.
	 */
	long sadd(String key, String... members);
	long sadd(byte[] key, byte[]... members);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Returns the set cardinality (number of elements) of the set stored at key.
	 * 
	 * @param key
	 * @return the cardinality (number of elements) of the set, or 0 if key does not exist.
	 */
	long scard(String key);
	long scard(byte[] key);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Returns if member is a member of the set stored at key.
	 * 
	 * @param key
	 * @param member
	 * @return true if the element is a member of the set.
	 *         false if the element is not a member of the set, or if key does not exist.
	 */
	boolean sismember(String key, String member);
	boolean sismember(byte[] key, byte[] member);
	
	/**
	 * Available since 1.0.0.
	 * Time complexity: O(N) where N is the set cardinality.
	 * 
	 * <p>
	 * Returns all the members of the set value stored at key.
	 * This has the same effect as running SINTER with one argument key.
	 * 
	 * @param key
	 * @return 
	 */
	Set<String> smembers(String key);
	Set<byte[]> smembers(byte[] key);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Removes and returns a random element from the set value stored at key.
	 * This operation is similar to SRANDMEMBER, that returns a random element from a set but does not remove it.
	 * 
	 * @param key
	 * @return the removed element, or null when key does not exist.
	 */
	String spop(String key);
	byte[] spop(byte[] key);
	
	/**
	 * Available since 2.6.0
	 * Time complexity: O(N) where N is the absolute value of the passed count.
	 * 
	 * <p>
	 * When called with just the key argument, return a random element from the set value stored at key.
	 * Starting from Redis version 2.6, when called with the additional count argument, 
	 * return an array of count distinct elements if count is positive. 
	 * If called with a negative count the behavior changes and the command is allowed to return the same element multiple times. 
	 * In this case the numer of returned elements is the absolute value of the specified count.
	 * When called with just the key argument, the operation is similar to SPOP, however while SPOP also removes the randomly selected element from the set, SRANDMEMBER will just return a random element without altering the original set in any way.

	 * @param key
	 * @return returns an set of elements, or an empty set when key does not exist.
	 */
	Set<String> srandmember(String key, int count);
	Set<byte[]> srandmember(byte[] key, int count);
	
	/**
	 * Available since 1.0.0.
	 * Time complexity: O(1)
	 * 
	 * @see {@link #srandmember(String, int)}
	 * @param key
	 * @return returns the randomly selected element, or null when key does not exist
	 */
	String srandmember(String key);
	byte[] srandmember(byte[] key);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(N) where N is the number of members to be removed.
	 * 
	 * <p>
	 * Remove the specified members from the set stored at key. Specified members that are not a member of this set are ignored. 
	 * If key does not exist, it is treated as an empty set and this command returns 0.
	 * An error is returned when the value stored at key is not a set.
	 * 
	 * <p>
	 * History
	 * >= 2.4: Accepts multiple member arguments. Redis versions older than 2.4 can only remove a set member per call.
	 * 
	 * @param key
	 * @param members
	 * @return the number of members that were removed from the set, not including non existing members.
	 */
	long srem(String key, String... members);
	long srem(byte[] key, byte[]... members);
}

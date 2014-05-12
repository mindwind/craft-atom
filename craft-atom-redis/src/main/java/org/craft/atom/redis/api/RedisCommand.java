package org.craft.atom.redis.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.craft.atom.redis.api.handler.RedisMonitorHandler;
import org.craft.atom.redis.api.handler.RedisPsubscribeHandler;
import org.craft.atom.redis.api.handler.RedisSubscribeHandler;


/**
 * All redis commands.
 * 
 * @author mindwind
 * @version 1.0, Jun 26, 2013
 */
public interface RedisCommand {
	
	
	// ~ --------------------------------------------------------------------------------------------------------- Keys

	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(N) where N is the number of keys that will be removed. <br>
	 * 
	 * <p>
	 * When a key to remove holds a value other than a string, 
	 * the individual complexity for this key is O(M) 
	 * where M is the number of elements in the list, set, sorted set or hash.<br>
	 * Removing a single key that holds a string value is O(1).<br>
	 * <p>
	 * 
	 * Removes the specified keys. A key is ignored if it does not exist.
	 * <p>
	 * 
	 * @param keys
	 * @return The number of keys that were removed.
	 */
	Long del(String... keys);
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: O(1) to access the key and additional O(N*M) to serialized it, 
	 * where N is the number of Redis objects composing the value and M their average size. <br>
	 * For small string values the time complexity is thus O(1)+O(1*M) where M is small, so simply O(1).<br>
	 * 
	 * <p>
	 * Serialize the value stored at key in a Redis-specific format and return it to the user. 
	 * The returned value can be synthesized back into a Redis key using the RESTORE command.<br>
	 * <p>
	 * The serialization format is opaque and non-standard, however it has a few semantical characteristics:<br>
	 * - It contains a 64-bit checksum that is used to make sure errors will be detected.
	 *   The RESTORE command makes sure to check the checksum before synthesizing a key using the serialized value.<br>
	 * - Values are encoded in the same format used by RDB.<br>
     * - An RDB version is encoded inside the serialized value, 
     *   so that different Redis versions with incompatible RDB formats will refuse to process the serialized value.<br> 
     * <p>
     * The serialized value does NOT contain expire information.<br> 
     * In order to capture the time to live of the current value the PTTL command should be used.<br>
     * <p>
     * If key does not exist a null bulk reply is returned.<br>
	 * 
	 * @param key
	 * @return the serialized value
	 */
	byte[] dump(String key);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Returns <tt>true</tt>if key exists.<br>
	 * 
	 * @param key
	 * @return true if the key exists.<br>
	 *         false if the key does not exist.<br>
	 */
	Boolean exists(String key);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Set a timeout on key. After the timeout has expired, the key will automatically be deleted. <br>
	 * A key with an associated timeout is often said to be volatile in Redis terminology.<br>
	 * <p>
	 * The timeout is cleared only when the key is removed using the DEL command or 
	 * overwritten using the SET or GETSET commands. <br>
	 * This means that all the operations that conceptually alter the value stored at the key 
	 * without replacing it with a new one will leave the timeout untouched. <br>
	 * For instance, incrementing the value of a key with INCR, pushing a new value into a list with LPUSH, 
	 * or altering the field value of a hash with HSET are all operations that will leave the timeout untouched.<br>
	 * <p>
	 * The timeout can also be cleared, turning the key back into a persistent key, using the PERSIST command.<br>
	 * If a key is renamed with RENAME, the associated time to live is transferred to the new key name.<br>
	 * If a key is overwritten by RENAME, like in the case of an existing key Key_A that is overwritten 
	 * by a call like RENAME Key_B Key_A, it does not matter if the original Key_A had a timeout associated or not, 
	 * the new key Key_A will inherit all the characteristics of Key_B.<br>
	 * <p>
	 * It is possible to call EXPIRE using as argument a key that already has an existing expire set. <br>
	 * In this case the time to live of a key is updated to the new value. There are many useful applications for this.<br>
	 * 
	 * @param key
	 * @param seconds
	 * @return 1 if the timeout was set.<br>
     *         0 if key does not exist or the timeout could not be set.<br>
	 */
	Long expire(String key, int seconds);
	
	/**
	 * Available since 1.2.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * EXPIREAT has the same effect and semantic as EXPIRE, but instead of specifying the number of seconds representing 
	 * the TTL (time to live), it takes an absolute Unix timestamp (seconds since January 1, 1970).<br>
	 * <p>
	 * EXPIREAT was introduced in order to convert relative timeouts to absolute timeouts for the AOF persistence mode.<br> 
	 * Of course, it can be used directly to specify that a given key should expire at a given time in the future.<br>
	 * 
	 * @param key
	 * @param timestamp a unix timestamp
	 * @return 1 if the timeout was set.<br>
	 *         0 if key does not exist or the timeout could not be set .<br>
	 */
	Long expireat(String key, long timestamp);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(N) with N being the number of keys in the database, 
	 * under the assumption that the key names in the database and the given pattern have limited length.<br>
	 * 
	 * <p>
	 * Returns all keys matching pattern.<br>
	 * While the time complexity for this operation is O(N), the constant times are fairly low. <br>
	 * For example, Redis running on an entry level laptop can scan a 1 million key database in 40 milliseconds.<br>
	 * Warning: consider KEYS as a command that should only be used in production environments with extreme care. <br>
	 * It may ruin performance when it is executed against large databases. <br>
	 * This command is intended for debugging and special operations, such as changing your keyspace layout. <br>
	 * Don't use KEYS in your regular application code. If you're looking for a way to find keys in a subset of your keyspace, consider using sets.<br>
	 * Supported glob-style patterns:
	 * <pre>
	 * h?llo matches hello, hallo and hxllo
	 * h*llo matches hllo and heeeello
	 * h[ae]llo matches hello and hallo, but not hillo
	 * Use \ to escape special characters if you want to match them verbatim.
	 * </pre>
	 * 
	 * @param pattern
	 * @return ist of keys matching pattern.<br>
	 */
	Set<String> keys(String pattern);
	
	/**
	 * Available since 2.6.0 <br>
	 * Time complexity: This command actually executes a DUMP+DEL in the source instance, and a RESTORE in the target instance. <br>
	 * See the pages of these commands for time complexity. Also an O(N) data transfer between the two instances is performed.<br>
	 * 
	 * <p>
	 * Atomically transfer a key from a source Redis instance to a destination Redis instance. <br>
	 * On success the key is deleted from the original instance and is guaranteed to exist in the target instance.<br>
	 * The command is atomic and blocks the two instances for the time required to transfer the key, 
	 * at any given time the key will appear to exist in a given instance or in the other instance, unless a timeout error occurs.<br>
	 * 
	 * The command internally uses DUMP to generate the serialized version of the key value, and RESTORE in order 
	 * to synthesize the key in the target instance. The source instance acts as a client for the target instance.<br>
	 * If the target instance returns OK to the RESTORE command, the source instance deletes the key using DEL.<br>
	 * 
	 * The timeout specifies the maximum idle time in any moment of the communication with the destination instance in milliseconds.<br> 
	 * This means that the operation does not need to be completed within the specified amount of milliseconds, 
	 * but that the transfer should make progresses without blocking for more than the specified amount of milliseconds.<br>
	 * 
	 * MIGRATE needs to perform I/O operations and to honor the specified timeout. <br>
	 * When there is an I/O error during the transfer or if the timeout is reached the operation is aborted and the special error - IOERR returned.<br> 
	 * When this happens the following two cases are possible:
	 * - The key may be on both the instances.<br>
	 * - The key may be only in the source instance.<br>
	 * It is not possible for the key to get lost in the event of a timeout, but the client calling MIGRATE, 
	 * in the event of a timeout error, should check if the key is also present in the target instance and act accordingly.<br>
	 * When any other error is returned (starting with ERR) MIGRATE guarantees that the key is still only present in the 
	 * originating instance (unless a key with the same name was also already present on the target instance).<br>
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
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Move key from the currently selected database (see SELECT) to the specified destination database. <br>
	 * When key already exists in the destination database, or it does not exist in the source database, it does nothing. <br>
	 * It is possible to use MOVE as a locking primitive because of this.<br>
	 * 
	 * @param key
	 * @param db
	 * @return 1 if key was moved.<br>
	 *         0 if key was not moved.<br>
	 */
	Long move(String key, int db);
	
	/**
	 * Available since 2.2.3<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * The OBJECT command allows to inspect the internals of Redis Objects associated with keys. <br>
	 * It is useful for debugging or to understand if your keys are using the specially encoded data types to save space. <br>
	 * Your application may also use the information reported by the OBJECT command to implement application level 
	 * key eviction policies when using Redis as a Cache.<br>
	 * <p>
	 * The OBJECT command supports multiple sub commands:
	 * OBJECT REFCOUNT key 
	 *   returns the number of references of the value associated with the specified key. <br>
	 *   This command is mainly useful for debugging.<br>
	 *   
	 * OBJECT ENCODING key 
	 *   returns the kind of internal representation used in order to store the value associated with a key.<br>
	 *   
	 * OBJECT IDLETIME key 
	 *   returns the number of seconds since the object stored at the specified key is idle (not requested by read or write operations). <br>
	 *   While the value is returned in seconds the actual resolution of this timer is 10 seconds, but may vary in future implementations.<br>
	 * 
	 * <p>
	 * Objects can be encoded in different ways:
	 * - Strings can be encoded as raw (normal string encoding) or int (strings representing integers in a 64 bit signed 
	 *   interval are encoded in this way in order to save space).<br>
     * - Lists can be encoded as ziplist or linkedlist. The ziplist is the special representation that is used to save space for small lists.<br>
	 * - Sets can be encoded as intset or hashtable. The intset is a special encoding used for small sets composed solely of integers.<br>
     * - Hashes can be encoded as zipmap or hashtable. The zipmap is a special encoding used for small hashes.<br>
     * - Sorted Sets can be encoded as ziplist or skiplist format. As for the List type small sorted sets can be specially 
     * - encoded using ziplist, while the skiplist encoding is the one that works with sorted sets of any size.<br>
     * 
     * <p>
	 * All the specially encoded types are automatically converted to the general type once you perform an operation 
	 * that makes it no possible for Redis to retain the space saving encoding.<br>
	 * 
	 * @param key
	 * @return Different return values are used for different subcommands.<br>
	 *         Subcommands refcount and idletime returns integers.<br>
	 *         Subcommand encoding returns a bulk reply.<br>
	 *         If the object you try to inspect is missing, a null bulk reply is returned.
	 */
	Long objectrefcount(String key);
	String objectencoding(String key);
	Long objectidletime(String key);
	
	/**
	 * Available since 2.2.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Remove the existing timeout on key, turning the key from volatile (a key with an expire set) 
	 * to persistent (a key that will never expire as no timeout is associated).<br>
	 * 
	 * @param key
	 * @return 1 if the timeout was removed.<br>
	 *         0 if key does not exist or does not have an associated timeout.<br>
	 */
	Long persist(String key);
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * This command works exactly like EXPIRE but the time to live of the key is specified in milliseconds instead of seconds.<br>

	 * @param key
	 * @param milliseconds
	 * @return 1 if the timeout was set.<br>
	 *         0 if key does not exist or the timeout could not be set.<br>
	 */
	Long pexpire(String key, int milliseconds);
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * PEXPIREAT has the same effect and semantic as EXPIREAT, 
	 * but the Unix time at which the key will expire is specified in milliseconds instead of seconds.<br>
	 * 
	 * @param key
	 * @param millisecondstimestamp
	 * @return 1 if the timeout was set.<br>
	 *         0 if key does not exist or the timeout could not be set 
	 */
	Long pexpireat(String key, long millisecondstimestamp);
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Like TTL this command returns the remaining time to live of a key that has an expire set, 
	 * with the sole difference that TTL returns the amount of remaining time in seconds while PTTL returns it in milliseconds.<br>
	 * 
	 * @param key
	 * @return Time to live in milliseconds or -1 when key does not exist or does not have a timeout.
	 */
	Long pttl(String key);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Return a random key from the currently selected database.<br>
	 * 
	 * @return the random key, or nil when the database is empty.
	 */
	String randomkey();
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Renames key to newkey. <br>
	 * It returns an error when the source and destination names are the same, or when key does not exist. <br>
	 * If newkey already exists it is overwritten.<br>
	 * 
	 * @param key
	 * @param newkey
	 * @return The command returns OK on success.<br>
	 */
	String rename(String key, String newkey);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Renames key to newkey if newkey does not yet exist. It returns an error under the same conditions as RENAME.<br>
	 * 
	 * @param key
	 * @param newkey
	 * @return 1 if key was renamed to newkey.<br>
	 *         0 if newkey already exists.<br>
	 */
	Long renamenx(String key, String newkey);
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: O(1) to create the new key and additional O(N*M) to recostruct the serialized value, 
	 * where N is the number of Redis objects composing the value and M their average size. <br>
	 * For small string values the time complexity is thus O(1)+O(1*M) where M is small, so simply O(1).<br> 
	 * However for sorted set values the complexity is O(N*M*log(N)) because inserting values into sorted sets is O(log(N)).<br>
	 * 
	 * <p>
	 * Create a key associated with a value that is obtained by deserializing the provided serialized value (obtained via DUMP).<br>
	 * If ttl is 0 the key is created without any expire, otherwise the specified expire time (in milliseconds) is set.<br>
	 * RESTORE checks the RDB version and data checksum. If they don't match an error is returned.<br>
	 * 
	 * @param key
	 * @param ttl in milliseconds
	 * @param serializedvalue
	 * @return The command returns OK on success.
	 */
	String restore(String key, int ttl, byte[] serializedvalue);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements.<br> 
	 * When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.<br>
	 * 
	 * <p>
	 * Returns or stores the elements contained in the list, set or sorted set at key. <br>
	 * By default, sorting is numeric and elements are compared by their value interpreted as double precision floating point number. <br>
	 * This is SORT in its simplest form:
	 * <pre>
	 * SORT mylist
	 * </pre>
	 * 
	 * Assuming mylist is a list of numbers, this command will return the same list with the elements sorted from small to large. <br>
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
	 * Redis is UTF-8 aware, assuming you correctly set the !LC_COLLATE environment variable.<br>
	 * The number of returned elements can be limited using the LIMIT modifier. <br>
	 * This modifier takes the offset argument, specifying the number of elements to skip and the count argument, 
	 * specifying the number of elements to return from starting at offset. <br>
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
	 * Sometimes you want to sort elements using external keys as weights to compare instead of comparing the actual elements in the list, set or sorted set. <br>
	 * Let's say the list mylist contains the elements 1, 2 and 3 representing unique IDs of objects stored in object_1, object_2 and object_3. <br>
	 * When these objects have associated weights stored in weight_1, weight_2 and weight_3, 
	 * SORT can be instructed to use these weights to sort mylist with the following statement:
	 * <pre>
	 * SORT mylist BY weight_*
	 * </pre>
	 * The BY option takes a pattern (equal to weight_* in this example) that is used to generate the keys that are used for sorting. <br>
	 * These key names are obtained substituting the first occurrence of * with the actual value of the element in the list (1, 2 and 3 in this example).<br>
	 * <p>
	 * 
	 * The BY option can also take a non-existent key, which causes SORT to skip the sorting operation. <br>
	 * This is useful if you want to retrieve external keys (see the GET option below) without the overhead of sorting.
	 * <pre>
	 * SORT mylist BY nosort
	 * </pre>
	 * 
	 * Our previous example returns just the sorted IDs. In some cases, it is more useful to get the actual objects 
	 * instead of their IDs (object_1, object_2 and object_3). <br>
	 * Retrieving external keys based on the elements in a list, 
	 * set or sorted set can be done with the following command:
	 * <pre>
	 * SORT mylist BY weight_* GET object_*
	 * </pre>
	 * 
	 * The GET option can be used multiple times in order to get more keys for every element of the original list, set or sorted set.<br>
	 * It is also possible to GET the element itself using the special pattern #:
	 * <pre>
     * SORT mylist BY weight_* GET object_* GET #
     * </pre>
     * 
     * By default, SORT returns the sorted elements to the client. With the STORE option, the result will be stored as 
     * a list at the specified key instead of being returned to the client.<br>
	 * <pre>
	 * SORT mylist BY weight_* STORE resultkey
	 * </pre>
	 * An interesting pattern using SORT ... STORE consists in associating an EXPIRE timeout to the resulting key 
	 * so that in applications where the result of a SORT operation can be cached for some time. <br>
	 * Other clients will use the cached list instead of calling SORT for every request. When the key will timeout, 
	 * an updated version of the cache can be created by calling SORT ... STORE again.<br>
	 * Note that for correctly implementing this pattern it is important to avoid multiple clients rebuilding the cache 
	 * at the same time. Some kind of locking is needed here (for instance using SETNX).<br>
	 * <p>
	 * 
	 * It is possible to use BY and GET options against hash fields with the following syntax:
	 * <pre>
	 * SORT mylist BY weight_*->fieldname GET object_*->fieldname
	 * </pre>
	 * The string -> is used to separate the key name from the hash field name. <br>
	 * The key is substituted as documented above, and the hash stored at the resulting key is accessed to retrieve the specified hash field.<br>
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
	 * SORT mylist STORE resultkey
	 * SORT mylist DESC STORE resultkey
	 * SORT mylist ALPHA STORE resultkey
	 * SORT mylist ALPHA DESC STORE resultkey
	 * SORT mylist LIMIT 0 10 STORE resultkey
	 * SORT mylist LIMIT 0 10 ALPHA DESC STORE resultkey
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
	
	/**
	 * @see #sort(String)
	 * @return The number of elements of the list at destination.
	 */
	Long sort(String key, String destination);
	Long sort(String key, boolean desc, String destination);
	Long sort(String key, boolean alpha, boolean desc, String destination);
	Long sort(String key, int offset, int count, String destination);
	Long sort(String key, int offset, int count, boolean alpha, boolean desc, String destination);
	Long sort(String key, String bypattern, String destination, String... getpatterns);
	Long sort(String key, String bypattern, boolean desc, String destination, String... getpatterns);
	Long sort(String key, String bypattern, boolean alpha, boolean desc, String destination, String... getpatterns);
	Long sort(String key, String bypattern, int offset, int count, String destination, String... getpatterns);
	Long sort(String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String destination, String... getpatterns);

	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Returns the remaining time to live of a key that has a timeout. <br>
	 * This introspection capability allows a Redis client to check how many seconds a given key will continue to be part of the dataset.<br>
	 * 
	 * <p>
	 * >=2.6, return -1 when key does not have a timeout or not exist
	 * 
	 * @param key
	 * @return TTL in seconds, -2 when key does not exist or -1 when key does not have a timeout.
	 */
	Long ttl(String key);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Returns the string representation of the type of the value stored at key. <br>
	 * The different types that can be returned are: string, list, set, zset and hash.<br>
	 * 
	 * @param key
	 * @return type of key, or none when key does not exist.
	 */
	String type(String key);
	
	/**
	 * Available since 2.8.0<br>
	 * Time complexity: O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor 
	 * to return back to 0. N is the number of elements inside the collection..<br>
	 * 
	 * <p>
	 * The SCAN command and the closely related commands SSCAN, HSCAN and ZSCAN are used in order to incrementally iterate over a collection of elements.
     * SCAN iterates the set of keys in the currently selected Redis database.
     * SSCAN iterates elements of Sets types.
     * HSCAN iterates fields of Hash types and their associated values.
     * ZSCAN iterates elements of Sorted Set types and their associated scores.
     * Since these commands allow for incremental iteration, returning only a small number of elements per call, they can be used in production without the downside of commands like KEYS or SMEMBERS that may block the server for a long time (even several seconds) when called against big collections of keys or elements.
     * However while blocking commands like SMEMBERS are able to provide all the elements that are part of a Set in a given moment, The SCAN family of commands only offer limited guarantees about the returned elements since the collection that we incrementally iterate can change during the iteration process.
     * Note that SCAN, SSCAN, HSCAN and ZSCAN all work very similarly, so this documentation covers all the four commands. However an obvious difference is that in the case of SSCAN, HSCAN and ZSCAN the first argument is the name of the key holding the Set, Hash or Sorted Set value. The SCAN command does not need any key name argument as it iterates keys in the current database, so the iterated object is the database itself.
	 * 
	 * <p>
	 * <b>SCAN basic usage</b><br>
     * SCAN is a cursor based iterator. This means that at every call of the command, the server returns an updated cursor that the user needs to use as the cursor argument in the next call.
     * An iteration starts when the cursor is set to 0, and terminates when the cursor returned by the server is 0. The following is an example of SCAN iteration:
	 * <pre><code>
	 * redis 127.0.0.1:6379> scan 0
     * 1) "17"
     * 2)  1) "key:12"
     *     2) "key:8"
     *     3) "key:4"
     *     4) "key:14"
     *     5) "key:16"
     *     6) "key:17"
     *     7) "key:15"
     *     8) "key:10"
     *     9) "key:3"
     *    10) "key:7"
     *    11) "key:1"
     * redis 127.0.0.1:6379> scan 17
     * 1) "0"
     * 2) 1) "key:5"
     *    2) "key:18"
     *    3) "key:0"
     *    4) "key:2"
     *    5) "key:19"
     *    6) "key:13"
     *    7) "key:6"
     *    8) "key:9"
     *    9) "key:11"
	 * </code></pre>
	 * In the example above, the first call uses zero as a cursor, to start the iteration. The second call uses the cursor returned by the previous call as the first element of the reply, that is, 17.
     * As you can see the SCAN return value is an array of two values: the first value is the new cursor to use in the next call, the second value is an array of elements.
     * Since in the second call the returned cursor is 0, the server signaled to the caller that the iteration finished, and the collection was completely explored. Starting an iteration with a cursor value of 0, and calling SCAN until the returned cursor is 0 again is called a full iteration.
	 * 
	 * <p>
	 * <b>Scan guarantees</b><br>
     * The SCAN command, and the other commands in the SCAN family, are able to provide to the user a set of guarantees associated to full iterations.
     * A full iteration always retrieves all the elements that were present in the collection from the start to the end of a full iteration. This means that if a given element is inside the collection when an iteration is started, and is still there when an iteration terminates, then at some point SCAN returned it to the user.
     * A full iteration never returns any element that was NOT present in the collection from the start to the end of a full iteration. So if an element was removed before the start of an iteration, and is never added back to the collection for all the time an iteration lasts, SCAN ensures that this element will never be returned.
     * However because SCAN has very little state associated (just the cursor) it has the following drawbacks:
     * A given element may be returned multiple times. It is up to the application to handle the case of duplicated elements, for example only using the returned elements in order to perform operations that are safe when re-applied multiple times.
     * Elements that were not constantly present in the collection during a full iteration, may be returned or not: it is undefined.
     * 
     * <p>
     * <b>Number of elements returned at every SCAN call</b><br>
     * SCAN family functions do not guarantee that the number of elements returned per call are in a given range. The commands are also allowed to return zero elements, and the client should not consider the iteration complete as long as the returned cursor is not zero.
     * However the number of returned elements is reasonable, that is, in practical terms SCAN may return a maximum number of elements in the order of a few tens of elements when iterating a large collection, or may return all the elements of the collection in a single call when the iterated collection is small enough to be internally represented as an encoded data structure (this happens for small sets, hashes and sorted sets).
     * However there is a way for the user to tune the order of magnitude of the number of returned elements per call using the COUNT option.
     * 
     * <p>
     * <b>The COUNT option</b><br>
     * While SCAN does not provide guarantees about the number of elements returned at every iteration, it is possible to empirically adjust the behavior of SCAN using the COUNT option. Basically with COUNT the user specified the amount of work that should be done at every call in order to retrieve elements from the collection. This is just an hint for the implementation, however generally speaking this is what you could expect most of the times from the implementation.
     * The default COUNT value is 10.
     * When iterating the key space, or a Set, Hash or Sorted Set that is big enough to be represented by an hash table, assuming no MATCH option is used, the server will usually return count or a bit more than count elements per call.
     * When iterating Sets encoded as intsets (small sets composed of just integers), or Hashes and Sorted Sets encoded as ziplists (small hashes and sets composed of small individual values), usually all the elements are returned in the first SCAN call regardless of the COUNT value.
     * Important: there is no need to use the same COUNT value for every iteration. The caller is free to change the count from one iteration to the other as required, as long as the cursor passed in the next call is the one obtained in the previous call to the command.
     * 
     * <p>
     * <b>The MATCH option</b><br>
     * It is possible to only iterate elements matching a given glob-style pattern, similarly to the behavior of the KEYS command that takes a pattern as only argument.
     * To do so, just append the MATCH <pattern> arguments at the end of the SCAN command (it works with all the SCAN family commands).
     * This is an example of iteration using MATCH:
     * 
     * <pre><code>
     * redis 127.0.0.1:6379> sadd myset 1 2 3 foo foobar feelsgood
     * (integer) 6
     * redis 127.0.0.1:6379> sscan myset 0 match f*
     * 1) "0"
     * 2) 1) "foo"
     *    2) "feelsgood"
     *    3) "foobar"
     * redis 127.0.0.1:6379>
     * </code></pre>
     * 
     * It is important to note that the MATCH filter is applied after elements are retrieved from the collection, just before returning data to the client. This means that if the pattern matches very little elements inside the collection, SCAN will likely return no elements in most iterations. An example is shown below:
     * <pre><code>
     * redis 127.0.0.1:6379> scan 0 MATCH *11*
     * 1) "288"
     * 2) 1) "key:911"
     * redis 127.0.0.1:6379> scan 288 MATCH *11*
     * 1) "224"
     * 2) (empty list or set)
     * redis 127.0.0.1:6379> scan 224 MATCH *11*
     * 1) "80"
     * 2) (empty list or set)
     * redis 127.0.0.1:6379> scan 80 MATCH *11*
     * 1) "176"
     * 2) (empty list or set)
     * redis 127.0.0.1:6379> scan 176 MATCH *11* COUNT 1000
     * 1) "0"
     * 2)  1) "key:611"
     *     2) "key:711"
     *     3) "key:118"
     *     4) "key:117"
     *     5) "key:311"
     *     6) "key:112"
     *     7) "key:111"
     *     8) "key:110"
     *     9) "key:113"
     *    10) "key:211"
     *    11) "key:411"
     *    12) "key:115"
     *    13) "key:116"
     *    14) "key:114"
     *    15) "key:119"
     *    16) "key:811"
     *    17) "key:511"
     *    18) "key:11"
     * redis 127.0.0.1:6379>
     * </code></pre>
     * As you can see most of the calls returned zero elements, but the last call where a COUNT of 1000 was used in order to force the command to do more scanning for that iteration.
     * 
     * <p>
     * <b>Multiple parallel iterations</b><br>
     * It is possible for an infinite number of clients to iterate the same collection at the same time, as the full state of the iterator is in the cursor, that is obtained and returned to the client at every call. Server side no state is taken at all.
	 * 
	 * <p>
	 * <b>Terminating iterations in the middle</b><br>
     * Since there is no state server side, but the full state is captured by the cursor, the caller is free to terminate an iteration half-way without signaling this to the server in any way. An infinite number of iterations can be started and never terminated without any issue.
	 * 
	 * <p>
	 * <b>Calling SCAN with a corrupted cursor</b><br>
     * Calling SCAN with a broken, negative, out of range, or otherwise invalid cursor, will result into undefined behavior but never into a crash. What will be undefined is that the guarantees about the returned elements can no longer be ensured by the SCAN implementation.
     * The only valid cursors to use are:
     * The cursor value of 0 when starting an iteration.
     * The cursor returned by the previous call to SCAN in order to continue the iteration.
     * 
     * <b>Guarantee of termination</b><br>
     * The SCAN algorithm is guaranteed to terminate only if the size of the iterated collection remains bounded to a given maximum size, otherwise iterating a collection that always grows may result into SCAN to never terminate a full iteration.
     * This is easy to see intuitively: if the collection grows there is more and more work to do in order to visit all the possible elements, and the ability to terminate the iteration depends on the number of calls to SCAN and its COUNT option value compared with the rate at which the collection grows.
     * 
     * <b>Return value</b><br>
     * SCAN, SSCAN, HSCAN and ZSCAN return a two elements multi-bulk reply, where the first element is a string representing an unsigned 64 bit number (the cursor), and the second element is a multi-bulk with an array of elements.
     * SCAN array of elements is a list of keys.
     * SSCAN array of elements is a list of Set members.
     * HSCAN array of elements contain two elements, a field and a value, for every returned element of the Hash.
     * ZSCAN array of elements contain two elements, a member and its associated score, for every returned element of the sorted set.
     * 
     * <b>Additional examples</b><br>
     * Iteration of an Hash value.
     * <pre><code>
     * redis 127.0.0.1:6379> hmset hash name Jack age 33
     * OK
     * redis 127.0.0.1:6379> hscan hash 0
     * 1) "0"
     * 2) 1) "name"
     *    2) "Jack"
     *    3) "age"
     *    4) "33"
     * </code></pre>
     * 
	 * @param cursor
	 * @return
	 */
	ScanResult<String> scan(String cursor);
	ScanResult<String> scan(String cursor, int count);
	ScanResult<String> scan(String cursor, String pattern);
	ScanResult<String> scan(String cursor, String pattern, int count);
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Strings
	
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(1). The amortized time complexity is O(1) assuming the appended value is small 
	 * and the already present value is of any size, since the dynamic string library used by Redis will double the 
	 * free space available on every reallocation.<br>
	 * 
	 * <p>
	 * If key already exists and is a string, this command appends the value at the end of the string. <br>
	 * If key does not exist it is created and set as an empty string, so APPEND will be similar to SET in this special case.<br>
	 * 
	 * @param key
	 * @param value
	 * @return the length of the string after the append operation.
	 */
	Long append(String key, String value);
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: O(N)<br>
	 * 
	 * <p>
	 * Count the number of set bits (population counting) in a string.<br>
	 * By default all the bytes contained in the string are examined. It is possible to specify the counting operation 
	 * only in an interval passing the additional arguments start and end.<br>
	 * Like for the GETRANGE command start and end can contain negative values in order to index bytes starting from the end of the string, 
	 * where -1 is the last byte, -2 is the penultimate, and so forth.<br>
	 * Non-existent keys are treated as empty strings, so the command will return zero.
	 * 
	 * @param key
	 * @param start inclusive
	 * @param end   inclusive
	 * @return The number of bits set to 1.
	 */
	Long bitcount(String key, long start, long end);
	Long bitcount(String key);
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: O(N)<br>
	 * 
	 * <p>
	 * Perform a bitwise operation between multiple keys (containing string values) and store the result in the destination key.<br>
	 * The BITOP command supports four bitwise operations: AND, OR, XOR and NOT, thus the valid forms to call the command are:
	 * <pre>
	 * BITOP AND destkey srckey1 srckey2 srckey3 ... srckeyN
	 * BITOP OR destkey srckey1 srckey2 srckey3 ... srckeyN
	 * BITOP XOR destkey srckey1 srckey2 srckey3 ... srckeyN
	 * BITOP NOT destkey srckey
	 * </pre>
	 * As you can see NOT is special as it only takes an input key, because it performs inversion of bits so it only makes sense as an unary operator.<br>
	 * The result of the operation is always stored at destkey.<br>
	 * <p>
	 * Handling of strings with different lengths<br>
	 * When an operation is performed between strings having different lengths, all the strings shorter than the longest string 
	 * in the set are treated as if they were zero-padded up to the length of the Longest string.<br>
	 * The same holds true for non-existent keys, that are considered as a stream of zero bytes up to the length of the longest string.<br>
	 * 
	 * @param destkey
	 * @param key
	 * @return The size of the string stored in the destination key, that is equal to the size of the longest input string.
	 */
	Long bitnot(String destkey, String key);
	Long bitand(String destkey, String... keys);
	Long bitor(String destkey, String... keys);
	Long bitxor(String destkey, String... keys);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Decrements the number stored at key by one. If the key does not exist, it is set to 0 before performing the operation. <br>
	 * An error is returned if the key contains a value of the wrong type or contains a string that can not be represented as integer. <br>
	 * This operation is limited to 64 bit signed integers.<br>
	 * See INCR for extra information on increment/decrement operations.<br>
	 * 
	 * @param key
	 * @return the value of key after the decrement
	 */
	Long decr(String key);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Decrements the number stored at key by decrement. If the key does not exist, it is set to 0 before performing the operation. <br>
	 * An error is returned if the key contains a value of the wrong type or contains a string that can not be represented as integer. <br>
	 * This operation is limited to 64 bit signed integers.<br>
	 * See INCR for extra information on increment/decrement operations.<br>
	 * 
	 * @param key
	 * @param decrement
	 * @return the value of key after the decrement
	 */
	Long decrby(String key, long decrement);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Get the value of key. If the key does not exist the special value null is returned. <br>
	 * An error is returned if the value stored at key is not a string, because GET only handles string values.<br>
	 * 
	 * @param key
	 * @return the value of key, or null when key does not exist.
	 */
	String get(String key);
	
	/**
	 * Available since 2.2.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Returns the bit value at offset in the string value stored at key.<br>
	 * When offset is beyond the string length, the string is assumed to be a contiguous space with 0 bits. <br>
	 * When key does not exist it is assumed to be an empty string, so offset is always out of range and the value is also 
	 * assumed to be a contiguous space with 0 bits.<br>
	 * 
	 * @param key
	 * @param offset
	 * @return the bit value stored at offset.
	 */
	Boolean getbit(String key, long offset);
	
	/**
	 * Available since 2.4.0<br>
	 * Time complexity: O(N) where N is the length of the returned string. 
	 * The complexity is ultimately determined by the returned length, 
	 * but because creating a substring from an existing string is very cheap, it can be considered O(1) for small strings.<br>
	 * 
	 * <p>
	 * Warning: this command was renamed to GETRANGE, it is called SUBSTR in Redis versions <= 2.0.<br>
	 * Returns the substring of the string value stored at key, determined by the offsets start and end (both are inclusive). <br>
	 * Negative offsets can be used in order to provide an offset starting from the end of the string. So -1 means the last character, 
	 * -2 the penultimate and so forth.<br>
	 * The function handles out of range requests by limiting the resulting range to the actual length of the string.<br>
	 * 
	 * @param key
	 * @param start inclusive
	 * @param end   inclusive
	 * @return a substring of the string stored at a key
	 */
	String getrange(String key, long start, long end);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Atomically sets key to value and returns the old value stored at key. <br>
	 * Returns an error when key exists but does not hold a string value.<br>
	 * 
	 * @param key
	 * @param value
	 * @return the old value stored at key, or null when key did not exist.
	 */
	String getset(String key, String value);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Increments the number stored at key by one. If the key does not exist, it is set to 0 before performing the operation. <br>
	 * An error is returned if the key contains a value of the wrong type or contains a string that can not be represented as integer. <br>
	 * This operation is limited to 64 bit signed integers.<br>
	 * 
	 * Note: this is a string operation because Redis does not have a dedicated integer type. <br>
	 * The string stored at the key is interpreted as a base-10 64 bit signed integer to execute the operation.<br>
	 * Redis stores integers in their integer representation, so for string values that actually hold an integer, 
	 * there is no overhead for storing the string representation of the integer.<br>
	 * 
	 * @param key
	 * @return the value of key after the increment
	 */
	Long incr(String key);
	
	/**
	 * available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Increments the number stored at key by increment. If the key does not exist, it is set to 0 before performing the operation.<br> 
	 * An error is returned if the key contains a value of the wrong type or contains a string that can not be represented as integer. <br>
	 * This operation is limited to 64 bit signed integers. <br>
	 * See INCR for extra information on increment/decrement operations.<br>
	 * 
	 * @param key
	 * @param increment
	 * @return  the value of key after the increment
	 */
	Long incrby(String key, long increment);
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Increment the string representing a floating point number stored at key by the specified increment. <br>
	 * If the key does not exist, it is set to 0 before performing the operation. <br>
	 * An error is returned if one of the following conditions occur:<br>
	 * - The key contains a value of the wrong type (not a string).<br>
	 * - The current key content or the specified increment are not parsable as a double precision floating point number.<br>
	 * 
	 * If the command is successful the new incremented value is stored as the new value of the key (replacing the old one), 
	 * and returned to the caller as a string.<br>
	 * Both the value already contained in the string key and the increment argument can be optionally provided in exponential notation, 
	 * however the value computed after the increment is stored consistently in the same format, that is, 
	 * an integer number followed (if needed) by a dot, and a variable number of digits representing the decimal part of the number. <br>
	 * Trailing zeroes are always removed.<br>
	 * The precision of the output is fixed at 17 digits after the decimal point regardless of the actual internal precision of the computation.<br>
	 * 
	 * @param key
	 * @param increment
	 * @return the value of key after the increment.
	 */
	Double incrbyfloat(String key, double increment);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(N) where N is the number of keys to retrieve.<br>
	 * 
	 * <p>
	 * Returns the values of all specified keys. <br>
	 * For every key that does not hold a string value or does not exist, 
	 * the special value null is returned. Because of this, the operation never fails.<br>
	 * 
	 * @param keys
	 * @return the values of all the given keys
	 */
	List<String> mget(String... keys);
	
	/**
	 * Available since 1.0.1<br>
	 * Time complexity: O(N) where N is the number of keys to set.<br>
	 * 
	 * <p>
	 * Sets the given keys to their respective values. <br>
	 * MSET replaces existing values with new values, just as regular SET. <br>
	 * See MSETNX if you don't want to overwrite existing values.<br>
	 * MSET is atomic, so all given keys are set at once. <br>
	 * It is not possible for clients to see that some of the keys were updated while others are unchanged.
	 * 
	 * @param keysvalues
	 * @return always OK.
	 */
	String mset(String... keysvalues);
	
	/**
	 * Available since 1.0.1.<br>
	 * Time complexity: O(N) where N is the number of keys to set.<br>
	 * 
	 * <p>
	 * Sets the given keys to their respective values.<br>
	 * MSETNX will not perform any operation at all even if just a single key already exists.<br>
	 * Because of this semantic MSETNX can be used in order to set different keys representing different fields of an unique logic object 
	 * in a way that ensures that either all the fields or none at all are set.<br>
	 * MSETNX is atomic, so all given keys are set at once. <br>
	 * It is not possible for clients to see that some of the keys were updated while others are unchanged.
	 * 
	 * @param keysvalues
	 * @return 1 if the all the keys were set.<br>
	 * 	       0 if no key was set (at least one key already existed).<br>
	 */
	Long msetnx(String... keysvalues);
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * PSETEX works exactly like SETEX with the sole difference that the expire time is specified in milliseconds instead of seconds.
	 * 
	 * @param key
	 * @param milliseconds
	 * @param value
	 * @return OK
	 */
	String psetex(String key, int milliseconds, String value);

	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Set key to hold the string value. <br>
	 * If key already holds a value, it is overwritten, regardless of its type. <br>
	 * Any previous time to live associated with the key is discarded on successful SET operation.
	 * 
	 * @param key
	 * @param value
	 * @return OK if SET was executed correctly. 
	 */
	String set(String key, String value);
	
	/**
	 * Available since 2.6.12<br>
	 * 
	 * <p>
	 * Options<br>
	 * Starting with Redis 2.6.12 SET supports a set of options that modify its behavior:<br>
	 * SET key value [EX seconds] [PX milliseconds] [NX|XX]<br>
	 * EX seconds -- Set the specified expire time, in seconds.<br>
	 * PX milliseconds -- Set the specified expire time, in milliseconds.<br>
	 * NX -- Only set the key if it does not already exist.<br>
	 * XX -- Only set the key if it already exist.<br>
	 * Note: Since the SET command options can replace SETNX, SETEX, PSETEX, it is possible that in future versions of 
	 *       Redis these three commands will be deprecated and finally removed.<br>
	 * 
	 * @param key
	 * @param value
	 * @return OK if SET was executed correctly. <br>
	 *         Null multi-bulk reply: a Null Bulk Reply is returned if the SET operation was not performed becase the 
	 *         user specified the NX or XX option but the condition was not met.
	 */
	String setxx(String key, String value);
	String setnxex(String key, String value, int seconds);
	String setnxpx(String key, String value, int milliseconds);
	String setxxex(String key, String value, int seconds);
	String setxxpx(String key, String value, int milliseconds);
	
	/**
	 * Available since 2.2.0<br>
	 * Time complexity: O(1)<br>
	 * <p>
	 * 
	 * Sets or clears the bit at offset in the string value stored at key.<br>
	 * The bit is either set or cleared depending on value, which can be either 0 or 1. When key does not exist, 
	 * a new string value is created. <br>
	 * The string is grown to make sure it can hold a bit at offset. <br>
	 * The offset argument is required to be greater than or equal to 0, and smaller than 232 (this limits bitmaps to 512MB). 
	 * When the string at key is grown, added bits are set to 0.<br>
	 * <p>
	 * Warning: 
	 * When setting the last possible bit (offset equal to 232 -1) and the string value stored at key does not yet hold a string value, 
	 * or holds a small string value, Redis needs to allocate all intermediate memory which can block the server for some time.<br> 
	 * On a 2010 MacBook Pro, setting bit number 232 -1 (512MB allocation) takes ~300ms, setting bit number 230 -1 (128MB allocation) takes ~80ms, 
	 * setting bit number 228 -1 (32MB allocation) takes ~30ms and setting bit number 226 -1 (8MB allocation) takes ~8ms. <br>
	 * Note that once this first allocation is done, subsequent calls to SETBIT for the same key will not have the allocation overhead.<br>
	 * 
	 * @param key
	 * @param offset
	 * @param value true means bit is set 1, otherwise 0
	 * @return the original bit value stored at offset
	 */
	Boolean setbit(String key, long offset, boolean value);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Set key to hold the string value and set key to timeout after a given number of seconds. <br>
	 * This command is equivalent to executing the following commands:
	 * <pre>
	 * SET mykey value
	 * EXPIRE mykey seconds
	 * </pre>
	 * SETEX is atomic, and can be reproduced by using the previous two commands inside an MULTI / EXEC block. <br>
	 * It is provided as a faster alternative to the given sequence of operations, because this operation is very common when Redis is used as a cache.<br>
	 * An error is returned when seconds is invalid.<br>
	 * 
	 * @param key
	 * @param seconds
	 * @param value
	 * @return Status code reply, e.g. OK
	 */
	String setex(String key, int seconds, String value);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Set key to hold string value if key does not exist. In that case, it is equal to SET. <br>
	 * When key already holds a value, no operation is performed. SETNX is short for "SET if N ot e X ists".<br>
	 * 
	 * @param key
	 * @param value
	 * @return 1 if the key was set<br>
	 *         0 if the key was not set
	 */
	Long setnx(String key, String value);
	
	/**
	 * Available since 2.2.0<br>
	 * Time complexity: O(1)<br>
	 * not counting the time taken to copy the new string in place. <br>
	 * Usually, this string is very small so the amortized complexity is O(1). Otherwise, complexity is O(M) with M being the length of the value argument.<br>
	 * 
	 * <p>
	 * Overwrites part of the string stored at key, starting at the specified offset, for the entire length of value.<br> 
	 * If the offset is larger than the current length of the string at key, the string is padded with zero-bytes to make offset fit. <br>
	 * Non-existing keys are considered as empty strings, so this command will make sure it holds a string large enough to be able to set value at offset.<br>
	 * Note that the maximum offset that you can set is 229 -1 (536870911), as Redis Strings are limited to 512 megabytes. <br>
	 * If you need to grow beyond this size, you can use multiple keys.<br>
	 * <p>
	 * Warning: When setting the last possible byte and the string value stored at key does not yet hold a string value, 
	 * or holds a small string value, Redis needs to allocate all intermediate memory which can block the server for some time. <br>
	 * On a 2010 MacBook Pro, setting byte number 536870911 (512MB allocation) takes ~300ms, 
	 * setting byte number 134217728 (128MB allocation) takes ~80ms, setting bit number 33554432 (32MB allocation) takes ~30ms 
	 * and setting bit number 8388608 (8MB allocation) takes ~8ms. Note that once this first allocation is done, 
	 * subsequent calls to SETRANGE for the same key will not have the allocation overhead.<br>
	 * 
	 * @param key
	 * @param offset
	 * @param value
	 * @return Integer reply: the length of the string after it was modified by the command.
	 */
	Long setrange(String key, long offset, String value);
	
	/**
	 * Available since 2.2.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Returns the length of the string value stored at key. An error is returned when key holds a non-string value.
	 * 
	 * @param key
	 * @return the length of the string at key, or 0 when key does not exist.
	 */
	Long strlen(String key);
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Hashes
	
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(N) <br>
	 * where N is the number of fields to be removed.<br>
	 * 
	 * <p>
	 * Removes the specified fields from the hash stored at key. <br>
	 * Specified fields that do not exist within this hash are ignored. <br>
	 * If key does not exist, it is treated as an empty hash and this command returns 0.<br>
	 * 
	 * History
	 * >= 2.4: Accepts multiple field arguments. Redis versions older than 2.4 can only remove a field per call.<br>
	 * To remove multiple fields from a hash in an atomic fashion in earlier versions, use a MULTI / EXEC block.<br>
	 * 
	 * @param key
	 * @param fields
	 * @return the number of fields that were removed from the hash, not including specified but non existing fields.
	 */
	Long hdel(String key, String... fields);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Returns if field is an existing field in the hash stored at key.<br>
	 * 
	 * @param key
	 * @param field
	 * @return true if the hash contains field.<br>
	 *         false if the hash does not contain field, or key does not exist.<br>
	 */
	Boolean hexists(String key, String field);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Returns the value associated with field in the hash stored at key.<br>
	 * 
	 * @param key
	 * @param field
	 * @return the value associated with field, or null when field is not present in the hash or key does not exist.
	 */
	String hget(String key, String field);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(N) <br>
	 * where N is the size of the hash.<br>
	 * 
	 * <p>
	 * Returns all fields and values of the hash stored at key. <br>
	 * In the returned value, every field name is followed by its value, so the length of the reply is twice the size of the hash.<br>
	 * 
	 * @param key
	 * @return list of fields and their values stored in the hash, or an empty list when key does not exist.
	 */
	Map<String, String> hgetall(String key);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Increments the number stored at field in the hash stored at key by increment. <br>
	 * If key does not exist, a new key holding a hash is created. <br>
	 * If field does not exist the value is set to 0 before the operation is performed.<br>
	 * The range of values supported by HINCRBY is limited to 64 bit signed integers.<br>
	 * 
	 * @param key
	 * @param field
	 * @param increment
	 * @return the value at field after the increment operation.
	 */
	Long hincrby(String key, String field, long increment);
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Increment the specified field of an hash stored at key, and representing a floating point number, by the specified increment. <br>
	 * If the field does not exist, it is set to 0 before performing the operation. <br>
	 * An error is returned if one of the following conditions occur:<br>
	 * - The field contains a value of the wrong type (not a string).<br>
	 * - The current field content or the specified increment are not parsable as a double precision floating point number.<br>
	 * The exact behavior of this command is identical to the one of the INCRBYFLOAT command, 
	 * please refer to the documentation of INCRBYFLOAT for further information.<br>
	 * 
	 * @param key
	 * @param field
	 * @param increment
	 * @return the value of field after the increment.
	 */
	Double hincrbyfloat(String key, String field, double increment);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(N) where N is the size of the hash.<br>
	 * 
	 * <p>
	 * Returns all field names in the hash stored at key.<br>
	 * 
	 * @param key
	 * @return list of fields in the hash, or an empty list when key does not exist.<br>
	 */
	Set<String> hkeys(String key);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Returns the number of fields contained in the hash stored at key.<br>
	 * 
	 * @param key
	 * @return number of fields in the hash, or 0 when key does not exist.<br>
	 */
	Long hlen(String key);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(N) where N is the number of fields being requested.<br>
	 * 
	 * <p>
	 * Returns the values associated with the specified fields in the hash stored at key.<br>
	 * For every field that does not exist in the hash, a null value is returned. <br>
	 * Because a non-existing keys are treated as empty hashes, running HMGET against a non-existing key will return a list of nil values.<br>
	 * 
	 * @param key
	 * @param fields
	 * @return list of values associated with the given fields, in the same order as they are requested.
	 */
	List<String> hmget(String key, String... fields);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(N) where N is the number of fields being set.<br>
	 * 
	 * <p>
	 * Sets the specified fields to their respective values in the hash stored at key. <br>
	 * This command overwrites any existing fields in the hash. If key does not exist, a new key holding a hash is created.<br>
	 * 
	 * @param key
	 * @param fieldvalues
	 * @return Status code reply, e.g. OK
	 */
	String hmset(String key, Map<String, String> fieldvalues);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Sets field in the hash stored at key to value. <br>
	 * If key does not exist, a new key holding a hash is created. <br>
	 * If field already exists in the hash, it is overwritten.<br>
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return 1 if field is a new field in the hash and value was set.<br>
	 *         0 if field already exists in the hash and the value was updated.<br>
	 */
	Long hset(String key, String field, String value);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Sets field in the hash stored at key to value, only if field does not yet exist. <br>
	 * If key does not exist, a new key holding a hash is created. <br>
	 * If field already exists, this operation has no effect.<br>
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return 1 if field is a new field in the hash and value was set.<br>
	 *         0 if field already exists in the hash and no operation was performed.<br>
	 */
	Long hsetnx(String key, String field, String value);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(N) where N is the size of the hash.<br>
	 * 
	 * <p>
	 * Returns all values in the hash stored at key.
	 * 
	 * @param key
	 * @return list of values in the hash, or an empty list when key does not exist.
	 */
	List<String> hvals(String key);
	
	/**
	 * @see #scan(String)
	 */
	ScanResult<Map.Entry<String, String>> hscan(String key, String cursor);
	ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, int count);
	ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, String pattern);
	ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, String pattern, int count);
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Lists
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * BLPOP is a blocking list pop primitive. <br>
	 * It is the blocking version of LPOP because it blocks the connection when 
	 * there are no elements to pop from any of the given lists. An element is popped from the head of the first list that is non-empty, 
	 * with the given keys being checked in the order that they are given.<br>
	 * 
	 * <p>
	 * Non-blocking behavior<br>
	 * When BLPOP is called, if at least one of the specified keys contains a non-empty list, 
	 * an element is popped from the head of the list and returned to the caller together with the key it was popped from.<br>
	 * Keys are checked in the order that they are given. Let's say that the key list1 doesn't exist and list2 and list3 hold non-empty lists.<br> 
	 * Consider the following command:
	 * <pre>
	 * BLPOP list1 list2 list3 0
	 * </pre>
	 * BLPOP guarantees to return an element from the list stored at list2 (since it is the first non empty list when 
	 * checking list1, list2 and list3 in that order).
	 * 
	 * <p>
	 * Blocking behavior<br>
	 * If none of the specified keys exist, BLPOP blocks the connection until another client performs an LPUSH or RPUSH
	 * operation against one of the keys.<br>
	 * Once new data is present on one of the lists, the client returns with the name of the key unblocking it and the popped value.<br>
	 * When BLPOP causes a client to block and a non-zero timeout is specified, the client will unblock returning a nil 
	 * multi-bulk value when the specified timeout has expired without a push operation against at least one of the specified keys.<br>
	 * The timeout argument is interpreted as an integer value specifying the maximum number of seconds to block. <br>
	 * A timeout of zero can be used to block infinitely.<br>
	 * 
	 * <p>
	 * What key is served first? What client? What element? Priority ordering details.<br>
	 * If the client tries to blocks for multiple keys, but at least one key contains elements, 
	 * the returned key / element pair is the first key from left to right that has one or more elements. <br>
	 * In this case the client is not blocked. So for instance BLPOP key1 key2 key3 key4 0, assuming that both key2 and key4 are non-empty, 
	 * will always return an element from key2.<br>
	 * If multiple clients are blocked for the same key, the first client to be served is the one that was waiting for 
	 * more time (the first that blocked for the key). <br>
	 * Once a client is unblocked it does not retain any priority, 
	 * when it blocks again with the next call to BLPOP it will be served accordingly to the number of clients already 
	 * blocked for the same key, that will all be served before it (from the first to the last that blocked).<br>
	 * When a client is blocking for multiple keys at the same time, and elements are available at the same time in 
	 * multiple keys (because of a transaction or a Lua script added elements to multiple lists), 
	 * the client will be unblocked using the first key that received a push operation (assuming it has enough elements to 
	 * serve our client, as there may be other clients as well waiting for this key). <br>
	 * Basically after the execution of every 
	 * command Redis will run a list of all the keys that received data AND that have at least a client blocked. <br>
	 * The list is ordered by new element arrival time, from the first key that received data to the last. <br>
	 * For every key processed, Redis will serve all the clients waiting for that key in a FIFO fashion, as long as there 
	 * are elements in this key. When the key is empty or there are no longer clients waiting for this key, 
	 * the next key that received new data in the previous command / transaction / script is processed, and so forth.<br>
	 * 
	 * <p>
	 * Behavior of BLPOP when multiple elements are pushed inside a list.<br>
	 * There are times when a list can receive multiple elements in the context of the same conceptual command:
	 * Variadic push operations such as LPUSH mylist a b c.<br>
	 * After an EXEC of a MULTI block with multiple push operations against the same list.<br>
	 * Executing a Lua Script with Redis 2.6 or newer.<br>
	 * When multiple elements are pushed inside a list where there are clients blocking, the behavior is different for Redis 2.4 and Redis 2.6 or newer.<br>
	 * For Redis 2.6 what happens is that the command performing multiple pushes is executed, 
	 * and only after the execution of the command the blocked clients are served. Consider this sequence of commands.<br>
	 * <pre>
	 * Client A:   BLPOP foo 0
	 * Client B:   LPUSH foo a b c
	 * </pre>
	 * If the above condition happens using a Redis 2.6 server or greater, Client A will be served with the c element, 
	 * because after the LPUSH command the list contains c,b,a, so taking an element from the left means to return c.<br>
	 * Instead Redis 2.4 works in a different way: clients are served in the context of the push operation, so as long as 
	 * LPUSH foo a b c starts pushing the first element to the list, it will be delivered to the Client A, that will receive a (the first element pushed).<br>
	 * The behavior of Redis 2.4 creates a lot of problems when replicating or persisting data into the AOF file, 
	 * so the much more generic and semantically simpler behaviour was introduced into Redis 2.6 to prevent problems.<br>
	 * Note that for the same reason a Lua script or a MULTI/EXEC block may push elements into a list and afterward delete the list. <br>
	 * In this case the blocked clients will not be served at all and will continue to be blocked as long as no data is 
	 * present on the list after the execution of a single command, transaction, or script.<br>
	 * 
	 * <p>
	 * BLPOP inside a MULTI / EXEC transaction<br>
	 * BLPOP can be used with pipelining (sending multiple commands and reading the replies in batch), 
	 * however this setup makes sense almost solely when it is the last command of the pipeline.<br>
	 * Using BLPOP inside a MULTI / EXEC block does not make a lot of sense as it would require blocking the entire 
	 * server in order to execute the block atomically, which in turn does not allow other clients to perform a push operation. <br>
	 * For this reason the behavior of BLPOP inside MULTI / EXEC when the list is empty is to return a nil multi-bulk reply, 
	 * which is the same thing that happens when the timeout is reached.<br>
	 * If you like science fiction, think of time flowing at infinite speed inside a MULTI / EXEC block...<br>
	 * 
	 * @param key
	 * @return A null when no element could be popped and the timeout expired.
	 *         A popped element.
	 */
	String blpop(String key);
	String blpop(String key, int timeout);
	
	/**
	 * @see #blpop(String)
	 * @param timeout max seconds to block
	 * @param keys
	 * @return A empty map(nil multi-bulk) when no element could be popped and the timeout expired.
	 *         A map (two-element multi-bulk) with the key (first element) being the name of the key where an element was popped 
	 *         and the value (second element) being the value of the popped element.
	 */
	Map<String, String> blpop(int timeout, String... keys);
	Map<String, String> blpop(String... keys);
	
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * BRPOP is a blocking list pop primitive. <br>
	 * It is the blocking version of RPOP because it blocks the connection when there are no elements to pop from any of the given lists.<br>
	 *  An element is popped from the tail of the first list that is non-empty, 
	 * with the given keys being checked in the order that they are given.<br>
	 * See the BLPOP documentation for the exact semantics, since BRPOP is identical to BLPOP with the only difference being that 
	 * it pops elements from the tail of a list instead of popping from the head.<br>
	 * 
	 * @see #blpop(String)
	 * @param key
	 * @return A null when no element could be popped and the timeout expired.<br>
	 *         A popped element.<br>
	 */
	String brpop(String key);
	String brpop(String key, int timeout);
	
	/**
	 * @see #brpop(String)
	 * @param timeout
	 * @param keys
	 * @return A empty map(nil multi-bulk) when no element could be popped and the timeout expired.<br>
	 *         A map (two-element multi-bulk) with the key (first element) being the name of the key where an element was popped 
	 *         and the value (second element) being the value of the popped element.<br>
	 */
	Map<String, String> brpop(int timeout, String... keys);
	Map<String, String> brpop(String... keys);
	
	/**
	 * Available since 2.2.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * BRPOPLPUSH is the blocking variant of RPOPLPUSH. When source contains elements, this command behaves exactly like RPOPLPUSH. <br>
	 * When source is empty, Redis will block the connection until another client pushes to it or until timeout is reached. <br>
	 * A timeout of zero can be used to block infinitely.<br>
	 * See RPOPLPUSH for more information.<br>
	 * 
	 * @param source
	 * @param destination
	 * @param timeout
	 * @return the element being popped from source and pushed to destination. <br>
	 * 		   If timeout is reached, a null reply is returned.<br>
	 */
	String brpoplpush(String source, String destination, int timeout);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(N) where N is the number of elements to traverse to get to the element at index. <br>
	 * This makes asking for the first or the last element of the list O(1).<br>
	 * 
	 * <p>
	 * Returns the element at index index in the list stored at key. <br>
	 * The index is zero-based, so 0 means the first element, 1 the second element and so on. <br>
	 * Negative indices can be used to designate elements starting at the tail of the list. <br>
	 * Here, -1 means the last element, -2 means the penultimate and so forth.<br>
	 * When the value at key is not a list, an error is returned.<br>
	 * 
	 * @param key
	 * @param index zero-based
	 * @return an element from a list by its index
	 */
	String lindex(String key, long index);
	
	/**
	 * Available since 2.2.0<br>
	 * Time complexity: O(N) where N is the number of elements to traverse before seeing the value pivot. <br>
	 * This means that inserting somewhere on the left end on the list (head) can be considered O(1)
	 * and inserting somewhere on the right end (tail) is O(N).<br>
	 * 
	 * <p>
	 * Inserts value in the list stored at key either before or after the reference value pivot.<br>
	 * When key does not exist, it is considered an empty list and no operation is performed.<br>
	 * When pivot can not be found and no operation is performed.<br>
	 * An error is returned when key exists but does not hold a list value.<br>
	 * 
	 * @param key
	 * @param pivot
	 * @param value
	 * @return the length of the list after the insert operation, or -1 when the value pivot was not found.
	 */
	Long linsertbefore(String key, String pivot, String value);
	Long linsertafter(String key, String pivot, String value);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Returns the length of the list stored at key. If key does not exist, it is interpreted as an empty list and 0 is returned. <br>
	 * An error is returned when the value stored at key is not a list.<br>
	 * 
	 * @param key
	 * @return the length of the list at key.
	 */
	Long llen(String key);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Removes and returns the first element of the list stored at key.<br>
	 * 
	 * @param key
	 * @return the value of the first element, or null when key does not exist.<br>
	 */
	String lpop(String key);
	
	/**
	 * Available since 1.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Insert all the specified values at the head of the list stored at key. <br>
	 * If key does not exist, it is created as empty list before performing the push operations. <br>
	 * When key holds a value that is not a list, an error is returned.<br>
	 * It is possible to push multiple elements using a single command call just specifying multiple arguments at the end of the command.<br> 
	 * Elements are inserted one after the other to the head of the list, from the leftmost element to the rightmost element. <br>
	 * So for instance the command LPUSH mylist a b c will result into a list containing c as first element, b as second element and a as third element.<br>
	 * 
	 * <p>
	 * History<br>
	 * >= 2.4: Accepts multiple value arguments. <br>
	 * In Redis versions older than 2.4 it was possible to push a single value per command.<br>
	 * 
	 * @param key
	 * @param values
	 * @return the length of the list after the push operations.
	 */
	Long lpush(String key, String... values);
	
	/**
	 * Available since 2.2.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Inserts value at the head of the list stored at key, only if key already exists and holds a list. <br>
	 * In contrary to LPUSH, no operation will be performed when key does not yet exist.<br>
	 * 
	 * @param key
	 * @param value
	 * @return the length of the list after the push operation.
	 */
	Long lpushx(String key, String value);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(S+N) where S is the start offset and N is the number of elements in the specified range.<br>
	 * 
	 * <p>
	 * Returns the specified elements of the list stored at key. <br>
	 * The offsets start and stop are zero-based indexes,
	 * with 0 being the first element of the list (the head of the list), 1 being the next element and so on.<br>
	 * These offsets can also be negative numbers indicating offsets starting at the end of the list. <br>
	 * For example, -1 is the last element of the list, -2 the penultimate, and so on.<br>
	 * 
	 * <p>
	 * Consistency with range functions in various programming languages<br>
	 * Note that if you have a list of numbers from 0 to 100, LRANGE list 0 10 will return 11 elements, that is, the rightmost item is included. <br>
	 * This may or may not be consistent with behavior of range-related functions in your programming language of choice 
	 * (think Ruby's Range.new, Array#slice or Python's range() function).<br>
	 * 
	 * <p>
	 * Out-of-range indexes<br>
	 * Out of range indexes will not produce an error. <br>
	 * If start is larger than the end of the list, an empty list is returned.<br> 
	 * If stop is larger than the actual end of the list, Redis will treat it like the last element of the list.<br>
	 * 
	 * @param key
	 * @param start
	 * @param stop
	 * @return list of elements in the specified range.
	 */
	List<String> lrange(String key, long start, long stop);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(N) where N is the length of the list.<br>
	 * 
	 * <p>
	 * Removes the first count occurrences of elements equal to value from the list stored at key. <br>
	 * The count argument influences the operation in the following ways:<br>
	 * count > 0: Remove elements equal to value moving from head to tail.<br>
	 * count < 0: Remove elements equal to value moving from tail to head.<br>
	 * count = 0: Remove all elements equal to value.<br>
	 * For example, LREM list -2 "hello" will remove the last two occurrences of "hello" in the list stored at list.<br>
	 * Note that non-existing keys are treated like empty lists, so when key does not exist, the command will always return 0.<br>
	 * 
	 * @param key
	 * @param count
	 * @param value
	 * @return the number of removed elements.
	 */
	Long lrem(String key, long count, String value);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(N) where N is the length of the list. Setting either the first or the last element of the list is O(1).<br>
	 * 
	 * <p>
	 * Sets the list element at index to value. <br>
	 * For more information on the index argument, see LINDEX.<br>
	 * An error is returned for out of range indexes.<br>
	 * 
	 * @param key
	 * @param index
	 * @param value
	 * @return Status code reply, e.g. OK
	 */
	String lset(String key, long index, String value);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(N) where N is the number of elements to be removed by the operation.<br>
	 * 
	 * <p>
	 * Trim an existing list so that it will contain only the specified range of elements specified. <br>
	 * Both start and stop are zero-based indexes, where 0 is the first element of the list (the head), 1 the next element and so on.<br>
	 * For example: LTRIM foobar 0 2 will modify the list stored at foobar so that only the first three elements of the list will remain.<br>
	 * start and end can also be negative numbers indicating offsets from the end of the list, where -1 is the last element of the list, 
	 * -2 the penultimate element and so on.<br>
	 * Out of range indexes will not produce an error: if start is larger than the end of the list, or start > end, 
	 * the result will be an empty list (which causes key to be removed). <br>
	 * If end is larger than the end of the list, Redis will treat it like the last element of the list.
	 * A common use of LTRIM is together with LPUSH / RPUSH. For example:
	 * <pre>
	 * LPUSH mylist someelement
	 * LTRIM mylist 0 99
	 * </pre>
	 * This pair of commands will push a new element on the list, while making sure that the list will not grow larger than 100 elements. <br>
	 * This is very useful when using Redis to store logs for example. <br>
	 * It is important to note that when used in this way LTRIM is an O(1) operation because in the average case just one element 
	 * is removed from the tail of the list.
	 * 
	 * @param key
	 * @param start inclusive
	 * @param stop  inclusive
	 * @return Status code reply, e.g. OK
	 */
	String ltrim(String key, long start, long stop);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Removes and returns the last element of the list stored at key.
	 * 
	 * @param key
	 * @return the value of the last element, or null when key does not exist.
	 */
	String rpop(String key);
	
	/**
	 * Available since 1.2.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Atomically returns and removes the last element (tail) of the list stored at source, 
	 * and pushes the element at the first element (head) of the list stored at destination.<br>
	 * For example: consider source holding the list a,b,c, and destination holding the list x,y,z. <br>
	 * Executing RPOPLPUSH results in source holding a,b and destination holding c,x,y,z.<br>
	 * If source does not exist, the value null is returned and no operation is performed. <br>
	 * If source and destination are the same, the operation is equivalent to removing the last element from the list and 
	 * pushing it as first element of the list, so it can be considered as a list rotation command.<br>
	 * 
	 * @param source
	 * @param destination
	 * @return the element being popped and pushed.
	 */
	String rpoplpush(String source, String destination); 
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Insert all the specified values at the tail of the list stored at key. <br>
	 * If key does not exist, it is created as empty list before performing the push operation. <br>
	 * When key holds a value that is not a list, an error is returned.<br>
	 * It is possible to push multiple elements using a single command call just specifying multiple arguments at the end of the command.<br> 
	 * Elements are inserted one after the other to the tail of the list, from the leftmost element to the rightmost element. <br>
	 * So for instance the command RPUSH mylist a b c will result into a list containing a as first element, b as second element and c as third element.<br>
	 * 
	 * <p>
	 * History<br>
	 * >= 2.4: Accepts multiple value arguments.<br>
	 * In Redis versions older than 2.4 it was possible to push a single value per command.
	 * 
	 * @param key
	 * @param values
	 * @return the length of the list after the push operation.
	 */
	Long rpush(String key, String... values);
	
	/**
	 * Available since 2.2.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Inserts value at the tail of the list stored at key, only if key already exists and holds a list. <br>
	 * In contrary to RPUSH, no operation will be performed when key does not yet exist.<br>
	 * 
	 * @param key
	 * @param value
	 * @return the length of the list after the push operation.
	 */
	Long rpushx(String key, String value);
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Sets
	
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(N) where N is the number of members to be added.<br>
	 * 
	 * <p>
	 * Add the specified members to the set stored at key. <br>
	 * Specified members that are already a member of this set are ignored. <br>
	 * If key does not exist, a new set is created before adding the specified members.<br>
	 * An error is returned when the value stored at key is not a set.<br>
	 * 
	 * <p>
	 * History<br>
	 * >= 2.4: Accepts multiple member arguments. <br>
	 * Redis versions before 2.4 are only able to add a single member per call.<br>
	 * 
	 * @param key
	 * @param members
	 * @return the number of elements that were added to the set, not including all the elements already present into the set.
	 */
	Long sadd(String key, String... members);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Returns the set cardinality (number of elements) of the set stored at key.<br>
	 * 
	 * @param key
	 * @return the cardinality (number of elements) of the set, or 0 if key does not exist.
	 */
	Long scard(String key);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(N) where N is the total number of elements in all given sets.<br>
	 * 
	 * <p>
	 * Returns the members of the set resulting from the difference between the first set and all the successive sets.<br>
	 * For example:
	 * <pre>
	 * key1 = {a,b,c,d}
	 * key2 = {c}
	 * key3 = {a,c,e}
	 * SDIFF key1 key2 key3 = {b,d}
	 * </pre>
	 * Keys that do not exist are considered to be empty sets.<br>
	 * 
	 * @param keys
	 * @return list with members of the resulting set.
	 */
	Set<String> sdiff(String... keys);
	
	/**
	 * Available since 1.0.0.<br>
	 * Time complexity: O(N) where N is the total number of elements in all given sets.<br>
	 * 
	 * <p>
	 * This command is equal to SDIFF, but instead of returning the resulting set, it is stored in destination.<br>
	 * If destination already exists, it is overwritten.<br>
	 * 
	 * @param destination
	 * @param keys
	 * @return the number of elements in the resulting set.
	 */
	Long sdiffstore(String destination, String... keys);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(N*M) worst case where N is the cardinality of the smallest set and M is the number of sets.<br>
	 * 
	 * <p>
	 * Returns the members of the set resulting from the intersection of all the given sets.<br>
	 * For example:
	 * <pre>
	 * key1 = {a,b,c,d}
	 * key2 = {c}
	 * key3 = {a,c,e}
	 * SINTER key1 key2 key3 = {c}
	 * </pre>
	 * Keys that do not exist are considered to be empty sets.<br> 
	 * With one of the keys being an empty set, the resulting set is also empty (since set intersection with an empty set always results in an empty set).
	 * 
	 * @param keys
	 * @return list with members of the resulting set.
	 */
	Set<String> sinter(String... keys);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(N*M) worst case where N is the cardinality of the smallest set and M is the number of sets.<br>
	 * 
	 * <p>
	 * This command is equal to SINTER, but instead of returning the resulting set, it is stored in destination.<br>
	 * If destination already exists, it is overwritten.<br>
	 * 
	 * @param destination
	 * @param keys
	 * @return the number of elements in the resulting set.
	 */
	Long sinterstore(String destination, String... keys);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Returns if member is a member of the set stored at key.<br>
	 * 
	 * @param key
	 * @param member
	 * @return true if the element is a member of the set.<br>
	 *         false if the element is not a member of the set, or if key does not exist.
	 */
	Boolean sismember(String key, String member);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(N) where N is the set cardinality.<br>
	 * 
	 * <p>
	 * Returns all the members of the set value stored at key.<br>
	 * This has the same effect as running SINTER with one argument key.<br>
	 * 
	 * @param key
	 * @return all the members in a set
	 */
	Set<String> smembers(String key);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Move member from the set at source to the set at destination. This operation is atomic. <br>
	 * In every given moment the element will appear to be a member of source or destination for other clients.<br>
	 * If the source set does not exist or does not contain the specified element, no operation is performed and 0 is returned. <br>
	 * Otherwise, the element is removed from the source set and added to the destination set. <br>
	 * When the specified element already exists in the destination set, it is only removed from the source set.<br>
	 * An error is returned if source or destination does not hold a set value.<br>
	 * 
	 * @param source
	 * @param destination
	 * @param member
	 * @return 1 if the element is moved.<br>
	 *         0 if the element is not a member of source and no operation was performed.<br>
	 */
	Long smove(String source, String destination, String member);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Removes and returns a random element from the set value stored at key.<br>
	 * This operation is similar to SRANDMEMBER, that returns a random element from a set but does not remove it.<br>
	 * 
	 * @param key
	 * @return the removed element, or null when key does not exist.
	 */
	String spop(String key);
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: O(N) where N is the absolute value of the passed count.<br>
	 * 
	 * <p>
	 * When called with just the key argument, return a random element from the set value stored at key.<br>
	 * Starting from Redis version 2.6, when called with the additional count argument, 
	 * return an array of count distinct elements if count is positive. <br>
	 * If called with a negative count the behavior changes and the command is allowed to return the same element multiple times. <br>
	 * In this case the numer of returned elements is the absolute value of the specified count.<br>
	 * When called with just the key argument, the operation is similar to SPOP, however while SPOP also removes the randomly selected element from the set, 
	 * SRANDMEMBER will just return a random element without altering the original set in any way.

	 * @param key
	 * @param count
	 * @return returns an set of elements, or an empty set when key does not exist.
	 */
	List<String> srandmember(String key, int count);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * @see #srandmember(String, int)
	 * @param key
	 * @return returns the randomly selected element, or null when key does not exist
	 */
	String srandmember(String key);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(N) where N is the number of members to be removed.<br>
	 * 
	 * <p>
	 * Remove the specified members from the set stored at key. <br>
	 * Specified members that are not a member of this set are ignored. <br>
	 * If key does not exist, it is treated as an empty set and this command returns 0.<br>
	 * An error is returned when the value stored at key is not a set.<br>
	 * 
	 * <p>
	 * History<br>
	 * >= 2.4: Accepts multiple member arguments. <br>
	 * Redis versions older than 2.4 can only remove a set member per call.
	 * 
	 * @param key
	 * @param members
	 * @return the number of members that were removed from the set, not including non existing members.
	 */
	Long srem(String key, String... members);
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(N) where N is the total number of elements in all given sets.<br>
	 * 
	 * <p>
	 * Returns the members of the set resulting from the union of all the given sets.<br>
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
	
	/**
	 * Available since 1.0.0<br>
	 * Time complexity: O(N) where N is the total number of elements in all given sets.<br>
	 * 
	 * <p>
	 * This command is equal to SUNION, but instead of returning the resulting set, it is stored in destination.
	 * If destination already exists, it is overwritten.<br>
	 * 
	 * @param destination
	 * @param keys
	 * @return the number of elements in the resulting set
	 */
	Long sunionstore(String destination, String... keys);
	
	/**
	 * @see #scan(String)
	 */
	ScanResult<String> sscan(String key, String cursor);
	ScanResult<String> sscan(String key, String cursor, int count);
	ScanResult<String> sscan(String key, String cursor, String pattern);
	ScanResult<String> sscan(String key, String cursor, String pattern, int count);
	
	
	// ~ -------------------------------------------------------------------------------------------------- Sorted Sets
	
	/**
	 * Available since 1.2.0<br>
	 * Time complexity: O(log(N)) where N is the number of elements in the sorted set.<br>
	 * 
	 * <p>
	 * Adds all the specified members with the specified scores to the sorted set stored at key. <br>
	 * It is possible to specify multiple score/member pairs. <br>
	 * If a specified member is already a member of the sorted set, 
	 * the score is updated and the element reinserted at the right position to ensure the correct ordering. <br>
	 * If key does not exist, a new sorted set with the specified members as sole members is created, like if the sorted set was empty.<br>
	 * If the key exists but does not hold a sorted set, an error is returned.<br>
	 * The score values should be the string representation of a numeric value, and accepts double precision floating point numbers.
	 * 
	 * <p>
	 * History<br>
	 * >= 2.4: Accepts multiple elements. <br>
	 * In Redis versions older than 2.4 it was possible to add or update a single member per call.
	 * 
	 * @param key
	 * @param score
	 * @param member
	 * @return The number of elements added to the sorted sets, not including elements already existing for which the score was updated.
	 */
	Long zadd(String key, double score, String member);
	Long zadd(String key, Map<String, Double> scoremembers);
	
	/**
	 * Available since 1.2.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Returns the sorted set cardinality (number of elements) of the sorted set stored at key.
	 * 
	 * @param key
	 * @return the cardinality (number of elements) of the sorted set, or 0 if key does not exist.
	 */
	Long zcard(String key);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(log(N)+M) with N being the number of elements in the sorted set and M being the number of elements between min and max.<br>
	 * 
	 * <p>
	 * Returns the number of elements in the sorted set at key with a score between min and max.<br>
	 * The min and max arguments have the same semantic as described for ZRANGEBYSCORE.<br>
	 * 
	 * @param key
	 * @param min inclusive
	 * @param max inclusive
	 * @return the number of members in a sorted set with scores within the given values
	 */
	Long zcount(String key, double min, double max);
	Long zcount(String key, String min, String max);
	
	/**
	 * Available since 1.2.0<br>
	 * Time complexity: O(log(N)) where N is the number of elements in the sorted set.<br>
	 * 
	 * <p>
	 * Increments the score of member in the sorted set stored at key by increment. <br>
	 * If member does not exist in the sorted set, it is added with increment as its score (as if its previous score was 0.0). <br>
	 * If key does not exist, a new sorted set with the specified member as its sole member is created.<br>
	 * An error is returned when key exists but does not hold a sorted set.<br>
	 * The score value should be the string representation of a numeric value, and accepts double precision floating point numbers. <br>
	 * It is possible to provide a negative value to decrement the score.<br>
	 * 
	 * @param key
	 * @param score
	 * @param member
	 * @return the new score of member (a double precision floating point number), represented as string.
	 */
	Double zincrby(String key, double score, String member);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(N*K)+O(M*log(M)) worst case with N being the smallest input sorted set, 
	 * K being the number of input sorted sets and M being the number of elements in the resulting sorted set.<br>
	 * 
	 * <p>
	 * Computes the intersection of sorted sets given by the specified keys, and stores the result in destination. <br>
	 * By default, the resulting score of an element is the sum of its scores in the sorted sets where it exists. <br>
	 * Because intersection requires an element to be a member of every given sorted set, 
	 * this results in the score of every element in the resulting sorted set to be equal to the number of input sorted sets.<br>
	 * For a description of the WEIGHTS and AGGREGATE options, see ZUNIONSTORE.<br>
	 * If destination already exists, it is overwritten.<br>
	 * 
	 * @param destination
	 * @param keys
	 * @return the number of elements in the resulting sorted set at destination.
	 */
	Long zinterstore(String destination, String... keys);
	Long zinterstoremax(String destination, String... keys);
	Long zinterstoremin(String destination, String... keys);
	Long zinterstore(String destination, Map<String, Integer> weightkeys);
	Long zinterstoremax(String destination, Map<String, Integer> weightkeys);
	Long zinterstoremin(String destination, Map<String, Integer> weightkeys);
	
	/**
	 * Available since 1.2.0<br>
	 * Time complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements returned.<br>
	 * 
	 * <p>
	 * Returns the specified range of elements in the sorted set stored at key. <br>
	 * The elements are considered to be ordered from the lowest to the highest score. <br>
	 * Lexicographical order is used for elements with equal score.<br>
	 * See ZREVRANGE when you need the elements ordered from highest to lowest score (and descending lexicographical order for elements with equal score).<br>
	 * Both start and stop are zero-based indexes, where 0 is the first element, 1 is the next element and so on. <br>
	 * They can also be negative numbers indicating offsets from the end of the sorted set, with -1 being the last element of the sorted set, 
	 * -2 the penultimate element and so on.<br>
	 * Out of range indexes will not produce an error. <br>
	 * If start is larger than the largest index in the sorted set, or start > stop, an empty list is returned. 
	 * If stop is larger than the end of the sorted set Redis will treat it like it is the last element of the sorted set.<br>
	 * It is possible to pass the WITHSCORES option in order to return the scores of the elements together with the elements. <br>
	 * The returned list will contain value1,score1,...,valueN,scoreN instead of value1,...,valueN. <br>
	 * Client libraries are free to return a more appropriate data type (suggestion: an array with (value, score) arrays/tuples).<br>
	 * 
	 * @param key
	 * @param start inclusive
	 * @param stop  invlusive
	 * @return list of elements in the specified range (optionally with their scores).
	 */
	Set<String> zrange(String key, long start, long stop);
	Map<String, Double> zrangewithscores(String key, long start, long stop);
	
	/**
	 * Available since 1.0.5<br>
	 * Time complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements being returned. <br>
	 * If M is constant (e.g. always asking for the first 10 elements with LIMIT), you can consider it O(log(N)).<br>
	 * 
	 * <p>
	 * Returns all the elements in the sorted set at key with a score between min and max (including elements with score equal to min or max). <br>
	 * The elements are considered to be ordered from low to high scores.<br>
	 * The elements having the same score are returned in lexicographical order (this follows from a property of the sorted set
 	 * implementation in Redis and does not involve further computation).<br>
	 * The optional LIMIT argument can be used to only get a range of the matching elements (similar to SELECT LIMIT offset, count in SQL). <br>
	 * Keep in mind that if offset is large, the sorted set needs to be traversed for offset elements before getting to the elements to return,
	 * which can add up to O(N) time complexity.<br>
	 * The optional WITHSCORES argument makes the command return both the element and its score, instead of the element alone. <br>
	 * This option is available since Redis 2.0.<br>
	 * <p>
	 * Exclusive intervals and infinity<br>
	 * min and max can be -inf and +inf, so that you are not required to know the highest or lowest score in the sorted 
	 * set to get all elements from or up to a certain score.<br>
	 * By default, the interval specified by min and max is closed (inclusive). <br>
	 * It is possible to specify an open interval 
	 * (exclusive) by prefixing the score with the character (. For example:
	 * <pre>
	 * ZRANGEBYSCORE zset (1 5
	 * </pre>
	 * Will return all elements with 1 < score <= 5 while:
	 * <pre>
	 * ZRANGEBYSCORE zset (5 (10
	 * </pre>
	 * Will return all the elements with 5 < score < 10 (5 and 10 excluded).
	 * 
	 * @param key
	 * @param min inclusive
	 * @param max inclusive
	 * @return list of elements in the specified score range (optionally with their scores). 
	 */
	Set<String> zrangebyscore(String key, double min, double max);
	Set<String> zrangebyscore(String key, String min, String max);
	Set<String> zrangebyscore(String key, double min, double max, int offset, int count);
	Set<String> zrangebyscore(String key, String min, String max, int offset, int count);
	Map<String, Double> zrangebyscorewithscores(String key, double min, double max);
	Map<String, Double> zrangebyscorewithscores(String key, String min, String max);
	Map<String, Double> zrangebyscorewithscores(String key, double min, double max, int offset, int count);
	Map<String, Double> zrangebyscorewithscores(String key, String min, String max, int offset, int count);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(log(N))<br>
	 * 
	 * <p>
	 * Returns the rank of member in the sorted set stored at key, with the scores ordered from low to high. <br>
	 * The rank (or index) is 0-based, which means that the member with the lowest score has rank 0.<br>
	 * Use ZREVRANK to get the rank of an element with the scores ordered from high to low.<br>
	 * 
	 * @param key
	 * @param member
	 * @return If member exists in the sorted set, return the rank of member.<br>
	 *         If member does not exist in the sorted set or key does not exist, return null.<br>
	 */
	Long zrank(String key, String member);
	
	/**
	 * Available since 1.2.0<br>
	 * Time complexity: O(M*log(N)) with N being the number of elements in the sorted set and M the number of elements to be removed.<br>
	 * 
	 * <p>
	 * Removes the specified members from the sorted set stored at key. Non existing members are ignored.<br>
	 * An error is returned when key exists and does not hold a sorted set.<br>
	 * 
	 * <p>
	 * History<br>
	 * >= 2.4: Accepts multiple elements. <br>
	 * In Redis versions older than 2.4 it was possible to remove a single member per call.<br>
	 * 
	 * @param key
	 * @param members
	 * @return The number of members removed from the sorted set, not including non existing members.
	 */
	Long zrem(String key, String... members);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements removed by the operation.<br>
	 * 
	 * <p>
	 * Removes all elements in the sorted set stored at key with rank between start and stop. <br>
	 * Both start and stop are 0 -based indexes with 0 being the element with the lowest score. <br>
	 * These indexes can be negative numbers, where they indicate offsets starting at the element with the highest score. <br>
	 * For example: -1 is the element with the highest score, -2 the element with the second highest score and so forth.<br>
	 * 
	 * @param key
	 * @param start inclusive
	 * @param stop  inclusive
	 * @return the number of elements removed.
	 */
	Long zremrangebyrank(String key, long start, long stop);
	
	/**
	 * Available since 1.2.0<br>
	 * Time complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements removed by the operation.<br>
	 * 
	 * <p>
	 * Removes all elements in the sorted set stored at key with a score between min and max (inclusive).<br>
	 * Since version 2.1.6, min and max can be exclusive, e.g. <br>
	 * <pre>
	 * ZREMRANGEBYSOCRE test (1 (3
	 * </pre>
	 * following the syntax of ZRANGEBYSCORE.<br>
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return the number of elements removed.
	 */
	Long zremrangebyscore(String key, double min, double max);
	Long zremrangebyscore(String key, String min, String max);
	
	/**
	 * Available since 1.2.0<br>
	 * Time complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements returned.<br>
	 * 
	 * <p>
	 * Returns the specified range of elements in the sorted set stored at key. <br>
	 * The elements are considered to be ordered from the highest to the lowest score. <br>
	 * Descending lexicographical order is used for elements with equal score.<br>
	 * Apart from the reversed ordering, ZREVRANGE is similar to ZRANGE.<br>
	 * 
	 * @param key
	 * @param start
	 * @param stop
	 * @return  list of elements in the specified range (optionally with their scores)
	 */
	Set<String> zrevrange(String key, long start, long stop);
	Map<String, Double> zrevrangewithscores(String key, long start, long stop);
	
	/**
	 * Available since 2.2.0<br>
	 * Time complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements being returned. <br>
	 * If M is constant (e.g. always asking for the first 10 elements with LIMIT), you can consider it O(log(N)).<br>
	 * 
	 * <p>
	 * Returns all the elements in the sorted set at key with a score between max and min (including elements with score equal to max or min).<br>
	 * In contrary to the default ordering of sorted sets, for this command the elements are considered to be ordered from high to low scores.<br>
	 * The elements having the same score are returned in reverse lexicographical order.<br>
	 * Apart from the reversed ordering, ZREVRANGEBYSCORE is similar to ZRANGEBYSCORE.<br>
	 * 
	 * @param key
	 * @param max
	 * @param min
	 * @return list of elements in the specified score range (optionally with their scores)
	 */
	Set<String> zrevrangebyscore(String key, double max, double min);
	Set<String> zrevrangebyscore(String key, String max, String min);
	Set<String> zrevrangebyscore(String key, double max, double min, int offset, int count);
	Set<String> zrevrangebyscore(String key, String max, String min, int offset, int count);
	Map<String, Double> zrevrangebyscorewithscores(String key, double max, double min);
	Map<String, Double> zrevrangebyscorewithscores(String key, String max, String min);
	Map<String, Double> zrevrangebyscorewithscores(String key, double max, double min, int offset, int count);
	Map<String, Double> zrevrangebyscorewithscores(String key, String max, String min, int offset, int count);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(log(N))<br>
	 *
	 * <p>
	 * Returns the rank of member in the sorted set stored at key, with the scores ordered from high to low. <br>
	 * The rank (or index) is 0-based, which means that the member with the highest score has rank 0.<br>
	 * Use ZRANK to get the rank of an element with the scores ordered from low to high.<br>
	 * 
	 * @param key
	 * @param member
	 * @return If member exists in the sorted set, return the rank of member.
	 *         If member does not exist in the sorted set or key does not exist, return null.
	 */
	Long zrevrank(String key, String member);
	
	/**
	 * Available since 1.2.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Returns the score of member in the sorted set at key.<br>
	 * If member does not exist in the sorted set, or key does not exist, null is returned.<br>
	 * 
	 * @param key
	 * @param member
	 * @return the score of member (a double precision floating point number), represented as string.<br>
	 */
	Double zscore(String key, String member);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(N)+O(M log(M)) with N being the sum of the sizes of the input sorted sets, 
	 * and M being the number of elements in the resulting sorted set.<br>
	 * 
	 * <p>
	 * Computes the union of sorted sets given by the specified keys, and stores the result in destination. <br>
	 * By default, the resulting score of an element is the sum of its scores in the sorted sets where it exists.<br>
	 * Using the WEIGHTS option, it is possible to specify a multiplication factor for each input sorted set. <br>
	 * This means that the score of every element in every input sorted set is multiplied by this factor before being 
	 * passed to the aggregation function. <br>
	 * When WEIGHTS is not given, the multiplication factors default to 1.<br>
	 * With the AGGREGATE option, it is possible to specify how the results of the union are aggregated. <br>
	 * This option defaults to SUM, where the score of an element is summed across the inputs where it exists. <br>
	 * When this option is set to either MIN or MAX, the resulting set will contain the minimum or maximum score of an 
	 * element across the inputs where it exists.<br>
	 * If destination already exists, it is overwritten.<br>
	 * 
	 * @param destination
	 * @param keys
	 * @return the number of elements in the resulting sorted set at destination.
	 */
	Long zunionstore(String destination, String... keys);
	Long zunionstoremax(String destination, String... keys);
	Long zunionstoremin(String destination, String... keys);
	Long zunionstore(String destination, Map<String, Integer> weightkeys);
	Long zunionstoremax(String destination, Map<String, Integer> weightkeys);
	Long zunionstoremin(String destination, Map<String, Integer> weightkeys);
	
	
	/**
	 * @see #scan(String)
	 */
	ScanResult<Map.Entry<String, Double>> zscan(String key, String cursor);
	ScanResult<Map.Entry<String, Double>> zscan(String key, String cursor, int count);
	ScanResult<Map.Entry<String, Double>> zscan(String key, String cursor, String pattern);
	ScanResult<Map.Entry<String, Double>> zscan(String key, String cursor, String pattern, int count);
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Pub/Sub
	
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(N) where N is the number of patterns the client is already subscribed to. <br>
	 * 
	 * <p>
	 * Subscribes the client to the given patterns.<br>
	 * 
	 * @param handler
	 * @param patterns
	 * @return a redis pub/sub object
	 */
	RedisPubSub psubscribe(RedisPsubscribeHandler handler, String... patterns);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(N+M) where N is the number of clients subscribed to the receiving channel 
	 * and M is the total number of subscribed patterns (by any client).<br>
	 * 
	 * <p>
	 * Posts a message to the given channel.<br>
	 * 
	 * @param channel
	 * @param message
	 * @return the number of clients that received the message.
	 */
	Long publish(String channel, String message);
	
	/**
	 * Available since 2.0.0.<br>
	 * Time complexity: O(N+M) where N is the number of patterns the client is already subscribed 
	 * and M is the number of total patterns subscribed in the system (by any client).<br>
	 * 
	 * <p>
	 * Unsubscribes the client from the given patterns, or from all of them if none is given.<br>
	 * When no patters are specified, the client is unsubscribed from all the previously subscribed patterns. <br>
	 * In this case, a message for every unsubscribed pattern will be sent to the client.<br>
	 * 
	 * @param pubsub
	 * @param patterns
	 */
	void punsubscribe(RedisPubSub pubsub, String... patterns);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(N) where N is the number of channels to subscribe to.<br>
	 * 
	 * <p>
	 * Subscribes the client to the specified channels.<br>
	 * Once the client enters the subscribed state it is not supposed to issue any other commands, 
	 * except for additional SUBSCRIBE, PSUBSCRIBE, UNSUBSCRIBE and PUNSUBSCRIBE commands.<br>
	 * 
	 * @param handler
	 * @param channels
	 * @return redis pub/sub object.
	 */
	RedisPubSub subscribe(RedisSubscribeHandler handler, String... channels);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(N) where N is the number of clients already subscribed to a channel.<br>
	 * 
	 * <p>
	 * Unsubscribes the client from the given channels, or from all of them if none is given.<br>
	 * When no channels are specified, the client is unsubscribed from all the previously subscribed channels.<br>
	 * In this case, a message for every unsubscribed channel will be sent to the client.<br>
	 * 
	 * @param pubsub
	 * @param channels
	 */
	 void unsubscribe(RedisPubSub pubsub, String... channels);
	 
	 /**
	  * Available since 2.8.0<br>
      * Time complexity: O(N) for the CHANNELS subcommand, where N is the number of active channels, and assuming constant 
      * time pattern matching (relatively short channels and patterns). O(N) for the NUMSUB subcommand, 
      * where N is the number of requested channels. O(1) for the NUMPAT subcommand.
      * 
      * <p>
	  * The PUBSUB command is an introspection command that allows to inspect the state of the Pub/Sub subsystem. 
	  * It is composed of subcommands that are documented separately. The general form is:
	  * <pre><code>
	  * PUBSUB subcommand ... args ...
	  * </code></pre>
	  * 
	  * <p>
	  * PUBSUB CHANNELS [pattern]
      * Lists the currently active channels. An active channel is a Pub/Sub channel with one ore more subscribers (not including clients subscribed to patterns).
      * If no pattern is specified, all the channels are listed, otherwise if pattern is specified only channels matching the specified glob-style pattern are listed.
      * Return value
      * Array reply: a list of active channels, optionally matching the specified pattern.
      * 
      * <p>
      * PUBSUB NUMSUB [channel-1 ... channel-N]
      * Returns the number of subscribers (not counting clients subscribed to patterns) for the specified channels.
      * Return value
      * Array reply: a list of channels and number of subscribers for every channel. The format is channel, count, channel, count, ..., so the list is flat. The order in which the channels are listed is the same as the order of the channels specified in the command call.
      * Note that it is valid to call this command without channels. In this case it will just return an empty list.
      * 
      * <p>
      * PUBSUB NUMPAT
      * Returns the number of subscriptions to patterns (that are performed using the PSUBSCRIBE command). Note that this is not just the count of clients subscribed to patterns but the total number of patterns all the clients are subscribed to.
      * Return value
      * Integer reply: the number of patterns all the clients are subscribed to.
	  * 
	  * @param pattern
	  * @return
	  */
	 List<String> pubsubchannels(String pattern);
	 Long pubsubnumpat();
	 Map<String, String> pubsubnumsub(String... channels);
	
	
	// ~ ------------------------------------------------------------------------------------------------ Transactions
	
	
	/**
	 * Available since 2.0.0<br>
	 * 
	 * <p>
	 * Flushes all previously queued commands in a transaction and restores the connection state to normal.<br>
	 * If WATCH was used, DISCARD unwatches all keys.<br>
	 * 
	 * @param t
	 * @return always OK.
	 */
	String discard(RedisTransaction t);
	
	/**
	 * Available since 1.2.0<br>
	 * 
	 * <p>
	 * Executes all previously queued commands in a transaction and restores the connection state to normal.<br>
	 * When using WATCH, EXEC will execute commands only if the watched keys were not modified, allowing for a check-and-set mechanism.<br>
	 * 
	 * @param t
	 * @return each element being the reply to each of the commands in the atomic transaction.<br>
	 *         When using WATCH, EXEC can return a empty list if the execution was aborted.<br>
	 */
	List<Object> exec(RedisTransaction t);
	
	/**
	 * Available since 1.2.0<br>
	 * 
	 * <p>
	 * Marks the start of a transaction block. Subsequent commands will be queued for atomic execution using EXEC.<br>
	 * 
	 * @return RedisTransaction
	 */
	RedisTransaction multi();
	
	/**
	 * Available since 2.2.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Flushes all the previously watched keys for a transaction.<br>
	 * If you call EXEC or DISCARD, there's no need to manually call UNWATCH.<br>
	 * 
	 * @return always OK.
	 */
	String unwatch();
	
	/**
	 * Available since 2.2.0<br>
	 * Time complexity: O(1) for every key.<br>
	 * 
	 * <p>
	 * Marks the given keys to be watched for conditional execution of a transaction.<br>
	 * 
	 * @param keys
	 * @return always OK.
	 */
	String watch(String... keys);
	
	
	// ~ --------------------------------------------------------------------------------------------------- Scripting
	
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: Depends on the script that is executed.<br>
	 * 
	 * <p>
	 * EVAL and EVALSHA are used to evaluate scripts using the Lua interpreter built into Redis starting from version 2.6.0.<br>
	 * For details, please see <a href="http://redis.io/commands/eval">Redis Script Document</a>
	 * 
	 * @param script
	 * @return script execution result object.
	 */
	Object eval(String script);
	Object eval(String script, List<String> keys);
	Object eval(String script, List<String> keys, List<String> args);
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: Depends on the script that is executed.<br>
	 * 
	 * <p>
	 * Evaluates a script cached on the server side by its SHA1 digest. <br>
	 * Scripts are cached on the server side using the SCRIPT LOAD command. The command is otherwise identical to EVAL.<br>
	 * 
	 * @param sha1
	 * @return script execution result object.
	 */
	Object evalsha(String sha1);
	Object evalsha(String sha1, List<String> keys);
	Object evalsha(String sha1, List<String> keys, List<String> args);
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: O(N) with N being the number of scripts to check (so checking a single script is an O(1) operation).<br>
	 * 
	 * <p>
	 * Returns information about the existence of the scripts in the script cache.<br>
	 * This command accepts one or more SHA1 digests and returns a list of ones or zeros to signal if the scripts 
	 * are already defined or not inside the script cache. <br>
	 * This can be useful before a pipelining operation to ensure 
	 * that scripts are loaded (and if not, to load them using SCRIPT LOAD) so that the pipelining operation can be performed 
	 * solely using EVALSHA instead of EVAL to save bandwidth.<br>
	 * 
	 * Please refer to the EVAL documentation for detailed information about Redis Lua scripting.<br>
	 * 
	 * @param sha1
	 * @return true if exists, otherwise false
	 */
	Boolean scriptexists(String sha1);
	Boolean[] scriptexists(String... sha1s);
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: O(N) with N being the number of scripts in cache<br>
	 * 
	 * <p>
	 * Flush the Lua scripts cache.<br>
	 * Please refer to the EVAL documentation for detailed information about Redis Lua scripting.<br>
	 * 
	 * @return Status code reply, e.g. OK
	 */
	String scriptflush();
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Kills the currently executing Lua script, assuming no write operation was yet performed by the script.<br>
	 * This command is mainly useful to kill a script that is running for too much time(for instance because it entered an infinite loop because of a bug). <br>
	 * The script will be killed and the client currently blocked into EVAL will see the command returning with an error.<br>
	 * If the script already performed write operations it can not be killed in this way because it would violate Lua script atomicity contract. <br>
	 * In such a case only SHUTDOWN NOSAVE is able to kill the script, killing the Redis process in an hard way preventing 
	 * it to persist with half-written information.<br>
	 * Please refer to the EVAL documentation for detailed information about Redis Lua scripting.<br>
	 * 
	 * @return Status code reply, e.g. OK
	 */
	String scriptkill();
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: O(N) with N being the length in bytes of the script body.<br>
	 * 
	 * <p>
	 * Load a script into the scripts cache, without executing it. <br>
	 * After the specified command is loaded into the script cache it will be callable using EVALSHA with the correct SHA1 digest of the script, 
	 * exactly like after the first successful invocation of EVAL.<br>
	 * The script is guaranteed to stay in the script cache forever (unless SCRIPT FLUSH is called).<br>
	 * The command works in the same way even if the script was already present in the script cache.<br>
	 * Please refer to the EVAL documentation for detailed information about Redis Lua scripting.<br>
	 * 
	 * @param script
	 * @return This command returns the SHA1 digest of the script added into the script cache.
	 */
	String scriptload(String script);
	
	
	// ~ --------------------------------------------------------------------------------------------------- Connection
	
	
	/**
	 * Available since 1.0.0<br>
	 * 
	 * <p>
	 * Request for authentication in a password-protected Redis server. <br>
	 * Redis can be instructed to require a password before allowing clients to execute commands. <br>
	 * This is done using the requirepass directive in the configuration file.<br>
	 * If password matches the password in the configuration file, the server replies with the OK status code and starts accepting commands.<br>
	 *  Otherwise, an error is returned and the clients needs to try a new password.<br>
	 * Note: because of the high performance nature of Redis, it is possible to try a lot of passwords in parallel in very short time, 
	 * so make sure to generate a strong and very long password so that this attack is infeasible.<br>
	 * 
	 * @param password
	 * @return Status code reply, e.g. OK
	 */
	String auth(String password);
	
	/**
	 * Available since 1.0.0<br>
	 * 
	 * @param message
	 * @return the given message
	 */
	String echo(String message);
	
	/**
	 * Available since 1.0.0<br>
	 * 
	 * <p>
	 * Returns PONG. <br>
	 * This command is often used to test if a connection is still alive, or to measure latency.<br>
	 * 
	 * @return PONG
	 */
	String ping();
	
	/**
	 * Available since 1.0.0<br>
	 * 
	 * <p>
	 * Ask the server to close the connection. <br>
	 * The connection is closed as soon as all pending replies have been written to the client.
	 * 
	 * @return always OK.
	 */
	String quit();
	
	/**
	 * Available since 1.0.0<br>
	 * 
	 * <p>
	 * Select the DB with having the specified zero-based numeric index. <br>
	 * New connections always use DB 0.
	 * 
	 * @param index
	 * @return Status code reply, e.g. OK
	 */
	String select(int index);
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Server
	
	
	/**
	 * Available since 1.0.0<br>
	 * 
	 * <p>
	 * Instruct Redis to start an Append Only File rewrite process. <br>
	 * The rewrite will create a small optimized version of the current Append Only File.<br>
	 * If BGREWRITEAOF fails, no data gets lost as the old AOF will be untouched.<br>
	 * The rewrite will be only triggered by Redis if there is not already a background process doing persistence. <br>
	 * Specifically:<br>
	 * If a Redis child is creating a snapshot on disk, the AOF rewrite is scheduled but not started until the saving child producing the RDB file terminates. <br>
	 * In this case the BGREWRITEAOF will still return an OK code, but with an appropriate message. <br>
	 * You can check if an AOF rewrite is scheduled looking at the INFO command as of Redis 2.6.<br>
	 * If an AOF rewrite is already in progress the command returns an error and no AOF rewrite will be scheduled for a later time.<br>
	 * Since Redis 2.4 the AOF rewrite is automatically triggered by Redis, however the BGREWRITEAOF command can be used to trigger a rewrite at any time.<br>
	 * Please refer to the persistence documentation for detailed information.<br>
	 * 
	 * @return Status code reply, e.g. Background append only file rewriting started
	 */
	String bgrewriteaof();
	
	/**
	 * Available since 1.0.0<br>
	 * 
	 * <p>
	 * Save the DB in background. <br>
	 * The OK code is immediately returned. <br>
	 * Redis forks, the parent continues to serve the clients, 
	 * the child saves the DB on disk then exits. <br>
	 * A client my be able to check if the operation succeeded using the LASTSAVE command.<br>
	 * Please refer to the persistence documentation for detailed information.<br>
	 * 
	 * @return Status code reply, e.g. Background saving started
	 */
	String bgsave();
	
	/**
	 * Available since 2.6.9<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * The CLIENT GETNAME returns the name of the current connection as set by CLIENT SETNAME. <br>
	 * Since every new connection starts without an associated name, if no name was assigned a null bulk reply is returned.<br>
	 * 
	 * @return The connection name, or a null bulk reply if no name is set.
	 */
	String clientgetname();
	
	/**
	 * Available since 2.4.0<br>
	 * Time complexity: O(N) where N is the number of client connections<br>
	 * 
	 * <p>
	 * The CLIENT KILL command closes a given client connection identified by ip:port.<br>
	 * The ip:port should match a line returned by the CLIENT LIST command.<br>
	 * Due to the single-treaded nature of Redis, it is not possible to kill a client connection while it is executing a command. <br>
	 * From the client point of view, the connection can never be closed in the middle of the execution of a command. However, 
	 * the client will notice the connection has been closed only when the next command is sent (and results in network error).<br>
	 * 
	 * @param ip
	 * @param port
	 * @return OK if the connection exists and has been closed
	 */
	String clientkill(String ip, int port);
	
	/**
	 * Available since 2.4.0<br>
	 * 
	 * <p>
	 * Time complexity: O(N) where N is the number of client connections
	 * The CLIENT LIST command returns information and statistics about the client connections server in a mostly human readable format.<br>
	 * 
	 * <p>
	 * Returns a unique string, formatted as follows: 
	 * <pre>
	 * addr=127.0.0.1:39185 fd=6 name= age=246223 idle=0 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=0 qbuf-free=32768 obl=0 oll=0 omem=0 events=r cmd=client
	 * </pre>
	 * 
	 * One client connection per line (separated by LF).<br>
	 * Each line is composed of a succession of property=value fields separated by a space character.<br>
	 * Here is the meaning of the fields:
	 * <pre>
	 * addr: address/port of the client
	 * fd: file descriptor corresponding to the socket
	 * age: total duration of the connection in seconds
	 * idle: idle time of the connection in seconds
	 * flags: client flags (see below)
	 * db: current database ID
	 * sub: number of channel subscriptions
	 * psub: number of pattern matching subscriptions
	 * multi: number of commands in a MULTI/EXEC context
	 * qbuf: query buffer length (0 means no query pending)
	 * qbuf-free: free space of the query buffer (0 means the buffer is full)
	 * obl: output buffer length
	 * oll: output list length (replies are queued in this list when the buffer is full)
	 * omem: output buffer memory usage
	 * events: file descriptor events (see below)
	 * cmd: last command played
	 * </pre>
	 * 
	 * The client flags can be a combination of:
	 * <pre>
	 * O: the client is a slave in MONITOR mode
	 * S: the client is a normal slave server
	 * M: the client is a master
	 * x: the client is in a MULTI/EXEC context
	 * b: the client is waiting in a blocking operation
	 * i: the client is waiting for a VM I/O (deprecated)
	 * d: a watched keys has been modified - EXEC will fail
	 * c: connection to be closed after writing entire reply
	 * u: the client is unblocked
	 * A: connection to be closed ASAP
	 * N: no specific flag set
	 * </pre>
	 * 
	 * The file descriptor events can be:
	 * <pre>
	 * r: the client socket is readable (event loop)
	 * w: the client socket is writable (event loop)
	 * </pre>
	 * 
	 * Notes<br>
	 * New fields are regularly added for debugging purpose. Some could be removed in the future. <br>
	 * A version safe Redis client using this command should parse the output accordingly 
	 * (i.e. handling gracefully missing fields, skipping unknown fields).<br>
	 * 
	 * @return a formatted string list
	 */
	List<String> clientlist();
	
	/**
	 * Available since 2.6.9<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * The CLIENT SETNAME command assigns a name to the current connection.<br>
	 * The assigned name is displayed in the output of CLIENT LIST so that it is possible to identify the client that performed a given connection.<br>
	 * For instance when Redis is used in order to implement a queue, producers and consumers of messages may want to 
	 * set the name of the connection according to their role.<br>
	 * There is no limit to the length of the name that can be assigned if not the usual limits of the Redis string type (512 MB). <br>
	 * However it is not possible to use spaces in the connection name as this would violate the format of the CLIENT LIST reply.<br>
	 * It is possible to entirely remove the connection name setting it to the empty string, 
	 * that is not a valid connection name since it serves to this specific purpose.<br>
	 * The connection name can be inspected using CLIENT GETNAME.<br>
	 * Every new connection starts without an assigned name.<br>
	 * Tip: setting names to connections is a good way to debug connection leaks due to bugs in the application using Redis.<br>
	 * 
	 * @param connectionname
	 * @return OK if the connection name was successfully set.
	 */
	String clientsetname(String connectionname);
	
	/**
	 * Available since 2.0.0<br>
	 * 
	 * <p>
	 * The CONFIG GET command is used to read the configuration parameters of a running Redis server. <br>
	 * Not all the configuration parameters are supported in Redis 2.4, while Redis 2.6 can read the whole configuration of a server using this command.<br>
	 * The symmetric command used to alter the configuration at run time is CONFIG SET.<br>
	 * CONFIG GET takes a single argument, which is a glob-style pattern. <br>
	 * All the configuration parameters matching this parameter are reported as a list of key-value pairs. <br>
	 * Example:
	 * <pre>
	 * redis> config get *max-*-entries*
	 * 1) "hash-max-zipmap-entries"
	 * 2) "512"
	 * 3) "list-max-ziplist-entries"
	 * 4) "512"
	 * 5) "set-max-intset-entries"
	 * 6) "512"
	 * </pre>
	 * 
	 * You can obtain a list of all the supported configuration parameters by typing CONFIG GET * in an open redis-cli prompt.<br>
	 * All the supported parameters have the same meaning of the equivalent configuration parameter used in the redis.conf file, 
	 * with the following important differences:<br>
	 * Where bytes or other quantities are specified, it is not possible to use the redis.conf abbreviated form (10k 2gb ... and so forth), 
	 * everything should be specified as a well-formed 64-bit integer, in the base unit of the configuration directive.<br>
	 * The save parameter is a single string of space-separated integers. Every pair of integers represent a seconds/modifications threshold.<br>
	 * For instance what in redis.conf looks like:
	 * <pre>
	 * save 900 1
	 * save 300 10
	 * </pre>
	 * 
	 * that means, save after 900 seconds if there is at least 1 change to the dataset, 
	 * and after 300 seconds if there are at least 10 changes to the datasets, will be reported by CONFIG GET as "900 1 300 10".
	 * 
	 * @param parameter
	 * @return configuration parameters key-value pairs.
	 */
	Map<String, String> configget(String parameter);
	
	/**
	 * Available since 2.0.0<br>
	 * 
	 * <p>
	 * The CONFIG SET command is used in order to reconfigure the server at run time without the need to restart Redis. <br>
	 * You can change both trivial parameters or switch from one to another persistence option using this command.<br>
	 * The list of configuration parameters supported by CONFIG SET can be obtained issuing a CONFIG GET * command, 
	 * that is the symmetrical command used to obtain information about the configuration of a running Redis instance.<br>
	 * 
	 * All the configuration parameters set using CONFIG SET are immediately loaded by Redis and will take effect starting with the next command executed.<br>
	 * All the supported parameters have the same meaning of the equivalent configuration parameter used in the redis.conf file, 
	 * with the following important differences:
	 * 
	 * Where bytes or other quantities are specified, it is not possible to use the redis.conf abbreviated form (10k 2gb ... and so forth), 
	 * everything should be specified as a well-formed 64-bit integer, in the base unit of the configuration directive.<br>
	 * The save parameter is a single string of space-separated integers. Every pair of integers represent a seconds/modifications threshold.<br>
	 * For instance what in redis.conf looks like:
	 * <pre>
	 * save 900 1
	 * save 300 10
	 * </pre>
	 * 
	 * that means, save after 900 seconds if there is at least 1 change to the dataset, and after 300 seconds if there are at least 10 changes to the datasets, 
	 * should be set using CONFIG SET as "900 1 300 10".<br>
	 * It is possible to switch persistence from RDB snapshotting to append-only file (and the other way around) using the CONFIG SET command. <br>
	 * For more information about how to do that please check the persistence page.<br>
	 * In general what you should know is that setting the appendonly parameter to yes will start a background process to 
	 * save the initial append-only file (obtained from the in memory data set), and will append all the subsequent commands on the append-only file, 
	 * thus obtaining exactly the same effect of a Redis server that started with AOF turned on since the start.<br>
	 * You can have both the AOF enabled with RDB snapshotting if you want, the two options are not mutually exclusive.<br>
	 * 
	 * @param parameter
	 * @param value
	 * @return  OK when the configuration was set properly. <br>
	 *          Otherwise an error is returned. <br>
	 */
	String configset(String parameter, String value);
	
	/**
	 * Available since 2.0.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
	 * Resets the statistics reported by Redis using the INFO command.<br>
	 * These are the counters that are reset:<br>
	 * Keyspace hits<br>
	 * Keyspace misses<br>
	 * Number of commands processed<br>
	 * Number of connections received<br>
	 * Number of expired keys<br>
	 * Number of rejected connections<br>
	 * Latest fork(2) time<br>
	 * The aof_delayed_fsync counter<br>
	 * 
	 * @return always OK.
	 */
	String configresetstat();
	
	/**
	 * Available since 1.0.0<br>
	 * 
	 * @return the number of keys in the currently-selected database.
	 */
	Long dbsize();
	
	/**
	 * Available since 1.0.0<br>
	 * 
	 * <p>
	 * DEBUG OBJECT is a debugging command that should not be used by clients. <br>
	 * Check the OBJECT command instead.
	 * 
	 * @param key
	 * @return debugging information about a key
	 */
	String debugobject(String key);
	
	/**
	 * Available since 1.0.0<br>
	 * 
	 * <p>
	 * DEBUG SEGFAULT performs an invalid memory access that crashes Redis. <br>
	 * It is used to simulate bugs during the development.<br>
	 * 
	 * @return Status code reply, e.g. OK
	 */
	String debugsegfault();
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * Delete all the keys of all the existing databases, not just the currently selected one. <br>
	 * This command never fails.<br>
	 * 
	 * @return Status code reply, e.g. OK
	 */
	String flushall();
	
	/**
	 * Available since 1.0.0<br>
	 * 
	 * <p>
	 * Delete all the keys of the currently selected DB. <br>
	 * This command never fails.<br>
	 * 
	 * @return Status code reply, e.g. OK
	 */
	String flushdb();
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * The INFO command returns information and statistics about the server in a format that is simple to parse by computers and easy to read by humans.<br>
	 * The optional parameter can be used to select a specific section of information:
	 * <pre>
	 * server: General information about the Redis server
	 * clients: Client connections section
	 * memory: Memory consumption related information
	 * persistence: RDB and AOF related information
	 * stats: General statistics
	 * replication: Master/slave replication information
	 * cpu: CPU consumption statistics
	 * commandstats: Redis command statistics
	 * cluster: Redis Cluster section
	 * keyspace: Database related statistics
	 * </pre>
	 * 
	 * It can also take the following values:
	 * <pre>
	 * all: Return all sections
	 * default: Return only the default set of sections
	 * </pre>
	 * 
	 * When no parameter is provided, the default option is assumed.<br>
	 * Return value<br>
	 * Bulk reply: as a collection of text lines.<br>
	 * Lines can contain a section name (starting with a # character) or a property. <br>
	 * All the properties are in the form of field:value terminated by \r\n.
	 * <pre>
	 * redis> INFO
	 * # Server
	 * redis_version:2.5.13
	 * redis_git_sha1:2812b945
	 * redis_git_dirty:0
	 * os:Linux 2.6.32.16-linode28 i686
	 * arch_bits:32
	 * multiplexing_api:epoll
	 * gcc_version:4.4.1
	 * process_id:8107
	 * run_id:2e0192d968d1d4a36a927413d3b4ae4aa46e7ccd
	 * tcp_port:6379
	 * uptime_in_seconds:12571289
	 * uptime_in_days:145
	 * lru_clock:761289
	 * 
	 * # Clients
	 * connected_clients:8
	 * client_longest_output_list:0
	 * client_biggest_input_buf:0
	 * blocked_clients:0
	 *
	 * # Memory
	 * used_memory:1327064
	 * used_memory_human:1.27M
	 * used_memory_rss:2146304
	 * used_memory_peak:1737576
	 * used_memory_peak_human:1.66M
	 * used_memory_lua:20480
	 * mem_fragmentation_ratio:1.62
	 * mem_allocator:jemalloc-3.0.0
	 * 
	 * # Persistence
	 * loading:0
	 * rdb_changes_since_last_save:3
	 * rdb_bgsave_in_progress:0
	 * rdb_last_save_time:1370761692
	 * rdb_last_bgsave_status:ok
	 * rdb_last_bgsave_time_sec:0
	 * rdb_current_bgsave_time_sec:-1
	 * aof_enabled:0
	 * aof_rewrite_in_progress:0
	 * aof_rewrite_scheduled:0
	 * aof_last_rewrite_time_sec:-1
	 * aof_current_rewrite_time_sec:-1
	 * aof_last_bgrewrite_status:ok
	 * 
	 * # Stats
	 * total_connections_received:5532
	 * total_commands_processed:32348773
	 * instantaneous_ops_per_sec:0
	 * rejected_connections:0
	 * expired_keys:32035
	 * evicted_keys:0
	 * keyspace_hits:7829334
	 * keyspace_misses:1923266
	 * pubsub_channels:0
	 * pubsub_patterns:0
	 * latest_fork_usec:1295
 	 * 
	 * # Replication
	 * role:master
	 * connected_slaves:0
	 * 
	 * # CPU
	 * used_cpu_sys:3664.41
	 * used_cpu_user:1653.62
	 * used_cpu_sys_children:204.48
	 * used_cpu_user_children:493.54
	 * 
	 * # Keyspace
	 * db0:keys=10572,expires=1
	 * redis>
	 * </pre>
	 * 
	 * Notes<br>
	 * Please note depending on the version of Redis some of the fields have been added or removed. <br>
	 * A robust client application should therefore parse the result of this command by skipping unknown properties, and gracefully handle missing fields.
	 * Here is the description of fields for Redis >= 2.4.
	 * 
	 * <p>
	 * Here is the meaning of all fields in the server section:
	 * <pre>
	 * redis_version: Version of the Redis server
	 * redis_git_sha1: Git SHA1
	 * redis_git_dirty: Git dirty flag
	 * os: Operating system hosting the Redis server
	 * arch_bits: Architecture (32 or 64 bits)
	 * multiplexing_api: event loop mechanism used by Redis
	 * gcc_version: Version of the GCC compiler used to compile the Redis server
	 * process_id: PID of the server process
	 * run_id: Random value identifying the Redis server (to be used by Sentinel and Cluster)
	 * tcp_port: TCP/IP listen port
	 * uptime_in_seconds: Number of seconds since Redis server start
	 * uptime_in_days: Same value expressed in days
	 * lru_clock: Clock incrementing every minute, for LRU management
	 * </pre>
	 * 
	 * <p>
	 * Here is the meaning of all fields in the clients section:
	 * <pre>
	 * connected_clients: Number of client connections (excluding connections from slaves)
	 * client_longest_output_list: longest output list among current client connections
	 * client_biggest_input_buf: biggest input buffer among current client connections
	 * blocked_clients: Number of clients pending on a blocking call (BLPOP, BRPOP, BRPOPLPUSH)
	 * </pre>
	 * 
	 * <p>
	 * Here is the meaning of all fields in the memory section:
	 * <pre>
	 * used_memory: total number of bytes allocated by Redis using its allocator (either standard libc, jemalloc, or an alternative allocator such as tcmalloc
	 * used_memory_human: Human readable representation of previous value
	 * used_memory_rss: Number of bytes that Redis allocated as seen by the operating system (a.k.a resident set size). This is the number reported by tools such as top and ps.
     * used_memory_peak: Peak memory consumed by Redis (in bytes)
	 * used_memory_peak_human: Human readable representation of previous value
	 * used_memory_lua: Number of bytes used by the Lua engine
  	 * mem_fragmentation_ratio: Ratio between used_memory_rss and used_memory
	 * mem_allocator: Memory allocator, chosen at compile time.
	 * Ideally, the used_memory_rss value should be only slightly higher than used_memory. When rss >> used, 
	 * a large difference means there is memory fragmentation (internal or external), which can be evaluated by checking mem_fragmentation_ratio. 
	 * When used >> rss, it means part of Redis memory has been swapped off by the operating system: expect some significant latencies.
	 * Because Redis does not have control over how its allocations are mapped to memory pages, 
	 * high used_memory_rss is often the result of a spike in memory usage.
	 * When Redis frees memory, the memory is given back to the allocator, and the allocator may or may not give the memory back to the system. 
	 * There may be a discrepancy between the used_memory value and memory consumption as reported by the operating system. 
	 * It may be due to the fact memory has been used and released by Redis, but not given back to the system. 
	 * The used_memory_peak value is generally useful to check this point.
	 * </pre>
	 * 
	 * <p>
	 * Here is the meaning of all fields in the persistence section:
	 * <pre>
	 * loading: Flag indicating if the load of a dump file is on-going
	 * rdb_changes_since_last_save: Number of changes since the last dump
	 * rdb_bgsave_in_progress: Flag indicating a RDB save is on-going
	 * rdb_last_save_time: Epoch-based timestamp of last successful RDB save
	 * rdb_last_bgsave_status: Status of the last RDB save operation
	 * rdb_last_bgsave_time_sec: Duration of the last RDB save operation in seconds
	 * rdb_current_bgsave_time_sec: Duration of the on-going RDB save operation if any
	 * aof_enabled: Flag indicating AOF logging is activated
	 * aof_rewrite_in_progress: Flag indicating a AOF rewrite operation is on-going
	 * aof_rewrite_scheduled: Flag indicating an AOF rewrite operation will be scheduled once the on-going RDB save is complete.
	 * aof_last_rewrite_time_sec: Duration of the last AOF rewrite operation in seconds
	 * aof_current_rewrite_time_sec: Duration of the on-going AOF rewrite operation if any
	 * aof_last_bgrewrite_status: Status of the last AOF rewrite operation
	 * changes_since_last_save refers to the number of operations that produced some kind of changes in the dataset since the last time either SAVE or BGSAVE was called.
	 * If AOF is activated, these additional fields will be added:
	 * aof_current_size: AOF current file size
	 * aof_base_size: AOF file size on latest startup or rewrite
	 * aof_pending_rewrite: Flag indicating an AOF rewrite operation will be scheduled once the on-going RDB save is complete.
	 * aof_buffer_length: Size of the AOF buffer
	 * aof_rewrite_buffer_length: Size of the AOF rewrite buffer
	 * aof_pending_bio_fsync: Number of fsync pending jobs in background I/O queue
	 * aof_delayed_fsync: Delayed fsync counter 
	 * If a load operation is on-going, these additional fields will be added:
	 * loading_start_time: Epoch-based timestamp of the start of the load operation
	 * loading_total_bytes: Total file size
	 * loading_loaded_bytes: Number of bytes already loaded
	 * loading_loaded_perc: Same value expressed as a percentage
	 * loading_eta_seconds: ETA in seconds for the load to be complete
	 * </pre>
	 * 
	 * <p>
	 * Here is the meaning of all fields in the stats section:
	 * <pre>
	 * total_connections_received: Total number of connections accepted by the server
	 * total_commands_processed: Total number of commands processed by the server
	 * instantaneous_ops_per_sec: Number of commands processed per second
	 * rejected_connections: Number of connections rejected because of maxclients limit
	 * expired_keys: Total number of key expiration events
	 * evicted_keys: Number of evicted keys due to maxmemory limit
	 * keyspace_hits: Number of successful lookup of keys in the main dictionary
	 * keyspace_misses: Number of failed lookup of keys in the main dictionary
	 * pubsub_channels: Global number of pub/sub channels with client subscriptions
	 * pubsub_patterns: Global number of pub/sub pattern with client subscriptions
	 * latest_fork_usec: Duration of the latest fork operation in microseconds
	 * </pre>
	 * 
	 * <p>
	 * Here is the meaning of all fields in the replication section:
	 * <pre>
	 * role: Value is "master" if the instance is slave of no one, or "slave" if the instance is enslaved to a master. Note that a slave can be master of another slave (daisy chaining).
	 * If the instance is a slave, these additional fields are provided:
	 * master_host: Host or IP address of the master
	 * master_port: Master listening TCP port
	 * master_link_status: Status of the link (up/down)
	 * master_last_io_seconds_ago: Number of seconds since the last interaction with master
	 * master_sync_in_progress: Indicate the master is SYNCing to the slave
	 * If a SYNC operation is on-going, these additional fields are provided:
	 * master_sync_left_bytes: Number of bytes left before SYNCing is complete
	 * master_sync_last_io_seconds_ago: Number of seconds since last transfer I/O during a SYNC operation
	 * If the link between master and slave is down, an additional field is provided:
	 * master_link_down_since_seconds: Number of seconds since the link is down
	 * The following field is always provided:
	 * connected_slaves: Number of connected slaves
	 * For each slave, the following line is added:
	 * slaveXXX: id, ip address, port, state
	 * </pre>
	 * 
	 * <p>
	 * Here is the meaning of all fields in the cpu section:
	 * <pre>
	 * used_cpu_sys: System CPU consumed by the Redis server
	 * used_cpu_user:User CPU consumed by the Redis server
	 * used_cpu_sys_children: System CPU consumed by the background processes
	 * used_cpu_user_children: User CPU consumed by the background processes
	 * </pre>
	 * 
	 * The commandstats section provides statistics based on the command type, including the number of calls, 
	 * the total CPU time consumed by these commands, and the average CPU consumed per command execution.<br>
	 * For each command type, the following line is added:<br>
	 * <pre>
	 * cmdstat_XXX:calls=xxx,usec=xxx,usecpercall=xxx
	 * </pre>
	 * 
	 * The cluster section currently only contains a unique field:<br>
	 * <pre>
	 * cluster_enabled: Indicate Redis cluster is enabled
	 * </pre>
	 * 
	 * The keyspace section provides statistics on the main dictionary of each database. <br>
	 * The statistics are the number of keys, and the number of keys with an expiration. <br>
	 * For each database, the following line is added:
	 * <pre>
	 * dbXXX:keys=xxx,expires=xxx
	 * </pre>
	 * 
	 * @return as a collection of text lines.
	 */
	String info();
	String info(String section);
	
	/**
	 * Available since 1.0.0<br>
	 * 
	 * <p>
	 * Return the UNIX TIME of the last DB save executed with success. <br>
	 * A client may check if a BGSAVE command succeeded reading the LASTSAVE value, 
	 * then issuing a BGSAVE command and checking at regular intervals every N seconds if LASTSAVE changed.<br>
	 * 
	 * @return an UNIX time stamp.
	 */
	Long lastsave();
	
	/**
	 * Available since 1.0.0<br>
	 * 
	 * <p>
	 * MONITOR is a debugging command that streams back every command processed by the Redis server.<br> 
	 * It can help in understanding what is happening to the database. <br>
	 * This command can both be used via redis-cli and via telnet.<br>
	 * The ability to see all the requests processed by the server is useful in order to spot bugs in an application 
	 * both when using Redis as a database and as a distributed caching system.<br>
	 * <pre>
	 * $ redis-cli monitor
	 * 1339518083.107412 [0 127.0.0.1:60866] "keys" "*"
	 * 1339518087.877697 [0 127.0.0.1:60866] "dbsize"
	 * 1339518090.420270 [0 127.0.0.1:60866] "set" "x" "6"
	 * 1339518096.506257 [0 127.0.0.1:60866] "get" "x"
	 * 1339518099.363765 [0 127.0.0.1:60866] "del" "x"
	 * 1339518100.544926 [0 127.0.0.1:60866] "get" "x"
	 * </pre>
	 * 
	 * Use SIGINT (Ctrl-C) to stop a MONITOR stream running via redis-cli.
	 * <pre>
	 * $ telnet localhost 6379
	 * Trying 127.0.0.1...
	 * Connected to localhost.
	 * Escape character is '^]'.
	 * MONITOR
	 * +OK
	 * +1339518083.107412 [0 127.0.0.1:60866] "keys" "*"
	 * +1339518087.877697 [0 127.0.0.1:60866] "dbsize"
	 * +1339518090.420270 [0 127.0.0.1:60866] "set" "x" "6"
	 * +1339518096.506257 [0 127.0.0.1:60866] "get" "x"
	 * +1339518099.363765 [0 127.0.0.1:60866] "del" "x"
	 * +1339518100.544926 [0 127.0.0.1:60866] "get" "x"
	 * QUIT
	 * +OK
	 * Connection closed by foreign host.
	 * </pre>
	 * Manually issue the QUIT command to stop a MONITOR stream running via telnet.<br>
	 * 
	 * Cost of running MONITOR<br>
	 * Because MONITOR streams back all commands, its use comes at a cost. The following (totally unscientific) benchmark numbers 
	 * illustrate what the cost of running MONITOR can be.<br>
	 * Benchmark result without MONITOR running:<br>
	 * <pre>
	 * $ src/redis-benchmark -c 10 -n 100000 -q
	 * PING_INLINE: 101936.80 requests per second
	 * PING_BULK: 102880.66 requests per second
	 * SET: 95419.85 requests per second
	 * GET: 104275.29 requests per second
	 * INCR: 93283.58 requests per second
	 * </pre>
	 * Benchmark result with MONITOR running (redis-cli monitor > /dev/null):
	 * 
	 * <pre>
	 * $ src/redis-benchmark -c 10 -n 100000 -q
	 * PING_INLINE: 58479.53 requests per second
	 * PING_BULK: 59136.61 requests per second
	 * SET: 41823.50 requests per second
	 * GET: 45330.91 requests per second
	 * INCR: 41771.09 requests per second
	 * </pre>
	 * In this particular case, running a single MONITOR client can reduce the throughput by more than 50%. 
	 * Running more MONITOR clients will reduce throughput even more.<br>
	 * 
	 * Return value<br>
	 * Non standard return value, just dumps the received commands in an infinite flow.
	 * 
	 * @param handler
	 */
	void monitor(RedisMonitorHandler handler);
	
	/**
	 * Available since 1.0.0<br>
	 * 
	 * <pre>
	 * The SAVE commands performs a synchronous save of the dataset producing a point in time snapshot of all the data inside the Redis instance, 
	 * in the form of an RDB file.<br>
	 * You almost never want to call SAVE in production environments where it will block all the other clients. <br>
	 * Instead usually BGSAVE is used. <br>
	 * However in case of issues preventing Redis to create the background saving child 
	 * (for instance errors in the fork(2) system call), the SAVE command can be a good last resort to perform the dump of the latest dataset.<br>
	 * Please refer to the persistence documentation for detailed information.<br>
	 * 
	 * @return The commands returns OK on success.
	 */
	String save();
	
	/**
	 * Available since 1.0.0<br>
	 * 
	 * <p>
	 * The command behavior is the following:<br>
	 * Stop all the clients.<br>
	 * Perform a blocking SAVE if at least one save point is configured.<br>
	 * Flush the Append Only File if AOF is enabled.<br>
	 * Quit the server.<br>
	 * If persistence is enabled this commands makes sure that Redis is switched off without the lost of any data. <br>
	 * This is not guaranteed if the client uses simply SAVE and then QUIT because other clients may alter the DB data between the two commands.<br>
	 * Note: A Redis instance that is configured for not persisting on disk (no AOF configured, nor "save" directive) 
	 * will not dump the RDB file on SHUTDOWN, as usually you don't want Redis instances used only for caching to block on when shutting down.<br>
	 * 
	 * SAVE and NOSAVE modifiers<br>
	 * It is possible to specify an optional modifier to alter the behavior of the command. <br>
	 * Specifically:<br>
	 * SHUTDOWN SAVE will force a DB saving operation even if no save points are configured.<br>
	 * SHUTDOWN NOSAVE will prevent a DB saving operation even if one or more save points are configured. <br>
	 * (You can think at this variant as an hypothetical ABORT command that just stops the server)
	 * 
	 * @return on error. On success nothing is returned since the server quits and the connection is closed.
	 */
	String shutdown(boolean save);
	
	/**
	 * Available since 1.0.0<br>
	 * 
	 * <p>
	 * The SLAVEOF command can change the replication settings of a slave on the fly. <br>
	 * If a Redis server is already acting as slave, the command SLAVEOF NO ONE will turn off the replication, 
	 * turning the Redis server into a MASTER. <br>
	 * In the proper form SLAVEOF hostname port will make the server a slave of another server listening at the specified hostname and port.<br>
	 * 
	 * If a server is already a slave of some master, SLAVEOF hostname port will stop the replication against the old server 
	 * and start the synchronization against the new one, discarding the old dataset.<br>
	 * The form SLAVEOF NO ONE will stop replication, turning the server into a MASTER, but will not discard the replication. <br>
	 * So, if the old master stops working, it is possible to turn the slave into a master and set the application to use this new master in read/write. 
	 * Later when the other Redis server is fixed, it can be reconfigured to work as a slave.
	 * 
	 * @param host
	 * @param port
	 * @return Status code reply, e.g. OK
	 */
	String slaveof(String host, int port);
	String slaveofnoone();
	
	/**
	 * Available since 2.2.12<br>
	 * 
	 * <p>
	 * This command is used in order to read and reset the Redis slow queries log.<br>
	 * 
	 * Redis slow log overview<br>
	 * The Redis Slow Log is a system to log queries that exceeded a specified execution time. <br>
	 * The execution time does not include I/O operations like talking with the client, sending the reply and so forth, 
	 * but just the time needed to actually execute the command (this is the only stage of command execution where the 
	 * thread is blocked and can not serve other requests in the meantime).<br>
	 * 
	 * You can configure the slow log with two parameters: slowlog-log-slower-than tells Redis what is the execution time, 
	 * in microseconds, to exceed in order for the command to get logged. Note that a negative number disables the slow log,
	 * while a value of zero forces the logging of every command. <br>
	 * slowlog-max-len is the length of the slow log. <br>
	 * The minimum value is zero. When a new command is logged and the 
	 * slow log is already at its maximum length, the oldest one is removed from the queue of logged commands in order to make space.
	 * The configuration can be done by editing redis.conf or while the server is running using the CONFIG GET and CONFIG SET commands.<br>
	 * 
	 * Reading the slow log<br>
	 * The slow log is accumulated in memory, so no file is written with information about the slow command executions. <br>
	 * This makes the slow log remarkably fast at the point that you can enable the logging of all the commands 
	 * (setting the slowlog-log-slower-than config parameter to zero) with minor performance hit.<br>
	 * To read the slow log the SLOWLOG GET command is used, that returns every entry in the slow log. <br>
	 * It is possible to return only the N most recent entries passing an additional argument to the command (for instance SLOWLOG GET 10).
	 * Note that you need a recent version of redis-cli in order to read the slow log output, 
	 * since it uses some features of the protocol that were not formerly implemented in redis-cli (deeply nested multi bulk replies).
	 * 
	 * Output format
	 * <pre>
	 * redis 127.0.0.1:6379> slowlog get 2
	 * 1) 1) (integer) 14
   	 *    2) (integer) 1309448221
   	 *    3) (integer) 15
   	 *    4) 1) "ping"
	 * 2) 1) (integer) 13
   	 *    2) (integer) 1309448128
   	 *    3) (integer) 30
   	 *    4) 1) "slowlog"
     *       2) "get"
     *       3) "100"
     *</pre>
	 * Every entry is composed of four fields:<br>
	 * A unique progressive identifier for every slow log entry.<br>
	 * The unix timestamp at which the logged command was processed.<br>
	 * The amount of time needed for its execution, in microseconds.<br>
	 * The array composing the arguments of the command.<br>
	 * The entry's unique ID can be used in order to avoid processing slow log entries multiple times (for instance you may have a script sending you an email alert for every new slow log entry).
	 * The ID is never reset in the course of the Redis server execution, only a server restart will reset it.<br>
	 * 
	 * Obtaining the current length of the slow log<br>
	 * It is possible to get just the length of the slow log using the command SLOWLOG LEN.<br>
	 * Resetting the slow log.<br>
	 * You can reset the slow log using the SLOWLOG RESET command. Once deleted the information is lost forever.<br>
	 * 
	 * @return slow queries log
	 */
	List<Slowlog> slowlogget();
	List<Slowlog> slowlogget(long len);
	String slowlogreset();
	Long slowloglen();
	
	/**
	 * Available since 1.0.0
	 */
	void sync();
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
     * Returns the current time in milliseconds<br>
     * 
	 * @return the difference, measured in milliseconds, between the current time and midnight, January 1, 1970 UTC.
	 */
	Long time();
	
	/**
	 * Available since 2.6.0<br>
	 * Time complexity: O(1)<br>
	 * 
	 * <p>
     * Returns the current time in microseconds<br>
     * 
	 * @return the difference, measured in microseconds, between the current time and midnight, January 1, 1970 UTC.
	 */
	Long microtime();
	
}

package org.craft.atom.redis.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.craft.atom.redis.api.handler.RedisMonitorHandler;
import org.craft.atom.redis.api.handler.RedisPsubscribeHandler;
import org.craft.atom.redis.api.handler.RedisSubscribeHandler;

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
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Strings
	
	
	/**
	 * @see {@link #bitnot(String, String)}
	 * @param destKey
	 * @param keys
	 * @return
	 */
	long bitand(String destkey, String... keys);
	long bitor(String destkey, String... keys);
	long bitxor(String destkey, String... keys);
	
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
	Map<String, String> blpop(int timeout, String... keys);
	
	/**
	 * @see {@link RedisCommand#brpop(String)}
	 * @param timeout
	 * @param keys
	 * @return A empty map(nil multi-bulk) when no element could be popped and the timeout expired.
	 *         A map (two-element multi-bulk) with the key (first element) being the name of the key where an element was popped 
	 *         and the value (second element) being the value of the popped element.
	 */
	Map<String, String> brpop(String... keys);
	Map<String, String> brpop(int timeout, String... keys);
	
	
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
	
	
	// ~ -------------------------------------------------------------------------------------------------- Sorted Sets
	
	
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
	long zinterstoremax(String destination, String... keys);
	long zinterstoremin(String destination, String... keys);
	long zinterstore(String destination, Map<String, Integer> weightkeys);
	long zinterstoremax(String destination, Map<String, Integer> weightkeys);
	long zinterstoremin(String destination, Map<String, Integer> weightkeys);
	
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
	long zunionstoremax(String destination, String... keys);
	long zunionstoremin(String destination, String... keys);
	long zunionstore(String destination, Map<String, Integer> weightkeys);
	long zunionstoremax(String destination, Map<String, Integer> weightkeys);
	long zunionstoremin(String destination, Map<String, Integer> weightkeys);
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Pub/Sub
	
	
	/**
	 * @see {@link #punsubscribe(RedisPsubscribeHandler, String)}
	 * @param handler
	 * @param patterns
	 */
	void psubscribe(RedisPsubscribeHandler handler, String... patterns);
	
	/**
	 * @see {@link #punsubscribe(RedisPunsubscribeHandler, String)}
	 * @param handler
	 * @param patterns
	 * @return unsubscribed patterns
	 */
	List<String> punsubscribe(String... patterns);
	
	/**
	 * @see {@link #subscribe(RedisSubscribeHandler, String)}
	 * @param handler
	 * @param channels
	 */
	void subscribe(RedisSubscribeHandler handler, String... channels);
	
	/**
	 * @see {@link #unsubscribe(String)}}
	 * @param channel
	 * @return unsubscribed channels
	 */
	List<String> unsubscribe(String... channels);
	
	
	// ~ ------------------------------------------------------------------------------------------------ Transactions
	
	
	/**
	 * Available since 2.0.0
	 * 
	 * <p>
	 * Flushes all previously queued commands in a transaction and restores the connection state to normal.
	 * If WATCH was used, DISCARD unwatches all keys.
	 * 
	 * @return always OK.
	 */
	String discard();
	
	/**
	 * Available since 1.2.0
	 * 
	 * <p>
	 * Executes all previously queued commands in a transaction and restores the connection state to normal.
	 * When using WATCH, EXEC will execute commands only if the watched keys were not modified, allowing for a check-and-set mechanism.
	 * 
	 * @return each element being the reply to each of the commands in the atomic transaction.
	 *         When using WATCH, EXEC can return a empty list if the execution was aborted.
	 */
	List<Object> exec();
	
	/**
	 * Available since 1.2.0
	 * 
	 * <p>
	 * Marks the start of a transaction block. Subsequent commands will be queued for atomic execution using EXEC.
	 * 
	 * @return always OK.
	 */
	String multi();
	
	/**
	 * Available since 2.2.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Flushes all the previously watched keys for a transaction.
	 * If you call EXEC or DISCARD, there's no need to manually call UNWATCH.
	 * 
	 * @return always OK.
	 */
	String unwatch();
	
	/**
	 * Available since 2.2.0
	 * Time complexity: O(1) for every key.
	 * 
	 * <p>
	 * Marks the given keys to be watched for conditional execution of a transaction.
	 * 
	 * @param keys
	 * @return always OK.
	 */
	String watch(String... keys);
	
	
	// ~ --------------------------------------------------------------------------------------------------- Scripting
	
	
	/**
	 * @see {@link #scriptexists(String)}
	 * @param sha1
	 * @return
	 */
	boolean[] scriptexists(String... sha1);
	
	/**
	 * Available since 2.6.0
	 * Time complexity: O(N) with N being the number of scripts in cache
	 * 
	 * <p>
	 * Flush the Lua scripts cache.
	 * Please refer to the EVAL documentation for detailed information about Redis Lua scripting.
	 * 
	 * @return Status code reply, e.g. OK
	 */
	String scriptflush();
	
	/**
	 * Available since 2.6.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Kills the currently executing Lua script, assuming no write operation was yet performed by the script.
	 * This command is mainly useful to kill a script that is running for too much time(for instance because it entered an infinite loop because of a bug). 
	 * The script will be killed and the client currently blocked into EVAL will see the command returning with an error.
	 * If the script already performed write operations it can not be killed in this way because it would violate Lua script atomicity contract. 
	 * In such a case only SHUTDOWN NOSAVE is able to kill the script, killing the Redis process in an hard way preventing 
	 * it to persist with half-written information.
	 * Please refer to the EVAL documentation for detailed information about Redis Lua scripting.
	 * 
	 * @return Status code reply, e.g. OK
	 */
	String scriptkill();
	
	
	// ~ --------------------------------------------------------------------------------------------------- Connection
	
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * Request for authentication in a password-protected Redis server. 
	 * Redis can be instructed to require a password before allowing clients to execute commands. 
	 * This is done using the requirepass directive in the configuration file.
	 * If password matches the password in the configuration file, the server replies with the OK status code and starts accepting commands.
	 *  Otherwise, an error is returned and the clients needs to try a new password.
	 * Note: because of the high performance nature of Redis, it is possible to try a lot of passwords in parallel in very short time, 
	 * so make sure to generate a strong and very long password so that this attack is infeasible.
	 * 
	 * @param password
	 * @return Status code reply, e.g. OK
	 */
	String auth(String password);
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * Returns message.
	 * 
	 * @param message
	 * @return
	 */
	String echo(String message);
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * Returns PONG. This command is often used to test if a connection is still alive, or to measure latency.
	 * 
	 * @return PONG
	 */
	String ping();
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * Ask the server to close the connection. The connection is closed as soon as all pending replies have been written to the client.
	 * 
	 * @return always OK.
	 */
	String quit();
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * Select the DB with having the specified zero-based numeric index. New connections always use DB 0.
	 * 
	 * @param index
	 * @return Status code reply, e.g. OK
	 */
	String select(int index);
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Server
	
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * Instruct Redis to start an Append Only File rewrite process. The rewrite will create a small optimized version of the current Append Only File.
	 * If BGREWRITEAOF fails, no data gets lost as the old AOF will be untouched.
	 * The rewrite will be only triggered by Redis if there is not already a background process doing persistence. Specifically:
	 * If a Redis child is creating a snapshot on disk, the AOF rewrite is scheduled but not started until the saving child producing the RDB file terminates. 
	 * In this case the BGREWRITEAOF will still return an OK code, but with an appropriate message. You can check if an AOF rewrite is scheduled looking at the INFO command as of Redis 2.6.
	 * If an AOF rewrite is already in progress the command returns an error and no AOF rewrite will be scheduled for a later time.
	 * Since Redis 2.4 the AOF rewrite is automatically triggered by Redis, however the BGREWRITEAOF command can be used to trigger a rewrite at any time.
	 * Please refer to the persistence documentation for detailed information.
	 * 
	 * @return always OK.
	 */
	String bgrewriteaof();
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * Save the DB in background. The OK code is immediately returned. Redis forks, the parent continues to serve the clients, 
	 * the child saves the DB on disk then exits. A client my be able to check if the operation succeeded using the LASTSAVE command.
	 * Please refer to the persistence documentation for detailed information.
	 * 
	 * @return Status code reply, e.g. OK
	 */
	String bgsave();
	
	/**
	 * Available since 2.6.9
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * The CLIENT GETNAME returns the name of the current connection as set by CLIENT SETNAME. 
	 * Since every new connection starts without an associated name, if no name was assigned a null bulk reply is returned.
	 * 
	 * @return The connection name, or a null bulk reply if no name is set.
	 */
	String clientgetname();
	
	/**
	 * Available since 2.4.0
	 * Time complexity: O(N) where N is the number of client connections
	 * 
	 * <p>
	 * The CLIENT KILL command closes a given client connection identified by ip:port.
	 * The ip:port should match a line returned by the CLIENT LIST command.
	 * Due to the single-treaded nature of Redis, it is not possible to kill a client connection while it is executing a command. 
	 * From the client point of view, the connection can never be closed in the middle of the execution of a command. However, 
	 * the client will notice the connection has been closed only when the next command is sent (and results in network error).
	 * 
	 * @param ip
	 * @param port
	 * @return OK if the connection exists and has been closed
	 */
	String clientkill(String ip, int port);
	
	/**
	 * Available since 2.4.0
	 * 
	 * <p>
	 * Time complexity: O(N) where N is the number of client connections
	 * The CLIENT LIST command returns information and statistics about the client connections server in a mostly human readable format.
	 * 
	 * <p>
	 * Returns a unique string, formatted as follows: 
	 * <pre>
	 * addr=127.0.0.1:39185 fd=6 name= age=246223 idle=0 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=0 qbuf-free=32768 obl=0 oll=0 omem=0 events=r cmd=client
	 * </pre>
	 * 
	 * One client connection per line (separated by LF)
	 * Each line is composed of a succession of property=value fields separated by a space character.
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
	 * Notes
	 * New fields are regularly added for debugging purpose. Some could be removed in the future. 
	 * A version safe Redis client using this command should parse the output accordingly 
	 * (i.e. handling gracefully missing fields, skipping unknown fields).
	 * 
	 * @return a formatted string list
	 */
	List<String> clientlist();
	
	/**
	 * Available since 2.6.9
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * The CLIENT SETNAME command assigns a name to the current connection.
	 * The assigned name is displayed in the output of CLIENT LIST so that it is possible to identify the client that performed a given connection.
	 * For instance when Redis is used in order to implement a queue, producers and consumers of messages may want to 
	 * set the name of the connection according to their role.
	 * There is no limit to the length of the name that can be assigned if not the usual limits of the Redis string type (512 MB). 
	 * However it is not possible to use spaces in the connection name as this would violate the format of the CLIENT LIST reply.
	 * It is possible to entirely remove the connection name setting it to the empty string, 
	 * that is not a valid connection name since it serves to this specific purpose.
	 * The connection name can be inspected using CLIENT GETNAME.
	 * Every new connection starts without an assigned name.
	 * Tip: setting names to connections is a good way to debug connection leaks due to bugs in the application using Redis.
	 * 
	 * @param connectionName
	 * @return OK if the connection name was successfully set.
	 */
	String clientsetname(String connectionName);
	
	/**
	 * Available since 2.0.0
	 * 
	 * <p>
	 * The CONFIG GET command is used to read the configuration parameters of a running Redis server. 
	 * Not all the configuration parameters are supported in Redis 2.4, while Redis 2.6 can read the whole configuration of a server using this command.
	 * The symmetric command used to alter the configuration at run time is CONFIG SET.
	 * CONFIG GET takes a single argument, which is a glob-style pattern. 
	 * All the configuration parameters matching this parameter are reported as a list of key-value pairs. 
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
	 * You can obtain a list of all the supported configuration parameters by typing CONFIG GET * in an open redis-cli prompt.
	 * All the supported parameters have the same meaning of the equivalent configuration parameter used in the redis.conf file, 
	 * with the following important differences:
	 * Where bytes or other quantities are specified, it is not possible to use the redis.conf abbreviated form (10k 2gb ... and so forth), 
	 * everything should be specified as a well-formed 64-bit integer, in the base unit of the configuration directive.
	 * The save parameter is a single string of space-separated integers. Every pair of integers represent a seconds/modifications threshold.
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
	 * @return
	 */
	String configget(String parameter);
	
	/**
	 * Available since 2.0.0
	 * Time complexity: O(1)
	 * 
	 * <p>
	 * Resets the statistics reported by Redis using the INFO command.
	 * These are the counters that are reset:
	 * Keyspace hits
	 * Keyspace misses
	 * Number of commands processed
	 * Number of connections received
	 * Number of expired keys
	 * Number of rejected connections
	 * Latest fork(2) time
	 * The aof_delayed_fsync counter
	 * 
	 * @return always OK.
	 */
	String configresetstat();
	
	/**
	 * Available since 2.0.0
	 * 
	 * <p>
	 * The CONFIG SET command is used in order to reconfigure the server at run time without the need to restart Redis. 
	 * You can change both trivial parameters or switch from one to another persistence option using this command.
	 * The list of configuration parameters supported by CONFIG SET can be obtained issuing a CONFIG GET * command, 
	 * that is the symmetrical command used to obtain information about the configuration of a running Redis instance.
	 * 
	 * All the configuration parameters set using CONFIG SET are immediately loaded by Redis and will take effect starting with the next command executed.
	 * All the supported parameters have the same meaning of the equivalent configuration parameter used in the redis.conf file, 
	 * with the following important differences:
	 * 
	 * Where bytes or other quantities are specified, it is not possible to use the redis.conf abbreviated form (10k 2gb ... and so forth), 
	 * everything should be specified as a well-formed 64-bit integer, in the base unit of the configuration directive.
	 * The save parameter is a single string of space-separated integers. Every pair of integers represent a seconds/modifications threshold.
	 * For instance what in redis.conf looks like:
	 * <pre>
	 * save 900 1
	 * save 300 10
	 * </pre>
	 * 
	 * that means, save after 900 seconds if there is at least 1 change to the dataset, and after 300 seconds if there are at least 10 changes to the datasets, 
	 * should be set using CONFIG SET as "900 1 300 10".
	 * It is possible to switch persistence from RDB snapshotting to append-only file (and the other way around) using the CONFIG SET command. 
	 * For more information about how to do that please check the persistence page.
	 * In general what you should know is that setting the appendonly parameter to yes will start a background process to 
	 * save the initial append-only file (obtained from the in memory data set), and will append all the subsequent commands on the append-only file, 
	 * thus obtaining exactly the same effect of a Redis server that started with AOF turned on since the start.
	 * You can have both the AOF enabled with RDB snapshotting if you want, the two options are not mutually exclusive.
	 * 
	 * @param parameter
	 * @param value
	 * @return  OK when the configuration was set properly. Otherwise an error is returned.
	 */
	String configset(String parameter, String value);
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * Return the number of keys in the currently-selected database.
	 * @return
	 */
	long dbsize();
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * DEBUG SEGFAULT performs an invalid memory access that crashes Redis. It is used to simulate bugs during the development.
	 * @return Status code reply, e.g. OK
	 */
	String debugsegfault();
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * Delete all the keys of all the existing databases, not just the currently selected one. This command never fails.
	 * 
	 * @return Status code reply, e.g. OK
	 */
	String flushall();
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * Delete all the keys of the currently selected DB. This command never fails.
	 * @return Status code reply, e.g. OK
	 */
	String flushdb();
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * The INFO command returns information and statistics about the server in a format that is simple to parse by computers and easy to read by humans.
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
	 * When no parameter is provided, the default option is assumed.
	 * Return value
	 * Bulk reply: as a collection of text lines.
	 * Lines can contain a section name (starting with a # character) or a property. All the properties are in the form of field:value terminated by \r\n.
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
	 * Notes
	 * Please note depending on the version of Redis some of the fields have been added or removed. A robust client application should therefore parse the result of this command by skipping unknown properties, and gracefully handle missing fields.
	 * Here is the description of fields for Redis >= 2.4.
	 * 
	 * Here is the meaning of all fields in the server section:
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
	 * 
	 * Here is the meaning of all fields in the clients section:
	 * connected_clients: Number of client connections (excluding connections from slaves)
	 * client_longest_output_list: longest output list among current client connections
	 * client_biggest_input_buf: biggest input buffer among current client connections
	 * blocked_clients: Number of clients pending on a blocking call (BLPOP, BRPOP, BRPOPLPUSH)
	 * 
	 * Here is the meaning of all fields in the memory section:
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
	 * 
	 * Here is the meaning of all fields in the persistence section:
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
	 * 
	 * Here is the meaning of all fields in the stats section:
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
	 * 
	 * Here is the meaning of all fields in the replication section:
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
	 * 
	 * Here is the meaning of all fields in the cpu section:
	 * used_cpu_sys: System CPU consumed by the Redis server
	 * used_cpu_user:User CPU consumed by the Redis server
	 * used_cpu_sys_children: System CPU consumed by the background processes
	 * used_cpu_user_children: User CPU consumed by the background processes
	 * 
	 * The commandstats section provides statistics based on the command type, including the number of calls, 
	 * the total CPU time consumed by these commands, and the average CPU consumed per command execution.
	 * For each command type, the following line is added:
	 * cmdstat_XXX:calls=XXX,usec=XXX,usecpercall=XXX
	 * 
	 * The cluster section currently only contains a unique field:
	 * cluster_enabled: Indicate Redis cluster is enabled
	 * 
	 * The keyspace section provides statistics on the main dictionary of each database. The statistics are the number of keys, 
	 * and the number of keys with an expiration.
	 * For each database, the following line is added:
	 * dbXXX:keys=XXX,expires=XXX
	 * 
	 * @return as a collection of text lines.
	 */
	String info();
	String info(String section);
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * Return the UNIX TIME of the last DB save executed with success. A client may check if a BGSAVE command succeeded reading the LASTSAVE value, 
	 * then issuing a BGSAVE command and checking at regular intervals every N seconds if LASTSAVE changed.
	 * 
	 * @return an UNIX time stamp.
	 */
	long lastsave();
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * MONITOR is a debugging command that streams back every command processed by the Redis server. 
	 * It can help in understanding what is happening to the database. This command can both be used via redis-cli and via telnet.
	 * The ability to see all the requests processed by the server is useful in order to spot bugs in an application 
	 * both when using Redis as a database and as a distributed caching system.
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
	 * Manually issue the QUIT command to stop a MONITOR stream running via telnet.
	 * 
	 * Cost of running MONITOR
	 * Because MONITOR streams back all commands, its use comes at a cost. The following (totally unscientific) benchmark numbers 
	 * illustrate what the cost of running MONITOR can be.
	 * Benchmark result without MONITOR running:
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
	 * In this particular case, running a single MONITOR client can reduce the throughput by more than 50%. 
	 * Running more MONITOR clients will reduce throughput even more.
	 * 
	 * Return value
	 * Non standard return value, just dumps the received commands in an infinite flow.
	 * 
	 * @param handler
	 */
	void monitor(RedisMonitorHandler handler);
	
	/**
	 * Available since 1.0.0
	 * 
	 * <pre>
	 * The SAVE commands performs a synchronous save of the dataset producing a point in time snapshot of all the data inside the Redis instance, 
	 * in the form of an RDB file.
	 * You almost never want to call SAVE in production environments where it will block all the other clients. 
	 * Instead usually BGSAVE is used. However in case of issues preventing Redis to create the background saving child 
	 * (for instance errors in the fork(2) system call), the SAVE command can be a good last resort to perform the dump of the latest dataset.
	 * Please refer to the persistence documentation for detailed information.
	 * 
	 * @return The commands returns OK on success.
	 */
	String save();
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * The command behavior is the following:
	 * Stop all the clients.
	 * Perform a blocking SAVE if at least one save point is configured.
	 * Flush the Append Only File if AOF is enabled.
	 * Quit the server.
	 * If persistence is enabled this commands makes sure that Redis is switched off without the lost of any data. 
	 * This is not guaranteed if the client uses simply SAVE and then QUIT because other clients may alter the DB data between the two commands.
	 * Note: A Redis instance that is configured for not persisting on disk (no AOF configured, nor "save" directive) 
	 * will not dump the RDB file on SHUTDOWN, as usually you don't want Redis instances used only for caching to block on when shutting down.
	 * 
	 * SAVE and NOSAVE modifiers
	 * It is possible to specify an optional modifier to alter the behavior of the command. Specifically:
	 * SHUTDOWN SAVE will force a DB saving operation even if no save points are configured.
	 * SHUTDOWN NOSAVE will prevent a DB saving operation even if one or more save points are configured. 
	 * (You can think at this variant as an hypothetical ABORT command that just stops the server)
	 * 
	 * @return on error. On success nothing is returned since the server quits and the connection is closed.
	 */
	String shutdown(boolean save);
	
	/**
	 * Available since 1.0.0
	 * 
	 * <p>
	 * The SLAVEOF command can change the replication settings of a slave on the fly. 
	 * If a Redis server is already acting as slave, the command SLAVEOF NO ONE will turn off the replication, 
	 * turning the Redis server into a MASTER. In the proper form SLAVEOF hostname port will make the server a slave 
	 * of another server listening at the specified hostname and port.
	 * 
	 * If a server is already a slave of some master, SLAVEOF hostname port will stop the replication against the old server 
	 * and start the synchronization against the new one, discarding the old dataset.
	 * The form SLAVEOF NO ONE will stop replication, turning the server into a MASTER, but will not discard the replication. 
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
	 * Available since 2.2.12
	 * 
	 * <p>
	 * This command is used in order to read and reset the Redis slow queries log.
	 * 
	 * Redis slow log overview
	 * The Redis Slow Log is a system to log queries that exceeded a specified execution time. 
	 * The execution time does not include I/O operations like talking with the client, sending the reply and so forth, 
	 * but just the time needed to actually execute the command (this is the only stage of command execution where the 
	 * thread is blocked and can not serve other requests in the meantime).
	 * 
	 * You can configure the slow log with two parameters: slowlog-log-slower-than tells Redis what is the execution time, 
	 * in microseconds, to exceed in order for the command to get logged. Note that a negative number disables the slow log,
	 * while a value of zero forces the logging of every command. 
	 * slowlog-max-len is the length of the slow log. The minimum value is zero. When a new command is logged and the 
	 * slow log is already at its maximum length, the oldest one is removed from the queue of logged commands in order to make space.
	 * The configuration can be done by editing redis.conf or while the server is running using the CONFIG GET and CONFIG SET commands.
	 * 
	 * Reading the slow log
	 * The slow log is accumulated in memory, so no file is written with information about the slow command executions. 
	 * This makes the slow log remarkably fast at the point that you can enable the logging of all the commands 
	 * (setting the slowlog-log-slower-than config parameter to zero) with minor performance hit.
	 * To read the slow log the SLOWLOG GET command is used, that returns every entry in the slow log. 
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
	 * Every entry is composed of four fields:
	 * A unique progressive identifier for every slow log entry.
	 * The unix timestamp at which the logged command was processed.
	 * The amount of time needed for its execution, in microseconds.
	 * The array composing the arguments of the command.
	 * The entry's unique ID can be used in order to avoid processing slow log entries multiple times (for instance you may have a script sending you an email alert for every new slow log entry).
	 * The ID is never reset in the course of the Redis server execution, only a server restart will reset it.
	 * 
	 * Obtaining the current length of the slow log
	 * It is possible to get just the length of the slow log using the command SLOWLOG LEN.
	 * Resetting the slow log.
	 * You can reset the slow log using the SLOWLOG RESET command. Once deleted the information is lost forever.
	 * 
	 * @return
	 */
	List<Slowlog> slowlogget();
	List<Slowlog> slowlogget(long no);
	String slowlogreset();
	long slowloglen();
	
	/**
	 * Available since 1.0.0
	 */
	void sync();
	
	/**
	 * Available since 2.6.0
	 * Time complexity: O(1)
	 * 
	 * <p>
     * Returns the current time in milliseconds
	 * @return the difference, measured in milliseconds, between the current time and midnight, January 1, 1970 UTC.
	 */
	long time();
	
	/**
	 * Available since 2.6.0
	 * Time complexity: O(1)
	 * 
	 * <p>
     * Returns the current time in microseconds
	 * @return the difference, measured in microseconds, between the current time and midnight, January 1, 1970 UTC.
	 */
	long microtime();
}

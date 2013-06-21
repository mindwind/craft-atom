package org.craft.atom.redis.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.craft.atom.redis.api.handler.RedisPsubscribeHandler;
import org.craft.atom.redis.api.handler.RedisSubscribeHandler;

/**
 * The atomic commands supported by sharded Redis.
 * <p>
 * In <code>ShardedRedisCommand</code>, use <tt>shardKey</tt> force certain keys to go to the same shard.<br>
 * In fact we use <tt>shardKey</tt> to select shard, so we can guarantee atomicity of command.
 * 
 * @author mindwind
 * @version 1.0, May 4, 2013
 */
public interface ShardedRedisCommand extends RedisCommand {
	
	// ~ --------------------------------------------------------------------------------------------------------- Keys
	
	
	/**
	 * @see {@link #del(String)}
	 * @param shardKey
	 * @param keys
	 * @return
	 */
	Long del(String shardkey, String... keys);
	
	/**
	 * @see {@link #dump(String)}}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	String dump(String shardkey, String key);
	
	/**
	 * @see {@link #exists(String)}}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	Boolean exists(String shardkey, String key);
	
	/**
	 * @see {@link #expire(String, int)}
	 * @param shardkey
	 * @param key
	 * @param seconds
	 * @return
	 */
	Long expire(String shardkey, String key, int seconds);
	
	/**
	 * @see {@link #expireat(String, long)}}
	 * @param shardkey
	 * @param key
	 * @param timestamp
	 * @return
	 */
	Long expireat(String shardkey, String key, long timestamp);
	
	/**
	 * @see {@link #keys(String)}}
	 * @param shardkey
	 * @param pattern
	 * @return
	 */
	Set<String> keys(String shardkey, String pattern);
	
	/**
	 * @see {@link #migrate(String, int, String, int, int)}}
	 * @param shardkey
	 * @param host
	 * @param port
	 * @param key
	 * @param destinationdb
	 * @param timeout
	 * @return
	 */
	String migrate(String shardkey, String host, int port, String key, int destinationdb, int timeout);
	
	/**
	 * @see {@link #move(String, int)}
	 * @param shardkey
	 * @param key
	 * @param db
	 * @return
	 */
	Long move(String shardkey, String key, int db);
	
	/**
	 * @see {@link #objectrefcount(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	Long objectrefcount(String shardkey, String key);
	String objectencoding(String shardkey, String key);
	Long objectidletime(String shardkey, String key);
	
	/**
	 * @see {@link #persist(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	Long persist(String shardkey, String key);
	
	/**
	 * @see {@link #pexpire(String, int)}}
	 * @param shardkey
	 * @param key
	 * @param milliseconds
	 * @return
	 */
	Long pexpire(String shardkey, String key, int milliseconds);
	
	/**
	 * @see {@link #pexpireat(String, long)}}
	 * @param shardkey
	 * @param key
	 * @param millisecondstimestamp
	 * @return
	 */
	Long pexpireat(String shardkey, String key, long millisecondstimestamp);
	
	/**
	 * @see {@link #pttl(String)}}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	Long pttl(String shardkey, String key);
	
	/**
	 * @see {@link SingletonRedisCommand#randomkey()}
	 * @param shardkey
	 * @return
	 */
	String randomkey(String shardkey);
	
	/**
	 * @see {@link #rename(String, String)}
	 * @param shardkey
	 * @param key
	 * @param newkey
	 * @return
	 */
	String rename(String shardkey, String key, String newkey);
	
	/**
	 * @see {@link #renamenx(String, String)}
	 * @param shardkey
	 * @param key
	 * @param newkey
	 * @return
	 */
	Long renamenx(String shardkey, String key, String newkey);
	
	/**
	 * @see {@link #restore(String, long, String)}
	 * @param shardkey
	 * @param key
	 * @param ttl
	 * @param serializedvalue
	 * @return
	 */
	String restore(String shardkey, String key, long ttl, String serializedvalue);
	
	/**
	 * @see {@link #sort(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	List<String> sort(String shardkey, String key);
	List<String> sort(String shardkey, String key, boolean desc);
	List<String> sort(String shardkey, String key, boolean alpha, boolean desc);
	List<String> sort(String shardkey, String key, int offset, int count);
	List<String> sort(String shardkey, String key, int offset, int count, boolean alpha, boolean desc);
	List<String> sort(String shardkey, String key, String bypattern, String... getpatterns);
	List<String> sort(String shardkey, String key, String bypattern, boolean desc, String... getpatterns);
	List<String> sort(String shardkey, String key, String bypattern, boolean alpha, boolean desc, String... getpatterns);
	List<String> sort(String shardkey, String key, String bypattern, int offset, int count, String... getpatterns);
	List<String> sort(String shardkey, String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String... getpatterns);
	List<String> sort(String shardkey, String key, String bypattern, String destination);
	List<String> sort(String shardkey, String key, String bypattern, boolean desc, String destination, String... getpatterns);
	List<String> sort(String shardkey, String key, String bypattern, boolean alpha, boolean desc, String destination, String... getpatterns);
	List<String> sort(String shardkey, String key, String bypattern, int offset, int count, String destination, String... getpatterns);
	List<String> sort(String shardkey, String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String destination, String... getpatterns);
	
	/**
	 * @see {@link #ttl(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	Long ttl(String shardkey, String key);
	
	/**
	 * @see {@link #type(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	String type(String shardkey, String key);
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Strings
	
	
	/**
	 * @see {@link #append(String, String)}
	 * @param shardkey
	 * @param key
	 * @param value
	 * @return
	 */
	Long append(String shardkey, String key, String value);
	
	/**
	 * @see {@link #bitcount(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	Long bitcount(String shardkey, String key);
	
	/**
	 * @see {@link #bitnot(String, String)}
	 * @param shardkey
	 * @param destkey
	 * @param key
	 * @return
	 */
	Long bitnot(String shardkey, String destkey, String key);
	
	/**
	 * @see {@link #bitnot(String, String)}
	 * @param shardKey
	 * @param destKey
	 * @param keys
	 * @return
	 */
	Long bitand(String shardkey, String destkey, String... keys);
	Long bitor(String shardkey, String destkey, String... keys);
	Long bitxor(String shardkey, String destkey, String... keys);
	
	/**
	 * @see {@link #decr(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	Long decr(String shardkey, String key);
	
	/**
	 * @see {@link #decrby(String, long)}
	 * @param shardkey
	 * @param key
	 * @param decrement
	 * @return
	 */
	Long decrby(String shardkey, String key, long decrement);
	
	/**
	 * @see {@link #get(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	String get(String shardkey, String key);
	
	/**
	 * @see {@link #getbit(String, long)}
	 * @param shardkey
	 * @param key
	 * @param offset
	 * @return
	 */
	Boolean getbit(String shardkey, String key, long offset);
	
	/**
	 * @see {@link #getrange(String, long, long)}
	 * @param shardkey
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	String getrange(String shardkey, String key, long start, long end);
	
	/**
	 * @see {@link #getset(String, String)}
	 * @param shardkey
	 * @param key
	 * @param value
	 * @return
	 */
	String getset(String shardkey, String key, String value);
	
	/**
	 * @see {@link #incr(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	Long incr(String shardkey, String key);
	
	/**
	 * @see {@link #incrby(String, long)}
	 * @param shardkey
	 * @param key
	 * @param increment
	 * @return
	 */
	Long incrby(String shardkey, String key, long increment);
	
	/**
	 * @see {@link #incrbyfloat(String, double)}
	 * @param shardkey
	 * @param key
	 * @param increment
	 * @return
	 */
	Double incrbyfloat(String shardkey, String key, double increment);
	
	/**
	 * @see {@link SingletonRedisCommand#mget(String...)}
	 * @param shardkey
	 * @param keys
	 * @return
	 */
	List<String> mget(String shardkey, String... keys);
	
	/**
	 * @see {@link SingletonRedisCommand#mset(String...)}
	 * @param shardkey
	 * @param keysvalues
	 * @return
	 */
	String mset(String shardkey, String... keysvalues);

	/**
	 * @see {@link SingletonRedisCommand#msetnx(String...)}
	 * @param shardkey
	 * @param keysvalues
	 * @return
	 */
	String msetnx(String shardkey, String... keysvalues);
	
	/**
	 * @see {@link #psetex(String, int, String)}
	 * @param shardkey
	 * @param key
	 * @param milliseconds
	 * @param value
	 * @return
	 */
	String psetex(String shardkey, String key, int milliseconds, String value);

	/**
	 * @see {@link #set(String, String)}
	 * @param shardkey
	 * @param key
	 * @param value
	 * @return
	 */
	String set(String shardkey, String key, String value);
	
	/**
	 * @see {@link #setxx(String, String)}
	 * @param shardkey
	 * @param key
	 * @param value
	 * @return
	 */
	String setxx(String shardkey, String key, String value);
	String setnxex(String shardkey, String key, String value, int seconds);
	String setnxpx(String shardkey, String key, String value, int milliseconds);
	String setxxex(String shardkey, String key, String value, int seconds);
	String setxxpx(String shardkey, String key, String value, int milliseconds);
	
	/**
	 * @see {@link #setbit(String, long, boolean)}
	 * @param shardkey
	 * @param key
	 * @param offset
	 * @param value
	 * @return
	 */
	Boolean setbit(String shardkey, String key, long offset, boolean value);
	
	/**
	 * @see {@link #setex(String, int, String)}
	 * @param shardkey
	 * @param key
	 * @param seconds
	 * @param value
	 * @return
	 */
	String setex(String shardkey, String key, int seconds, String value);
	
	/**
	 * @see {@link #setnx(String, String)}
	 * @param shardkey
	 * @param key
	 * @param value
	 * @return
	 */
	Long setnx(String shardkey, String key, String value);
	
	/**
	 * @see {@link #setrange(String, long, String)}
	 * @param shardkey
	 * @param key
	 * @param offset
	 * @param value
	 * @return
	 */
	Long setrange(String shardkey, String key, long offset, String value);
	
	/**
	 * @see {@link #strlen(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	Long strlen(String shardkey, String key);
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Hashes
	
	
	/**
	 * @see {@link #hdel(String, String...)}
	 * @param shardkey
	 * @param key
	 * @param fields
	 * @return
	 */
	Long hdel(String shardkey, String key, String... fields);
	
	/**
	 * @see {@link #hexists(String, String)}
	 * @param shardkey
	 * @param key
	 * @param field
	 * @return
	 */
	Boolean hexists(String shardkey, String key, String field);
	
	/**
	 * @see {@link #hget(String, String)}
	 * @param shardkey
	 * @param key
	 * @param field
	 * @return
	 */
	String hget(String shardkey, String key, String field);
	
	/**
	 * @see {@link #hgetall(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	Map<String, String> hgetall(String shardkey, String key);
	
	/**
	 * @see {@link #hincrby(String, String, long)}}
	 * @param shardkey
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	Long hincrby(String shardkey, String key, String field, long value);
	
	/**
	 * @see {@link #hincrbyfloat(String, String, double)}
	 * @param shardkey
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	Double hincrbyfloat(String shardkey, String key, String field, double value);
	
	/**
	 * @see {@link #hkeys(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	Set<String> hkeys(String shardkey, String key);
	
	/**
	 * @see {@link #hlen(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	Long hlen(String shardkey, String key);
	
	/**
	 * @see {@link #hmget(String, String...)}
	 * @param shardkey
	 * @param key
	 * @param fields
	 * @return
	 */
	List<String> hmget(String shardkey, String key, String... fields);
	
	/**
	 * @see {@link #hmset(String, Map)}
	 * @param shardkey
	 * @param key
	 * @param fieldvalues
	 * @return
	 */
	String hmset(String shardkey, String key, Map<String, String> fieldvalues);
	
	/**
	 * @see {@link #hset(String, String, String)}
	 * @param shardkey
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	Long hset(String shardkey, String key, String field, String value);
	
	/**
	 * @see {@link #hsetnx(String, String, String)}
	 * @param shardkey
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	Long hsetnx(String shardkey, String key, String field, String value);
	
	/**
	 * @see {@link #hvals(String)}k
	 * @param shardkey
	 * @param key
	 * @return
	 */
	List<String> hvals(String shardkey, String key);
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Lists
	
	/**
	 * @see {@link #blpop(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	String blpop(String shardkey, String key);
	String blpop(String shardkey, String key, int timeout);
	
	/**
	 * @see {@link #blpop(String)}
	 * @param timeout
	 * @param keys
	 * @return A empty map(nil multi-bulk) when no element could be popped and the timeout expired.
	 *         A map (two-element multi-bulk) with the key (first element) being the name of the key where an element was popped 
	 *         and the value (second element) being the value of the popped element.
	 */
	Map<String, String> blpop(String shardkey, String... keys);
	Map<String, String> blpop(String shardkey, int timeout, String... keys);
	
	/**
	 * @see {@link #brpop(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	String brpop(String shardkey, String key);
	String brpop(String shardkey, String key, int timeout);
	
	/**
	 * @see {@link #brpop(String)}
	 * @param timeout
	 * @param keys
	 * @return A empty map(nil multi-bulk) when no element could be popped and the timeout expired.
	 *         A map (two-element multi-bulk) with the key (first element) being the name of the key where an element was popped 
	 *         and the value (second element) being the value of the popped element.
	 */
	Map<String, String> brpop(String shardkey, String... keys);
	Map<String, String> brpop(String shardkey, int timeout, String... keys);
	
	/**
	 * @see {@link #brpoplpush(String, String, int)}
	 * @param shardkey
	 * @param source
	 * @param destination
	 * @param timeout
	 * @return
	 */
	String brpoplpush(String shardkey, String source, String destination, int timeout);
	
	/**
	 * @see {@link #lindex(String, long)}
	 * @param shardkey
	 * @param key
	 * @param index
	 * @return
	 */
	String lindex(String shardkey, String key, long index);
	
	/**
	 * @see {@link #linsertbefore(String, String, String)}
	 * @param shardkey
	 * @param key
	 * @param pivot
	 * @param value
	 * @return
	 */
	Long linsertbefore(String shardkey, String key, String pivot, String value);
	Long linsertafter(String shardkey, String key, String pivot, String value);
	
	/**
	 * @see {@link #llen(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	Long llen(String shardkey, String key);
	
	/**
	 * @see {@link #lpop(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	String lpop(String shardkey, String key);
	
	/**
	 * @see {@link #lpush(String, String...)}
	 * @param shardkey
	 * @param key
	 * @param values
	 * @return
	 */
	Long lpush(String shardkey, String key, String... values);
	
	/**
	 * @see {@link #lpushx(String, String)}
	 * @param shardkey
	 * @param key
	 * @param value
	 * @return
	 */
	Long lpushx(String shardkey, String key, String value);
	
	/**
	 * @see {@link #lrange(String, long, long)}
	 * @param shardkey
	 * @param key
	 * @param start
	 * @param stop
	 * @return
	 */
	List<String> lrange(String shardkey, String key, long start, long stop);
	
	/**
	 * @see {@link #lrem(String, long, String)}
	 * @param shardkey
	 * @param key
	 * @param count
	 * @param value
	 * @return
	 */
	Long lrem(String shardkey, String key, long count, String value);
	
	/**
	 * @see {@link #lset(String, long, String)}
	 * @param shardkey
	 * @param key
	 * @param index
	 * @param value
	 * @return
	 */
	String lset(String shardkey, String key, long index, String value);
	
	/**
	 * @see {@link #ltrim(String, long, long)}
	 * @param shardkey
	 * @param key
	 * @param start
	 * @param stop
	 * @return
	 */
	String ltrim(String shardkey, String key, long start, long stop);
	
	/**
	 * @see {@link #rpop(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	String rpop(String shardkey, String key);
	
	/**
	 * @see {@link #rpoplpush(String, String)}
	 * @param shardkey
	 * @param source
	 * @param destination
	 * @return
	 */
	String rpoplpush(String shardkey, String source, String destination); 
	
	/**
	 * @see {@link #rpush(String, String...)}
	 * @param shardkey
	 * @param key
	 * @param values
	 * @return
	 */
	Long rpush(String shardkey, String key, String... values);
	
	/**
	 * @see {@link #rpushx(String, String)}
	 * @param shardkey
	 * @param key
	 * @param value
	 * @return
	 */
	Long rpushx(String shardkey, String key, String value);
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Sets
	
	/**
	 * @see {@link #sadd(String, String...)}
	 * @param shardkey
	 * @param key
	 * @param members
	 * @return
	 */
	Long sadd(String shardkey, String key, String... members);
	
	/**
	 * @see {@link #scard(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	Long scard(String shardkey, String key);
	
	/**
	 * @see {@link SingletonRedisCommand#sdiff(String...)}
	 * @param shardkey
	 * @param keys
	 * @return
	 */
	Set<String> sdiff(String shardkey, String... keys);
	
	/**
	 * @see {@link SingletonRedisCommand#sdiffstore(String, String...)}
	 * @param destination
	 * @param keys
	 * @return
	 */
	Long sdiffstore(String shardkey, String destination, String... keys);
	
	/**
	 * @see {@link SingletonRedisCommand#sinter(String...)}
	 * @param keys
	 * @return
	 */
	Set<String> sinter(String shardkey, String... keys);
	
	/**
	 * @see {@link SingletonRedisCommand#sinterstore(String...)}
	 * @param shardkey
	 * @param destination
	 * @param keys
	 * @return
	 */
	Long sinterstore(String shardkey, String destination, String... keys);
	
	/**
	 * @see {@link #sismember(String, String)}
	 * @param shardkey
	 * @param key
	 * @param member
	 * @return
	 */
	Boolean sismember(String shardkey, String key, String member);
	
	/**
	 * @see {@link #smembers(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	Set<String> smembers(String shardkey, String key);
	
	/**
	 * @see {@link SingletonRedisCommand#smove(String, String, String)}
	 * @param shardkey
	 * @param source
	 * @param destination
	 * @param member
	 * @return
	 */
	Long smove(String shardkey, String source, String destination, String member);
	
	/**
	 * @see {@link #spop(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	String spop(String shardkey, String key);
	
	/**
	 * @see {@link #srandmember(String)}
	 * @param shardkey
	 * @param key
	 * @param count
	 * @return
	 */
	Set<String> srandmember(String shardkey, String key, int count);
	String srandmember(String shardkey, String key);
	
	/**
	 * @see {@link #srem(String, String...)}
	 * @param shardkey
	 * @param key
	 * @param members
	 * @return
	 */
	Long srem(String shardkey, String key, String... members);
	
	/**
	 * @see {@link SingletonRedisCommand#sunion(String...)}
	 * @param shardkey
	 * @param keys
	 * @return
	 */
	Set<String> sunion(String shardkey, String... keys);
	
	/**
	 * @see {@link SingletonRedisCommand#sunionstore(String, String...)}
	 * @param shardkey
	 * @param destination
	 * @param keys
	 * @return
	 */
	Set<String> sunionstore(String shardkey, String destination, String... keys);
	
	
	// ~ -------------------------------------------------------------------------------------------------- Sorted Sets
	
	/**
	 * @see {@link #zadd(String, double, String)}
	 * @param shardkey
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	Long zadd(String shardkey, String key, double score, String member);
	
	/**
	 * @see {@link #zcard(String)}
	 * @param shardkey
	 * @param key
	 * @return
	 */
	Long zcard(String shardkey, String key);
	
	/**
	 * @see {@link #zcount(String, double, double)}
	 * @param shardkey
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	Long zcount(String shardkey, String key, double min, double max);
	
	/**
	 * @see {@link #zincrby(String, double, String)}
	 * @param shardkey
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	Double zincrby(String shardkey, String key, double score, String member);
	
	/**
	 * @see {@link SingletonRedisCommand#zinterstore(String, String...)}
	 * @param destination
	 * @param keys
	 * @return
	 */
	Long zinterstore(String shardkey, String destination, String... keys);
	Long zinterstoremax(String shardkey, String destination, String... keys);
	Long zinterstoremin(String shardkey, String destination, String... keys);
	Long zinterstore(String shardkey, String destination, Map<String, Integer> weightkeys);
	Long zinterstoremax(String shardkey, String destination, Map<String, Integer> weightkeys);
	Long zinterstoremin(String shardkey, String destination, Map<String, Integer> weightkeys);
	
	/**
	 * @see {@link #zrange(String, long, long)}
	 * @param shardkey
	 * @param key
	 * @param start
	 * @param stop
	 * @return
	 */
	Set<String> zrange(String shardkey, String key, long start, long stop);
	Map<String, Double> zrangewithscores(String shardkey, String key, long start, long stop);
	
	/**
	 * @see {@link #zrangebyscore(String, double, double)}
	 * @param shardkey
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	Set<String> zrangebyscore(String shardkey, String key, double min, double max);
	Set<String> zrangebyscore(String shardkey, String key, String min, String max);
	Set<String> zrangebyscore(String shardkey, String key, double min, double max, int offset, int count);
	Set<String> zrangebyscore(String shardkey, String key, String min, String max, int offset, int count);
	Map<String, Double> zrangebyscorewithscores(String shardkey, String key, double min, double max);
	Map<String, Double> zrangebyscorewithscores(String shardkey, String key, String min, String max);
	Map<String, Double> zrangebyscorewithscores(String shardkey, String key, double min, double max, int offset, int count);
	Map<String, Double> zrangebyscorewithscores(String shardkey, String key, String min, String max, int offset, int count);
	
	/**
	 * @see {@link #zrank(String, String)}
	 * @param shardkey
	 * @param key
	 * @param member
	 * @return
	 */
	Long zrank(String shardkey, String key, String member);
	
	/**
	 * @see {@link #zrem(String, String...)}
	 * @param shardkey
	 * @param key
	 * @param members
	 * @return
	 */
	Long zrem(String shardkey, String key, String... members);
	
	/**
	 * @see {@link #zremrangebyrank(String, long, long)}
	 * @param shardkey
	 * @param key
	 * @param start
	 * @param stop
	 * @return
	 */
	Long zremrangebyrank(String shardkey, String key, long start, long stop);
	
	/**
	 * @see {@link #zremrangebyscore(String, double, double)}
	 * @param shardkey
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	Long zremrangebyscore(String shardkey, String key, double min, double max);
	Long zremrangebyscore(String shardkey, String key, String min, String max);
	
	/**
	 * @see {@link #zrevrange(String, long, long)}
	 * @param shardkey
	 * @param key
	 * @param start
	 * @param stop
	 * @return
	 */
	Set<String> zrevrange(String shardkey, String key, long start, long stop);
	Map<String, Double> zrerangewithscores(String shardkey, String key, long start, long stop);
	
	/**
	 * @see {@link #zrerangebyscore(String, double, double)}
	 * @param shardkey
	 * @param key
	 * @param max
	 * @param min
	 * @return
	 */
	Set<String> zrerangebyscore(String shardkey, String key, double max, double min);
	Set<String> zrerangebyscore(String shardkey, String key, String max, String min);
	Set<String> zrerangebyscore(String shardkey, String key, double max, double min, int offset, int count);
	Set<String> zrerangebyscore(String shardkey, String key, String max, String min, int offset, int count);
	
	/**
	 * @see {@link #zrerank(String, String)}
	 * @param shardkey
	 * @param key
	 * @param member
	 * @return
	 */
	Long zrerank(String shardkey, String key, String member);
	
	/**
	 * @see {@link #zscore(String, String)}
	 * @param shardkey
	 * @param key
	 * @param member
	 * @return
	 */
	Double zscore(String shardkey, String key, String member);
	
	/**
	 * @see {@link SingletonRedisCommand#zunionstore(String, String...)}
	 * @param shardkey
	 * @param destination
	 * @param keys
	 * @return
	 */
	Long zunionstore(String shardkey, String destination, String... keys);
	Long zunionstoremax(String shardkey, String destination, String... keys);
	Long zunionstoremin(String shardkey, String destination, String... keys);
	Long zunionstore(String shardkey, String destination, Map<String, Integer> weightkeys);
	Long zunionstoremax(String shardkey, String destination, Map<String, Integer> weightkeys);
	Long zunionstoremin(String shardkey, String destination, Map<String, Integer> weightkeys);
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Pub/Sub
	
	/**
	 * @see {@link SingletonRedisCommand#psubscribe(RedisPsubscribeHandler, String)}
	 * @param shardkey
	 * @param handler
	 * @param patterns
	 */
	void psubscribe(String shardkey, RedisPsubscribeHandler handler, String... patterns);
	
	/**
	 * @see {@link #publish(String, String)}
	 * @param shardkey
	 * @param channel
	 * @param message
	 * @return
	 */
	Long publish(String shardkey, String channel, String message);
	
	/**
	 * @see {@link SingletonRedisCommand#punsubscribe(String)}
	 * @param shardkey
	 * @param pattern
	 * @return
	 */
	String punsubscribe(String shardkey, String pattern);
	
	/**
	 * @see {@link #punsubscribe(String)}
	 * @param shardkey
	 * @param handler
	 * @param patterns
	 * @return unsubscribed patterns
	 */
	List<String> punsubscribe(String shardkey, String... patterns);
	
	/**
	 * @see {@link #subscribe(RedisSubscribeHandler, String)}
	 * @param shardkey
	 * @param handler
	 * @param channels
	 */
	void subscribe(String shardkey, RedisSubscribeHandler handler, String... channels);
	
	/**
	 * @see {@link #unsubscribe(String)}}
	 * @param channel
	 * @return unsubscribed channels
	 */
	List<String> unsubscribe(String shardkey, String... channels);
	
	
	// ~ ------------------------------------------------------------------------------------------------- Transactions
	
	
	/**
	 * @see {@link SingletonRedisCommand#discard()}
	 * @param shardkey
	 * @return always OK.
	 */
	String discard(String shardkey);
	
	/**
	 * @see {@link SingletonRedisCommand#exec()}}
	 * @param shardkey
	 * @return
	 */
	List<Object> exec(String shardkey);
	
	/**
	 * @see {@link SingletonRedisCommand#multi()}
	 * @param shardkey
	 * @return always OK.
	 */
	String multi(String shardkey);
	
	/**
	 * @see {@link SingletonRedisCommand#unwatch()}
	 * @param shardkey
	 * @return always OK.
	 */
	String unwatch(String shardkey);
	
	/**
	 * @see {@link SingletonRedisCommand#watch(String...)}
	 * @param shardkey
	 * @param keys
	 * @return always OK.
	 */
	String watch(String shardkey, String... keys);
	
	
	// ~ --------------------------------------------------------------------------------------------------- Scripting
	
	/**
	 * @see {@link #eval(String)}
	 * @param shardkey
	 * @param script
	 * @return
	 */
	Object eval(String shardkey, String script);
	Object eval(String shardkey, String script, List<String> keys);
	Object eval(String shardkey, String script, List<String> keys, List<String> args);
	
	/**
	 * @see {@link #evalsha(String)}}
	 * @param shardkey
	 * @param sha1
	 * @return
	 */
	Object evalsha(String shardkey, String sha1);
	Object evalsha(String shardkey, String sha1, List<String> keys);
	Object evalsha(String shardkey, String sha1, List<String> keys, List<String> args);
	
	/**
	 * @see {@link #scriptexists(String)}
	 * @param shardkey
	 * @param sha1
	 * @return
	 */
	Boolean scriptexists(String shardkey, String sha1);
	Boolean[] scriptexists(String shardkey, String... sha1);
	
	/**
	 * @see {@link SingletonRedisCommand#scriptflush()}}
	 * @param shardkey
	 * @return
	 */
	String scriptflush(String shardkey);
	
	/**
	 * @see {@link SingletonRedisCommand#scriptkill()}
	 * @param shardkey
	 * @return
	 */
	String scriptkill(String shardkey);
	
	/**
	 * @see {@link #scriptload(String)}
	 * @param shardkey
	 * @param script
	 * @return
	 */
	String scriptload(String shardkey, String script);
	
}

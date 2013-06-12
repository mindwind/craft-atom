package org.craft.atom.redis.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.craft.atom.redis.api.handler.BinaryRedisPsubscribeHandler;
import org.craft.atom.redis.api.handler.BinaryRedisSubscribeHandler;
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
	long del(String shardkey, String... keys);
	long del(byte[] shardkey, byte[]... keys);
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Strings
	
	
	/**
	 * @see {@link #bitnot(String, String)}
	 * @param shardKey
	 * @param destKey
	 * @param keys
	 * @return
	 */
	long bitand(String shardkey, String destkey, String... keys);
	long bitand(byte[] shardkey, byte[] destkey, byte[]... keys);
	long bitor(String shardkey, String destkey, String... keys);
	long bitor(byte[] shardkey, byte[] destkey, byte[]... keys);
	long bitxor(String shardkey, String destkey, String... keys);
	long bitxor(byte[] shardkey, byte[] destkey, byte[]... keys);
	
	/**
	 * @see {@link SingletonRedisCommand#mget(String...)}
	 * @param shardkey
	 * @param keys
	 * @return
	 */
	List<String> mget(String shardkey, String... keys);
	List<byte[]> mget(byte[] shardkey, byte[]... keys);
	
	/**
	 * @see {@link SingletonRedisCommand#mset(String...)}
	 * @param shardkey
	 * @param keysvalues
	 * @return
	 */
	String mset(String shardkey, String... keysvalues);
	byte[] mset(byte[] shardkey, byte[]... keysvalues);
	
	/**
	 * @see {@link SingletonRedisCommand#msetnx(String...)}
	 * @param shardkey
	 * @param keysvalues
	 * @return
	 */
	String msetnx(String shardkey, String... keysvalues);
	byte[] msetnx(byte[] shardkey, byte[]... keysvalues);
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Lists
	
	/**
	 * @see {@link RedisCommand#blpop(String)}
	 * @param timeout
	 * @param keys
	 * @return A empty map(nil multi-bulk) when no element could be popped and the timeout expired.
	 *         A map (two-element multi-bulk) with the key (first element) being the name of the key where an element was popped 
	 *         and the value (second element) being the value of the popped element.
	 */
	Map<String, String> blpop(String shardkey, String... keys);
	Map<byte[], byte[]> blpop(byte[] shardkey, byte[]... keys);
	Map<String, String> blpop(String shardkey, int timeout, String... keys);
	Map<byte[], byte[]> blpop(byte[] shardkey, int timeout, byte[]... keys);
	
	/**
	 * @see {@link RedisCommand#brpop(String)}
	 * @param timeout
	 * @param keys
	 * @return A empty map(nil multi-bulk) when no element could be popped and the timeout expired.
	 *         A map (two-element multi-bulk) with the key (first element) being the name of the key where an element was popped 
	 *         and the value (second element) being the value of the popped element.
	 */
	Map<String, String> brpop(String shardkey, String... keys);
	Map<byte[], byte[]> brpop(byte[] shardkey, byte[]... keys);
	Map<String, String> brpop(String shardkey, int timeout, String... keys);
	Map<byte[], byte[]> brpop(byte[] shardkey, int timeout, byte[]... keys);
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Sets
	
	
	/**
	 * @see {@link SingletonRedisCommand#sdiff(String...)}
	 * @param shardkey
	 * @param keys
	 * @return
	 */
	Set<String> sdiff(String shardkey, String... keys);
	Set<byte[]> sdiff(byte[] shardkey, byte[]... keys);
	
	/**
	 * @see {@link SingletonRedisCommand#sdiffstore(String, String...)}
	 * @param destination
	 * @param keys
	 * @return
	 */
	long sdiffstore(String shardkey, String destination, String... keys);
	long sdiffstore(byte[] shardkey, byte[] destination, byte[]... keys);
	
	/**
	 * @see {@link SingletonRedisCommand#sinter(String...)}
	 * @param keys
	 * @return
	 */
	Set<String> sinter(String shardkey, String... keys);
	Set<byte[]> sinter(byte[] shardkey, byte[]... keys);
	
	/**
	 * @see {@link SingletonRedisCommand#sinterstore(String...)}
	 * @param shardkey
	 * @param destination
	 * @param keys
	 * @return
	 */
	long sinterstore(String shardkey, String destination, String... keys);
	long sinterstore(byte[] shardkey, byte[] destination, byte[]... keys);
	
	/**
	 * @see {@link SingletonRedisCommand#smove(String, String, String)}
	 * @param shardkey
	 * @param source
	 * @param destination
	 * @param member
	 * @return
	 */
	long smove(String shardkey, String source, String destination, String member);
	long smove(byte[] shardkey, byte[] source, byte[] destination, byte[] member);
	
	/**
	 * @see {@link SingletonRedisCommand#sunion(String...)}
	 * @param shardkey
	 * @param keys
	 * @return
	 */
	Set<String> sunion(String shardkey, String... keys);
	Set<byte[]> sunion(byte[] shardkey, byte[]... keys);
	
	/**
	 * @see {@link SingletonRedisCommand#sunionstore(String, String...)}
	 * @param shardkey
	 * @param destination
	 * @param keys
	 * @return
	 */
	Set<String> sunionstore(String shardkey, String destination, String... keys);
	Set<byte[]> sunionstore(byte[] stardkey, byte[] destination, byte[]... keys);
	
	
	// ~ -------------------------------------------------------------------------------------------------- Sorted Sets
	
	/**
	 * @see {@link SingletonRedisCommand#zinterstore(String, String...)}
	 * @param destination
	 * @param keys
	 * @return
	 */
	long zinterstore(String shardkey, String destination, String... keys);
	long zinterstore(byte[] shardkey, byte[] destination, byte[]... keys);
	long zinterstoremax(String shardkey, String destination, String... keys);
	long zinterstoremax(byte[] shardkey, byte[] destination, byte[]... keys);
	long zinterstoremin(String shardkey, String destination, String... keys);
	long zinterstoremin(byte[] shardkey, byte[] destination, byte[]... keys);
	long zinterstore(String shardkey, String destination, Map<String, Integer> weightkeys);
	long zinterstore(byte[] shardkey, byte[] destination, Map<String, Integer> weightkeys);
	long zinterstoremax(String shardkey, String destination, Map<String, Integer> weightkeys);
	long zinterstoremax(byte[] shardkey, byte[] destination, Map<String, Integer> weightkeys);
	long zinterstoremin(String shardkey, String destination, Map<String, Integer> weightkeys);
	long zinterstoremin(byte[] shardkey, byte[] destination, Map<String, Integer> weightkeys);
	
	/**
	 * @see {@link SingletonRedisCommand#zunionstore(String, String...)}
	 * @param shardkey
	 * @param destination
	 * @param keys
	 * @return
	 */
	long zunionstore(String shardkey, String destination, String... keys);
	long zunionstore(byte[] shardkey, byte[] destination, byte[]... keys);
	long zunionstoremax(String shardkey, String destination, String... keys);
	long zunionstoremax(byte[] shardkey, byte[] destination, byte[]... keys);
	long zunionstoremin(String shardkey, String destination, String... keys);
	long zunionstoremin(byte[] shardkey, byte[] destination, byte[]... keys);
	long zunionstore(String shardkey, String destination, Map<String, Integer> weightkeys);
	long zunionstore(byte[] shardkey, byte[] destination, Map<String, Integer> weightkeys);
	long zunionstoremax(String shardkey, String destination, Map<String, Integer> weightkeys);
	long zunionstoremax(byte[] shardkey, byte[] destination, Map<String, Integer> weightkeys);
	long zunionstoremin(String shardkey, String destination, Map<String, Integer> weightkeys);
	long zunionstoremin(byte[] shardkey, byte[] destination, Map<String, Integer> weightkeys);
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Pub/Sub
	
	/**
	 * @see {@link #punsubscribe(RedisPsubscribeHandler, String)}
	 * @param shardkey
	 * @param handler
	 * @param patterns
	 */
	void psubscribe(String shardkey, RedisPsubscribeHandler handler, String... patterns);
	void psubscribe(byte[] shardkey, BinaryRedisPsubscribeHandler handler, byte[]... patterns);
	
	/**
	 * @see {@link #punsubscribe(String)}
	 * @param shardkey
	 * @param handler
	 * @param patterns
	 * @return unsubscribed patterns
	 */
	List<String> punsubscribe(String shardkey, String... patterns);
	List<byte[]> punsubscribe(byte[] shardkey, byte[]... patterns);
	
	/**
	 * @see {@link #subscribe(RedisSubscribeHandler, String)}
	 * @param shardkey
	 * @param handler
	 * @param channels
	 */
	void subscribe(String shardkey, RedisSubscribeHandler handler, String... channels);
	void subscribe(byte[] shardkey, BinaryRedisSubscribeHandler handler, String... channels);
	
	/**
	 * @see {@link #unsubscribe(String)}}
	 * @param channel
	 * @return unsubscribed channels
	 */
	List<String> unsubscribe(String shardkey, String... channels);
	List<byte[]> unsubscribe(byte[] shardkey, byte[]... channels);
	
	
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
	Object eval(byte[] shardkey, byte[] script);
	Object eval(byte[] shardkey, byte[] script, List<byte[]> keys);
	Object eval(byte[] shardkey, byte[] script, List<byte[]> keys, List<byte[]> args);
	
	/**
	 * @see {@link #evalsha(String)}}
	 * @param shardkey
	 * @param sha1
	 * @return
	 */
	Object evalsha(String shardkey, String sha1);
	Object evalsha(String shardkey, String sha1, List<String> keys);
	Object evalsha(String shardkey, String sha1, List<String> keys, List<String> args);
	Object evalsha(byte[] sha1);
	Object evalsha(byte[] sha1, List<byte[]> keys);
	Object evalsha(byte[] sha1, List<byte[]> keys, List<byte[]> args);
	
	/**
	 * @see {@link #scriptexists(String)}
	 * @param sha1
	 * @return
	 */
	boolean[] scriptexists(String shardkey, String... sha1);
	boolean[] scriptexists(byte[] shardkey, byte[]... sha1);
	
	/**
	 * @see {@link SingletonRedisCommand#scriptflush()}}
	 * @param shardkey
	 * @return
	 */
	String scriptflush(String shardkey);
	byte[] scriptflush(byte[] shardkey);
	
	/**
	 * @see {@link SingletonRedisCommand#scriptkill()}
	 * @param shardkey
	 * @return
	 */
	String scriptkill(String shardkey);
	byte[] scriptkill(byte[] shardkey);
	
	/**
	 * @see {@link #scriptload(String)}
	 * @param shardkey
	 * @param script
	 * @return
	 */
	String scriptload(String shardkey, String script);
	byte[] scriptload(byte[] shardkey, byte[] script);
	
}

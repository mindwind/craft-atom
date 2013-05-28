package org.craft.atom.redis.api;

import java.util.List;

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
	long bitand(String shardkey, String destKey, String... keys);
	long bitand(byte[] shardkey, byte[] destKey, byte[]... keys);
	long bitor(String shardkey, String destKey, String... keys);
	long bitor(byte[] shardkey, byte[] destKey, byte[]... keys);
	long bitxor(String shardkey, String destKey, String... keys);
	long bitxor(byte[] shardkey, byte[] destKey, byte[]... keys);
	
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
	byte[] mset(String shardkey, byte[]... keysvalues);
	
	/**
	 * @see {@link SingletonRedisCommand#msetnx(String...)}
	 * @param shardkey
	 * @param keysvalues
	 * @return
	 */
	String msetnx(String shardkey, String... keysvalues);
	byte[] msetnx(String shardkey, byte[]... keysvalues);
}

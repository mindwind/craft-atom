package org.craft.atom.redis.api;

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
	
	
	long del(String shardKey, String... keys);
	long del(byte[] shardKey, byte[]... keys);
	
	long bitand(String shardKey, String destKey, String... keys);
	long bitand(byte[] shardKey, byte[] destKey, byte[]... keys);
	long bitor(String shardKey, String destKey, String... keys);
	long bitor(byte[] shardKey, byte[] destKey, byte[]... keys);
	long bitxor(String shardKey, String destKey, String... keys);
	long bitxor(byte[] shardKey, byte[] destKey, byte[]... keys);

}

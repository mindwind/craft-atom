package org.craft.atom.lock.api;

import org.craft.atom.lock.Redis24DLock;
import org.craft.atom.lock.Redis26DLock;
import org.craft.atom.lock.ShardedRedis24DLock;
import org.craft.atom.lock.ShardedRedis26DLock;
import org.craft.atom.redis.api.RedisCommand;
import org.craft.atom.redis.api.ShardedRedisCommand;

/**
 * Distributed lock factory
 * 
 * @author mindwind
 * @version 1.0, Jul 16, 2013
 */
public class DLockFactory {
	
	/**
	 * @param redis
	 * @return new distributed lock base on singleton redis version 2.4.x
	 */
	public static DLock newRedis24DLock(RedisCommand redis) {
		return new Redis24DLock(redis);
	}
	
	/**
	 * @param redis
	 * @return new distributed lock base on sharded redis version 2.4.x
	 */
	public static DLock newRedis24DLock(ShardedRedisCommand redis) {
		return new ShardedRedis24DLock(redis);
	}
	
	/**
	 * @param redis
	 * @return new distributed lock base on singleton redis version 2.6.12+
	 */
	public static DLock newRedis26DLock(RedisCommand redis) {
		return new Redis26DLock(redis);
	}
	
	/**
	 * @param redis
	 * @return new distributed lock base on sharded redis version 2.6.x
	 */
	public static DLock newRedis26DLock(ShardedRedisCommand redis) {
		return new ShardedRedis26DLock(redis);
	}
	
	/**
	 * @deprecated replace by {@link #newRedis24DLock(ShardedRedisCommand)}
	 * @param redis
	 * @return new distributed lock base on sharded redis version 2.4.x
	 */
	public static DLock newShardedRedis24DLock(ShardedRedisCommand redis) {
		return new ShardedRedis24DLock(redis);
	}
	
	/**
	 * @deprecated replace by {@link #newRedis26DLock(ShardedRedisCommand)}
	 * @param redis
	 * @return new distributed lock base on sharded redis version 2.6.12+
	 */
	public static DLock newShardedRedis26DLock(ShardedRedisCommand redis) {
		return new ShardedRedis26DLock(redis);
	}
	
}

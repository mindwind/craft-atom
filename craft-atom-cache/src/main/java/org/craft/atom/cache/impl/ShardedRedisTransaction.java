package org.craft.atom.cache.impl;

import java.util.List;

import redis.clients.jedis.ShardedJedis;

/**
 * Redis transaction for sharded environment
 * 
 * @author Hu Feng
 * @version 1.0, Oct 24, 2012
 */
public class ShardedRedisTransaction extends AbstractTransaction {
	
	private ShardedJedis sj;

	public ShardedRedisTransaction(RedisCache redisCache, redis.clients.jedis.Transaction jedisTransaction, ShardedJedis sj) {
		this.redisCache = redisCache;
		this.delegate = jedisTransaction;
		this.sj = sj;
	}
	
	@Override
	public List<Object> commit() {
		try {
			return super.commit();
		} catch (Exception e) {
			close(true);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void close() {
		 close(false);
	}
	
	private void close(boolean broken) {
		if (closed) { 
			return; 
		}
		redisCache.cleanTransaction(sj, broken);
		closed = true;
	}

}

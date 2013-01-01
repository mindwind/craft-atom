package org.craft.atom.cache.impl;

import java.util.List;

import redis.clients.jedis.Jedis;

/**
 * Redis transaction for no sharded environment
 * 
 * @author Hu Feng
 * @version 1.0, Oct 24, 2012
 */
public class RedisTransaction extends AbstractTransaction {
	
	private Jedis j;
	
	public RedisTransaction(RedisCache redisCache, redis.clients.jedis.Transaction jedisTransaction, Jedis j) {
		this.redisCache = redisCache;
		this.delegate = jedisTransaction;
		this.j = j;
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
		redisCache.cleanTransaction(j, broken);
		closed = true;
	}

}

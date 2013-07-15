package org.craft.atom.lock.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.cache.Transaction;
import org.craft.atom.cache.impl.RedisCache;
import org.craft.atom.lock.DLock;

/**
 * A implementation of the {@code DLock} basis on redis version 2.4.x
 * 
 * @author mindwind
 * @version 1.0, Nov 19, 2012
 */
public class RedisDLock24 implements DLock {
	
	private static final Log LOG = LogFactory.getLog(RedisDLock24.class);
	private RedisCache redisCache;
	
	public RedisDLock24() {
		super();
	}
	
	public RedisDLock24(RedisCache redisCache) {
		this.redisCache = redisCache;
	}
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	@Override
	public boolean tryLock(String lockKey, int ttl, TimeUnit unit) {
		if (lockKey == null || unit == null) {
			throw new IllegalArgumentException("invalid tryLock() arguments!");
		}
		
		int ttlSeconds = (int) TimeUnit.SECONDS.convert(ttl, unit);
		if (ttlSeconds == 0) {
			throw new IllegalArgumentException("ttl=0, this implementation time precision is second!");
		}
		
		boolean success = true;
		try {
			redisCache.watch(lockKey);
			if (redisCache.get(lockKey) == null) {
				success = tryLock0(lockKey, ttlSeconds);
			} else {
				success = false;
			}
		} catch (Exception e) {
			LOG.error("tryLock failed!", e);
			success = false;
		} finally {
			redisCache.unwatch(lockKey);
		}
		return success;
	}
	
	private boolean tryLock0(String lockKey, int ttlSeconds) {
		Transaction tx = null;
		try {
			tx = redisCache.beginTransaction(lockKey);
			redisCache.setex(lockKey, ttlSeconds, "1");
			List<Object> result = tx.commit();
			return result != null && result.size() > 0;
		} finally {
			if (tx != null) {
				tx.close();
			}
		}
	}

	@Override
	public boolean unlock(String lockKey) {
		boolean success = true;
		
		try {
			redisCache.del(lockKey);
		} catch (Exception e) {
			LOG.error("unlock failed!", e);
			success = false;
		}
		
		return success;
	}
	
	// ~ ----------------------------------------------------------------------------------------------------------

	public RedisCache getRedisCache() {
		return redisCache;
	}

	public void setRedisCache(RedisCache redisCache) {
		this.redisCache = redisCache;
	}

}

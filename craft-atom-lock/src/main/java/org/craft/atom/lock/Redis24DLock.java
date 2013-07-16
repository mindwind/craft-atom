package org.craft.atom.lock;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.lock.api.DLock;
import org.craft.atom.redis.api.RedisCommand;
import org.craft.atom.redis.api.RedisTransaction;

/**
 * A implementation of the {@code DLock} base on redis version 2.4.x, which using singleton redis instance.
 * 
 * @author mindwind
 * @version 1.0, Jul 16, 2013
 */
public class Redis24DLock implements DLock {
	
	private static final Log LOG = LogFactory.getLog(Redis24DLock.class);
	private RedisCommand redis;
	
	public Redis24DLock() {
		super();
	}
	
	public Redis24DLock(RedisCommand redis) {
		this.redis = redis;
	}
	
	
	// ~ ----------------------------------------------------------------------------------------------------------
	

	@Override
	public boolean tryLock(String lockKey, int ttl, TimeUnit unit) {
		if (lockKey == null || unit == null) {
			throw new IllegalArgumentException(String.format("Args=<lockKey=%s, unit=%s>", lockKey, unit));
		}
		
		int ttlSeconds = (int) TimeUnit.SECONDS.convert(ttl, unit);
		if (ttlSeconds == 0) {
			ttlSeconds = 1;
		}
		
		boolean success = true;
		try {
			redis.watch(lockKey);
			if (redis.get(lockKey) == null) {
				success = tryLock0(lockKey, ttlSeconds);
			} else {
				success = false;
			}
		} catch (Exception e) {
			LOG.error(String.format("Try lock fail, args=<lockKey=%s, ttl=%s, unit=%s>", lockKey, ttl, unit), e);
			success = false;
		} finally {
			redis.unwatch();
		}
		return success;
	}
	
	private boolean tryLock0(String lockKey, int ttlSeconds) {
		RedisTransaction t = redis.multi();
		t.setex(lockKey, ttlSeconds, "1");
		List<Object> result = redis.exec(t);
		return result != null && result.size() > 0;
	}

	@Override
	public boolean unlock(String lockKey) {
		boolean success = true;
		
		try {
			redis.del(lockKey);
		} catch (Exception e) {
			LOG.error(String.format("Unlock fail, args=<lockKey=%s>", lockKey), e);
			success = false;
		}
		
		return success;
	}
	
	
	// ~ ----------------------------------------------------------------------------------------------------------
	

	public RedisCommand getRedis() {
		return redis;
	}

	public void setRedis(RedisCommand redis) {
		this.redis = redis;
	}

}

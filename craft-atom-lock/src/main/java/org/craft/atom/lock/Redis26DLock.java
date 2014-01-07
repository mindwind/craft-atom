package org.craft.atom.lock;

import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.craft.atom.lock.api.DLock;
import org.craft.atom.redis.api.RedisCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A implementation of the {@code DLock} base on redis version 2.6.x, which using singleton redis instance.
 * 
 * @author mindwind
 * @version 1.0, Jul 16, 2013
 */
@ToString(of = "redis")
public class Redis26DLock implements DLock {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(Redis26DLock.class);
	
	
	@Getter @Setter private RedisCommand redis;
	
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	
	public Redis26DLock() {
		super();
	}
	
	public Redis26DLock(RedisCommand redis) {
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
			String s = redis.setnxex(lockKey, "1", ttlSeconds);
			if (!"OK".equals(s)) {
				success = false;
			} 
		} catch (Exception e) {
			LOG.error("[CRAFT-ATOM-LOCK] Try lock fail, lockKey={}, ttl={}, unit={}", lockKey, ttl, unit, e);
			success = false;
		}
		return success;
	}

	@Override
	public boolean unlock(String lockKey) {
		boolean success = true;
		
		try {
			redis.del(lockKey);
		} catch (Exception e) {
			LOG.error("[CRAFT-ATOM-LOCK] Unlock fail, lockKey={}", lockKey, e);
			success = false;
		}
		
		return success;
	}
	
}

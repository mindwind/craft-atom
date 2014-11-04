package io.craft.atom.lock;

import io.craft.atom.lock.api.DLock;
import io.craft.atom.redis.api.ShardedRedisCommand;

import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A implementation of the {@code DLock} base on redis version 2.6.x, which using sharded redis instance.
 * 
 * @author mindwind
 * @version 1.0, Jul 16, 2013
 */
@ToString(of = "shardedRedis")
public class ShardedRedis26DLock implements DLock {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(ShardedRedis26DLock.class);
	
	
	@Getter @Setter private ShardedRedisCommand shardedRedis;
	
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	
	public ShardedRedis26DLock() {
		super();
	}
	
	public ShardedRedis26DLock(ShardedRedisCommand shardedRedis) {
		this.shardedRedis = shardedRedis;
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
			String s = shardedRedis.setnxex(lockKey, lockKey, "1", ttlSeconds);
			if (!"OK".equals(s)) {
				success = false;
			} 
		} catch (Exception e) {
			LOG.error("[CRAFT-ATOM-LOCK] Try lock fail, |lockKey={}, ttl={}, unit={}|", lockKey, ttl, unit, e);
			success = false;
		}
		return success;
	}

	@Override
	public boolean unlock(String lockKey) {
		boolean success = true;
		
		try {
			shardedRedis.del(lockKey, lockKey);
		} catch (Exception e) {
			LOG.error("[CRAFT-ATOM-LOCK] Unlock fail, |lockKey={}|", lockKey, e);
			success = false;
		}
		
		return success;
	}
	
}

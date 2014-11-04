package io.craft.atom.lock;

import io.craft.atom.lock.api.DLock;
import io.craft.atom.redis.api.RedisTransaction;
import io.craft.atom.redis.api.ShardedRedisCommand;

import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A implementation of the {@code DLock} base on redis version 2.4.x, which using sharded redis instance.
 * 
 * @author mindwind
 * @version 1.0, Jul 16, 2013
 */
@ToString(of = "shardedRedis")
public class ShardedRedis24DLock implements DLock {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(ShardedRedis24DLock.class);
	
	
	@Getter @Setter private ShardedRedisCommand shardedRedis;
	
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	
	public ShardedRedis24DLock() {
		super();
	}
	
	public ShardedRedis24DLock(ShardedRedisCommand shardedRedis) {
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
			shardedRedis.watch(lockKey, lockKey);
			if (shardedRedis.get(lockKey, lockKey) == null) {
				success = tryLock0(lockKey, ttlSeconds);
			} else {
				success = false;
			}
		} catch (Exception e) {
			LOG.error("[CRAFT-ATOM-LOCK] Try lock fail, |lockKey={}, ttl={}, unit={}|", lockKey, ttl, unit, e);
			success = false;
		} finally {
			shardedRedis.unwatch(lockKey);
		}
		return success;
	}
	
	private boolean tryLock0(String lockKey, int ttlSeconds) {
		RedisTransaction t = shardedRedis.multi(lockKey);
		t.setex(lockKey, ttlSeconds, "1");
		List<Object> result = shardedRedis.exec(lockKey, t);
		return result != null && result.size() > 0;
	}

	@Override
	public boolean unlock(String lockKey) {
		boolean success = true;
		
		try {
			shardedRedis.del(lockKey, lockKey);
		} catch (Exception e) {
			LOG.error("Unlock fail, |lockKey={}|", lockKey, e);
			success = false;
		}
		
		return success;
	}
	
}

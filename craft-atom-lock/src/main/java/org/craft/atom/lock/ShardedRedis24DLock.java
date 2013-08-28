package org.craft.atom.lock;

import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.lock.api.DLock;
import org.craft.atom.redis.api.RedisTransaction;
import org.craft.atom.redis.api.ShardedRedisCommand;

/**
 * A implementation of the {@code DLock} base on redis version 2.4.x, which using sharded redis instance.
 * 
 * @author mindwind
 * @version 1.0, Jul 16, 2013
 */
@ToString(of = "shardedRedis")
public class ShardedRedis24DLock implements DLock {
	
	private static final Log LOG = LogFactory.getLog(ShardedRedis24DLock.class);
	@Getter @Setter private ShardedRedisCommand shardedRedis;
	
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
			LOG.error(String.format("Try lock fail, args=<lockKey=%s, ttl=%s, unit=%s>", lockKey, ttl, unit), e);
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
			LOG.error(String.format("Unlock fail, args=<lockKey=%s>", lockKey), e);
			success = false;
		}
		
		return success;
	}
	
}

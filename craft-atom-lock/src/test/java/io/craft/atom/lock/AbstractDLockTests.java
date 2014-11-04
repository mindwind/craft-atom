package io.craft.atom.lock;

import io.craft.atom.lock.api.DLock;
import io.craft.atom.lock.api.DLockFactory;
import io.craft.atom.redis.api.Redis;
import io.craft.atom.redis.api.RedisFactory;
import io.craft.atom.redis.api.ShardedRedis;

import org.junit.Assert;

/**
 * @author mindwind
 * @version 1.0, Dec 22, 2013
 */
public abstract class AbstractDLockTests {
	
	protected Redis        redis1      ;
	protected Redis        redis2      ;
	protected DLock        dLock       ;
	protected ShardedRedis shardedRedis;
	protected DLock        shardedDLock;

	
	public AbstractDLockTests() {
		init();
		selfcheck();
	}
	
	private void init() {
		redis1       = RedisFactory.newRedis("localhost", 6379)    ;
		redis2       = RedisFactory.newRedis("localhost", 6380)    ;
		shardedRedis = RedisFactory.newShardedRedis(redis1, redis2);
		dLock        = DLockFactory.newRedis24DLock(redis1)        ;
		shardedDLock = DLockFactory.newRedis24DLock(shardedRedis)  ;
	}
	
	private void selfcheck() {
		try {
			redis1.ping();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[CRAFT-ATOM-LOCK] Self check for redis1 fail");
			Assert.fail();
		}
		
		try {
			redis2.ping();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[CRAFT-ATOM-LOCK] Self check for redis2 fail");
			Assert.fail();
		}
	}
}

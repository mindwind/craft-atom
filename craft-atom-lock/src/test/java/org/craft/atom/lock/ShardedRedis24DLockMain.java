package org.craft.atom.lock;

import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.craft.atom.lock.api.DLock;
import org.craft.atom.lock.api.DLockFactory;
import org.craft.atom.redis.api.RedisFactory;
import org.craft.atom.redis.api.ShardedRedis;

/**
 * @author Hu Feng
 * @version 1.0, Nov 19, 2012
 */
public class ShardedRedis24DLockMain {
	
	private static ShardedRedis shardedRedis;
	private static DLock dlock;
	private static String lockKey = "sharded-redis24-dlock";
	
	private static void init() {
		shardedRedis = RedisFactory.newShardedRedis("localhost:6379,localhost:6380");
		dlock = DLockFactory.newShardedRedis24DLock(shardedRedis);
	}
	
	public static void before(String desc) {
		System.out.println("case -- " + desc);
	}
	
	public static void after() {
		dlock.unlock(lockKey);
	}
	
	public static void main(String[] args) {
		init();
		
		// case 1
		testTryLock1();
		
		// case 2
		testTryLock2();
		
		// case 3
		testTryLock3();
		
		// case 4
		testTryLock4();
	}
	
	// WATCH: lock=true  GET: lock=true   LOCK: false
	public static void testTryLock1() {
		before("testTryLock1");
		
		try {
			shardedRedis.set(lockKey, lockKey, "1");
			boolean b = dlock.tryLock(lockKey, 30, TimeUnit.SECONDS);
			Assert.assertFalse(b);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			after();
		}
	}
	
	// WATCH: lock=false  GET: lock=true    LOCK: false	    execute in debug mode to simulate
	public static void testTryLock2() {
		before("testTryLock2");
		
		try {
			shardedRedis.del(lockKey, lockKey);
			boolean b = dlock.tryLock(lockKey, 30, TimeUnit.SECONDS);
			System.out.println("	lock result=" + b);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			after();
		}
	}
	
	// WATCH: lock=true   GET: lock=false   LOCK: false	    execute in debug mode to simulate
	public static void testTryLock3() {
		before("testTryLock3");
		
		try {
			shardedRedis.set(lockKey, lockKey, "1");
			boolean b = dlock.tryLock(lockKey, 30, TimeUnit.SECONDS);
			Assert.assertFalse(b);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			after();
		}
	}
	
	// WATCH: lock=false  GET: lock=false   LOCK: true	
	public static void testTryLock4() {
		before("testTryLock4");
		
		try {
			shardedRedis.del(lockKey, lockKey);
			boolean b = dlock.tryLock(lockKey, 30, TimeUnit.SECONDS);
			Assert.assertTrue(b);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			after();
		}
	}
	
}

package org.craft.atom.lock;

import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.craft.atom.cache.impl.RedisCache;
import org.craft.atom.lock.impl.RedisDLock24;

/**
 * @author Hu Feng
 * @version 1.0, Nov 19, 2012
 */
public class RedisDLock24Main {
	
	private static RedisCache rc;
	private static DLock dlock;
	private static String lockKey = "lockTest";
	
	private static void init() {
		boolean isShard = true;
		int timeout = 3000;
		rc = new RedisCache(isShard, timeout, "localhost:6379", 1);
		dlock = new RedisDLock24(rc);
	}
	
	public static void before() {
		System.out.println("------------------------------- before");
	}
	
	public static void after() {
		dlock.unlock(lockKey);
		
		System.out.println("------------------------------- after");
	}
	
	public static void main(String[] args) {
		init();
		
		// case 1
//		testTryLock1();
		
		// case 2
//		testTryLock2();
		
		// case 3
//		testTryLock3();
		
		// case 4
		testTryLock4();
	}
	
	// WATCH: lock=true  GET: lock=true   LOCK: false
	public static void testTryLock1() {
		System.out.println("testTryLock1");
		
		before();
		try {
			rc.set(lockKey, "1");
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
		System.out.println("testTryLock2");
		
		before();
		try {
			rc.del(lockKey);
			boolean b = dlock.tryLock(lockKey, 30, TimeUnit.SECONDS);
			Assert.assertFalse(b);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			after();
		}
	}
	
	// WATCH: lock=true   GET: lock=false   LOCK: false	    execute in debug mode to simulate
	public static void testTryLock3() {
		System.out.println("testTryLock3");
		
		before();
		try {
			rc.set(lockKey, "1");
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
		System.out.println("testTryLock4");
		
		before();
		try {
			rc.del(lockKey);
			boolean b = dlock.tryLock(lockKey, 30, TimeUnit.SECONDS);
			Assert.assertTrue(b);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			after();
		}
	}
	
}

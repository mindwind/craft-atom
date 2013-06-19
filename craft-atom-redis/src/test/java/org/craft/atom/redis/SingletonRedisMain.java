package org.craft.atom.redis;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import junit.framework.Assert;

import org.craft.atom.redis.api.RedisDataException;

/**
 * @author mindwind
 * @version 1.0, Jun 19, 2013
 */
public class SingletonRedisMain {
	
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 6379;
	private static final String K = "test-key";
	private static final String V = "test-value";
	private static SingletonRedis redis;
	
	private static void init() {
		redis = new SingletonRedis(HOST, PORT);
	}
	
	private static void before(String desc) {
		System.out.println("case ------> " + desc);
	}
	
	private static void after() {
		redis.del(K);
	}
	
	public static void main(String[] args) throws Exception {
		init();
		
		// ~ -------------------------------------------------------------------------------------------- Transactions
		
		testMultiExec();
		testWatchMultiExec();
		testMultiDiscard();
		testWatchUnwatchMultiExec();
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------ Test Cases
	
	private static void testWatchUnwatchMultiExec() {
		before("testWatchUnwatchMultiExec");
		
		String wkey = "watch-key";
		redis.watch(wkey);
		redis.set(wkey, "1");
		redis.unwatch();
		redis.multi();
		redis.set(K, V);
		redis.exec();
		String v = redis.get(K);
		Assert.assertEquals(V, v);
		
		after();
	}
	
	private static void testMultiDiscard() {
		before("testMultiDiscard");
		
		redis.multi();
		redis.set(K, V);
		redis.get(K);
		redis.discard();
		try {
			redis.exec();
			Assert.fail();
		} catch (RedisDataException e) {
		}
		
		after();
	}
	
	private static void testWatchMultiExec() throws InterruptedException {
		before("testWatchMultiExec");
		
		final String wkey = "watch-key";
		final Lock lock = new ReentrantLock();
		final Condition c1 = lock.newCondition();
		final Condition c2 = lock.newCondition();
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				lock.lock();
				try {
					redis.watch(wkey);
					c1.await();
					redis.multi();
					redis.set(K, V);
					redis.exec();
					c2.signal();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		});
		t1.start();
		
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				lock.lock();
				try {
					redis.set(wkey, "1");
					c1.signal();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		});
		t2.start();
		
		lock.lock();
		try {
			c2.await();
		} finally {
			lock.unlock();
		}
		
		String v = redis.get(K);
		Assert.assertNull(v);
		redis.del(wkey);
		
		after();
	}
	
	private static void testMultiExec() {
		before("testMultiExec");
		
		redis.multi();
		redis.set(K, V);
		redis.get(K);
		List<Object> r = redis.exec();
		Assert.assertEquals(2, r.size());
		Assert.assertEquals(V, r.get(1));
		
		after();
	}
	
}

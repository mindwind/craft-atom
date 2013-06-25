package org.craft.atom.redis;

import java.util.List;
import java.util.Set;
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
	private static DefaultRedis redis;
	
	private static void init() {
		redis = new DefaultRedis(HOST, PORT);
	}
	
	private static void before(String desc) {
		System.out.println("case --> " + desc);
		redis.del(K);
	}
	
	private static void after() {
		
	}
	
	public static void main(String[] args) throws Exception {
		init();
		
		// Keys
		testDel();
		testExists();
		testExpire();
		testExpireat();
		testKeys();
		testSortByGet();
		
		// Hashes
		
		// Lists
		testLpushLrangeLlen();
		testBlpop();
		
		// Transactions
		testMultiExec();
		testWatchMultiExec();
		testMultiDiscard();
		testWatchUnwatchMultiExec();
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------ Test Cases
	
	
	private static void testBlpop() throws InterruptedException {
		before("testBlpop");
		
		final Lock lock = new ReentrantLock();
		final Condition c = lock.newCondition();
		
		Thread t1 = new Thread(new Runnable() {	
			@Override
			public void run() {
				String v = redis.blpop(K);
				Assert.assertEquals("3", v);
				lock.lock();
				try {
					c.signal();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		});
		
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				redis.lpush(K, "1", "2", "3");
			}
		});
		
		t1.start();
		Thread.sleep(1000);
		t2.start();
		
		lock.lock();
		try {
			c.await();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		
		after();
	}
	
	private static void testLpushLrangeLlen() {
		before("testLpush");
		
		long len = redis.lpush(K, "1", "2", "3");
		Assert.assertEquals(3, len);
		len = redis.llen(K);
		Assert.assertEquals(3, len);
		List<String> l = redis.lrange(K, 0, -1);
		Assert.assertEquals("1", l.get(2));
		
		after();
	}
	
	private static void testSortByGet() {
		before("testSortByGet");
		
		redis.lpush(K, "1", "2", "3");
		redis.set("w_1", "3");
		redis.set("w_2", "2");
		redis.set("w_3", "1");
		redis.set("o_1", "1-aaa");
		redis.set("o_2", "2-bbb");
		redis.set("o_3", "3-ccc");
		List<String> l = redis.sort(K, "w_*");
		Assert.assertEquals("1", l.get(2));
		l = redis.sort(K, "w_*", new String[] { "o_*" });
		Assert.assertEquals("1-aaa", l.get(2));
		
		after();
	}
	
	private static void testKeys() {
		before("testKeys");
		
		redis.set(K, V);
		Set<String> keys = redis.keys("test*");
		Assert.assertTrue(keys.size() > 0);
		
		after();
	}
	
	private static void testExpireat() throws InterruptedException {
		before("testExpireat");
		
		redis.set(K, V);
		redis.expireat(K, (System.currentTimeMillis() + 2000) / 1000);
		Thread.sleep(3000);
		boolean b = redis.exists(K);
		Assert.assertEquals(false, b);
		
		after();
	}
	
	private static void testExpire() throws InterruptedException {
		before("testExpire");
		
		redis.set(K, V);
		redis.expire(K, 3);
		Thread.sleep(4000);
		boolean b = redis.exists(K);
		Assert.assertEquals(false, b);
		
		after();
	}
	
	private static void testExists() {
		before("testExists");
		
		redis.set(K, V);
		boolean b = redis.exists(K);
		Assert.assertEquals(true, b);
		
		after();
	}
	
	private static void testDel() {
		before("testDel");
		
		redis.set(K, V);
		redis.del(K);
		boolean b = redis.exists(K);
		Assert.assertEquals(false, b);
		
		after();
	}
	
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

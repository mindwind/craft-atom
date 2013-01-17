package org.craft.atom.cache;

import java.util.List;

import org.craft.atom.cache.impl.RedisCache;
import org.junit.Assert;

/**
 * @author Hu Feng
 * @version 1.0, Sep 10, 2012
 */
public class RedisCacheMain {
	
	private static RedisCache rc;
	private static String key = "test";
	
	private static void init() {
		boolean isShard = true;
		int timeout = 3000;
		rc = new RedisCache(isShard, timeout, "localhost:6379", 1);
	}
	
	public static void before() {
		System.out.println("------------------------------- before");
	}
	
	public static void after() {
		rc.del(key);
		
		System.out.println("------------------------------- after");
	}
	
	public static void main(String[] args) {
		init();
		RedisCacheMain rcm = new RedisCacheMain();
		
		// case 1
//		rcm.testGetAndSet();

//		// case 2
//		rcm.testSetex();
//
//		// case 3
//		rcm.testTransaction();
//
//		// case 4
//		rcm.testTransactionInMultiThread();
//		
//		// case 5
//		rcm.testHmget();
		
		// case 6
		rcm.testSetexInTransaction();

	}
	
	public void testSetexInTransaction() {
		before();
		try {
			testSetexInTransaction0();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			after();
		}
	}
	
	private void testSetexInTransaction0() {
		Transaction tx = null;
		try {
			tx = rc.beginTransaction(key);
			rc.setex(key, 5, "1");
			List<Object> result = tx.commit();
			System.out.println("testSetexInTransaction() result=" + result);
		} finally {
			if (tx != null) {
				tx.close();
			}
		}
	}
	
	public void testHmget() {
		before();
		try {
			rc.del(key);
			String v = rc.hget(key, "f1");
			List<String> fields = rc.hmget(key, "f1", "f2");
			Assert.assertNull(v);
			Assert.assertNotNull(fields);
			System.out.println("testHmget - " + v + "," + fields);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			after();
		}
	}
	
	public void testGetAndSet() {
		before();
		try {
			rc.set(key, "redis.cache.test.GetAndSet");
			String value = rc.get(key);
			Assert.assertEquals("redis.cache.test.GetAndSet", value);
			System.out.println("testGetAndSet");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			after();
		}
	}
	
	public void testSetex() {
		before();
		try {
			rc.setex(key, 5, "test");
			System.out.println("testSetex");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			after();
		}
	}
	
	public void testTransaction() {
		before();
		try {
			pushData();
			doTransaction(0);
			System.out.println("testTransaction");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			after();
		}
	}
	
	public void testTransactionInMultiThread() {
		before();
		try {
			pushData();
			Thread t1 = new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("t1 start");
					doTransaction(10000);
					System.out.println("t1 end");
				}
			}, "t1");
			t1.start();
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Thread t2 = new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("t2 start");
					doTransaction(0);
					System.out.println("t2 end");
				}
			}, "t2");
			t2.start();
			
			try {
				t1.join();
				t2.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("testTransactionInMultiThread");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			after();
		}
	}
	
	private void pushData() {
		for (int i = 0; i < 1000; i++) {
			rc.lpush(key, "test" + i);
		}
	}
	
	private void doTransaction(long millis) {
		long s = System.currentTimeMillis();
		Transaction tx = null;
		try {
			rc.watch(key);
			tx = rc.beginTransaction(key);
			System.out.println(Thread.currentThread() + " Begin transaction: " + tx);
			rc.lrange(key, 0, -1);

			if (millis > 0) {
				try {
					Thread.sleep(millis);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

			rc.del(key);
			List<Object> result = tx.commit();
			long e = System.currentTimeMillis();
			System.out.println(Thread.currentThread() + " Commit transaciton elapse: " + (e - s) + " ms");

			if (result != null) {
				@SuppressWarnings("unchecked")
				List<String> l = (List<String>) result.get(0);
				System.out.println(Thread.currentThread() + " get list size=" + l.size() + ", del size=" + result.get(1));
			}
		} finally {
			if (tx != null) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				tx.close();
			}
		}
	}
	
}

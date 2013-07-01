package org.craft.atom.redis;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import junit.framework.Assert;

import org.craft.atom.redis.api.Redis;
import org.craft.atom.redis.api.RedisDataException;
import org.craft.atom.redis.api.RedisException;
import org.craft.atom.redis.api.RedisFactory;
import org.craft.atom.redis.api.RedisPubSub;
import org.craft.atom.redis.api.RedisTransaction;
import org.craft.atom.redis.api.handler.RedisPsubscribeHandler;
import org.craft.atom.redis.api.handler.RedisSubscribeHandler;

/**
 * @author mindwind
 * @version 1.0, Jun 19, 2013
 */
public class RedisMain extends TestMain {
	
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 6379;
	private static final int PORT2 = 6380;
	private static final String key = "foo";
	private static final String value = "bar";
	private static Redis redis;
	private static Redis redis2;
	
	private static void init() {
		redis = RedisFactory.newRedis(HOST, PORT);
		redis2 = RedisFactory.newRedis(HOST, PORT2);
	}
	
	protected static void after() {
		redis.flushall();
		redis2.flushall();
	}
	
	public static void main(String[] args) throws Exception {
		init();
		
		// Keys
		del_set();
		dump_restore();
		exists();
		expire();
		expireat();
		keys();
		migrate();
		move_select();
		objectrefcount();
		objectencoding();
		objectidletime();
		persist();
		pexpire();
		pexpireat();
		pttl();
		randomkey();
		rename();
		renamenx();
		sort();
		sort_dest();
		ttl();
		type();
		
		// Hashes
		
		// Lists
		blpop();
		lpush_lrange_llen();
		
		// Sets
		
		// Sorted Sets
		
		// Pub/Sub
		subscribe_publish_unsubscribe();
	    psubscribe_publish_punsubscribe();
		
		// Transactions
		multi_exec();
		watch_multi_exec();
		multi_discard();
		watch_unwatch_multi_exec();
	}
		
	
	// ~ ------------------------------------------------------------------------------------------------ Test Cases
	
	
	private static void type() {
		before("type");
		
		String r = redis.type(key);
		Assert.assertEquals("none", r);
		
		redis.set(key, value);
		r = redis.type(key);
		Assert.assertEquals("string", r);
		
		after();
	}
	
	private static void ttl() {
		before("ttl");
		
		long r = redis.ttl(key);
		Assert.assertTrue(r < 0);
		redis.setex(key, 100, value);
		r = redis.ttl(key);
		Assert.assertTrue(r > 0);
		
		after();
	}
	
	private static void sort() {
		before("sort");
		
		redis.lpush(key, "1", "2", "3");
		List<String> l = redis.sort(key);
		Assert.assertEquals("3", l.get(2));
		
		l = redis.sort(key, true);
		Assert.assertEquals("1", l.get(2));
		
		redis.lpush(key, "a");
		l = redis.sort(key, true, true);
		Assert.assertEquals("a", l.get(0));
		
		redis.del(key);
		redis.lpush(key, "1", "2", "3");
		l = redis.sort(key, 1, 2);
		Assert.assertEquals("3", l.get(1));
		Assert.assertEquals(2, l.size());
		
		redis.lpush(key, "a");
		l = redis.sort(key, 0, 3, true, true);
		Assert.assertEquals("a", l.get(0));
		Assert.assertEquals(3, l.size());
		
		redis.flushall();
		redis.lpush(key, "1", "2", "3");
		redis.set("w_1", "3");
		redis.set("w_2", "2");
		redis.set("w_3", "1");
		redis.set("o_1", "1-aaa");
		redis.set("o_2", "2-bbb");
		redis.set("o_3", "3-ccc");
		String bypattern = "w_*";
		String[] getpatterns = new String[] { "o_*" };
		
		l = redis.sort(key, bypattern, new String[] {});
		Assert.assertEquals("1", l.get(2));
		
		l = redis.sort(key, bypattern, getpatterns);
		Assert.assertEquals("1-aaa", l.get(2));
		
		l = redis.sort(key, bypattern, true, getpatterns);
		Assert.assertEquals("3-ccc", l.get(2));
		
		l = redis.sort(key, bypattern, true, true, getpatterns);
		Assert.assertEquals("3-ccc", l.get(2));
		
		l = redis.sort(key, bypattern, 0, 1, getpatterns);
		Assert.assertEquals("3-ccc", l.get(0));
		Assert.assertEquals(1, l.size());
		
		l = redis.sort(key, bypattern, 0, 1, true, true, getpatterns);
		Assert.assertEquals("1-aaa", l.get(0));
		Assert.assertEquals(1, l.size());
		
		after();
	}
	
	private static void sort_dest() {
		before("sort_dest");
		
		redis.lpush(key, "1", "2", "3");
		String dest = "foo1";
		redis.sort(key, dest);
		List<String> l = redis.lrange(dest, 0, -1);
		Assert.assertEquals("3", l.get(2));
		
		redis.sort(key, true, dest);
		l = redis.lrange(dest, 0, -1);
		Assert.assertEquals("1", l.get(2));
		
		redis.lpush(key, "a");
		redis.sort(key, true, true, dest);
		l = redis.lrange(dest, 0, -1);
		Assert.assertEquals("a", l.get(0));
		
		redis.del(key);
		redis.lpush(key, "1", "2", "3");
		redis.sort(key, 1, 2, dest);
		l = redis.lrange(dest, 0, -1);
		Assert.assertEquals("3", l.get(1));
		Assert.assertEquals(2, l.size());
		
		redis.lpush(key, "a");
		redis.sort(key, 0, 3, true, true, dest);
		l = redis.lrange(dest, 0, -1);
		Assert.assertEquals("a", l.get(0));
		Assert.assertEquals(3, l.size());
		
		redis.flushall();
		redis.lpush(key, "1", "2", "3");
		redis.set("w_1", "3");
		redis.set("w_2", "2");
		redis.set("w_3", "1");
		redis.set("o_1", "1-aaa");
		redis.set("o_2", "2-bbb");
		redis.set("o_3", "3-ccc");
		String bypattern = "w_*";
		String[] getpatterns = new String[] { "o_*" };
		
		redis.sort(key, bypattern, dest, new String[] {});
		l = redis.lrange(dest, 0, -1);
		Assert.assertEquals("1", l.get(2));
		
		redis.sort(key, bypattern, dest, getpatterns);
		l = redis.lrange(dest, 0, -1);
		Assert.assertEquals("1-aaa", l.get(2));
		
		redis.sort(key, bypattern, true, dest, getpatterns);
		l = redis.lrange(dest, 0, -1);
		Assert.assertEquals("3-ccc", l.get(2));
		
		redis.sort(key, bypattern, true, true, dest, getpatterns);
		l = redis.lrange(dest, 0, -1);
		Assert.assertEquals("3-ccc", l.get(2));
		
		redis.sort(key, bypattern, 0, 1, dest, getpatterns);
		l = redis.lrange(dest, 0, -1);
		Assert.assertEquals("3-ccc", l.get(0));
		Assert.assertEquals(1, l.size());
		
		redis.sort(key, bypattern, 0, 1, true, true, dest, getpatterns);
		l = redis.lrange(dest, 0, -1);
		Assert.assertEquals("1-aaa", l.get(0));
		Assert.assertEquals(1, l.size());
		
		after();
	}
	
	
	private static void renamenx() {
		before("renamenx");
		
		redis.set(key, value);
		long r = redis.renamenx(key, "foo1");
		Assert.assertTrue(r == 1);
		redis.set("foo2", "bar2");
		r = redis.renamenx("foo1", "foo2");
		Assert.assertTrue(r == 0);
		
		after();
	}
	
	private static void rename() {
		before("rename");
		
		redis.set(key, value);
		redis.rename(key, "foo1");
		Assert.assertTrue(redis.exists("foo1").booleanValue());
		
		after();
	}
	
	private static void randomkey() {
		before("randomkey");
		
		String r = redis.randomkey();
		Assert.assertNull(r);
		redis.set(key, value);
		r = redis.randomkey();
		Assert.assertNotNull(r);
		
		after();
	}
	
	private static void pttl() {
		before("pttl");
		
		long r = redis.pttl(key);
		Assert.assertTrue(r < 0);
		redis.setex(key, 100, value);
		r = redis.pttl(key);
		Assert.assertTrue(r > 0);
		
		after();
	}
	
	private static void pexpireat() {
		before("pexpireat");
		
		redis.set(key, value);
		long r = redis.pexpireat(key, (System.currentTimeMillis() + 2000));
		Assert.assertEquals(1, r);
		redis.del(key);
		r = redis.pexpireat(key, (System.currentTimeMillis() + 2000));
		Assert.assertEquals(0, r);
		
		
		after();
	}
	
	private static void pexpire() {
		before("pexpire");
		
		redis.set(key, value);
		long r = redis.pexpire(key, 3000);
		Assert.assertEquals(1, r);
		redis.del(key);
		r = redis.pexpire(key, 3000);
		Assert.assertEquals(0, r);
		
		after();
	}
	
	private static void persist() {
		before("persist");
		
		long r = redis.persist(key);
		Assert.assertTrue(r == 0);
		redis.setex(key, 10, value);
		r = redis.persist(key);
		Assert.assertTrue(r == 1);
		
		after();
	}
	
	private static void objectidletime() {
		before("objectidletime");
		
		redis.set(key, value);
		long r = redis.objectidletime(key);
		Assert.assertTrue(r >= 0);
		
		after();
	}
	
	private static void objectencoding() {
		before("objectencoding");
		
		redis.set(key, value);
		String r = redis.objectencoding(key);
		Assert.assertEquals("raw", r);
		
		after();
	}
	
	private static void objectrefcount() {
		before("objectrefcount");
		
		Long r = redis.objectrefcount(key);
		Assert.assertNull(r);
		redis.set(key, value);
		r = redis.objectrefcount(key);
		Assert.assertEquals(1, r.longValue());
		
		after();
	}
	
	private static void move_select() {
		before("move_select");
		
		long r = redis.move(key, 1);
		Assert.assertEquals(0, r);
		redis.set(key, value);
		r = redis.move(key, 1);
		Assert.assertEquals(1, r);
		Assert.assertEquals(false, redis.exists(key).booleanValue());
		redis.select(1);
		Assert.assertEquals(true, redis.exists(key).booleanValue());
		redis.select(0);
		
		after();
	}
	
	private static void migrate() {
		before("migrate");
		
		redis.set(key, value);
		redis.migrate(HOST, 6380, key, 0, 2000);
		Assert.assertEquals(true, redis2.exists(key).booleanValue());
		Assert.assertEquals(false, redis.exists(key).booleanValue());
		
		after();
	}
	
	private static void dump_restore() {
		before("dump_restore");
		
		redis.set(key, value);
		byte[] serializedvalue = redis.dump(key);
		redis.restore("foo1", 0, serializedvalue);
		Assert.assertEquals(true, redis.exists("foo1").booleanValue());
		
		after();
	}
	
	private static void psubscribe_publish_punsubscribe() {
		before("psubscribe_publish_punsubscribe");
		
		final Redis redis = RedisFactory.newRedis(HOST, PORT, 2000, 5);
		final String[] patterns = new String[] { "fo*", "ba*" };
		RedisPubSub pubsub = redis.psubscribe(new RedisPsubscribeHandler() {
			@Override
			public void onPsubscribe(String pattern, int no) {
				System.out.println("	on-psubscribe, pattern=" + pattern + ", no=" + no);
			}
			
			@Override
			public void onMessage(String pattern, String channel, String message) {
				System.out.println("	on-message, pattern=" + pattern + ", channel=" + channel + ", message=" + message);
				Assert.assertEquals("bar", message);
			}
			
			@Override
			public void onException(RedisException e) {
				try {
					System.out.println("	on-exception, retry, e=" + e.getMessage());
					Thread.sleep(3000);
					redis.psubscribe(this, patterns);
				} catch (Exception e2) {
				}
			}
		}, patterns);	
		redis.publish("foo", "bar");
		redis.publish("bar", "bar");
		redis.punsubscribe(pubsub, "ba*");
		redis.publish("foo", "bar");
		redis.publish("bar", "bar");
		redis.punsubscribe(pubsub, "ba*", "fo*");
		after();
	}
	
	private static void subscribe_publish_unsubscribe() {
		before("subscribe_publish_unsubscribe");
		
		final Redis redis = RedisFactory.newRedis(HOST, PORT, 2000, 5);
		final String[] channels = new String[] { "foo", "foo1" };
		RedisPubSub pubsub = redis.subscribe(new RedisSubscribeHandler() {
			
			@Override
			public void onSubscribe(String channel, int no) {
				System.out.println("	on-subscribe, channel=" + channel + ", no=" + no);
			}
			
			@Override
			public void onMessage(String channel, String message) {
				System.out.println("	on-message, channel=" + channel + ", message=" + message);
				Assert.assertEquals("bar", message);
			}
			
			@Override
			public void onException(RedisException e) {
				try {
					e.printStackTrace();
					System.out.println("	on-exception, retry, e=" + e.getMessage());
					Thread.sleep(3000);
					redis.subscribe(this, channels);
				} catch (Exception e2) {
				}
			}
		}, channels);
		
		redis.publish("foo", "bar");
		redis.publish("foo1", "bar");
		redis.unsubscribe(pubsub, "foo1");
		redis.publish("foo", "bar");
		redis.publish("foo1", "bar");
		redis.unsubscribe(pubsub, "foo", "foo1");
		
		after();
	}
	
	private static void blpop() throws InterruptedException {
		before("blpop");
		
		final Lock lock = new ReentrantLock();
		final Condition c = lock.newCondition();
		
		Thread t1 = new Thread(new Runnable() {	
			@Override
			public void run() {
				String v = redis.blpop(key);
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
				redis.lpush(key, "3");
			}
		});
		
		t1.start();
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
	
	private static void lpush_lrange_llen() {
		before("lpush_lrange_llen");
		
		long len = redis.lpush(key, "1", "2", "3");
		Assert.assertEquals(3, len);
		len = redis.llen(key);
		Assert.assertEquals(3, len);
		List<String> l = redis.lrange(key, 0, -1);
		Assert.assertEquals("1", l.get(2));
		
		after();
	}
	
	private static void keys() {
		before("keys");
		
		redis.set(key, value);
		Set<String> keys = redis.keys("foo*");
		Assert.assertTrue(keys.size() > 0);
		
		after();
	}
	
	private static void expireat() throws InterruptedException {
		before("expireat");
		
		redis.set(key, value);
		long r = redis.expireat(key, (System.currentTimeMillis() + 2000) / 1000);
		Assert.assertEquals(1, r);
		redis.del(key);
		r = redis.expireat(key, (System.currentTimeMillis() + 2000) / 1000);
		Assert.assertEquals(0, r);
		
		after();
	}
	
	private static void expire() throws InterruptedException {
		before("expire");
		
		redis.set(key, value);
		long r = redis.expire(key, 3);
		Assert.assertEquals(1, r);
		redis.del(key);
		r = redis.expire(key, 3);
		Assert.assertEquals(0, r);
		
		after();
	}
	
	private static void exists() {
		before("exists");
		
		redis.set(key, value);
		boolean b = redis.exists(key);
		Assert.assertEquals(true, b);
		
		after();
	}
	
	private static void del_set() {
		before("del_set");
		
		redis.set(key, value);
		redis.del(key);
		boolean b = redis.exists(key);
		Assert.assertEquals(false, b);
		
		after();
	}
	
	private static void watch_unwatch_multi_exec() {
		before("watch_unwatch_multi_exec");
		
		String wkey = "watch-key";
		redis.watch(wkey);
		redis.set(wkey, "1");
		redis.unwatch();
		RedisTransaction t = redis.multi();
		t.set(key, value);
		redis.exec(t);
		String v = redis.get(key);
		Assert.assertEquals(value, v);
		
		after();
	}
	
	private static void multi_discard() {
		before("multi_discard");
		
		RedisTransaction t = redis.multi();
		t.set(key, value);
		t.get(key);
		redis.discard(t);
		try {
			redis.exec(t);
			Assert.fail();
		} catch (RedisDataException e) {
		}
		
		after();
	}
	
	private static void watch_multi_exec() throws InterruptedException {
		before("watch_multi_exec");
		
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
					RedisTransaction t = redis.multi();
					t.set(key, value);
					redis.exec(t);
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
		
		String v = redis.get(key);
		Assert.assertNull(v);
		redis.del(wkey);
		
		after();
	}
	
	private static void multi_exec() {
		before("multi_exec");
		
		RedisTransaction t = redis.multi();
		t.set(key, value);
		t.get(key);
		List<Object> l = redis.exec(t);
		Assert.assertEquals(2, l.size());
		Assert.assertEquals(value, l.get(1));
		
		after();
	}
	
}

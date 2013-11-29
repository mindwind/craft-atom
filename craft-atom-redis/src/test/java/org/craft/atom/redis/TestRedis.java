package org.craft.atom.redis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.craft.atom.redis.api.Redis;
import org.craft.atom.redis.api.RedisFactory;
import org.craft.atom.test.CaseCounter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link Redis} api
 * 
 * @author mindwind
 * @version 1.0, Nov 28, 2013
 */
public class TestRedis {
	
	
	private static final String HOST  = "127.0.0.1";
	private static final int    PORT1 = 6379       ;
	private static final int    PORT2 = 6381       ;
	
	
	private String key    = "foo"                                 ;
	private String field  = "1"                                   ;
	private String value  = "bar"                                 ;
	private String script = "return redis.call('set','foo','bar')";
	private Redis  redis1                                         ;
	private Redis  redis2                                         ;

	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public TestRedis() {
		init();
		selfcheck();
	}
	
	private void init() {
		redis1 = RedisFactory.newRedis(HOST, PORT1);
		redis2 = RedisFactory.newRedis(HOST, PORT2);
	}
	
	private void selfcheck() {
		try {
			redis1.ping();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[CRAFT-ATOM-REDIS] Self check for redis1 fail");
			Assert.fail();
		}
		
		try {
			redis2.ping();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[CRAFT-ATOM-REDIS] Self check for redis2 fail");
			Assert.fail();
		}
	}
	
	@Before
	public void before() {
		clean();
	}
	
	@After
	public void after() {
		clean();
	}
	
	private void clean() {
		redis1.flushall();
		redis2.flushall();
	}
	
	
	// ~ --------------------------------------------------------------------------------------------------------- Keys
	
	
	@Test
	public void testDel() {
		redis1.set(key, value);
		redis1.del(key);
		boolean b = redis1.exists(key);
		Assert.assertEquals(false, b);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test del. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testDumpRestore() {
		redis1.set(key, value);
		byte[] serializedvalue = redis1.dump(key);
		redis1.restore("foo1", 0, serializedvalue);
		Assert.assertEquals(true, redis1.exists("foo1").booleanValue());
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test dump & restore. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testExists() {
		redis1.set(key, value);
		boolean b = redis1.exists(key);
		Assert.assertEquals(true, b);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test exists. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testExpire() {
		redis1.set(key, value);
		long r = redis1.expire(key, 3);
		Assert.assertEquals(1, r);
		redis1.del(key);
		r = redis1.expire(key, 3);
		Assert.assertEquals(0, r);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test expire. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testExpireat() {
		redis1.set(key, value);
		long r = redis1.expireat(key, (System.currentTimeMillis() + 2000) / 1000);
		Assert.assertEquals(1, r);
		redis1.del(key);
		r = redis1.expireat(key, (System.currentTimeMillis() + 2000) / 1000);
		Assert.assertEquals(0, r);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test expireat. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testKeys() {
		redis1.set(key, value);
		Set<String> keys = redis1.keys("foo*");
		Assert.assertTrue(keys.size() > 0);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test keys. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testMigrate() {
		redis1.set(key, value);
		redis1.migrate(HOST, PORT2, key, 0, 2000);
		Assert.assertEquals(true, redis2.exists(key).booleanValue());
		Assert.assertEquals(false, redis1.exists(key).booleanValue());
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test migrate. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testMoveSelect() {
		long r = redis1.move(key, 1);
		Assert.assertEquals(0, r);
		redis1.set(key, value);
		r = redis1.move(key, 1);
		Assert.assertEquals(1, r);
		Assert.assertEquals(false, redis1.exists(key).booleanValue());
		redis1.select(1);
		Assert.assertEquals(true, redis1.exists(key).booleanValue());
		redis1.select(0);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test move & select. ", CaseCounter.incr(4)));
	}
	
	@Test
	public void testObjectrefcount() {
		Long r = redis1.objectrefcount(key);
		Assert.assertNull(r);
		redis1.set(key, value);
		r = redis1.objectrefcount(key);
		Assert.assertEquals(1, r.longValue());
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test objectrefcount. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testObjectencoding() {
		redis1.set(key, value);
		String r = redis1.objectencoding(key);
		Assert.assertEquals("raw", r);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test objectencoding. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testObjectidletime() {
		redis1.set(key, value);
		long r = redis1.objectidletime(key);
		Assert.assertTrue(r >= 0);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test objectidletime. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testPersist() {
		long r = redis1.persist(key);
		Assert.assertTrue(r == 0);
		redis1.setex(key, 10, value);
		r = redis1.persist(key);
		Assert.assertTrue(r == 1);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test persist. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testPexpire() {
		redis1.set(key, value);
		long r = redis1.pexpire(key, 3000);
		Assert.assertEquals(1, r);
		redis1.del(key);
		r = redis1.pexpire(key, 3000);
		Assert.assertEquals(0, r);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test pexpire. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testPexpireat() {
		redis1.set(key, value);
		long r = redis1.pexpireat(key, (System.currentTimeMillis() + 2000));
		Assert.assertEquals(1, r);
		redis1.del(key);
		r = redis1.pexpireat(key, (System.currentTimeMillis() + 2000));
		Assert.assertEquals(0, r);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test pexpireat. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testPttl() {
		long r = redis1.pttl(key);
		Assert.assertTrue(r < 0);
		redis1.setex(key, 100, value);
		r = redis1.pttl(key);
		Assert.assertTrue(r > 0);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test pttl. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testRandomkey() {
		String r = redis1.randomkey();
		Assert.assertNull(r);
		redis1.set(key, value);
		r = redis1.randomkey();
		Assert.assertNotNull(r);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test randomkey. ", CaseCounter.incr(2)));
	}
	
	@Test 
	public void testRename() {
		String r = redis1.randomkey();
		Assert.assertNull(r);
		redis1.set(key, value);
		r = redis1.randomkey();
		Assert.assertNotNull(r);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test rename. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testRenamenx() {
		redis1.set(key, value);
		long r = redis1.renamenx(key, "foo1");
		Assert.assertTrue(r == 1);
		redis1.set("foo2", "bar2");
		r = redis1.renamenx("foo1", "foo2");
		Assert.assertTrue(r == 0);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test renamenx. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testSort() {
		redis1.lpush(key, "1", "2", "3");
		List<String> l = redis1.sort(key);
		Assert.assertEquals("3", l.get(2));
		
		l = redis1.sort(key, true);
		Assert.assertEquals("1", l.get(2));
		
		redis1.lpush(key, "a");
		l = redis1.sort(key, true, true);
		Assert.assertEquals("a", l.get(0));
		
		redis1.del(key);
		redis1.lpush(key, "1", "2", "3");
		l = redis1.sort(key, 1, 2);
		Assert.assertEquals("3", l.get(1));
		Assert.assertEquals(2, l.size());
		
		redis1.lpush(key, "a");
		l = redis1.sort(key, 0, 3, true, true);
		Assert.assertEquals("a", l.get(0));
		Assert.assertEquals(3, l.size());
		
		redis1.flushall();
		redis1.lpush(key, "1", "2", "3");
		redis1.set("w_1", "3");
		redis1.set("w_2", "2");
		redis1.set("w_3", "1");
		redis1.set("o_1", "1-aaa");
		redis1.set("o_2", "2-bbb");
		redis1.set("o_3", "3-ccc");
		String bypattern = "w_*";
		String[] getpatterns = new String[] { "o_*" };
		
		l = redis1.sort(key, bypattern, new String[] {});
		Assert.assertEquals("1", l.get(2));
		
		l = redis1.sort(key, bypattern, getpatterns);
		Assert.assertEquals("1-aaa", l.get(2));
		
		l = redis1.sort(key, bypattern, true, getpatterns);
		Assert.assertEquals("3-ccc", l.get(2));
		
		l = redis1.sort(key, bypattern, true, true, getpatterns);
		Assert.assertEquals("3-ccc", l.get(2));
		
		l = redis1.sort(key, bypattern, 0, 1, getpatterns);
		Assert.assertEquals("3-ccc", l.get(0));
		Assert.assertEquals(1, l.size());
		
		l = redis1.sort(key, bypattern, 0, 1, true, true, getpatterns);
		Assert.assertEquals("1-aaa", l.get(0));
		Assert.assertEquals(1, l.size());
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test sort. ", CaseCounter.incr(15)));
	}
	
	@Test
	public void testSortWithDestination() {
		redis1.lpush(key, "1", "2", "3");
		String dest = "foo1";
		redis1.sort(key, dest);
		List<String> l = redis1.lrange(dest, 0, -1);
		Assert.assertEquals("3", l.get(2));
		
		redis1.sort(key, true, dest);
		l = redis1.lrange(dest, 0, -1);
		Assert.assertEquals("1", l.get(2));
		
		redis1.lpush(key, "a");
		redis1.sort(key, true, true, dest);
		l = redis1.lrange(dest, 0, -1);
		Assert.assertEquals("a", l.get(0));
		
		redis1.del(key);
		redis1.lpush(key, "1", "2", "3");
		redis1.sort(key, 1, 2, dest);
		l = redis1.lrange(dest, 0, -1);
		Assert.assertEquals("3", l.get(1));
		Assert.assertEquals(2, l.size());
		
		redis1.lpush(key, "a");
		redis1.sort(key, 0, 3, true, true, dest);
		l = redis1.lrange(dest, 0, -1);
		Assert.assertEquals("a", l.get(0));
		Assert.assertEquals(3, l.size());
		
		redis1.flushall();
		redis1.lpush(key, "1", "2", "3");
		redis1.set("w_1", "3");
		redis1.set("w_2", "2");
		redis1.set("w_3", "1");
		redis1.set("o_1", "1-aaa");
		redis1.set("o_2", "2-bbb");
		redis1.set("o_3", "3-ccc");
		String bypattern = "w_*";
		String[] getpatterns = new String[] { "o_*" };
		
		redis1.sort(key, bypattern, dest, new String[] {});
		l = redis1.lrange(dest, 0, -1);
		Assert.assertEquals("1", l.get(2));
		
		redis1.sort(key, bypattern, dest, getpatterns);
		l = redis1.lrange(dest, 0, -1);
		Assert.assertEquals("1-aaa", l.get(2));
		
		redis1.sort(key, bypattern, true, dest, getpatterns);
		l = redis1.lrange(dest, 0, -1);
		Assert.assertEquals("3-ccc", l.get(2));
		
		redis1.sort(key, bypattern, true, true, dest, getpatterns);
		l = redis1.lrange(dest, 0, -1);
		Assert.assertEquals("3-ccc", l.get(2));
		
		redis1.sort(key, bypattern, 0, 1, dest, getpatterns);
		l = redis1.lrange(dest, 0, -1);
		Assert.assertEquals("3-ccc", l.get(0));
		Assert.assertEquals(1, l.size());
		
		redis1.sort(key, bypattern, 0, 1, true, true, dest, getpatterns);
		l = redis1.lrange(dest, 0, -1);
		Assert.assertEquals("1-aaa", l.get(0));
		Assert.assertEquals(1, l.size());
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test sort with destination. ", CaseCounter.incr(15)));
	}
	
	@Test
	public void testTtl() {
		long r = redis1.ttl(key);
		Assert.assertTrue(r < 0);
		redis1.setex(key, 100, value);
		r = redis1.ttl(key);
		Assert.assertTrue(r > 0);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test ttl. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testType() {
		String r = redis1.type(key);
		Assert.assertEquals("none", r);
		redis1.set(key, value);
		r = redis1.type(key);
		Assert.assertEquals("string", r);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test type. ", CaseCounter.incr(2)));
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Strings
	
	
	@Test
	public void testAppend() {
		redis1.append(key, value);
		Assert.assertEquals("bar", value);
		redis1.append(key, "bar");
		Assert.assertEquals("barbar", redis1.get(key));
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test append. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testBitcount() {
		redis1.set(key, "foobar");
		long c = redis1.bitcount(key);
		Assert.assertEquals(26, c);
		c = redis1.bitcount(key, 1, 1);
		Assert.assertEquals(6, c);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test bitcount. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testSetbitGetbitBitop() {
		boolean b = redis1.setbit(key, 1, true);
		Assert.assertFalse(b);
		b = redis1.getbit(key, 1);
		Assert.assertTrue(b);
		
		redis1.bitnot("foo1", key);
		b = redis1.getbit("foo1", 1);
		Assert.assertFalse(b);
		
		redis1.bitand("foo2", key, "foo1");
		b = redis1.getbit("foo2", 1);
		Assert.assertFalse(b);
		
		redis1.bitor("foo3", key, "foo2");
		b = redis1.getbit("foo3", 1);
		Assert.assertTrue(b);
		
		redis1.bitxor("foo4", key, "foo2");
		b = redis1.getbit("foo4", 1);
		Assert.assertTrue(b);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test setbit & getbit & bitop. ", CaseCounter.incr(6)));
	}
	
	@Test
	public void testDecrIncrByFloat() {
		long r = redis1.incr(key);
		Assert.assertEquals(1, r);
		
		r = redis1.decr(key);
		Assert.assertEquals(0, r);
		
		r = redis1.incrby(key, 10);
		Assert.assertEquals(10, r);
		
		r = redis1.decrby(key, 5);
		Assert.assertEquals(5, r);
		
		double d = redis1.incrbyfloat(key, 1.55);
		Assert.assertEquals(6.55, d, 0.0);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test decr & incr & decrby & incrby & incrbyfloat. ", CaseCounter.incr(5)));
	}
	
	@Test
	public void testGet() {
		String r = redis1.get(key);
		Assert.assertNull(r);
		
		redis1.set(key, value);
		r = redis1.get(key);
		Assert.assertEquals(value, r);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test get. ", CaseCounter.incr(6)));
	}
	
	@Test
	public void testGetrangeSetrange() {
		redis1.set(key, value);
		String r = redis1.getrange(key, 0, 10);
		Assert.assertEquals(value, r);
		
		r = redis1.getrange(key, -1, -3);
		Assert.assertEquals("", r);
		
		redis1.set(key, value);
		redis1.setrange(key, 1, "bbccc");
		r = redis1.get(key);
		Assert.assertEquals("bbbccc", r);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test getrange & setrange. ", CaseCounter.incr(3)));
	}
	
	@Test
	public void testGetSet() {
		String r = redis1.getset(key, value);
		Assert.assertNull(r);
		r = redis1.get(key);
		Assert.assertEquals(value, r);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test getset. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testMgetMset() {
		redis1.mset(key, value, "foo1", "bar1");
		List<String> l = redis1.mget(key, "foo1");
		Assert.assertEquals(2, l.size());
		Assert.assertEquals("bar", l.get(0));
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test mget & mset. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testMsetnx() {
		redis1.set(key, value);
		long r = redis1.msetnx(key, value, "foo1", "bar1");
		Assert.assertEquals(0, r);
		
		redis1.del(key);
		r = redis1.msetnx(key, value, "foo1", "bar1");
		Assert.assertEquals(1, r);
		List<String> l = redis1.mget(key, "foo1");
		Assert.assertEquals(2, l.size());
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test msetnx. ", CaseCounter.incr(3)));
	}
	
	@Test
	public void testPsetex() {
		redis1.psetex(key, 2000, value);
		String r = redis1.get(key);
		long t = redis1.pttl(key);
		Assert.assertEquals(value, r);
		Assert.assertTrue(t > 0 && t <= 2000);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test psetex. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testSet() {
		// setxx
		String r = redis1.setxx(key, value);
		Assert.assertNull(r);
		redis1.set(key, value);
		r = redis1.setxx(key, value);
		Assert.assertNotNull(r);
		
		// setex
		redis1.del(key);
		redis1.setex(key, 100, value);
		Assert.assertEquals(value, redis1.get(key));
		long c = redis1.ttl(key);
		Assert.assertTrue(c > 0 && c <= 100);
		
		// setxxex
		redis1.del(key);
		r = redis1.setxxex(key, value, 100);
		Assert.assertNull(r);
		redis1.set(key, value);
		r = redis1.setxxex(key, value, 100);
		Assert.assertNotNull(r);
		Assert.assertEquals(value, redis1.get(key));
		c = redis1.ttl(key);
		Assert.assertTrue(c > 0 && c <= 100);
		
		// setxxpx
		
		redis1.del(key);
		r = redis1.setxxpx(key, value, 100000);
		Assert.assertNull(r);
		redis1.set(key, value);
		r = redis1.setxxpx(key, value, 100000);
		Assert.assertNotNull(r);
		Assert.assertEquals(value, redis1.get(key));
		c = redis1.ttl(key);
		Assert.assertTrue(c > 0 && c <= 100000);
		
		// setnx
		redis1.set(key, value);
		long b = redis1.setnx(key, value);
		Assert.assertEquals(0, b);
		redis1.del(key);
		b = redis1.setnx(key, value);
		Assert.assertEquals(1, b);
		Assert.assertEquals(value, redis1.get(key));
		
		// setnxex
		redis1.set(key, value);
		r = redis1.setnxex(key, value, 100);
		Assert.assertNull(r);
		redis1.del(key);
		r = redis1.setnxex(key, value, 100);
		Assert.assertNotNull(r);
		Assert.assertEquals(value, redis1.get(key));
		c = redis1.ttl(key);
		Assert.assertTrue(c > 0 && c <= 100);
		
		// setnxpx
		redis1.set(key, value);
		r = redis1.setnxpx(key, value, 100000);
		Assert.assertNull(r);
		redis1.del(key);
		r = redis1.setnxpx(key, value, 100000);
		Assert.assertNotNull(r);
		Assert.assertEquals(value, redis1.get(key));
		c = redis1.ttl(key);
		Assert.assertTrue(c > 0 && c <= 100000);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test set. ", CaseCounter.incr(23)));
	}
	
	@Test
	public void testStrlen() {
		long len = redis1.strlen(key);
		Assert.assertTrue(len == 0);
		redis1.set(key, value);
		len = redis1.strlen(key);
		Assert.assertTrue(len == 3);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test set. ", CaseCounter.incr(2)));
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Hashes
	
	
	@Test
	public void testHdelHgetHsetHexists() {
		long c = redis1.hset(key, field, value);
		Assert.assertEquals(1, c);
		c = redis1.hset(key, field, value);
		Assert.assertEquals(0, c);
		boolean b = redis1.hexists(key, field);
		Assert.assertTrue(b);
		String v = redis1.hget(key, field);
		Assert.assertEquals(value, v);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test hdel & hget & hset & hexists. ", CaseCounter.incr(4)));
	}
	
	@Test
	public void testHgetall() {
		redis1.hset(key, field, value);
		redis1.hset(key, "2", "bar1");
		Map<String, String> map = redis1.hgetall(key);
		Assert.assertEquals(2, map.size());
		Assert.assertEquals("bar1", map.get("2"));
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test hgetall. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testHincrByFloat() {
		long c = redis1.hincrby(key, field, 10);
		Assert.assertEquals(10, c);
		double d = redis1.hincrbyfloat(key, field, 6.66);
		Assert.assertEquals(16.66, d, 0.00);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test hincr & hincrbyfloat. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testHkeys() {
		redis1.hset(key, field, value);
		Set<String> set = redis1.hkeys(key);
		Assert.assertEquals(1, set.size());
		for (String f : set) {
			Assert.assertEquals(field, f);
		}
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test hkeys. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testHlen() {
		redis1.hset(key, field, value);
		long len = redis1.hlen(key);
		Assert.assertEquals(1, len);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test hlen. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testHmgetHmset() {
		Map<String, String> hash = new HashMap<String, String>();
		hash.put("1", "1");
		hash.put("2", "2");
		redis1.hmset(key, hash);
		List<String> l = redis1.hmget(key, "1", "2");
		Assert.assertEquals(2, l.size());
		Assert.assertEquals("1", l.get(0));
		Assert.assertEquals("2", l.get(1));
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test hmget & hmset. ", CaseCounter.incr(3)));
	}
	
	@Test
	public void testHsetnx() {
		long c = redis1.hsetnx(key, field, value);
		Assert.assertEquals(1, c);
		c = redis1.hsetnx(key, field, value);
		Assert.assertEquals(0, c);
		String v = redis1.hget(key, field);
		Assert.assertEquals(value, v);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test hsetnx. ", CaseCounter.incr(3)));
	}
	
	@Test
	public void testHvals() {
		List<String> l = redis1.hvals(key);
		Assert.assertEquals(0, l.size());
		redis1.hset(key, field, value);
		l = redis1.hvals(key);
		Assert.assertEquals(1, l.size());
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test hvals. ", CaseCounter.incr(2)));
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Lists
	
	
	@Test
	public void testBlpopLpush() {
		final Lock lock = new ReentrantLock();
		final Condition c = lock.newCondition();
		
		Thread t1 = new Thread(new Runnable() {	
			@Override
			public void run() {
				String v = redis1.blpop(key);
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
				long len = redis1.lpush(key, "3");
				Assert.assertEquals(1, len);
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
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test blpop & lpush. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testBlpopTimeoutRpush() throws InterruptedException {
		Map<String, String> map = redis1.blpop(1, key);
		Assert.assertEquals(0, map.size());
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Map<String, String> hash = redis1.blpop(5, "foo", "foo1");
				Assert.assertEquals("bar1", hash.get("foo1"));
			}
		});
		t.start();
		long len = redis1.rpush("foo1", "bar1");
		Assert.assertEquals(1, len);
		t.join();
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test blpop with timeout & rpush. ", CaseCounter.incr(3)));
	}
	
	@Test
	public void testBrpop() {
		final Lock lock = new ReentrantLock();
		final Condition c = lock.newCondition();
		
		Thread t1 = new Thread(new Runnable() {	
			@Override
			public void run() {
				String v = redis1.brpop(key);
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
				redis1.lpush(key, "3");
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
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test brpop. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testBrpopTimeout() throws InterruptedException {
		Map<String, String> map = redis1.brpop(1, key);
		Assert.assertEquals(0, map.size());
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Map<String, String> hash = redis1.blpop(5, "foo", "foo1");
				Assert.assertEquals("bar1", hash.get("foo1"));
			}
		});
		t.start();
		redis1.rpush("foo1", "bar1");
		t.join();
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test brpop with timeout. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testBrpoppushLpop() {
		String v = redis1.brpoplpush(key, "foo1", 1);
		Assert.assertNull(v);
		
		redis1.rpush(key, value);
		v = redis1.brpoplpush(key, "foo1", 1);
		Assert.assertEquals(value, v);
		v = redis1.lpop("foo1");
		Assert.assertEquals(value, v);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test brpoplpush & lpop. ", CaseCounter.incr(3)));
	}
	
	@Test
	public void testLindexRpop() {
		redis1.rpush(key, value, "a", "b", "c");
		String v = redis1.lindex(key, 2);
		Assert.assertEquals("b", v);
		v = redis1.rpop(key);
		Assert.assertEquals("c", v);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test lindex & rpop. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testLinsert() {
		long len = redis1.lpush(key, value);
		len = redis1.linsertbefore(key, value, "before-bar");
		len = redis1.linsertafter(key, value, "after-bar");
		Assert.assertEquals(3, len);
		List<String> l = redis1.lrange(key, 0, -1);
		Assert.assertEquals("before-bar", l.get(0));
		Assert.assertEquals("after-bar", l.get(2));
		
		len = redis1.linsertbefore(key, "aaa", "aaa");
		Assert.assertEquals(-1, len);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test linsert. ", CaseCounter.incr(4)));
	}
	
	@Test
	public void testLrangeLlen() {
		long len = redis1.lpush(key, "1", "2", "3");
		Assert.assertEquals(3, len);
		len = redis1.llen(key);
		Assert.assertEquals(3, len);
		List<String> l = redis1.lrange(key, 0, -1);
		Assert.assertEquals("1", l.get(2));
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test lrange & llen. ", CaseCounter.incr(3)));
	}
	
	@Test
	public void testLpushxRpushx() {
		long len = redis1.lpushx(key, value);
		len = redis1.rpushx(key, value);
		Assert.assertEquals(0, len);
		redis1.lpush(key, value);;
		len = redis1.lpushx(key, value);
		len = redis1.rpushx(key, value);
		Assert.assertEquals(3, len);
		List<String> l = redis1.lrange(key, 0, -1);
		for (String v : l) {
			Assert.assertEquals(value, v);
		}
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test lpushx & rpushx. ", CaseCounter.incr(3)));
	}
	
	@Test
	public void testLrem() {
		redis1.rpush(key, "1", "2", "1", "2", "3");
		long r = redis1.lrem(key, 1, "1");
		Assert.assertEquals(1, r);
		Assert.assertTrue(Arrays.equals(new String[]{ "2", "1", "2", "3" }, redis1.lrange(key, 0, -1).toArray(new String[] {})));
		redis1.lrem(key, -1, "3");
		Assert.assertTrue(Arrays.equals(new String[]{ "2", "1", "2" }, redis1.lrange(key, 0, -1).toArray(new String[] {})));
		redis1.lrem(key, 0, "2");
		Assert.assertTrue(Arrays.equals(new String[]{ "1" }, redis1.lrange(key, 0, -1).toArray(new String[] {})));
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test lrem. ", CaseCounter.incr(4)));
	}
	
	@Test
	public void testLset() {
		redis1.rpush(key, "1", "2", "3");
		redis1.lset(key, 0, "4");
		Assert.assertTrue(Arrays.equals(new String[]{ "4", "2", "3" }, redis1.lrange(key, 0, -1).toArray(new String[] {})));
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test lset. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testLtrim() {
		redis1.rpush(key, "1", "2", "3", "4", "5");
		redis1.ltrim(key, 0, 10);
		Assert.assertEquals(5, redis1.llen(key).longValue());
		redis1.ltrim(key, 0, 3);
		Assert.assertEquals(4, redis1.llen(key).longValue());
		redis1.ltrim(key, 4, 3);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test ltrim. ", CaseCounter.incr(2)));
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Sets
	
	
	@Test
	public void testSaddScard() {
		long c = redis1.sadd(key, "a", "b", "c");
		Assert.assertEquals(3, c);
		c = redis1.sadd(key, "a", "b", "c");
		Assert.assertEquals(0, c);
		c = redis1.scard(key);
		Assert.assertEquals(3, c);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test sadd & scard. ", CaseCounter.incr(3)));
	}
	
	@Test
	public void testSdiffStore() {
		redis1.sadd("foo1", "a", "c");
		redis1.sadd("foo2", "c", "d", "e");
		Set<String> diff = redis1.sdiff("foo1", "foo2");
		Assert.assertEquals(1, diff.size());
		Assert.assertEquals("a", diff.iterator().next());
		long len = redis1.sdiffstore(key, "foo1", "foo2");
		Assert.assertEquals(1, len);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test sdiff & sdiffstore. ", CaseCounter.incr(3)));
	}
	
	@Test
	public void testSinterStoreSpop() {
		redis1.sadd("foo1", "a", "b", "c");
		redis1.sadd("foo2", "c", "d", "e");
		Set<String> set = redis1.sinter("foo1", "foo2");
		Assert.assertEquals("c", set.iterator().next());
		long len = redis1.sinterstore(key, "foo1", "foo2");
		Assert.assertEquals(1, len);
		String v = redis1.spop(key);
		Assert.assertEquals("c", v);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test sinter & sinterstore & spop. ", CaseCounter.incr(3)));
	}
	
	@Test
	public void testSismember() {
		redis1.sadd(key, value);
		boolean b = redis1.sismember(key, value);
		Assert.assertTrue(b);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test sismember. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testSmembers() {
		redis1.sadd(key, value);
		Set<String> set = redis1.smembers(key);
		Assert.assertEquals(value, set.iterator().next());
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test smembers. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testSmove() {
		redis1.sadd(key, "a");
		redis1.sadd(key, "b");
		redis1.smove(key, "foo1", "a");
		Assert.assertFalse(redis1.sismember(key, "a"));
		Assert.assertTrue(redis1.sismember("foo1", "a"));
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test smove. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testSrandmember() {
		redis1.sadd(key, value);
		String v = redis1.srandmember(key);
		Assert.assertEquals(value, v);
		
		List<String> l = redis1.srandmember(key, 5);
		Assert.assertEquals(1, l.size());
		
		redis1.sadd(key, "a");
		l = redis1.srandmember(key, 5);
		Assert.assertEquals(2, l.size());
		
		l = redis1.srandmember("foo1", 5);
		Assert.assertEquals(0, l.size());
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test srandmember. ", CaseCounter.incr(4)));
	}
	
	@Test
	public void testSrem() {
		redis1.sadd(key, "a", "b");
		long r = redis1.srem(key, "a", "c");
		Assert.assertEquals(1, r);
		Assert.assertFalse(redis1.sismember(key, "a"));
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test srem. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testSunionStore() {
		redis1.sadd("foo1", "a", "c");
		redis1.sadd("foo2", "c", "d", "e");
		Set<String> u = redis1.sunion("foo1", "foo2");
		Assert.assertEquals(4, u.size());
		long len = redis1.sunionstore(key, "foo1", "foo2");
		Assert.assertEquals(4, len);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test sunion & sunionstore. ", CaseCounter.incr(2)));
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------- Sorted Sets
	
	
	@Test
	public void testZaddZcard() {
		redis1.sadd("foo1", "a", "c");
		redis1.sadd("foo2", "c", "d", "e");
		Set<String> u = redis1.sunion("foo1", "foo2");
		Assert.assertEquals(4, u.size());
		long len = redis1.sunionstore(key, "foo1", "foo2");
		Assert.assertEquals(4, len);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test zadd & zcard. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testZcount() {
		redis1.zadd(key, 10, "a");
		redis1.zadd(key, 20, "b");
		redis1.zadd(key, 30, "c");
		long c = redis1.zcount(key, 20, 30);
		Assert.assertEquals(2, c);
		c = redis1.zcount(key, "20", "inf");
		Assert.assertEquals(2, c);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test zcount. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testZincrbyZscore() {
		redis1.zadd(key, 1, value);
		double score = redis1.zincrby(key, 10.5, value);
		Assert.assertEquals(11.5, score, 0.0);
		score = redis1.zscore(key, value);
		Assert.assertEquals(11.5, score, 0.0);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test zincrby & zscore. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testZrange() {
		redis1.zadd(key, 1, "a");
		redis1.zadd(key, 2, "b");
		Set<String> set = redis1.zrange(key, 0, -1);
		Iterator<String> it = set.iterator();
		Assert.assertTrue(it.next().equals("a"));
		Assert.assertTrue(it.next().equals("b"));
		Map<String, Double> map = redis1.zrangewithscores(key, 0, -1);
		Iterator<Entry<String, Double>> it2 = map.entrySet().iterator();
		Assert.assertEquals(it2.next().getValue(), 1.0, 0.0);
		Assert.assertEquals(it2.next().getValue(), 2.0, 0.0);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test zrange. ", CaseCounter.incr(4)));
	}
	
	@Test
	public void testZrevrange() {
		redis1.zadd(key, 1, "a");
		redis1.zadd(key, 2, "b");
		Set<String> set = redis1.zrevrange(key, 0, -1);
		Iterator<String> it = set.iterator();
		Assert.assertTrue(it.next().equals("b"));
		Assert.assertTrue(it.next().equals("a"));
		Map<String, Double> map = redis1.zrevrangewithscores(key, 0, -1);
		Iterator<Entry<String, Double>> it2 = map.entrySet().iterator();
		Assert.assertEquals(it2.next().getValue(), 2.0, 0.0);
		Assert.assertEquals(it2.next().getValue(), 1.0, 0.0);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test zrevrange. ", CaseCounter.incr(4)));
	}
	
	@Test
	public void testZinterstore() {
		redis1.zadd("foo1", 1, "a");
		redis1.zadd("foo1", 1, "b");
		redis1.zadd("foo2", 2, "b");
		redis1.zadd("foo2", 1, "c");
		Map<String, Integer> wk = new HashMap<String, Integer>();
		wk.put("foo1", 5);
		wk.put("foo2", 2);
		
		// zinterstore
		long l = redis1.zinterstore(key, "foo1", "foo2");
		Assert.assertEquals(1, l);
		Map<String, Double> map = redis1.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 3.0, 0.0);
		redis1.zinterstore(key, wk);
		map = redis1.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 9.0, 0.0);
		
		// zinterstoremax
		l = redis1.zinterstoremax(key, "foo1", "foo2");
		Assert.assertEquals(1, l);
		map = redis1.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 2.0, 0.0);
		redis1.zinterstoremax(key, wk);
		map = redis1.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 5.0, 0.0);
		
		// zinterstoremin
		l = redis1.zinterstoremin(key, "foo1", "foo2");
		Assert.assertEquals(1, l);
		map = redis1.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 1.0, 0.0);
		redis1.zinterstoremin(key, wk);
		map = redis1.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 4.0, 0.0);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test zinterstore. ", CaseCounter.incr(9)));
	}
	
	@Test
	public void testZunionstore() {
		redis1.zadd("foo1", 1, "a");
		redis1.zadd("foo1", 1, "b");
		redis1.zadd("foo2", 2, "b");
		redis1.zadd("foo2", 1, "c");
		Map<String, Integer> wk = new HashMap<String, Integer>();
		wk.put("foo1", 5);
		wk.put("foo2", 2);
		
		// zunionstore
		long l = redis1.zunionstore(key, "foo1", "foo2");
		Assert.assertEquals(3, l);
		Map<String, Double> map = redis1.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 3.0, 0.0);
		redis1.zunionstore(key, wk);
		map = redis1.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 9.0, 0.0);
		
		// zunionstoremax
		l = redis1.zunionstoremax(key, "foo1", "foo2");
		Assert.assertEquals(3, l);
		map = redis1.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 2.0, 0.0);
		redis1.zunionstoremax(key, wk);
		map = redis1.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 5.0, 0.0);
		
		// zunionstoremin
		l = redis1.zunionstoremin(key, "foo1", "foo2");
		Assert.assertEquals(3, l);
		map = redis1.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 1.0, 0.0);
		redis1.zunionstoremin(key, wk);
		map = redis1.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 4.0, 0.0);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test zunionstore. ", CaseCounter.incr(9)));
	}
	
	@Test
	public void testZrangebyscore() {
		redis1.zadd(key, 1, "a");
		redis1.zadd(key, 2, "b");
		redis1.zadd(key, 3, "c");
		redis1.zadd(key, 4, "d");
		redis1.zadd(key, 5, "e");
		
		Set<String> set = redis1.zrangebyscore(key, 1, 2);
		Assert.assertTrue(set.contains("a"));
		Assert.assertTrue(set.contains("b"));
		set = redis1.zrangebyscore(key, "-inf", "2");
		Assert.assertTrue(set.contains("a"));
		Assert.assertTrue(set.contains("b"));
		
		set = redis1.zrangebyscore(key, 1, 5, 0, 2);
		Assert.assertTrue(set.contains("a"));
		Assert.assertTrue(set.contains("b"));
		set = redis1.zrangebyscore(key, "-inf", "5", 0, 2);
		Assert.assertTrue(set.contains("a"));
		Assert.assertTrue(set.contains("b"));
		
		Map<String, Double> map = redis1.zrangebyscorewithscores(key, 1, 2);
		Assert.assertTrue(map.get("a") == 1.0);
		Assert.assertTrue(map.get("b") == 2.0);
		map = redis1.zrangebyscorewithscores(key, "-inf", "2");
		Assert.assertTrue(map.get("a") == 1.0);
		Assert.assertTrue(map.get("b") == 2.0);
		map = redis1.zrangebyscorewithscores(key, 1, 5, 0, 2);
		Assert.assertTrue(map.get("a") == 1.0);
		Assert.assertTrue(map.get("b") == 2.0);
		map = redis1.zrangebyscorewithscores(key, "-inf", "5", 0, 2);
		Assert.assertTrue(map.get("a") == 1.0);
		Assert.assertTrue(map.get("b") == 2.0);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test zrangebyscore. ", CaseCounter.incr(16)));
	}
	
	@Test
	public void testZrevrangebyscore() {
		redis1.zadd(key, 1, "a");
		redis1.zadd(key, 2, "b");
		redis1.zadd(key, 3, "c");
		redis1.zadd(key, 4, "d");
		redis1.zadd(key, 5, "e");
		
		Set<String> set = redis1.zrevrangebyscore(key, 2, 1);
		Assert.assertTrue(set.contains("a"));
		Assert.assertTrue(set.contains("b"));
		set = redis1.zrevrangebyscore(key, "2", "-inf");
		Assert.assertTrue(set.contains("a"));
		Assert.assertTrue(set.contains("b"));
		
		set = redis1.zrevrangebyscore(key, 5, 1, 0, 2);
		Assert.assertTrue(set.contains("d"));
		Assert.assertTrue(set.contains("e"));
		set = redis1.zrevrangebyscore(key, "5", "-inf", 0, 2);
		Assert.assertTrue(set.contains("d"));
		Assert.assertTrue(set.contains("e"));
		
		Map<String, Double> map = redis1.zrevrangebyscorewithscores(key, 2, 1);
		Assert.assertTrue(map.get("a") == 1.0);
		Assert.assertTrue(map.get("b") == 2.0);
		map = redis1.zrevrangebyscorewithscores(key, "2", "-inf");
		Assert.assertTrue(map.get("a") == 1.0);
		Assert.assertTrue(map.get("b") == 2.0);
		map = redis1.zrevrangebyscorewithscores(key, 5, 1, 0, 2);
		Assert.assertTrue(map.get("d") == 4.0);
		Assert.assertTrue(map.get("e") == 5.0);
		map = redis1.zrevrangebyscorewithscores(key, "5", "-inf", 0, 2);
		Assert.assertTrue(map.get("d") == 4.0);
		Assert.assertTrue(map.get("e") == 5.0);
		
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test zrevrangebyscore. ", CaseCounter.incr(16)));
	}
	
	@Test
	public void testZrank() {
		redis1.zadd(key, 1, "a");
		redis1.zadd(key, 2, "b");
		Long r = redis1.zrank(key, "b");
		Assert.assertEquals(1, r.longValue());
		r = redis1.zrank(key, "c");
		Assert.assertNull(r);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test zrank. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testZrevrank() {
		redis1.zadd(key, 1, "a");
		redis1.zadd(key, 2, "b");
		Long r = redis1.zrevrank(key, "b");
		Assert.assertEquals(0, r.longValue());
		r = redis1.zrank(key, "c");
		Assert.assertNull(r);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test zrevrank. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testZrem() {
		redis1.zadd(key, 1, "a");
		redis1.zadd(key, 2, "b");
		redis1.zadd(key, 3, "c");
		redis1.zrem(key, "a", "b");
		Set<String> set = redis1.zrange(key, 0, -1);
		Assert.assertEquals(set.iterator().next(), "c");
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test zrem. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testZremrangebyrank() {
		redis1.zadd(key, 1, "a");
		redis1.zadd(key, 2, "b");
		redis1.zadd(key, 3, "c");
		redis1.zremrangebyrank(key, 0, 1);
		Set<String> set = redis1.zrange(key, 0, -1);
		Assert.assertEquals(set.iterator().next(), "c");
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test zremrangebyrank. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testZremrangebyscore() {
		redis1.zadd(key, 1, "a");
		redis1.zadd(key, 2, "b");
		redis1.zadd(key, 3, "c");
		redis1.zremrangebyscore(key, 1, 2);
		Set<String> set = redis1.zrange(key, 0, -1);
		Assert.assertEquals(set.iterator().next(), "c");
		
		redis1.zadd(key, 1, "a");
		redis1.zadd(key, 2, "b");
		redis1.zremrangebyscore(key, "-inf", "2");
		set = redis1.zrange(key, 0, -1);
		Assert.assertEquals(set.iterator().next(), "c");
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test zremrangebyrank. ", CaseCounter.incr(2)));
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------- Pub/Sub
	
	
	@Test
	public void testSubscribePublishUnsubscribe() {
		
	}
}

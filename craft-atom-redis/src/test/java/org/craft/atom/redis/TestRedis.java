package org.craft.atom.redis;

import java.util.Set;

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
//	private String field  = "1"                                   ;
	private String value  = "bar"                                 ;
//	private String script = "return redis.call('set','foo','bar')";
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
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
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
}

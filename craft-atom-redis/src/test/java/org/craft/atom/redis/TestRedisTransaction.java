package org.craft.atom.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.craft.atom.redis.api.RedisTransaction;
import org.craft.atom.test.CaseCounter;
import org.junit.Test;

/**
 * Test for {@link RedisTransaction}
 * 
 * @author mindwind
 * @version 1.0, Dec 3, 2013
 */
public class TestRedisTransaction extends AbstractRedisTests {

	
	private String key   = "foo";
	private String value = "bar";
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public TestRedisTransaction() {
		super();
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testTransactionKeys() {
		RedisTransaction t = redis1.multi();
		t.set(key, value);
		t.get(key);
		t.exists(key);
		t.expire(key, 10);
		t.expireat(key, System.currentTimeMillis() / 1000 + 10000);
		List<Object> l = redis1.exec(t);
		Assert.assertEquals(5, l.size());
		Assert.assertEquals("OK", l.get(0));
		Assert.assertEquals("bar", l.get(1));
		Assert.assertEquals(true, l.get(2));
		Assert.assertEquals(new Long(1), l.get(3));
		Assert.assertEquals(new Long(1), l.get(4));
		
		redis1.persist(key);
		t = redis1.multi();
		t.objectrefcount(key);
		t.objectencoding(key);
		t.objectidletime(key);
		t.pexpire(key, 10000);
		t.pexpireat(key, System.currentTimeMillis() + 10000);
		t.ttl(key);
		t.pttl(key);
		t.randomkey();
		t.type(key);
		t.keys("fo*");
		t.dump(key);
		t.del(key);
		l = redis1.exec(t);
		Assert.assertEquals(12, l.size());
		Assert.assertEquals(new Long(1), l.get(0));
		Assert.assertEquals("raw", l.get(1));
		Assert.assertEquals(new Long(0), l.get(2));
		Assert.assertEquals(new Long(1), l.get(3));
		Assert.assertEquals(new Long(1), l.get(4));
		Assert.assertTrue((Long) l.get(5) > 0);
		Assert.assertTrue((Long) l.get(6) > 0);
		Assert.assertEquals("foo", l.get(7));
		Assert.assertEquals("string", l.get(8));
		Assert.assertEquals("foo", ((Set<String>) l.get(9)).iterator().next());
		Assert.assertEquals(new Long(1), l.get(11));
		
		redis1.set(key, value);
		redis1.set("foo1", "bar1");
		redis1.set("foo2", "bar2");
		redis1.set("foo3", "bar3");
		t = redis1.multi();
		t.migrate(HOST, 6380, key, 0, 2000);
		t.move("foo1", 1);
		t.rename("foo2", "newfoo");
		t.renamenx("foo3", "newfoonx");
		l = redis1.exec(t);
		Assert.assertEquals(4, l.size());
		Assert.assertEquals("OK", l.get(0));
		Assert.assertEquals(new Long(1), l.get(1));
		Assert.assertEquals("OK", l.get(2));
		Assert.assertEquals(new Long(1), l.get(3));
		
		redis1.flushall();
		redis1.set(key, value);
		byte[] serializedvalue = redis1.dump(key);
		t = redis1.multi();
		t.restore("foo1", 0, serializedvalue);
		l = redis1.exec(t);
		Assert.assertEquals(1, l.size());
		Assert.assertEquals("OK", l.get(0));
		
		redis1.flushall();
		redis1.set("w_1", "3");
		redis1.set("w_2", "2");
		redis1.set("w_3", "1");
		redis1.set("o_1", "1-aaa");
		redis1.set("o_2", "2-bbb");
		redis1.set("o_3", "3-ccc");
		String bypattern = "w_*";
		String[] getpatterns = new String[] { "o_*" };
		redis1.lpush(key, "1", "2", "3");
		t = redis1.multi();
		t.sort(key);
		t.sort(key, true);
		t.sort(key, true, true);
		t.sort(key, 1, 2);
		t.sort(key, 0, 3, true, true);
		t.sort(key, bypattern, new String[] {});
		t.sort(key, bypattern, getpatterns);
		t.sort(key, bypattern, true, getpatterns);
		t.sort(key, bypattern, true, true, getpatterns);
		t.sort(key, bypattern, 0, 1, getpatterns);
		t.sort(key, bypattern, 0, 1, true, true, getpatterns);
		l = redis1.exec(t);
		Assert.assertEquals(11, l.size());
		
		String dest = "foo1";
		t = redis1.multi();
		t.sort(key, dest);
		t.sort(key, true, dest);
		t.sort(key, true, true, dest);
		t.sort(key, 1, 2, dest);
		t.sort(key, 0, 3, true, true, dest);
		t.sort(key, bypattern, dest, new String[] {});
		t.sort(key, bypattern, dest, getpatterns);
		t.sort(key, bypattern, true, dest, getpatterns);
		t.sort(key, bypattern, true, true, dest, getpatterns);
		t.sort(key, bypattern, 0, 1, dest, getpatterns);
		t.sort(key, bypattern, 0, 1, true, true, dest, getpatterns);
		l = redis1.exec(t);
		Assert.assertEquals(11, l.size());
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test transaction keys. ", CaseCounter.incr(27)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testTransactionStrings() {
		String key1 = "foo1";
		String key2 = "foo2";
		redis1.set(key1, "foobar");
		redis1.set(key2, "abcdef");
		redis1.set("setrange", "hello world");
		redis1.set("setxx", "hello world");
		
		RedisTransaction t = redis1.multi();
		t.append("append", value);
		t.bitcount(key1);           
		t.bitcount(key1, 1, 1);    
		t.bitnot("bitnot", key1);
		t.bitand("bitand", key1, key2);
		t.bitor("bitor", key1, key2);
		t.bitxor("bitxor", key1, key2);
		t.decr(key);
		t.decrby(key, 5);
		t.setbit("mykey", 7, true);
		t.getbit("mykey", 7);
		t.getrange(key1, 3, 10);
		t.getset(key2, "foobar2");
		t.incr(key);
		t.incrby(key, 10);
		t.incrbyfloat(key, 0.5);
		t.msetnx("msetnx1", "abc", "msetnx2", "123");
		t.mset("mset1", "abc", "mset2", "123");
		t.mget("mset1", "mset2");
		t.psetex("psetex", 10000, "123");
		t.set("set", "123");
		t.get("set");
		t.setex("setex", 10, "123");
		t.setnx("setnx", "123");
		t.setnxex("setnxex", "123", 100);
		t.setnxpx("setnxpx", "123", 10000);
		t.setxx("setxx", "123");
		t.setxxex("setxx", "123", 1000);
		t.setxxpx("setxx", "123", 10000);
		t.setrange("setrange", 6, "redis");
		t.strlen(key1);
		List<Object> l = redis1.exec(t);
		
		Assert.assertEquals(31, l.size());
		Assert.assertEquals(new Long(3), l.get(0));
		Assert.assertEquals(new Long(26), l.get(1));
		Assert.assertEquals(new Long(6), l.get(2));
		Assert.assertEquals(new Long(6), l.get(3));
		Assert.assertEquals(new Long(6), l.get(4));
		Assert.assertEquals(new Long(6), l.get(5));
		Assert.assertEquals(new Long(6), l.get(6));
		Assert.assertEquals(new Long(-1), l.get(7));
		Assert.assertEquals(new Long(-6), l.get(8));
		Assert.assertFalse((Boolean) l.get(9));
		Assert.assertTrue((Boolean) l.get(10));
		Assert.assertEquals("bar", l.get(11));
		Assert.assertEquals("abcdef", l.get(12));
		Assert.assertEquals(new Long(-5), l.get(13));
		Assert.assertEquals(new Long(5), l.get(14));
		Assert.assertEquals(new Double(5.5), l.get(15));
		Assert.assertEquals(new Long(1), l.get(16));
		Assert.assertEquals("OK", l.get(17));
		Assert.assertEquals("abc", ((List<String>) l.get(18)).iterator().next());
		Assert.assertEquals("OK", l.get(19));
		Assert.assertEquals("OK", l.get(20));
		Assert.assertEquals("123", l.get(21));
		Assert.assertEquals("OK", l.get(22));
		Assert.assertEquals(new Long(1), l.get(23));
		Assert.assertEquals("OK", l.get(24));
		Assert.assertEquals("OK", l.get(25));
		Assert.assertEquals("OK", l.get(26));
		Assert.assertEquals("OK", l.get(27));
		Assert.assertEquals("OK", l.get(28));
		Assert.assertEquals(new Long(11), l.get(29));
		Assert.assertEquals(new Long(6), l.get(30));
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test transaction strings. ", CaseCounter.incr(32)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testTransactionHashes() {
		redis1.hset("hdel", "1", value);
		redis1.hset("hdel", "2", value);
		redis1.hset("hgetall", "1", value);
		Map<String, String> fieldvalues = new HashMap<String, String>();
		fieldvalues.put("a", "a");
		fieldvalues.put("b", "b");
		
		RedisTransaction t = redis1.multi();
		t.hdel("hdel", "1", "2");
		t.hexists("hdel", "1");
		t.hset("hset", "1", "123");
		t.hget("hset", "1");
		t.hgetall("hgetall");
		t.hincrby("hincrby", "1", 3);
		t.hincrbyfloat("hincrby", "1", 0.5);
		t.hkeys("hgetall");
		t.hlen("hgetall");
		t.hmset("hmset", fieldvalues);
		t.hmget("hmset", "a", "b");
		t.hsetnx("hsetnx", "a", "a");
		List<Object> l = redis1.exec(t);
		
		Assert.assertEquals(12, l.size());
		Assert.assertEquals(new Long(2), l.get(0));
		Assert.assertFalse((Boolean)l.get(1));
		Assert.assertEquals(new Long(1), l.get(2));
		Assert.assertEquals("123", l.get(3));
		Assert.assertEquals(value, ((Map<String, String>) l.get(4)).get("1"));
		Assert.assertEquals(new Long(3), l.get(5));
		Assert.assertEquals(new Double(3.5), l.get(6));
		Assert.assertEquals("1", ((Set<String>) l.get(7)).iterator().next());
		Assert.assertEquals(new Long(1), l.get(8));
		Assert.assertEquals("OK", l.get(9));
		Assert.assertEquals("a", ((List<String>) l.get(10)).iterator().next());
		Assert.assertEquals(new Long(1), l.get(11));
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test transaction hashes. ", CaseCounter.incr(13)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testTransactionLists() {
		redis1.lpush("blpop", "123");
		redis1.lpush("brpop", "123");
		redis1.lpush("brpoplpush", "123");
		redis1.lpush("lindex", "a", "b", "c");
		
		RedisTransaction t = redis1.multi();
		t.blpop("blpop");
		t.brpop("brpop");
		t.brpoplpush("brpoplpush", "brpoplpush-dest", 1000);
		t.lindex("lindex", 1);
		t.linsertbefore("lindex", "b", "bb");
		t.linsertafter("lindex", "b", "ab");
		t.llen("lindex");
		t.lpush("lpush", "a", "b", "c");
		t.lpop("lpush");
		t.lpushx("lpush", "c");
		t.lrange("lpush", 0, -1);
		t.lrem("lpush", 0, "a");
		t.lset("lpush", 0, "d");
		t.ltrim("lindex", 0, 3);
		t.rpush("rpush", "a", "b", "c");
		t.rpop("rpush");
		t.rpushx("rpush", "c");
		t.rpoplpush("rpush", "rpush");
		List<Object> l = redis1.exec(t);
		
		Assert.assertEquals(18, l.size());
		Assert.assertEquals("123", ((Map<String, String>) l.get(0)).get("blpop"));
		Assert.assertEquals("123", ((Map<String, String>) l.get(1)).get("brpop"));
		Assert.assertEquals("123", l.get(2));
		Assert.assertEquals("b", l.get(3));
		Assert.assertEquals(new Long(4), l.get(4));
		Assert.assertEquals(new Long(5), l.get(5));
		Assert.assertEquals(new Long(5), l.get(6));
		Assert.assertEquals(new Long(3), l.get(7));
		Assert.assertEquals("c", l.get(8));
		Assert.assertEquals(new Long(3), l.get(9));
		Assert.assertEquals("c", ((List<String>) l.get(10)).iterator().next());
		Assert.assertEquals(new Long(1), l.get(11));
		Assert.assertEquals("OK", l.get(12));
		Assert.assertEquals("OK", l.get(13));
		Assert.assertEquals(new Long(3), l.get(14));
		Assert.assertEquals("c", l.get(15));
		Assert.assertEquals(new Long(3), l.get(16));
		Assert.assertEquals("c", l.get(17));
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test transaction lists. ", CaseCounter.incr(19)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testTransactionSets() {
		redis1.sadd("sdiff1", "a", "b", "c");
		redis1.sadd("sdiff2", "d", "b", "c");
		redis1.sadd("sinter1", "a", "b");
		redis1.sadd("sinter2", "b", "d");
		redis1.sadd("smove1", "a");
		redis1.sadd("spop", "a");
		redis1.sadd("srandmemeber1", "a");
		redis1.sadd("srandmemeber2", "b", "d", "e");
		redis1.sadd("sunion1", "a", "b");
		redis1.sadd("sunion2", "b", "c");
		
		RedisTransaction t = redis1.multi();
		t.sadd(key, "a", "b", "c", "c");
		t.scard(key);
		t.sdiff("sdiff1", "sdiff2");
		t.sdiffstore("sdiff", "sdiff1", "sdiff2");
		t.sinter("sinter1", "sinter2");
		t.sinterstore("sinter", "sinter1", "sinter2");
		t.sismember(key, "b");
		t.smembers(key);
		t.smove("smove1", "smove", "a");
		t.spop("spop");
		t.srandmember("srandmemeber1");
		t.srandmember("srandmemeber2", 2);
		t.srem(key, "a", "b");
		t.sunion("sunion1", "sunion2");
		t.sunionstore("sunion", "sunion1", "sunion2");
		List<Object> l = redis1.exec(t);

		Assert.assertEquals(15, l.size());
		Assert.assertEquals(new Long(3), l.get(0));
		Assert.assertEquals(new Long(3), l.get(1));
		Assert.assertEquals("a", ((Set<String>) l.get(2)).iterator().next());
		Assert.assertEquals(new Long(1), l.get(3));
		Assert.assertEquals("b", ((Set<String>) l.get(4)).iterator().next());
		Assert.assertEquals(new Long(1), l.get(5));
		Assert.assertTrue((Boolean) l.get(6));
		Assert.assertEquals(3, ((Set<String>) l.get(7)).size());
		Assert.assertEquals(new Long(1), l.get(8));
		Assert.assertEquals("a", l.get(9));
		Assert.assertEquals("a", l.get(10));
		Assert.assertEquals(2, ((List<String>) l.get(11)).size());
		Assert.assertEquals(new Long(2), l.get(12));
		Assert.assertEquals(3, ((Set<String>) l.get(13)).size());
		Assert.assertEquals(new Long(3), l.get(14));
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test transaction sets. ", CaseCounter.incr(16)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testTransactionSortedSets() {
		redis1.zadd("zrem", 1, "a");
		redis1.zadd("zrem", 2, "b");
		redis1.zadd("zrem", 3, "c");
		redis1.zadd("zrank", 1, "a");
		redis1.zadd("zrank", 2, "b");
		redis1.zadd("zrangebyscore", 1, "a");
		redis1.zadd("zrangebyscore", 2, "b");
		redis1.zadd("zrangebyscore", 3, "c");
		redis1.zadd("zrangebyscore", 4, "d");
		redis1.zadd("zrangebyscore", 5, "e");
		redis1.zadd("zcount", 1, "a");
		redis1.zadd("zcount", 2, "b");
		redis1.zadd("zcount", 3, "c");
		redis1.zadd("foo1", 1, "a");
		redis1.zadd("foo1", 1, "b");
		redis1.zadd("foo2", 2, "b");
		redis1.zadd("foo2", 1, "c");
		Map<String, Integer> wk = new HashMap<String, Integer>();
		wk.put("foo1", 5);
		wk.put("foo2", 2);
		redis1.zadd("zremrangebyrank", 1, "a");
		redis1.zadd("zremrangebyrank", 2, "b");
		redis1.zadd("zremrangebyrank", 3, "c");
		redis1.zadd("zremrangebyscore", 1, "a");
		redis1.zadd("zremrangebyscore", 2, "b");
		redis1.zadd("zremrangebyscore", 3, "c");
		redis1.zadd("zrevrange", 1, "a");
		redis1.zadd("zrevrange", 2, "b");
		redis1.zadd("zrevrangebyscore", 1, "a");
		redis1.zadd("zrevrangebyscore", 2, "b");
		redis1.zadd("zrevrangebyscore", 3, "c");
		redis1.zadd("zrevrangebyscore", 4, "d");
		redis1.zadd("zrevrangebyscore", 5, "e");
		redis1.zadd("zrevrank", 1, "a");
		redis1.zadd("zrevrank", 2, "b");
		redis1.zadd("zunionstore1", 1, "a");
		redis1.zadd("zunionstore1", 1, "b");
		redis1.zadd("zunionstore2", 2, "b");
		redis1.zadd("zunionstore2", 1, "c");
		Map<String, Integer> wk1 = new HashMap<String, Integer>();
		wk1.put("zunionstore1", 5);
		wk1.put("zunionstore2", 2);
		
		RedisTransaction t = redis1.multi();
		t.zadd(key, 1, value);
		t.zcard(key);
		t.zcount("zcount", 1, 3);
		t.zcount("zcount", "(1", "3");
		t.zincrby(key, 1, value);
		t.zscore(key, value);
		t.zinterstore("foo3", "foo1", "foo2");
		t.zinterstore("foo3", wk);
		t.zinterstoremax("foo3", "foo1", "foo2");
		t.zinterstoremax("foo3", wk);
		t.zinterstoremin("foo3", "foo1", "foo2");
		t.zinterstoremin("foo3", wk);
		t.zrange(key, 0, -1);
		t.zrangewithscores(key, 0, -1);
		t.zrangebyscore("zrangebyscore", 1, 2);
		t.zrangebyscore("zrangebyscore", "-inf", "2");
		t.zrangebyscore("zrangebyscore", 1, 5, 0, 2);
		t.zrangebyscore("zrangebyscore", "-inf", "5", 0, 2);
		t.zrangebyscorewithscores("zrangebyscore", 1, 2);
		t.zrangebyscorewithscores("zrangebyscore", "-inf", "2");
		t.zrangebyscorewithscores("zrangebyscore", 1, 5, 0, 2);
		t.zrangebyscorewithscores("zrangebyscore", "-inf", "5", 0, 2);
		t.zrank("zrank", "b");
		t.zrem("zrem", "a", "b");
		t.zremrangebyrank("zremrangebyrank", 0, 1);
		t.zremrangebyscore("zremrangebyscore", 0, 1.5);
		t.zremrangebyscore("zremrangebyscore", "-inf", "3");
		t.zrevrange("zrevrange", 0, -1);
		t.zrevrangewithscores("zrevrange", 0, -1);
		t.zrevrangebyscore("zrevrangebyscore", 2, 1);
		t.zrevrangebyscore("zrevrangebyscore", "2", "-inf");
		t.zrevrangebyscore("zrevrangebyscore", 5, 1, 0, 2);
		t.zrevrangebyscore("zrevrangebyscore", "5", "-inf", 0, 2);
		t.zrevrangebyscorewithscores("zrevrangebyscore", 2, 1);
		t.zrevrangebyscorewithscores("zrevrangebyscore", "2", "-inf");
		t.zrevrangebyscorewithscores("zrevrangebyscore", 5, 1, 0, 2);
		t.zrevrangebyscorewithscores("zrevrangebyscore", "5", "-inf", 0, 2);
		t.zrevrank("zrevrank", "b");
		t.zunionstore("zunionstore", "zunionstore1", "zunionstore2");
		t.zunionstore("zunionstore", wk1);
		t.zunionstoremax("zunionstore", "zunionstore1", "zunionstore2");
		t.zunionstoremax("zunionstore", wk1);
		t.zunionstoremin("zunionstore", "zunionstore1", "zunionstore2");
		t.zunionstoremin("zunionstore", wk1);
		List<Object> l = redis1.exec(t);
		
		Assert.assertEquals(44, l.size());
		Assert.assertEquals(new Long(1), l.get(0));
		Assert.assertEquals(new Long(1), l.get(1));
		Assert.assertEquals(new Long(3), l.get(2));
		Assert.assertEquals(new Long(2), l.get(3));
		Assert.assertEquals(new Double(2.0), l.get(4));
		Assert.assertEquals(new Double(2.0), l.get(5));
		Assert.assertEquals(new Long(1), l.get(6));
		Assert.assertEquals(new Long(1), l.get(7));
		Assert.assertEquals(new Long(1), l.get(8));
		Assert.assertEquals(new Long(1), l.get(9));
		Assert.assertEquals(new Long(1), l.get(10));
		Assert.assertEquals(new Long(1), l.get(11));
		Assert.assertEquals(value, ((Set<String>) l.get(12)).iterator().next());
		Assert.assertEquals(new Double(2.0), ((Map<String, Double>) l.get(13)).get(value));
		Assert.assertTrue(((Set<String>) l.get(14)).contains("a"));
		Assert.assertTrue(((Set<String>) l.get(14)).contains("b"));
		Assert.assertTrue(((Set<String>) l.get(15)).contains("a"));
		Assert.assertTrue(((Set<String>) l.get(15)).contains("b"));
		Assert.assertTrue(((Set<String>) l.get(16)).contains("a"));
		Assert.assertTrue(((Set<String>) l.get(16)).contains("b"));
		Assert.assertTrue(((Set<String>) l.get(17)).contains("a"));
		Assert.assertTrue(((Set<String>) l.get(17)).contains("b"));
		Assert.assertTrue(((Map<String, Double>) l.get(18)).get("a") == 1.0);
		Assert.assertTrue(((Map<String, Double>) l.get(18)).get("b") == 2.0);
		Assert.assertTrue(((Map<String, Double>) l.get(19)).get("a") == 1.0);
		Assert.assertTrue(((Map<String, Double>) l.get(19)).get("b") == 2.0);
		Assert.assertTrue(((Map<String, Double>) l.get(20)).get("a") == 1.0);
		Assert.assertTrue(((Map<String, Double>) l.get(20)).get("b") == 2.0);
		Assert.assertTrue(((Map<String, Double>) l.get(21)).get("a") == 1.0);
		Assert.assertTrue(((Map<String, Double>) l.get(21)).get("b") == 2.0);
		Assert.assertEquals(new Long(1), l.get(22));
		Assert.assertEquals(new Long(2), l.get(23));
		Assert.assertEquals(new Long(2), l.get(24));
		Assert.assertEquals(new Long(1), l.get(25));
		Assert.assertEquals(new Long(2), l.get(26));
		Assert.assertEquals("b", ((Set<String>) l.get(27)).iterator().next());
		Assert.assertTrue(((Map<String, Double>) l.get(28)).get("a") == 1.0);
		Assert.assertTrue(((Set<String>) l.get(29)).contains("a"));
		Assert.assertTrue(((Set<String>) l.get(29)).contains("b"));
		Assert.assertTrue(((Set<String>) l.get(30)).contains("a"));
		Assert.assertTrue(((Set<String>) l.get(30)).contains("b"));
		Assert.assertTrue(((Set<String>) l.get(31)).contains("d"));
		Assert.assertTrue(((Set<String>) l.get(31)).contains("e"));
		Assert.assertTrue(((Set<String>) l.get(32)).contains("d"));
		Assert.assertTrue(((Set<String>) l.get(32)).contains("e"));
		Assert.assertTrue(((Map<String, Double>) l.get(33)).get("a") == 1.0);
		Assert.assertTrue(((Map<String, Double>) l.get(33)).get("b") == 2.0);
		Assert.assertTrue(((Map<String, Double>) l.get(34)).get("a") == 1.0);
		Assert.assertTrue(((Map<String, Double>) l.get(34)).get("b") == 2.0);
		Assert.assertTrue(((Map<String, Double>) l.get(35)).get("d") == 4.0);
		Assert.assertTrue(((Map<String, Double>) l.get(35)).get("e") == 5.0);
		Assert.assertTrue(((Map<String, Double>) l.get(36)).get("d") == 4.0);
		Assert.assertTrue(((Map<String, Double>) l.get(36)).get("e") == 5.0);
		Assert.assertEquals(new Long(0), l.get(37));
		Assert.assertEquals(new Long(3), l.get(38));
		Assert.assertEquals(new Long(3), l.get(39));
		Assert.assertEquals(new Long(3), l.get(40));
		Assert.assertEquals(new Long(3), l.get(41));
		Assert.assertEquals(new Long(3), l.get(42));
		Assert.assertEquals(new Long(3), l.get(43));
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test transaction sorted sets. ", CaseCounter.incr(61)));
	}
	
	@Test
	public void testTransactionPublish() {
		RedisTransaction t = redis1.multi();
		t.publish("channel", "abc");
		List<Object> l = redis1.exec(t);
		Assert.assertEquals(1, l.size());
		Assert.assertEquals(new Long(0), l.get(0));
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test transaction publish. ", CaseCounter.incr(2)));
	}
	
}

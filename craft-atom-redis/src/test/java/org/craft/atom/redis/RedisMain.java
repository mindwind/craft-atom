package org.craft.atom.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	private static final String field = "1";
	private static final String value = "bar";
	private static final String script = "return redis.call('set','foo','bar')";
	private static Redis redis;
	private static Redis redis2;
	
	private static void init() {
		redis = RedisFactory.newRedis(HOST, PORT);
		redis2 = RedisFactory.newRedis(HOST, PORT2);
		after();
	}
	
	protected static void after() {
		redis.flushall();
		redis2.flushall();
	}
	
	public static void main(String[] args) throws Exception {
		init();
		
		System.out.println("-------------------------------------------------------------- Keys\n");
		del();
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
		
		
		System.out.println("\n------------------------------------------------------------ Strings\n");
		append();
		bitcount();
		setbit_getbit_bitop();
		decr_incr_by_float();
		get();
		getrange_setrange();
		getset();
		mget_mset();
		msetnx();
		psetex();
		set();
		strlen();
		
		
		System.out.println("\n------------------------------------------------------------ Hashes\n");
		hdel_hget_hset_hexists();
		hgetall();
		hincrby_float();
		hkeys();
		hlen();
		hmget_hmset();
		hsetnx();
		hvals();
		
		
		System.out.println("\n------------------------------------------------------------ Lists\n");
		blpop_lpush();
		blpop_timeout_rpush();
		brpop();
		brpop_timeout();
		brpoplpush_lpop();
		lindex_rpop();
		linsert();
		lrange_llen();
		lpushx_rpushx();
		lrem();
		lset();
		ltrim();
		
		
		System.out.println("\n------------------------------------------------------------ Sets\n");
		sadd_scard();
		sdiff_store();
		sinter_store_spop();
		sismember();
		smembers();
		smove();
		srandmember();
		srem();
		sunion_store();
		
		System.out.println("\n------------------------------------------------------------ Sorted Sets\n");
		zadd_zcard();
		zcount();
		zincrby_zscore();
		zrange();
		zrevrange();
		zinterstore();
		zunionstore();
		zrangebyscore();
		zrevrangebyscore();
		zrank();
		zrevrank();
		zrem();
		zremrangebyrank();
		zremrangebyscore();
		
		
		System.out.println("\n------------------------------------------------------------ Pub/Sub\n");
		subscribe_publish_unsubscribe();
	    psubscribe_publish_punsubscribe();
		
	    
	    System.out.println("\n------------------------------------------------------------ Transactions\n");
		multi_exec();
		watch_multi_exec();
		multi_discard();
		watch_unwatch_multi_exec();
		
		
		System.out.println("\n------------------------------------------------------------ Scripting\n");
		eval();
		evalsha_scriptload();
		script_exists_flush();
		scriptkill();
		
		
		System.out.println("\n------------------------------------------------------------ Connection\n");
		
		
		System.out.println("\n------------------------------------------------------------ Server\n");
	}
		
	
	// ~ ------------------------------------------------------------------------------------------------ Test Cases
	
	private static void scriptkill() {
		before("scriptkill");
		
		try {
			redis.scriptkill();
		} catch (RedisDataException e) {
			System.out.println("	" + e.getMessage());
			Assert.assertTrue(true);
		}
		
		after();
	}
	
	private static void script_exists_flush() {
		before("script_exists_flush");
		
		String sha1 = redis.scriptload(script);
		boolean b = redis.scriptexists(sha1);
		Assert.assertTrue(b);
		redis.scriptflush();
		b = redis.scriptexists(sha1);
		Assert.assertFalse(b);
		
		after();
	}
	
	private static void evalsha_scriptload() {
		before("evalsha_scriptload");
		
		String sha1 = redis.scriptload(script);
		redis.evalsha(sha1);
		String v = redis.get(key);
		Assert.assertEquals(value, v);
		
		redis.flushall();
		List<String> keys = new ArrayList<String>();
		keys.add("foo");
		sha1 = redis.scriptload("return redis.call('set',KEYS[1],'bar')");
		redis.evalsha(sha1, keys);
		
		redis.flushall();
		sha1 = redis.scriptload("return redis.call('set',KEYS[1],ARGV[1])");
		List<String> args = new ArrayList<String>();
		args.add("bar");
		redis.evalsha(sha1, keys, args);
		v = redis.get(key);
		Assert.assertEquals(value, v);
		
		after();
	}
	
	private static void eval() {
		before("eval");
		
		redis.eval(script);
		String v = redis.get(key);
		Assert.assertEquals(value, v);
		
		redis.flushall();
		List<String> keys = new ArrayList<String>();
		keys.add("foo");
		redis.eval("return redis.call('set',KEYS[1],'bar')", keys);
		v = redis.get(key);
		Assert.assertEquals(value, v);
		
		redis.flushall();
		List<String> args = new ArrayList<String>();
		args.add("bar");
		redis.eval("return redis.call('set',KEYS[1],ARGV[1])", keys, args);
		v = redis.get(key);
		Assert.assertEquals(value, v);
		
		after();
	}
	
	private static void zremrangebyscore() {
		before("zremrangebyscore");
		
		redis.zadd(key, 1, "a");
		redis.zadd(key, 2, "b");
		redis.zadd(key, 3, "c");
		redis.zremrangebyscore(key, 1, 2);
		Set<String> set = redis.zrange(key, 0, -1);
		Assert.assertEquals(set.iterator().next(), "c");
		
		redis.zadd(key, 1, "a");
		redis.zadd(key, 2, "b");
		redis.zremrangebyscore(key, "-inf", "2");
		set = redis.zrange(key, 0, -1);
		Assert.assertEquals(set.iterator().next(), "c");
		
		after();
	}
	
	private static void zremrangebyrank() {
		before("zremrangebyrank");
		
		redis.zadd(key, 1, "a");
		redis.zadd(key, 2, "b");
		redis.zadd(key, 3, "c");
		redis.zremrangebyrank(key, 0, 1);
		Set<String> set = redis.zrange(key, 0, -1);
		Assert.assertEquals(set.iterator().next(), "c");
		
		after();
	}
	
	private static void zrem() {
		before("zrem");
		
		redis.zadd(key, 1, "a");
		redis.zadd(key, 2, "b");
		redis.zadd(key, 3, "c");
		redis.zrem(key, "a", "b");
		Set<String> set = redis.zrange(key, 0, -1);
		Assert.assertEquals(set.iterator().next(), "c");
		
		after();
	}
	
	private static void zrevrank() {
		before("zrevrank");
		
		redis.zadd(key, 1, "a");
		redis.zadd(key, 2, "b");
		Long r = redis.zrevrank(key, "b");
		Assert.assertEquals(0, r.longValue());
		r = redis.zrank(key, "c");
		Assert.assertNull(r);
		
		after();
	}
	
	private static void zrevrange() {
		before("zrevrange");
		
		redis.zadd(key, 1, "a");
		redis.zadd(key, 2, "b");
		Set<String> set = redis.zrevrange(key, 0, -1);
		Iterator<String> it = set.iterator();
		Assert.assertTrue(it.next().equals("b"));
		Assert.assertTrue(it.next().equals("a"));
		Map<String, Double> map = redis.zrevrangewithscores(key, 0, -1);
		Iterator<Entry<String, Double>> it2 = map.entrySet().iterator();
		Assert.assertEquals(it2.next().getValue(), 2.0, 0.0);
		Assert.assertEquals(it2.next().getValue(), 1.0, 0.0);
		
		after();
	}
	
	private static void zrank() {
		before("zrank");
		
		redis.zadd(key, 1, "a");
		redis.zadd(key, 2, "b");
		Long r = redis.zrank(key, "b");
		Assert.assertEquals(1, r.longValue());
		r = redis.zrank(key, "c");
		Assert.assertNull(r);
		
		after();
	}
	
	private static void zrevrangebyscore() {
		before("zrevrangebyscore");
		
		redis.zadd(key, 1, "a");
		redis.zadd(key, 2, "b");
		redis.zadd(key, 3, "c");
		redis.zadd(key, 4, "d");
		redis.zadd(key, 5, "e");
		
		Set<String> set = redis.zrevrangebyscore(key, 2, 1);
		Assert.assertTrue(set.contains("a"));
		Assert.assertTrue(set.contains("b"));
		set = redis.zrevrangebyscore(key, "2", "-inf");
		Assert.assertTrue(set.contains("a"));
		Assert.assertTrue(set.contains("b"));
		
		set = redis.zrevrangebyscore(key, 5, 1, 0, 2);
		Assert.assertTrue(set.contains("d"));
		Assert.assertTrue(set.contains("e"));
		set = redis.zrevrangebyscore(key, "5", "-inf", 0, 2);
		Assert.assertTrue(set.contains("e"));
		Assert.assertTrue(set.contains("e"));
		
		Map<String, Double> map = redis.zrevrangebyscorewithscores(key, 2, 1);
		Assert.assertTrue(map.get("a") == 1.0);
		Assert.assertTrue(map.get("b") == 2.0);
		map = redis.zrevrangebyscorewithscores(key, "2", "-inf");
		Assert.assertTrue(map.get("a") == 1.0);
		Assert.assertTrue(map.get("b") == 2.0);
		map = redis.zrevrangebyscorewithscores(key, 5, 1, 0, 2);
		Assert.assertTrue(map.get("d") == 4.0);
		Assert.assertTrue(map.get("e") == 5.0);
		map = redis.zrevrangebyscorewithscores(key, "5", "-inf", 0, 2);
		Assert.assertTrue(map.get("d") == 4.0);
		Assert.assertTrue(map.get("e") == 5.0);
		
		after();
	}
	
	private static void zrangebyscore() {
		before("zrangebyscore");
		
		redis.zadd(key, 1, "a");
		redis.zadd(key, 2, "b");
		redis.zadd(key, 3, "c");
		redis.zadd(key, 4, "d");
		redis.zadd(key, 5, "e");
		
		Set<String> set = redis.zrangebyscore(key, 1, 2);
		Assert.assertTrue(set.contains("a"));
		Assert.assertTrue(set.contains("b"));
		set = redis.zrangebyscore(key, "-inf", "2");
		Assert.assertTrue(set.contains("a"));
		Assert.assertTrue(set.contains("b"));
		
		set = redis.zrangebyscore(key, 1, 5, 0, 2);
		Assert.assertTrue(set.contains("a"));
		Assert.assertTrue(set.contains("b"));
		set = redis.zrangebyscore(key, "-inf", "5", 0, 2);
		Assert.assertTrue(set.contains("a"));
		Assert.assertTrue(set.contains("b"));
		
		Map<String, Double> map = redis.zrangebyscorewithscores(key, 1, 2);
		Assert.assertTrue(map.get("a") == 1.0);
		Assert.assertTrue(map.get("b") == 2.0);
		map = redis.zrangebyscorewithscores(key, "-inf", "2");
		Assert.assertTrue(map.get("a") == 1.0);
		Assert.assertTrue(map.get("b") == 2.0);
		map = redis.zrangebyscorewithscores(key, 1, 5, 0, 2);
		Assert.assertTrue(map.get("a") == 1.0);
		Assert.assertTrue(map.get("b") == 2.0);
		map = redis.zrangebyscorewithscores(key, "-inf", "5", 0, 2);
		Assert.assertTrue(map.get("a") == 1.0);
		Assert.assertTrue(map.get("b") == 2.0);
		
		after();
	}
	
	private static void zunionstore() {
		before("zunionstore");
		
		redis.zadd("foo1", 1, "a");
		redis.zadd("foo1", 1, "b");
		redis.zadd("foo2", 2, "b");
		redis.zadd("foo2", 1, "c");
		Map<String, Integer> wk = new HashMap<String, Integer>();
		wk.put("foo1", 5);
		wk.put("foo2", 2);
		
		// zunionstore
		long l = redis.zunionstore(key, "foo1", "foo2");
		Assert.assertEquals(3, l);
		Map<String, Double> map = redis.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 3.0, 0.0);
		redis.zunionstore(key, wk);
		map = redis.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 9.0, 0.0);
		
		// zunionstoremax
		l = redis.zunionstoremax(key, "foo1", "foo2");
		Assert.assertEquals(3, l);
		map = redis.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 2.0, 0.0);
		redis.zunionstoremax(key, wk);
		map = redis.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 5.0, 0.0);
		
		// zunionstoremin
		l = redis.zunionstoremin(key, "foo1", "foo2");
		Assert.assertEquals(3, l);
		map = redis.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 1.0, 0.0);
		redis.zunionstoremin(key, wk);
		map = redis.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 4.0, 0.0);
		
		after();
	}
	
	private static void zinterstore() {
		before("zinterstore");
		
		redis.zadd("foo1", 1, "a");
		redis.zadd("foo1", 1, "b");
		redis.zadd("foo2", 2, "b");
		redis.zadd("foo2", 1, "c");
		Map<String, Integer> wk = new HashMap<String, Integer>();
		wk.put("foo1", 5);
		wk.put("foo2", 2);
		
		// zinterstore
		long l = redis.zinterstore(key, "foo1", "foo2");
		Assert.assertEquals(1, l);
		Map<String, Double> map = redis.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 3.0, 0.0);
		redis.zinterstore(key, wk);
		map = redis.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 9.0, 0.0);
		
		// zinterstoremax
		l = redis.zinterstoremax(key, "foo1", "foo2");
		Assert.assertEquals(1, l);
		map = redis.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 2.0, 0.0);
		redis.zinterstoremax(key, wk);
		map = redis.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 5.0, 0.0);
		
		// zinterstoremin
		l = redis.zinterstoremin(key, "foo1", "foo2");
		Assert.assertEquals(1, l);
		map = redis.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 1.0, 0.0);
		redis.zinterstoremin(key, wk);
		map = redis.zrangewithscores(key, 0, -1);
		Assert.assertEquals(map.get("b"), 4.0, 0.0);
		
		after();
	}
	
	private static void zrange() {
		before("zrange");
		
		redis.zadd(key, 1, "a");
		redis.zadd(key, 2, "b");
		Set<String> set = redis.zrange(key, 0, -1);
		Iterator<String> it = set.iterator();
		Assert.assertTrue(it.next().equals("a"));
		Assert.assertTrue(it.next().equals("b"));
		Map<String, Double> map = redis.zrangewithscores(key, 0, -1);
		Iterator<Entry<String, Double>> it2 = map.entrySet().iterator();
		Assert.assertEquals(it2.next().getValue(), 1.0, 0.0);
		Assert.assertEquals(it2.next().getValue(), 2.0, 0.0);
		
		after();
	}
	
	private static void zincrby_zscore() {
		before("zincrby_zscore");
		
		redis.zadd(key, 1, value);
		double score = redis.zincrby(key, 10.5, value);
		Assert.assertEquals(11.5, score, 0.0);
		score = redis.zscore(key, value);
		Assert.assertEquals(11.5, score, 0.0);
		
		after();
	}
	
	private static void zcount() {
		before("zcount");
		
		redis.zadd(key, 10, "a");
		redis.zadd(key, 20, "b");
		redis.zadd(key, 30, "c");
		long c = redis.zcount(key, 20, 30);
		Assert.assertEquals(2, c);
		c = redis.zcount(key, "20", "inf");
		Assert.assertEquals(2, c);
		
		after();
	}
	
	private static void zadd_zcard() {
		before("zadd_zcard");
		
		redis.zadd(key, 50, value);
		long r = redis.zcard(key);
		Assert.assertEquals(1, r);
		
		after();
	}
	
	private static void sunion_store() {
		before("sunion_store");
		
		redis.sadd("foo1", "a", "c");
		redis.sadd("foo2", "c", "d", "e");
		Set<String> u = redis.sunion("foo1", "foo2");
		Assert.assertEquals(4, u.size());
		
		long len = redis.sunionstore(key, "foo1", "foo2");
		Assert.assertEquals(4, len);
		
		after();
	}
	
	private static void srem() {
		before("srem");
		
		redis.sadd(key, "a", "b");
		long r = redis.srem(key, "a", "c");
		Assert.assertEquals(1, r);
		Assert.assertFalse(redis.sismember(key, "a"));
		
		after();
	}
	
	private static void srandmember() {
		before("srandmember");
		
		redis.sadd(key, value);
		String v = redis.srandmember(key);
		Assert.assertEquals(value, v);
		List<String> l = redis.srandmember(key, 5);
		Assert.assertEquals(1, l.size());
		redis.sadd(key, "a");
		l = redis.srandmember(key, 5);
		Assert.assertEquals(2, l.size());
		
		after();
	}
	
	private static void smove() {
		before("smove");
		
		redis.sadd(key, "a");
		redis.sadd(key, "b");
		redis.smove(key, "foo1", "a");
		Assert.assertFalse(redis.sismember(key, "a"));
		Assert.assertTrue(redis.sismember("foo1", "a"));
		
		after();
	}
	
	private static void smembers() {
		before("smembers");
		
		redis.sadd(key, value);
		Set<String> set = redis.smembers(key);
		Assert.assertEquals(value, set.iterator().next());
		
		after();
	}
	
	private static void sismember() {
		before("sismember");
		
		redis.sadd(key, value);
		boolean b = redis.sismember(key, value);
		Assert.assertTrue(b);
		
		after();
	}
	
	private static void sinter_store_spop() {
		before("sinter_store_spop");
		
		redis.sadd("foo1", "a", "b", "c");
		redis.sadd("foo2", "c", "d", "e");
		Set<String> set = redis.sinter("foo1", "foo2");
		Assert.assertEquals("c", set.iterator().next());
		
		long len = redis.sinterstore(key, "foo1", "foo2");
		Assert.assertEquals(1, len);
		String v = redis.spop(key);
		Assert.assertEquals("c", v);
		
		after();
	}
	
	private static void sdiff_store() {
		before("sdiff_store");
		
		redis.sadd("foo1", "a", "c");
		redis.sadd("foo2", "c", "d", "e");
		Set<String> diff = redis.sdiff("foo1", "foo2");
		Assert.assertEquals(1, diff.size());
		Assert.assertEquals("a", diff.iterator().next());
		
		long len = redis.sdiffstore(key, "foo1", "foo2");
		Assert.assertEquals(1, len);
		
		after();
	}
	
	private static void sadd_scard() {
		before("sadd_scard");
		
		long c = redis.sadd(key, "a", "b", "c");
		Assert.assertEquals(3, c);
		c = redis.sadd(key, "a", "b", "c");
		Assert.assertEquals(0, c);
		c = redis.scard(key);
		Assert.assertEquals(3, c);
	}
	
	private static void ltrim() {
		before("ltrim");
		
		redis.rpush(key, "1", "2", "3", "4", "5");
		redis.ltrim(key, 0, 10);
		Assert.assertEquals(5, redis.llen(key).longValue());
		redis.ltrim(key, 0, 3);
		Assert.assertEquals(4, redis.llen(key).longValue());
		redis.ltrim(key, 4, 3);
		
		after();
	}
	
	private static void lset() {
		before("lset");
		
		redis.rpush(key, "1", "2", "3");
		redis.lset(key, 0, "4");
		Assert.assertTrue(Arrays.equals(new String[]{ "4", "2", "3" }, redis.lrange(key, 0, -1).toArray(new String[] {})));
		
		after();
	}
	
	private static void lrem() {
		before("lrem");
		
		redis.rpush(key, "1", "2", "1", "2", "3");
		long r = redis.lrem(key, 1, "1");
		Assert.assertEquals(1, r);
		Assert.assertTrue(Arrays.equals(new String[]{ "2", "1", "2", "3" }, redis.lrange(key, 0, -1).toArray(new String[] {})));
		redis.lrem(key, -1, "3");
		Assert.assertTrue(Arrays.equals(new String[]{ "2", "1", "2" }, redis.lrange(key, 0, -1).toArray(new String[] {})));
		redis.lrem(key, 0, "2");
		Assert.assertTrue(Arrays.equals(new String[]{ "1" }, redis.lrange(key, 0, -1).toArray(new String[] {})));
		
		after();
	}
	
	private static void lpushx_rpushx() {
		before("lpushx_rpushx");
		
		long len = redis.lpushx(key, value);
		len = redis.rpushx(key, value);
		Assert.assertEquals(0, len);
		redis.lpush(key, value);;
		len = redis.lpushx(key, value);
		len = redis.rpushx(key, value);
		Assert.assertEquals(3, len);
		List<String> l = redis.lrange(key, 0, -1);
		for (String v : l) {
			Assert.assertEquals(value, v);
		}
		
		after();
	}
	
	private static void linsert() {
		before("linsert");
		
		long len = redis.lpush(key, value);
		len = redis.linsertbefore(key, value, "before-bar");
		len = redis.linsertafter(key, value, "after-bar");
		Assert.assertEquals(3, len);
		List<String> l = redis.lrange(key, 0, -1);
		Assert.assertEquals("before-bar", l.get(0));
		Assert.assertEquals("after-bar", l.get(2));
		
		len = redis.linsertbefore(key, "aaa", "aaa");
		Assert.assertEquals(-1, len);
		
		after();
	}
	
	private static void lindex_rpop() {
		before("lindex_rpop");
		
		redis.rpush(key, value, "a", "b", "c");
		String v = redis.lindex(key, 2);
		Assert.assertEquals("b", v);
		v = redis.rpop(key);
		Assert.assertEquals("c", v);
		
		after();
	}
	
	private static void brpoplpush_lpop() {
		before("brpoplpush_lpop");
		
		String v = redis.brpoplpush(key, "foo1", 1);
		Assert.assertNull(v);
		
		redis.rpush(key, value);
		v = redis.brpoplpush(key, "foo1", 1);
		Assert.assertEquals(value, v);
		v = redis.lpop("foo1");
		Assert.assertEquals(value, v);
		
		after();
	}
	
	private static void brpop_timeout() throws InterruptedException {
		before("blpop_timeout");
		
		Map<String, String> map = redis.brpop(1, key);
		Assert.assertEquals(0, map.size());
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Map<String, String> hash = redis.blpop(5, "foo", "foo1");
				Assert.assertEquals("bar1", hash.get("foo1"));
			}
		});
		t.start();
		redis.rpush("foo1", "bar1");
		t.join();
		
		after();
	}
	
	private static void brpop() throws InterruptedException {
		before("brpop");
		
		final Lock lock = new ReentrantLock();
		final Condition c = lock.newCondition();
		
		Thread t1 = new Thread(new Runnable() {	
			@Override
			public void run() {
				String v = redis.brpop(key);
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
	
	private static void blpop_timeout_rpush() {
		before("blpop_timeout_rpush");
		
		Map<String, String> map = redis.blpop(1, key);
		Assert.assertEquals(0, map.size());
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Map<String, String> hash = redis.blpop(5, "foo", "foo1");
				Assert.assertEquals("bar1", hash.get("foo1"));
			}
		});
		t.start();
		long len = redis.rpush("foo1", "bar1");
		Assert.assertEquals(1, len);
		
		after();
	}
	
	private static void blpop_lpush() throws InterruptedException {
		before("blpop_lpush");
		
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
				long len = redis.lpush(key, "3");
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
		
		after();
	}
	
	private static void lrange_llen() {
		before("lrange_llen");
		
		long len = redis.lpush(key, "1", "2", "3");
		Assert.assertEquals(3, len);
		len = redis.llen(key);
		Assert.assertEquals(3, len);
		List<String> l = redis.lrange(key, 0, -1);
		Assert.assertEquals("1", l.get(2));
		
		after();
	}
	
	private static void hvals() {
		before("hvals");
		
		List<String> l = redis.hvals(key);
		Assert.assertEquals(0, l.size());
		redis.hset(key, field, value);
		l = redis.hvals(key);
		Assert.assertEquals(1, l.size());
		
		after();
	}
	
	private static void hsetnx() {
		before("hsetnx");
		
		long c = redis.hsetnx(key, field, value);
		Assert.assertEquals(1, c);
		c = redis.hsetnx(key, field, value);
		Assert.assertEquals(0, c);
		String v = redis.hget(key, field);
		Assert.assertEquals(value, v);
		
		after();
	}
	
	private static void hmget_hmset() {
		before("hmget_hmset");
		
		Map<String, String> hash = new HashMap<String, String>();
		hash.put("1", "1");
		hash.put("2", "2");
		redis.hmset(key, hash);
		List<String> l = redis.hmget(key, "1", "2");
		Assert.assertEquals(2, l.size());
		Assert.assertEquals("1", l.get(0));
		Assert.assertEquals("2", l.get(1));
		
		after();
	}
	
	private static void hlen() {
		before("hlen");
		
		redis.hset(key, field, value);
		long len = redis.hlen(key);
		Assert.assertEquals(1, len);
		
		after();
	}
	
	private static void hkeys() {
		before("hkeys");
		
		redis.hset(key, field, value);
		Set<String> set = redis.hkeys(key);
		Assert.assertEquals(1, set.size());
		for (String f : set) {
			Assert.assertEquals(field, f);
		}
		
		after();
	}
	
	private static void hincrby_float() {
		before("hincrby_float");
		
		long c = redis.hincrby(key, field, 10);
		Assert.assertEquals(10, c);
		double d = redis.hincrbyfloat(key, field, 6.66);
		Assert.assertEquals(16.66, d, 0.00);
		
		after();
	}
	
	private static void hgetall() {
		before("hgetall");
		
		redis.hset(key, field, value);
		redis.hset(key, "2", "bar1");
		Map<String, String> map = redis.hgetall(key);
		Assert.assertEquals(2, map.size());
		Assert.assertEquals("bar1", map.get("2"));
		
		after();
	}
	
	private static void hdel_hget_hset_hexists() {
		before("hdel_hget_hset_hexists");
		
		long c = redis.hset(key, field, value);
		Assert.assertEquals(1, c);
		c = redis.hset(key, field, value);
		Assert.assertEquals(0, c);
		boolean b = redis.hexists(key, field);
		Assert.assertTrue(b);
		String v = redis.hget(key, field);
		Assert.assertEquals(value, v);
		
		after();
	}
	
	private static void strlen() {
		before("strlen");
		
		long len = redis.strlen(key);
		Assert.assertTrue(len == 0);
		redis.set(key, value);
		len = redis.strlen(key);
		Assert.assertTrue(len == 3);
		
		after();
	}
	
	private static void set() {
		before("set");
		
		// setxx
		String r = redis.setxx(key, value);
		Assert.assertNull(r);
		redis.set(key, value);
		r = redis.setxx(key, value);
		Assert.assertNotNull(r);
		
		// setex
		redis.del(key);
		redis.setex(key, 100, value);
		Assert.assertEquals(value, redis.get(key));
		long c = redis.ttl(key);
		Assert.assertTrue(c > 0 && c <= 100);
		
		// setxxex
		redis.del(key);
		r = redis.setxxex(key, value, 100);
		Assert.assertNull(r);
		redis.set(key, value);
		r = redis.setxxex(key, value, 100);
		Assert.assertNotNull(r);
		Assert.assertEquals(value, redis.get(key));
		c = redis.ttl(key);
		Assert.assertTrue(c > 0 && c <= 100);
		
		// setxxpx
		
		redis.del(key);
		r = redis.setxxpx(key, value, 100000);
		Assert.assertNull(r);
		redis.set(key, value);
		r = redis.setxxpx(key, value, 100000);
		Assert.assertNotNull(r);
		Assert.assertEquals(value, redis.get(key));
		c = redis.ttl(key);
		Assert.assertTrue(c > 0 && c <= 100000);
		
		// setnx
		redis.set(key, value);
		long b = redis.setnx(key, value);
		Assert.assertEquals(0, b);
		redis.del(key);
		b = redis.setnx(key, value);
		Assert.assertEquals(1, b);
		Assert.assertEquals(value, redis.get(key));
		
		// setnxex
		redis.set(key, value);
		r = redis.setnxex(key, value, 100);
		Assert.assertNull(r);
		redis.del(key);
		r = redis.setnxex(key, value, 100);
		Assert.assertNotNull(r);
		Assert.assertEquals(value, redis.get(key));
		c = redis.ttl(key);
		Assert.assertTrue(c > 0 && c <= 100);
		
		// setnxpx
		redis.set(key, value);
		r = redis.setnxpx(key, value, 100000);
		Assert.assertNull(r);
		redis.del(key);
		r = redis.setnxpx(key, value, 100000);
		Assert.assertNotNull(r);
		Assert.assertEquals(value, redis.get(key));
		c = redis.ttl(key);
		Assert.assertTrue(c > 0 && c <= 100000);
		
		after();
	}
	
	private static void psetex() {
		before("psetex");
		
		redis.psetex(key, 2000, value);
		String r = redis.get(key);
		long t = redis.pttl(key);
		Assert.assertEquals(value, r);
		Assert.assertTrue(t > 0 && t <= 2000);
		
		after();
	}
	
	private static void msetnx() {
		before("msetnx");
		
		redis.set(key, value);
		long r = redis.msetnx(key, value, "foo1", "bar1");
		Assert.assertEquals(0, r);
		
		redis.del(key);
		r = redis.msetnx(key, value, "foo1", "bar1");
		Assert.assertEquals(1, r);
		List<String> l = redis.mget(key, "foo1");
		Assert.assertEquals(2, l.size());
		
		after();
	}
	
	private static void mget_mset() {
		before("mget_mset");
		
		redis.mset(key, value, "foo1", "bar1");
		List<String> l = redis.mget(key, "foo1");
		Assert.assertEquals(2, l.size());
		Assert.assertEquals("bar", l.get(0));
		
		after();
	}
	
	private static void getset() {
		before("getset");
		
		String r = redis.getset(key, value);
		Assert.assertNull(r);
		r = redis.get(key);
		Assert.assertEquals(value, r);
		
		after();
	}
	
	private static void getrange_setrange() {
		before("getrange_setrange");
		
		redis.set(key, value);
		String r = redis.getrange(key, 0, 10);
		Assert.assertEquals(value, r);
		
		r = redis.getrange(key, -1, -3);
		Assert.assertEquals("", r);
		
		redis.set(key, value);
		redis.setrange(key, 1, "bbccc");
		r = redis.get(key);
		Assert.assertEquals("bbbccc", r);
		
		after();
	}
	
	private static void get() {
		before("get");
		
		String r = redis.get(key);
		Assert.assertNull(r);
		
		redis.set(key, value);
		r = redis.get(key);
		Assert.assertEquals(value, r);
		
		after();
	}
	
	private static void decr_incr_by_float() {
		before("decr_incr_by_float");
		
		long r = redis.incr(key);
		Assert.assertEquals(1, r);
		
		r = redis.decr(key);
		Assert.assertEquals(0, r);
		
		r = redis.incrby(key, 10);
		Assert.assertEquals(10, r);
		
		r = redis.decrby(key, 5);
		Assert.assertEquals(5, r);
		
		double d = redis.incrbyfloat(key, 1.55);
		Assert.assertEquals(6.55, d, 0.0);
		
		after();
	}
	
	private static void setbit_getbit_bitop() {
		before("setbit_getbit_bitop");
		
		boolean b = redis.setbit(key, 1, true);
		Assert.assertFalse(b);
		b = redis.getbit(key, 1);
		Assert.assertTrue(b);
		
		redis.bitnot("foo1", key);
		b = redis.getbit("foo1", 1);
		Assert.assertFalse(b);
		
		redis.bitand("foo2", key, "foo1");
		b = redis.getbit("foo2", 1);
		Assert.assertFalse(b);
		
		redis.bitor("foo3", key, "foo2");
		b = redis.getbit("foo3", 1);
		Assert.assertTrue(b);
		
		redis.bitxor("foo4", key, "foo2");
		b = redis.getbit("foo4", 1);
		Assert.assertTrue(b);
		
		after();
	}
	
	private static void bitcount() {
		before("bitcount");
		
		redis.set(key, "foobar");
		long c = redis.bitcount(key);
		Assert.assertEquals(26, c);
		c = redis.bitcount(key, 1, 1);
		Assert.assertEquals(6, c);
		
		after();
	}
	
	private static void append() {
		before("append");
		
		redis.append(key, value);
		Assert.assertEquals("bar", value);
		redis.append(key, "bar");
		Assert.assertEquals("barbar", redis.get(key));
		
		after();
	}
	
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
	
	private static void del() {
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

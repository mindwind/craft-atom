package io.craft.atom.redis;

import io.craft.atom.redis.api.Redis;
import io.craft.atom.redis.api.RedisDataException;
import io.craft.atom.redis.api.RedisFactory;
import io.craft.atom.redis.api.RedisPoolConfig;
import io.craft.atom.test.CaseCounter;

import java.util.NoSuchElementException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * This test is for issue #1, {@link https://github.com/mindwind/craft-atom/issues/1 }
 * 
 * @author mindwind
 * @version 1.0, Jan 21, 2014
 */
public class TestJedisLeak extends AbstractRedisTests {
	
	
	private Redis redis;
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public TestJedisLeak() {
		RedisPoolConfig cfg = new RedisPoolConfig();
		cfg.setMaxTotal(5);
		cfg.setMaxIdle(5);
		cfg.setMinIdle(0);
		cfg.setBlockWhenExhausted(false);
		redis = RedisFactory.newRedis(HOST, PORT1, 2000, cfg);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test jedis leak. ", CaseCounter.incr(1)));
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------

	
	@Test
	public void test() {
		String key = "test.jedis.leak";
		redis.hset(key, "1", "1");
		for (int i = 0; i < 10; i++) {
			try {
				redis.get(key);
			} catch (RedisDataException e) {
				
			} catch (NoSuchElementException e) {
				Assert.fail();
			}
		}
	}
	
}

package org.craft.atom.redis;

import java.util.NoSuchElementException;

import junit.framework.Assert;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.craft.atom.redis.api.Redis;
import org.craft.atom.redis.api.RedisDataException;
import org.craft.atom.redis.api.RedisFactory;
import org.junit.Test;

import redis.clients.jedis.JedisPoolConfig;

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
		JedisPoolConfig jpc = new JedisPoolConfig();
		jpc.setMaxActive(5);
		jpc.setMaxIdle(5);
		jpc.setMinIdle(0);
		jpc.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_FAIL);
		redis = RedisFactory.newRedis(HOST, PORT1, 2000, jpc);
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

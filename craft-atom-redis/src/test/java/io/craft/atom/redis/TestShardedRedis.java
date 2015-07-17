package io.craft.atom.redis;

import io.craft.atom.redis.api.Redis;
import io.craft.atom.redis.api.RedisFactory;
import io.craft.atom.redis.api.ShardedRedis;
import io.craft.atom.test.CaseCounter;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Test for {@link ShardedRedis}
 * 
 * @author mindwind
 * @version 1.0, Dec 3, 2013
 */
public class TestShardedRedis extends AbstractRedisTests {
	
	
	private ShardedRedis     shardedRedis;
	private ShardedJedisPool shardedPool ;
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public TestShardedRedis() {
		super();
		init();
	}
	
	private void init() {
		redis1.slaveofnoone();
		redis2.slaveofnoone();
		redis3.slaveofnoone();
		List<Redis> shards = new ArrayList<Redis>(3);
		shards.add(redis1);
		shards.add(redis2);
		shards.add(redis3);
		shardedRedis = RedisFactory.newShardedRedis(shards);
		
		List<JedisShardInfo> shardinfos = new ArrayList<JedisShardInfo>();
		shardinfos.add(new JedisShardInfo(HOST, PORT1));
		shardinfos.add(new JedisShardInfo(HOST, PORT2));
		shardinfos.add(new JedisShardInfo(HOST, PORT3));
		shardedPool = new ShardedJedisPool(new JedisPoolConfig(), shardinfos);
	}	
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	@Test
	public void testHashSharded() {
		for (int i = 0; i < 10000; i++) {
			String key = "test-" + i;
			String value = "value-" + i;
			shardedRedis.set(key, key, value);
			ShardedJedis sj = shardedPool.getResource();
			try {
				String v = sj.get(key);
				Assert.assertEquals(value, v);
			} finally {
				sj.close();
			}
		}
		
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test hash sharded. ", CaseCounter.incr(1)));
	}
}

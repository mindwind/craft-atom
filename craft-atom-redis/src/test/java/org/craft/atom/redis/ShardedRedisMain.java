package org.craft.atom.redis;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.craft.atom.redis.api.Redis;
import org.craft.atom.redis.api.RedisFactory;
import org.craft.atom.redis.api.ShardedRedis;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @author mindwind
 * @version 1.0, Jun 28, 2013
 */
public class ShardedRedisMain extends TestMain {
	
	private static final String HOST = "127.0.0.1";
	private static final int PORT0 = 6379;
	private static final int PORT1 = 6380;
	private static final int PORT2 = 6381;
	private static ShardedRedis r;
	private static ShardedJedisPool shardedPool;
	
	private static void init() {
		Redis s1 = RedisFactory.newRedis(HOST, PORT0);
		Redis s2 = RedisFactory.newRedis(HOST, PORT1);
		Redis s3 = RedisFactory.newRedis(HOST, PORT2);
		s1.slaveofnoone();
		s2.slaveofnoone();
		s3.slaveofnoone();
		List<Redis> shards = new ArrayList<Redis>(3);
		shards.add(s1);
		shards.add(s2);
		shards.add(s3);
		r = RedisFactory.newShardedRedis(shards);
		
		List<JedisShardInfo> shardinfos = new ArrayList<JedisShardInfo>();
		shardinfos.add(new JedisShardInfo(HOST, PORT0));
		shardinfos.add(new JedisShardInfo(HOST, PORT1));
		shardinfos.add(new JedisShardInfo(HOST, PORT2));
		shardedPool = new ShardedJedisPool(new JedisPoolConfig(), shardinfos);
	}
	
	protected static void after() {
		List<Redis> shards = r.shards();
		for (Redis s : shards) {
			s.flushall();
		}
	}
	
	public static void main(String[] args) throws Exception {
		init();
		
		testHashSharded();
	}
	
	private static void testHashSharded() {
		before("testHashSharded");
		
		for (int i = 0; i < 10000; i++) {
			String key = "test-" + i;
			String value = "value-" + i;
			r.set(key, key, value);
			ShardedJedis sj = shardedPool.getResource();
			try {
				String v = sj.get(key);
				Assert.assertEquals(value, v);
			} finally {
				shardedPool.returnResource(sj);
			}
		}
		
		after();
	}
	
}

package org.craft.atom.redis.api;

import java.util.ArrayList;
import java.util.List;

import org.craft.atom.redis.DefaultShardedRedis;


/**
 * Builder for {@link ShardedRedis}
 * 
 * @author mindwind
 * @version 1.0, Apr 19, 2014
 */
public class ShardedRedisBuilder extends AbstractRedisBuilder<ShardedRedis> {
	
	
	private String shardstring;

	
	/**
	 * @param shardstring format string e.g. localhost:6379,localhost:6380,localhost:6381
	 */
	public ShardedRedisBuilder(String shardstring) {
		this.shardstring = shardstring;
	}
	
	
	static String[] parse(String shardstring) {
		return shardstring.trim().split(",");
	}
	
	@Override
	public ShardedRedis build() {
		RedisPoolConfig  poolConfig = new RedisPoolConfig();
		set(poolConfig);
		String[] hostports = parse(shardstring);
		
		List<Redis> shards = new ArrayList<Redis>(hostports.length);
		for (String hostport : hostports) {
			Redis redis = new RedisBuilder(hostport).copy(this).build();
			shards.add(redis);
		}
		return new DefaultShardedRedis(shards);
	}

}

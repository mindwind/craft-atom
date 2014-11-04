package io.craft.atom.redis;

import io.craft.atom.redis.api.Redis;
import io.craft.atom.redis.api.ShardedRedis;
import io.craft.atom.redis.spi.Sharded;

import java.util.List;

import lombok.ToString;


/**
 * @author mindwind
 * @version 1.0, Jun 25, 2013
 */
@ToString(callSuper = true)
public class DefaultShardedRedis extends AbstractShardedRedis<Redis> implements ShardedRedis {
	
	
	public DefaultShardedRedis(List<Redis> shards) {
		this.sharded = new RedisMurmurHashSharded(shards);
	}
	
	public DefaultShardedRedis(Sharded<Redis> sharded) {
		this.sharded = sharded;
	}
	
	
	// ~ ---------------------------------------------------------------------------------------------------------
	
	
	@Override
	public List<Redis> shards() {
		return sharded.shards();
	}

	@Override
	public Redis shard(String shardkey) {
		return sharded.shard(shardkey);
	}

}

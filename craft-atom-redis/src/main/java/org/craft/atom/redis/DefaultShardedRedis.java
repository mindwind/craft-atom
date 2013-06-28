package org.craft.atom.redis;

import java.util.List;

import org.craft.atom.redis.api.Redis;
import org.craft.atom.redis.api.ShardedRedis;
import org.craft.atom.redis.spi.Sharded;

/**
 * @author mindwind
 * @version 1.0, Jun 25, 2013
 */
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

	
	// ~ ---------------------------------------------------------------------------------------------------------
	

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("{")
		   .append("class: ").append(getClass().getName()).append(", ")
		   .append("shards: ").append(shards())
		   .append("}");
		return buf.toString();
	}
	

}

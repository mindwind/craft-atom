package org.craft.atom.redis;

import java.util.List;

import lombok.ToString;

import org.craft.atom.redis.api.Redis;
import org.craft.atom.redis.spi.Sharded;

/**
 * @author mindwind
 * @version 1.0, Jun 25, 2013
 */
@ToString(callSuper = true)
public class RedisMurmurHashSharded extends AbstractMurmurHashSharded<Redis> implements Sharded<Redis> {

	public RedisMurmurHashSharded(List<Redis> shards) {
		super(shards);
	}

	@Override
	public Redis shard(String shardkey) {
		return super.shard(shardkey);
	}

	@Override
	public List<Redis> shards() {
		return super.shards();
	}

}

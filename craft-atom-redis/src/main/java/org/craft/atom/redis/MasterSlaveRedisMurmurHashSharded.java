package org.craft.atom.redis;

import java.util.List;

import org.craft.atom.redis.api.MasterSlaveRedis;
import org.craft.atom.redis.spi.Sharded;

/**
 * @author mindwind
 * @version 1.0, Jun 25, 2013
 */
public class MasterSlaveRedisMurmurHashSharded extends AbstractMurmurHashSharded<MasterSlaveRedis> implements Sharded<MasterSlaveRedis> {

	public MasterSlaveRedisMurmurHashSharded(List<MasterSlaveRedis> shards) {
		super(shards);
	}

	@Override
	public MasterSlaveRedis shard(String shardkey) {
		return super.shard(shardkey);
	}

	@Override
	public List<MasterSlaveRedis> shards() {
		return super.shards();
	}

}

package org.craft.atom.redis;

import java.util.List;

import lombok.ToString;

import org.craft.atom.redis.api.MasterSlaveRedis;
import org.craft.atom.redis.api.MasterSlaveShardedRedis;
import org.craft.atom.redis.spi.Sharded;

/**
 * @author mindwind
 * @version 1.0, Jun 26, 2013
 */
@ToString(callSuper = true)
public class DefaultMasterSlaveShardedRedis extends AbstractShardedRedis<MasterSlaveRedis> implements MasterSlaveShardedRedis {


	public DefaultMasterSlaveShardedRedis(List<MasterSlaveRedis> shards) {
		this.sharded = new MasterSlaveRedisMurmurHashSharded(shards);
	}

	public DefaultMasterSlaveShardedRedis(Sharded<MasterSlaveRedis> sharded) {
		this.sharded = sharded;
	}
		
		
	// ~ ---------------------------------------------------------------------------------------------------------
	
	
	@Override
	public List<MasterSlaveRedis> shards() {
		return sharded.shards();
	}
	
}

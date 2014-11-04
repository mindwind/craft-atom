package io.craft.atom.redis;

import io.craft.atom.redis.api.MasterSlaveRedis;
import io.craft.atom.redis.api.MasterSlaveShardedRedis;
import io.craft.atom.redis.spi.Sharded;

import java.util.List;

import lombok.ToString;


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
	
	@Override
	public MasterSlaveRedis shard(String shardkey) {
		return sharded.shard(shardkey);
	}

	@Override
	public void enableReadSlave() {
		for (MasterSlaveRedis msr : shards()) {
			msr.enableReadSlave();
		}
	}
	
	@Override
	public void disableReadSlave() {
		for (MasterSlaveRedis msr : shards()) {
			msr.disableReadSlave();
		}
	}
	
}

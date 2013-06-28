package org.craft.atom.redis.api;

import java.util.List;

/**
 * The master slave sharded redis client.
 * 
 * @author mindwind
 * @version 1.0, Jun 25, 2013
 */
public interface MasterSlaveShardedRedis extends ShardedRedisCommand {
	
	/**
	 * Return all shards
	 * 
	 * @return
	 */
	List<MasterSlaveRedis> shards();
	
}

package org.craft.atom.redis.spi;

import java.util.List;

import org.craft.atom.redis.api.Redis;

/**
 * Implements this interface to provide custom sharded algorithm.
 * 
 * @author mindwind
 * @version 1.0, Jun 25, 2013
 */
public interface Sharded {

	/**
	 * Get redis shard.
	 * 
	 * @param shardkey
	 * @return
	 */
	Redis getShard(String shardkey);
	
	/**
	 * Get all redis shards.
	 * 
	 * @return
	 */
	List<Redis> getAllShards();
	
}

package org.craft.atom.redis.spi;

import java.util.List;

import org.craft.atom.redis.api.RedisCommand;

/**
 * Implements this interface to provide custom sharded algorithm.
 * 
 * @author mindwind
 * @version 1.0, Jun 25, 2013
 */
public interface Sharded<R extends RedisCommand> {

	/**
	 * Return redis shard by shardkey
	 * 
	 * @param shardkey
	 * @return
	 */
	R shard(String shardkey);
	
	/**
	 * Get all redis shards.
	 * 
	 * @return
	 */
	List<R> shards();
	
}

package io.craft.atom.redis.spi;

import io.craft.atom.redis.api.RedisCommand;

import java.util.List;


/**
 * Implements this interface to provide custom sharded algorithm.
 * 
 * @author mindwind
 * @version 1.0, Jun 25, 2013
 */
public interface Sharded<R extends RedisCommand> {

	/**
	 * @param shardkey
	 * @return redis shard by shardkey
	 */
	R shard(String shardkey);
	
	/**
	 * @return all redis shards.
	 */
	List<R> shards();
	
}

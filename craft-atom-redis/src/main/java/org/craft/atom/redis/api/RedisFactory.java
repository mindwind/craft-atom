package org.craft.atom.redis.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.craft.atom.redis.DefaultMasterSlaveRedis;
import org.craft.atom.redis.DefaultMasterSlaveShardedRedis;
import org.craft.atom.redis.DefaultRedis;
import org.craft.atom.redis.DefaultShardedRedis;
import org.craft.atom.redis.spi.Sharded;

/**
 * Redis Factory
 * 
 * @author mindwind
 * @version 1.0, Jun 28, 2013
 */
public class RedisFactory {
	
	
	// ~ -------------------------------------------------------------------------------------------- Singleton Redis
	
	
	public static Redis newRedis(String host, int port) {
		return new DefaultRedis(host, port);
	}
	
	public static Redis newRedis(String host, int port, int timeout) {
		return new DefaultRedis(host, port, timeout);
	}

	public static Redis newRedis(String host, int port, int timeout, int poolSize) {
		return new DefaultRedis(host, port, timeout, poolSize);
	}
	
	public static Redis newRedis(String host, int port, int timeout, int poolSize, String password) {
		return new DefaultRedis(host, port, timeout, poolSize, password);
	}
	
	public static Redis newRedis(String host, int port, int timeout, int poolSize, String password, int database) {
		return new DefaultRedis(host, port, timeout, poolSize, password, database);
	}
	
	public static Redis newRedis(String host, int port, int timeout, Config poolConfig) {
		return new DefaultRedis(host, port, timeout, poolConfig);
	}
	
	public static Redis newRedis(String host, int port, int timeout, Config poolConfig, String password) {
		return new DefaultRedis(host, port, timeout, poolConfig, password);
	}
	
	/**
	 * Creates a singleton redis client
	 * 
	 * @param host         redis server host
	 * @param port         redis server port
	 * @param timeout      connect and read timeout in milliseconds
	 * @param poolConfig   connection pool config, default poolConfig is maxActive=maxIdle=poolSize, minIdle=0
	 * @param password     redis server auth password
	 * @param database     redis server db index
	 * @return a singleton redis client
	 */
	public static Redis newRedis(String host, int port, int timeout, Config poolConfig, String password, int database) {
		return new DefaultRedis(host, port, timeout, poolConfig, password, database);
	}
	
	
	// ~ ----------------------------------------------------------------------------------------- Master-Slave Redis
	
	
	public static MasterSlaveRedis newMasterSlaveRedis(Redis master, Redis... slaves) {
		List<Redis> chain = new ArrayList<Redis>();
		chain.add(master);
		for (int i = 0; i < slaves.length; i++) {
			chain.add(slaves[i]);
		}
		return new DefaultMasterSlaveRedis(chain);
	}
	
	public static MasterSlaveRedis newMasterSlaveRedis(List<Redis> chain) {
		return new DefaultMasterSlaveRedis(chain);
	}
	
	/**
	 * Creates a master-slave redis client
	 * 
	 * @param chain   master-slave redis chain, the chain is clockwise direction.
	 * @param index   master index, default master index is 0.
	 * @return a master-slave redis client
	 */
	public static MasterSlaveRedis newMasterSlaveRedis(List<Redis> chain, int index) {
		return new DefaultMasterSlaveRedis(chain);
	}
	
	
	// ~ ---------------------------------------------------------------------------------------------- Sharded Redis
	
	
	public static ShardedRedis newShardedRedis(List<Redis> shards) {
		return new DefaultShardedRedis(shards);
	}
	
	/**
	 * Creates a sharded redis client.
	 * 
	 * @param sharded
	 * @return a sharded redis client.
	 */
	public static ShardedRedis newShardedRedis(Sharded<Redis> sharded) {
		return new DefaultShardedRedis(sharded);
	}
	
	
	// ~ ---------------------------------------------------------------------------------- Master-Slave Sharded Redis
	
	
	public static MasterSlaveShardedRedis newMasterSlaveShardedRedis(List<MasterSlaveRedis> shards) {
		return new DefaultMasterSlaveShardedRedis(shards);
	}
	
	/**
	 * Creates a master-salve sharded redis client.
	 * 
	 * @param sharded
	 * @return a master-salve sharded redis client
	 */
	public static MasterSlaveShardedRedis newMasterSlaveShardedRedis(Sharded<MasterSlaveRedis> sharded) {
		return new DefaultMasterSlaveShardedRedis(sharded);
	}
}

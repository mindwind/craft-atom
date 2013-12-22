package org.craft.atom.redis.api;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	/**
	 * @param hostport  e.g. localhost:6379
	 * @return a singleton redis client
	 */
	public static Redis newRedis(String hostport) {
		String[] sarr = hostport.trim().split(":");
		String host = sarr[0];
		int port = Integer.parseInt(sarr[1]);
		return new DefaultRedis(host, port);
	}
	
	
	// ~ ----------------------------------------------------------------------------------------- Master-Slave Redis
	
	
	public static MasterSlaveRedis newMasterSlaveRedis(Redis master, Redis... slaves) {
		List<Redis> chain = new ArrayList<Redis>();
		chain.add(master);
		for (int i = 0; i < slaves.length; i++) {
			chain.add(slaves[i]);
		}
		return newMasterSlaveRedis(chain);
	}
	
	public static MasterSlaveRedis newMasterSlaveRedis(List<Redis> chain) {
		return newMasterSlaveRedis(chain, 0);
	}
	
	/**
	 * Creates a master-slave redis client
	 * 
	 * @param chain   master-slave redis chain, the chain is clockwise direction.
	 * @param index   master index, default master index is 0.
	 * @return a master-slave redis client
	 */
	public static MasterSlaveRedis newMasterSlaveRedis(List<Redis> chain, int index) {
		return new DefaultMasterSlaveRedis(chain, index);
	}
	
	/**
	 * @param masterslavestring  e.g. localhost:6379-localhost:6380-localhost:6381 the first is master, others are slaves.
	 * @return a master-slave redis client
	 */
	public static MasterSlaveRedis newMasterSlaveRedis(String masterslavestring) {
		String[] hostports = masterslavestring.split("-");
		List<Redis> chain = convert(hostports);
		return newMasterSlaveRedis(chain);
	}
	
	private static List<Redis> convert(String[] hostports) {
		List<Redis> l = new ArrayList<Redis>(hostports.length);
		for (String hostport : hostports) {
			Redis redis = newRedis(hostport.trim());
			l.add(redis);
		}
		return l;
	}
	
	
	// ~ ---------------------------------------------------------------------------------------------- Sharded Redis
	
	
	public static ShardedRedis newShardedRedis(List<Redis> shards) {
		return new DefaultShardedRedis(shards);
	}
	
	public static ShardedRedis newShardedRedis(Redis... shards) {
		return new DefaultShardedRedis(Arrays.asList(shards));
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
	
	/**
	 * @param shardstring e.g. localhost:6379,localhost:6380
	 * @return sharded redis client
	 */
	public static ShardedRedis newShardedRedis(String shardstring) {
		String[] hostports = shardstring.split(",");
		List<Redis> shards = convert(hostports);
		return new DefaultShardedRedis(shards);
	}
	
	
	// ~ ---------------------------------------------------------------------------------- Master-Slave Sharded Redis
	
	
	public static MasterSlaveShardedRedis newMasterSlaveShardedRedis(List<MasterSlaveRedis> shards) {
		return new DefaultMasterSlaveShardedRedis(shards);
	}
	
	public static MasterSlaveShardedRedis newMasterSlaveShardedRedis(MasterSlaveRedis... shards) {
		return new DefaultMasterSlaveShardedRedis(Arrays.asList(shards));
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
	
	/**
	 * @param masterslaveshards  e.g. localhost:6379-localhost:6380,localhost:6389-localhost:6390
	 * @return a master-salve sharded redis client.
	 */
	public static MasterSlaveShardedRedis newMasterSlaveShardedRedis(String masterslaveshards) {
		String[] masterslaves = masterslaveshards.split(",");
		List<MasterSlaveRedis> shards = new ArrayList<MasterSlaveRedis>();
		for (String masterslavestring : masterslaves) {
			MasterSlaveRedis msr = newMasterSlaveRedis(masterslavestring);
			shards.add(msr);
		}
		return newMasterSlaveShardedRedis(shards);
	}
}

package io.craft.atom.redis.api;

import io.craft.atom.redis.DefaultMasterSlaveRedis;
import io.craft.atom.redis.DefaultMasterSlaveShardedRedis;
import io.craft.atom.redis.DefaultShardedRedis;
import io.craft.atom.redis.spi.Sharded;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * An object that creates new redis instance on demand.
 * 
 * @author mindwind
 * @version 1.0, Jun 28, 2013
 */
public class RedisFactory {
	
	
	// ~ ---------------------------------------------------------------------------------------------- redis builder
	
	
	public static RedisBuilder newRedisBuilder(String host, int port) {
		return new RedisBuilder(host, port);
	}
	
	/**
	 * @param hostport format string e.g. localhost:6379
	 */
	public static RedisBuilder newRedisBuilder(String hostport) {
		return new RedisBuilder(hostport);
	}
	
	/**
	 * @param masterslavestring format string e.g. localhost:6379-localhost:6380-localhost:6381 the first is master, others are slaves.
	 */
	public static MasterSlaveRedisBuilder newMasterSlaveRedisBuilder(String masterslavestring) {
		return new MasterSlaveRedisBuilder(masterslavestring);
	}
	
	/**
	 * @param shardstring format string e.g. localhost:6379,localhost:6380,localhost:6381
	 */
	public static ShardedRedisBuilder newShardedRedisBuilder(String shardstring) {
		return new ShardedRedisBuilder(shardstring);
	}
	
	/**
	 * @param masterslaveshardstring format string e.g. localhost:6379-localhost:6380,localhost:6389-localhost:6390
	 */
	public static MasterSlaveShardedRedisBuilder newMasterSlaveShardedRedisBuilder(String masterslaveshardstring) {
		return new MasterSlaveShardedRedisBuilder(masterslaveshardstring);
	}
	
	
	// ~ -------------------------------------------------------------------------------------------- Singleton Redis
	
	
	public static Redis newRedis(String host, int port) {
		return newRedisBuilder(host, port).build();
	}
	
	public static Redis newRedis(String host, int port, int timeoutInMillis) {
		return newRedisBuilder(host, port).timeoutInMillis(timeoutInMillis).build();
	}

	public static Redis newRedis(String host, int port, int timeoutInMillis, int poolSize) {
		return newRedisBuilder(host, port).timeoutInMillis(timeoutInMillis).poolMinIdle(0).poolMaxIdle(poolSize).poolMaxTotal(poolSize).build();
	}
	
	public static Redis newRedis(String host, int port, int timeoutInMillis, int poolSize, String password) {
		return newRedisBuilder(host, port).timeoutInMillis(timeoutInMillis).poolMinIdle(0).poolMaxIdle(poolSize).poolMaxTotal(poolSize).password(password).build();
	}
	
	public static Redis newRedis(String host, int port, int timeoutInMillis, int poolSize, String password, int database) {
		return newRedisBuilder(host, port).timeoutInMillis(timeoutInMillis).poolMinIdle(0).poolMaxIdle(poolSize).poolMaxTotal(poolSize).password(password).database(database).build();
	}
	
	public static Redis newRedis(String host, int port, int timeoutInMillis, RedisPoolConfig poolConfig) {
		return newRedisBuilder(host, port).timeoutInMillis(timeoutInMillis).redisPoolConfig(poolConfig).build();
	}
	
	public static Redis newRedis(String host, int port, int timeoutInMillis, RedisPoolConfig poolConfig, String password) {
		return newRedisBuilder(host, port).timeoutInMillis(timeoutInMillis).redisPoolConfig(poolConfig).password(password).build();
	}
	

	public static Redis newRedis(String host, int port, int timeoutInMillis, RedisPoolConfig poolConfig, String password, int database) {
		return newRedisBuilder(host, port).timeoutInMillis(timeoutInMillis).redisPoolConfig(poolConfig).password(password).database(database).build();
	}
	
	public static Redis newRedis(String hostport) {
		return newRedisBuilder(hostport).build();
	}
	
	
	// ~ ----------------------------------------------------------------------------------------- Master-Slave Redis
	
	
	public static MasterSlaveRedis newMasterSlaveRedis(String masterslavestring) {
		return newMasterSlaveRedisBuilder(masterslavestring).build();
	}
	
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
	
	public static MasterSlaveRedis newMasterSlaveRedis(List<Redis> chain, int index) {
		return new DefaultMasterSlaveRedis(chain, index);
	}
	
	
	// ~ ---------------------------------------------------------------------------------------------- Sharded Redis
	
	
	public static ShardedRedis newShardedRedis(List<Redis> shards) {
		return new DefaultShardedRedis(shards);
	}
	
	public static ShardedRedis newShardedRedis(Redis... shards) {
		return new DefaultShardedRedis(Arrays.asList(shards));
	}
	
	public static ShardedRedis newShardedRedis(Sharded<Redis> sharded) {
		return new DefaultShardedRedis(sharded);
	}
	
	public static ShardedRedis newShardedRedis(String shardstring) {
		return newShardedRedisBuilder(shardstring).build();
	}
	
	
	// ~ ---------------------------------------------------------------------------------- Master-Slave Sharded Redis
	
	
	public static MasterSlaveShardedRedis newMasterSlaveShardedRedis(List<MasterSlaveRedis> shards) {
		return new DefaultMasterSlaveShardedRedis(shards);
	}
	
	public static MasterSlaveShardedRedis newMasterSlaveShardedRedis(MasterSlaveRedis... shards) {
		return new DefaultMasterSlaveShardedRedis(Arrays.asList(shards));
	}
	
	public static MasterSlaveShardedRedis newMasterSlaveShardedRedis(Sharded<MasterSlaveRedis> sharded) {
		return new DefaultMasterSlaveShardedRedis(sharded);
	}
	
	public static MasterSlaveShardedRedis newMasterSlaveShardedRedis(String masterslaveshardstring) {
		return newMasterSlaveShardedRedisBuilder(masterslaveshardstring).build();
	}
}

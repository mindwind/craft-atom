package org.craft.atom.redis.api;

import java.util.ArrayList;
import java.util.List;

import org.craft.atom.redis.DefaultMasterSlaveShardedRedis;


/**
 * Builder for {@link MasterSlaveShardedRedis}
 * 
 * @author mindwind
 * @version 1.0, Apr 19, 2014
 */
public class MasterSlaveShardedRedisBuilder extends AbstractRedisBuilder<MasterSlaveShardedRedis> {
	
	
	private String masterslaveshardstring;

	
	/**
	 * @param masterslaveshardstring format string e.g. localhost:6379-localhost:6380,localhost:6389-localhost:6390
	 */
	public MasterSlaveShardedRedisBuilder(String masterslaveshardstring) {
		this.masterslaveshardstring = masterslaveshardstring;
	}
	
	
	static String[] parse(String masterslaveshardstring) {
		return masterslaveshardstring.trim().split(",");
	}
	
	
	@Override
	public MasterSlaveShardedRedis build() {
		RedisPoolConfig  poolConfig = new RedisPoolConfig();
		set(poolConfig);
		String[] masterslavestrings = parse(masterslaveshardstring);
		
		List<MasterSlaveRedis> shards = new ArrayList<MasterSlaveRedis>();
		for (String masterslavestring : masterslavestrings) {
			MasterSlaveRedis msr = new MasterSlaveRedisBuilder(masterslavestring).copy(this).build();
			shards.add(msr);
		}
		return new DefaultMasterSlaveShardedRedis(shards);
	}

}

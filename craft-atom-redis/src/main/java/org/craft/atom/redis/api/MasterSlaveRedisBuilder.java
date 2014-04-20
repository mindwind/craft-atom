package org.craft.atom.redis.api;

import java.util.ArrayList;
import java.util.List;

import org.craft.atom.redis.DefaultMasterSlaveRedis;


/**
 * Builder for {@link MasterSlaveRedis}
 * 
 * @author mindwind
 * @version 1.0, Apr 19, 2014
 */
public class MasterSlaveRedisBuilder extends AbstractRedisBuilder<MasterSlaveRedis> {
	
	
	private String masterslavestring;

	
	/**
	 * @param masterslavestring format string e.g. localhost:6379-localhost:6380-localhost:6381 the first is master, others are slaves.
	 */
	public MasterSlaveRedisBuilder(String masterslavestring) {
		this.masterslavestring = masterslavestring;
	}
	
	
	static String[] parse(String masterslavestring) {
		return masterslavestring.trim().split("-");
	}
	
	
	@Override
	public MasterSlaveRedis build() {
		RedisPoolConfig  poolConfig = new RedisPoolConfig();
		set(poolConfig);
		String[] hostports = parse(masterslavestring);
		
		List<Redis> chain = new ArrayList<Redis>(hostports.length);
		for (String hostport : hostports) {
			Redis redis = new RedisBuilder(hostport).copy(this).build();
			chain.add(redis);
		}
		return new DefaultMasterSlaveRedis(chain, 0);
	}

}

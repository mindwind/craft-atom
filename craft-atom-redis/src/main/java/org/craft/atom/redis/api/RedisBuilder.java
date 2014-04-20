package org.craft.atom.redis.api;

import org.craft.atom.redis.DefaultRedis;

/**
 * Builder for {@link Redis}
 * 
 * @author mindwind
 * @version 1.0, Apr 19, 2014
 */
public class RedisBuilder extends AbstractRedisBuilder<Redis> {
	
	
	private String host;
	private int    port;

	
	public RedisBuilder(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	/**
	 * @param hostport format string e.g. localhost:6379
	 */
	public RedisBuilder(String hostport) {
		String[] a = parse(hostport);
		this.host = a[0];
		this.port = Integer.parseInt(a[1]);
	}
	
	
	static String[] parse(String hostport) {
		return hostport.trim().split(":");
	}
	
	
	@Override
	public Redis build() {
		RedisPoolConfig  poolConfig = new RedisPoolConfig();
		set(poolConfig);
		return new DefaultRedis(host, port, timeoutInMillis, poolConfig, password, database);
	}

}

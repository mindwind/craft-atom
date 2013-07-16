package org.craft.atom.redis.api;

import org.apache.commons.pool.impl.GenericObjectPool.Config;

/**
 * The singleton redis client.
 * 
 * @author mindwind
 * @version 1.0, May 3, 2013
 */
public interface Redis extends RedisCommand {
	
	/**
	 * @return redis server host.
	 */
	String host();
	
	/**
	 * @return redis server port.
	 */
	int port();
		
	/** 
	 * @return redis server password.
	 */
	String password();
	
	/**
	 * @return redis server timeout in milliseconds.
	 */
	int timeout();
	
	/**
	 * @return redis client connection pool config.
	 */
	Config config();
	


}

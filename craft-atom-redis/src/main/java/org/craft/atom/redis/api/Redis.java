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
	 * Return redis server host.
	 * 
	 * @return
	 */
	String host();
	
	/**
	 * Return redis server port.
	 * 
	 * @return
	 */
	int port();
		
	/**
	 * Return redis server password.
	 * 
	 * @return
	 */
	String password();
	
	/**
	 * Return redis server timeout int milliseconds.
	 * 
	 * @return
	 */
	int timeout();
	
	/**
	 * Return redis client pool config.
	 * 
	 * @return
	 */
	Config config();
	


}

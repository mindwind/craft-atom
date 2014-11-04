package io.craft.atom.redis.api;


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
	 * @return socket connect/read timeout in milliseconds.
	 */
	int timeoutInMillis();
	
	/**
	 * @return redis database num
	 */
	int database();
	
	/**
	 * @return redis client connection pool config.
	 */
	RedisPoolConfig poolConfig();
	


}

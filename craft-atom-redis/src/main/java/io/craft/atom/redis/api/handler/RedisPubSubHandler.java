package io.craft.atom.redis.api.handler;

import io.craft.atom.redis.api.RedisException;

/**
 * @author mindwind
 * @version 1.0, Jul 1, 2013
 */
public interface RedisPubSubHandler {
	
	/**
	 * Invoked on redis exception occur.
	 * 
	 * @param e
	 */
	void onException(RedisException e);
	
}

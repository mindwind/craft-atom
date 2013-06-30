package org.craft.atom.redis.api.handler;



/**
 * Handles redis subscribe events
 * 
 * @author mindwind
 * @version 1.0, Jun 6, 2013
 */
public interface RedisSubscribeHandler {
	
	/**
	 * Invoked on subscribe occur.
	 * 
	 * @param channel
	 * @param no
	 */
	void onSubscribe(String channel, int no);
	
	/**
	 * Invoked on message published to the channel.
	 * 
	 * @param channel
	 * @param message
	 */
	void onMessage(String channel, String message);
	
}

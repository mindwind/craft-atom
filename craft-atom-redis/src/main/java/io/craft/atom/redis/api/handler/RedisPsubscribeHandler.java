package io.craft.atom.redis.api.handler;


/**
 * Handles redis psubscribe events
 * 
 * @author mindwind
 * @version 1.0, Jun 6, 2013
 */
public interface RedisPsubscribeHandler extends RedisPubSubHandler {
	
	/**
	 * Invoked on psubscribe occur.
	 * 
	 * @param pattern
	 * @param no
	 */
	void onPsubscribe(String pattern, int no);
	
	/**
	 * Invoked on message published to the pattern.
	 * 
	 * @param pattern
	 * @param channel
	 * @param message
	 */
	void onMessage(String pattern, String channel, String message);
}

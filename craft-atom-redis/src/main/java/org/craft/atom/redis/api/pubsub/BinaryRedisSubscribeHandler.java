package org.craft.atom.redis.api.pubsub;


/**
 * Handles redis subscribe events
 * 
 * @author mindwind
 * @version 1.0, Jun 6, 2013
 */
public interface BinaryRedisSubscribeHandler {
	
	/**
	 * Invoked on subscribe occur.
	 * 
	 * @param pattern
	 * @param no
	 */
	void onSubscribe(byte[] pattern, int no);
	
	/**
	 * Invoked on message published to the channel.
	 * 
	 * @param channel
	 * @param message
	 */
	void onMessage(byte[] channel, byte[] message);
}

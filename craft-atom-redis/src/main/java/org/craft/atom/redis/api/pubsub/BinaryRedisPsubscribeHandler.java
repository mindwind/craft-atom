package org.craft.atom.redis.api.pubsub;


/**
 * Handles redis psubscribe events
 * 
 * @author mindwind
 * @version 1.0, Jun 6, 2013
 */
public interface BinaryRedisPsubscribeHandler {
	
	/**
	 * Invoked on psubscribe occur.
	 * 
	 * @param pattern
	 * @param no
	 */
	void onPsubscribe(byte[] pattern, int no);
	
	/**
	 * Invoked on message published to the pattern.
	 * 
	 * @param pattern
	 * @param channel
	 * @param message
	 */
	void onMessage(byte[] pattern, byte[] channel, byte[] message);
}

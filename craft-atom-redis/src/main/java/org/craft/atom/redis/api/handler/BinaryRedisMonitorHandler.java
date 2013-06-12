package org.craft.atom.redis.api.handler;

/**
 * Handles redis monitor events.
 * 
 * @author mindwind
 * @version 1.0, Jun 12, 2013
 */
public interface BinaryRedisMonitorHandler {
	
	/**
	 * Invoked on a command be executed.
	 * 
	 * @param command
	 */
	void onCommand(byte[] command);
	
}

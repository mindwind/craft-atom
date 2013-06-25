package org.craft.atom.redis.api;

/**
 * The master slave redis client.
 * 
 * @author mindwind
 * @version 1.0, Jun 25, 2013
 */
public interface MasterSlaveRedis extends Redis {
	
	void switchover();
	
}

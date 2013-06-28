package org.craft.atom.redis.api;

import java.util.List;

/**
 * The master-slave redis client.
 * <p>
 * The master slave should be in chain structure, for example
 * <pre>
 *         MASTER --- SLAVE1 --- SLAVE2 ... --- ... SLAVEn
 * index     0          1          2                  n
 * </pre>
 * All commands would be sent to master node, if switch master would trigger rebuild chain structure as follows:
 * <pre>
 *         SLAVE0 --> SLAVE1     MASTER2 ... --- ... SLAVEn
 *           |                                         |
 *           |-----------------------------------------|
 * </pre>
 * 
 * @author mindwind
 * @version 1.0, Jun 25, 2013
 */
public interface MasterSlaveRedis extends RedisCommand {
	
	/**
	 * Set master node by index and rebuild chain.
	 * 
	 * @param index
	 */
	void master(int index);
	
	/**
	 * Return master redis node.
	 * 
	 * @return
	 */
	Redis master();
	
	/**
	 * Return master node index.
	 * 
	 * @return
	 */
	int index();
	
	/**
	 * Return master-slave redis chain list.
	 * 
	 * @return
	 */
	List<Redis> chain();
	
	/**
	 * Reset master-slave chain, using zero-index node as the master. 
	 */
	void reset();
	
}

package io.craft.atom.redis.api;

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
	 * Reset master-slave chain, using zero-index node as the master. 
	 */
	void reset();
	
	/**
	 * Enable read from slave and write still on master.
	 * For commands type "Server" "Connection" and "Transaction" all commands of these type all send to master.
	 */
	void enableReadSlave();
	
	/**
	 * Disable read from slave.
	 */
	void disableReadSlave();
	
	/**
	 * @return master redis node.
	 */
	Redis master();
	
	/**
	 * @return master node index.
	 */
	int index();
	
	/**
	 * @return master-slave redis chain list.
	 */
	List<Redis> chain();
	
}

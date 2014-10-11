package org.craft.atom.rpc.spi;


/**
 * RPC registry
 * 
 * @author mindwind
 * @version 1.0, Oct 11, 2014
 */
public interface RpcRegistry {

	/**
	 * Register a entry.
	 * 
	 * @param entry
	 */
	void register(RpcEntry entry);
	
	/**
	 * Unregister a entry.
	 * 
	 * @param entry
	 */
	void unregister(RpcEntry entry);
	
	/**
	 * Lookup a rpc entry by the entry key.
	 * 
	 * @param  entry query entry
	 * @return result entry
	 */
	RpcEntry lookup(RpcEntry entry);
}

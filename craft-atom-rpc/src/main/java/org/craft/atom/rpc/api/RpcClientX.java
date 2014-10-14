package org.craft.atom.rpc.api;


/**
 * The x-ray of {@link RpcClient}
 * 
 * @author mindwind
 * @version 1.0, Oct 13, 2014
 */
public interface RpcClientX {
	
	/**
	 * @return the approximate wait to be sent request count.
	 */
	int waitCount();
	
}

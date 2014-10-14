package org.craft.atom.rpc.api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * RPC behavioral parameter.
 * 
 * @author mindwind
 * @version 1.0, Sep 5, 2014
 */
@ToString
public class RpcParameter {
	
	
	/** RPC thread number for each rpc method. */
	@Getter @Setter private int rpcThreads = 1;
	
	/** RPC queue size for each rpc method. */
	@Getter @Setter private int rpcQueues  = 10;

	
	public RpcParameter() {}
	
	public RpcParameter(int rpcThreads, int rpcQueues) {
		this.rpcThreads = rpcThreads;
		this.rpcQueues = rpcQueues;
	}
	
}

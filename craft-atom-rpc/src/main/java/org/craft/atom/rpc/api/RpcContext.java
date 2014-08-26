package org.craft.atom.rpc.api;

import java.net.InetSocketAddress;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;


/**
 * RPC context is a thread local context. 
 * Each rpc invocation associate a context instance through current thread.
 * 
 * @author mindwind
 * @version 1.0, Aug 26, 2014
 */
public final class RpcContext {
	
	
	private static final ThreadLocal<RpcContext> THREAD_LOCAL = new ThreadLocal<RpcContext>() {
		@Override
		protected RpcContext initialValue() {
			return new RpcContext();
		}
	};
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	@Getter @Setter private InetSocketAddress   serverAddress     ;
	@Getter @Setter private InetSocketAddress   clientAddress     ;
	@Getter @Setter private Map<String, Object> attachments       ;
	@Getter @Setter private int                 rpcTimeoutInMillis;
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	

	/**
	 * Get rpc context.
	 * 
	 * @return context
	 */
	public static RpcContext getContext() {
	    return THREAD_LOCAL.get();
	}
	
	/**
	 * Remove rpc context.
	 */
	public static void removeContext() {
	    THREAD_LOCAL.remove();
	}

}

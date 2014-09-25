package org.craft.atom.rpc.api;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * RPC context is a thread local context. 
 * Each rpc invocation bind a context instance to current thread.
 * 
 * @author mindwind
 * @version 1.0, Aug 26, 2014
 */
@ToString
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
	@Getter @Setter private Map<String, String> attachments       ;
	@Getter @Setter private String              rpcId             ;
	@Getter @Setter private int                 rpcTimeoutInMillis;
	@Getter @Setter private boolean             oneway            ;
	@Getter @Setter private boolean             async             ;
	        @Setter private Future<?>           future            ;
	
	
	public RpcContext() {
		attachments = new HashMap<String, String>();
	}
	
	
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
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	/**
     * Set attachment.
     * 
     * @param key
     * @param value
     * @return context
     */
    public RpcContext setAttachment(String key, String value) {
    	attachments.put(key, value);
        return this;
    }

    /**
     * Remove attachment.
     * 
     * @param key
     * @return context
     */
    public RpcContext removeAttachment(String key) {
        attachments.remove(key);
        return this;
    }
    
    /**
     * Get attachment.
     * 
     * @param key
     * @return attachment
     */
    public String getAttachment(String key) {
    	return attachments.get(key);
    }
    
    /**
     * Get future object for asynchronous invocation.
     * 
     * @return
     */
	@SuppressWarnings("unchecked")
	public <T> Future<T> getFuture() {
        return (Future<T>) future;
    }

}

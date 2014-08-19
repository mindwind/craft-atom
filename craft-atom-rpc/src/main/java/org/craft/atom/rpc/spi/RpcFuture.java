package org.craft.atom.rpc.spi;

import java.util.concurrent.TimeUnit;

import org.craft.atom.protocol.rpc.model.RpcMessage;

/**
 * Represents the completion of an asynchronous rpc invocation.
 * 
 * @author mindwind
 * @version 1.0, Aug 19, 2014
 */
public interface RpcFuture {
	
	
	/**
	 * Wait for the asynchronous operation to complete with the specified timeout.
	 * 
	 * @param timeout
	 * @param unit
	 * @return <tt>true</tt> if the operation is completed.
	 * @throws InterruptedException
	 */
	boolean await(long timeout, TimeUnit unit) throws InterruptedException;
	
	/**
     * Returns the cause of the rpc failure if and only if the rpc operation has failed due to an {@link Exception}.  
     * Otherwise, <tt>null</tt> is returned.
     */
    Throwable getThrowable();
    
    /**
     * Returns the rpc response message, it returns <tt>null</tt> if this future is not ready.
     * @return rpc response message.
     */
    RpcMessage getResponse();
    
	/**
	 * Set the cause of the rpc failure, and notifies all threads waiting for
	 * this future. This method is invoked internally. Please do not call this
	 * method directly.
	 * 
	 * @param cause
	 */
	void setThrowable(Throwable cause);

	/**
	 * Set the rpc response message, and notifies all threads waiting for this
	 * future. This method is invoked internally. Please do not call this method
	 * directly.
	 * 
	 * @param rsp rpc response message.
	 */
	void setResponse(RpcMessage rsp);
	
}

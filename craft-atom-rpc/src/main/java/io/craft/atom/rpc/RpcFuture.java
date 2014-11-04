package io.craft.atom.rpc;

import io.craft.atom.protocol.rpc.model.RpcMessage;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Represents the completion of an asynchronous rpc invocation.
 * 
 * @author mindwind
 * @version 1.0, Aug 19, 2014
 */
public interface RpcFuture<V> extends Future<V> {
	
	
	/**
	 * Wait for the asynchronous operation to complete with the specified timeout.
	 * 
	 * @param timeout
	 * @param unit
	 * @return <tt>true</tt> if the operation is completed.
	 * @throws InterruptedException if the current thread was interrupted while waiting
	 */
	boolean await(long timeout, TimeUnit unit) throws InterruptedException;
	
	/**
     * Returns the cause of the rpc failure if and only if the rpc operation has failed due to an {@link Exception}.  
     * Otherwise, <tt>null</tt> is returned.
     */
    Exception getException();
    
    /**
     * Returns the rpc response message, it returns <tt>null</tt> if this future is not ready.
     * @return rpc response message.
     * @throws TimeoutException if the wait timed out
     * @throws IOException if some other I/O error occurs
     */
    RpcMessage getResponse() throws IOException, TimeoutException;
    
	/**
	 * Set the cause of the rpc failure, and notifies all threads waiting for
	 * this future. This method is invoked internally. Please do not call this
	 * method directly.
	 * 
	 * @param exception
	 */
	void setException(Exception exception);

	/**
	 * Set the rpc response message, and notifies all threads waiting for this
	 * future. This method is invoked internally. Please do not call this method
	 * directly.
	 * 
	 * @param response rpc response message.
	 */
	void setResponse(RpcMessage response);
	
}

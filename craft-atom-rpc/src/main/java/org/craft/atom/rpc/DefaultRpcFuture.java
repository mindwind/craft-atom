package org.craft.atom.rpc;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.craft.atom.protocol.rpc.model.RpcMessage;

/**
 * @author mindwind
 * @version 1.0, Aug 19, 2014
 */
public class DefaultRpcFuture<V> implements RpcFuture<V> {
	
	
	private RpcMessage response ;
	private Exception  exception;
	private boolean    ready    ;
	private int        waiters  ;
	
	 
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		long timeoutMillis = unit.toMillis(timeout);
		long endTime       = System.currentTimeMillis() + timeoutMillis;
		synchronized (this) {
			if (ready)              return ready;
			if (timeoutMillis <= 0) return ready;
			waiters++;
			try {
				while (!ready) {
					wait(timeoutMillis);
					if (endTime < System.currentTimeMillis() && !ready) {
						exception = new TimeoutException();
						break;
					}
				}
			} finally {
				waiters--;
			}
		}
		return ready;
	}

	@Override
	public Exception getException() {
		synchronized (this) {
			return exception;
		}
	}

	@Override
	public RpcMessage getResponse() throws IOException, TimeoutException {
		Exception e = getException();
		if (e != null) {
			if (e instanceof IOException     ) throw (IOException)      e;
			if (e instanceof TimeoutException) throw (TimeoutException) e;
			throw new RpcException(RpcException.UNKNOWN, "unkonw error", e);
		}
		
		synchronized (this) {
			return response;
		}
	}

	@Override
	public void setException(Exception exception) {
		synchronized (this) {
			if (ready) return;
			this.exception = exception;
			ready = true;
			if (waiters > 0) {
                notifyAll();
            }
		}
	}

	@Override
	public void setResponse(RpcMessage response) {
		synchronized (this) {
			if (ready) return;
			this.response = response;
			ready = true;
			if (waiters > 0) {
                notifyAll();
            }
		}
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

}

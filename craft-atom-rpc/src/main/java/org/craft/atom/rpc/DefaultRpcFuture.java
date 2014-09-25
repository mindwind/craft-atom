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
	
	
	private volatile RpcMessage response ;
	private volatile Exception  exception;
	private volatile boolean    done     ;
	private volatile int        waiters  ;
	
	 
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		long timeoutMillis = unit.toMillis(timeout);
		long endTime       = System.currentTimeMillis() + timeoutMillis;
		synchronized (this) {
			if (done)               return done;
			if (timeoutMillis <= 0) return done;
			waiters++;
			try {
				while (!done) {
					wait(timeoutMillis);
					if (endTime < System.currentTimeMillis() && !done) {
						exception = new TimeoutException();
						break;
					}
				}
			} finally {
				waiters--;
			}
		}
		return done;
	}

	@Override
	public Exception getException() {
		return exception;
	}

	@Override
	public RpcMessage getResponse() throws IOException, TimeoutException {
		Exception e = getException();
		if (e != null) {
			if (e instanceof IOException     ) throw (IOException)      e;
			if (e instanceof TimeoutException) throw (TimeoutException) e;
			throw new RpcException(RpcException.UNKNOWN, "unkonw error", e);
		}
		return response;
	}

	@Override
	public void setException(Exception exception) {
		synchronized (this) {
			if (done) return;
			this.exception = exception;
			done = true;
			if (waiters > 0) {
                notifyAll();
            }
		}
	}

	@Override
	public void setResponse(RpcMessage response) {
		synchronized (this) {
			if (done) return;
			this.response = response;
			done = true;
			if (waiters > 0) {
                notifyAll();
            }
		}
	}
	
	@Override
	public V get() throws InterruptedException, ExecutionException {
		try { return get(Long.MAX_VALUE, TimeUnit.DAYS); } catch (TimeoutException e) { throw new InterruptedException(e.getMessage()); }
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		await(timeout, TimeUnit.MILLISECONDS);
		try {
			RpcMessage rsp = getResponse();
			return (V) RpcMessages.unpackResponseMessage(rsp);
		} catch (Exception e) {
			throw new ExecutionException(e);
		}
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return done;
	}

}

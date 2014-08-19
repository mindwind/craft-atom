package org.craft.atom.rpc;

import java.util.concurrent.TimeUnit;

import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.spi.RpcFuture;

/**
 * @author mindwind
 * @version 1.0, Aug 19, 2014
 */
public class DefaultRpcFuture implements RpcFuture {

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Throwable getThrowable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RpcMessage getResponse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setThrowable(Throwable cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setResponse(RpcMessage rsp) {
		// TODO Auto-generated method stub
		
	}

}

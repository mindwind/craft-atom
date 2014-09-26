package org.craft.atom.rpc;

import org.craft.atom.io.IllegalChannelStateException;
import org.craft.atom.rpc.api.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Sep 5, 2014
 */
public class DemoServiceImpl1 implements DemoService {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(DemoServiceImpl1.class);

	
	@Override
	public String echo(String in) {
		return in;
	}

	@Override
	public void noreturn(String in) {
		LOG.debug("[CRAFT-ATOM-RPC] Invoked noreturn() in={}", in);
	}

	@Override
	public void timeout(String in) throws InterruptedException {
		Thread.sleep(200);
	}

	@Override
	public String attachment() {
		RpcContext ctx = RpcContext.getContext();
		String r = ctx.getAttachment("demo");
		return r;
	}

	@Override
	public String oneway() {
		LOG.debug("[CRAFT-ATOM-RPC] Invoked oneway()");
		return "oneway";
	}

	@Override
	public void overload() throws InterruptedException {
		Thread.sleep(1000);
	}

	@Override
	public void bizException() throws IllegalAccessException {
		throw new IllegalAccessException("biz error");
	}

	@Override
	public void error() {
		throw new OutOfMemoryError();
	}

	@Override
	public void undeclaredException() throws IllegalStateException {
		throw new IllegalChannelStateException(); 
	}

}

package org.craft.atom.rpc;

import org.craft.atom.rpc.api.RpcClient;
import org.craft.atom.rpc.api.RpcFactory;
import org.craft.atom.rpc.api.RpcParameter;
import org.craft.atom.rpc.api.RpcServer;
import org.craft.atom.test.AvailablePortFinder;
import org.craft.atom.test.CaseCounter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for RPC
 * 
 * @author mindwind
 * @version 1.0, Sep 5, 2014
 */
public class TestRpc {
	
	
	@Test
	public void testBasic() {
		int port = AvailablePortFinder.getNextAvailable();
		RpcServer server = RpcFactory.newRpcServer(port);
		server.expose(DemoService.class, new DefaultDemoService(), new RpcParameter());
		server.serve();
		RpcClient client = RpcFactory.newRpcClient("localhost", port);
		client.open();
		
		DemoService ds = client.refer(DemoService.class);
		String hi = ds.echo("hi");
		Assert.assertEquals("hi", hi);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test basic. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testRt() {
		int port = AvailablePortFinder.getNextAvailable();
		RpcServer server = RpcFactory.newRpcServer(port);
		server.expose(DemoService.class, new DefaultDemoService(), new RpcParameter());
		server.serve();
		RpcClient client = RpcFactory.newRpcClient("localhost", port);
		client.open();
		
		DemoService ds = client.refer(DemoService.class);
		ds.echo("hi");
		long s = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			ds.echo("hi");
		}
		long e = System.currentTimeMillis();
		long rt = e - s;
		Assert.assertTrue(rt < 1000);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test rt <%s> ms. ", CaseCounter.incr(1), rt));
	}
	
}

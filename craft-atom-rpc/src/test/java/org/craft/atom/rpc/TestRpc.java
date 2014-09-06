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
	
	
	private static final int PORT = AvailablePortFinder.getNextAvailable();
	
	
	@Test
	public void testBasic() {
		RpcServer server = RpcFactory.newRpcServer(PORT);
		server.expose(DemoService.class, new DefaultDemoService(), new RpcParameter());
		server.serve();
		
		RpcClient client = RpcFactory.newRpcClient("localhost", PORT);
		client.open();
		DemoService ds = client.refer(DemoService.class);
		String hi = ds.echo("hi");
		Assert.assertEquals("hi", hi);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test basic. ", CaseCounter.incr(1)));
	}
	
}

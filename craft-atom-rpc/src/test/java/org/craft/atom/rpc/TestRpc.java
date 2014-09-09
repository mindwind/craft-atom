package org.craft.atom.rpc;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.craft.atom.rpc.api.RpcClient;
import org.craft.atom.rpc.api.RpcContext;
import org.craft.atom.rpc.api.RpcFactory;
import org.craft.atom.rpc.api.RpcParameter;
import org.craft.atom.rpc.api.RpcServer;
import org.craft.atom.test.AvailablePortFinder;
import org.craft.atom.test.CaseCounter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test for RPC
 * 
 * @author mindwind
 * @version 1.0, Sep 5, 2014
 */
public class TestRpc {
	
	
	private DemoService ds           ; 
	private RpcServer   server       ;
	private RpcClient   client       ;
	private String      host         ;
	private int         port         ;
	
	
	@Before
	public void before() {
		host = "localhost";
		port = AvailablePortFinder.getNextAvailable();
		server = RpcFactory.newRpcServer(port);
		server.expose(DemoService.class, new DefaultDemoService(), new RpcParameter());
		server.serve();
		client = RpcFactory.newRpcClient(host, port);
		client.open();
		ds = client.refer(DemoService.class);
	}
	
	@Test
	public void testBasic() {
		String hi = ds.echo("hi");
		Assert.assertEquals("hi", hi);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test basic. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testTimeout() throws InterruptedException {
		RpcContext.getContext().setRpcTimeoutInMillis(100);
		try {
			ds.timeout("hi");
			Assert.fail();
		} catch (RpcException e) {
			Assert.assertTrue(RpcException.CLIENT_TIMEOUT == e.getCode() || RpcException.SERVER_TIMEOUT == e.getCode());
		}
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test timeout. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testVoid() {
		ds.noreturn("hi");
		Assert.assertTrue(true);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test void. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testAttachment() {
		RpcContext.getContext().setAttachment("demo", "demo");
		String atta = ds.attachment();
		Assert.assertEquals("demo", atta);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test attachment. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testOneway() throws InterruptedException {
		RpcContext.getContext().setOneway(true);
		String r = ds.oneway();
		Assert.assertNull(r);
		Thread.sleep(100);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test oneway. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testMultiConnections() {
		client = RpcFactory.newRpcClientBuilder(host, port).connections(10).build();
		client.open();
		ds = client.refer(DemoService.class);
		for (int i = 0; i < 20; i++) {
			String hi = ds.echo("hi-" + i);
			Assert.assertEquals("hi-" + i, hi);
		}
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test multi connections. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testBrokenConnectionAndReconnect() throws InterruptedException {
		int conns = 10;
		client = RpcFactory.newRpcClientBuilder(host, port).connections(conns).build();
		client.open();
		ds = client.refer(DemoService.class);
		DefaultRpcConnector connector = (DefaultRpcConnector) ((DefaultRpcClient) client).getConnector();
		connector.setAllowReconnect(false);
		connector.setReconnectDelay(10);
		connector.brokeAll();
		try {
			ds.echo("hi");
		} catch (RpcException e) {
			Assert.assertEquals(RpcException.NETWORK, e.getCode());
		}
		connector.setAllowReconnect(true);
		Thread.sleep(100);
		int ac = connector.aliveConnectionNum();
		Assert.assertEquals(conns, ac);
		String hello = ds.echo("hello");
		Assert.assertEquals("hello", hello);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test broken connection and reconnect. ", CaseCounter.incr(3)));
	}
	
	@Test
	public void testHeartbeat() throws InterruptedException {
		port = AvailablePortFinder.getNextAvailable();
		server = RpcFactory.newRpcServerBuilder(port).ioTimeoutInMillis(100).build();
		server.expose(DemoService.class, new DefaultDemoService(), new RpcParameter());
		server.serve();
		client = RpcFactory.newRpcClient(host, port);
		client.open();
		ds = client.refer(DemoService.class);
		DefaultRpcConnector connector = (DefaultRpcConnector) ((DefaultRpcClient) client).getConnector();
		connector.setAllowReconnect(false);
		Thread.sleep(220);
		
		// no heartbeat
		try {
			ds.echo("hi");
			Assert.fail();
		} catch (RpcException e) {
			Assert.assertEquals(RpcException.NETWORK, e.getCode());
		}
		// heartbeat
		client = RpcFactory.newRpcClientBuilder(host, port).heartbeatInMillis(50).build();
		client.open();
		ds = client.refer(DemoService.class);
		Thread.sleep(210);
		
		String hi = ds.echo("hi");
		Assert.assertEquals("hi", hi);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test heartbeat. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testOverload() throws InterruptedException {
		Executor executor = Executors.newCachedThreadPool();
		for (int i = 0; i < 11; i++) {
			executor.execute(new Runnable() {
				
				@Override
				public void run() {
					try { ds.overload(); } catch (InterruptedException e) {}
					
				}
			});
		}
		Thread.sleep(100);
		try {
			ds.overload();
		} catch (RpcException e) {
			Assert.assertEquals(RpcException.SERVER_OVERLOAD, e.getCode());
		}
	}
	
	@Test
	public void testBizException() {
		try {
			ds.bizException();
			Assert.fail();
		} catch (IllegalAccessException e) {
			Assert.assertTrue(true);
		}
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test biz exception. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testError() {
		try {
			ds.error();
			Assert.fail();
		} catch (RpcException e) {
			Assert.assertEquals(RpcException.SERVER_ERROR, e.getCode());
		}
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test error. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testUndeclaredException() {
		try {
			ds.undeclaredException();
			Assert.fail();
		} catch (RpcException e) {
			Assert.assertEquals(RpcException.SERVER_ERROR, e.getCode());
		}
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test undeclared exception. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testRt() {
		// remote
		RpcContext.getContext().setRpcTimeoutInMillis(5000);
		ds.echo("hi");
		long s = System.nanoTime();
		for (int i = 0; i < 1000; i++) {
			ds.echo("hi");
		}
		long e = System.nanoTime();
		long rrt = e - s;
		
		// local
		ds = new DefaultDemoService();
		s = System.nanoTime();
		for (int i = 0; i < 100; i++) {
			ds.echo("hi");
		}
		e = System.nanoTime();
		long lrt = e - s;
		
		Assert.assertTrue(rrt > lrt);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test rt |local=%s, remote=%s| ns. ", CaseCounter.incr(1), lrt, rrt));
	}
	
}

package org.craft.atom.rpc;

import java.net.Socket;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.craft.atom.io.AbstractIoHandler;
import org.craft.atom.io.Channel;
import org.craft.atom.io.IoConnector;
import org.craft.atom.nio.api.NioFactory;
import org.craft.atom.protocol.rpc.model.RpcMethod;
import org.craft.atom.rpc.api.RpcClient;
import org.craft.atom.rpc.api.RpcContext;
import org.craft.atom.rpc.api.RpcFactory;
import org.craft.atom.rpc.api.RpcParameter;
import org.craft.atom.rpc.api.RpcServer;
import org.craft.atom.rpc.api.RpcServerX;
import org.craft.atom.rpc.spi.RpcApi;
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
	
	
	private DemoService ds    ; 
	private RpcServer   server;
	private RpcClient   client;
	private String      host  ;
	private int         port  ;
	
	
	@Before
	public void before() {
		host = "localhost";
		port = AvailablePortFinder.getNextAvailable();
		server = RpcFactory.newRpcServer(port);
		server.export(DemoService.class, new DemoServiceImpl1(), new RpcParameter(10, 100));
		server.open();
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
	public void testInMultiThreads() throws InterruptedException {
		int count = 20;
		final CountDownLatch latch = new CountDownLatch(count);
		final AtomicBoolean flag = new AtomicBoolean(true);
		Executor executor = Executors.newFixedThreadPool(100);
		for (int i = 0; i < count; i++) {
			executor.execute(new Runnable() {
				
				@Override
				public void run() {
					for (int i = 0; i < 10; i++) {
						String hi = Thread.currentThread().getId() + "-hi-" + i;
						String ret = ds.echo(hi);
						if (!hi.equals(ret)) {
							flag.set(false);
						}
					}
					latch.countDown();
				}
			});
		}
		latch.await();
		Assert.assertTrue(flag.get());
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test in multi threads. ", CaseCounter.incr(1)));
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
	public void testMaxAcceptConnections() throws Exception {
		int connections = 100;
		port = AvailablePortFinder.getNextAvailable(33333);
		server = RpcFactory.newRpcServerBuilder(port).connections(connections).build();
		server.open();
		for (int i = 0; i < connections; i++) { new Socket("127.0.0.1", port); }
		
		Thread.sleep(50);
		IoConnector connector = NioFactory.newTcpConnector(new AbstractIoHandler() {});
		Future<Channel<byte[]>> future = connector.connect("127.0.0.1", port);
		Channel<byte[]> channel = future.get(200, TimeUnit.MILLISECONDS);
		Thread.sleep(50);
		Assert.assertFalse(channel.isOpen());
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test max accept connections. ", CaseCounter.incr(1)));
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
		server.export(DemoService.class, new DemoServiceImpl1(), new RpcParameter());
		server.open();
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
		for (int i = 0; i < 100; i++) {
			ds.echo("hi");
		}
		long e = System.nanoTime();
		long rrt = e - s;
		
		// local
		ds = new DemoServiceImpl1();
		s = System.nanoTime();
		for (int i = 0; i < 100; i++) {
			ds.echo("hi");
		}
		e = System.nanoTime();
		long lrt = e - s;
		
		Assert.assertTrue(rrt > lrt);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test rt |local=%s, remote=%s| ns. ", CaseCounter.incr(1), lrt, rrt));
	}
	
	@Test
	public void testMultipleImplementor() {
		server.export("ds2", DemoService.class, new DemoServiceImpl2(), new RpcParameter());
		String hi = ds.echo("hi");
		Assert.assertEquals("hi", hi);
		RpcContext.getContext().setRpcId("ds2");
		String hihi = ds.echo("hi");
		Assert.assertEquals("hihi", hihi);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test multiple implementor. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testAsync() throws Exception {
		// Async two-way
		RpcContext ctx = RpcContext.getContext();
		ctx.setAsync(true);
		String r = ds.echo("hi");
		Assert.assertNull(r);
		Future<String> future = ctx.getFuture();
		r = future.get(2, TimeUnit.SECONDS);
		Assert.assertEquals("hi", r);
		
		// Async one-way
		ctx = RpcContext.getContext();
		ctx.setAsync(true);
		ctx.setOneway(true);
		r = ds.echo("hi");
		Assert.assertNull(r);
		future = ctx.getFuture();
		Assert.assertNull(future);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test async. ", CaseCounter.incr(4)));
	}
	
	@Test
	public void testPartialExported() {
		port = AvailablePortFinder.getNextAvailable(33333);
		server = RpcFactory.newRpcServerBuilder(port).build();
		server.open();
		RpcMethod rpcMethod = new RpcMethod();
		rpcMethod.setName("echo");
		rpcMethod.setParameterTypes(new Class<?>[] { String.class });
		server.export(DemoService.class, rpcMethod, new DemoServiceImpl1(), new RpcParameter(10, 100));
		client = RpcFactory.newRpcClient(host, port);
		client.open();
		ds = client.refer(DemoService.class);
		
		String hi = ds.echo("hi");
		Assert.assertEquals("hi", hi);
		
		try {
			ds.noreturn("hello");
			Assert.fail();
		} catch (RpcException e) {
			Assert.assertEquals(RpcException.SERVER_ERROR, e.getCode());
		}
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test partial exported. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testServerX() {
		ds.echo("hi");
		RpcServerX x = server.x();
		Set<RpcApi> apis = x.apis();	
		Assert.assertEquals(1, x.connectionCount());
		for (RpcApi api : apis) {
			if (api.getMethodName().equals("echo")) {
				Assert.assertEquals(0, x.waitCount(api));
				Assert.assertEquals(0, x.processingCount(api));
				Assert.assertEquals(1, x.completeCount(api));
			}
		}
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test server x. ", CaseCounter.incr(4)));
	}
	
}

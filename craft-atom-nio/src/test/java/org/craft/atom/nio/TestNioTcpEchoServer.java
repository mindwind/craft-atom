package org.craft.atom.nio;

import java.util.concurrent.Future;

import junit.framework.Assert;

import org.craft.atom.io.Channel;
import org.craft.atom.io.IoAcceptor;
import org.craft.atom.io.IoConnector;
import org.craft.atom.nio.api.NioFactory;
import org.craft.atom.nio.api.NioTcpConnector;
import org.craft.atom.test.AvailablePortFinder;
import org.craft.atom.test.CaseCounter;
import org.junit.Test;

/**
 * Tests for nio tcp echo server
 *
 * @author mindwind
 * @version 1.0, 2011-12-19
 */
public class TestNioTcpEchoServer {
	
	
	private static final int PORT = AvailablePortFinder.getNextAvailable();
	
	
	private IoConnector connector;
    
	
	@Test
    public void testHelloMsg() throws Exception {
		NioConnectorHandler handler = new NioConnectorHandler();
		connector = NioFactory.newTcpConnector(handler);
    	String msg = "hello\n";
    	test(msg, PORT);
    	Assert.assertEquals(msg, handler.getRcv().toString());
    	System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test nio tcp echo server hello msg. ", CaseCounter.incr(1)));
    }
    
	@Test
    public void test2048Msg() throws Exception {
    	NioConnectorHandler handler = new NioConnectorHandler();
		connector = new NioTcpConnector(handler);
    	String msg = build(2048);
    	test(msg, PORT);
    	Assert.assertEquals(msg, handler.getRcv().toString());
    	System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test nio tcp echo server 2048 msg. ", CaseCounter.incr(1)));
    }
	
	@Test
    public void test5000Msg() throws Exception {
    	NioConnectorHandler handler = new NioConnectorHandler();
		connector = new NioTcpConnector(handler);
    	String msg = build(5000);
    	test(msg, PORT);
    	Assert.assertEquals(msg, handler.getRcv().toString());
    	System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test nio tcp echo server 5000 msg. ", CaseCounter.incr(1)));
    }
    
	@Test
    public void test98304Msg() throws Exception {
		NioConnectorHandler handler = new NioConnectorHandler();
		connector = new NioTcpConnector(handler);
    	String msg = build(98304);
    	test(msg, PORT);
    	Assert.assertEquals(msg, handler.getRcv().toString());
    	System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test nio tcp echo server 98304 msg. ", CaseCounter.incr(1)));
    }
    
	@Test
    public void test200000Msg() throws Exception {
		NioConnectorHandler handler = new NioConnectorHandler();
		connector = new NioTcpConnector(handler);
    	String msg = build(200000);
    	test(msg, PORT);
    	Assert.assertEquals(msg, handler.getRcv().toString());
    	System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test nio tcp echo server 200000 msg. ", CaseCounter.incr(1)));
    }
    
    private void test(String msg, int port) throws Exception {
    	IoAcceptor acceptor = NioFactory.newTcpAcceptor(new NioAcceptorHandler());
    	acceptor.bind(port);
    	
    	Future<Channel<byte[]>> future = connector.connect("127.0.0.1", port);
    	Channel<byte[]> channel = future.get();
    	synchronized(channel) {
    		channel.write(msg.getBytes());
    		channel.wait();
    		acceptor.shutdown();
    	}
    }
    
    private String build(int len) {
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < len - 1; i++) {
			sb.append("1");
		}
    	sb.append("\n");
    	return sb.toString();
    }
	
}

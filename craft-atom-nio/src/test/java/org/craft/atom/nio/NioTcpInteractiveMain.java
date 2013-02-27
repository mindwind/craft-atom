package org.craft.atom.nio;

import java.util.concurrent.Future;

import org.craft.atom.io.Channel;
import org.craft.atom.nio.api.NioTcpAcceptor;
import org.craft.atom.nio.api.NioTcpConnector;

/**
 * Tests for TCP client server interactive communication.
 *
 * @author mindwind
 * @version 1.0, 2011-12-19
 */
public class NioTcpInteractiveMain {
	
	private static final int PORT = 9988;
	private NioTcpConnector connector;
	
	public static void main(String[] args) throws Exception {
		NioTcpInteractiveMain tim = new NioTcpInteractiveMain();
		tim.setUp();
		
		// case 1
		tim.testHello();
		
		// case 2
		tim.testCriticalValue2048();
		
		// case 3
		tim.testCriticalValue3072();
		
		// case 4
		tim.testCriticalValue98304();
		
		// case 5
		for (int i = 0; i < 3; i++) {
			tim.testCriticalValue200000();
		}
	}
	
    public void setUp() throws Exception {
        connector =  new NioTcpConnector(new NioConnectorHandler());
    }
    
    public void testHello() throws Exception {
    	String msg = "hello\n";
    	test("testHello", msg, PORT);
    }
    
    public void testCriticalValue2048() throws Exception {
    	String msg = build(2048);
    	test("testCriticalValue2048", msg, PORT + 1);
    }
    
    public void testCriticalValue3072() throws Exception {
    	String msg = build(5000);
    	test("testCriticalValue3072", msg, PORT + 2);
    }
    
    public void testCriticalValue98304() throws Exception {
    	String msg = build(98304);
    	test("testCriticalValue98304", msg, PORT + 3);
    }
    
    public void testCriticalValue200000() throws Exception {
    	String msg = build(200000);
    	test("testCriticalValue200000", msg, PORT + 4);
    }
    
    private void test(String desc, String msg, int port) throws Exception {
    	NioTcpAcceptor acceptor = new NioTcpAcceptor(new NioAcceptorHandler(), port);
    	
    	Future<Channel<byte[]>> future = connector.connect("127.0.0.1", port);
    	Channel<byte[]> channel = future.get();
    	synchronized(channel) {
    		long s = System.currentTimeMillis();
    		channel.write(msg.getBytes());
    		channel.wait();
    		long e = System.currentTimeMillis();
    		acceptor.shutdown();
    		System.out.println("Test case=" + desc + " finished, elapse: " + (e - s) + "ms \n");
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

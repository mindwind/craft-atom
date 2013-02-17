package org.craft.atom.nio;

import java.util.concurrent.Future;

import org.craft.atom.nio.api.Connector;
import org.craft.atom.nio.api.Session;
import org.craft.atom.nio.api.TcpAcceptor;

/**
 * Tests for TCP client server interactive communication.
 *
 * @author Hu Feng
 * @version 1.0, 2011-12-19
 */
public class TcpInteractiveMain {
	
	private static final int PORT = 9988;
	private Connector connector;
	
	public static void main(String[] args) throws Exception {
		TcpInteractiveMain tim = new TcpInteractiveMain();
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
		tim.testCriticalValue200000();
	}
	
    public void setUp() throws Exception {
         connector =  new Connector(new NothingHandler());
    }
    
    public void testHello() throws Exception {
    	String msg = "hello!";
    	test(msg, PORT);
    }
    
    public void testCriticalValue2048() throws Exception {
    	String msg = build(2048);
    	test(msg, PORT + 1);
    }
    
    public void testCriticalValue3072() throws Exception {
    	String msg = build(5000);
    	test(msg, PORT + 2);
    }
    
    public void testCriticalValue98304() throws Exception {
    	String msg = build(98304);
    	test(msg, PORT + 3);
    }
    
    public void testCriticalValue200000() throws Exception {
    	String msg = build(200000);
    	test(msg, PORT + 4);
    }
    
    private void test(String msg, int port) throws Exception {
    	TcpAcceptor acceptor = new TcpAcceptor(new MyHandler(), port);
    	Future<Session> future = connector.connect("127.0.0.1", port);
    	Session session = future.get();
    	synchronized(session) {
    		session.write(msg.getBytes());
    		session.wait();
    		acceptor.shutdown();
    	}
    }
    
    private String build(int len) {
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < len - 1; i++) {
			sb.append("1");
		}
    	sb.append("!");
    	return sb.toString();
    }
	
}

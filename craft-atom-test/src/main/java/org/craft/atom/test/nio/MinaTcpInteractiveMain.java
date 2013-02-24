package org.craft.atom.test.nio;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 * @author mindwind
 * @version 1.0, Feb 24, 2013
 */
public class MinaTcpInteractiveMain {

	private static final int PORT = 9992;
	private static final int MAX_LINE_LEN = 1000*10000;
	private NioSocketConnector connector;

	public static void main(String[] args) throws Exception {
		MinaTcpInteractiveMain tim = new MinaTcpInteractiveMain();
		tim.setUp();
		tim.testCriticalValue200000();
	}
	
	public void testCriticalValue200000() throws Exception {
    	String msg = build(200*1000 + 1);
    	test("testCriticalValue200000", msg, PORT);
    }

	public void setUp() throws Exception {
		connector = new NioSocketConnector();
		TextLineCodecFactory factory = new TextLineCodecFactory();
		factory.setEncoderMaxLineLength(MAX_LINE_LEN);
		factory.setDecoderMaxLineLength(MAX_LINE_LEN);
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(factory));
		connector.setHandler(new MinaConnectorHandler());
	}
	
	private void test(String desc, String msg, int port) throws Exception {
    	NioSocketAcceptor acceptor = new NioSocketAcceptor();
    	TextLineCodecFactory factory = new TextLineCodecFactory();
    	factory.setEncoderMaxLineLength(MAX_LINE_LEN);
		factory.setDecoderMaxLineLength(MAX_LINE_LEN);
    	acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(factory));
    	acceptor.setHandler(new MinaAcceptorHandler());
    	SocketAddress address = new InetSocketAddress("127.0.0.1", port);
    	acceptor.bind(address);
    	
    	ConnectFuture future = connector.connect(address);
    	future.awaitUninterruptibly();  
    	IoSession session = future.getSession();
    	synchronized(session) {
    		long s = System.currentTimeMillis();
    		session.write(msg);
    		session.wait();
    		long e = System.currentTimeMillis();
    		acceptor.dispose();
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
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	private class MinaConnectorHandler extends IoHandlerAdapter {
		
		@Override
		public void messageReceived(IoSession session, Object message) throws Exception {
			byte[] bytes = ((String) message).getBytes();
			System.out.println("[Mina Connector Handler] read bytes size=" + bytes.length);
			
			synchronized(session) {
				session.notifyAll();
			}
		}
	}
	
	private class MinaAcceptorHandler extends IoHandlerAdapter {

		@Override
		public void messageReceived(IoSession session, Object message) throws Exception {
			byte[] bytes = ((String) message).getBytes();
			System.out.println("[Mina Acceptor Handler] read bytes size=" + bytes.length);
			session.write(message);
		}

	}
	
}

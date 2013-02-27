package org.craft.atom.nio;

import java.util.concurrent.Future;

import org.craft.atom.io.Channel;
import org.craft.atom.nio.api.NioAcceptorConfig;
import org.craft.atom.nio.api.NioConnectorConfig;
import org.craft.atom.nio.api.NioTcpAcceptor;
import org.craft.atom.nio.api.NioTcpConnector;

/**
 * Tests for TCP client server interactive communication.
 *
 * @author mindwind
 * @version 1.0, 2011-12-19
 */
public class NioTcpInteractiveOverloadProtecionMain {
	
	private static final int PORT = 9988;
	private NioTcpConnector connector;
	
	public static void main(String[] args) throws Exception {
		NioTcpInteractiveOverloadProtecionMain tim = new NioTcpInteractiveOverloadProtecionMain();
		tim.setUp();
		
		// case 1
		tim.test();
	}
	
    public void setUp() throws Exception {
    	NioConnectorConfig config = new NioConnectorConfig();
    	config.setTotalEventSize(1);
    	config.setChannelEventSize(1);
    	connector =  new NioTcpConnector(new NioConnectorHandler(), config);
    }
 
    public void test() throws Exception {
    	String msg = build(200000);
    	test("test", msg, PORT);
    }
    
    private void test(String desc, String msg, int port) throws Exception {
    	NioAcceptorConfig config = new NioAcceptorConfig();
    	config.setTotalEventSize(1);
    	config.setChannelEventSize(1);
    	NioTcpAcceptor acceptor = new NioTcpAcceptor(new NioAcceptorNapHandler(), config, port);
    	
    	Future<Channel<byte[]>> future = connector.connect("127.0.0.1", port);
    	Channel<byte[]> channel = future.get();
    	synchronized(channel) {
    		long s = System.currentTimeMillis();
    		boolean b = false;
    		while(!b) {
    			b = channel.write(msg.getBytes());
    		}
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

package org.craft.atom.nio;

import java.util.concurrent.Future;

import junit.framework.Assert;

import org.craft.atom.io.Channel;
import org.craft.atom.nio.api.NioAcceptorConfig;
import org.craft.atom.nio.api.NioConnectorConfig;
import org.craft.atom.nio.api.NioTcpAcceptor;
import org.craft.atom.nio.api.NioTcpConnector;
import org.craft.atom.test.AvailablePortFinder;
import org.craft.atom.test.CaseCounter;
import org.junit.Test;

/**
 * @author mindwind
 * @version 1.0, 2011-12-19
 */
public class TestNioTcpEchoServerForOverloadProtection {
	
	private static final int PORT = AvailablePortFinder.getNextAvailable();
	
	
	private NioTcpConnector connector;
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
 
	@Test
    public void test() throws Exception {
    	NioConnectorConfig config = new NioConnectorConfig();
    	config.setTotalEventSize(1);
    	config.setChannelEventSize(1);
    	NioConnectorHandler handler = new NioConnectorHandler();
    	connector = new NioTcpConnector(handler, config);
    	
    	String msg = build(20000);
    	test(msg, PORT);
    	Assert.assertEquals(msg, handler.getRcv().toString());
    	System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test nio tcp echo server for overload protection. ", CaseCounter.incr(1)));
    }
    
    private void test(String msg, int port) throws Exception {
    	NioAcceptorConfig config = new NioAcceptorConfig();
    	config.setTotalEventSize(1);
    	config.setChannelEventSize(1);
    	NioTcpAcceptor acceptor = new NioTcpAcceptor(new NioAcceptorNapHandler(), config, new NioOrderedDirectChannelEventDispatcher(), port);
    	
    	Future<Channel<byte[]>> future = connector.connect("127.0.0.1", port);
    	Channel<byte[]> channel = future.get();
    	synchronized(channel) {
    		boolean b = false;
    		while(!b) {
    			b = channel.write(msg.getBytes());
    		}
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

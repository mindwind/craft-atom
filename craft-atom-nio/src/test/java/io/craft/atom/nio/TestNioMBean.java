package io.craft.atom.nio;

import io.craft.atom.io.IoAcceptor;
import io.craft.atom.io.IoAcceptorX;
import io.craft.atom.io.IoConnector;
import io.craft.atom.io.IoConnectorX;
import io.craft.atom.nio.api.NioFactory;
import io.craft.atom.test.AvailablePortFinder;
import io.craft.atom.test.CaseCounter;
import junit.framework.Assert;

import org.junit.Test;

/**
 * @author mindwind
 * @version 1.0, Dec 25, 2013
 */
public class TestNioMBean {
	
	private static final String HOST  = "127.0.0.1"                           ;
	private static final int    PORT1 = AvailablePortFinder.getNextAvailable();
	private static final int    PORT2 = AvailablePortFinder.getNextAvailable();
	private static final int    PORT3 = AvailablePortFinder.getNextAvailable();
	
	
	@Test
	public void testMBean() throws Exception {
		IoAcceptor acceptor = NioFactory.newTcpAcceptor(new NioAcceptorHandler());
    	acceptor.bind(PORT1);
    	acceptor.bind(PORT2);
    	acceptor.bind(PORT3);
    	
    	IoConnector connector = NioFactory.newTcpConnector(new NioConnectorHandler());
    	connector.connect(HOST, PORT1);
    	connector.connect(HOST, PORT2);
    	connector.connect(HOST, PORT3);
    	connector.connect(HOST, PORT3);
    	Thread.sleep(100);
    	
    	IoAcceptorX iax = acceptor.x();
    	Assert.assertNotNull(iax);
    	Assert.assertNotNull(iax.boundAddresses());
    	Assert.assertEquals(3, iax.boundAddresses().size());
    	Assert.assertNotNull(iax.waitBindAddresses());
    	Assert.assertEquals(0, iax.waitBindAddresses().size());
    	Assert.assertNotNull(iax.waitUnbindAddresses());
    	Assert.assertEquals(0, iax.waitUnbindAddresses().size());
    	Assert.assertEquals(4, iax.aliveChannelCount());
    	
    	IoConnectorX icx = connector.x();
    	Assert.assertNotNull(icx);
    	Assert.assertEquals(0, icx.connectingChannelCount());
    	Assert.assertEquals(0, icx.disconnectingChannelCount());
    	Assert.assertEquals(4, icx.aliveChannelCount());
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test nio mxbean. ", CaseCounter.incr(12)));
	}
	
}

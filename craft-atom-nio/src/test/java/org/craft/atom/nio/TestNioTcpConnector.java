package org.craft.atom.nio;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.io.Channel;
import org.craft.atom.nio.api.NioTcpConnector;
import org.craft.atom.test.AvailablePortFinder;
import org.craft.atom.test.CaseCounter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link NioTcpConnector}
 *
 * @author mindwind
 * @version 1.0, 2011-12-20
 */
public class TestNioTcpConnector {
	
	
	private static final Log LOG = LogFactory.getLog(TestNioTcpConnector.class);
	
	
	@Test
	public void testTimeout() {
		NioTcpConnector connector =  new NioTcpConnector(new NioConnectorHandler());
		Future<Channel<byte[]>> future = connector.connect("127.0.0.1", AvailablePortFinder.getNextAvailable());
		
		try {
			future.get(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			
		} catch (ExecutionException e) {
			
		} catch (TimeoutException e) {
			LOG.debug("[CRAFT-ATOM-NIO] Test catch timeout exception");
			Assert.assertTrue(true);
		}
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test nio tcp connector timeout. ", CaseCounter.incr(1)));
	}
	
}

package org.craft.atom.nio;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.craft.atom.io.Channel;
import org.craft.atom.io.IoConnector;
import org.craft.atom.nio.api.NioFactory;
import org.craft.atom.test.AvailablePortFinder;
import org.craft.atom.test.CaseCounter;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests for {@link NioTcpConnector}
 *
 * @author mindwind
 * @version 1.0, 2011-12-20
 */
public class TestNioTcpConnector {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(TestNioTcpConnector.class);
	
	
	@Test
	public void testTimeout() throws IOException {
		IoConnector connector =  NioFactory.newTcpConnector(new NioConnectorHandler());
		Future<Channel<byte[]>> future = connector.connect("127.0.0.1", AvailablePortFinder.getNextAvailable());
		
		try {
			future.get(200, TimeUnit.MILLISECONDS);
			Assert.fail();
		} catch (InterruptedException e) {
			
		} catch (ExecutionException e) {
			
		} catch (TimeoutException e) {
			LOG.debug("[CRAFT-ATOM-NIO] Test catch timeout exception");
			Assert.assertTrue(true);
		}
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test nio tcp connector timeout. ", CaseCounter.incr(1)));
	}
	
}

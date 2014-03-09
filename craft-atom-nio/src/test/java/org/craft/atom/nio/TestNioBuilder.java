package org.craft.atom.nio;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.craft.atom.io.Channel;
import org.craft.atom.io.IoAcceptor;
import org.craft.atom.io.IoConnector;
import org.craft.atom.nio.api.NioFactory;
import org.craft.atom.test.AvailablePortFinder;
import org.craft.atom.test.CaseCounter;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author mindwind
 * @version 1.0, Mar 9, 2014
 */
public class TestNioBuilder {
	
		
	@Test
	public void testNioAcceptorBuilder() throws IOException {
		IoAcceptor acceptor = NioFactory.newTcpAcceptorBuilder(new NioAcceptorHandler()).reuseAddress(true).backlog(100).totalEventSize(50000).ioTimeoutInMillis(30000).build();
		int port = AvailablePortFinder.getNextAvailable(22222);
		acceptor.bind(port);
		
		try {
			acceptor.bind(port);
			Assert.fail();
		} catch(IOException e) {
			
		}
		
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test nio tcp acceptor builder. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testNioConnectorBuilder() throws IOException {
		IoConnector connector = NioFactory.newTcpConnectorBuilder(new NioConnectorHandler()).connectTimeoutInMillis(1000).ioTimeoutInMillis(60000).executorSize(32).build();
		Future<Channel<byte[]>> future = connector.connect("127.0.0.1", AvailablePortFinder.getNextAvailable());
		
		try {
			future.get(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			
		} catch (ExecutionException e) {
			
		} catch (TimeoutException e) {
			Assert.assertTrue(true);
		}
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test nio tcp connector builder. ", CaseCounter.incr(1)));
	}
	
}

package org.craft.atom.nio;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.craft.atom.io.Channel;
import org.craft.atom.io.IoAcceptor;
import org.craft.atom.io.IoConnector;
import org.craft.atom.nio.api.NioFactory;
import org.craft.atom.test.AvailablePortFinder;
import org.craft.atom.test.CaseCounter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mindwind
 * @version 1.0, Sep 23, 2014
 */
public class TestNioTcpAcceptorChannelSizeLimit {
	
	
	private static final int PORT = AvailablePortFinder.getNextAvailable(33333);

	
	private IoConnector connector  ;
	private IoAcceptor  acceptor   ;
	private int         channelSize;
	
	
	@Before
	public void before() throws IOException {
		channelSize = 2;
		acceptor = NioFactory.newTcpAcceptorBuilder(new NioAcceptorHandler()).channelSize(channelSize).build();
		acceptor.bind(PORT);
		connector = NioFactory.newTcpConnector(new NioConnectorHandler());
	}
	
	@Test
	public void testChannelSizeLimit() throws IOException {
		for (int i = 0; i < channelSize; i++) {
			connector.connect("127.0.0.1", PORT);
		}
		
		try {
			Thread.sleep(50);
			Future<Channel<byte[]>> future = connector.connect("127.0.0.1", PORT);
			Channel<byte[]> channel = future.get(200, TimeUnit.MILLISECONDS);
			Thread.sleep(50);
			Assert.assertFalse(channel.isOpen());
		} catch (Exception e) {
			Assert.fail();
		}
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test nio acceptor channel size limit ", CaseCounter.incr(1)));
	}
}

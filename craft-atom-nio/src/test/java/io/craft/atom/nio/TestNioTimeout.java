package io.craft.atom.nio;

import io.craft.atom.io.AbstractIoHandler;
import io.craft.atom.io.Channel;
import io.craft.atom.io.IoAcceptor;
import io.craft.atom.io.IoHandler;
import io.craft.atom.nio.api.NioFactory;
import io.craft.atom.nio.api.NioTcpAcceptorBuilder;
import io.craft.atom.test.AvailablePortFinder;
import io.craft.atom.test.CaseCounter;
import junit.framework.Assert;

import org.junit.Test;

/**
 * Test for nio timeout config
 * 
 * @author mindwind
 * @version 1.0, Sep 5, 2014
 */
public class TestNioTimeout {
	
	
	@Test
	public void testMaxIntegerTimeout() {
		try {
			int port = AvailablePortFinder.getNextAvailable();
			NioTcpAcceptorBuilder builder = NioFactory.newTcpAcceptorBuilder(new NioAcceptorHandler());
	    	IoAcceptor acceptor = builder.ioTimeoutInMillis(Integer.MAX_VALUE).build();
			acceptor.bind(port);
		} catch (Throwable t) {
			Assert.fail();
		}
		
		Assert.assertTrue(true);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test max integer timeout. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testLessOneHundredMillisecondsTimeout() {
		try {
			int port = AvailablePortFinder.getNextAvailable();
			IoHandler handler = new AbstractIoHandler() {
				@Override
				public void channelIdle(Channel<byte[]> channel) {
					super.channelIdle(channel);
					channel.close();
				}
			};
			NioTcpAcceptorBuilder builder = NioFactory.newTcpAcceptorBuilder(handler);
	    	IoAcceptor acceptor = builder.ioTimeoutInMillis(50).build();
			acceptor.bind(port);
		} catch (Throwable t) {
			Assert.fail();
		}
		
		Assert.assertTrue(true);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test less one hundred milliseconds timeout. ", CaseCounter.incr(1)));
	}
	
}

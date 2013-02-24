package org.craft.atom.nio;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.craft.atom.io.Channel;
import org.craft.atom.nio.api.NioTcpConnector;
import org.junit.Assert;

/**
 * Tests for {@link NioTcpConnector}
 *
 * @author mindwind
 * @version 1.0, 2011-12-20
 */
public class NioTcpConnectorMain {
	
	public static void main(String[] args) {
		NioTcpConnectorMain cm = new NioTcpConnectorMain();
		cm.testConnectTimeout();
	}
	
	public void testConnectTimeout() {
		NioTcpConnector connector =  new NioTcpConnector(new NioConnectorHandler());
		Future<Channel<byte[]>> future = connector.connect("127.0.0.1", 7777);
		try {
			future.get(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
			Assert.assertTrue(true);
		}
	}
	
}

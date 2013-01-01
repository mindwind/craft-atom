package org.craft.atom.nio;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.craft.atom.nio.api.Connector;
import org.craft.atom.nio.api.Session;
import org.junit.Assert;

/**
 * Tests for {@link Connector}
 *
 * @author Hu Feng
 * @version 1.0, 2011-12-20
 */
public class ConnectorMain {
	
	public static void main(String[] args) {
		ConnectorMain cm = new ConnectorMain();
		cm.testConnectTimeout();
	}
	
	public void testConnectTimeout() {
		Connector connector =  new Connector(new NothingHandler());
		Future<Session> future = connector.connect("127.0.0.1", 7777);
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

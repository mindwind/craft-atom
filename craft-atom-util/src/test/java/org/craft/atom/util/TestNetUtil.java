package org.craft.atom.util;

import java.io.IOException;
import java.net.ServerSocket;

import junit.framework.Assert;

import org.craft.atom.test.AvailablePortFinder;
import org.craft.atom.test.CaseCounter;
import org.junit.Test;

/**
 * @author Hu Feng
 * @version 1.0, Dec 31, 2012
 */
public class TestNetUtil {
	
	
	@Test
	public void testIsPortUsing() throws IOException {
		int port = AvailablePortFinder.getNextAvailable();
		boolean isUsing = NetUtil.isPortUsing("127.0.0.1", port);
		Assert.assertFalse(isUsing);
		
		port = 6666;
		ServerSocket ss = new ServerSocket(port);
		isUsing = NetUtil.isPortUsing("127.0.0.1", port);
		Assert.assertTrue(isUsing);
		ss.close();
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> test is port using. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testGetIpv4Address() throws IOException {
		String addr = NetUtil.getIpv4Address().getHostAddress();
		Assert.assertNotNull(addr);
		addr = NetUtil.getIpv4Address("11.24").getHostAddress();
		Assert.assertNotNull(addr);
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> test get ipv4 address. ", CaseCounter.incr(2)));
	}
	
}

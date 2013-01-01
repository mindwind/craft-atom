package org.craft.atom.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hu Feng
 * @version 1.0, Dec 31, 2012
 */
public class NetUtilMain {
	
	public static void main(String[] args) throws IOException {
//		testGetIpv4Address();
		testIsPortUsing();
	}
	
	static void testIsPortUsing() {
		System.out.println("test result=" + NetUtil.isPortUsing("127.0.0.1", 6060));
	}
	
	static void testGetIpv4Address() throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < 10000; i++) {
			long s = System.nanoTime();
			String addr = map.get("ip");
			if (addr != null) {
				addr = NetUtil.getIpv4Address("10.28").getHostAddress();
				map.put("ip", addr);
			}
			long e = System.nanoTime();
			System.out.println( addr + ", elapse: " + ( e - s ) + " ns");
		}
	}
	
}

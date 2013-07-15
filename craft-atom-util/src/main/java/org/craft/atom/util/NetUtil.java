package org.craft.atom.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;

/**
 * Net util class
 * 
 * @author Hu Feng
 * @version 1.0, 2011-9-6
 */
public class NetUtil {

	/**
	 * Get local host network interface ip(version 4) address. if has multiple addresses return the first randomly.
	 * if has not bind any ip address return loopback address.
	 * 
	 * @return local host ipv4 address
	 * @throws IOException
	 */
	public static InetAddress getIpv4Address() throws IOException {
		return getIpv4Address(null);
	}
	
	/**
	 * Get network interface ip(version 4) address. if has multiple addresses return the first match prefix address.
	 * 
	 * @param prefix
	 * @return local host ipv4 address match the prefix
	 * @throws IOException
	 */
	public static InetAddress getIpv4Address(String prefix) throws IOException {
		Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress addr = null;
		boolean found = false;
		while (netInterfaces.hasMoreElements()) {
			NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
			Enumeration<InetAddress> addrs = ni.getInetAddresses();
			while (addrs.hasMoreElements()) {
				addr = addrs.nextElement();
				if (!addr.isLoopbackAddress() && addr.getHostAddress().indexOf(":") == -1) {
					if (prefix == null) {
						found = true;
						break;
					}
					if (prefix != null && addr.getHostAddress().startsWith(prefix)) {	
						found = true;
						break;
					}
				} else {
					addr = null;
				}
			}
			
			if (found) { break; }
		}

		if (addr == null) {
			addr = InetAddress.getLocalHost();
		}

		return addr;
	}
	
	/**
	 * Check local port is in using
	 * 
	 * @param port
	 * @return true if in using, otherwise false.
	 */
	public static boolean isLocalPortUsing(int port) {
		return isPortUsing("127.0.0.1", port);
	}
	
	public static boolean isPortUsing(String host, int port) {
		try {
			new Socket(host, port);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------

	private NetUtil() {
		super();
	}

}

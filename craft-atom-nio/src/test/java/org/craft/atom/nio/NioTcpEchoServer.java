package org.craft.atom.nio;

import org.craft.atom.nio.api.NioTcpAcceptor;

/**
 * @author mindwind
 * @version 1.0, 2011-12-20
 */
public class NioTcpEchoServer {
	
	public static void main(String[] args) {
		new NioTcpAcceptor(new NioEchoHandler(), 7777);
	}
	
}

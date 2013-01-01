package org.craft.atom.nio;

import org.craft.atom.nio.api.TcpAcceptor;

/**
 * Echo Server
 *
 * @author Hu Feng
 * @version 1.0, 2011-12-20
 */
public class EchoServer {
	
	public static void main(String[] args) {
		new TcpAcceptor(new EchoHandler(), 7777);
	}
	
}

package org.craft.atom.nio;

import java.io.IOException;

import org.craft.atom.io.IoAcceptor;
import org.craft.atom.nio.api.NioFactory;

/**
 * @author mindwind
 * @version 1.0, Mar 15, 2014
 */
public class NioEchoServer {

	private int port;
	
	public NioEchoServer(int port) {
		this.port = port;
	}

	public void start() throws IOException {
		IoAcceptor acceptor = NioFactory.newTcpAcceptorBuilder(new NioEchoServerHandler()).build();
		acceptor.bind(port);
	}
	
	public static void main(String[] args) throws IOException {
		new NioEchoServer(1314).start();
	}

}

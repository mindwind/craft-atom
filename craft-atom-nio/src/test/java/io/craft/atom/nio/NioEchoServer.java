package io.craft.atom.nio;

import io.craft.atom.io.IoAcceptor;
import io.craft.atom.nio.api.NioFactory;

import java.io.IOException;


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

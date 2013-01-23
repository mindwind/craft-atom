package org.craft.atom.test.nio;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * @author Hu Feng
 * @version 1.0, Jan 22, 2013
 */
public class MinaEchoServer {
	
	private static final int PORT = 9123;
	
	public static void main(String[] args) throws IOException {
		IoAcceptor acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Runtime.getRuntime().availableProcessors() * 2)); 
		acceptor.setHandler(new MinaEchoHandler());
		acceptor.getSessionConfig().setReadBufferSize(2048);
		acceptor.bind(new InetSocketAddress(PORT));
		
		System.out.println("mina echo server listening on port=" + PORT);
	}
	
}

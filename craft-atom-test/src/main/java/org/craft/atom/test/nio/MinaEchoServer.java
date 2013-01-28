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
		String ioPool = System.getProperty("io.pool");
		if (ioPool == null) {
			ioPool = "5";
		}
		
		String executorPool = System.getProperty("executor.pool");
		if (executorPool == null) {
			executorPool = Integer.toString(Runtime.getRuntime().availableProcessors());
		}
		
		IoAcceptor acceptor = new NioSocketAcceptor(Integer.parseInt(ioPool));
		acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Integer.parseInt(executorPool))); 
		acceptor.setHandler(new MinaEchoHandler());
		acceptor.getSessionConfig().setReadBufferSize(2048);
		acceptor.bind(new InetSocketAddress(PORT));
		
		System.out.println("mina echo server listening on port=" + PORT);
	}
	
}

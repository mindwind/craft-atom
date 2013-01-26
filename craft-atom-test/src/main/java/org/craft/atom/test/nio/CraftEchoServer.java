package org.craft.atom.test.nio;

import java.io.IOException;

import org.craft.atom.nio.api.AcceptorConfig;
import org.craft.atom.nio.api.TcpAcceptor;

/**
 * @author Hu Feng
 * @version 1.0, Jan 22, 2013
 */
public class CraftEchoServer {
	
	private static final int PORT = 9123;
	
	public static void main(String[] args) throws IOException {
		String rbs = System.getProperty("read.buffer.size");
		if (rbs == null) {
			rbs = "1024";
		}
		
		String fairMode = System.getProperty("fair.mode");
		if (fairMode == null) {
			fairMode = "false";
		}
		
		String ioPool = System.getProperty("io.pool");
		if (ioPool == null) {
			ioPool = "4";
		}
		
		String executorPool = System.getProperty("executor.pool");
		if (executorPool == null) {
			executorPool = Integer.toString(Runtime.getRuntime().availableProcessors());
		}
		
		AcceptorConfig ac = new AcceptorConfig();
		ac.setReadBufferSize(Integer.parseInt(rbs));
		ac.setReadWritefair(Boolean.parseBoolean(fairMode));
		ac.setProcessorPoolSize(Integer.parseInt(ioPool));
		ac.setExecutorSize(Integer.parseInt(executorPool));
		new TcpAcceptor(new CraftEchoHandler(), ac, PORT);
		System.out.println("craft echo server listening on port=" + PORT);
	}
	
}

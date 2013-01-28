package org.craft.atom.test.nio;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;

/**
 * @author Hu Feng
 * @version 1.0, Jan 23, 2013
 */
public class NettyEchoServer {
	
	private static final int PORT = 9123;

	public static void main(String[] args) {
		String ioPool = System.getProperty("io.pool");
		if (ioPool == null) {
			ioPool = "1";
		}
		
		String executorPool = System.getProperty("executor.pool");
		if (executorPool == null) {
			executorPool = Integer.toString(Runtime.getRuntime().availableProcessors());
		}
		
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Integer.parseInt(ioPool), new NioWorkerPool(Executors.newCachedThreadPool(), Integer.parseInt(executorPool)));
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		NettyEchoHandler handler = new NettyEchoHandler();
		ChannelPipeline pipeline = bootstrap.getPipeline();
		pipeline.addLast("handler", handler);
		bootstrap.setOption("child.receiveBufferSize", 2048);
		bootstrap.bind(new InetSocketAddress(PORT));
		
		System.out.println("netty echo server listening on port=" + PORT);
	}

}

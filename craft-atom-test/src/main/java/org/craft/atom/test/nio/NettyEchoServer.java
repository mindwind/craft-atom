package org.craft.atom.test.nio;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogLevel;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Log4JLoggerFactory;

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
		
		InternalLoggerFactory.setDefaultFactory(new Log4JLoggerFactory());
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Integer.parseInt(ioPool), new NioWorkerPool(Executors.newCachedThreadPool(), Integer.parseInt(executorPool)));
		ExecutionHandler executionHandler = new ExecutionHandler(new OrderedMemoryAwareThreadPoolExecutor(Integer.parseInt(executorPool), 0, 0));
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		NettyEchoHandler handler = new NettyEchoHandler();
		ChannelPipeline pipeline = bootstrap.getPipeline();
		pipeline.addFirst("executor", executionHandler);  
		pipeline.addLast("logger", new LoggingHandler(InternalLogLevel.DEBUG));
		pipeline.addLast("handler", handler);
		bootstrap.setOption("child.receiveBufferSize", 2048);
		bootstrap.bind(new InetSocketAddress(PORT));
		
		System.out.println("netty echo server listening on port=" + PORT);
	}

}

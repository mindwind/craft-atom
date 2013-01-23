package org.craft.atom.test.nio;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * @author Hu Feng
 * @version 1.0, Jan 23, 2013
 */
public class NettyEchoServer {
	
	private static final int PORT = 9123;

	public static void main(String[] args) {
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		NettyEchoHandler handler = new NettyEchoHandler();
		ChannelPipeline pipeline = bootstrap.getPipeline();
		pipeline.addLast("handler", handler);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.bind(new InetSocketAddress(PORT));
		
		System.out.println("netty echo server listening on port=" + PORT);
	}

}

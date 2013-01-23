package org.craft.atom.test.nio;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * @author Hu Feng
 * @version 1.0, Jan 23, 2013
 */
public class NettyEchoHandler extends SimpleChannelHandler {
	
	@Override  
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {  
		Channel channel = e.getChannel();  
		channel.write(e.getMessage());
    }  
	
}

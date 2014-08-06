package org.craft.atom.rpc;


import org.craft.atom.io.Channel;
import org.craft.atom.io.IoHandler;
import org.craft.atom.rpc.spi.RpcTransporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Aug 6, 2014
 */
public class DefaultRpcTransporter implements RpcTransporter {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRpcTransporter.class);
	

	@Override
	public void bind(String ip, int port) {
		
	}
	
	private static class RpcServerIoHandler implements IoHandler {

		@Override
		public void channelOpened(Channel<byte[]> channel) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void channelRead(Channel<byte[]> channel, byte[] bytes) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void channelClosed(Channel<byte[]> channel) {
			channel.close();
		}

		@Override
		public void channelIdle(Channel<byte[]> channel) {
			channel.close();
		}
		
		@Override
		public void channelThrown(Channel<byte[]> channel, Throwable cause) {
			LOG.info("[CRAFT-ATOM-RPC] Rpc server handle throw, |Channel={} thrown={}|");
			channel.close();
		}

		@Override
		public void channelFlush(Channel<byte[]> channel, byte[] bytes) {}

		@Override
		public void channelWritten(Channel<byte[]> channel, byte[] bytes) {}
		
	}

}

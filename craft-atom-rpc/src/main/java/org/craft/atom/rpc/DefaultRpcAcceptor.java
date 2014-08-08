package org.craft.atom.rpc;


import java.io.IOException;
import java.net.InetSocketAddress;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.io.IoAcceptor;
import org.craft.atom.io.IoHandler;
import org.craft.atom.nio.NioOrderedDirectChannelEventDispatcher;
import org.craft.atom.nio.api.NioFactory;
import org.craft.atom.rpc.spi.RpcAcceptor;
import org.craft.atom.rpc.spi.RpcProcessor;
import org.craft.atom.rpc.spi.RpcProtocol;

/**
 * @author mindwind
 * @version 1.0, Aug 6, 2014
 */
public class DefaultRpcAcceptor implements RpcAcceptor {
	
	
	@Getter @Setter private RpcProcessor processor;
	@Getter @Setter private RpcProtocol  protocol ;


	@Override
	public void bind(String host, int port, int ioTimeoutInMillis) throws IOException {
		IoHandler  handler  = new RpcServerIoHandler(protocol, processor);
		IoAcceptor acceptor = NioFactory.newTcpAcceptorBuilder(handler)
										.ioTimeoutInMillis(ioTimeoutInMillis)
										.dispatcher(new NioOrderedDirectChannelEventDispatcher())
										.build();
		if (host != null) {
			acceptor.bind(new InetSocketAddress(host, port));
		} else {
			acceptor.bind(port);
		}
	}

}

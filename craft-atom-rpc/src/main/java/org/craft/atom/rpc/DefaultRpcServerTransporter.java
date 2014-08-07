package org.craft.atom.rpc;


import java.io.IOException;
import java.net.InetSocketAddress;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.io.IoAcceptor;
import org.craft.atom.io.IoHandler;
import org.craft.atom.nio.api.NioFactory;
import org.craft.atom.rpc.spi.RpcProtocol;
import org.craft.atom.rpc.spi.RpcServerTransporter;

/**
 * @author mindwind
 * @version 1.0, Aug 6, 2014
 */
public class DefaultRpcServerTransporter implements RpcServerTransporter {
	
	
	@Getter @Setter private IoAcceptor  acceptor ;
	@Getter @Setter private IoHandler   handler  ;
	@Getter @Setter private RpcProtocol protocol ;


	@Override
	public void bind(String host, int port, int ioTimeoutInMillis) throws IOException {
		handler = new RpcServerIoHandler(protocol);
		acceptor = NioFactory.newTcpAcceptorBuilder(handler).ioTimeoutInMillis(ioTimeoutInMillis).build();
		if (host != null) {
			acceptor.bind(new InetSocketAddress(host, port));
		} else {
			acceptor.bind(port);
		}
	}

}

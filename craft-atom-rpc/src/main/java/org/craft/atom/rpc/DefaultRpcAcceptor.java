package org.craft.atom.rpc;


import java.io.IOException;
import java.net.SocketAddress;

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
	
	
	@Getter @Setter private int           ioTimeoutInMillis;
	@Getter @Setter private int           connections      ;
	@Getter @Setter private SocketAddress address          ;
	@Getter @Setter private RpcProcessor  processor        ;
	@Getter @Setter private RpcProtocol   protocol         ;
	@Getter @Setter private IoHandler     ioHandler        ;
	@Getter @Setter private IoAcceptor    ioAcceptor       ;
	
	
	// ~ ------------------------------------------------------------------------------------------------------------

	
	public DefaultRpcAcceptor() {}
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	

	@Override
	public void bind() throws IOException {
		ioHandler  = new RpcServerIoHandler(protocol, processor);
		ioAcceptor = NioFactory.newTcpAcceptorBuilder(ioHandler)
							   .channelSize(connections)
				               .ioTimeoutInMillis(ioTimeoutInMillis)
				               .dispatcher(new NioOrderedDirectChannelEventDispatcher())
				               .build();
		ioAcceptor.bind(address);
	}


	@Override
	public int connectionCount() {
		return ioAcceptor.x().aliveChannelCount();
	}


	@Override
	public void close() {
		ioAcceptor.shutdown();
	}

}

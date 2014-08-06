package org.craft.atom.rpc;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.rpc.api.RpcServer;
import org.craft.atom.rpc.spi.RpcTransporter;

/**
 * @author mindwind
 * @version 1.0, Aug 6, 2014
 */
public class DefaultRpcServer implements RpcServer {
	
	
	@Getter @Setter private String         ip         ;
	@Getter @Setter private int            port       ;
	@Getter @Setter private RpcTransporter transporter;

	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public void serve() {
		transporter.bind(ip, port);
	}

	@Override
	public void register(Class<?> rpcInterface, Object rpcObject) {
		// TODO Auto-generated method stub

	}

}

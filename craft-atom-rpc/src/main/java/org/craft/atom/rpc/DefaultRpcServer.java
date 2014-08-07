package org.craft.atom.rpc;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.rpc.api.RpcServer;
import org.craft.atom.rpc.spi.RpcServerTransporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Aug 6, 2014
 */
public class DefaultRpcServer implements RpcServer {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRpcServer.class);
	
	
	@Getter @Setter private String               host                       ;
	@Getter @Setter private int                  port                       ;
	@Getter @Setter private int                  rpcTimeoutInMillis = 10000 ;
	@Getter @Setter private int                  ioTimeoutInMillis  = 300000;
	@Getter @Setter private RpcServerTransporter transporter                ;

	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public void serve() {
		try {
			transporter.bind(host, port, ioTimeoutInMillis);
		} catch (Exception e) {
			LOG.error("[CRAFT-ATOM-RPC] Rpc server start fail, exit!", e);
			System.exit(0);
		}
	}

	@Override
	public void register(Class<?> rpcInterface, Object rpcObject) {
		// TODO Auto-generated method stub

	}

}

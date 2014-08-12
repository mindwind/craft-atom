package org.craft.atom.rpc;

import java.lang.reflect.Method;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.rpc.api.RpcServer;
import org.craft.atom.rpc.spi.RpcAcceptor;
import org.craft.atom.rpc.spi.RpcInvoker;
import org.craft.atom.rpc.spi.RpcProcessor;
import org.craft.atom.rpc.spi.RpcProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Aug 6, 2014
 */
public class DefaultRpcServer implements RpcServer {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRpcServer.class);
	
	
	@Getter @Setter private String               host                      ;
	@Getter @Setter private int                  port                      ;
	@Getter @Setter private int                  ioTimeoutInMillis = 300000;
	@Getter @Setter private RpcAcceptor          acceptor                  ;
	@Getter @Setter private RpcProcessor         processor                 ;
	@Getter @Setter private RpcProtocol          protocol                  ;
	@Getter @Setter private RpcInvoker           invoker                   ;

	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public DefaultRpcServer() {
		invoker   = new DefaultRpcInvoker();
		protocol  = new DefaultRpcProtocol();
		processor = new DefaultRpcProcessor();
		acceptor  = new DefaultRpcAcceptor();
		acceptor .setProcessor(processor);
		acceptor .setProtocol(protocol);
		processor.setInvoker(invoker);
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public void serve() {
		try {
			acceptor.bind(host, port, ioTimeoutInMillis);
		} catch (Exception e) {
			LOG.error("[CRAFT-ATOM-RPC] Rpc server start fail, exit!", e);
			System.exit(0);
		}
	}


	@Override
	public void expose(Class<?> rpcInterface, Object rpcObject, int rpcTimeoutInMillis) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void expose(Class<?> rpcInterface, Method rpcMethod, Object rpcObject, int rpcTimeoutInMillis) {
		// TODO Auto-generated method stub
		
	}

}

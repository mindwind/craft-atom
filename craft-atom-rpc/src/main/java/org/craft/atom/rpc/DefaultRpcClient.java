package org.craft.atom.rpc;

import java.io.IOException;
import java.net.InetSocketAddress;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.rpc.api.RpcClient;
import org.craft.atom.rpc.spi.RpcConnector;
import org.craft.atom.rpc.spi.RpcInvoker;
import org.craft.atom.rpc.spi.RpcProtocol;
import org.craft.atom.rpc.spi.RpcProxyFactory;

/**
 * @author mindwind
 * @version 1.0, Aug 14, 2014
 */
public class DefaultRpcClient implements RpcClient {

	
	@Getter @Setter private String          host                  ;
	@Getter @Setter private int             port                  ;
	@Getter @Setter private int             heartbeatInMillis     ;
	@Getter @Setter private int             connectTimeoutInMillis;
	@Getter @Setter private RpcConnector    connector             ; 
	@Getter @Setter private RpcProtocol     protocol              ;
	@Getter @Setter private RpcProxyFactory proxyFactory          ;
	@Getter @Setter private RpcInvoker      invoker               ;
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public DefaultRpcClient() {
		connector              = new DefaultRpcConnector()    ;
		protocol               = new DefaultRpcProtocol()     ;
		proxyFactory           = new DefaultRpcProxyFactory() ;
		invoker                = new DefaultRpcClientInvoker();
		connectTimeoutInMillis = Integer.MAX_VALUE            ;
		init();
	}
	
	public void init() {
		connector.setProtocol(protocol);
		connector.setAddress(new InetSocketAddress(host, port));
		connector.setHeartbeatInMillis(heartbeatInMillis);
		connector.setConnectTimeoutInMillis(connectTimeoutInMillis);
		proxyFactory.setInvoker(invoker);
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public <T> T refer(Class<T> rpcInterface) {
		return proxyFactory.getProxy(rpcInterface);
	}


	@Override
	public long connect() throws IOException {
		return connector.connect();
	}


	@Override
	public void disconnect(long connectionId) throws IOException {
		disconnect(connectionId);
	}

}

package io.craft.atom.rpc.api;

import io.craft.atom.rpc.DefaultRpcClient;
import io.craft.atom.rpc.DefaultRpcClientInvoker;
import io.craft.atom.rpc.DefaultRpcConnector;
import io.craft.atom.rpc.DefaultRpcProtocol;
import io.craft.atom.rpc.DefaultRpcProxyFactory;
import io.craft.atom.rpc.spi.RpcConnector;
import io.craft.atom.rpc.spi.RpcInvoker;
import io.craft.atom.rpc.spi.RpcProtocol;
import io.craft.atom.rpc.spi.RpcProxyFactory;

/**
 * Builder for {@link RpcServer}
 * 
 * @author mindwind
 * @version 1.0, Aug 21, 2014
 */
public class RpcClientBuilder {
	
	
	private String             host                                                  ;
	private int                port                                                  ;
	private int                connections            = 1                            ;
	private int                heartbeatInMillis      = 0                            ;
	private int                connectTimeoutInMillis = Integer.MAX_VALUE            ;
	private int                rpcTimeoutInMillis     = Integer.MAX_VALUE            ;
	private RpcConnector       connector              = new DefaultRpcConnector()    ;
	private RpcProtocol        protocol               = new DefaultRpcProtocol()     ;
	private RpcProxyFactory    proxyFactory           = new DefaultRpcProxyFactory() ;
	private RpcInvoker         invoker                = new DefaultRpcClientInvoker();
	
	
	public RpcClientBuilder host                  (String          host                  ) { this.host                   = host                  ; return this; }
	public RpcClientBuilder port                  (int             port                  ) { this.port                   = port                  ; return this; }
	public RpcClientBuilder connections           (int             connections           ) { this.connections            = connections           ; return this; }
	public RpcClientBuilder heartbeatInMillis     (int             heartbeatInMillis     ) { this.heartbeatInMillis      = heartbeatInMillis     ; return this; }
	public RpcClientBuilder connectTimeoutInMillis(int             connectTimeoutInMillis) { this.connectTimeoutInMillis = connectTimeoutInMillis; return this; }
	public RpcClientBuilder rpcTimeoutInMillis    (int             rpcTimeoutInMillis    ) { this.rpcTimeoutInMillis     = rpcTimeoutInMillis    ; return this; }
	public RpcClientBuilder rpcConnector          (RpcConnector    connector             ) { this.connector              = connector             ; return this; }
	public RpcClientBuilder rpcProtocol           (RpcProtocol     protocol              ) { this.protocol               = protocol              ; return this; }
	public RpcClientBuilder rpcProxyFactory       (RpcProxyFactory proxyFactory          ) { this.proxyFactory           = proxyFactory          ; return this; }
	public RpcClientBuilder rpcInvoker            (RpcInvoker      invoker               ) { this.invoker                = invoker               ; return this; }
	
	
	public RpcClient build() {
		DefaultRpcClient rc = new DefaultRpcClient();
		rc.setHost(host);
		rc.setPort(port);
		rc.setConnections(connections);
		rc.setHeartbeatInMillis(heartbeatInMillis);
		rc.setConnectTimeoutInMillis(connectTimeoutInMillis);
		rc.setRpcTimeoutInMillis(rpcTimeoutInMillis);
		rc.setConnector(connector);
		rc.setProtocol(protocol);
		rc.setProxyFactory(proxyFactory);
		rc.setInvoker(invoker);
		rc.init();
		return rc;
	}
}

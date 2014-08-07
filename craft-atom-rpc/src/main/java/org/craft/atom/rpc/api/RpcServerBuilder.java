package org.craft.atom.rpc.api;

import org.craft.atom.rpc.DefaultRpcServer;

/**
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public class RpcServerBuilder {
	
	
	private String host                       ;
	private int    port                       ;
	private int    rpcTimeoutInMillis = 10000 ;
	private int    ioTimeoutInMillis  = 300000;
	
	
	public RpcServerBuilder host      (String host           ) { this.host               = host              ; return this; }
	public RpcServerBuilder port      (int port              ) { this.port               = port              ; return this; }
	public RpcServerBuilder rpcTimeout(int rpcTimeoutInMillis) { this.rpcTimeoutInMillis = rpcTimeoutInMillis; return this; }
	public RpcServerBuilder ioTimeout (int ioTimeoutInMillis ) { this.ioTimeoutInMillis  = ioTimeoutInMillis ; return this; }
	
	
	public RpcServer build() {
		DefaultRpcServer rs =  new DefaultRpcServer();
		rs.setHost(host);
		rs.setPort(port);
		rs.setIoTimeoutInMillis(ioTimeoutInMillis);
		rs.setRpcTimeoutInMillis(rpcTimeoutInMillis);
		return rs;
	}
	
}

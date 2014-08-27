package org.craft.atom.rpc.api;

import org.craft.atom.rpc.DefaultRpcAcceptor;
import org.craft.atom.rpc.DefaultRpcExecutorFactory;
import org.craft.atom.rpc.DefaultRpcServerInvoker;
import org.craft.atom.rpc.DefaultRpcProcessor;
import org.craft.atom.rpc.DefaultRpcProtocol;
import org.craft.atom.rpc.DefaultRpcServer;
import org.craft.atom.rpc.spi.RpcAcceptor;
import org.craft.atom.rpc.spi.RpcExecutorFactory;
import org.craft.atom.rpc.spi.RpcInvoker;
import org.craft.atom.rpc.spi.RpcProcessor;
import org.craft.atom.rpc.spi.RpcProtocol;

/**
 * Builder for {@link RpcServer}
 * 
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public class RpcServerBuilder {
	
	
	private String             host                                               ;
	private int                port                                               ;
	private int                ioTimeoutInMillis = Integer.MAX_VALUE              ;
	private RpcAcceptor        acceptor          = new DefaultRpcAcceptor()       ;
	private RpcInvoker         invoker           = new DefaultRpcServerInvoker()  ;
	private RpcProtocol        protocol          = new DefaultRpcProtocol()       ;
	private RpcProcessor       processor         = new DefaultRpcProcessor()      ;
	private RpcExecutorFactory executorFactory   = new DefaultRpcExecutorFactory();
	
	
	public RpcServerBuilder host              (String             host             ) { this.host               = host             ; return this; }
	public RpcServerBuilder port              (int                port             ) { this.port               = port             ; return this; }
	public RpcServerBuilder ioTimeoutInMillis (int                ioTimeoutInMillis) { this.ioTimeoutInMillis  = ioTimeoutInMillis; return this; }
	public RpcServerBuilder rpcAcceptor       (RpcAcceptor        acceptor         ) { this.acceptor           = acceptor         ; return this; }
	public RpcServerBuilder rpcInvoker        (RpcInvoker         invoker          ) { this.invoker            = invoker          ; return this; }
	public RpcServerBuilder rpcProtocol       (RpcProtocol        protocol         ) { this.protocol           = protocol         ; return this; }
	public RpcServerBuilder rpcProcessor      (RpcProcessor       processor        ) { this.processor          = processor        ; return this; }
	public RpcServerBuilder rpcExecutorFactory(RpcExecutorFactory executorFactory  ) { this.executorFactory    = executorFactory  ; return this; }
	
	
	public RpcServer build() {
		DefaultRpcServer rs = new DefaultRpcServer();
		rs.setHost(host);
		rs.setPort(port);
		rs.setIoTimeoutInMillis(ioTimeoutInMillis);
		rs.setAcceptor(acceptor);
		rs.setInvoker(invoker);
		rs.setProtocol(protocol);
		rs.setProcessor(processor);
		rs.setExecutorFactory(executorFactory);
		rs.init();
		return rs;
	}
	
}

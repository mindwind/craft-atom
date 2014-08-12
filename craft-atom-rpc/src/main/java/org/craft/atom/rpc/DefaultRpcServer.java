package org.craft.atom.rpc;

import java.lang.reflect.Method;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.protocol.rpc.model.RpcMethod;
import org.craft.atom.protocol.rpc.model.RpcOption;
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
	
	
	@Getter @Setter private String               host             ;
	@Getter @Setter private int                  port             ;
	@Getter @Setter private int                  ioTimeoutInMillis;
	@Getter @Setter private RpcAcceptor          acceptor         ;
	@Getter @Setter private RpcProcessor         processor        ;
	@Getter @Setter private RpcProtocol          protocol         ;
	@Getter @Setter private RpcInvoker           invoker          ;
	@Getter @Setter private RpcRegistry          registry         ;

	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public DefaultRpcServer() {
		ioTimeoutInMillis = 5 * 60 * 1000;
		invoker   = new DefaultRpcInvoker();
		protocol  = new DefaultRpcProtocol();
		processor = new DefaultRpcProcessor();
		acceptor  = new DefaultRpcAcceptor();
		registry  = RpcRegistry.getInstance();
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
	public void expose(Class<?> rpcInterface, Object rpcObject, RpcOption rpcOption) {
		Method[] methods = rpcInterface.getMethods();
		for (Method method : methods) {
			RpcMethod rpcMethod = new RpcMethod();
			rpcMethod.setName(method.getName());
			rpcMethod.setParameterTypes(method.getParameterTypes());
			rpcMethod.setReturnType(method.getReturnType());
			expose(rpcInterface, rpcMethod, rpcObject, rpcOption);
		}
		
	}

	@Override
	public void expose(Class<?> rpcInterface, RpcMethod rpcMethod, Object rpcObject, RpcOption rpcOption) {
		RpcEntry entry = new RpcEntry();
		entry.setRpcInterface(rpcInterface);
		entry.setRpcMethod(rpcMethod);
		entry.setRpcObject(rpcObject);
		entry.setRpcOption(rpcOption);
		String key = registry.key(rpcInterface, rpcMethod);
		registry.register(key, entry);	
	}

}

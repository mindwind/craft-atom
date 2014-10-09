package org.craft.atom.rpc;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.protocol.rpc.model.RpcMethod;
import org.craft.atom.rpc.api.RpcParameter;
import org.craft.atom.rpc.api.RpcServer;
import org.craft.atom.rpc.spi.RpcAcceptor;
import org.craft.atom.rpc.spi.RpcExecutorFactory;
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
	@Getter @Setter private int                  connections      ;
	@Getter @Setter private RpcAcceptor          acceptor         ;
	@Getter @Setter private RpcProcessor         processor        ;
	@Getter @Setter private RpcProtocol          protocol         ;
	@Getter @Setter private RpcInvoker           invoker          ;
	@Getter @Setter private RpcExecutorFactory   executorFactory  ;
	@Getter @Setter private RpcRegistry          registry         ;

	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public DefaultRpcServer() {
		acceptor          = new DefaultRpcAcceptor()       ;
		protocol          = new DefaultRpcProtocol()       ;
		processor         = new DefaultRpcProcessor()      ;
		invoker           = new DefaultRpcServerInvoker()  ;
		executorFactory   = new DefaultRpcExecutorFactory();
		registry          = RpcRegistry.getInstance()      ;
	}
	
	public void init() {
		SocketAddress address = (host == null ? new InetSocketAddress(port) : new InetSocketAddress(host, port));
		acceptor .setProcessor(processor);
		acceptor .setProtocol(protocol);
		acceptor .setIoTimeoutInMillis(ioTimeoutInMillis);
		acceptor.setConnections(connections);
		acceptor .setAddress(address);
		processor.setInvoker(invoker);
		processor.setExecutorFactory(executorFactory);
		LOG.debug("[CRAFT-ATOM-RPC] Rpc server init.");
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public void open() {
		try {
			acceptor.bind();
		} catch (Exception e) {
			LOG.error("[CRAFT-ATOM-RPC] Rpc server start fail, exit!", e);
			System.exit(0);
		}
		LOG.debug("[CRAFT-ATOM-RPC] Rpc server is serving, |host={}, port={}|.", host, port);
	}

	@Override
	public void export(Class<?> rpcInterface, Object rpcObject, RpcParameter rpcParameter) {
		export(null, rpcInterface, rpcObject, rpcParameter);
	}

	@Override
	public void export(Class<?> rpcInterface, RpcMethod rpcMethod, Object rpcObject, RpcParameter rpcParameter) {
		export(null, rpcInterface, rpcMethod, rpcObject, rpcParameter);
	}

	@Override
	public void export(String rpcId, Class<?> rpcInterface, Object rpcObject, RpcParameter rpcParameter) {
		Method[] methods = rpcInterface.getMethods();
		for (Method method : methods) {
			RpcMethod rpcMethod = new RpcMethod();
			rpcMethod.setName(method.getName());
			rpcMethod.setParameterTypes(method.getParameterTypes());
			export(rpcId, rpcInterface, rpcMethod, rpcObject, rpcParameter);
		}
	}

	@Override
	public void export(String rpcId, Class<?> rpcInterface, RpcMethod rpcMethod, Object rpcObject, RpcParameter rpcParameter) {
		RpcEntry entry = new RpcEntry();
		entry.setRpcInterface(rpcInterface);
		entry.setRpcMethod(rpcMethod);
		entry.setRpcObject(rpcObject);
		entry.setRpcParameter(rpcParameter);
		String key = registry.key(rpcId, rpcInterface, rpcMethod);
		registry.register(key, entry);	
		LOG.debug("[CRAFT-ATOM-RPC] Rpc server export, |key={}, entry={}|", key, entry);
	}

}

package org.craft.atom.rpc;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.protocol.rpc.model.RpcMethod;
import org.craft.atom.rpc.api.RpcParameter;
import org.craft.atom.rpc.api.RpcServer;
import org.craft.atom.rpc.api.RpcServerX;
import org.craft.atom.rpc.spi.RpcAcceptor;
import org.craft.atom.rpc.spi.RpcApi;
import org.craft.atom.rpc.spi.RpcExecutorFactory;
import org.craft.atom.rpc.spi.RpcInvoker;
import org.craft.atom.rpc.spi.RpcProcessor;
import org.craft.atom.rpc.spi.RpcProtocol;
import org.craft.atom.rpc.spi.RpcRegistry;
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
		registry          = new DefaultRpcRegistry()       ;
	}
	
	public void init() {
		SocketAddress address = (host == null ? new InetSocketAddress(port) : new InetSocketAddress(host, port));
		executorFactory.setRegistry(registry);
		invoker.setRegistry(registry);
		processor.setInvoker(invoker);
		processor.setExecutorFactory(executorFactory);
		acceptor .setProcessor(processor);
		acceptor .setProtocol(protocol);
		acceptor .setIoTimeoutInMillis(ioTimeoutInMillis);
		acceptor .setConnections(connections);
		acceptor .setAddress(address);
		LOG.debug("[CRAFT-ATOM-RPC] Rpc server init complete.");
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
		LOG.debug("[CRAFT-ATOM-RPC] Rpc server is open for serving, |host={}, port={}|.", host, port);
	}
	
	@Override
	public void close() {
		acceptor.close();
		processor.close();
	}

	@Override
	public void export(Class<?> rpcInterface, Object rpcObject, RpcParameter rpcParameter) {
		export(null, rpcInterface, rpcObject, rpcParameter);
	}

	@Override
	public void export(Class<?> rpcInterface, String rpcMethodName, Class<?>[] rpcMethodParameterTypes, Object rpcObject, RpcParameter rpcParameter) {
		export(null, rpcInterface, rpcMethodName, rpcMethodParameterTypes, rpcObject, rpcParameter);
	}

	@Override
	public void export(String rpcId, Class<?> rpcInterface, Object rpcObject, RpcParameter rpcParameter) {
		Method[] methods = rpcInterface.getMethods();
		for (Method method : methods) {
			export(rpcId, rpcInterface, method.getName(), method.getParameterTypes(), rpcObject, rpcParameter);
		}
	}

	@Override
	public void export(String rpcId, Class<?> rpcInterface, String rpcMethodName, Class<?>[] rpcMethodParameterTypes, Object rpcObject, RpcParameter rpcParameter) {
		DefaultRpcApi api = new DefaultRpcApi(rpcId, rpcInterface, new RpcMethod(rpcMethodName, rpcMethodParameterTypes), rpcObject, rpcParameter);
		registry.register(api);	
		LOG.debug("[CRAFT-ATOM-RPC] Rpc server export, |api={}|", api);
	}

	@Override
	public RpcServerX x() {
		DefaultRpcServerX x = new DefaultRpcServerX();
		Set<RpcApi> apis = registry.apis();
		x.setApis(apis);
		x.setConnectionCount(acceptor.connectionCount());
		Map<String, long[]> counts = new HashMap<String, long[]>();
		for (RpcApi api : apis) {
			int wc = processor.waitCount(api);
			int pc = processor.processingCount(api);
			long cc = processor.completeCount(api);
			counts.put(api.getKey(), new long[] {wc, pc, cc});
		}
		x.setCounts(counts);
		return x;
	}

}

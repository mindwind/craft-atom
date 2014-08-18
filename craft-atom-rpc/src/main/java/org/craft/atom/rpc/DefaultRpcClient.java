package org.craft.atom.rpc;

import java.io.IOException;
import java.net.InetSocketAddress;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.rpc.api.RpcClient;
import org.craft.atom.rpc.spi.RpcConnector;

/**
 * @author mindwind
 * @version 1.0, Aug 14, 2014
 */
public class DefaultRpcClient implements RpcClient {

	
	@Getter @Setter private String       host     ;
	@Getter @Setter private int          port     ;
	@Getter @Setter private RpcConnector connector;
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public DefaultRpcClient() {
		connector = new DefaultRpcConnector();
		init();
	}
	
	public void init() {
		connector.setAddress(new InetSocketAddress(host, port));
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public <T> T refer(Class<T> clazz) {
		// TODO
		return null;
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

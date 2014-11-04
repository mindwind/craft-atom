package io.craft.atom.nio.api;

import io.craft.atom.io.IoConnector;
import io.craft.atom.io.IoHandler;
import io.craft.atom.nio.NioTcpConnector;


/**
 * Builder for {@link NioTcpConnector}
 * 
 * @author mindwind
 * @version 1.0, Mar 7, 2014
 */
public class NioTcpConnectorBuilder extends NioBuilder<IoConnector> {


	private int connectTimeoutInMillis = 2000;
	
	
	public NioTcpConnectorBuilder(IoHandler handler) {
		super(handler);
	}
	
	
	public NioTcpConnectorBuilder connectTimeoutInMillis(int timeout) { this.connectTimeoutInMillis = timeout; return this; }
	
	
	public IoConnector build() {
		NioConnectorConfig config = new NioConnectorConfig();
		config.setConnectTimeoutInMillis(connectTimeoutInMillis);
		set(config);
		return new NioTcpConnector(handler, config, dispatcher, predictorFactory);
	}
}

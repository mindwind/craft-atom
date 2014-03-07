package org.craft.atom.nio.api;

import org.craft.atom.io.IoConnector;
import org.craft.atom.io.IoHandler;
import org.craft.atom.nio.NioTcpConnector;

/**
 * Builder for {@link NioTcpConnector}
 * 
 * @author mindwind
 * @version 1.0, Mar 7, 2014
 */
public class NioTcpConnectorBuilder extends NioBuilder {


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

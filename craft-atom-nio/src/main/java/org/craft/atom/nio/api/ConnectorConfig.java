package org.craft.atom.nio.api;

/**
 * Configuration object for {@link Connector}
 *
 * @author Hu Feng
 * @version 1.0, 2011-11-29
 */
public class ConnectorConfig extends AbstractConfig {
	
	/** 30 seconds as default connect timeout */
	private int connectTimeoutInMillis = 30000;

	public int getConnectTimeoutInMillis() {
		return connectTimeoutInMillis;
	}

	public void setConnectTimeoutInMillis(int connectTimeoutInMillis) {
		this.connectTimeoutInMillis = connectTimeoutInMillis;
	}

	@Override
	public String toString() {
		return super.toString() + ", connectTimeoutInMillis=" + connectTimeoutInMillis;
	} 
	
}

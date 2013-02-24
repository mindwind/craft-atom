package org.craft.atom.nio.api;

import org.craft.atom.nio.NioConfig;
import org.craft.atom.nio.NioConnector;

/**
 * Configuration object for {@link NioConnector}
 * 
 * @author mindwind
 * @version 1.0, Feb 24, 2013
 */
public class NioConnectorConfig extends NioConfig {
	
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
		return String
				.format("NioConnectorConfig [connectTimeoutInMillis=%s, processorPoolSize=%s, executorSize=%s, readWritefair=%s, minReadBufferSize=%s, defaultReadBufferSize=%s, maxReadBufferSize=%s, ioTimeoutInMillis=%s]",
						connectTimeoutInMillis, processorPoolSize,
						executorSize, readWritefair, minReadBufferSize,
						defaultReadBufferSize, maxReadBufferSize,
						ioTimeoutInMillis);
	}
	
}

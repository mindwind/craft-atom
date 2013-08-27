package org.craft.atom.nio.api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.craft.atom.nio.NioConfig;
import org.craft.atom.nio.NioConnector;

/**
 * Configuration object for {@link NioConnector}
 * 
 * @author mindwind
 * @version 1.0, Feb 24, 2013
 */
@ToString(callSuper = true, of = "connectTimeoutInMillis")
public class NioConnectorConfig extends NioConfig {
	
	/** 30 seconds as default connect timeout */
	@Getter @Setter private int connectTimeoutInMillis = 30000;
	
}

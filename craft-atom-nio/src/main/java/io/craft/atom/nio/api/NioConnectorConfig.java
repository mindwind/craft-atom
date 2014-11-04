package io.craft.atom.nio.api;

import io.craft.atom.nio.NioConfig;
import io.craft.atom.nio.NioConnector;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Configuration object for {@link NioConnector}
 * 
 * @author mindwind
 * @version 1.0, Feb 24, 2013
 */
@ToString(callSuper = true, of = "connectTimeoutInMillis")
public class NioConnectorConfig extends NioConfig {
	
	/** 2 seconds as default connect timeout */
	@Getter @Setter private int connectTimeoutInMillis = 2000;
	
}

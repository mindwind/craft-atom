package io.craft.atom.nio.api;

import io.craft.atom.nio.NioConfig;
import io.craft.atom.nio.NioTcpAcceptor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Configuration object for {@link NioTcpAcceptor}
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
@ToString(callSuper = true, of = { "reuseAddress", "backlog" })
public class NioAcceptorConfig extends NioConfig {
	
	
	@Getter @Setter private boolean reuseAddress = true             ;
	@Getter @Setter private int     channelSize  = Integer.MAX_VALUE;
	@Getter         private int     backlog      = 50               ;
	
	
	// ~ ---------------------------------------------------------------------------------------------------------------

	
	public void setBacklog(int backlog) {
		this.backlog = (backlog <= 0 ? 50 : backlog);
	}

}

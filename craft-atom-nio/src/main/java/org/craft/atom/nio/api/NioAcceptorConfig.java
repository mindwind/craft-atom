package org.craft.atom.nio.api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.craft.atom.nio.NioConfig;
import org.craft.atom.nio.NioTcpAcceptor;

/**
 * Configuration object for {@link NioTcpAcceptor}
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
@ToString(callSuper = true, of = { "reuseAddress", "backlog" })
public class NioAcceptorConfig extends NioConfig {
	
	
	@Getter @Setter private boolean reuseAddress = true;
	@Getter         private int     backlog      = 50  ;
	
	
	// ~ ---------------------------------------------------------------------------------------------------------------

	
	public void setBacklog(int backlog) {
		this.backlog = (backlog <= 0 ? 50 : backlog);
	}

}

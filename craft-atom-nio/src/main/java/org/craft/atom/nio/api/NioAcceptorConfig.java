package org.craft.atom.nio.api;

import java.net.ServerSocket;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.craft.atom.nio.NioConfig;

/**
 * Configuration object for {@link NioTcpAcceptor}
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
@ToString(callSuper = true, of = { "reuseAddress", "backlog" })
public class NioAcceptorConfig extends NioConfig {
	
	/** Reuse address flag for Acceptor */
	@Getter @Setter private boolean reuseAddress = true;
	
	/** Define the number of socket that can wait to be accepted. Default to 50, same as the {@link ServerSocket} default */
	@Getter private int backlog = 50;
	
	// ~ ---------------------------------------------------------------------------------------------------------------

	public void setBacklog(int backlog) {
		this.backlog = (backlog <= 0 ? 50 : backlog);
	}

}

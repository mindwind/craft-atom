package org.craft.atom.nio.api;

import org.craft.atom.nio.NioConfig;

/**
 * Configuration object for {@link NioAcceptor}
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
public class NioAcceptorConfig extends NioConfig {
	
	/** Reuse address flag for Acceptor */
	private boolean reuseAddress = true;
	
	/** Define the number of socket that can wait to be accepted. Default to 50, same as the {@link ServerSocket} default */
	private int backlog = 50;
	
	// ~ ---------------------------------------------------------------------------------------------------------------

	public boolean isReuseAddress() {
		return reuseAddress;
	}

	public void setReuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
	}

	public int getBacklog() {
		return backlog;
	}

	public void setBacklog(int backlog) {
		this.backlog = (backlog <= 0 ? 50 : backlog);
	}

	@Override
	public String toString() {
		return String
				.format("NioAcceptorConfig [reuseAddress=%s, backlog=%s, processorPoolSize=%s, executorSize=%s, readWritefair=%s, minReadBufferSize=%s, defaultReadBufferSize=%s, maxReadBufferSize=%s, ioTimeoutInMillis=%s]",
						reuseAddress, backlog, processorPoolSize, executorSize,
						readWritefair, minReadBufferSize,
						defaultReadBufferSize, maxReadBufferSize,
						ioTimeoutInMillis);
	}

}

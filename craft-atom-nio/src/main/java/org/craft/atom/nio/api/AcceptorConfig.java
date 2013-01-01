package org.craft.atom.nio.api;

/**
 * Configuration object for {@literal Acceptor}
 *
 * @author Hu Feng
 * @version 1.0, 2011-11-10
 */
public class AcceptorConfig extends AbstractConfig {
	
	/** reuse address flag for Acceptor */
	private boolean reuseAddress = true;
	
	/** Define the number of socket that can wait to be accepted. Default to 50(same as the SocketServer default) */
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
		return super.toString() + ", reuseAddress=" + reuseAddress + ", backlog=" + backlog;
	}
	
}

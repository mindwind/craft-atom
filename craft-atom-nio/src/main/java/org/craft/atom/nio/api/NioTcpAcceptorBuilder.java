package org.craft.atom.nio.api;

import org.craft.atom.io.IoAcceptor;
import org.craft.atom.io.IoHandler;
import org.craft.atom.nio.NioTcpAcceptor;

/**
 * Builder for {@link NioTcpAcceptor}
 * 
 * @author mindwind
 * @version 1.0, Mar 7, 2014
 */
public class NioTcpAcceptorBuilder extends NioBuilder {
	
	
	private int     backlog      = 50  ;
	private boolean reuseAddress = true;

	
	public NioTcpAcceptorBuilder(IoHandler handler) {
		super(handler);
	}
	
	
	public NioTcpAcceptorBuilder backlog     (int backlog)   { this.backlog = backlog; return this; }
    public NioTcpAcceptorBuilder reuseAddress(boolean reuse) { this.reuseAddress = reuse; return this; }
	
    
	public IoAcceptor build() {
		NioAcceptorConfig config = new NioAcceptorConfig();
		config.setBacklog(backlog);
		config.setReuseAddress(reuseAddress);
		set(config);
		return new NioTcpAcceptor(handler, config, dispatcher, predictorFactory);
	}
	
}

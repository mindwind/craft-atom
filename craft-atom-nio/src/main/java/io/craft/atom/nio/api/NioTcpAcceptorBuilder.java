package io.craft.atom.nio.api;

import io.craft.atom.io.IoAcceptor;
import io.craft.atom.io.IoHandler;
import io.craft.atom.nio.NioTcpAcceptor;


/**
 * Builder for {@link NioTcpAcceptor}
 * 
 * @author mindwind
 * @version 1.0, Mar 7, 2014
 */
public class NioTcpAcceptorBuilder extends NioBuilder<IoAcceptor> {
	
	
	private int     backlog      = 50               ;
	private int     channelSize  = Integer.MAX_VALUE;
	private boolean reuseAddress = true             ;

	
	public NioTcpAcceptorBuilder(IoHandler handler) {
		super(handler);
	}
	
	
	public NioTcpAcceptorBuilder backlog     (int backlog)          { this.backlog      = backlog     ; return this; }
	public NioTcpAcceptorBuilder channelSize (int channelSize)      { this.channelSize  = channelSize ; return this; }
    public NioTcpAcceptorBuilder reuseAddress(boolean reuseAddress) { this.reuseAddress = reuseAddress; return this; }
	
    
	public IoAcceptor build() {
		NioAcceptorConfig config = new NioAcceptorConfig();
		config.setBacklog(backlog);
		config.setChannelSize(channelSize);
		config.setReuseAddress(reuseAddress);
		set(config);
		return new NioTcpAcceptor(handler, config, dispatcher, predictorFactory);
	}
	
}

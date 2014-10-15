package org.craft.atom.nio;

import java.net.SocketAddress;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.craft.atom.io.IoAcceptorX;

/**
 * @author mindwind
 * @version 1.0, Oct 15, 2014
 */
@ToString
public class NioAcceptorX extends NioReactorX implements IoAcceptorX {

	
	@Getter @Setter private Set<SocketAddress> waitBindAddresses  ;
	@Getter @Setter private Set<SocketAddress> waitUnbindAddresses;
	@Getter @Setter private Set<SocketAddress> boundAddresses     ;
	
	
	@Override
	public Set<SocketAddress> waitBindAddresses() {
		return waitBindAddresses;
	}

	@Override
	public Set<SocketAddress> waitUnbindAddresses() {
		return waitUnbindAddresses;
	}

	@Override
	public Set<SocketAddress> boundAddresses() {
		return boundAddresses;
	}

}

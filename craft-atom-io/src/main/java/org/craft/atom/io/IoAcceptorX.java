package org.craft.atom.io;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author mindwind
 * @version 1.0, Dec 24, 2013
 */
@ToString(callSuper = true)
public class IoAcceptorX extends IoReactorX implements Serializable {

	
	private static final long serialVersionUID = 3536608515158732642L;
	
	
	@Getter @Setter private Set<SocketAddress> waitBindAddresses  ;
	@Getter @Setter private Set<SocketAddress> waitUnbindAddresses;
	@Getter @Setter private Set<SocketAddress> boundAddresses     ;
	
}

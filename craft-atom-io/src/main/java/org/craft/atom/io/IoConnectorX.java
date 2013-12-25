package org.craft.atom.io;

import java.io.Serializable;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author mindwind
 * @version 1.0, Dec 24, 2013
 */
@ToString(callSuper = true)
public class IoConnectorX extends IoReactorX implements Serializable {
	
	
	private static final long serialVersionUID = 6549663964261987160L;

	
	@Getter @Setter private Set<SocketChannel> connectingChannels    = new HashSet<SocketChannel>();
	@Getter @Setter private Set<SocketChannel> disconnectingChannels = new HashSet<SocketChannel>();
	
	
	public void addConnectingChannel(SocketChannel sc) {
		connectingChannels.add(sc);
	}
	
	public void addDisconnectingChannel(SocketChannel sc) {
		disconnectingChannels.add(sc);
	}
	
}

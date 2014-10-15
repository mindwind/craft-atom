package org.craft.atom.nio;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.craft.atom.io.IoConnectorX;

/**
 * @author mindwind
 * @version 1.0, Oct 15, 2014
 */
@ToString
public class NioConnectorX extends NioReactorX implements IoConnectorX {

	
	@Getter @Setter private int connectingChannelCount   ;
	@Getter @Setter private int disconnectingChannelCount;
	
	
	@Override
	public int connectingChannelCount() {
		return connectingChannelCount;
	}

	@Override
	public int disconnectingChannelCount() {
		return disconnectingChannelCount;
	}

}

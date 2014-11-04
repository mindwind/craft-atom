package io.craft.atom.nio;

import io.craft.atom.io.IoConnectorX;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


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

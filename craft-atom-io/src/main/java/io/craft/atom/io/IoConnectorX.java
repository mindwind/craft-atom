package io.craft.atom.io;

/**
 * The x-ray of {@link IoConnector}
 * 
 * @author mindwind
 * @version 1.0, Oct 15, 2014
 */
public interface IoConnectorX extends IoReactorX {

	
	/**
	 * @return current connecting channel count.
	 */
	int connectingChannelCount();
	
	/**
	 * @return current disconnecting channel count.
	 */
	int disconnectingChannelCount();
	
}

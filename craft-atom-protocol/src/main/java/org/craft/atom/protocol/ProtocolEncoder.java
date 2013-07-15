package org.craft.atom.protocol;

/**
 * Encodes higher-level message objects into binary data, implementor should be thread safe.
 * 
 * @author mindwind
 * @version 1.0, Oct 16, 2012
 */
public interface ProtocolEncoder<P> {
	
	/**
	 * Encodes higher-level protocol objects into binary data.
	 * 
	 * @param protocolObject
	 * @return byte array
	 * @throws ProtocolException
	 */
	byte[] encode(P protocolObject) throws ProtocolException;
	
}

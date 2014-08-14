package org.craft.atom.protocol;

/**
 * Encodes higher-level protocol objects into binary data, implementor should be thread safe.
 * 
 * @author mindwind
 * @version 1.0, Oct 16, 2012
 */
public interface ProtocolEncoder<P> {
	
	/**
	 * Encodes higher-level protocol objects into binary data.
	 * If input <tt>null</tt> output <tt>null</tt>
	 * 
	 * @param protocolObject
	 * @return byte array
	 * @throws ProtocolException
	 */
	byte[] encode(P protocolObject) throws ProtocolException;
	
}

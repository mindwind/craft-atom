package org.craft.atom.protocol;

import java.util.List;

/**
 * Decodes binary data into higher-level protocol objects, thread safe is not required.
 * 
 * @author mindwind
 * @version 1.0, Oct 16, 2012
 */
public interface ProtocolDecoder<P> {
	
	/**
	 * Decodes binary data into higher-level protocol objects.
	 * 
	 * @param bytes
	 * @return higher-level protocol objects, an empty list returned if these bytes are incomplete for protocol definition.
	 * @throws ProtocolException
	 */
	List<P> decode(byte[] bytes) throws ProtocolException;
	
}

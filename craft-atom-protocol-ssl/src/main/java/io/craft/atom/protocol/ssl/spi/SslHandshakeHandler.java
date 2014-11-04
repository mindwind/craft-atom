package io.craft.atom.protocol.ssl.spi;

/**
 * The handshake handler for {@link SslCodec} 
 * 
 * @author mindwind
 * @version 1.0, Oct 18, 2013
 */
public interface SslHandshakeHandler {
	
	/**
	 * Need write some handshake data to remote side before handshake can continue.
	 * 
	 * @param bytes
	 */
	void needWrite(byte[] bytes);
	
}

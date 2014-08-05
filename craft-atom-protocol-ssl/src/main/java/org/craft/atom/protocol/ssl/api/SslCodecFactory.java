package org.craft.atom.protocol.ssl.api;

import javax.net.ssl.SSLContext;

import org.craft.atom.protocol.ssl.DefaultSslCodec;

/**
 * SSL codec factory.
 * 
 * @author mindwind
 * @version 1.0, Aug 5, 2014
 */
public class SslCodecFactory {
	
	public static SslCodec newSslCodec(SSLContext sslContext, org.craft.atom.protocol.ssl.spi.SslHandshakeHandler sslHandshakeHandler) {
		return new DefaultSslCodec(sslContext, sslHandshakeHandler);
	}
	
}

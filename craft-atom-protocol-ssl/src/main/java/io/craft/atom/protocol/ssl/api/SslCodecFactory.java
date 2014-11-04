package io.craft.atom.protocol.ssl.api;

import io.craft.atom.protocol.ssl.DefaultSslCodec;

import javax.net.ssl.SSLContext;


/**
 * SSL codec factory, which provides static factory method to create {@code SslCodec} instance.
 * 
 * @author mindwind
 * @version 1.0, Aug 5, 2014
 */
public class SslCodecFactory {
	
	public static SslCodec newSslCodec(SSLContext sslContext, io.craft.atom.protocol.ssl.spi.SslHandshakeHandler sslHandshakeHandler) {
		return new DefaultSslCodec(sslContext, sslHandshakeHandler);
	}
	
}

package org.craft.atom.protocol.http.api;

import java.nio.charset.Charset;

import org.craft.atom.protocol.http.HttpRequestDecoder;
import org.craft.atom.protocol.http.HttpResponseEncoder;

/**
 * HTTP codec factory.
 * 
 * @author mindwind
 * @version 1.0, Aug 5, 2014
 */
public class HttpCodecFactory {
	
	
	public static HttpRequestDecoder newHttpRequestDecoder(Charset charset) {
		return newHttpRequestDecoderBuilder(charset).build();
	}
	
	public static HttpResponseEncoder newHttpResponseEncoder(Charset charset) {
		return new HttpResponseEncoder(charset);
	}
	
	public static HttpRequestDecoderBuilder newHttpRequestDecoderBuilder(Charset charset) {
		return new HttpRequestDecoderBuilder(charset);
	}

	
}

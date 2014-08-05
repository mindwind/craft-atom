package org.craft.atom.protocol.http.api;

import java.nio.charset.Charset;

import org.craft.atom.protocol.http.HttpCookieDecoder;
import org.craft.atom.protocol.http.HttpCookieEncoder;
import org.craft.atom.protocol.http.HttpParameterDecoder;
import org.craft.atom.protocol.http.HttpParameterEncoder;
import org.craft.atom.protocol.http.HttpRequestDecoder;
import org.craft.atom.protocol.http.HttpRequestEncoder;
import org.craft.atom.protocol.http.HttpResponseEncoder;

/**
 * HTTP codec factory.
 * 
 * @author mindwind
 * @version 1.0, Aug 5, 2014
 */
public class HttpCodecFactory {
	
	
	// http request
	public static HttpRequestEncoder newHttpRequestEncoder() {
		return new HttpRequestEncoder();
	}
	
	public static HttpRequestEncoder newHttpRequestEncoder(Charset charset) {
		return new HttpRequestEncoder(charset);
	}
	
	public static HttpRequestDecoder newHttpRequestDecoder() {
		return newHttpRequestDecoderBuilder().build();
	}
	
	public static HttpRequestDecoderBuilder newHttpRequestDecoderBuilder() {
		return new HttpRequestDecoderBuilder();
	}
	
	
	// http response
	public static HttpResponseEncoder newHttpResponseEncoder() {
		return new HttpResponseEncoder();
	}
	
	public static HttpResponseEncoder newHttpResponseEncoder(Charset charset) {
		return new HttpResponseEncoder(charset);
	}
	
	
	// http cookie
	public static HttpCookieEncoder newHttpCookieEncoder() {
		return new HttpCookieEncoder();
	}
	
	public static HttpCookieEncoder newHttpCookieEncoder(Charset charset) {
		return new HttpCookieEncoder(charset);
	}
	
	public static HttpCookieDecoder newHttpCookieDecoder() {
		return new HttpCookieDecoder();
	}
	
	public static HttpCookieDecoder newHttpCookieDecoder(Charset charset) {
		return new HttpCookieDecoder(charset);
	}
	
	public static HttpCookieDecoder newHttpCookieDecoder(Charset charset, boolean setCookie) {
		return new HttpCookieDecoder(charset, setCookie);
	}
	
	
	// http parameter
	public static HttpParameterEncoder newHttpParameterEncoder() {
		return new HttpParameterEncoder();
	}
	
	public static HttpParameterEncoder newHttpParameterEncoder(Charset charset) {
		return new HttpParameterEncoder(charset);
	}
	
	public static HttpParameterDecoder newHttpParameterDecoder() {
		return new HttpParameterDecoder();
	}
	
	public static HttpParameterDecoder newHttpParameterDecoder(Charset charset) {
		return new HttpParameterDecoder(charset);
	}
	
}

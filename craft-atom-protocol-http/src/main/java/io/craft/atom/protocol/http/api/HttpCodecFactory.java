package io.craft.atom.protocol.http.api;

import io.craft.atom.protocol.ProtocolDecoder;
import io.craft.atom.protocol.ProtocolEncoder;
import io.craft.atom.protocol.http.HttpCookieDecoder;
import io.craft.atom.protocol.http.HttpCookieEncoder;
import io.craft.atom.protocol.http.HttpParameterDecoder;
import io.craft.atom.protocol.http.HttpParameterEncoder;
import io.craft.atom.protocol.http.HttpRequestEncoder;
import io.craft.atom.protocol.http.HttpResponseEncoder;
import io.craft.atom.protocol.http.model.HttpCookie;
import io.craft.atom.protocol.http.model.HttpRequest;
import io.craft.atom.protocol.http.model.HttpResponse;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;


/**
 * HTTP codec factory, which provides static factory method and builder to create {@link ProtocolEncoder} instance
 * 
 * @author mindwind
 * @version 1.0, Aug 5, 2014
 */
public class HttpCodecFactory {
	
	
	// http request
	public static ProtocolEncoder<HttpRequest> newHttpRequestEncoder() {
		return new HttpRequestEncoder();
	}
	
	public static ProtocolEncoder<HttpRequest> newHttpRequestEncoder(Charset charset) {
		return new HttpRequestEncoder(charset);
	}
	
	public static ProtocolDecoder<HttpRequest> newHttpRequestDecoder() {
		return newHttpRequestDecoderBuilder().build();
	}
	
	public static HttpRequestDecoderBuilder newHttpRequestDecoderBuilder() {
		return new HttpRequestDecoderBuilder();
	}
	
	
	// http response
	public static ProtocolEncoder<HttpResponse> newHttpResponseEncoder() {
		return new HttpResponseEncoder();
	}
	
	public static ProtocolEncoder<HttpResponse> newHttpResponseEncoder(Charset charset) {
		return new HttpResponseEncoder(charset);
	}
	
	
	// http cookie
	public static ProtocolEncoder<HttpCookie> newHttpCookieEncoder() {
		return new HttpCookieEncoder();
	}
	
	public static ProtocolEncoder<HttpCookie> newHttpCookieEncoder(Charset charset) {
		return new HttpCookieEncoder(charset);
	}
	
	public static ProtocolDecoder<HttpCookie> newHttpCookieDecoder() {
		return new HttpCookieDecoder();
	}
	
	public static ProtocolDecoder<HttpCookie> newHttpCookieDecoder(Charset charset) {
		return new HttpCookieDecoder(charset);
	}
	
	public static HttpCookieDecoder newHttpCookieDecoder(Charset charset, boolean setCookie) {
		return new HttpCookieDecoder(charset, setCookie);
	}
	
	
	// http parameter
	public static ProtocolEncoder<Map<String, List<String>>> newHttpParameterEncoder() {
		return new HttpParameterEncoder();
	}
	
	public static ProtocolEncoder<Map<String, List<String>>> newHttpParameterEncoder(Charset charset) {
		return new HttpParameterEncoder(charset);
	}
	
	public static ProtocolDecoder<Map<String, List<String>>> newHttpParameterDecoder() {
		return new HttpParameterDecoder();
	}
	
	public static ProtocolDecoder<Map<String, List<String>>> newHttpParameterDecoder(Charset charset) {
		return new HttpParameterDecoder(charset);
	}
	
}

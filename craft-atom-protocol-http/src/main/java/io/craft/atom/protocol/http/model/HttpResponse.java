package io.craft.atom.protocol.http.model;

import io.craft.atom.protocol.ProtocolException;
import io.craft.atom.protocol.http.HttpCookieDecoder;
import io.craft.atom.protocol.http.HttpHeaders;
import io.craft.atom.protocol.http.api.HttpCodecFactory;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP response message.
 * <pre>
 *     Response      = Status-Line
 *                     *(( general-header
 *                      | response-header
 *                      | entity-header ) CRLF)
 *                     CRLF
 *                     [ message-body ]
 * </pre>
 * 
 * @author mindwind
 * @version 1.0, Feb 2, 2013
 */
@ToString(callSuper = true, of = { "statusLine" })
public class HttpResponse extends HttpMessage {

	
	private static final Logger            LOG                = LoggerFactory.getLogger(HttpResponse.class)                          ;
	private static final long              serialVersionUID   = 1532809882773093282L                                                 ;
	private static final HttpCookieDecoder SET_COOKIE_DECODER = HttpCodecFactory.newHttpCookieDecoder(Charset.forName("utf-8"), true);
	
	
	@Getter @Setter private HttpStatusLine statusLine;
	
	
	// ~ ------------------------------------------------------------------------------------------------------------

	
	public HttpResponse() {
		super();
	}

	public HttpResponse(HttpStatusLine statusLine, List<HttpHeader> headers) {
		super(headers);
		this.statusLine = statusLine;
	}
	
	public HttpResponse(HttpStatusLine statusLine, List<HttpHeader> headers, HttpEntity entity) {
		super(headers, entity);
		this.statusLine = statusLine;
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public void addCookie(HttpCookie cookie) {
		addHeader(HttpHeaders.newSetCookieHeader(cookie));
	}
	
	@Override
	protected List<HttpCookie> parseCookies() {
		List<HttpCookie> cookies = new ArrayList<HttpCookie>();
		
		List<HttpHeader> cookieHeaders = getHeaders(HttpHeaderType.COOKIE.getName());
		for (HttpHeader cookieHeader : cookieHeaders) {
			String cookieValue = cookieHeader.getValue();
			try {
				List<HttpCookie> cl = SET_COOKIE_DECODER.decode(cookieValue.getBytes(SET_COOKIE_DECODER.getCharset()));
				cookies.addAll(cl);
			} catch (ProtocolException e) {
				LOG.warn("[CRAFT-ATOM-PROTOCOL-HTTP] Decode response cookie error", e);
				return cookies;
			}
		}
		
		this.cookies = cookies;
		return this.cookies;
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	public String toHttpString(Charset charset) {
		StringBuilder sb = new StringBuilder();
		
		// request line
		HttpStatusLine statusLine = getStatusLine();
		if (statusLine != null) {
			sb.append(statusLine.toHttpString());
		}
		
		// message headers and entity
		sb.append(super.toHttpString(charset));
		
		return sb.toString();
	}

}

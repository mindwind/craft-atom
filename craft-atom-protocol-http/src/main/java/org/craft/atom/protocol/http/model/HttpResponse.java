package org.craft.atom.protocol.http.model;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.http.HttpCookieDecoder;

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
public class HttpResponse extends HttpMessage {

	private static final long serialVersionUID = 1532809882773093282L;
	private static final Log LOG = LogFactory.getLog(HttpResponse.class);
	private static final HttpCookieDecoder SET_COOKIE_DECODER = new HttpCookieDecoder(true);
	
	private HttpStatusLine statusLine;
	
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

	public HttpStatusLine getStatusLine() {
		return statusLine;
	}

	public void setStatusLine(HttpStatusLine statusLine) {
		this.statusLine = statusLine;
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	protected List<Cookie> parseCookies() {
		List<Cookie> cookies = new ArrayList<Cookie>();
		
		List<HttpHeader> cookieHeaders = getHeaders(HttpHeaderType.COOKIE.getName());
		for (HttpHeader cookieHeader : cookieHeaders) {
			String cookieValue = cookieHeader.getValue();
			try {
				List<Cookie> cl = SET_COOKIE_DECODER.decode(cookieValue.getBytes(SET_COOKIE_DECODER.getCharset()));
				cookies.addAll(cl);
			} catch (ProtocolException e) {
				LOG.error("Decode response cookie error", e);
				return cookies;
			}
		}
		
		this.cookies = cookies;
		return this.cookies;
	}
	
//	protected List<Cookie> getCookies(String name, boolean all) {
//		List<Cookie> cookies = new ArrayList<Cookie>();
//		if (name == null && !all) {
//			return cookies;
//		}
//		
//		List<HttpHeader> cookieHeaders = getHeaders(HttpHeaderType.COOKIE.getName());
//		for (HttpHeader cookieHeader : cookieHeaders) {
//			String setCookieString = cookieHeader.getValue();
//			Cookie cookie = Cookie.fromSetCookieString(setCookieString);
//			cookies.add(cookie);
//		}
//		
//		return cookies;
//	}
	
	// ~ ------------------------------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return String.format("HttpResponse [statusLine=%s, headers=%s, entity=%s]", statusLine, headers, entity);
	}
	
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

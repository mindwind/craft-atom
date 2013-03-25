package org.craft.atom.protocol.http.model;

import static org.craft.atom.protocol.http.HttpConstants.S_SEMICOLON;
import static org.craft.atom.protocol.http.HttpConstants.S_SP;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * A request message from a client to a server includes, within the
 * first line of that message, the method to be applied to the resource,
 * the identifier of the resource, and the protocol version in use.
 * <pre>
 *      Request       = Request-Line
 *                      *(( general-header
 *                       | request-header
 *                       | entity-header ) CRLF)
 *                      CRLF
 *                      [ message-body ]
 * </pre>
 * 
 * @author mindwind
 * @version 1.0, Feb 1, 2013
 */
public class HttpRequest extends HttpMessage {

	private static final long serialVersionUID = 2454619732646455653L;
	
	private HttpRequestLine requestLine = new HttpRequestLine();
	
	// ~ ------------------------------------------------------------------------------------------------------------

	public HttpRequest() {
		super();
	}
	
	public HttpRequest(HttpRequestLine requestLine) {
		this.requestLine = requestLine;
	}
	
	public HttpRequest(HttpRequestLine requestLine, List<HttpHeader> headers) {
		super(headers);
		this.requestLine = requestLine;
	}
	
	public HttpRequest(HttpRequestLine requestLine, List<HttpHeader> headers, HttpEntity entity) {
		super(headers, entity);
		this.requestLine = requestLine;
	}

	public HttpRequestLine getRequestLine() {
		return requestLine;
	}

	public void setRequestLine(HttpRequestLine requestLine) {
		this.requestLine = requestLine;
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	protected List<Cookie> getCookies(String name, boolean all) {
		List<Cookie> cookies = new ArrayList<Cookie>();
		if (name == null && !all) {
			return cookies;
		}
		
		List<HttpHeader> cookieHeaders = getHeaders(HttpHeaderType.COOKIE.getName());
		for (HttpHeader cookieHeader : cookieHeaders) {
			String cookieValue = cookieHeader.getValue();
			String[] cookiePairArr = cookieValue.split(S_SEMICOLON + S_SP);
			for (String cookiePair : cookiePairArr) {
				Cookie cookie = Cookie.fromCookiePair(cookiePair);
				cookies.add(cookie);
			}
		}
		
		return cookies;
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return String.format("HttpRequest [requestLine=%s, headers=%s, entity=%s]", requestLine, headers, entity);
	}
	
	public String toHttpString(Charset charset) {
		StringBuilder sb = new StringBuilder();
		
		// request line
		HttpRequestLine requestLine = getRequestLine();
		if (requestLine != null) {
			sb.append(requestLine.toHttpString());
		}
		
		// message headers and entity
		sb.append(super.toHttpString(charset));
		
		return sb.toString();
	}

}

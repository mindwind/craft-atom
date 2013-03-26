package org.craft.atom.protocol.http.model;

import static org.craft.atom.protocol.http.HttpConstants.S_Q_MARK;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.http.HttpCookieDecoder;


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

	private static final Log LOG = LogFactory.getLog(HttpRequest.class);
	private static final long serialVersionUID = 2454619732646455653L;
	private static final HttpCookieDecoder COOKIE_DECODER = new HttpCookieDecoder();
	
	private HttpRequestLine requestLine = new HttpRequestLine();
	private Map<String, List<String>> parameterMap;
	
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
	
	/**
	 * Returns the value of a request parameter as a <code>String</code>, or
     * <code>null</code> if the parameter does not exist. Request parameters are
     * extra information sent with the request. For HTTP, parameters are contained in the query string 
     * or posted form data with content type <code>application/x-www-form-urlencoded</code>.
     * <p>
     * You should only use this method when you are sure the parameter has only one value. 
     * If the parameter might have more than one value, use {@link #getParameters}.
     * 
	 * @param name
	 * @return
	 */
	public String getParameter(String name) {
		if (name == null) {
			return null;
		}
		
		List<String> values = getParameters(name);
		if (values.isEmpty()) {
			return null;
		}
		
		return values.get(0);
	}
	
	/**
	 * Returns an array of <code>String</code> objects containing all of the
     * values the given request parameter has, or empty list if the parameter does not exist.
     * 
	 * @param name
	 * @return
	 */
	public List<String> getParameters(String name) {
		List<String> values = new ArrayList<String>();
		if (name == null) {
			return values;
		}
		
		Map<String, List<String>> map = getParameterMap();
		values = map.get(name);
		if (values == null) {
			values = Collections.emptyList();
		}
		
		return values;
	}
	
	/**
	 * Returns a immutable parameter map of this request
	 * 
	 * @return
	 */
	public Map<String, List<String>> getParameterMap() {
		if (this.parameterMap != null) {
			return Collections.unmodifiableMap(this.parameterMap);
		}
		
		synchronized (this) {
			if (this.parameterMap != null) {
				return Collections.unmodifiableMap(this.parameterMap);
			}
			return Collections.unmodifiableMap(parseParameters());
		}
	}
	
	private Map<String, List<String>> parseParameters() {
		// TODO
		return null;
	}
	
	/**
     * Returns the query string that is contained in the request URL after
     * the path. This method returns <code>null</code> if the URL does not have a query string.
     *
     * @return
     */
    public String getQueryString() {
    	if (requestLine == null) {
    		return null;
    	}
    	
    	String uri = requestLine.getUri();
    	if (uri == null) {
    		return null;
    	}
    	
    	int idx = uri.indexOf(S_Q_MARK);
    	if (idx < 0) {
    		return null;
    	}
    	
    	return uri.substring(idx + 1);
    }
	
	protected List<Cookie> parseCookies() {
		List<Cookie> cookies = new ArrayList<Cookie>();
		
		List<HttpHeader> cookieHeaders = getHeaders(HttpHeaderType.COOKIE.getName());
		for (HttpHeader cookieHeader : cookieHeaders) {
			String cookieValue = cookieHeader.getValue();
			try {
				List<Cookie> cl = COOKIE_DECODER.decode(cookieValue.getBytes(COOKIE_DECODER.getCharset()));
				cookies.addAll(cl);
			} catch (ProtocolException e) {
				LOG.error("Decode request cookie error", e);
				return cookies;
			}
		}
		
		this.cookies = cookies;
		return this.cookies;
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

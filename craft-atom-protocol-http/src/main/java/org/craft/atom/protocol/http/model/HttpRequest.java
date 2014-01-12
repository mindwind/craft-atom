package org.craft.atom.protocol.http.model;

import static org.craft.atom.protocol.http.HttpConstants.S_AMPERSAND;
import static org.craft.atom.protocol.http.HttpConstants.S_Q_MARK;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.http.HttpCookieDecoder;
import org.craft.atom.protocol.http.HttpHeaders;
import org.craft.atom.protocol.http.HttpParameterDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
@ToString(callSuper = true, of = { "requestLine", "parameterMap" })
public class HttpRequest extends HttpMessage {
	
	
	private static final long                 serialVersionUID  = 2454619732646455653L                      ;
	private static final Logger               LOG               = LoggerFactory.getLogger(HttpRequest.class);
	private static final HttpCookieDecoder    COOKIE_DECODER    = new HttpCookieDecoder()                   ;
	private static final HttpParameterDecoder PARAMETER_DECODER = new HttpParameterDecoder()                ;
	
	
	@Getter @Setter private HttpRequestLine           requestLine  = new HttpRequestLine();
	@Setter         private Map<String, List<String>> parameterMap                        ;
	
	
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
	 * @return parameter value.
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
	 * @return parameter value list
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
	 * @return a immutable parameter map of this request
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
		Map<String, List<String>> map = Collections.emptyMap();
		
		StringBuilder buf = new StringBuilder();
		String queryString = getQueryString();
		if (queryString != null) {
			buf.append(queryString);
		}
		
		HttpContentType contentType = getContentType();
		if (contentType != null && MimeType.APPLICATION_X_WWW_FORM_URLENCODED == contentType.getMimeType()) {
			buf.append(S_AMPERSAND).append(getEntity().getContentAsString());
		}
		
		try {
			List<Map<String, List<String>>> paras = PARAMETER_DECODER.decode(buf.toString().getBytes(PARAMETER_DECODER.getCharset()));
			if (!paras.isEmpty()) {
				map = paras.get(0);
			}
		} catch (ProtocolException e) {
			LOG.error("[CRAFT-ATOM-PROTOCOL-HTTP] Decode request parameter error", e);
			return map;
		}
		
		this.parameterMap = map;
		return this.parameterMap;
	}
	
	/**
     * Get the query string that is contained in the request URL after
     * the path. This method returns <code>null</code> if the URL does not have a query string.
     *
     * @return query string
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
	
    @Override
	public void addCookie(Cookie cookie) {
		addHeader(HttpHeaders.newCookieHeader(cookie));
	}
    
    @Override
	protected List<Cookie> parseCookies() {
		List<Cookie> cookies = new ArrayList<Cookie>();
		
		List<HttpHeader> cookieHeaders = getHeaders(HttpHeaderType.COOKIE.getName());
		for (HttpHeader cookieHeader : cookieHeaders) {
			String cookieValue = cookieHeader.getValue();
			try {
				List<Cookie> cl = COOKIE_DECODER.decode(cookieValue.getBytes(COOKIE_DECODER.getCharset()));
				cookies.addAll(cl);
			} catch (ProtocolException e) {
				LOG.error("[CRAFT-ATOM-PROTOCOL-HTTP] Decode request cookie error", e);
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
		HttpRequestLine requestLine = getRequestLine();
		if (requestLine != null) {
			sb.append(requestLine.toHttpString());
		}
		
		// message headers and entity
		sb.append(super.toHttpString(charset));
		
		return sb.toString();
	}

}

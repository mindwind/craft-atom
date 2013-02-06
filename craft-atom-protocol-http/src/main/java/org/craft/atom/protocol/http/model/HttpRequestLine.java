package org.craft.atom.protocol.http.model;


/**
 * The Request-Line begins with a method token, followed by the Request-URI and the protocol version, 
 * and ending with CRLF. The elements are separated by SP characters. <br>
 * No CR or LF is allowed except in the final CRLF sequence.
 * 
 * <pre>
 *      Request-Line   = Method SP Request-URI SP HTTP-Version CRLF
 * </pre>
 * 
 * @author mindwind
 * @version 1.0, Feb 1, 2013
 * @see HttpRequest
 */
public class HttpRequestLine extends HttpStartLine {

	private static final long serialVersionUID = 1393510808581169505L;

	private HttpMethod method;
	private String uri;

	public HttpRequestLine() {
		super();
	}

	public HttpRequestLine(HttpMethod method, String uri, HttpVersion version) {
		super(version);
		this.method = method;
		this.uri = uri;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@Override
	public String toString() {
		return String.format("HttpRequestLine [method=%s, uri=%s, version=%s]", method, uri, version);
	}

}

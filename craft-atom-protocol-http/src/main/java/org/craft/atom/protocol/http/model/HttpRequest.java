package org.craft.atom.protocol.http.model;

import java.util.Map;


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
 * @see HttpRequestLine
 */
public class HttpRequest extends HttpMessage {

	private static final long serialVersionUID = 2454619732646455653L;
	
	private HttpRequestLine requestLine = new HttpRequestLine();

	public HttpRequest() {
		super();
	}
	
	public HttpRequest(HttpRequestLine requestLine) {
		this.requestLine = requestLine;
	}
	
	public HttpRequest(HttpRequestLine requestLine, Map<String, HttpHeader> headers) {
		super(headers);
		this.requestLine = requestLine;
	}
	
	public HttpRequest(HttpRequestLine requestLine, Map<String, HttpHeader> headers, HttpEntity entity) {
		super(headers, entity);
		this.requestLine = requestLine;
	}

	public HttpRequestLine getRequestLine() {
		return requestLine;
	}

	public void setRequestLine(HttpRequestLine requestLine) {
		this.requestLine = requestLine;
	}

	@Override
	public String toString() {
		return String.format("HttpRequest [requestLine=%s, headers=%s, entity=%s]", requestLine, headers, entity);
	}

}

package org.craft.atom.protocol.http.model;

import java.util.Map;

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
	
	private HttpStatusLine statusLine;

	public HttpResponse() {
		super();
	}

	public HttpResponse(HttpStatusLine statusLine, Map<String, HttpHeader> headers) {
		super(headers);
		this.statusLine = statusLine;
	}
	
	public HttpResponse(HttpStatusLine statusLine, Map<String, HttpHeader> headers, HttpEntity entity) {
		super(headers, entity);
		this.statusLine = statusLine;
	}

	public HttpStatusLine getStatusLine() {
		return statusLine;
	}

	public void setStatusLine(HttpStatusLine statusLine) {
		this.statusLine = statusLine;
	}

	@Override
	public String toString() {
		return String.format("HttpResponse [statusLine=%s, headers=%s, entity=%s]", statusLine, headers, entity);
	}

}

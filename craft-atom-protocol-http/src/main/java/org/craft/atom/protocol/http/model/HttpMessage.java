package org.craft.atom.protocol.http.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HTTP messages consist of requests from client to server and responses
 * from server to client.
 * <pre>
 *     HTTP-message   = Request | Response     ; HTTP/1.1 messages
 * </pre>
 * <p>
 * HTTP messages use the generic message format of RFC 822 for
 * transferring entities (the payload of the message). Both types
 * of message consist of a start-line, zero or more header fields
 * (also known as "headers"), an empty line (i.e., a line with nothing
 * preceding the CRLF) indicating the end of the header fields,
 * and possibly a message-body.
 * </p>
 * <pre>
 *      generic-message = start-line
 *                        *(message-header CRLF)
 *                        CRLF
 *                        [ message-body ]
 *      start-line      = Request-Line | Status-Line
 * </pre>
 *  
 * @author mindwind
 * @version 1.0, Feb 1, 2013
 */
public abstract class HttpMessage implements Serializable {

	private static final long serialVersionUID = -8373186983205172162L;
	
	protected Map<String, HttpHeader> headers = new LinkedHashMap<String, HttpHeader>();
	protected HttpEntity entity;
	
	// ~ ------------------------------------------------------------------------------------------------------------

	public HttpMessage() {
		super();
	}

	public HttpMessage(Map<String, HttpHeader> headers) {
		this.headers = headers;
	}
	
	public HttpMessage(Map<String, HttpHeader> headers, HttpEntity entity) {
		this.headers = headers;
		this.entity = entity;
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	/**
	 * Add a new header to http message, if the header exists replace it.
	 * 
	 * @param header
	 */
	public void addHeader(HttpHeader header) {
		if (header == null || header.getName() == null) {
			throw new IllegalArgumentException("header or header name is null!");
		}
		
		headers.put(header.getName(), header);
	}
	
	public HttpHeader getHeader(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name is null!");
		}
		
		return headers.get(name);
	}

	public Map<String, HttpHeader> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, HttpHeader> headers) {
		this.headers = headers;
	}

	public HttpEntity getEntity() {
		return entity;
	}

	public void setEntity(HttpEntity entity) {
		this.entity = entity;
	}
	
	public Iterator<HttpHeader> headerIterator() {
		return headers.values().iterator();
	}

	@Override
	public String toString() {
		return String.format("HttpMessage [headers=%s, entity=%s]", headers, entity);
	}

}

package org.craft.atom.protocol.http.model;

import static org.craft.atom.protocol.http.HttpConstants.S_CR;
import static org.craft.atom.protocol.http.HttpConstants.S_LF;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	
	protected List<HttpHeader> headers = new ArrayList<HttpHeader>();
	protected HttpEntity entity;
	
	// ~ ------------------------------------------------------------------------------------------------------------

	public HttpMessage() {
		super();
	}

	public HttpMessage(List<HttpHeader> headers) {
		this.headers = headers;
	}
	
	public HttpMessage(List<HttpHeader> headers, HttpEntity entity) {
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
		
		headers.add(header);
	}
	
	/**
	 * Remove the first header with given name.
	 * 
	 * @param name the name of the header
	 */
	public void removeFirstHeader(String name) {
		removeHeaders0(name, true);
	}
	
	/**
	 * Remove all the headers with the given name.
	 * 
	 * @param name the name of the header
	 */
	public void removeHeaders(String name) {
		removeHeaders0(name, false);
	}
	
	private void removeHeaders0(String name, boolean interrupt) {
		if (name == null) {
			throw new IllegalArgumentException("name is null!");
		}
		
		for (int i = 0; i < headers.size(); i++) {
            HttpHeader header = headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
            	headers.remove(i);
            	if (interrupt) {
            		break;
            	}
            }
        }
	}
	
	/**
     * Get the first header with the given name.
     *
     * <p>Header name comparison is case insensitive.
     *
     * @param name the name of the header
     * @return the first header or <code>null</code>
     */
	public HttpHeader getFirstHeader(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name is null!");
		}
		
		for (int i = 0; i < headers.size(); i++) {
            HttpHeader header = headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }
		
        return null;
	}
	
	/**
     * Gets all of the headers with the given name.  The returned list
     * maintains the relative order in which the headers were added.
     *
     * <p>Header name comparison is case insensitive.
     *
     * @param name the name of the header(s) to get
     *
     * @return an array of length >= 0
     */
	public List<HttpHeader> getHeaders(String name) {
		List<HttpHeader> headersFound = new ArrayList<HttpHeader>();

        for (int i = 0; i < headers.size(); i++) {
        	HttpHeader header = headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                headersFound.add(header);
            }
        }

        return headersFound;
	}

	public List<HttpHeader> getHeaders() {
		return headers;
	}

	public void setHeaders(List<HttpHeader> headers) {
		this.headers = headers;
	}

	public HttpEntity getEntity() {
		return entity;
	}

	public void setEntity(HttpEntity entity) {
		this.entity = entity;
	}
	
	public Iterator<HttpHeader> headerIterator() {
		return headers.iterator();
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	

	@Override
	public String toString() {
		return String.format("HttpMessage [headers=%s, entity=%s]", headers, entity);
	}
	
	public String toHttpString(Charset charset) {
		StringBuilder sb = new StringBuilder();
		
		// headers
		Iterator<HttpHeader> it = headerIterator();
		boolean hasHeader = false;
		while (it.hasNext()) {
			HttpHeader header = (HttpHeader) it.next();
			sb.append(header.toHttpString());
			if (!hasHeader) { 
				hasHeader = true; 
			}
		}
		
		// empty lines
		if (hasHeader) {
			sb.append(S_CR).append(S_LF);
		}
		
		// entity
		HttpEntity entity = getEntity();
		if (entity != null) {
			if (entity instanceof HttpChunkEntity) {
				HttpChunkEntity chunkEntity = (HttpChunkEntity) entity;
				sb.append(chunkEntity.toHttpString());
			} else {
				sb.append(entity.toHttpString());
			}
		}
		
		return sb.toString();
	}

}

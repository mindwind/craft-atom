package org.craft.atom.protocol.http.model;

import static org.craft.atom.protocol.http.HttpConstants.S_CR;
import static org.craft.atom.protocol.http.HttpConstants.S_LF;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
@ToString(of = { "headers", "entity", "cookies" })
public abstract class HttpMessage implements Serializable {

	private static final long serialVersionUID = -8373186983205172162L;
	
	@Getter @Setter protected List<HttpHeader> headers = new ArrayList<HttpHeader>();
	@Getter @Setter protected HttpEntity entity;
	@Setter protected List<Cookie> cookies;
	
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
			return;
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
			return;
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
			return null;
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
     * @return header list
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
	
	/**
	 * Returns an list containing all of the <code>Cookie</code> objects.
	 * 
	 * @return all cookies
	 */
	public List<Cookie> getCookies() {
		if (this.cookies != null) {
			return Collections.unmodifiableList(this.cookies);
		}
		
		synchronized (this) {
			if (this.cookies != null) {
				return Collections.unmodifiableList(this.cookies);
			}
			return Collections.unmodifiableList(parseCookies());
		}
	}
	
	/**
	 * Returns an list containing the <code>Cookie</code> objects with specified name.
	 * 
	 * @param name
	 * @return cookies with the name
	 */
	public List<Cookie> getCookies(String name) {
		List<Cookie> cookies = new ArrayList<Cookie>();
		if (name == null) {
			return cookies;
		}
		
		List<Cookie> allCookies = getCookies();
		for (Cookie cookie : allCookies) {
			if (name.equalsIgnoreCase(cookie.getName())) {
				cookies.add(cookie);
			}
		}
		
		return cookies;
	}
	
	/**
	 * Returns the cookie with specified name or <tt>null</tt> if does not exist.
	 * <p>
	 * You should only use this method when you are sure the cookie is unique. 
     * If the cookie might have more than one, use {@link #getCookies}.
	 * 
	 * @param name
	 * @return a cookie with the name
	 */
	public Cookie getCookie(String name) {
		List<Cookie> cookies = getCookies(name);
		if (cookies.isEmpty()) {
			return null;
		}
		return cookies.get(0);
	}
	
	 /**
     * Adds the specified cookie to http message. This method can be called
     * multiple times to set more than one cookie.
     * 
     * @param cookie
     *            the Cookie to return to the client
     */
    abstract public void addCookie(Cookie cookie);
	
	abstract protected List<Cookie> parseCookies();
	
	/**
	 * Returns the content type of the http message, or <code>null</code> if message has no entity.
	 * 
	 * @return content type object.
	 */
	public HttpContentType getContentType() {
		HttpEntity entity = getEntity();
		if (entity == null) {
			return null;
		}
		
		return entity.getContentType();
	}
	
	public Iterator<HttpHeader> headerIterator() {
		return headers.iterator();
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
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

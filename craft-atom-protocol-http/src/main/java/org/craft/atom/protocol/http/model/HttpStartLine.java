package org.craft.atom.protocol.http.model;

import java.io.Serializable;

/**
 * The start line of http protocol string.
 * 
 * @author mindwind
 * @version 1.0, Feb 1, 2013
 * @see HttpRequestLine
 * @see HttpStatusLine
 */
public abstract class HttpStartLine implements Serializable {

	private static final long serialVersionUID = -2856300388955363870L;
	
	protected HttpVersion version;
	
	public HttpStartLine() {
		super();
	}

	public HttpStartLine(HttpVersion version) {
		this.version = version;
	}

	public HttpVersion getVersion() {
		return version;
	}

	public void setVersion(HttpVersion version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return String.format("HttpStartLine [version=%s]", version);
	}

}

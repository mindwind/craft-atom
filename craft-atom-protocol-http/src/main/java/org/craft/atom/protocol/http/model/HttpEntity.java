package org.craft.atom.protocol.http.model;

import java.io.Serializable;

/**
 * Represents an http entity.<br>
 * An entity that can be sent or received with an HTTP message, 
 * but not all messages contain entity, it is optional.
 * The entity contains a block of arbitrary data.
 * 
 * @author mindwind
 * @version 1.0, Feb 1, 2013
 */
public class HttpEntity implements Serializable {

	private static final long serialVersionUID = -3461343279665456788L;
	
	protected String content;

	public HttpEntity() {
		super();
	}

	public HttpEntity(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return String.format("HttpEntity [content=%s]", content);
	}

}

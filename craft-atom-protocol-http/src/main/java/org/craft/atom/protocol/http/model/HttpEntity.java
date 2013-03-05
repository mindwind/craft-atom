package org.craft.atom.protocol.http.model;

import java.io.Serializable;
import java.nio.charset.Charset;

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
	
	protected HttpContentType contentType = HttpContentType.DEFAULT;
	protected byte[] content;

	public HttpEntity() {
		super();
	}

	public HttpEntity(byte[] content) {
		this.content = content;
	}
	
	public HttpEntity(String content, HttpContentType contentType) {
		this.contentType = contentType;
		Charset charset = contentType.getCharset();
		this.content = content.getBytes(charset == null ? Charset.defaultCharset() : charset);
	}
	
	public String getContentAsString() {
		Charset charset = contentType.getCharset();
		return new String(content, charset == null ? Charset.defaultCharset() : charset);
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public HttpContentType getContentType() {
		return contentType;
	}

	public void setContentType(HttpContentType contentType) {
		this.contentType = contentType;
	}

	public String toHttpString() {
		Charset charset = contentType.getCharset();
		return new String(content, charset == null ? Charset.defaultCharset() : charset);
	}

}

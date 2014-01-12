package org.craft.atom.protocol.http.model;

import java.io.Serializable;
import java.nio.charset.Charset;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents an http entity.<br>
 * An entity that can be sent or received with an HTTP message, 
 * but not all messages contain entity, it is optional.
 * The entity contains a block of arbitrary data.
 * 
 * @author mindwind
 * @version 1.0, Feb 1, 2013
 */
@ToString(of = { "contentType", "content" })
public class HttpEntity implements Serializable {

	
	private static final long serialVersionUID = -3461343279665456788L;
	
	
	@Getter @Setter protected HttpContentType contentType = HttpContentType.DEFAULT;
	@Getter @Setter protected byte[]          content                              ;
	
	
	// ~ -----------------------------------------------------------------------------------------------------------

	
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
	
	
	// ~ -----------------------------------------------------------------------------------------------------------
	
	
	public String getContentAsString() {
		Charset charset = contentType.getCharset();
		return new String(content, charset == null ? Charset.defaultCharset() : charset);
	}

	public String toHttpString() {
		Charset charset = contentType.getCharset();
		return new String(content, charset == null ? Charset.defaultCharset() : charset);
	}

}

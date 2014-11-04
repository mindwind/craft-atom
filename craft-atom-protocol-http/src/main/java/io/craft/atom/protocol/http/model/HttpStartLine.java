package io.craft.atom.protocol.http.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The start line of http protocol string.
 * 
 * @author mindwind
 * @version 1.0, Feb 1, 2013
 * @see HttpRequestLine
 * @see HttpStatusLine
 */
@ToString(of = "version")
public abstract class HttpStartLine implements Serializable {

	
	private static final long serialVersionUID = -2856300388955363870L;
	
	
	@Getter @Setter protected HttpVersion version;
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	public HttpStartLine() {
		super();
	}

	public HttpStartLine(HttpVersion version) {
		this.version = version;
	}
}

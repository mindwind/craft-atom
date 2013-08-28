package org.craft.atom.protocol.http.model;

import static org.craft.atom.protocol.http.HttpConstants.S_CR;
import static org.craft.atom.protocol.http.HttpConstants.S_LF;
import static org.craft.atom.protocol.http.HttpConstants.S_SP;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * The Request-Line begins with a method token, followed by the Request-URI and the protocol version, 
 * and ending with CRLF. The elements are separated by SP characters. <br>
 * No CR or LF is allowed except in the final CRLF sequence.
 * 
 * <pre>
 *      Request-Line   = Method SP Request-URI SP HTTP-Version CRLF
 * </pre>
 * 
 * @author mindwind
 * @version 1.0, Feb 1, 2013
 * @see HttpRequest
 */
@ToString(callSuper = true, of = { "method", "uri" })
public class HttpRequestLine extends HttpStartLine {

	private static final long serialVersionUID = 1393510808581169505L;

	@Getter @Setter private HttpMethod method;
	@Getter @Setter private String uri;

	// ~ ------------------------------------------------------------------------------------------------------------

	public HttpRequestLine() {
		super();
	}

	public HttpRequestLine(HttpMethod method, String uri, HttpVersion version) {
		super(version);
		this.method = method;
		this.uri = uri;
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	public String toHttpString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getMethod()).append(S_SP).append(getUri()).append(S_SP).append(getVersion().getValue()).append(S_CR).append(S_LF);
		return sb.toString();
	}

}

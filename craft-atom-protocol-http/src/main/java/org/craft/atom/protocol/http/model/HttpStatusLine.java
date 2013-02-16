package org.craft.atom.protocol.http.model;

import static org.craft.atom.protocol.http.model.HttpConstants.S_CR;
import static org.craft.atom.protocol.http.model.HttpConstants.S_LF;
import static org.craft.atom.protocol.http.model.HttpConstants.S_SP;

import org.apache.http.HttpResponse;

/**
 * The first line of a Response message is the Status-Line, consisting
 * of the protocol version followed by a numeric status code and its
 * associated textual phrase, with each element separated by SP
 * characters. No CR or LF is allowed except in the final CRLF sequence.
 * <pre>
 *     Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
 * </pre>
 * 
 * @author mindwind
 * @version 1.0, Feb 1, 2013
 * @see HttpResponse
 */
public class HttpStatusLine extends HttpStartLine {

	private static final long serialVersionUID = -3219302257563696666L;
	
	private HttpStatus status;

	public HttpStatusLine() {
		super();
	}

	public HttpStatusLine(HttpVersion version, HttpStatus status) {
		super(version);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return String.format("HttpStatusLine [status=%s, version=%s]", status, version);
	}
	
	public String toHttpString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getVersion().getValue()).append(S_SP).append(getStatus().getStatusCode()).append(S_SP).append(getStatus().getReasonPhrase()).append(S_CR).append(S_LF);
		return sb.toString();
	}
}

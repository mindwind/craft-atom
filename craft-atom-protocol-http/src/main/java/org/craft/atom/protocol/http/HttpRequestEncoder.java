package org.craft.atom.protocol.http;

import static org.craft.atom.protocol.http.model.HttpConstants.S_COLON;
import static org.craft.atom.protocol.http.model.HttpConstants.S_CR;
import static org.craft.atom.protocol.http.model.HttpConstants.S_LF;
import static org.craft.atom.protocol.http.model.HttpConstants.S_SP;

import java.util.Iterator;

import org.craft.atom.protocol.ProtocolEncoder;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.http.model.HttpEntity;
import org.craft.atom.protocol.http.model.HttpHeader;
import org.craft.atom.protocol.http.model.HttpRequest;
import org.craft.atom.protocol.http.model.HttpRequestLine;

/**
 * A {@link ProtocolEncoder} which encodes a {@code HttpRequest} object into bytes follow the HTTP specification, default charset is utf-8.
 * <br>
 * Thread safe.
 * 
 * @author mindwind
 * @version 1.0, Feb 3, 2013
 */
public class HttpRequestEncoder extends HttpEncoder implements ProtocolEncoder<HttpRequest> {

	@Override
	public byte[] encode(HttpRequest request) throws ProtocolException {
		StringBuilder sb = new StringBuilder();
		
		// request line
		HttpRequestLine requestLine = request.getRequestLine();
		sb.append(requestLine.getMethod()).append(S_SP).append(requestLine.getUri()).append(S_SP).append(requestLine.getVersion().getValue()).append(S_CR).append(S_LF);
		
		// headers
		Iterator<HttpHeader> it = request.headerIterator();
		while (it.hasNext()) {
			HttpHeader header = (HttpHeader) it.next();
			sb.append(header.getName()).append(S_COLON).append(S_SP).append(header.getValue()).append(S_CR).append(S_LF);
		}
		
		// empty lines
		sb.append(S_CR).append(S_LF);
		
		// entity
		HttpEntity entity = request.getEntity();
		if (entity != null) {
			sb.append(entity.getContent());
		}
		
		return sb.toString().getBytes(charset);
	}

}

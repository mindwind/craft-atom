package org.craft.atom.protocol.http;

import org.craft.atom.protocol.ProtocolEncoder;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.http.model.HttpRequest;

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
	public byte[] encode(HttpRequest protocolObject) throws ProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

}

package org.craft.atom.protocol.http;

import org.craft.atom.protocol.ProtocolEncoder;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.http.model.HttpResponse;

/**
 * A {@link ProtocolEncoder} which encodes a {@code HttpResponse} object into bytes follow the HTTP specification, default charset is utf-8.
 * <br>
 * Thread safe.
 * 
 * @author mindwind
 * @version 1.0, Feb 3, 2013
 */
public class HttpResponseEncoder extends HttpEncoder implements ProtocolEncoder<HttpResponse> {

	@Override
	public byte[] encode(HttpResponse protocolObject) throws ProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

}

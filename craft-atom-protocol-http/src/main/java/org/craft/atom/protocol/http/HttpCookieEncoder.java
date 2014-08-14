package org.craft.atom.protocol.http;

import java.nio.charset.Charset;

import lombok.ToString;

import org.craft.atom.protocol.AbstractProtocolCodec;
import org.craft.atom.protocol.ProtocolEncoder;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.http.model.HttpCookie;

/**
 * A {@link ProtocolEncoder} which encodes a {@code Cookie} object into bytes follow the HTTP specification, default charset is utf-8.
 * <br>
 * Thread safe.
 * 
 * @author mindwind
 * @version 1.0, Mar 25, 2013
 */
@ToString(callSuper = true)
public class HttpCookieEncoder extends AbstractProtocolCodec implements ProtocolEncoder<HttpCookie> {

	
	public HttpCookieEncoder() {}
	
	public HttpCookieEncoder(Charset charset) {
		this.charset = charset;
	}
	
	@Override
	public byte[] encode(HttpCookie cookie) throws ProtocolException {
		if (cookie == null) return null;
		String httpString = cookie.toHttpString();
		return httpString.getBytes(charset);
	}

}

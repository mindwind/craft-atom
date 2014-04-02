package org.craft.atom.protocol.http;

import java.nio.charset.Charset;

import lombok.ToString;

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
@ToString(callSuper = true)
public class HttpResponseEncoder extends HttpEncoder implements ProtocolEncoder<HttpResponse> {

	public HttpResponseEncoder() {
		super();
	}
	
	public HttpResponseEncoder(Charset charset)  {
		this.charset = charset;
	}

	@Override
	public byte[] encode(HttpResponse resonpse) throws ProtocolException {
		String httpString = resonpse.toHttpString(charset);
		return httpString.getBytes(charset);
	}

}

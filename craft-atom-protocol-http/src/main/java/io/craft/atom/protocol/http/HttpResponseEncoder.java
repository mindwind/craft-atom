package io.craft.atom.protocol.http;

import io.craft.atom.protocol.ProtocolEncoder;
import io.craft.atom.protocol.ProtocolException;
import io.craft.atom.protocol.http.model.HttpResponse;

import java.nio.charset.Charset;

import lombok.ToString;


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
	public byte[] encode(HttpResponse response) throws ProtocolException {
		if (response == null) return null;
		String httpString = response.toHttpString(charset);
		return httpString.getBytes(charset);
	}

}

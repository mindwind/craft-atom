package io.craft.atom.protocol.http;

import io.craft.atom.protocol.ProtocolDecoder;
import io.craft.atom.protocol.ProtocolException;
import io.craft.atom.protocol.http.model.HttpResponse;

import java.util.List;

import lombok.ToString;


/**
 * A {@link ProtocolDecoder} which decodes bytes into {@code HttpResponse} object, default charset is utf-8
 * <br>
 * Not thread safe
 * 
 * @author mindwind
 * @version 1.0, Feb 3, 2013
 */
@ToString(callSuper = true)
public class HttpResponseDecoder extends HttpDecoder<HttpResponse> implements ProtocolDecoder<HttpResponse> {

	@Override
	public List<HttpResponse> decode(byte[] bytes) throws ProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	boolean hasEntity(HttpResponse httpMessage) {
		// Here assume all response has entity, it does not hurt the correctness.
		return true;
	}

}

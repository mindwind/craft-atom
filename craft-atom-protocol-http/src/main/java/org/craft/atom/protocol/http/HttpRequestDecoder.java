package org.craft.atom.protocol.http;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.craft.atom.protocol.ProtocolDecoder;
import org.craft.atom.protocol.ProtocolException;

/**
 * A {@link ProtocolDecoder} which decodes bytes into {@code HttpRequest} object, default charset is utf-8
 * <br>
 * Not thread safe
 * 
 * @author mindwind
 * @version 1.0, Feb 2, 2013
 */
public class HttpRequestDecoder extends HttpDecoder implements ProtocolDecoder<HttpRequest> {
	
	private int maxLineLength = defaultBufferSize;
	
	// ~ ------------------------------------------------------------------------------------------------------------

	public HttpRequestDecoder() {
		super();
	}

	public HttpRequestDecoder(Charset charset) {
		this.charset = charset;
	}

	public HttpRequestDecoder(Charset charset, int defaultBufferSize) {
		this(charset);
		this.defaultBufferSize = defaultBufferSize;
		buf.reset(defaultBufferSize);
	}

	public HttpRequestDecoder(Charset charset, int defaultBufferSize, int maxLineLength) {
		this(charset, defaultBufferSize);
		this.maxLineLength = maxLineLength;
	}

	public HttpRequestDecoder(Charset charset, int defaultBufferSize, int maxLineLength, int maxRequestSize) {
		this(charset, defaultBufferSize, maxLineLength);
		this.maxSize = maxRequestSize;
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------

	@Override
	public List<HttpRequest> decode(byte[] bytes) throws ProtocolException {
		List<HttpRequest> reqs = new ArrayList<HttpRequest>();
		reset();
		buf.append(bytes);
		
		while (splitIndex < buf.length()) {
			switch (state) {
			case START:

				break;
			case REQUEST_LINE:

				break;
			case HEADER:

				break;
			case ENTITY:

				break;
			default:
				throw new IllegalStateException("invalid decoder state!");
			}
		}
		
		return reqs;
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------

	public int getMaxLineLength() {
		return maxLineLength;
	}

	public void setMaxLineLength(int maxLineLength) {
		this.maxLineLength = maxLineLength;
	}

	@Override
	public String toString() {
		return String
				.format("HttpRequestDecoder [maxLineLength=%s, state=%s, charset=%s, defaultBufferSize=%s, buf=%s, splitIndex=%s, searchIndex=%s, maxSize=%s]",
						maxLineLength, state, charset, defaultBufferSize, buf,
						splitIndex, searchIndex, maxSize);
	}

}

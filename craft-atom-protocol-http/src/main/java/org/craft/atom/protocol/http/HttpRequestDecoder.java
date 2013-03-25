package org.craft.atom.protocol.http;

import static org.craft.atom.protocol.http.HttpConstants.CR;
import static org.craft.atom.protocol.http.HttpConstants.HT;
import static org.craft.atom.protocol.http.HttpConstants.LF;
import static org.craft.atom.protocol.http.HttpConstants.SP;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.craft.atom.protocol.ProtocolDecoder;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.http.model.HttpMethod;
import org.craft.atom.protocol.http.model.HttpRequest;
import org.craft.atom.protocol.http.model.HttpVersion;


/**
 * A {@link ProtocolDecoder} which decodes bytes into {@code HttpRequest} object, default charset is utf-8.
 * <br>
 * Not thread safe
 * 
 * @author mindwind
 * @version 1.0, Feb 2, 2013
 */
public class HttpRequestDecoder extends HttpDecoder<HttpRequest> implements ProtocolDecoder<HttpRequest> {

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
		try {
			return decode0(bytes);
		} catch (Exception e) {
			clear();
			resetIndex();
			if (e instanceof ProtocolException) {
				throw (ProtocolException) e;
			}
			throw new ProtocolException(e);
		}
	}
	
	protected boolean hasEntity(HttpRequest request) {
		HttpMethod method = request.getRequestLine().getMethod();
		if (method == HttpMethod.POST || method == HttpMethod.PUT) {
			// Only POST and PUT method may has entity in a http request.
			return true;
		}
		return false;
	}

	private List<HttpRequest> decode0(byte[] bytes) throws ProtocolException, IOException {
		List<HttpRequest> reqs = new ArrayList<HttpRequest>();
		reset();
		buf.append(bytes);
		
		while (searchIndex < buf.length()) {
			switch (state) {
			case START:
				state4START();
				break;
			case METHOD:
				state4METHOD();
				break;
			case REQUEST_URI:
				state4REQUEST_URI();
				break;
			case VERSION:
				state4VERSION();
				break;
			case HEADER_NAME:
				state4HEADER_NAME();
				break;
			case HEADER_VALUE_PREFIX:
				state4HEADER_VALUE_PREFIX();
				break;
			case HEADER_VALUE:
				state4HEADER_VALUE();
				break;
			case HEADER_VALUE_SUFFIX:
				state4HEADER_VALUE_SUFFIX();
				break;
			case ENTITY:
				state4ENTITY();
				break;
			case ENTITY_LENGTH:
				state4ENTITY_LENGTH();
				break;
			case ENTITY_CHUNKED_SIZE:
				state4ENTITY_CHUNKED_SIZE();
				break;
			case ENTITY_CHUNKED_EXTENSION_NAME:
				state4ENTITY_CHUNKED_EXTENSION_NAME();
				break;
			case ENTITY_CHUNKED_EXTENSION_VALUE:
				state4ENTITY_CHUNKED_EXTENSION_VALUE();
				break;
			case ENTITY_CHUNKED_DATA:
				state4ENTITY_CHUNKED_DATA();
				break;
			case ENTITY_CHUNKED_TRAILER_NAME:
				state4ENTITY_CHUNKED_TRAILER_NAME();
				break;
			case ENTITY_CHUNKED_TRAILER_VALUE:
				state4ENTITY_CHUNKED_TRAILER_VALUE();
				break;
			case ENTITY_ENCODING:
				state4ENTITY_ENCODING();
				break;
			case END:
				state4END(reqs);
				break;
			default:
				throw new IllegalStateException("invalid decoder state!");
			}
		}
		
		return reqs;
	}
	
	private void state4VERSION() throws ProtocolException {
		// slice version part
		String versionStr = sliceBySeparators(-1, LF);
		if (versionStr == null) { 
			return; 
		}
		
		// render current request with version
		HttpVersion version = HttpVersion.from(versionStr);
		httpMessage.getRequestLine().setVersion(version);
		
		// to next state;
		if (CR == currentByte() && LF == nextByte()) {
			state = ENTITY;
			slide(2);
		} else {
			state = HEADER_NAME;
		}
	}
	
	private void state4REQUEST_URI() throws ProtocolException {
		// slice request uri part
		String uri = sliceBySeparators(0, SP, HT);
		if (uri == null) { 
			return; 
		}
		
		// render current request with request uri
		httpMessage.getRequestLine().setUri(uri);
		
		// to next state
		state = VERSION;
	}
	
	private void state4METHOD() throws ProtocolException {
		// slice method part
		String methodStr = sliceBySeparators(0, SP, HT);
		if (methodStr == null) { 
			return;
		}
		
		// render current request with method
		HttpMethod method = HttpMethod.valueOf(methodStr);
		httpMessage.getRequestLine().setMethod(method);
		
		// to next state
		state = REQUEST_URI;
	}
	
	private void state4START() throws ProtocolException {
		// skip any CR or LF before request line
		boolean done = skip(CR, LF);
		if (!done) { 
			return; 
		}
		
		// on START state create a new request as current request.
		httpMessage = new HttpRequest();
		
		// to next state
		state = METHOD;
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	public HttpRequest getRequest() {
		return httpMessage;
	}

	public void setRequest(HttpRequest request) {
		this.httpMessage = request;
	}

	@Override
	public String toString() {
		return String
				.format("HttpRequestDecoder [stateIndex=%s, state=%s, maxLineLength=%s, trailerSize=%s, header=%s, entity=%s, chunk=%s, contentType=%s, chunkExtName=%s, httpMessage=%s, defaultBufferSize=%s, buf=%s, splitIndex=%s, searchIndex=%s, maxSize=%s, charset=%s]",
						stateIndex, state, maxLineLength, trailerSize, header,
						entity, chunk, contentType, chunkExtName, httpMessage,
						defaultBufferSize, buf, splitIndex, searchIndex,
						maxSize, charset);
	}
	
}

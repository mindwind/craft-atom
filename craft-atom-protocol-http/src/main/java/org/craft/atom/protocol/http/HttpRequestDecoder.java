package org.craft.atom.protocol.http;

import static org.craft.atom.protocol.http.model.HttpConstants.COLON;
import static org.craft.atom.protocol.http.model.HttpConstants.CR;
import static org.craft.atom.protocol.http.model.HttpConstants.HT;
import static org.craft.atom.protocol.http.model.HttpConstants.LF;
import static org.craft.atom.protocol.http.model.HttpConstants.NUL;
import static org.craft.atom.protocol.http.model.HttpConstants.SP;
import static org.craft.atom.protocol.http.model.HttpConstants.TRANSFER_ENCODING_CHUNKED;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.craft.atom.protocol.ProtocolDecoder;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.ProtocolExceptionType;
import org.craft.atom.protocol.http.model.HttpEntity;
import org.craft.atom.protocol.http.model.HttpHeader;
import org.craft.atom.protocol.http.model.HttpHeaders;
import org.craft.atom.protocol.http.model.HttpMethod;
import org.craft.atom.protocol.http.model.HttpRequest;
import org.craft.atom.protocol.http.model.HttpRequestLine;
import org.craft.atom.protocol.http.model.HttpVersion;
import org.craft.atom.util.ByteUtil;


/**
 * A {@link ProtocolDecoder} which decodes bytes into {@code HttpRequest} object, default charset is utf-8
 * <br>
 * Not thread safe
 * <p>
 * State-Transition Diagram
 * <pre>
 * 
 * </pre>
 * 
 * @author mindwind
 * @version 1.0, Feb 2, 2013
 */
public class HttpRequestDecoder extends HttpDecoder implements ProtocolDecoder<HttpRequest> {
	
	private HttpRequest request;
	private HttpHeader header;
	private HttpEntity entity;
	
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
		try {
			return decode0(bytes);
		} catch (Exception e) {
			clear();
			if (e instanceof ProtocolException) {
				throw (ProtocolException) e;
			}
			throw new ProtocolException(e);
		}
	}

	public List<HttpRequest> decode0(byte[] bytes) throws ProtocolException {
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
			case HEADER_VALUE_SUFFFIX:
				state4HEADER_VALUE_SUFFIX();
				break;
			case ENTITY:
				state4ENTITY();
				break;
			case ENTITY_LENGTH:
				state4ENTITY_LENGTH();
				break;
			case ENTITY_CHUNKED:
				
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
	
	private void state4END(List<HttpRequest> reqs) throws ProtocolException {
		slideIfMatch(LF);
		reqs.add(request);
		splitIndex = stateIndex = searchIndex;
		clear();
		state = START;
	}

	private void state4ENTITY_ENCODING() throws ProtocolException {
		String coding = request.getHeader(HttpHeaders.CONTENT_ENCODING.getName()).getValue();
		
		// none or identity
		if (coding == null || "identity".equals(coding)) {
			
		}
		// gzip or x-gzip
		else if ("gzip".equals(coding) && "x-gzip".equals(coding)) {
			
		}
		// deflate
		else if ("deflate".equals(coding)) {
			
		}
		
		// next state
		state = END;
	}
	
	private void state4ENTITY_LENGTH() throws ProtocolException {
		// get content length
		int clen = Integer.parseInt(request.getHeader(HttpHeaders.CONTENT_LENGTH.getName()).getValue());
		
		// slice content value
		String content = sliceByLength(clen);
		if (content == null) {
			return;
		}
		
		// render current request with entity
		entity.setContent(content);
		request.setEntity(entity);
		
		// to next state
		state = ENTITY_ENCODING;
	}

	private void state4ENTITY() throws ProtocolException {
		entity = new HttpEntity();
		
		// content length
		if (request.getHeader(HttpHeaders.CONTENT_LENGTH.getName()) != null) {
			state = ENTITY_LENGTH;
		}
		// chunked
		else if (TRANSFER_ENCODING_CHUNKED.equals(request.getHeader(HttpHeaders.TRANSFER_ENCODING.getName()).getValue())) {
			state = ENTITY_CHUNKED;
		}
		// no entity
		else {
			state = END;
		}
	}
	
	private void state4HEADER_VALUE_SUFFIX() throws ProtocolException {
		byte cb = currentByte();
		
		// folded header
		if (SP == cb || HT == cb) {
			state = HEADER_VALUE_PREFIX;
		}
		// header end
		else if (CR == cb) {
			request.addHeader(header);
			state = hasEntity(request.getRequestLine()) ? ENTITY : END;
			slide(1);
		}
		// next header
		else {
			request.addHeader(header);
			state = HEADER_NAME;
		}
	}
	
	private void state4HEADER_VALUE() throws ProtocolException {
		// slice header value
		String value = sliceBySeparators(-1, LF);
		if (value == null) {
			return;
		}
		
		header.appendValue(value);
		
		// to next state
		byte cb = currentByte();
		// has no next byte in buffer
		if (NUL == cb) {
			state = HEADER_VALUE_SUFFFIX;
		}
		// folded header
		else if (SP == cb || HT == cb) {
			state = HEADER_VALUE_PREFIX;
		}
		// header end
		else if (CR == cb) {
			request.addHeader(header);
			state = hasEntity(request.getRequestLine()) ? ENTITY : END;
			slide(1);
		}
		// next header
		else {
		   request.addHeader(header);
		   state = HEADER_NAME;
		}
	}
	
	private void state4HEADER_VALUE_PREFIX() throws ProtocolException {
		// skip SP or HT 
		boolean done = skip(SP, HT);
		if (!done) { 
			return; 
		}
		
		// to next state
		state = HEADER_VALUE;
	}
	
	private void state4HEADER_NAME() throws ProtocolException {
		// slice header name
		String name = sliceBySeparators(0, COLON);
		if (name == null) { 
			return; 
		}
		
		header = new HttpHeader();
		header.setName(name);
		
		// to next state
		state = HEADER_VALUE_PREFIX;
	}
	
	private void state4VERSION() throws ProtocolException {
		// slice version part
		String versionStr = sliceBySeparators(-1, LF);
		if (versionStr == null) { 
			return; 
		}
		
		// render current request with version
		HttpVersion version = HttpVersion.from(versionStr);
		request.getRequestLine().setVersion(version);
		
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
		request.getRequestLine().setUri(uri);
		
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
		request.getRequestLine().setMethod(method);
		
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
		request = new HttpRequest();
		
		// to next state
		state = METHOD;
	}
	
	private boolean hasEntity(HttpRequestLine requestLine) {
		HttpMethod method = requestLine.getMethod();
		if (method == HttpMethod.POST || method == HttpMethod.PUT) {
			return true;
		}
		return false;
	}
	
	private byte currentByte() {
		int i = searchIndex;
		if ( i < buf.length()) {
			return buf.byteAt(i);
		} else {
			return NUL;
		}
	}
	
	private byte nextByte() {
		int i = searchIndex + 1;
		if (i < buf.length()) {
			return buf.byteAt(i);
		} else {
			return NUL;
		}
	}
	
	private String sliceByLength(int len) throws ProtocolException {
		boolean done = false;
		int offset = stateIndex;
		int length = searchIndex - stateIndex;
		if (len < length) {
			throw new ProtocolException(ProtocolExceptionType.UNEXPECTED, "slice len=" + len + "< length=" + length); 
		}
		
		int tailIndex = offset + len;
		if (tailIndex <= buf.length()) {
			stateIndex = searchIndex = tailIndex;
			done = true;
		}
		
		if (searchIndex > maxSize) { throw new ProtocolException(ProtocolExceptionType.MAX_SIZE_LIMIT, maxSize); }
		
		if (done) {
			return new String(buf.buffer(), offset, len, charset);
		} else {
			return null;	
		}
	}
	
	private String sliceBySeparators(int shift, byte... separators) throws ProtocolException {
		boolean done = false;
		int offset = stateIndex;
		int length = searchIndex - stateIndex;
		for (int i = searchIndex; i < buf.length(); length++) {
			if (length > maxLineLength) { throw new ProtocolException(ProtocolExceptionType.LINE_LENGTH_LIMIT, maxLineLength); }
			
			byte b = buf.byteAt(i);
			searchIndex = ++i;
			if (ByteUtil.indexOf(separators, b) >= 0) {
				length = searchIndex - stateIndex - 1;
				stateIndex = searchIndex;
				done = true;
				break;
			}
		}
		
		if (searchIndex > maxSize) { throw new ProtocolException(ProtocolExceptionType.MAX_SIZE_LIMIT, maxSize); }
		
		if (done) {
			return new String(buf.buffer(), offset, length + shift, charset);
		} else {
			return null;	
		}
	}
	
	/** 
	 * Stop when encounter first byte not in bytes or to the buffer end. 
	 */
	private boolean skip(byte... bytes) throws ProtocolException {
		boolean done = false;
		
		int length = searchIndex - stateIndex;
		for (int i = searchIndex; i < buf.length(); i++, length++) {
			if (length > maxLineLength) { throw new ProtocolException(ProtocolExceptionType.LINE_LENGTH_LIMIT, maxLineLength); }
			
			byte b = buf.byteAt(i);
			stateIndex = searchIndex = i;
			if (ByteUtil.indexOf(bytes, b) < 0) {
				done = true;
				break;
			}
		}
		
		if (searchIndex == buf.length() - 1) {
			done = true;
		}
		
		return done;
	}
	
	private void slideIfMatch(byte b) throws ProtocolException {
		int length = searchIndex - stateIndex;
		while (b == currentByte()) {
			slide(1);
			length++;
			if (length > maxSize) { throw new ProtocolException(ProtocolExceptionType.MAX_SIZE_LIMIT, maxSize); }
		}
	}
	
	private void slide(int shift) {
		stateIndex = searchIndex += shift;
	}
	
	private void clear() {
		request = null;
		header = null;
		entity = null;
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	public HttpRequest getRequest() {
		return request;
	}

	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	public HttpHeader getHeader() {
		return header;
	}

	public void setHeader(HttpHeader header) {
		this.header = header;
	}

	@Override
	public String toString() {
		return String
				.format("HttpRequestDecoder [request=%s, header=%s, entity=%s, stateIndex=%s, state=%s, maxLineLength=%s, charset=%s, defaultBufferSize=%s, buf=%s, splitIndex=%s, searchIndex=%s, maxSize=%s]",
						request, header, entity, stateIndex, state,
						maxLineLength, charset, defaultBufferSize, buf,
						splitIndex, searchIndex, maxSize);
	}

}

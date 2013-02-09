package org.craft.atom.protocol.http;

import static org.craft.atom.protocol.http.model.HttpConstants.COLON;
import static org.craft.atom.protocol.http.model.HttpConstants.CONTENT_ENCODING_DEFLATE;
import static org.craft.atom.protocol.http.model.HttpConstants.CONTENT_ENCODING_GZIP;
import static org.craft.atom.protocol.http.model.HttpConstants.CONTENT_ENCODING_IDENTITY;
import static org.craft.atom.protocol.http.model.HttpConstants.CR;
import static org.craft.atom.protocol.http.model.HttpConstants.EQUAL_SIGN;
import static org.craft.atom.protocol.http.model.HttpConstants.HT;
import static org.craft.atom.protocol.http.model.HttpConstants.LF;
import static org.craft.atom.protocol.http.model.HttpConstants.NUL;
import static org.craft.atom.protocol.http.model.HttpConstants.SEMICOLON;
import static org.craft.atom.protocol.http.model.HttpConstants.SP;
import static org.craft.atom.protocol.http.model.HttpConstants.TRANSFER_ENCODING_CHUNKED;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.craft.atom.protocol.ProtocolDecoder;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.ProtocolExceptionType;
import org.craft.atom.protocol.http.model.HttpChunk;
import org.craft.atom.protocol.http.model.HttpChunkEntity;
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
	private HttpChunk chunk;
	private String chunkExtName;
	private int trailerSize;
	
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
			resetIndex();
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
	
	private void state4END(List<HttpRequest> reqs) throws ProtocolException {
		// enter END state means search index stay for the last byte of the HttpMessage, move to next
		slide(1);
		
		reqs.add(request);
		splitIndex = stateIndex = searchIndex;
		clear();
		state = START;
	}

	private void state4ENTITY_ENCODING() throws ProtocolException {
		HttpHeader ceh = request.getHeader(HttpHeaders.CONTENT_ENCODING.getName());
		String coding = null;
		if (ceh == null){
			coding = CONTENT_ENCODING_IDENTITY;
		} else {
			coding = ceh.getValue();
		}
		
		// none or identity
		if (coding == null || CONTENT_ENCODING_IDENTITY.equals(coding)) {
			request.getEntity().setContent(request.getEntity().getContent());
		}
		// gzip
		else if (CONTENT_ENCODING_GZIP.equals(coding)) {
			
		}
		// deflate
		else if (CONTENT_ENCODING_DEFLATE.equals(coding)) {
			/*
			 * A zlib stream will have a header.
			 * 
			 * CMF | FLG [| DICTID ] | ...compressed data | ADLER32 |
			 * 
			 * * CMF is one byte.
			 * 
			 * * FLG is one byte.
			 * 
			 * * DICTID is four bytes, and only present if FLG.FDICT is set.
			 * 
			 * Sniff the content. Does it look like a zlib stream, with a CMF, etc?
			 * c.f. RFC1950, section 2.2. http://tools.ietf.org/html/rfc1950#page-4
			 * 
			 * We need to see if it looks like a proper zlib stream, or whether it
			 * is just a deflate stream. RFC2616 calls zlib streams deflate.
			 * Confusing, isn't it? That's why some servers implement deflate
			 * Content-Encoding using deflate streams, rather than zlib streams.
			 * 
			 * We could start looking at the bytes, but to be honest, someone else
			 * has already read the RFCs and implemented that for us. So we'll just
			 * use the JDK libraries and exception handling to do this. If that
			 * proves slow, then we could potentially change this to check the first
			 * byte - does it look like a CMF? What about the second byte - does it
			 * look like a FLG, etc.
			 * 
			 * Many browsers over the years implemented an incorrect deflate algorithm, 
			 * For example: deflate works in Safari 4.0 but is broken in Safari 5.1, it also always has issues on IE.
			 * So, we don't support deflate encoding now.
			 */
			throw new ProtocolException(ProtocolExceptionType.UNEXPECTED, "unsupported content encoding=" + coding);
		}
		// compress or others encoding is unsupported
		else {
			throw new ProtocolException(ProtocolExceptionType.UNEXPECTED, "unsupported content encoding=" + coding);
		}
		
		// next state
		request.setEntity(entity);
		state = END;
	}
	
	private void state4ENTITY_CHUNKED_TRAILER_NAME() throws ProtocolException {
		boolean done = skip(CR, LF);
		if (!done) { 
			return; 
		}
		
		// slice header name
		String name = sliceBySeparators(0, COLON);
		if (name == null) {
			return;
		}

		header = new HttpHeader();
		header.setName(name);

		// to next state
		trailerSize--;
		state = ENTITY_CHUNKED_TRAILER_VALUE;
	}
	
	private void state4ENTITY_CHUNKED_TRAILER_VALUE() throws ProtocolException {
		// skip SP or HT
		boolean done = skip(SP, HT);
		if (!done) {
			return;
		}
		
		// slice header value
		String value = sliceBySeparators(-1, LF);
		if (value == null) {
			return;
		}
		
		header.appendValue(value);
		((HttpChunkEntity) entity).addTrailer(header);
		
		// to next state
		if (trailerSize == 0) {
			slide(-1);
			state = END;
		} else {
			state = ENTITY_CHUNKED_TRAILER_NAME;
		}
	}
	
	private void state4ENTITY_CHUNKED_DATA() throws ProtocolException {
		boolean done = skip(CR, LF);
		if (!done) { 
			return; 
		}
		
		// slice content value
		int clen = chunk.getSize();
		String chunkData = sliceByLength(clen);
		if (chunkData == null) {
			return;
		}
		
		chunk.setData(chunkData);
		((HttpChunkEntity) entity).addChunk(chunk);
		
		// skip CRLF
		slide(2);
		state = ENTITY_CHUNKED_SIZE;
	}
	
	private void state4ENTITY_CHUNKED_EXTENSION_VALUE() throws ProtocolException {
		// slice chunk extension value
		String chunkExtValue = sliceBySeparators(0, SEMICOLON, CR);
		if (chunkExtValue == null) {
			return;
		}
		
		chunk.addExtension(chunkExtName, chunkExtValue);
		
		byte pb = previousByte();
		if (SEMICOLON == pb) {
			state = ENTITY_CHUNKED_EXTENSION_NAME;
		} else {
			state = ENTITY_CHUNKED_DATA;
		}
	}
	
	private void state4ENTITY_CHUNKED_EXTENSION_NAME() throws ProtocolException {
		// slice chunk extension name
		String name = sliceBySeparators(0, EQUAL_SIGN, CR);
		if (name == null) {
			return;
		}
		
		this.chunkExtName = name;
		
		byte pb = previousByte();
		if (EQUAL_SIGN == pb) {
			state = ENTITY_CHUNKED_EXTENSION_VALUE;
		} else {
			chunk.addExtension(chunkExtName, null);
			state = ENTITY_CHUNKED_DATA;
		}
	}
	
	private void state4ENTITY_CHUNKED_SIZE() throws ProtocolException {		
		boolean done = skip(LF);
		if (!done) { 
			return; 
		}
		
		// slice chunk size
		String sizeStr = sliceBySeparators(0, SEMICOLON, CR);
		if (sizeStr == null) {
			return;
		}
		
		int size = Integer.parseInt(sizeStr, 16);
		if (size < 0) {
			throw new ProtocolException(ProtocolExceptionType.UNEXPECTED, "chunked size < 0");
		}
		
		chunk = new HttpChunk();
		chunk.setSize(size);
		
		byte pb = previousByte();
		if (SEMICOLON == pb) {
			state = ENTITY_CHUNKED_EXTENSION_NAME;
		} else if (size > 0){
			state = ENTITY_CHUNKED_DATA;
		} else if (size == 0) {
			HttpHeader trailerHeader = request.getHeader(HttpHeaders.TRAILER.getName());
			request.setEntity(entity);
			if (trailerHeader != null) {
				trailerSize = trailerHeader.getValue().split(",").length;
				if (trailerSize <= 0) {
					throw new ProtocolException(ProtocolExceptionType.UNEXPECTED, "trailer size ilegal=" + trailerSize);
				}
				state = ENTITY_CHUNKED_TRAILER_NAME;
			} else {
				state = ENTITY_ENCODING;
			}
		}
	}
	
	private void state4ENTITY_LENGTH() throws ProtocolException {
		// get content length
		int clen = Integer.parseInt(request.getHeader(HttpHeaders.CONTENT_LENGTH.getName()).getValue());
		if (clen < 0) {
			throw new ProtocolException(ProtocolExceptionType.UNEXPECTED, "content length < 0");
		}
		
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
		boolean done = skip(CR, LF);
		if (!done) { 
			return; 
		}
		
		// content length
		if (request.getHeader(HttpHeaders.CONTENT_LENGTH.getName()) != null) {
			entity = new HttpEntity();
			state = ENTITY_LENGTH;
		}
		// chunked
		else if (TRANSFER_ENCODING_CHUNKED.equals(request.getHeader(HttpHeaders.TRANSFER_ENCODING.getName()).getValue())) {
			entity = new HttpChunkEntity();
			state = ENTITY_CHUNKED_SIZE;
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
			state = HEADER_VALUE_SUFFIX;
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
	
	private byte previousByte() {
		int i = searchIndex - 1;
		if ( i < buf.length()) {
			return buf.byteAt(i);
		} else {
			return NUL;
		}
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
		
		int tailIndex = offset + len - 1;
		if (tailIndex < buf.length()) {
			stateIndex = searchIndex = tailIndex;
			done = true;
		} else {
			searchIndex = buf.length();
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
	
	private boolean skip(byte... bytes) throws ProtocolException {
		boolean done = false;
		int length = searchIndex - stateIndex;
		while (searchIndex < buf.length()) {
			byte cb = currentByte();
			if (ByteUtil.indexOf(bytes, cb) < 0 && NUL != cb) {
				done = true;
				break;
			}
			slide(1);
			length++;
			if (length > maxSize) { throw new ProtocolException(ProtocolExceptionType.MAX_SIZE_LIMIT, maxSize); }
		}
		return done;
	}
	
	private void slide(int shift) throws ProtocolException {
		stateIndex = searchIndex += shift;
		if (searchIndex > maxSize) {
			throw new ProtocolException(ProtocolExceptionType.MAX_SIZE_LIMIT, maxSize);
		}
	}
	
	private void clear() {
		request = null;
		header = null;
		entity = null;
		chunk = null;
		chunkExtName = null;
		trailerSize = 0;
	}
	
	private void resetIndex() {
		searchIndex = splitIndex = stateIndex = 0;
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

	public HttpEntity getEntity() {
		return entity;
	}

	public void setEntity(HttpEntity entity) {
		this.entity = entity;
	}

	public HttpChunk getChunk() {
		return chunk;
	}

	public void setChunk(HttpChunk chunk) {
		this.chunk = chunk;
	}

	public String getChunkExtName() {
		return chunkExtName;
	}

	public void setChunkExtName(String chunkExtName) {
		this.chunkExtName = chunkExtName;
	}

	@Override
	public String toString() {
		return String
				.format("HttpRequestDecoder [request=%s, header=%s, entity=%s, chunk=%s, chunkExtName=%s, stateIndex=%s, state=%s, maxLineLength=%s, defaultBufferSize=%s, buf=%s, splitIndex=%s, searchIndex=%s, maxSize=%s, charset=%s]",
						request, header, entity, chunk, chunkExtName,
						stateIndex, state, maxLineLength, defaultBufferSize,
						buf, splitIndex, searchIndex, maxSize, charset);
	}

}

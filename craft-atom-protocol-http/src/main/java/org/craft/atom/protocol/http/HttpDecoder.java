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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.craft.atom.protocol.AbstractProtocolDecoder;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.ProtocolExceptionType;
import org.craft.atom.protocol.http.model.HttpChunk;
import org.craft.atom.protocol.http.model.HttpChunkEntity;
import org.craft.atom.protocol.http.model.HttpConstants;
import org.craft.atom.protocol.http.model.HttpContentType;
import org.craft.atom.protocol.http.model.HttpEntity;
import org.craft.atom.protocol.http.model.HttpHeader;
import org.craft.atom.protocol.http.model.HttpHeaderType;
import org.craft.atom.protocol.http.model.HttpHeaderValueElement;
import org.craft.atom.protocol.http.model.HttpMessage;
import org.craft.atom.protocol.http.model.MimeType;
import org.craft.atom.util.ByteUtil;
import org.craft.atom.util.GzipUtil;

/**
 * A http decoder for {@code HttpRequest} and {@code HttpResponse}
 * 
 * @author mindwind
 * @version 1.0, Feb 3, 2013
 * @param <T>
 * @see HttpRequestDecoder
 * @see HttpResponseDecoder
 */
abstract public class HttpDecoder<T extends HttpMessage>  extends AbstractProtocolDecoder {
	
	protected static final int START = 0;
	protected static final int METHOD = 11;
	protected static final int REQUEST_URI = 12;
	protected static final int STATUS_CODE = 21;
	protected static final int REASON_PHRASE = 22;
	protected static final int VERSION = 30;
	protected static final int HEADER_NAME = 40;
	protected static final int HEADER_VALUE_PREFIX = 41;
	protected static final int HEADER_VALUE = 42;
	protected static final int HEADER_VALUE_SUFFIX = 43;
	protected static final int ENTITY = 50;
	protected static final int ENTITY_LENGTH = 51;
	protected static final int ENTITY_CHUNKED_SIZE = 52;
	protected static final int ENTITY_CHUNKED_EXTENSION_NAME = 53;
	protected static final int ENTITY_CHUNKED_EXTENSION_VALUE = 54;
	protected static final int ENTITY_CHUNKED_DATA = 55;
	protected static final int ENTITY_CHUNKED_TRAILER_NAME = 56;
	protected static final int ENTITY_CHUNKED_TRAILER_VALUE = 57;
	protected static final int ENTITY_ENCODING = 58;
	protected static final int END = -1;
	
	protected int stateIndex = 0;
	protected int state = START;
	protected int maxLineLength = defaultBufferSize;
	protected int trailerSize;
	protected HttpHeader header;
	protected HttpEntity entity;
	protected HttpChunk chunk;
	protected HttpContentType contentType;
	protected String chunkExtName;
	protected T httpMessage;
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	protected void state4END(List<T> httpMessages) throws ProtocolException {
		// enter END state means search index stay for the last byte of the HttpMessage, move to next
		slide(1);
		
		httpMessages.add(httpMessage);
		splitIndex = stateIndex = searchIndex;
		clear();
		state = START;
	}
	
	protected void state4ENTITY_ENCODING() throws ProtocolException, IOException {
		HttpHeader ceh = httpMessage.getFirstHeader(HttpHeaderType.CONTENT_ENCODING.getName());
		String coding = null;
		if (ceh == null){
			coding = CONTENT_ENCODING_IDENTITY;
		} else {
			coding = ceh.getValue();
		}
		
		// none or identity
		if (coding == null || CONTENT_ENCODING_IDENTITY.equals(coding)) {
			// if content is chunked, getContent() will restructure chunked content to a complete content.
			httpMessage.getEntity().setContent(httpMessage.getEntity().getContent());
		}
		// gzip
		else if (CONTENT_ENCODING_GZIP.equals(coding)) {
			byte[] content = GzipUtil.ungzip(httpMessage.getEntity().getContent());
			httpMessage.getEntity().setContent(content);
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
		httpMessage.setEntity(entity);
		state = END;
	}
	
	protected void state4ENTITY_CHUNKED_TRAILER_NAME() throws ProtocolException {
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
	
	protected void state4ENTITY_CHUNKED_TRAILER_VALUE() throws ProtocolException {
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
	
	protected void state4ENTITY_CHUNKED_DATA() throws ProtocolException {
		boolean done = skip(CR, LF);
		if (!done) { 
			return; 
		}
		
		// slice content value
		int clen = chunk.getSize();
		byte[] chunkData = sliceByLength(clen);
		if (chunkData == null) {
			return;
		}
		
		chunk.setData(chunkData);
		((HttpChunkEntity) entity).addChunk(chunk);
		
		// skip CRLF
		slide(2);
		state = ENTITY_CHUNKED_SIZE;
	}
	
	protected void state4ENTITY_CHUNKED_EXTENSION_VALUE() throws ProtocolException {
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
	
	protected void state4ENTITY_CHUNKED_EXTENSION_NAME() throws ProtocolException {
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
	
	protected void state4ENTITY_CHUNKED_SIZE() throws ProtocolException {		
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
			HttpHeader trailerHeader = httpMessage.getFirstHeader(HttpHeaderType.TRAILER.getName());
			httpMessage.setEntity(entity);
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
	
	protected void state4ENTITY_LENGTH() throws ProtocolException {
		// get content length
		int clen = Integer.parseInt(httpMessage.getFirstHeader(HttpHeaderType.CONTENT_LENGTH.getName()).getValue());
		if (clen < 0) {
			throw new ProtocolException(ProtocolExceptionType.UNEXPECTED, "content length < 0");
		}
		
		// slice content value
		byte[] content = sliceByLength(clen);
		if (content == null) {
			return;
		}
		
		// render current request with entity
		entity.setContent(content);
		httpMessage.setEntity(entity);
		
		// to next state
		state = ENTITY_ENCODING;
	}
	
	protected void state4ENTITY() throws ProtocolException {
		boolean done = skip(CR, LF);
		if (!done) { 
			return; 
		}
		
		// content length
		if (httpMessage.getFirstHeader(HttpHeaderType.CONTENT_LENGTH.getName()) != null) {
			entity = new HttpEntity();
			entity.setContentType(getContentType(httpMessage));
			state = ENTITY_LENGTH;
		}
		// chunked
		else if (TRANSFER_ENCODING_CHUNKED.equals(httpMessage.getFirstHeader(HttpHeaderType.TRANSFER_ENCODING.getName()).getValue())) {
			entity = new HttpChunkEntity();
			entity.setContentType(getContentType(httpMessage));
			state = ENTITY_CHUNKED_SIZE;
		}
		// no entity
		else {
			state = END;
		}
	}
	
	protected void state4HEADER_VALUE_SUFFIX() throws ProtocolException {
		byte cb = currentByte();
		
		// folded header
		if (SP == cb || HT == cb) {
			state = HEADER_VALUE_PREFIX;
		}
		// header end
		else if (CR == cb) {
			httpMessage.addHeader(header);
			state = hasEntity(httpMessage) ? ENTITY : END;
			slide(1);
		}
		// next header
		else {
			httpMessage.addHeader(header);
			state = HEADER_NAME;
		}
	}
	
	protected void state4HEADER_VALUE() throws ProtocolException {
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
			httpMessage.addHeader(header);
			state = hasEntity(httpMessage) ? ENTITY : END;
			slide(1);
		}
		// next header
		else {
			httpMessage.addHeader(header);
		   state = HEADER_NAME;
		}
	}
	
	protected void state4HEADER_VALUE_PREFIX() throws ProtocolException {
		// skip SP or HT 
		boolean done = skip(SP, HT);
		if (!done) { 
			return; 
		}
		
		// to next state
		state = HEADER_VALUE;
	}
	
	protected void state4HEADER_NAME() throws ProtocolException {
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
	
	protected void reset() {
		if (splitIndex > 0 && splitIndex < buf.length()) {
			byte[] tailBytes = buf.array(splitIndex, buf.length());
			buf.clear();
			buf.append(tailBytes);
			stateIndex -= splitIndex;
			searchIndex = buf.length();
			splitIndex = 0;
		}
		
		if (splitIndex > 0 && splitIndex == buf.length()) {
			buf.clear();
			stateIndex = splitIndex = searchIndex = 0;
		}
		
		if (buf.length() == 0 && buf.capacity() > maxSize * 2) {
			buf.reset(defaultBufferSize);
		}
	}
	
	protected HttpContentType getContentType(HttpMessage httpMessage) {
		if (contentType != null) {
			return contentType;
		}
		
		// No Content-Type header
		HttpHeader contentTypeHeader = httpMessage.getFirstHeader(HttpHeaderType.CONTENT_TYPE.getName());
		if (contentTypeHeader == null) {
			contentType = new HttpContentType(charset);
			return contentType;
		}
		
		// value element is null, e.g. Content-Type: 
		List<HttpHeaderValueElement> elements = contentTypeHeader.getValueElements();
		if (elements.isEmpty()) {
			contentType = new HttpContentType(charset);
			return contentType;
		}
		
		// e.g. Content-Type: text/plain; charset=utf-8
		HttpHeaderValueElement element = elements.get(0);
		Charset contentCharset = charset;
		String mimeType = element.getName();
		String charsetName = element.getParamValue(HttpConstants.CHARSET);
		if (charsetName != null) {
			contentCharset = Charset.forName(charsetName);
		}
		contentType = new HttpContentType(MimeType.from(mimeType), contentCharset);
		return contentType;
	}
	
	protected byte previousByte() {
		int i = searchIndex - 1;
		if ( i < buf.length()) {
			return buf.byteAt(i);
		} else {
			return NUL;
		}
	}
	
	protected byte currentByte() {
		int i = searchIndex;
		if ( i < buf.length()) {
			return buf.byteAt(i);
		} else {
			return NUL;
		}
	}
	
	protected byte nextByte() {
		int i = searchIndex + 1;
		if (i < buf.length()) {
			return buf.byteAt(i);
		} else {
			return NUL;
		}
	}
	
	protected byte[] sliceByLength(int len) throws ProtocolException {
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
			byte[] bytes = new byte[len];
			System.arraycopy(buf.buffer(), offset, bytes, 0, len);
			return bytes;
		} else {
			return null;	
		}
	}
	
	protected String sliceBySeparators(int shift, byte... separators) throws ProtocolException {
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
	
	protected boolean skip(byte... bytes) throws ProtocolException {
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
	
	protected void slide(int shift) throws ProtocolException {
		stateIndex = searchIndex += shift;
		if (searchIndex > maxSize) {
			throw new ProtocolException(ProtocolExceptionType.MAX_SIZE_LIMIT, maxSize);
		}
	}
	
	protected void clear() {
		header = null;
		entity = null;
		chunk = null;
		chunkExtName = null;
		trailerSize = 0;
		httpMessage = null;
	}
	
	protected void resetIndex() {
		searchIndex = splitIndex = stateIndex = 0;
	}
	
	abstract boolean hasEntity(T httpMessage);
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	public int getMaxLineLength() {
		return maxLineLength;
	}

	public void setMaxLineLength(int maxLineLength) {
		this.maxLineLength = maxLineLength;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
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
				.format("HttpDecoder [stateIndex=%s, state=%s, maxLineLength=%s, trailerSize=%s, header=%s, entity=%s, chunk=%s, contentType=%s, chunkExtName=%s, httpMessage=%s, defaultBufferSize=%s, buf=%s, splitIndex=%s, searchIndex=%s, maxSize=%s, charset=%s]",
						stateIndex, state, maxLineLength, trailerSize, header,
						entity, chunk, contentType, chunkExtName, httpMessage,
						defaultBufferSize, buf, splitIndex, searchIndex,
						maxSize, charset);
	}

}

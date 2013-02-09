package org.craft.atom.protocol.http;

import org.craft.atom.protocol.AbstractProtocolDecoder;

/**
 * A http decoder for {@code HttpRequest} and {@code HttpResponse}
 * 
 * @author mindwind
 * @version 1.0, Feb 3, 2013
 * @see HttpRequestDecoder
 * @see HttpResponseDecoder
 */
abstract public class HttpDecoder extends AbstractProtocolDecoder {
	
	protected static final int START = 0;
	protected static final int METHOD = 1;
	protected static final int REQUEST_URI = 2;
	protected static final int VERSION = 3;
	protected static final int HEADER_NAME = 4;
	protected static final int HEADER_VALUE_PREFIX = 5;
	protected static final int HEADER_VALUE = 6;
	protected static final int HEADER_VALUE_SUFFIX = 7;
	protected static final int ENTITY = 8;
	protected static final int ENTITY_LENGTH = 9;
	protected static final int ENTITY_CHUNKED_SIZE = 10;
	protected static final int ENTITY_CHUNKED_EXTENSION_NAME = 11;
	protected static final int ENTITY_CHUNKED_EXTENSION_VALUE = 12;
	protected static final int ENTITY_CHUNKED_DATA = 13;
	protected static final int ENTITY_CHUNKED_TRAILER_NAME = 14;
	protected static final int ENTITY_CHUNKED_TRAILER_VALUE = 15;
	protected static final int ENTITY_ENCODING = 16;
	protected static final int END = -1;
	
	protected int stateIndex = 0;
	protected int state = START;
	protected int maxLineLength = defaultBufferSize;
	
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

}

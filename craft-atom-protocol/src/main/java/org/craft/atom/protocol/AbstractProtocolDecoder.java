package org.craft.atom.protocol;

import org.craft.atom.util.ByteArrayBuffer;

/**
 * @author mindwind
 * @version 1.0, Feb 3, 2013
 */
abstract public class AbstractProtocolDecoder extends AbstractProtocolCodec {
	
	protected int defaultBufferSize = 2048;
	protected ByteArrayBuffer buf = new ByteArrayBuffer(defaultBufferSize);
	protected int splitIndex = 0;
	protected int searchIndex = 0;
	protected int maxSize = defaultBufferSize * 10;
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	protected void reset() {
		if (splitIndex > 0 && splitIndex < buf.length()) {
			byte[] tailBytes = buf.array(splitIndex, buf.length());
			buf.clear();
			buf.append(tailBytes);
			splitIndex = 0;
			searchIndex = buf.length();
		}
		
		if (splitIndex > 0 && splitIndex == buf.length()) {
			buf.clear();
			splitIndex = searchIndex = 0;
		}
		
		if (buf.length() == 0 && buf.capacity() > maxSize * 2) {
			buf.reset(defaultBufferSize);
		}
	}
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public int getDefaultBufferSize() {
		return defaultBufferSize;
	}

	public void setDefaultBufferSize(int defaultBufferSize) {
		this.defaultBufferSize = defaultBufferSize;
	}

	public ByteArrayBuffer getBuf() {
		return buf;
	}

	public void setBuf(ByteArrayBuffer buf) {
		this.buf = buf;
	}

	public int getSplitIndex() {
		return splitIndex;
	}

	public void setSplitIndex(int splitIndex) {
		this.splitIndex = splitIndex;
	}

	public int getSearchIndex() {
		return searchIndex;
	}

	public void setSearchIndex(int searchIndex) {
		this.searchIndex = searchIndex;
	}

	@Override
	public String toString() {
		return String
				.format("AbstractProtocolDecoder [charset=%s, defaultBufferSize=%s, buf=%s, splitIndex=%s, searchIndex=%s, maxSize=%s]",
						charset, defaultBufferSize, buf, splitIndex,
						searchIndex, maxSize);
	}
	
}

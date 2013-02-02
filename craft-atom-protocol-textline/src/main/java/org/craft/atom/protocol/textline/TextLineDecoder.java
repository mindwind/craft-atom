package org.craft.atom.protocol.textline;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.craft.atom.protocol.ProtocolDecoder;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.util.ByteArrayBuffer;

/**
 * A {@link ProtocolDecoder} which decodes bytes into text line string, default charset is utf-8
 * <br>
 * Not thread safe
 * 
 * @author Hu Feng
 * @version 1.0, Oct 16, 2012
 */
public class TextLineDecoder implements ProtocolDecoder<String> {
	
	private Charset charset = Charset.forName("utf-8");
	private String delimiter = "\n";
	private byte[] delimiterBytes = delimiter.getBytes(charset);
	private int delimiterLen = delimiterBytes.length;
	private int defaultBufferSize = 1024;
	private int maxLineLength = defaultBufferSize * 5;
	private ByteArrayBuffer buf = new ByteArrayBuffer(maxLineLength);
	private int splitIndex = 0;
	private int searchIndex = 0;
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	public TextLineDecoder() {
		super();
	}

	public TextLineDecoder(Charset charset) {
		this.charset = charset;
	}

	public TextLineDecoder(Charset charset, String delimiter) {
		this(charset);
		this.delimiter = delimiter;
		this.delimiterBytes = delimiter.getBytes(this.charset);
		this.delimiterLen = this.delimiterBytes.length;
	}

	public TextLineDecoder(Charset charset, String delimiter, int defaultBufferSize) {
		this(charset, delimiter);
		this.defaultBufferSize = defaultBufferSize;
	}

	public TextLineDecoder(Charset charset, String delimiter, int defaultBufferSize, int maxLineLength) {
		this(charset, delimiter, defaultBufferSize);
		this.maxLineLength = maxLineLength;
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------

	@Override
	public List<String> decode(byte[] bytes) throws ProtocolException {
		if (bytes.length > maxLineLength) {
			throw new ProtocolException("Line is too long, maxLineLength=" + maxLineLength );
		}
		List<String> strs = new ArrayList<String>();
		
		reset();
		buf.append(bytes);
		while (splitIndex < buf.length()) {
			int idx = buf.indexOf(delimiterBytes, searchIndex);
			if (idx < 0) {
				if (buf.length() > maxLineLength) {
					buf.reset(maxLineLength);
					throw new ProtocolException("Line is too long, maxLineLength=" + maxLineLength );
				}
				searchIndex = buf.length();
				break;
			}
			byte[] lineBytes = buf.array(splitIndex, idx);
			if (lineBytes.length > maxLineLength) {
				buf.reset(maxLineLength);
				throw new ProtocolException("Line is too long, maxLineLength=" + maxLineLength );
			}
			searchIndex = splitIndex = idx + delimiterLen;
			strs.add(new String(lineBytes, charset));
		}
		
		return strs;
	}
	
	private void reset() {
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
		if (buf.capacity() > maxLineLength * 2) {
			buf.reset(defaultBufferSize);
		}
	}
	
	// ~ ----------------------------------------------------------------------------------------------------------

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public int getMaxLineLength() {
		return maxLineLength;
	}

	public void setMaxLineLength(int maxLineLength) {
		this.maxLineLength = maxLineLength;
		buf.reset(maxLineLength);
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

	@Override
	public String toString() {
		return String
				.format("TextLineDecoder [charset=%s, delimiter=%s, delimiterBytes=%s, delimiterLen=%s, defaultBufferSize=%s, maxLineLength=%s, buf=%s, splitIndex=%s, searchIndex=%s]",
						charset, delimiter, Arrays.toString(delimiterBytes),
						delimiterLen, defaultBufferSize, maxLineLength, buf,
						splitIndex, searchIndex);
	}

}

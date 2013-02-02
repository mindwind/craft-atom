package org.craft.atom.protocol.textline;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.craft.atom.protocol.ProtocolDecoder;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.util.ByteArrayBuffer;

/**
 * A {@link ProtocolDecoder} which decodes a text line into a string, default charset is utf-8
 * <br>
 * Not thread safe
 * 
 * @author Hu Feng
 * @version 1.0, Oct 16, 2012
 */
public class TextLineDecoder implements ProtocolDecoder<String> {
	
	private static final int DEFAULT_BUFFER_LENGTH = 1024;
	private Charset charset = Charset.forName("utf-8");
	private String delimiter = "\n";
	private byte[] delimiterBytes = delimiter.getBytes(charset);
	private int delimiterLen = delimiterBytes.length;
	private int maxLineLength = DEFAULT_BUFFER_LENGTH * 5;
	private ByteArrayBuffer buf = new ByteArrayBuffer(maxLineLength);
	private int splitIndex = 0;
	private int searchIndex = 0;
	
	public TextLineDecoder() {
		super();
	}

	public TextLineDecoder(String charset, String delimiter, int maxLineLength) {
		this.charset = Charset.forName(charset);
		this.delimiter = delimiter;
		this.delimiterBytes = delimiter.getBytes(this.charset);
		this.delimiterLen = this.delimiterBytes.length;
		this.maxLineLength = maxLineLength;
	}

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
			buf.reset(DEFAULT_BUFFER_LENGTH);
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
	
	

}

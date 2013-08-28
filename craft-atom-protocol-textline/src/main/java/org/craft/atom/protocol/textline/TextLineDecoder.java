package org.craft.atom.protocol.textline;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.craft.atom.protocol.AbstractProtocolDecoder;
import org.craft.atom.protocol.ProtocolDecoder;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.ProtocolExceptionType;

/**
 * A {@link ProtocolDecoder} which decodes bytes into text line string, default charset is utf-8
 * <br>
 * Not thread safe
 * 
 * @author Hu Feng
 * @version 1.0, Oct 16, 2012
 */
@ToString(callSuper = true, of = { "delimiter" })
public class TextLineDecoder extends AbstractProtocolDecoder implements ProtocolDecoder<String> {
	
	@Getter @Setter private String delimiter = "\n";
	private byte[] delimiterBytes = delimiter.getBytes(charset);
	private int delimiterLen = delimiterBytes.length;
	
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
		buf.reset(defaultBufferSize);
	}

	public TextLineDecoder(Charset charset, String delimiter, int defaultBufferSize, int maxLineLength) {
		this(charset, delimiter, defaultBufferSize);
		this.maxSize = maxLineLength;
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------

	@Override
	public List<String> decode(byte[] bytes) throws ProtocolException {
		List<String> strs = new ArrayList<String>();
		reset();
		buf.append(bytes);
		
		while (searchIndex < buf.length()) {
			int idx = buf.indexOf(delimiterBytes, searchIndex);
			if (idx < 0) {
				if (buf.length() > maxSize) {
					buf.reset(defaultBufferSize);
					throw new ProtocolException(ProtocolExceptionType.LINE_LENGTH_LIMIT, maxSize);
				}
				searchIndex = buf.length();
				break;
			}
			byte[] lineBytes = buf.array(splitIndex, idx);
			if (lineBytes.length > maxSize) {
				buf.reset(defaultBufferSize);
				throw new ProtocolException(ProtocolExceptionType.LINE_LENGTH_LIMIT, maxSize);
			}
			searchIndex = splitIndex = idx + delimiterLen;
			strs.add(new String(lineBytes, charset));
		}
		
		return strs;
	}

}

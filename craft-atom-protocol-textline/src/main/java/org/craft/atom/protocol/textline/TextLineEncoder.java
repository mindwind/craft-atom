package org.craft.atom.protocol.textline;

import java.nio.charset.Charset;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.craft.atom.protocol.AbstractProtocolEncoder;
import org.craft.atom.protocol.ProtocolEncoder;
import org.craft.atom.protocol.ProtocolException;

/**
 * A {@link ProtocolEncoder} which encodes a text line string into bytes which ends with the delimiter, default charset is utf-8.
 * <br>
 * Thread safe.
 * 
 * @author Hu Feng
 * @version 1.0, Oct 16, 2012
 */
@ToString(callSuper = true, of = { "delimiter" })
public class TextLineEncoder extends AbstractProtocolEncoder implements ProtocolEncoder<String> {
	
	@Getter @Setter private String delimiter = "\n";
	private int maxLineLength = Integer.MAX_VALUE;
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	public TextLineEncoder() {
		super();
	}
	
	public TextLineEncoder(Charset charset) {
		this.charset = charset;
	}

	public TextLineEncoder(Charset charset, String delimiter) {
		this(charset);
		this.delimiter = delimiter;
	}

	public TextLineEncoder(Charset charset, String delimiter, int maxLineLength) {
		this(charset, delimiter);
		this.maxLineLength = maxLineLength;
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------

	@Override
	public byte[] encode(String str) throws ProtocolException {
		str += delimiter;
		byte[] lineBytes = str.getBytes(charset);
		if (lineBytes.length > maxLineLength) {
			throw new ProtocolException("Line is too long, maxLineLength=" + maxLineLength );
		}
		
		return lineBytes;
	}

}

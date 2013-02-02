package org.craft.atom.protocol.textline;

import java.nio.charset.Charset;

import org.craft.atom.protocol.ProtocolEncoder;
import org.craft.atom.protocol.ProtocolException;

/**
 * A {@link ProtocolEncoder} which encodes a string into a text line which ends with the delimiter.
 * 
 * @author Hu Feng
 * @version 1.0, Oct 16, 2012
 */
public class TextLineEncoder implements ProtocolEncoder<String> {
	
	private Charset charset = Charset.forName("utf-8");
	private String delimiter = "\n";
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
	public byte[] encode(String protocolObject) throws ProtocolException {
		protocolObject += delimiter;
		byte[] lineBytes = protocolObject.getBytes(charset);
		if (lineBytes.length > maxLineLength) {
			throw new ProtocolException("Line is too long, maxLineLength=" + maxLineLength );
		}
		
		return lineBytes;
	}

	@Override
	public String toString() {
		return String.format("TextLineEncoder [charset=%s, delimiter=%s, maxLineLength=%s]", charset, delimiter, maxLineLength);
	}

}

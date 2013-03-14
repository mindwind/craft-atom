package org.craft.atom.protocol;

import java.nio.charset.Charset;

/**
 * @author mindwind
 * @version 1.0, Feb 6, 2013
 */
public class AbstractProtocolCodec {
	
	protected Charset charset = Charset.forName("utf-8");

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	@Override
	public String toString() {
		return String.format("AbstractProtocolCodec [charset=%s]", charset);
	}
	
}

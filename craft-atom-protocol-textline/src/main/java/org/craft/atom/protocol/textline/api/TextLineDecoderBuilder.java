package org.craft.atom.protocol.textline.api;

import java.nio.charset.Charset;

import org.craft.atom.protocol.textline.TextLineDecoder;

/**
 * @author mindwind
 * @version 1.0, Aug 5, 2014
 */
public class TextLineDecoderBuilder {

	
	private String  delimiter        ;
	private Charset charset          ;
	private int     defaultBufferSize;
	private int     maxSize          ;
	
	
	public TextLineDecoderBuilder(Charset charset, String delimiter) {
		this.delimiter = delimiter;
		this.charset   = charset;
	}
	
	
	public TextLineDecoderBuilder defaultBufferSize(int defaultBufferSize) { this.defaultBufferSize = defaultBufferSize; return this; } 
	public TextLineDecoderBuilder maxSize          (int maxSize)           { this.maxSize           = maxSize          ; return this; }
	
	
	public TextLineDecoder build() {
		return new TextLineDecoder(charset, delimiter, defaultBufferSize, maxSize);
	}
	
}

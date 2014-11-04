package io.craft.atom.protocol.textline.api;

import io.craft.atom.protocol.ProtocolDecoder;
import io.craft.atom.protocol.textline.TextLineDecoder;

import java.nio.charset.Charset;


/**
 * @author mindwind
 * @version 1.0, Aug 5, 2014
 */
public class TextLineDecoderBuilder {

	
	private String  delimiter         = "\n"                    ;
	private Charset charset           = Charset.forName("utf-8");
	private int     defaultBufferSize = 2048                    ;
	private int     maxSize           = defaultBufferSize * 1024;
	
	
	public TextLineDecoderBuilder(Charset charset, String delimiter) {
		this.delimiter = delimiter;
		this.charset   = charset;
	}
	
	
	public TextLineDecoderBuilder defaultBufferSize(int defaultBufferSize) { this.defaultBufferSize = defaultBufferSize; return this; } 
	public TextLineDecoderBuilder maxSize          (int maxSize)           { this.maxSize           = maxSize          ; return this; }
	
	
	public ProtocolDecoder<String> build() {
		return new TextLineDecoder(charset, delimiter, defaultBufferSize, maxSize);
	}
	
}

package io.craft.atom.protocol.textline.api;

import io.craft.atom.protocol.ProtocolDecoder;
import io.craft.atom.protocol.ProtocolEncoder;
import io.craft.atom.protocol.textline.TextLineDecoder;
import io.craft.atom.protocol.textline.TextLineEncoder;

import java.nio.charset.Charset;


/**
 * TextLine codec factory, which provides static factory method to create {@link ProtocolEncoder<String>} and {@link ProtocolDecoder<String>}.
 * 
 * @author mindwind
 * @version 1.0, Aug 4, 2014
 */
public class TextLineCodecFactory {

	
	public static ProtocolEncoder<String> newTextLineEncoder(Charset charset, String delimiter) {
		return new TextLineEncoder(charset, delimiter);
	}
	
	public static ProtocolDecoder<String> newTextLineDecoder(Charset charset, String delimiter) {
		return new TextLineDecoder(charset, delimiter);
	}
	
	public static TextLineDecoderBuilder newTextLineDecoderBuilder(Charset charset, String delimiter) {
		return new TextLineDecoderBuilder(charset, delimiter);
	}
	
}

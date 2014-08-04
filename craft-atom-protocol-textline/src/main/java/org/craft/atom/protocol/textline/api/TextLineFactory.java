package org.craft.atom.protocol.textline.api;

import java.nio.charset.Charset;

import org.craft.atom.protocol.ProtocolDecoder;
import org.craft.atom.protocol.ProtocolEncoder;
import org.craft.atom.protocol.textline.TextLineDecoder;
import org.craft.atom.protocol.textline.TextLineEncoder;

/**
 * TextLine factory
 * 
 * @author mindwind
 * @version 1.0, Aug 4, 2014
 */
public class TextLineFactory {

	
	public static ProtocolEncoder<String> newTextLineEncoder(Charset charset, String delimiter) {
		return new TextLineEncoder(charset, delimiter);
	}
	
	public static ProtocolDecoder<String> newTextLineDecoder(Charset charset, String delimiter) {
		return new TextLineDecoder(charset, delimiter);
	}
	
}

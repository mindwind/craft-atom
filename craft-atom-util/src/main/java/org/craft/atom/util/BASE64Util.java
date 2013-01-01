package org.craft.atom.util;

import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * BASE64 codec class.
 *
 * @see org.apache.commons.codec.binary.Base64
 * @author Hu Feng
 * @version 1.0, 2011-10-17
 * @deprecated apache commons-codec may be a better choice
 */
@SuppressWarnings({ "restriction" })
@Deprecated
public class BASE64Util {

	/**
	 * Decode by BASE64 
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static byte[] decode(String data) throws IOException {
		return (new BASE64Decoder()).decodeBuffer(data);
	}

	/**
	 * Encode by BASE64
	 * 
	 * @param key
	 * @return
	 */
	public static String encode(byte[] data) {
		return (new BASE64Encoder()).encodeBuffer(data);
	}

}

package io.craft.atom.protocol.http;

import static io.craft.atom.protocol.http.HttpConstants.S_AMPERSAND;
import static io.craft.atom.protocol.http.HttpConstants.S_EQUAL_SIGN;

import io.craft.atom.protocol.AbstractProtocolCodec;
import io.craft.atom.protocol.ProtocolEncoder;
import io.craft.atom.protocol.ProtocolException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.ToString;


/**
 * A {@link ProtocolEncoder} which encodes a {@code Map<String, List<String>>} object into bytes 
 * with the <CODE>application/x-www-form-urlencoded</CODE> MIME format follow the HTTP specification, default charset is utf-8.
 * <br>
 * Thread safe.
 * 
 * @author mindwind
 * @version 1.0, Mar 25, 2013
 */
@ToString(callSuper = true)
public class HttpParameterEncoder extends AbstractProtocolCodec implements ProtocolEncoder<Map<String, List<String>>> {
	
	
	public HttpParameterEncoder() {}
	
	public HttpParameterEncoder(Charset charset) {
		this.charset = charset;
	}

	@Override
	public byte[] encode(Map<String, List<String>> paras) throws ProtocolException {
		if (paras == null) return null;
		StringBuilder buf = new StringBuilder();
		Set<Entry<String, List<String>>> entrys = paras.entrySet();
		for (Entry<String, List<String>> entry : entrys) {
			String name = entry.getKey();
			List<String> values = entry.getValue();
			for (String value : values) {
				try {
					name = URLEncoder.encode(name, charset.name());
					value = URLEncoder.encode(value, charset.name());
					buf.append(name).append(S_EQUAL_SIGN).append(value).append(S_AMPERSAND);
				} catch (UnsupportedEncodingException e) {
					throw new ProtocolException(e);
				}
			}
		}
		
		// delete last & char
		buf.deleteCharAt(buf.length() - 1);
		return buf.toString().getBytes(charset);
	}

}

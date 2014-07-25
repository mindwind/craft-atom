package org.craft.atom.protocol.http;

import static org.craft.atom.protocol.http.HttpConstants.AMPERSAND;
import static org.craft.atom.protocol.http.HttpConstants.EQUAL_SIGN;
import static org.craft.atom.protocol.http.HttpConstants.PERCENT_SIGN;
import static org.craft.atom.protocol.http.HttpConstants.PLUS_SIGN;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.ToString;

import org.craft.atom.protocol.AbstractProtocolCodec;
import org.craft.atom.protocol.ProtocolDecoder;
import org.craft.atom.protocol.ProtocolException;

/**
 * A {@link ProtocolDecoder} which decodes cookie string bytes into {@code Map<String, List<String>>} object, default charset is utf-8.
 * <br>
 * Only accept complete parameter bytes to decode, because this implementation is stateless and thread safe.
 * 
 * @author mindwind
 * @version 1.0, Mar 26, 2013
 */
@ToString(callSuper = true)
public class HttpParameterDecoder extends AbstractProtocolCodec implements ProtocolDecoder<Map<String, List<String>>> {
	
	
	private static final int START =  0;
	private static final int NAME  =  1;
	private static final int VALUE =  2;
	private static final int END   = -1;
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public void reset() {}
	
	@Override
	public List<Map<String, List<String>>> decode(byte[] bytes) throws ProtocolException {
		try {
			return decode0(bytes);
		} catch (Exception e) {
			if (e instanceof ProtocolException) {
				throw (ProtocolException) e;
			}
			throw new ProtocolException(e);
		}
	}
	
	private List<Map<String, List<String>>> decode0(byte[] bytes) throws ProtocolException, UnsupportedEncodingException {
		List<Map<String, List<String>>> paras = new ArrayList<Map<String,List<String>>>();
		
		Map<String, List<String>> map = null;
		String name = null;
		String value = null;
		int searchIndex = 0;
		int stateIndex = 0;
		int state = START;
		int len = bytes.length;
		int i = 0;
		boolean decode = false;
		while (searchIndex < len) {
			switch (state) {
			case START:
				state = NAME;
				map = new HashMap<String, List<String>>();
				break;
			case NAME:
				byte nameHead = bytes[searchIndex];
				if (nameHead == PLUS_SIGN || nameHead == PERCENT_SIGN) {
					decode = true;
				}
				for (; searchIndex < len && bytes[searchIndex] != EQUAL_SIGN; searchIndex++, i++);
				name = new String(bytes, stateIndex, i, charset);
				if (decode) {
					name = URLDecoder.decode(name, charset.name());
				}
				
				stateIndex = ++searchIndex;
				i = 0;
				decode = false;
				state = VALUE;
				break;
			case VALUE:
				byte valueHead = bytes[searchIndex];
				if (valueHead == PLUS_SIGN || valueHead == PERCENT_SIGN) {
					decode = true;
				}
				for (; searchIndex < len && bytes[searchIndex] != AMPERSAND; searchIndex++, i++);
				value = new String(bytes, stateIndex, i, charset);
				if (decode) {
					value = URLDecoder.decode(value, charset.name());
				}
				
				List<String> values = map.get(name);
				if (values == null) {
					values = new ArrayList<String>();
					map.put(name, values);
				}
				values.add(value);
				
				name = value = null;
				stateIndex = ++searchIndex;
				i = 0;
				decode = false;
				if (searchIndex >= len) {
					state = END;
				} else {
					state = NAME;
				}
				break;
			case END:
				// nothing to do
				break;
			}
		}
		
		if (map != null) {
			paras.add(map);
		}
		return paras;
	}

}

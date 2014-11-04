package io.craft.atom.protocol.http;

import static io.craft.atom.protocol.http.HttpConstants.EQUAL_SIGN;
import static io.craft.atom.protocol.http.HttpConstants.SEMICOLON;
import static io.craft.atom.protocol.http.HttpConstants.SP;

import io.craft.atom.protocol.AbstractProtocolCodec;
import io.craft.atom.protocol.ProtocolDecoder;
import io.craft.atom.protocol.ProtocolException;
import io.craft.atom.protocol.http.model.HttpCookie;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import lombok.ToString;


/**
 * A {@link ProtocolDecoder} which decodes cookie string bytes into {@code Cookie} object, default charset is utf-8.
 * <br>
 * Only accept complete cookie bytes to decode, because this implementation is stateless and thread safe.
 * 
 * @see HttpCookie
 * @author mindwind
 * @version 1.0, Mar 25, 2013
 */
@ToString(callSuper = true)
public class HttpCookieDecoder extends AbstractProtocolCodec implements ProtocolDecoder<HttpCookie> {
	
	
	private static final int START                     =  0;
	private static final int NAME                      =  1;
	private static final int VALUE                     =  2;
	private static final int ATTRIBUTE_START           =  3;
	private static final int ATTRIBUTE_NAME            =  4;
	private static final int DOMAIN_ATTRIBUTE_VALUE    =  5;
	private static final int PATH_ATTRIBUTE_VALUE      =  6;
	private static final int EXPIRES_ATTRIBUTE_VALUE   =  7;
	private static final int MAX_AGE_ATTRIBUTE_VALUE   =  8;
	private static final int EXTENSION_ATTRIBUTE_VALUE =  9;
	private static final int END                       = -1;
	
	
	private boolean setCookie = false;
	
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	
	public HttpCookieDecoder() {}
	
	public HttpCookieDecoder(Charset charset) {
		this.charset = charset;
	}
	
	public HttpCookieDecoder(Charset charset, boolean setCookie) {
		this.charset   = charset;
		this.setCookie = setCookie;
	}
	
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	
	@Override
	public void reset() {}
	
	@Override
	public List<HttpCookie> decode(byte[] bytes) throws ProtocolException {
		try {
			if (setCookie) {
				return decode4setCookie(bytes);
			} else {
				return decode4cookie(bytes);
			}
		} catch (Exception e) {
			if (e instanceof ProtocolException) {
				throw (ProtocolException) e;
			}
			throw new ProtocolException(e);
		}
	}
	
	// Cookie: SID=31d4d96e407aad42; lang=en-US
	// Cookie: test=test123
	private List<HttpCookie> decode4cookie(byte[] bytes) throws ProtocolException {
		List<HttpCookie> cookies = new ArrayList<HttpCookie>();
		
		HttpCookie cookie = null;
		int searchIndex = 0;
		int stateIndex = 0;
		int state = START;
		int len = bytes.length;
		int i = 0;
		while (searchIndex < len) {
			switch (state) {
			case START:
				// skip all OWS(Optional White Space)
				for (; searchIndex < len && bytes[searchIndex] == SP; searchIndex++);
				stateIndex = searchIndex;
				state = NAME;
				cookie = new HttpCookie();
				break;
			case NAME:
				for (; searchIndex < len && bytes[searchIndex] != EQUAL_SIGN; searchIndex++, i++);
				String name = new String(bytes, stateIndex, i, charset);
				cookie.setName(name);
				stateIndex = ++searchIndex;
				i = 0;
				state = VALUE;
				break;
			case VALUE:
				for (; searchIndex < len && bytes[searchIndex] != SEMICOLON; searchIndex++, i++);
				String value = new String(bytes, stateIndex, i, charset);
				cookie.setValue(value);
				cookies.add(cookie);
				stateIndex = ++searchIndex;
				i = 0;
				if (searchIndex >= len) {
					state = END;
				} else {
					state = START;
				}
				break;
			case END:
				// nothing to do
				break;
			}
		}
		
		return cookies;
	}
	
	// Set-Cookie: SID=31d4d96e407aad42; Domain=example.com; Path=/; HttpOnly; Secure; Expires=Wed, 09 Jun 2021 10:18:14 GMT; Max-Age=86400
	// Set-Cookie: SID=31d4d96e407aad42; Domain=example.com; Path=/; HttpOnly; Secure; Expires=Wed, 09 Jun 2021 10:18:14 GMT; Max-Age=86400; test=extensionTest
	private List<HttpCookie> decode4setCookie(byte[] bytes) throws ProtocolException {
		List<HttpCookie> cookies = new ArrayList<HttpCookie>();
		
		HttpCookie cookie = null;
		int searchIndex = 0;
		int stateIndex = 0;
		int state = START;
		int len = bytes.length;
		int i = 0;
		String extentionName = "";
		
		while (searchIndex < len) {
			switch (state) {
			case START:
				// skip all OWS(Optional White Space)
				for (; searchIndex < len && bytes[searchIndex] == SP; searchIndex++);
				stateIndex = searchIndex;
				state = NAME;
				cookie = new HttpCookie();
				break;
			case NAME:
				for (; searchIndex < len && bytes[searchIndex] != EQUAL_SIGN; searchIndex++, i++);
				String name = new String(bytes, stateIndex, i, charset);
				cookie.setName(name);
				stateIndex = ++searchIndex;
				i = 0;
				state = VALUE;
				break;
			case VALUE:
				for (; searchIndex < len && bytes[searchIndex] != SEMICOLON; searchIndex++, i++);
				String value = new String(bytes, stateIndex, i, charset);
				cookie.setValue(value);
				cookies.add(cookie);
				stateIndex = ++searchIndex;
				i = 0;
				if (searchIndex >= len) {
					state = END;
				} else {
					state = ATTRIBUTE_START;
				}
				break;
			case ATTRIBUTE_START:
				for (; searchIndex < len && bytes[searchIndex] == SP; searchIndex++);
				stateIndex = searchIndex;
				state = ATTRIBUTE_NAME;
				break;
			case ATTRIBUTE_NAME:
				for (; searchIndex < len && bytes[searchIndex] != EQUAL_SIGN && bytes[searchIndex] != SEMICOLON; searchIndex++, i++);
				String an = new String(bytes, stateIndex, i, charset);
				if (HttpCookie.DOMAIN.equalsIgnoreCase(an)) {
					state = DOMAIN_ATTRIBUTE_VALUE;
				} else if (HttpCookie.PATH.equalsIgnoreCase(an)) {
					state = PATH_ATTRIBUTE_VALUE;
				} else if (HttpCookie.HTTP_ONLY.equalsIgnoreCase(an)) {
					cookie.setHttpOnly(true);
					if (searchIndex >= len) {
						state = END;
					} else {
						state = ATTRIBUTE_START;
					}
				} else if (HttpCookie.SECURE.equalsIgnoreCase(an)) {
					cookie.setSecure(true);
					if (searchIndex >= len) {
						state = END;
					} else {
						state = ATTRIBUTE_START;
					}
				} else if (HttpCookie.EXPIRES.equalsIgnoreCase(an)) {
					state = EXPIRES_ATTRIBUTE_VALUE;
				} else if (HttpCookie.MAX_AGE.equalsIgnoreCase(an)) {
					state = MAX_AGE_ATTRIBUTE_VALUE;
				} else {
					extentionName = an;
					state = EXTENSION_ATTRIBUTE_VALUE;
				}
				stateIndex = ++searchIndex;
				i = 0;
				break;
			case DOMAIN_ATTRIBUTE_VALUE:
				for (; searchIndex < len && bytes[searchIndex] != SEMICOLON; searchIndex++, i++);
				String domain = new String(bytes, stateIndex, i, charset);
				cookie.setDomain(domain);
				stateIndex = ++searchIndex;
				i = 0;
				if (searchIndex >= len) {
					state = END;
				} else {
					state = ATTRIBUTE_START;
				}
				break;
			case PATH_ATTRIBUTE_VALUE:
				for (; searchIndex < len && bytes[searchIndex] != SEMICOLON; searchIndex++, i++);
				String path = new String(bytes, stateIndex, i, charset);
				cookie.setPath(path);
				stateIndex = ++searchIndex;
				i = 0;
				if (searchIndex >= len) {
					state = END;
				} else {
					state = ATTRIBUTE_START;
				}
				break;
			case EXPIRES_ATTRIBUTE_VALUE:
				for (; searchIndex < len && bytes[searchIndex] != SEMICOLON; searchIndex++, i++);
				String expires = new String(bytes, stateIndex, i, charset);
				cookie.setExpires(HttpDates.parse(expires));
				stateIndex = ++searchIndex;
				i = 0;
				if (searchIndex >= len) {
					state = END;
				} else {
					state = ATTRIBUTE_START;
				}
				break;
			case MAX_AGE_ATTRIBUTE_VALUE:
				for (; searchIndex < len && bytes[searchIndex] != SEMICOLON; searchIndex++, i++);
				String maxAge = new String(bytes, stateIndex, i, charset);
				cookie.setMaxAge(Integer.parseInt(maxAge));
				stateIndex = ++searchIndex;
				i = 0;
				if (searchIndex >= len) {
					state = END;
				} else {
					state = ATTRIBUTE_START;
				}
				break;
			case EXTENSION_ATTRIBUTE_VALUE:
				for (; searchIndex < len && bytes[searchIndex] != SEMICOLON; searchIndex++, i++);
				String extentionValue = new String(bytes, stateIndex, i, charset);
				cookie.addExtensionAttribute(extentionName, extentionValue);
				stateIndex = ++searchIndex;
				i = 0;
				if (searchIndex >= len) {
					state = END;
				} else {
					state = ATTRIBUTE_START;
				}
				break;
			case END:
				// nothing to do
				break;
			}
		}
		
		return cookies;
	}

}

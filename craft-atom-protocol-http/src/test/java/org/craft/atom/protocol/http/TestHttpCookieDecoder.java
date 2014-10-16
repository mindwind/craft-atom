package org.craft.atom.protocol.http;

import java.util.List;

import junit.framework.Assert;

import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.http.model.Cookie;
import org.craft.atom.test.CaseCounter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Mar 25, 2013
 */
public class TestHttpCookieDecoder {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(TestHttpCookieDecoder.class);
	
	
	private HttpCookieDecoder cookieDecoder    = new HttpCookieDecoder()    ;
	private HttpCookieEncoder encoder          = new HttpCookieEncoder()    ;
	private HttpCookieDecoder setCookieDecoder = new HttpCookieDecoder(true);
	

	@Test
	public void testCookie() throws ProtocolException {
		String cookie1 = "SID=31d4d96e407aad42; lang=en-US";
		List<Cookie> cookies = cookieDecoder.decode(cookie1.getBytes());
		Assert.assertEquals(2, cookies.size());
		for (Cookie cookie : cookies) {
			LOG.debug("[CRAFT-ATOM-PROTOCOL-HTTP] Encoded cookie={}", new String(encoder.encode(cookie)));
		}
		
		String cookie2 = "test=test123";
		cookies = cookieDecoder.decode(cookie2.getBytes());
		Assert.assertEquals(1, cookies.size());
		for (Cookie cookie : cookies) {
			LOG.debug("[CRAFT-ATOM-PROTOCOL-HTTP] Encoded cookie={}", new String(encoder.encode(cookie)));
		}
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-HTTP] (^_^)  <%s>  Case -> test cookie. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testSetCookie() throws ProtocolException {
		String setCookie1 = "SID=31d4d96e407aad42; Domain=example.com; Path=/";
		List<Cookie> cookies = setCookieDecoder.decode(setCookie1.getBytes());
		Assert.assertEquals(1, cookies.size());
		for (Cookie cookie : cookies) {
			LOG.debug(new String(encoder.encode(cookie)));
		}
		
		String setCookie2 = "SID=31d4d96e407aad42; Domain=example.com; Path=/chat; HttpOnly; Secure; Expires=Wed, 09 Jun 2021 10:18:14 GMT; Max-Age=86400; test=extensionTest";
		cookies = setCookieDecoder.decode(setCookie2.getBytes());
		Assert.assertEquals(1, cookies.size());
		for (Cookie cookie : cookies) {
			Assert.assertEquals("SID", cookie.getName());
			Assert.assertEquals("31d4d96e407aad42", cookie.getValue());
			Assert.assertEquals("example.com", cookie.getDomain());
			Assert.assertEquals("/chat", cookie.getPath());
			Assert.assertEquals(true, cookie.isHttpOnly().booleanValue());
			Assert.assertEquals(true, cookie.isSecure().booleanValue());
			Assert.assertEquals("Wed, 09 Jun 2021 10:18:14 GMT", HttpDates.format(cookie.getExpires()));
			Assert.assertEquals(86400, cookie.getMaxAge().intValue());
			Assert.assertEquals("extensionTest", cookie.getExtensionAttribute("test"));
			LOG.debug(new String(encoder.encode(cookie)));
		}
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-HTTP] (^_^)  <%s>  Case -> test set cookie. ", CaseCounter.incr(2)));
	}
	
}

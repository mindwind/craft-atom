package org.craft.atom.protocol.http;

import junit.framework.Assert;

import org.craft.atom.protocol.http.model.Cookie;
import org.junit.Test;

/**
 * @author mindwind
 * @version 1.0, Mar 25, 2013
 */
public class CookieTest {
	
	@Test public void test() {
		String setCookieString = "SID=31d4d96e407aad42; Domain=example.com; Path=/chat; HttpOnly; Secure; Expires=Wed, 09 Jun 2021 10:18:14 GMT; Max-Age=86400";
		Cookie cookie = Cookie.fromSetCookieString(setCookieString);
		
		Assert.assertEquals("SID", cookie.getName());
		Assert.assertEquals("31d4d96e407aad42", cookie.getValue());
		Assert.assertEquals("example.com", cookie.getDomain());
		Assert.assertEquals("/chat", cookie.getPath());
		Assert.assertEquals(true, cookie.isHttpOnly().booleanValue());
		Assert.assertEquals(true, cookie.isSecure().booleanValue());
		Assert.assertEquals("Wed, 09 Jun 2021 10:18:14 GMT", HttpDates.format(cookie.getExpires()));
		Assert.assertEquals(86400, cookie.getMaxAge().intValue());
		System.out.println(cookie.toHttpString());
	}
	
}

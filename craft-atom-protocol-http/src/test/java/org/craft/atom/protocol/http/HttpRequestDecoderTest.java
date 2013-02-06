package org.craft.atom.protocol.http;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.http.model.HttpRequest;
import org.craft.atom.util.StringUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mindwind
 * @version 1.0, Feb 6, 2013
 */
public class HttpRequestDecoderTest {
	
	private HttpRequestEncoder encoder;
	private HttpRequestDecoder decoder;
	private Charset charset = Charset.forName("utf-8");
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	@Before
	public void before() {
		encoder = new HttpRequestEncoder();
		decoder = new HttpRequestDecoder();
	}
	
	/**
	 * Test http get for one complete request
	 */
	@Test public void testGetOneRequest() throws ProtocolException {
		String req = "\r\nGET /s?wd=java+jdk7&rsv_bp=0&inputT=14326 HTTP/1.1\r\nHost: www.baidu.com\r\nUser-Agent: Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0\r\nAccept: text/html,application/xhtml+xml,\r\n\tapplication/xml;q=0.9,*/*;q=0.8\r\nAccept-Language: zh-cn,zh;q=0.5\r\nAccept-Encoding: gzip, deflate\r\nAccept-Charset: GB2312,utf-8;q=0.7,*;q=0.7\r\nConnection: keep-alive\r\nReferer: http://www.baidu.com/\r\nCookie: BAIDUID=34C25418C0B70D93E53A8E1CB8CB150F:FG=1\r\n\r\n";
		List<HttpRequest> reqs = decoder.decode(req.getBytes(charset));
		Assert.assertEquals(1, reqs.size());
		System.out.println(new String(encoder.encode(reqs.get(0)), charset));
	}
	
	/**
	 * Test http get for one and half request
	 */
	@Test public void testGetOneAndHalfRequest() throws ProtocolException {
		String req = "\r\nGET /s?wd=java+jdk7&rsv_bp=0&inputT=14326 HTTP/1.1\r\nHost: www.baidu.com\r\nUser-Agent: Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0\r\nAccept: text/html,application/xhtml+xml,\r";
		List<HttpRequest> reqs = decoder.decode(req.getBytes(charset));
		Assert.assertEquals(0, reqs.size());
		
		req = "\n\tapplication/xml;q=0.9,*/*;q=0.8\r\nAccept-Language: zh-cn,zh;q=0.5\r\nAccept-Encoding: gzip, deflate\r\nAccept-Charset: GB2312,utf-8;q=0.7,*;q=0.7\r\nConnection: keep-alive\r\nReferer: http://www.baidu.com/\r\nCookie: BAIDUID=34C25418C0B70D93E53A8E1CB8CB150F:FG=1\r\n\r\nGET /s?wd=java+jdk7&rsv_bp=0&inputT=14326 HTTP/1.1\r";
		reqs = decoder.decode(req.getBytes(charset));
		Assert.assertEquals(1, reqs.size());
		System.out.println(new String(encoder.encode(reqs.get(0)), charset));
		
		req = "\nAccept-Language: zh-cn,zh;q=0.5\r\n\r\n";
		reqs = decoder.decode(req.getBytes(charset));
		Assert.assertEquals(1, reqs.size());
		System.out.println(new String(encoder.encode(reqs.get(0)), charset));
	}
	
	/**
	 * Test http get for streaming request
	 */
	@Test public void testGetStreamingRequest() throws ProtocolException {
		String req = "GET";
		List<HttpRequest> reqs = decoder.decode(req.getBytes(charset));
		Assert.assertEquals(0, reqs.size());
		
		req = " /s?wd=java+jdk7&rsv_bp=0&inputT";
		reqs = decoder.decode(req.getBytes(charset));
		Assert.assertEquals(0, reqs.size());
		
		req = "=14326";
		reqs = decoder.decode(req.getBytes(charset));
		Assert.assertEquals(0, reqs.size());
		
		req = " HTTP/1.1\r";
		reqs = decoder.decode(req.getBytes(charset));
		Assert.assertEquals(0, reqs.size());
		
		req = "\nHost: www.baidu.com\r\nUser-Agent: Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0\r";
		reqs = decoder.decode(req.getBytes(charset));
		Assert.assertEquals(0, reqs.size());
		
		req = "\nAccept: text/html,application/xhtml+xml,\r\n\tapplication/xml;q=0.9,*/*;q=0.8\r\nAccept-Language: zh-cn,zh;q=0.5\r\nAccept-Encoding: gzip, deflate\r\nAccept-Charset: GB2312,utf-8;q=0.7,*;q=0.7\r\nConnection: keep-alive\r\nReferer: http://www.baidu.com/\r\nCookie: BAIDUID=34C25418C0B70D93E53A8E1CB8CB150F:FG=1\r\n\r\n";
		reqs = decoder.decode(req.getBytes(charset));
		Assert.assertEquals(1, reqs.size());
		System.out.println(new String(encoder.encode(reqs.get(0)), charset));
		
		req = "GET /s?wd=java+jdk7&rsv_bp=0&inputT=14326 HTTP/1.1\r\nAccept-Language: zh-cn,zh;q=0.5\r\n\r\n";
		reqs = decoder.decode(req.getBytes(charset));
		Assert.assertEquals(1, reqs.size());
		System.out.println(new String(encoder.encode(reqs.get(0)), charset));
	}
	
	/**
	 * Test http get for random streaming request
	 */
	@Test public void testGetStreamingRequestRandom() throws ProtocolException {
		String req = "GET /s?wd=java+jdk7&rsv_bp=0&inputT=14326 HTTP/1.1\r\nHost: www.baidu.com\r\nUser-Agent: Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0\r\nAccept: text/html,application/xhtml+xml,\r\n\tapplication/xml;q=0.9,*/*;q=0.8\r\nAccept-Language: zh-cn,zh;q=0.5\r\nAccept-Encoding: gzip, deflate\r\nAccept-Charset: GB2312,utf-8;q=0.7,*;q=0.7\r\nConnection: keep-alive\r\nReferer: http://www.baidu.com/\r\nCookie: BAIDUID=34C25418C0B70D93E53A8E1CB8CB150F:FG=1\r\n\r\n";
		
		for (int i = 0; i < 100; i++) {
			int num = new Random().nextInt(req.length() + 1);
			String[] sarr = StringUtil.split(req, num);
			List<HttpRequest> reqs = null;
			for (String str : sarr) {
				reqs = decoder.decode(str.getBytes(charset));
			}
			
			Assert.assertEquals(1, reqs.size());
			System.out.println("split num=" + num);
			System.out.println(new String(encoder.encode(reqs.get(0)), charset));	
		}
	}
	
}

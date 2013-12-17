package org.craft.atom.protocol.http;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.http.model.Cookie;
import org.craft.atom.protocol.http.model.HttpRequest;
import org.craft.atom.test.CaseCounter;
import org.craft.atom.util.StringUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mindwind
 * @version 1.0, Feb 6, 2013
 */
public class TestHttpRequestDecoder {
	
	
	private static final Log LOG = LogFactory.getLog(TestHttpRequestDecoder.class);
	
	
	private HttpRequestEncoder encoder                            ;
	private HttpRequestDecoder decoder                            ;
	private Charset            charset  = Charset.forName("utf-8");
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	@Before
	public void before() {
		encoder = new HttpRequestEncoder();
		decoder = new HttpRequestDecoder();
	}
	
	@Test 
	public void testOneRequestWithoutEntity() throws ProtocolException {
		String req = "\r\nGET /s?wd=java+jdk7&rsv_bp=0&inputT=14326 HTTP/1.1\r\nHost: www.baidu.com\r\nUser-Agent: Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0\r\nAccept: text/html,application/xhtml+xml,\r\n\tapplication/xml;q=0.9,*/*;q=0.8\r\nAccept-Language: zh-cn,zh;q=0.5\r\nAccept-Encoding: gzip, deflate\r\nAccept-Charset: GB2312,utf-8;q=0.7,*;q=0.7\r\nConnection: keep-alive\r\nReferer: http://www.baidu.com/\r\nCookie: BAIDUID=34C25418C0B70D93E53A8E1CB8CB150F:FG=1\r\n\r\n";
		List<HttpRequest> reqs = decoder.decode(req.getBytes(charset));
		Assert.assertEquals(1, reqs.size());
		LOG.debug(new String(encoder.encode(reqs.get(0)), charset));
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-HTTP] (^_^)  <%s>  Case -> test one request without entity. ", CaseCounter.incr(1)));
	}
	
	@Test 
	public void testOneAndHalfRequestWithoutEntity() throws ProtocolException {
		String req = "\r\nGET /s?wd=java+jdk7&rsv_bp=0&inputT=14326 HTTP/1.1\r\nHost: www.baidu.com\r\nUser-Agent: Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0\r\nAccept: text/html,application/xhtml+xml,\r";
		List<HttpRequest> reqs = decoder.decode(req.getBytes(charset));
		Assert.assertEquals(0, reqs.size());
		
		req = "\n\tapplication/xml;q=0.9,*/*;q=0.8\r\nAccept-Language: zh-cn,zh;q=0.5\r\nAccept-Encoding: gzip, deflate\r\nAccept-Charset: GB2312,utf-8;q=0.7,*;q=0.7\r\nConnection: keep-alive\r\nReferer: http://www.baidu.com/\r\nCookie: BAIDUID=34C25418C0B70D93E53A8E1CB8CB150F:FG=1\r\n\r\nGET /s?wd=java+jdk7&rsv_bp=0&inputT=14326 HTTP/1.1\r";
		reqs = decoder.decode(req.getBytes(charset));
		Assert.assertEquals(1, reqs.size());
		LOG.debug(new String(encoder.encode(reqs.get(0)), charset));
		
		req = "\nAccept-Language: zh-cn,zh;q=0.5\r\n\r\n";
		reqs = decoder.decode(req.getBytes(charset));
		Assert.assertEquals(1, reqs.size());
		LOG.debug(new String(encoder.encode(reqs.get(0)), charset));
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-HTTP] (^_^)  <%s>  Case -> test one and half request without entity. ", CaseCounter.incr(3)));
	}
	
	@Test 
	public void testStreamingRequestWithoutEntity() throws ProtocolException {
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
		LOG.debug(new String(encoder.encode(reqs.get(0)), charset));
		
		req = "GET /s?wd=java+jdk7&rsv_bp=0&inputT=14326 HTTP/1.1\r\nAccept-Language: zh-cn,zh;q=0.5\r\n\r\n";
		reqs = decoder.decode(req.getBytes(charset));
		Assert.assertEquals(1, reqs.size());
		LOG.debug(new String(encoder.encode(reqs.get(0)), charset));
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-HTTP] (^_^)  <%s>  Case -> test streaming request without entity. ", CaseCounter.incr(7)));
	}
	
	
	@Test 
	public void testStreamingRequestWithoutEntityInRandomLoop() throws ProtocolException {
		String req = "GET /s?wd=java+jdk7&rsv_bp=0&inputT=14326 HTTP/1.1\r\nHost: www.baidu.com\r\nUser-Agent: Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0\r\nAccept: text/html,application/xhtml+xml,\r\n\tapplication/xml;q=0.9,*/*;q=0.8\r\nAccept-Language: zh-cn,zh;q=0.5\r\nAccept-Encoding: gzip, deflate\r\nAccept-Charset: GB2312,utf-8;q=0.7,*;q=0.7\r\nConnection: keep-alive\r\nReferer: http://www.baidu.com/\r\nCookie: BAIDUID=34C25418C0B70D93E53A8E1CB8CB150F:FG=1\r\n\r\n";	
		testInRandomLoop(req, 100, false);
		testInRandomLoop(req, 1, true);
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-HTTP] (^_^)  <%s>  Case -> test streaming request without entity in random loop. ", CaseCounter.incr(2)));
	}
	
	@Test 
	public void testOneRequestWithFixLengthEntity() throws ProtocolException {
		String req = "\r\nPOST /craft/webservice/helloWS HTTP/1.1\r\nContent-Type: text/xml; charset=UTF-8\r\nAccept: */*\r\nSOAPAction: \"\"\r\nUser-Agent: Apache CXF 2.4.0\r\nCache-Control: no-cache\r\nPragma: no-cache\r\nHost: localhost:9999\r\nConnection: keep-alive\r\nContent-Length: 194\r\n\r\n<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><ns2:sayHi xmlns:ns2=\"http://craft.org\"><ns2:text>How are you?</ns2:text></ns2:sayHi></soap:Body></soap:Envelope>";
		List<HttpRequest> reqs = decoder.decode(req.getBytes(charset));
		
		Assert.assertEquals(1, reqs.size());
		LOG.debug(new String(encoder.encode(reqs.get(0)), charset));
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-HTTP] (^_^)  <%s>  Case -> test one request with fix length entity. ", CaseCounter.incr(1)));
	}

	public void testStreamingRequestWithFixLengthEntity() throws ProtocolException {
		String req = "\r\nPOST /craft/webservice/helloWS HTTP/1.1\r\nContent-Type: text/xml; charset=UTF-8\r\nAccept: */*\r\nSOAPAction: \"\"\r\nUser-Agent: Apache CXF 2.4.0\r\nCache-Control: no-cache\r\nPragma: no-cache\r\nHost: localhost:9999\r\nConnection: keep-alive\r\nContent-Length: 194\r\n\r\n<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><ns2:sayHi xmlns:ns2=\"http://craft.org\"><ns2:text>How are you?</ns2:text></ns2:sayHi></soap:Body></soap:Envelope>";
		testInRandomLoop(req, 100, false);
		testInRandomLoop(req, 1, true);
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-HTTP] (^_^)  <%s>  Case -> test streaming request with fix length entity. ", CaseCounter.incr(2)));
	}
	
	@Test 
	public void testStreamingRequestWithChunkedEntity() throws ProtocolException {
		String req = "POST /craft/webservice/helloWS HTTP/1.1\r\nTransfer-Encoding: chunked\r\nTrailer: Content-MD5\r\nContent-Type: text/xml; charset=UTF-8\r\n\r\n24;extname=extvalue;aaa=bbb;kkk\r\nThis is the data in the first chunk \r\n1A\r\nand this is the second one\r\n0\r\nContent-MD5: gjqesdflj12dsfsf12\r\n";
		testInRandomLoop(req, 100, false);
		testInRandomLoop(req, 1, true);
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-HTTP] (^_^)  <%s>  Case -> test streaming request with chunked entity. ", CaseCounter.incr(2)));
	}
	
	@Test 
	public void testStreamingRequestWithChineseUrlAndEntity() throws ProtocolException {
		String req = "POST /测试/s?in=测试 HTTP/1.1\r\nHost: www.baidu.com\r\nContent-Length: 34\r\nUser-Agent: Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0\r\nAccept: text/html,application/xhtml+xml,\r\n\tapplication/xml;q=0.9,*/*;q=0.8\r\nAccept-Language: zh-cn,zh;q=0.5\r\nAccept-Encoding: gzip, deflate\r\nAccept-Charset: GB2312,utf-8;q=0.7,*;q=0.7\r\nConnection: keep-alive\r\nReferer: http://www.baidu.com/\r\nCookie: BAIDUID=34C25418C0B70D93E53A8E1CB8CB150F:FG=1\r\n\r\ntest这是一个中文字符测试";
		testInRandomLoop(req, 100, false);
		testInRandomLoop(req, 1, true);
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-HTTP] (^_^)  <%s>  Case -> test streaming request with chinese url and entity. ", CaseCounter.incr(2)));
	}
	
	@Test 
	public void testForFoldedHeader() throws ProtocolException {
		String req = "POST /craft/webservice/helloWS HTTP/1.1\r\nTransfer-Encoding: chunked\r\nTrailer: Content-MD5\r\nContent-Type: text/xml;\r\n \t charset=UTF-8\r\n\r\n24;extname=extvalue;aaa=bbb;kkk\r\nThis is the data in the first chunk \r\n1A\r\nand this is the second one\r\n0\r\nContent-MD5: gjqesdflj12dsfsf12\r\n";
		testInRandomLoop(req, 100, false);
		testInRandomLoop(req, 1, true);
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-HTTP] (^_^)  <%s>  Case -> test for folded header. ", CaseCounter.incr(2)));
	}
	
	@Test 
	public void testOneRequestWithCookie() throws ProtocolException {
		String req = "GET /s?wd=java+jdk7&rsv_bp=0&inputT=14326 HTTP/1.1\r\nHost: www.baidu.com\r\nUser-Agent: Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0\r\nAccept: text/html,application/xhtml+xml,\r\n\tapplication/xml;q=0.9,*/*;q=0.8\r\nAccept-Language: zh-cn,zh;q=0.5\r\nAccept-Encoding: gzip, deflate\r\nAccept-Charset: GB2312,utf-8;q=0.7,*;q=0.7\r\nConnection: keep-alive\r\nReferer: http://www.baidu.com/\r\nCookie: SID=31d4d96e407aad42\r\nCookie: BAIDUID=34C25418C0B70D93E53A8E1CB8CB150F:FG=1; lang=en-US\r\n\r\n";
		List<HttpRequest> reqs = decoder.decode(req.getBytes(charset));
		Assert.assertEquals(1, reqs.size());
		
		HttpRequest request = reqs.get(0);
		List<Cookie> cookies = request.getCookies();
		Assert.assertEquals(3, cookies.size());
		
		for (Cookie cookie : cookies) {
			LOG.debug(cookie.toHttpString());
		}
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-HTTP] (^_^)  <%s>  Case -> test one request with cookie. ", CaseCounter.incr(2)));
	}
	
	private void testInRandomLoop(String req, int loop, boolean onebyte) throws ProtocolException {
		for (int i = 0; i < loop; i++) {
			int num = new Random().nextInt(req.length() + 1);
			String[] sarr = null;
			if (onebyte) {
				sarr = StringUtil.split(req, req.length());
			} else {
				sarr = StringUtil.split(req, num);
			}
			
			List<HttpRequest> reqs = null;
			for (String str : sarr) {
				reqs = decoder.decode(str.getBytes(charset));
			}
			
			Assert.assertEquals(1, reqs.size());
			LOG.debug("split num=" + num);
			LOG.debug(new String(encoder.encode(reqs.get(0)), charset));	
		}
	}

}

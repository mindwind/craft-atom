package org.craft.atom.protocol.textline;

import java.nio.charset.Charset;
import java.util.List;

import junit.framework.Assert;

import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.test.CaseCounter;
import org.junit.Before;
import org.junit.Test;


/** 
 * Tests for {@link TextLineDecoder}
 * 
 * @author Hu Feng
 * @version 1.0, Oct 17, 2012
 */
public class TestTextLineDecoder {
	
	
	private static final Charset UTF_8 = Charset.forName("utf-8");
	
	
	private TextLineDecoder decoder;
	
	
	
	@Before
	public void setup() {
		decoder = new TextLineDecoder();
		decoder.setMaxSize(20);
	}
 	
	@Test
	public void testDecode() throws ProtocolException {
		String c1 = "123\n";
		String c2 = "123\n456\n";
		String c3 = "123\n456";
		String c4 = "\n123\n";
		String c5 = "123456789012345678901\n";
		String c6 = "测试\nhello\n";
		String c7 = "1234567";
		
		// case 1
		List<String> l = null;
		l = decoder.decode(c1.getBytes(UTF_8));
		Assert.assertEquals("123", l.get(0));
		
		// case 2
		l = decoder.decode(c2.getBytes(UTF_8));
		Assert.assertEquals("123", l.get(0));
		Assert.assertEquals("456", l.get(1));
		
		// case 3
		l = decoder.decode(c3.getBytes(UTF_8));
		Assert.assertEquals("123", l.get(0));
		l = decoder.decode("\n".getBytes(UTF_8));
		Assert.assertEquals("456", l.get(0));
		
		// case 4
		l = decoder.decode(c4.getBytes(UTF_8));
		Assert.assertEquals("", l.get(0));
		Assert.assertEquals("123", l.get(1));
		
		// case 5
		try {
			l = decoder.decode(c5.getBytes(UTF_8));
			Assert.fail();
		} catch(Exception e) {
			Assert.assertTrue(true);
		}
		
		// case 6
		l = decoder.decode(c6.getBytes(UTF_8));
		Assert.assertEquals("测试", l.get(0));
		Assert.assertEquals("hello", l.get(1));
		
		// case 7
		l = decoder.decode(c7.getBytes(UTF_8));
		Assert.assertEquals(0, l.size());
		l = decoder.decode(c7.getBytes(UTF_8));
		Assert.assertEquals(0, l.size());
		try {
			l = decoder.decode(c7.getBytes(UTF_8));
			Assert.fail();
		} catch(Exception e) {
			Assert.assertTrue(true);
		}
		
		// case 8
		decoder.decode(c7.getBytes(UTF_8));
		decoder.decode(c7.getBytes(UTF_8));
		l = decoder.decode("\n".getBytes(UTF_8));
		Assert.assertEquals(c7 + c7, l.get(0));
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-TEXTLINE] (^_^)  <%s>  Case -> test decode. ", CaseCounter.incr(16)));
	}
}

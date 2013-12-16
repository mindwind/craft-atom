package org.craft.atom.util;

import java.io.IOException;
import java.nio.charset.Charset;

import junit.framework.Assert;

import org.craft.atom.test.CaseCounter;
import org.junit.Test;

/**
 * Test for {@code GzipUtil}
 * 
 * @author mindwind
 * @version 1.0, Feb 9, 2013
 */
public class TestGzipUtil {
	
	@Test
	public void testZipUnzip() throws IOException {
		Charset charset = Charset.forName("utf-8");
		String raw = "这是一个gzip压缩测试!!~~hhhllsjf123123";
		byte[] gzipData = GzipUtil.gzip(raw.getBytes(charset));
		String ungzipData = new String(GzipUtil.ungzip(gzipData), charset);
		Assert.assertEquals(raw, ungzipData);
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> test zip & unzip. ", CaseCounter.incr(1)));
	}
	
}

package org.craft.atom.util;

import java.io.IOException;
import java.nio.charset.Charset;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test for {@code GzipUtil}
 * 
 * @author mindwind
 * @version 1.0, Feb 9, 2013
 */
public class GzipUtilTest {
	
	@Test public void test() throws IOException {
		Charset charset = Charset.forName("utf-8");
		String raw = "这是一个gzip压缩测试!!~~hhhllsjf123123";
		byte[] gzipData = GzipUtil.gzip(raw.getBytes(charset));
		System.out.println("gzip=" + new String(gzipData, charset));
		String ungzipData = new String(GzipUtil.ungzip(gzipData), charset);
		System.out.println("ungzip=" + ungzipData);
		Assert.assertEquals(raw, ungzipData);
	}
	
}

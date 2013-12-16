package org.craft.atom.util;

import junit.framework.Assert;

import org.craft.atom.test.CaseCounter;
import org.junit.Test;

/**
 * Tests for {@link StringUtil}
 *
 * @author mindwind
 * @version 1.0, 2011-12-23
 */
public class TestStringUtil {
	
	@Test
	public void testSBCAndDBC() {
		String sbcStr = "＠＃￥％＆×　";
		String dbcStr = "@#￥%&× ";
		
		Assert.assertEquals(dbcStr, StringUtil.sbc2dbcCase(sbcStr));
		Assert.assertEquals(sbcStr, StringUtil.dbc2sbcCase(dbcStr));
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> test sbc and dbc. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testToString() {
		String[] array = new String[] {"oh", "hello", "world"};
		
		Assert.assertEquals("oh,hello,world", StringUtil.toString(array, ","));
		Assert.assertEquals(null, StringUtil.toString(null, ","));
		Assert.assertEquals("ohhelloworld", StringUtil.toString(array, null));
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> test to string. ", CaseCounter.incr(3)));
	}
	
	@Test
	public void testContains() {
		String[] strings = new String[] {"oh", "hello", "world"};
		
		Assert.assertFalse(StringUtil.contains(strings, "yes", true));
		Assert.assertTrue(StringUtil.contains(strings, "hello", true));
		Assert.assertTrue(StringUtil.contains(strings, "Hello", false));
		Assert.assertTrue(StringUtil.containSubstring(strings, "llo", true));
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> test contains. ", CaseCounter.incr(4)));
	}
	
	@Test
	public void testCount() {
		String src = "oh,hello,world!ha!ha!";
		
		Assert.assertEquals(1, StringUtil.count(src, "oh"));
		Assert.assertEquals(2, StringUtil.count(src, "ha"));
		Assert.assertEquals(3, StringUtil.count(src, "!"));
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> test count. ", CaseCounter.incr(3)));
	}
	
	@Test
	public void testReplace() {
		String source = "<a>%1</a>%2<c>%3</c>";
		String[] values = new String[] { "AAA", "BBB", "CCC" };
		String actual = StringUtil.replace("%", source, values);
		String expected = "<a>AAA</a>BBB<c>CCC</c>";
		Assert.assertEquals(expected, actual);
		values = new String[] { "AAA", "BBB" };
		Assert.assertEquals("<a>AAA</a>BBB<c>BBB</c>", StringUtil.replace("%", source, values));
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> test replace. ", CaseCounter.incr(2)));
	}
	
}

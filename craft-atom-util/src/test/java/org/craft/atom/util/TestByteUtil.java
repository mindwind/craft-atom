package org.craft.atom.util;

import org.craft.atom.test.CaseCounter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link ByteUtil}
 *
 * @author Hu Feng
 * @version 1.0, 2011-12-21
 */
public class TestByteUtil {
	
	@Test
	public void testIndexOf() {
		String s = "123456\r\n9\r\n\r\n";
		byte[] bytes = s.getBytes();

		int idx = ByteUtil.indexOf(bytes, (byte) '9');
		Assert.assertEquals(8, idx);
		idx = ByteUtil.indexOf(bytes, new byte[] { '\r', '\n', '\r', '\n' });
		Assert.assertEquals(9, idx);
		idx = ByteUtil.indexOf(bytes, new byte[] { '\t', '\n' });
		Assert.assertEquals(-1, idx);
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> test indexof. ", CaseCounter.incr(3)));
	}
	
	@Test
	public void testAsHex() {
		byte[] bytes = new byte[] { 10, 11, 12, 13, 127};
		String hexStr = ByteUtil.asHex(bytes, ",");
		Assert.assertEquals("0a,0b,0c,0d,7f", hexStr);
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> test as hex. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testIntToNetworkByteOrder() {
		int num = 28543;
		byte[] bytes = ByteUtil.intToNetworkByteOrder(num, 2);
		Assert.assertArrayEquals(new byte[] {111, 127}, bytes);
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> test int to network byte order. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testBytesShort() {
		short s = 28543;
		Assert.assertEquals(s, ByteUtil.bytes2short(ByteUtil.short2bytes(s)));
		s = -18120;
		Assert.assertEquals(s, ByteUtil.bytes2short(ByteUtil.short2bytes(s)));
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> bytes-short. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testBytesInt() {
		int i = 1234567;
		Assert.assertEquals(i, ByteUtil.bytes2int(ByteUtil.int2bytes(i)));
		i = -1234567;
		Assert.assertEquals(i, ByteUtil.bytes2int(ByteUtil.int2bytes(i)));
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> bytes-int. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testBytesLong() {
		long l = 12345678910L;
		Assert.assertEquals(l, ByteUtil.bytes2long(ByteUtil.long2bytes(l)));
		l = -12345678910L;
		Assert.assertEquals(l, ByteUtil.bytes2long(ByteUtil.long2bytes(l)));
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> bytes-long. ", CaseCounter.incr(2)));
	}
	
	@Test
	public void testNetworkByteOrderToInt() {
		byte[] bytes = new byte[] {0, 0, 1, 1};
		int num = ByteUtil.networkByteOrderToInt(bytes);
		Assert.assertEquals(257, num);
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> make network byte order to int. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testReverse() {
		byte[] in = new byte[] {1, 0, 0, 0};
		byte[] out = ByteUtil.reverse(in);
		Assert.assertArrayEquals(new byte[] {0, 0, 0, 1}, out);
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> make reverse. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testSplit() {
		byte[] in = new byte[] {0, 0, 1, 1, 1, 1, 0, 0};
		byte[] out = ByteUtil.split(in, 2, 6);
		Assert.assertArrayEquals(new byte[] {1, 1, 1, 1}, out);
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> make split. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testMakeIntFromByte2() {
		byte[] bytes = new byte[] {111, 127};
		@SuppressWarnings("deprecation")
		int num = ByteUtil.makeIntFromByte2(bytes);
		Assert.assertEquals(28543, num);
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> make int from byte2. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testMakeIntFromByte4() {
		byte[] bytes = new byte[] {1, 0, 0, 0};
		@SuppressWarnings("deprecation")
		int num = ByteUtil.makeIntFromByte4(bytes);
		Assert.assertEquals(16777216, num);
		System.out.println(String.format("[CRAFT-ATOM-UTIL] (^_^)  <%s>  Case -> make int from byte4. ", CaseCounter.incr(1)));
	}
	
}

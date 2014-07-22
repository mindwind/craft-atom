package org.craft.atom.util;

/**
 * A byte utility class that manipulates bytes.
 * 
 * @author  mindwind
 * @version 1.0, 2011-8-2
 */
public class ByteUtil {

	/**
	 * Returns the first target occurrence position in the source byte array.
	 * 
	 * @param source
	 * @param target
	 * @return if occurs, return the index of the first byte; if it does not occur, <code>-1</code> is returned.
	 */
	public static int indexOf(final byte[] source, final byte[] target) {
		if (source == null || target == null) {
			return -1;
		}

		if (target.length == 1) {
			return indexOf(source, target[0]);
		} else {
			return indexOf(source, 0, source.length, target, 0, target.length, 0);
		}
	}

	/**
	 * Returns the first target occurrence position in the source byte array.
	 * 
	 * @param source
	 * @param target
	 * @param fromIndex the index to start the search from, inclusive
	 * @return if occurs, return the index of the first byte; if it does not occur, <code>-1</code> is returned.
	 */
	public static int indexOf(final byte[] source, final byte[] target, int fromIndex) {
		if (source == null || target == null) {
			return -1;
		}
		
		if (target.length == 1) {
			return indexOf(source, target[0], fromIndex);
		} else {
			return ByteUtil.indexOf(source, 0, source.length, target, 0, target.length, fromIndex);
		}
	}
	
	/**
	 * Returns the first target occurrence position in the source byte array.
	 * 
	 * @param source
	 * @param target
	 * @param fromIndex the index to start the search from, inclusive
	 * @param endIndex the index to finish the search at, exclusive
	 * @return if occurs, return the index of the first byte; if it does not occur, <code>-1</code> is returned.
	 */
	public static int indexOf(final byte[] source, final byte[] target, int fromIndex, int endIndex) {
		if (source == null || target == null || fromIndex >= endIndex) {
			return -1;
		}
		
		if (target.length == 1) {
			return indexOf(source, target[0], fromIndex, endIndex);
		} else {
			int sourceCount = endIndex - fromIndex;
			return ByteUtil.indexOf(source, 0, sourceCount, target, 0, target.length, fromIndex);
		}
	}

	/**
	 * Returns the first target occurrence position in the source byte array
	 * 
	 * @param source
	 * @param target
	 * @return if occurs, return the index of the first byte; if it does not occur, <code>-1</code> is returned.
	 */
	public static int indexOf(byte[] source, byte target) {
		if (source == null || source.length == 0) {
			return -1;
		}

		for (int i = 0; i < source.length; i++) {
			if (source[i] == target) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the first target occurrence position in the source byte array, search from <code>fromIndex</code>.
	 * 
	 * @param source
	 * @param target
	 * @param fromIndex the index to start the search from, inclusive
	 * @return if occurs, return the index of the first byte; if it does not occur, <code>-1</code> is returned.
	 */
	public static int indexOf(byte[] source, byte target, int fromIndex) {
		if (source == null || source.length == 0 || fromIndex >= source.length) {
			return -1;
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		
		for (int i = fromIndex; i < source.length; i++) {
			if (source[i] == target) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the first target occurrence position in the source byte array, search from <code>fromIndex</code>.
	 * 
	 * @param source
	 * @param target
	 * @param fromIndex the index to start the search from, inclusive
	 * @param endIndex the index to finish the search at, exclusive
	 * @return if occurs, return the index of the first byte; if it does not occur, <code>-1</code> is returned.
	 */
	public static int indexOf(byte[] source, byte target, int fromIndex, int endIndex) {
		if (source == null || source.length == 0 || fromIndex >= endIndex) {
			return -1;
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (endIndex > source.length) {
			endIndex = source.length;
		}
		
		for (int i = fromIndex; i < endIndex; i++) {
			if (source[i] == target) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * The source is the byte array being searched, and the target is the bytes being searched for.
	 * 
	 * @param source
	 *            the byte array being searched.
	 * @param sourceOffset
	 *            offset of the source byte array.
	 * @param sourceCount
	 *            count of the byte.
	 * @param target
	 *            the bytes being searched for.
	 * @param targetOffset
	 *            offset of the target bytes.
	 * @param targetCount
	 *            count of the target bytes.
	 * @param fromIndex
	 *            the index to begin searching from.
	 */
	private static int indexOf(byte[] source, int sourceOffset, int sourceCount, byte[] target, int targetOffset, int targetCount, int fromIndex) {
		if (fromIndex >= sourceCount) {
			return (targetCount == 0 ? sourceCount : -1);
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (targetCount == 0) {
			return fromIndex;
		}

		byte first = target[targetOffset];
		int max = sourceOffset + (sourceCount - targetCount);
		for (int i = sourceOffset + fromIndex; i <= max; i++) {
			/* Look for first character. */
			if (source[i] != first) {
				while (++i <= max && source[i] != first);
			}

			/* Found first character, now look at the rest of v2 */
			if (i <= max) {
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = targetOffset + 1; j < end && source[j] == target[k]; j++, k++);

				if (j == end) {
					/* Found whole string. */
					return i - sourceOffset;
				}
			}
		}
		
		return -1;
	}

	/**
	 * Returns a new byte array between start and end index.
	 * 
	 * @param bytes
	 *            to be splited
	 * @param start
	 *            the start index, inclusive.
	 * @param end
	 *            the ending index, exclusive.
	 * @return split byte array
	 */
	public static byte[] split(byte[] bytes, int start, int end) {
		if (bytes == null) {
			return null;
		}

		if (start < 0) {
			throw new IllegalArgumentException("start < 0");
		}
		if (end > bytes.length) {
			throw new IllegalArgumentException("end > size");
		}
		if (start > end) {
			throw new IllegalArgumentException("start > end");
		}

		int len = end - start;
		byte[] dest = new byte[len];
		System.arraycopy(bytes, start, dest, 0, len);

		return dest;
	}

	/**
	 * Reverse src byte array.
	 * 
	 * @param src
	 * @return the reversed byte array.
	 */
	public static byte[] reverse(byte[] src) {
		if (src == null) {
			return null;
		}

		byte[] dst = new byte[src.length];
		int j = 0;
		for (int i = src.length - 1; i >= 0; i--) {
			dst[j] = src[i];
			j++;
		}
		return dst;
	}

	/**
	 * Returns a hexadecimal representation of the given byte array.
	 * 
	 * @param bytes
	 *            the array to output to an hex string
	 * @return the hex representation as a string
	 */
	public static String asHex(byte[] bytes) {
		return asHex(bytes, null);
	}

	/**
	 * Returns a hexadecimal representation of the given byte array.
	 * 
	 * @param bytes
	 *            the array to output to an hex string
	 * @param separator
	 *            the separator to use between each byte in the output string. If null no char is inserted between each byte value.
	 * @return the hex representation as a string
	 */
	public static String asHex(byte[] bytes, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String code = Integer.toHexString(bytes[i] & 0xFF);
			if ((bytes[i] & 0xFF) < 16) {
				sb.append('0');
			}

			sb.append(code);

			if (separator != null && i < bytes.length - 1) {
				sb.append(separator);
			}
		}

		return sb.toString();
	}

	/**
	 * Encodes an integer into up to 4 bytes in network byte order.
	 * 
	 * @param num
	 *            the int to convert to a byte array
	 * @param count
	 *            the number of reserved bytes for the write operation
	 * @return the resulting byte array
	 */
	public static byte[] intToNetworkByteOrder(int num, int count) {
		byte[] buf = new byte[count];
		intToNetworkByteOrder(num, buf, 0, count);

		return buf;
	}

	/**
	 * Encodes an integer into up to 4 bytes in network byte order in the supplied buffer, 
	 * starting at <code>start</code> offset and writing <code>count</code> bytes.
	 * 
	 * @param num the int to convert to a byte array
	 * @param buf the buffer to write the bytes to
	 * @param start the offset from beginning for the write operation
	 * @param count the number of reserved bytes for the write operation
	 * @return the resulting byte array
	 */
	private static void intToNetworkByteOrder(int num, byte[] buf, int start, int count) {
		if (count > 4) {
			throw new IllegalArgumentException("Cannot handle more than 4 bytes");
		}

		for (int i = count - 1; i >= 0; i--) {
			buf[start + i] = (byte) (num & 0xff);
			num >>>= 8;
		}
	}
	
	/**
	 * Returns the integer represented by up to 4 bytes in network byte order.
	 * 
	 * @param buf
	 * @return the integer represented by up to 4 bytes in network byte order.
	 */
	public static int networkByteOrderToInt(byte[] buf) {
		return networkByteOrderToInt(buf, 0, buf.length);
	}

	/**
	 * Returns the integer represented by up to 4 bytes in network byte order.
	 * 
	 * @param buf
	 *            the buffer to read the bytes from
	 * @param start
	 * @param count
	 * @return the integer represented by up to 4 bytes in network byte order.
	 */
	public static int networkByteOrderToInt(byte[] buf, int start, int count) {
		if (count > 4) {
			throw new IllegalArgumentException("Cannot handle more than 4 bytes");
		}

		int result = 0;

		for (int i = 0; i < count; i++) {
			result <<= 8;
			result |= (buf[start + i] & 0xff);
		}

		return result;
	}

	/**
	 * Checks if a byte array is empty [] or null.
	 * 
	 * <pre>
	 * ByteUtil.isEmpty(null)          = true
	 * ByteUtil.isEmpty(new byte[] {}) = true
	 * </pre>
	 * 
	 * @param bytes
	 * @return true if byte array is empty.
	 */
	public static boolean isEmpty(byte[] bytes) {
		return bytes == null || bytes.length == 0;
	}
	
	/**
	 * Get a short from 2 bytes of the given array at offset 0.
	 * 
	 * @param  b byte array
	 * @return a short
	 */
	public static final short bytes2short(byte[] b) {
		return bytes2short(b, 0);
	}
	
	/**
	 * Get a short from 2 bytes of the given array at specific offset.
	 * 
	 * @param b   byte array
	 * @param off offset of the byte array
	 * @return a short
	 */
	public static final short bytes2short(byte[] b, int off) {
		return (short) ((b[off] & 0xff) << 8 | (b[off + 1] & 0xff));
	}
	
	/**
	 * Get a byte array from a short.
	 * 
	 * @param  s short
	 * @return a byte array
	 */
	public static final byte[] short2bytes(short s) {
		byte[] b = { 0, 0 };
		short2bytes(s, b, 0);
		return b;
	}
	
	/**
	 * Set a byte array at specific offset from a short.
	 * 
	 * @param s   short
	 * @param b   byte array
	 * @param off offset
	 */
	public static void short2bytes(short s, byte[] b, int off) {
		b[off + 1] = (byte) s;
		b[off + 0] = (byte) (s >>> 8);
	}
	
	/**
	 * Get a int from 4 bytes of the given array at offset 0.
	 * 
	 * @param b byte array
	 * @return a short
	 */
	public static final int bytes2int(byte[] b) {
		return bytes2int(b, 0);
	}
	
	/**
	 * Get a int from 4 bytes of the given array at specific offset.
	 * 
	 * @param b byte array
	 * @param off offset of the byte array
	 * @return a short
	 */
	public static final int bytes2int(byte[] b, int off) {
		return b[off] << 24 | (b[off + 1] & 0xff) << 16 | (b[off + 2] & 0xff) << 8 | (b[off + 3] & 0xff);
	}
	
	/**
	 * Get a byte array from an int.
	 * 
	 * @param  i int
	 * @return a byte array
	 */
	public static final byte[] int2bytes(int i) {
		byte[] b = { 0, 0, 0, 0 };
		int2bytes(i, b, 0);
		return b;
	}
	
	/**
	 * Set a byte array at specific offset from an int.
	 * 
	 * @param i   int
	 * @param b   byte array
	 * @param off offset
	 */
	public static void int2bytes(int i, byte[] b, int off) {
		b[off + 3] = (byte)  i;
		b[off + 2] = (byte) (i >>> 8);
		b[off + 1] = (byte) (i >>> 16);
		b[off + 0] = (byte) (i >>> 24);
	}
	
	/**
	 * Get a long from 8 bytes of the given array at offset 0.
	 * 
	 * @param b byte array
	 * @return a short
	 */
	public static final long bytes2long(byte[] b) {
		return bytes2long(b, 0);
	}
	
	/**
	 * Get a long from 8 bytes of the given array at specific offset.
	 * 
	 * @param b byte array
	 * @param off offset of the byte array
	 * @return a short
	 */
	public static final long bytes2long(byte[] b, int off) {
		return (((long) b[off]) << 56 | ((long) (b[off + 1] & 0xff)) << 48 | ((long) (b[off + 2]) & 0xff) << 40 | ((long) (b[off + 3]) & 0xff) << 32 | (((long) b[off + 4]) & 0xff) << 24 | (((long) b[off + 5]) & 0xff) << 16 | (((long) b[off + 6]) & 0xff) << 8 | (((long) b[off + 7]) & 0xff));
	}
	
	/**
	 * Get a byte array from a long.
	 * 
	 * @param  l long
	 * @return a byte array
	 */
	public static final byte[] long2bytes(long l) {
		byte[] b = { 0, 0, 0, 0, 0, 0, 0, 0 };
		long2bytes(l, b, 0);
		return b;
	}
	
	/**
	 * Set a byte array at specific offset from a long.
	 * 
	 * @param l long
	 * @param b byte array
	 * @param off offset
	 */
	public static void long2bytes(long l, byte[] b, int off) {
		b[off + 7] = (byte)  l;
		b[off + 6] = (byte) (l >>> 8);
		b[off + 5] = (byte) (l >>> 16);
		b[off + 4] = (byte) (l >>> 24);
		b[off + 3] = (byte) (l >>> 32);
		b[off + 2] = (byte) (l >>> 40);
		b[off + 1] = (byte) (l >>> 48);
		b[off + 0] = (byte) (l >>> 56);
	}
	
	/**
	 * Get a float from 4 bytes of the given array.
	 * 
	 * @param b byte array.
	 * @return a float.
	 */
	public static float bytes2float(byte[] b) {
		return bytes2float(b, 0);
	}

	/**
	 * Get a float from 4 bytes of the given array at specific offset.
	 * 
	 * @param b byte array.
	 * @param off offset.
	 * @return a float.
	 */
	public static float bytes2float(byte[] b, int off) {
		int i = b[off] << 24 | (b[off + 1] & 0xff) << 16 | (b[off + 2] & 0xff) << 8 | (b[off + 3] & 0xff);
		return Float.intBitsToFloat(i);
	}
	
	/**
	 * Get a byte array from a float.
	 * 
	 * @param f float
	 * @return a byte array
	 */
	public static final byte[] float2bytes(float f) {
		byte[] b = { 0, 0, 0, 0 };
		float2bytes(f, b, 0);
		return b;
	}
	
	/**
	 * Set a byte array at specific offset from a float.
	 * 
	 * @param f   float
	 * @param b   byte array
	 * @param off offset
	 */
	public static void float2bytes(float f, byte[] b, int off) {
		int i = Float.floatToIntBits(f);
		b[off + 3] = (byte)  i;
		b[off + 2] = (byte) (i >>> 8);
		b[off + 1] = (byte) (i >>> 16);
		b[off + 0] = (byte) (i >>> 24);
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * @deprecated replace by {@link #bytes2short(byte[])}
	 */
	public static final int makeIntFromByte2(byte[] b) {
		return makeIntFromByte2(b, 0);
	}

	/**
	 * @deprecated replace by {@link #bytes2short(byte[], off)}
	 */
	public static final int makeIntFromByte2(byte[] b, int off) {
		return (b[off] & 0xff) << 8 | (b[off + 1] & 0xff);
	}

	/**
	 * @deprecated replace by {@link #bytes2int(byte[])}
	 */
	public static final int makeIntFromByte4(byte[] b) {
		return makeIntFromByte4(b, 0);
	}

	/**
	 * @deprecated replace by {@link #bytes2int(byte[], off)}
	 */
	public static final int makeIntFromByte4(byte[] b, int off) {
		return b[off] << 24 | (b[off + 1] & 0xff) << 16 | (b[off + 2] & 0xff) << 8 | (b[off + 3] & 0xff);
	}

	private ByteUtil() {
		throw new UnsupportedOperationException();
	}

}

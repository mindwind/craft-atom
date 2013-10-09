package org.craft.atom.util;

import java.io.Serializable;

import lombok.ToString;

/**
 * A byte array buffer.
 * <br>
 * <b>NOTE: </b> it's not thread safe.
 * 
 * @author mindwind
 * @version 1.0, 2011-10-26
 */
@ToString
public final class ByteArrayBuffer implements Serializable {

	private static final long serialVersionUID = -5219299551050201309L;

	private byte[] buffer;
	private int len;
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * Creates an instance of {@link ByteArrayBuffer} with default(2048) initial capacity.
	 */
	public ByteArrayBuffer() {
		this(2048);
	}

	/**
	 * Creates an instance of {@link ByteArrayBuffer} with the given initial capacity.
	 * 
	 * @param capacity
	 *            the capacity
	 */
	public ByteArrayBuffer(int capacity) {
		checkCapacity(capacity);
		this.buffer = new byte[capacity];
	}

	/**
	 * Creates an instance of {@link ByteArrayBuffer} with the given byte array.
	 * 
	 * @param bytes
	 */
	public ByteArrayBuffer(final byte[] bytes) {
		super();
		if (bytes == null) {
			throw new NullPointerException("bytes is null");
		}
		this.buffer = bytes;
		this.len = bytes.length;
	}

	// ~ -------------------------------------------------------------------------------------------------------------

	private void checkCapacity(int capacity) {
		if (capacity < 0) {
			throw new IllegalArgumentException("Buffer capacity may not be negative");
		}
	}
	
	/**
	 * Appends <code>len</code> bytes to this buffer from the given source array starting at index <code>off</code>. 
	 * The capacity of the buffer is increased, if necessary, to accommodate all <code>len</code> bytes.
	 * 
	 * @param b
	 *            the bytes to be appended.
	 * @param off
	 *            the index of the first byte to append.
	 * @param len
	 *            the number of bytes to append.
	 * @throws IndexOutOfBoundsException
	 *            if <code>off</code> if out of range, <code>len</code> is negative, or <code>off</code> + <code>len</code> is out of range.
	 * @return a reference to this object.
	 */
	public ByteArrayBuffer append(final byte[] b, int off, int len) {
		if (b == null) {
			return this;
		}
		if ((off | len | (b.length - off) | (off + len) | (b.length - off - len)) < 0) {
			throw new IndexOutOfBoundsException("off: " + off + " len: " + len + " b.length: " + b.length);
		}
		if (len == 0) {
			return this;
		}

		int newlen = this.len + len;
		if (newlen > this.buffer.length) {
			expand(newlen);
		}
		System.arraycopy(b, off, this.buffer, this.len, len);
		this.len = newlen;

		return this;
	}

	/**
	 * Appends <code>b</code> byte to this buffer. The capacity of the buffer is increased, if necessary, to accommodate the additional byte.
	 * 
	 * @param b 
	 * 		   the byte to be appended.
	 * @return a reference to this object.
	 */
	public ByteArrayBuffer append(byte b) {
		int newlen = this.len + 1;
		if (newlen > this.buffer.length) {
			expand(newlen);
		}
		this.buffer[this.len] = b;
		this.len = newlen;

		return this;
	}

	/**
	 * Appends <code>b</code> byte to this buffer. The capacity of the buffer is increased, if necessary, to accommodate the additional byte.
	 * 
	 * @param b
	 *            the byte to be appended.
	 * @return a reference to this object.
	 */
	public ByteArrayBuffer append(final byte[] b) {
		if (b == null) {
			return this;
		}

		return append(b, 0, b.length);
	}

	/**
	 * Clears content of the buffer. The underlying byte array is not resized.
	 */
	public void clear() {
		this.len = 0;
	}
	
	/**
	 * Reset buffer with new capacity
	 * 
	 * @param capacity
	 */
	public void reset(int capacity) {
		checkCapacity(capacity);
		clear();
		this.buffer = new byte[capacity];
	}

	/**
	 * Return an new array of bytes from the buffer.
	 * 
	 * @return byte array
	 */
	public byte[] array() {
		return array0(0, this.len);
	}
	
	/**
	 * Return an new sub array of bytes from the buffer, with the boundary from start to end, if start==end return a zero byte array.
	 * 
	 * @param start The beginning index, inclusive
	 * @param end The ending index, exclusive.
	 * @return byte array
	 */
	public byte[] array(int start, int end) {
		if (start < 0 || end < 0 || start > end || end > length()) {
			throw new IllegalArgumentException("start=" + start + ", end=" + end + ", len=" + length());
		}
		
		int len = end - start;
		if (len == 0) {
			return new byte[0];
		}
		
		return array0(start, len);
	}
	
    private byte[] array0(int start, int len) {
		if (start < 0 || len < 0 || start + len > length()) {
			throw new IllegalArgumentException("start=" + start + ", len=" + len);
		}
		
		byte[] b = new byte[len];
		System.arraycopy(this.buffer, start, b, 0, len);
		return b;
	}

	/**
	 * Returns the <code>byte</code> value in this buffer at the specified index. 
	 * The index argument must be greater than or equal to <code>0</code>, and less than the length of this buffer.
	 * 
	 * @param i the index of the desired byte value.
	 * @return the byte value at the specified index.
	 * @throws IndexOutOfBoundsException if <code>index</code> is negative or greater than or equal to {@link #length()}.
	 */
	public byte byteAt(int i) {
		return this.buffer[i];
	}

	/**
	 * Returns the current capacity. The capacity is the amount of storage available for newly appended bytes, 
	 * beyond which an allocation will occur.
	 * 
	 * @return the current capacity
	 */
	public int capacity() {
		return this.buffer.length;
	}

	/**
	 * Returns the length of the buffer (byte count).
	 * 
	 * @return the length of the buffer
	 */
	public int length() {
		return this.len;
	}

	/**
	 * Returns reference to the underlying byte array.
	 * 
	 * @return the byte array.
	 */
	public byte[] buffer() {
		return this.buffer;
	}

	/**
	 * Returns <code>true</code> if this buffer is empty, that is, its {@link #length()} is equal to <code>0</code>.
	 * 
	 * @return <code>true</code> if this buffer is empty, <code>false</code> otherwise.
	 */
	public boolean isEmpty() {
		return this.len == 0;
	}

	/**
	 * Returns <code>true</code> if this buffer is full, that is, its {@link #length()} is equal to its {@link #capacity()}.
	 * 
	 * @return <code>true</code> if this buffer is full, <code>false</code> otherwise.
	 */
	public boolean isFull() {
		return this.len == this.buffer.length;
	}

	/**
	 * Returns the first occurence position of the specified byte in backing byte array
	 * 
	 * @param b
	 * @return if occurs, return the index of the first byte; if it does not occur, <code>-1</code> is returned.
	 */
	public int indexOf(byte b) {
		return ByteUtil.indexOf(this.buffer, b);
	}
	
    /**
     * Returns the index within this buffer of the first occurrence of the specified byte, starting the search at the specified
     * <code>beginIndex</code> and finishing at <code>endIndex</code>. If no such byte occurs in this buffer within the specified bounds, 
     * <code>-1</code> is returned.
     * <p>
     * There is no restriction on the value of <code>beginIndex</code> and <code>endIndex</code>. 
     * If <code>beginIndex</code> is negative, it has the same effect as if it were zero. 
     * If <code>endIndex</code> is greater than {@link #length()}, it has the same effect as if it were {@link #length()}. 
     * If the <code>beginIndex</code> is greater than the <code>endIndex</code>, <code>-1</code> is returned.
     *
     * @param   b            the byte to search for.
     * @param   beginIndex   the index to start the search from, inclusive
     * @param   endIndex     the index to finish the search at, exclusive
     * @return  the index of the first occurrence of the byte in the buffer within the given bounds, or <code>-1</code> if the bytes does not occur.
     *
     */
    public int indexOf(byte b, int beginIndex, int endIndex) {
    	if (beginIndex < 0) {
            beginIndex = 0;
        }
        if (endIndex > this.len) {
            endIndex = this.len;
        }
        if (beginIndex >= endIndex) {
            return -1;
        }
        
        for (int i = beginIndex; i < endIndex; i++) {
            if (this.buffer[i] == b) {
                return i;
            }
        }
        return -1;
    }

	/**
	 * Returns the first occurrence position of the specified bytes in backing byte array
	 * 
	 * @param bytes
	 * @return if occurs, return the index of the first byte; if it does not occur, <code>-1</code> is returned.
	 */
	public int indexOf(byte[] bytes) {
		return ByteUtil.indexOf(this.buffer, bytes);
	}
	
	/**
	 * Returns the index within this buffer of the first occurrence of the specified bytes, starting the search at the specified
     * <code>beginIndex</code> and finishing buffer end. If no such byte occurs in this buffer within the specified bounds, <code>-1</code> is returned.
     * <p>
     * There is no restriction on the value of <code>beginIndex</code>.
     * If <code>beginIndex</code> is negative, it has the same effect as if it were zero. 
     * 
	 * @param bytes the bytes to search for.
	 * @param beginIndex the index to start the search from, inclusive.
	 * @return the index of the first occurrence of the byte in the buffer within the given bounds, or <code>-1</code> if the byte does not occur.
	 */
	public int indexOf(byte[] bytes, int beginIndex) {
		if(bytes == null || beginIndex >= length()) {
			return -1;
		}
		if (beginIndex < 0) {
            beginIndex = 0;
        }
        
        return ByteUtil.indexOf(this.buffer, bytes, beginIndex, length());
	}
	
	/**
     * Returns the index within this buffer of the first occurrence of the specified bytes, starting the search at the specified
     * <code>beginIndex</code> and finishing at <code>endIndex</code>. If no such byte occurs in this buffer within the specified bounds, 
     * <code>-1</code> is returned.
     * <p>
     * There is no restriction on the value of <code>beginIndex</code> and <code>endIndex</code>. 
     * If <code>beginIndex</code> is negative, it has the same effect as if it were zero. 
     * If <code>endIndex</code> is greater than {@link #length()}, it has the same effect as if it were {@link #length()}. 
     * If the <code>beginIndex</code> is greater than the <code>endIndex</code>, <code>-1</code> is returned.
     *
     * @param   bytes        the bytes to search for.
     * @param   beginIndex   the index to start the search from, inclusive
     * @param   endIndex     the index to finish the search at, exclusive
     * @return  the index of the first occurrence of the byte in the buffer within the given bounds, or <code>-1</code> if the byte does not occur.
     *
     */
	public int indexOf(byte[] bytes, int beginIndex, int endIndex) {
		if(bytes == null || beginIndex >= endIndex) {
			return -1;
		}
		if (beginIndex < 0) {
            beginIndex = 0;
        }
		if (endIndex > length()) {
			endIndex = length();
		}
		
		return ByteUtil.indexOf(this.buffer, bytes, beginIndex, endIndex);
	}

	private void expand(int newlen) {
		byte newbuffer[] = new byte[Math.max(this.buffer.length << 1, newlen)];
		System.arraycopy(this.buffer, 0, newbuffer, 0, this.len);
		this.buffer = newbuffer;
	}

}

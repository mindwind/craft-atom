package org.craft.atom.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link ByteArrayBuffer}
 * 
 * @author Hu Feng
 * @version 1.0, 2011-12-21
 */
public class ByteArrayBufferTest {

	@Test
	public void testAppendNull() {
		ByteArrayBuffer buffer = new ByteArrayBuffer(8);
		
		buffer.append((byte[])null, 0, 0);
        Assert.assertEquals(0, buffer.length());
	}
	
	@Test
	public void testAppendEmpty() {
		ByteArrayBuffer buffer = new ByteArrayBuffer(8);
        buffer.append(new byte[] {});
        Assert.assertEquals(0, buffer.length());
	}

	@Test
	public void testAppendBytes() {
		ByteArrayBuffer buffer = new ByteArrayBuffer(8);
		byte[] tmp = new byte[] { 1, 127, -1, -128, 1, -2 };
		for (int i = 0; i < tmp.length; i++) {
			buffer.append(tmp[i]);
		}
		Assert.assertEquals(8, buffer.capacity());
		Assert.assertEquals(6, buffer.length());

		for (int i = 0; i < tmp.length; i++) {
			Assert.assertEquals(tmp[i], buffer.byteAt(i));
		}
	}
	
    @Test
    public void testConstructor() throws Exception {
        ByteArrayBuffer buffer = new ByteArrayBuffer(16);
        Assert.assertEquals(16, buffer.capacity());
        Assert.assertEquals(0, buffer.length());
        Assert.assertNotNull(buffer.buffer());
        Assert.assertEquals(16, buffer.buffer().length);
        try {
            new ByteArrayBuffer(-1);
            Assert.fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException ex) {
        	// expected
        }
    }
    
    @Test
    public void testExpandAppend() throws Exception {
        ByteArrayBuffer buffer = new ByteArrayBuffer(4);
        Assert.assertEquals(4, buffer.capacity());

        byte[] tmp = new byte[] { 1, 2, 3, 4};
        buffer.append(tmp, 0, 2);
        buffer.append(tmp, 0, 4);
        buffer.append(tmp, 0, 0);

        Assert.assertEquals(8, buffer.capacity());
        Assert.assertEquals(6, buffer.length());

        buffer.append(tmp, 0, 4);

        Assert.assertEquals(16, buffer.capacity());
        Assert.assertEquals(10, buffer.length());
    }
	
    @Test
    public void testInvalidAppend() throws Exception {
        ByteArrayBuffer buffer = new ByteArrayBuffer(4);
        buffer.append((byte[])null, 0, 0);

        byte[] tmp = new byte[] { 1, 2, 3, 4};
        try {
            buffer.append(tmp, -1, 0);
            Assert.fail("IndexOutOfBoundsException should have been thrown");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        try {
            buffer.append(tmp, 0, -1);
            Assert.fail("IndexOutOfBoundsException should have been thrown");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        try {
            buffer.append(tmp, 0, 8);
            Assert.fail("IndexOutOfBoundsException should have been thrown");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        try {
            buffer.append(tmp, 10, Integer.MAX_VALUE);
            Assert.fail("IndexOutOfBoundsException should have been thrown");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        try {
            buffer.append(tmp, 2, 4);
            Assert.fail("IndexOutOfBoundsException should have been thrown");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
    }
    
    @Test
    public void testIndexOf() throws Exception {
        final byte COLON = (byte) ':';
        final byte COMMA = (byte) ',';
        byte[] bytes = "name1: value1; name2: value2".getBytes("US-ASCII");
        int index1 = 5;
        int index2 = 20;

        ByteArrayBuffer buffer = new ByteArrayBuffer(16);
        buffer.append(bytes, 0, bytes.length);

        Assert.assertEquals(index1, buffer.indexOf(COLON));
        Assert.assertEquals(-1, buffer.indexOf(COMMA));
        Assert.assertEquals(index1, buffer.indexOf(COLON, -1, 11));
        Assert.assertEquals(index1, buffer.indexOf(COLON, 0, 1000));
        Assert.assertEquals(-1, buffer.indexOf(COLON, 2, 1));
        Assert.assertEquals(index2, buffer.indexOf(COLON, index1 + 1, buffer.length()));
    }
    
    @Test
    public void testArray() throws Exception {
    	ByteArrayBuffer buffer = new ByteArrayBuffer(16);
    	byte[] bytes = buffer.array(0, 0);
    	Assert.assertNotNull(bytes);
    	Assert.assertEquals(0, bytes.length);
    }
}

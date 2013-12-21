package org.craft.atom.nio;

import java.nio.ByteBuffer;

import junit.framework.Assert;

import org.craft.atom.test.CaseCounter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Jan 26, 2013
 */
public class TestNioByteBuffer {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(TestNioByteBuffer.class);
	
	
	private int[] arr = new int[] { 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 10240 };

	
	@Test
	public void testAllocateDirectHeap() throws InterruptedException {
		long s = System.nanoTime();
		for (int i = 32; i < 1024000; i*=2) {
			ByteBuffer.allocateDirect(i);
		}
		long e = System.nanoTime();
		long directElapse = e - s;
		
		s = System.nanoTime();
		for (int i = 32; i < 1024000; i*=2) {
			ByteBuffer.allocate(i);
		}
		e = System.nanoTime();
		long heapElapse = e - s;
		
		LOG.info("[CRAFT-ATOM-NIO] Test allocate heap elpase={} ns", heapElapse);
		LOG.info("[CRAFT-ATOM-NIO] Test allocate direct elpase={} ns", directElapse);
		Assert.assertTrue(true);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test allocate direct & heap. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testAllocateDirectHeapEnum() throws InterruptedException {
		long s = System.nanoTime();
		for (int i = 1; i < 10000; i++) {
			ByteBuffer.allocateDirect(arr[i % arr.length]);
		}
		long e = System.nanoTime();
		long directElapse = e - s;
		
		s = System.nanoTime();
		for (int i = 1; i < 10000; i++) {
			ByteBuffer.allocate(arr[i % arr.length]);
		}
		e = System.nanoTime();
		long heapElapse = e - s;
		
		LOG.info("[CRAFT-ATOM-NIO] Test allocate heap enum elpase={} ns", heapElapse);
		LOG.info("[CRAFT-ATOM-NIO] Test allocate direct enum elpase={} ns", directElapse);
		Assert.assertTrue(true);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test allocate direct & heap enum . ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testAllocateDirectHeapFix() throws InterruptedException {
		long s = System.nanoTime();
		for (int i = 1; i < 10000; i++) {
			ByteBuffer.allocateDirect(2048);
		}
		long e = System.nanoTime();
		long directElapse = e - s;
		
		s = System.nanoTime();
		for (int i = 1; i < 10000; i++) {
			ByteBuffer.allocate(2048);
		}
		e = System.nanoTime();
		long heapElapse = e - s;
		
		LOG.info("[CRAFT-ATOM-NIO] Test allocate heap fix elpase={} ns", heapElapse);
		LOG.info("[CRAFT-ATOM-NIO] Test allocate direct fix elpase={} ns", directElapse);
		Assert.assertTrue(true);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test allocate direct & heap fix . ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testDirectHeapByteBufferCopy() throws InterruptedException {
		int size = 10240;
		ByteBuffer buf = ByteBuffer.allocate(size);
		for (int i = 0; i < size; i++) {
			buf.put((byte) (i % 127));
		}
		buf.position(0);
		long s = System.nanoTime();
		byte[] arr = new byte[size];
		buf.get(arr, 0, size);
		long e = System.nanoTime();
		long heapElapse = e - s;
		
		
		buf = ByteBuffer.allocateDirect(size);
		for (int i = 0; i < size; i++) {
			buf.put((byte) (i % 127));
		}
		buf.position(0);
		s = System.nanoTime();
		arr = new byte[size];
		buf.get(arr, 0, size);
		e = System.nanoTime();
		long directElapse = e - s;
		
		LOG.info("[CRAFT-ATOM-NIO] Test heap byte buffer copy elpase={} ns", heapElapse);
		LOG.info("[CRAFT-ATOM-NIO] Test direct byte buffer copy elpase={} ns", directElapse);
		Assert.assertTrue(true);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test direct & heap byte buffer copy. ", CaseCounter.incr(1)));
	}
	
}

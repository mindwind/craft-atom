package org.craft.atom.nio;

import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * @author mindwind
 * @version 1.0, Jan 26, 2013
 */
public class NioByteBufferTest {
	
	private int[] arr = new int[] {32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 10240};

	@Test
	public void testAllocateDirect() throws InterruptedException {
		Thread.sleep(2000);
		
		long s = System.nanoTime();
		for (int i = 32; i < 1024000; i*=2) {
			ByteBuffer.allocateDirect(i);
		}
		long e = System.nanoTime();
		System.out.println("allocate direct end ... elpase :" + (e - s) + " ns");
	}
	
	@Test
	public void testAllocate() throws InterruptedException {
		Thread.sleep(2000);
		
		long s = System.nanoTime();
		for (int i = 32; i < 1024000; i*=2) {
			ByteBuffer.allocate(i);
		}
		long e = System.nanoTime();
		System.out.println("allocate heap end ... elpase :" + (e - s) + " ns");
	}
	
	@Test
	public void testAllocateDirectRepeat() throws InterruptedException {
		Thread.sleep(2000);
		
		long s = System.nanoTime();
		for (int i = 1; i < 10000; i++) {
			ByteBuffer.allocateDirect(arr[i % arr.length]);
		}
		long e = System.nanoTime();
		System.out.println("allocate direct repeat end ... elpase :" + (e - s) + " ns");
	}
	
	@Test
	public void testAllocateRepeat() throws InterruptedException {
		Thread.sleep(2000);
		
		long s = System.nanoTime();
		for (int i = 1; i < 10000; i++) {
			ByteBuffer.allocate(arr[i % arr.length]);
		}
		long e = System.nanoTime();
		System.out.println("allocate heap repeat end ... elpase :" + (e - s) + " ns");
	}
	
	@Test
	public void testAllocateDirectFix() throws InterruptedException {
		Thread.sleep(2000);
		
		long s = System.nanoTime();
		for (int i = 1; i < 10000; i++) {
			ByteBuffer.allocateDirect(2048);
		}
		long e = System.nanoTime();
		System.out.println("allocate direct fix end ... elpase :" + (e - s) + " ns");
	}
	
	@Test
	public void testAllocateFix() throws InterruptedException {
		Thread.sleep(2000);
		
		long s = System.nanoTime();
		for (int i = 1; i < 10000; i++) {
			ByteBuffer.allocate(2048);
		}
		long e = System.nanoTime();
		System.out.println("allocate heap fix end ... elpase :" + (e - s) + " ns");
	}
	
	@Test
	public void testHeapByteBufferCopy() throws InterruptedException {
		int size = 10240;
		ByteBuffer buf = ByteBuffer.allocate(size);
		for (int i = 0; i < size; i++) {
			buf.put((byte) (i % 127));
		}
		buf.position(0);
		
		Thread.sleep(2000);
		long s = System.nanoTime();
		byte[] arr = new byte[size];
//		System.arraycopy(buf.array(), 0, arr, 0, size);
		buf.get(arr, 0, size);
		long e = System.nanoTime();
		System.out.println("heap byte buffer copy end ... elpase :" + (e - s) + " ns");
	}
	
	@Test
	public void testDirectByteBufferCopy() throws InterruptedException {
		int size = 10240;
		ByteBuffer buf = ByteBuffer.allocateDirect(size);
		for (int i = 0; i < size; i++) {
			buf.put((byte) (i % 127));
		}
		buf.position(0);
		
		Thread.sleep(2000);
		long s = System.nanoTime();
		byte[] arr = new byte[size];
		buf.get(arr, 0, size);
		long e = System.nanoTime();
		System.out.println("direct byte buffer copy end ... elpase :" + (e - s) + " ns");
	}
	
}

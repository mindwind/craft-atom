package org.craft.atom.nio;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author Hu Feng
 * @version 1.0, Jan 25, 2013
 */
public class AdaptiveSizePredictorTest {
	
	@Test
	public void testDefault() {
		AdaptiveSizePredictor asp = new AdaptiveSizePredictor();
		System.out.println(asp.next());
	}
	
	@Test
	public void testUp() {
		AdaptiveSizePredictor asp = new AdaptiveSizePredictor();
		for (int i = 32; i < 4000; i++) {
			asp.previous(i);
		}
		System.out.println(asp.next());
	}
	
	@Test
	public void testDown() {
		AdaptiveSizePredictor asp = new AdaptiveSizePredictor();
		for (int i = 4000; i >= 80; i--) {
			asp.previous(i);
		}
		System.out.println(asp.next());
	}
	
	@Test
	public void testRegular() {
		AdaptiveSizePredictor asp = new AdaptiveSizePredictor(64, 2048, 65536);
		System.out.println(Arrays.toString(AdaptiveSizePredictor.getSizeTable()));
		
		asp.previous(1024);
		System.out.println("pre=1024, next=" + asp.next());
		
		asp.previous(2048);
		System.out.println("pre=2048, next=" + asp.next());
		
		asp.previous(2048);
		System.out.println("pre=4096, next=" + asp.next());
		
		for (int i = 0; i < 21; i++) {
			asp.previous(512);
			System.out.println("pre=512, next=" + asp.next());
		}
	}
	
}

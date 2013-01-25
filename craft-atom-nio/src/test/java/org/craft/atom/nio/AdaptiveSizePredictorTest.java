package org.craft.atom.nio;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Hu Feng
 * @version 1.0, Jan 25, 2013
 */
public class AdaptiveSizePredictorTest {
	
	@Test
	public void testDefault() {
		AdaptiveSizePredictor asp = new AdaptiveSizePredictor();
		Assert.assertEquals(1024, asp.next());
		System.out.println(asp.next());
	}
	
	@Test
	public void testUp() {
		AdaptiveSizePredictor asp = new AdaptiveSizePredictor();
		for (int i = 32; i < 4000; i++) {
			asp.previous(i);
		}
		Assert.assertEquals(4096, asp.next());
		System.out.println(asp.next());
	}
	
	@Test
	public void testDown() {
		AdaptiveSizePredictor asp = new AdaptiveSizePredictor();
		for (int i = 4000; i >= 80; i--) {
			asp.previous(i);
		}
		Assert.assertEquals(96, asp.next());
		System.out.println(asp.next());
	}
	
}

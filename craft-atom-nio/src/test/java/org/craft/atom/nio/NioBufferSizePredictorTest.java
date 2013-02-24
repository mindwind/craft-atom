package org.craft.atom.nio;

import java.util.Arrays;

import org.craft.atom.nio.spi.NioBufferSizePredictor;
import org.junit.Test;

/**
 * @author mindwind
 * @version 1.0, Jan 25, 2013
 */
public class NioBufferSizePredictorTest {
	
	@Test
	public void testDefault() {
		NioBufferSizePredictor predictor = new NioAdaptiveBufferSizePredictor();
		System.out.println(predictor.next());
	}
	
	@Test
	public void testUp() {
		NioBufferSizePredictor predictor = new NioAdaptiveBufferSizePredictor();
		for (int i = 32; i < 4000; i++) {
			predictor.previous(i);
		}
		System.out.println(predictor.next());
	}
	
	@Test
	public void testDown() {
		NioBufferSizePredictor predictor = new NioAdaptiveBufferSizePredictor();
		for (int i = 4000; i >= 80; i--) {
			predictor.previous(i);
		}
		System.out.println(predictor.next());
	}
	
	@Test
	public void testRegular() {
		NioBufferSizePredictor predictor = new NioAdaptiveBufferSizePredictor(64, 2048, 65536);
		System.out.println(Arrays.toString(NioAdaptiveBufferSizePredictor.getSizeTable()));
		
		predictor.previous(1024);
		System.out.println("pre=1024, next=" + predictor.next());
		
		predictor.previous(2048);
		System.out.println("pre=2048, next=" + predictor.next());
		
		predictor.previous(2048);
		System.out.println("pre=4096, next=" + predictor.next());
		
		for (int i = 0; i < 21; i++) {
			predictor.previous(512);
			System.out.println("pre=512, next=" + predictor.next());
		}
	}
	
}

package io.craft.atom.nio;

import io.craft.atom.nio.NioAdaptiveBufferSizePredictor;
import io.craft.atom.nio.spi.NioBufferSizePredictor;
import io.craft.atom.test.CaseCounter;
import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Jan 25, 2013
 */
public class TestNioBufferSizePredictor {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(TestNioBufferSizePredictor.class);
	
	
	@Test
	public void testDefault() {
		NioBufferSizePredictor predictor = new NioAdaptiveBufferSizePredictor();
		int next = predictor.next();
		Assert.assertEquals(1024, next);
		LOG.debug("[CRAFT-ATOM-NIO] Test predictor default, next={}", next);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test predictor default. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testUp() {
		int next = 0;
		NioBufferSizePredictor predictor = new NioAdaptiveBufferSizePredictor();
		for (int i = 32; i < 4000; i++) {
			predictor.previous(i);
			next = predictor.next();
			LOG.debug("[CRAFT-ATOM-NIO] Ttest predictor up, previous={}, next={}", i, next);
		}
		Assert.assertEquals(4096, next);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test predictor up. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testDown() {
		NioBufferSizePredictor predictor = new NioAdaptiveBufferSizePredictor();
		for (int i = 4000; i >= 80; i--) {
			predictor.previous(i);
			LOG.debug("[CRAFT-ATOM-NIO] Test predictor down, previous={}, next={}", i, predictor.next());
		}
		Assert.assertEquals(96, predictor.next());
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test predictor down. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testRegular() {
		NioBufferSizePredictor predictor = new NioAdaptiveBufferSizePredictor(64, 2048, 65536);
		
		int next = 0;
		predictor.previous(1024);
		next = predictor.next();
		Assert.assertEquals(2048, next);
		LOG.debug("[CRAFT-ATOM-NIO] Test predictor regular, previous={}, next={}", 1024, next);
		
		predictor.previous(2048);
		next = predictor.next();
		Assert.assertEquals(3072, next);
		LOG.debug("[CRAFT-ATOM-NIO] Test predictor regular, previous={}, next={}", 2048, next);
		
		predictor.previous(2048);
		next = predictor.next();
		Assert.assertEquals(3072, next);
		LOG.debug("[CRAFT-ATOM-NIO] Test predictor regular, previous={}, next={}", 2048, next);
		
		for (int i = 0; i < 40; i++) {
			predictor.previous(478);
		}
		next = predictor.next();
		Assert.assertEquals(512, next);
		LOG.debug("[CRAFT-ATOM-NIO] Test predictor regular, previous={} next={}", 478, next);
		System.out.println(String.format("[CRAFT-ATOM-NIO] (^_^)  <%s>  Case -> test predictor regular. ", CaseCounter.incr(4)));
	}
	
}

package io.craft.atom.lock;

import io.craft.atom.test.CaseCounter;

import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * @author mindwind
 * @version 1.0, Dec 22, 2013
 */
public class TestRedis24DLock extends AbstractDLockTests {
	
	
	private String lockKey = "redis24.dlock";
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public TestRedis24DLock() {
		super();
	}
	
	@Before
	public void before() {
		redis1.flushall();
	}
		
	
	// ~ -------------------------------------------------------------------------------------------------------------
		
	
	@Test
	public void testTryLockFalse() {
		// WATCH: lock=true   GET: lock=true  -> LOCK: false
		// WATCH: lock=true   GET: lock=false -> LOCK: false
		// WATCH: lock=false  GET: lock=true  -> LOCK: false
		redis1.set(lockKey, "1");
		boolean b = dLock.tryLock(lockKey, 30, TimeUnit.SECONDS);
		Assert.assertFalse(b);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test try lock false. ", CaseCounter.incr(1)));
	}
	
	@Test
	public void testTryLockTrue() {
		// WATCH: lock=false  GET: lock=false -> LOCK: true
		redis1.del(lockKey);
		boolean b = dLock.tryLock(lockKey, 30, TimeUnit.SECONDS);
		Assert.assertTrue(b);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test try lock true. ", CaseCounter.incr(1)));
	}
		
}

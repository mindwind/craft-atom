package org.craft.atom.redis;

import org.craft.atom.redis.api.Redis;
import org.craft.atom.redis.api.RedisFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/**
 * @author mindwind
 * @version 1.0, Dec 3, 2013
 */
public abstract class AbstractRedisTests {
	
	
	protected static final int    PORT1 = 6379       ;
	protected static final int    PORT2 = 6380       ;
	protected static final int    PORT3 = 6381       ;
	protected static final String HOST  = "127.0.0.1";
	
	
	protected Redis redis1;
	protected Redis redis2;
	protected Redis redis3;
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public AbstractRedisTests() {
		init();
		selfcheck();
	}
	
	
	private void init() {
		redis1 = RedisFactory.newRedis(HOST, PORT1);
		redis2 = RedisFactory.newRedis(HOST, PORT2);
		redis3 = RedisFactory.newRedis(HOST, PORT3);
	}
	
	
	private void selfcheck() {
		try {
			redis1.ping();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[CRAFT-ATOM-REDIS] Self check for redis1 fail");
			Assert.fail();
		}
		
		try {
			redis2.ping();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[CRAFT-ATOM-REDIS] Self check for redis2 fail");
			Assert.fail();
		}
		
		try {
			redis3.ping();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[CRAFT-ATOM-REDIS] Self check for redis3 fail");
			Assert.fail();
		}
	}
	
	@Before
	public void before() {
		clean();
	}
	
	@After
	public void after() {
		clean();
	}
	
	protected void clean() {
		try {
			redis1.flushall();
			redis2.flushall();
			redis3.flushall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

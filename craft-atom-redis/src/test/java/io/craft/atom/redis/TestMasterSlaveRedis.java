package io.craft.atom.redis;

import io.craft.atom.redis.api.MasterSlaveRedis;
import io.craft.atom.redis.api.Redis;
import io.craft.atom.redis.api.RedisFactory;
import io.craft.atom.test.CaseCounter;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link MasterSlaveRedis}
 * 
 * @author mindwind
 * @version 1.0, Dec 3, 2013
 */
public class TestMasterSlaveRedis extends AbstractRedisTests {

	
	private MasterSlaveRedis masterSlaveRedis;
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public TestMasterSlaveRedis() {
		super();
	}

	
	@Before
	public void before() {
		masterSlaveRedis = RedisFactory.newMasterSlaveRedis(redis1, redis2, redis3);
	}
	
	@After
	public void after() {
		try {
			redis1.slaveofnoone();
			redis2.slaveofnoone();
			redis3 = RedisFactory.newRedis(HOST, PORT3);
			redis3.slaveofnoone();
			clean();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	@Test
	public void testSwitchoverNormal() {
		masterSlaveRedis.master(1);
		Redis m = masterSlaveRedis.master();
		Assert.assertEquals(PORT2, m.port());
		
		List<Redis>  chain = masterSlaveRedis.chain();
		Assert.assertEquals(3, chain.size());
		Redis s1 = chain.get(1);
		Redis s2 = chain.get(2);
		Assert.assertEquals(PORT2, chain.get(0).port());
		Assert.assertEquals(PORT3, s1.port());
		Assert.assertEquals(PORT1, s2.port());
		
		String slaveof = masterSlaveRedis.configget("slaveof").get("slaveof");
		Assert.assertNull(slaveof);
		slaveof = s1.configget("slaveof").get("slaveof");
		Assert.assertEquals(m.host() + " " + m.port(), slaveof);
		slaveof = s2.configget("slaveof").get("slaveof");
		Assert.assertEquals(s1.host() + " " + s1.port(), slaveof);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test switchover normal. ", CaseCounter.incr(7)));
	}
	
	@Test
	public void testSwitchoverException() {
		redis3.quit();
		
		// switch to index-2
		try {
			masterSlaveRedis.master(2);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		masterSlaveRedis.master(1);
		Redis m = masterSlaveRedis.master();
		Assert.assertEquals(PORT2, m.port());
		String slaveof = redis2.configget("slaveof").get("slaveof");
		Assert.assertNull(slaveof);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test switchover exception. ", CaseCounter.incr(3)));
	}
	
	@Test
	public void testReadSlave() throws InterruptedException {
		// write to master
		String key = "test";
		String value = "123";
		masterSlaveRedis.enableReadSlave();
		masterSlaveRedis.set(key, value);
		Boolean b = masterSlaveRedis.exists(key);
		Assert.assertFalse(b);
		
		// wait a while for master-slave sync.
		Thread.sleep(2000);
		
		// read from slave
		b = masterSlaveRedis.exists(key);
		String v = masterSlaveRedis.get(key);
		Assert.assertTrue(b);
		Assert.assertEquals(value, v);
		System.out.println(String.format("[CRAFT-ATOM-REDIS] (^_^)  <%s>  Case -> test read slave. ", CaseCounter.incr(3)));
	}
	
}

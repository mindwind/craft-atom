package org.craft.atom.redis;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.craft.atom.redis.api.MasterSlaveRedis;
import org.craft.atom.redis.api.Redis;
import org.craft.atom.redis.api.RedisFactory;

/**
 * @author mindwind
 * @version 1.0, Jun 28, 2013
 */
public class MasterSlaveRedisMain extends AbstractMain {
	
	private static final String HOST = "127.0.0.1";
	private static final int PORT0 = 6379;
	private static final int PORT1 = 6380;
	private static final int PORT2 = 6381;
	private static MasterSlaveRedis r;
	
	private static void init() {
		Redis master = RedisFactory.newRedis(HOST, PORT0);
		Redis slave1 = RedisFactory.newRedis(HOST, PORT1);
		Redis slave2 = RedisFactory.newRedis(HOST, PORT2);
		List<Redis> chain = new ArrayList<Redis>(3);
		chain.add(master);
		chain.add(slave1);
		chain.add(slave2);
		r = RedisFactory.newMasterSlaveRedis(master, slave1, slave2);
	}
	
	protected static void after() {
		r.reset();
	}
	
	public static void main(String[] args) throws Exception {
		init();
		
		testSwitchover();
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------ Test Cases
	
	private static void testSwitchover() {
		before("testSwitchover");
		
		r.master(1);
		Redis m = r.master();
		Assert.assertEquals(PORT1, m.port());
		
		List<Redis> chain = r.chain();
		Assert.assertEquals(3, chain.size());
		Redis s1 = chain.get(1);
		Redis s2 = chain.get(2);
		Assert.assertEquals(PORT1, chain.get(0).port());
		Assert.assertEquals(PORT2, s1.port());
		Assert.assertEquals(PORT0, s2.port());
		
		String slaveof = r.configget("slaveof").get("slaveof");
		Assert.assertNull(slaveof);
		slaveof = s1.configget("slaveof").get("slaveof");
		Assert.assertEquals(m.host() + " " + m.port(), slaveof);
		slaveof = s2.configget("slaveof").get("slaveof");
		Assert.assertEquals(s1.host() + " " + s1.port(), slaveof);
		
		after();
	}
	
}

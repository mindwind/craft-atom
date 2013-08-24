package org.craft.atom.redis;

import lombok.ToString;

import org.craft.atom.redis.api.MasterSlaveRedis;
import org.craft.atom.redis.api.MasterSlaveShardedRedis;
import org.craft.atom.redis.api.Redis;
import org.craft.atom.redis.api.RedisFactory;
import org.craft.atom.redis.api.ShardedRedis;
import org.junit.Test;

/**
 * @author mindwind
 * @version 1.0, Aug 24, 2013
 */
public class ToStringTest {

	@Test
	public void test() {
		Redis r = RedisFactory.newRedis("localhost", 6379);
		System.out.println(r);

		MasterSlaveRedis msr = RedisFactory.newMasterSlaveRedis("localhost:6379-localhost:6380");
		System.out.println(msr);
		
		ShardedRedis sr = RedisFactory.newShardedRedis("localhost:6379,localhost:6380");
		System.out.println(sr);
		
		MasterSlaveShardedRedis mssr = RedisFactory.newMasterSlaveShardedRedis("localhost:6379-localhost:6380,127.0.0.1:6379-127.0.0.1:6380");
		System.out.println(mssr);
	}
	
	@Test 
	public void testFoobar() {
		Foo foo = new Foo();
		System.out.println(foo);
	}
	
	@ToString
	private static class Bar {
		private String bbb = "bbb";
		private String ccc = "ccc";
	}
	
	@ToString(of = { "aaa", "fff" }, callSuper = true)
	private static class Foo extends Bar {
		private String aaa = "aaa";
		private String fff = "fff";
	}

}

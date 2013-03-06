package org.craft.atom.cache.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.craft.atom.cache.HashCache;
import org.craft.atom.cache.ListCache;
import org.craft.atom.cache.LongCache;
import org.craft.atom.cache.SetCache;
import org.craft.atom.cache.SortedSetCache;
import org.craft.atom.cache.StringCache;
import org.craft.atom.cache.Transaction;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Tuple;

/**
 * @author mindwind
 * @version 1.0, Sep 8, 2012
 */
public class RedisCache implements ListCache, SetCache, SortedSetCache, HashCache, StringCache, LongCache {
	
	private static final ThreadLocal<AbstractTransaction> THREAD_LOCAL_TRANSACTION = new ThreadLocal<AbstractTransaction>();
	private static final ThreadLocal<Jedis> THREAD_LOCAL_JEDIS = new ThreadLocal<Jedis>();
	private static final ThreadLocal<ShardedJedis> THREAD_LOCAL_SHARDED_JEDIS = new ThreadLocal<ShardedJedis>();
	
	private int timeout = 3000;
	private boolean isShard = true;
	private ShardedJedisPool shardedPool;
	private JedisPool pool;
	private List<CacheHost> cacheHosts;
	private JedisPoolConfig poolConfig;
	
	// ~ --------------------------------------------------------------------------------------------------------
	
	/**
	 * Construct a new redis cache instance.
	 * 
	 * @param isShard 		means is sharded or not.
	 * @param cacheHosts	cache host represent by a IP address and port.
	 */
	public RedisCache(boolean isShard, String cacheHosts) {
		if (cacheHosts == null) {
			throw new IllegalArgumentException("at least have one cache host!");
		}
		
		List<CacheHost> chlist = toCacheHostList(cacheHosts);
		this.cacheHosts = chlist;
		init(isShard, timeout, chlist, defaultJedisPoolConfig());
	}
	
	/**
	 * Construct a new redis cache instance with cache hosts string with specified format e.g. 192.168.1.100:6379,192.168.1.101:6379
	 * 
	 * @param isShard 		means is sharded or not.
	 * @param timeout 		connect timeout in milliseconds.
	 * @param cacheHosts 	cache host represent by a IP address and port.
	 */
	public RedisCache(boolean isShard, int timeout, String cacheHosts) {
		if (cacheHosts == null) {
			throw new IllegalArgumentException("at least have one cache host!");
		}
		
		List<CacheHost> chlist = toCacheHostList(cacheHosts);
		this.cacheHosts = chlist;
		init(isShard, timeout, chlist, defaultJedisPoolConfig());
	}
	
	/**
	 * Construct a new redis cache instance.
	 * 
	 * @param isShard 		means is sharded or not.
	 * @param timeout 		connect timeout in milliseconds.
	 * @param cacheHosts 	cache host represent by a IP address and port.
	 */
	public RedisCache(boolean isShard, int timeout, List<CacheHost> cacheHosts) {
		this.cacheHosts = cacheHosts;
		init(isShard, timeout, cacheHosts, defaultJedisPoolConfig());
	}
	
	/**
	 * Construct a new redis cache instance.
	 * 
	 * @param isShard 		means is sharded or not.
	 * @param timeout 		connect timeout in milliseconds.
	 * @param cacheHosts 	cache host represent by a IP address and port with format e.g. 192.168.1.100:6379,192.168.1.101:6379
	 * @param poolSize 		connect pool size
	 */
	public RedisCache(boolean isShard, int timeout, String cacheHosts, int poolSize) {
		if (cacheHosts == null) {
			throw new IllegalArgumentException("at least have one cache host!");
		}
		
		List<CacheHost> chlist = toCacheHostList(cacheHosts);
		this.cacheHosts = chlist;
		JedisPoolConfig config = defaultJedisPoolConfig();
		config.setMinIdle(0);
		config.setMaxIdle(poolSize);
		config.setMaxActive(poolSize);
		init(isShard, timeout, chlist, config);
	}
	
	/**
	 * Construct a new redis cache instance.
	 * 
	 * @param isShard 		means is sharded or not.
	 * @param timeout 		connect timeout in milliseconds.
	 * @param cacheHosts 	cache host represent by a IP address and port.
	 */
	public RedisCache(boolean isShard, int timeout, List<CacheHost> cacheHosts, JedisPoolConfig poolConfig) {
		this.cacheHosts = cacheHosts;
		init(isShard, timeout, cacheHosts, poolConfig);
	}
	
	// ~ --------------------------------------------------------------------------------------------------------
	
	private JedisPoolConfig defaultJedisPoolConfig() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxActive(100);
		poolConfig.setMaxIdle(100);
		poolConfig.setMinIdle(100);
		return poolConfig;
	}
	
	private List<CacheHost> toCacheHostList(String cacheHosts) {
		List<CacheHost> hosts = new ArrayList<CacheHost>();
		
		String[] hostArr = cacheHosts.split(",");
		for (String hostStr : hostArr) {
			String[] sarr = hostStr.split(":");
			CacheHost ch = new CacheHost(sarr[0], Integer.valueOf(sarr[1]));
			hosts.add(ch);
		}
		
		return hosts;
	}
	
	private void init(boolean isShard, int timeout, List<CacheHost> cacheHosts, JedisPoolConfig poolConfig) {
		if (timeout <= 0) { 
			throw new IllegalArgumentException("timeout must > 0!"); 
		}
		if (cacheHosts == null || cacheHosts.size() == 0) {
			throw new IllegalArgumentException("at least have one cache host!");
		}
		
		this.timeout = timeout;
		this.isShard = isShard;
		this.cacheHosts = cacheHosts;
		this.poolConfig = poolConfig;

		if (isShard) {
			List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
			for (CacheHost host : cacheHosts) {
				shards.add(new JedisShardInfo(host.getIp(), host.getPort(), timeout));
			}
			shardedPool = new ShardedJedisPool(poolConfig, shards);
		} else {
			CacheHost host = cacheHosts.get(0);
			pool = new JedisPool(poolConfig, host.getIp(), host.getPort(), timeout);
		}
	}
	
	// ~ -------------------------------------------------------------------------------------------------------- keys

	@Override
	public Long expire(String key, int ttl) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().expire(key, ttl);
			return null;
		} else {
			return isShard ? expireN(key, ttl) : expire1(key, ttl);
		}
	}
	
	private Long expireN(String key, int ttl) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.expire(key, ttl);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long expire1(String key, int ttl) {
		Jedis j = pool.getResource();
		try {
			return j.expire(key, ttl);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Long del(String... keys) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			if (isShard && keys.length > 1) {
				throw new UnsupportedOperationException();
			}
			rt.getDelegate().del(keys[0]);
			return null;
		} else {
			return isShard ? delN(keys) : del1(keys);
		}
	}

	private Long delN(String... keys) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			long c = 0;
			for (String key : keys) {
				c += sj.del(key);
			}
			return c;
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}

	private Long del1(String... keys) {
		Jedis j = pool.getResource();
		try {
			return j.del(keys);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public Long ttl(String key) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().ttl(key);
			return null;
		} else {
			return isShard ? ttlN(key) : ttl1(key);
		}
	}
	
	private Long ttlN(String key) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.ttl(key);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long ttl1(String key) {
		Jedis j = pool.getResource();
		try {
			return j.ttl(key);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public Long persist(String key) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().persist(key);
			return null;
		} else {
			return isShard ? persistN(key) : persist1(key);
		}
	}
	
	private Long persistN(String key) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			// TODO ShardedJedis should provide this API directly.
			return sj.getShard(key).persist(key);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long persist1(String key) {
		Jedis j = pool.getResource();
		try {
			return j.persist(key);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public Boolean exists(String key) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().exists(key);
			return null;
		} else {
			return isShard ? existsN(key) : exists1(key);
		}
	}
	
	private Boolean existsN(String key) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.exists(key);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Boolean exists1(String key) {
		Jedis j = pool.getResource();
		try {
			return j.exists(key);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	// ------------------------------------------------------------------------------------------------------ long
	
	@Override
	public Long decr(String key) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().decr(key);
			return null;
		} else {
			return isShard ? decrN(key) : decr1(key);
		}
	}
	
	private Long decrN(String key) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.decr(key);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long decr1(String key) {
		Jedis j = pool.getResource();
		try {
			return j.decr(key);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public Long decrBy(String key, long num) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().decrBy(key, num);
			return null;
		} else {
			return isShard ? decrByN(key, num) : decrBy1(key, num);
		}
	}
	
	private Long decrByN(String key, long num) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.decrBy(key, num);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long decrBy1(String key, long num) {
		Jedis j = pool.getResource();
		try {
			return j.decrBy(key, num);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public Long incrBy(String key, long num) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().incrBy(key, num);
			return null;
		} else {
			return isShard ? incrByN(key, num) : incrBy1(key, num);
		}
	}
	
	private Long incrByN(String key, Long num) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.incrBy(key, num);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long incrBy1(String key, Long num) {
		Jedis j = pool.getResource();
		try {
			return j.incrBy(key, num);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public Long incr(String key) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().incr(key);
			return null;
		} else {
			return isShard ? incrN(key) : incr1(key);
		}
	}
	
	private Long incrN(String key) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.incr(key);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long incr1(String key) {
		Jedis j = pool.getResource();
		try {
			return j.incr(key);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	// ------------------------------------------------------------------------------------------------------ string

	@Override
	public String getset(String key, String value) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().getSet(key, value);
			return null;
		} else {
			return isShard ? getsetN(key, value) : getset1(key, value);
		}
	}
	
	private String getsetN(String key, String value) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.getSet(key, value);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private String getset1(String key, String value) {
		Jedis j = pool.getResource();
		try {
			return j.getSet(key, value);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public String get(String key) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().get(key);
			return null;
		} else {
			return isShard ? getN(key) : get1(key);
		}
	}
	
	private String getN(String key) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.get(key);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private String get1(String key) {
		Jedis j = pool.getResource();
		try {
			return j.get(key);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public void set(String key, String value) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().set(key, value);
		} else {
			if (isShard) {
				setN(key, value);
			} else {
				set1(key, value);
			}
		}
	}
	
	private void setN(String key, String value) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			sj.set(key, value);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private void set1(String key, String value) {
		Jedis j = pool.getResource();
		try {
			j.set(key, value);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public void setex(String key, int ttl, String value) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().setex(key, ttl, value);
		} else {
			if (isShard) {
				setexN(key, ttl, value);
			} else {
				setex1(key, ttl, value);
			}
		}
	}
	
	private void setexN(String key, int ttl, String value) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			sj.setex(key, ttl, value);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private void setex1(String key, int ttl, String value) {
		Jedis j = pool.getResource();
		try {
			j.setex(key, ttl, value);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public Long setnx(String key, String value) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().setnx(key, value);
			return null;
		} else {
			return isShard ? setnxN(key, value) : setnx1(key, value);
		}
	}
	
	private Long setnxN(String key, String value) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.setnx(key, value);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long setnx1(String key, String value) {
		Jedis j = pool.getResource();
		try {
			return j.setnx(key, value);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public List<String> mget(String... keys) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			if (isShard) {
				throw new UnsupportedOperationException();
			}
			rt.getDelegate().mget(keys);
			return null;
		} else {
			return isShard ? returnList(mgetN(keys)) : returnList(mget1(keys));
		}
	}
	
	private List<String> mgetN(String... keys) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			List<String> r = new ArrayList<String>();
			for (String key : keys) {
				r.add(sj.get(key));
			}
			return r;
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private List<String> mget1(String... keys) {
		Jedis j = pool.getResource();
		try {
			return j.mget(keys);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public List<String> mset(String... keysvalues) {
		if (keysvalues.length % 2 != 0) {
			throw new IllegalArgumentException("key value must be pair.");
		}
		
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			if (isShard) {
				throw new UnsupportedOperationException();
			}
			rt.getDelegate().mset(keysvalues);
			return null;
		} else {
			return isShard ? returnList(msetN(keysvalues)) : returnList(mset1(keysvalues));
		}
	}
	
	private List<String> msetN(String... keysvalues) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			List<String> r = new ArrayList<String>();
			for (int i = 0; i < keysvalues.length; i+=2) {
				try {
					String key = keysvalues[i];
					String value = keysvalues[i + 1];
					sj.set(key, value);
					r.add(key);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
			return r;
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private List<String> mset1(String... keysvalues) {
		Jedis j = pool.getResource();
		try {
			List<String> r = new ArrayList<String>();
			j.mset(keysvalues);
			for (int i = 0; i < keysvalues.length; i+=2) {
				r.add(keysvalues[i]);
			}
			return r;
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Long msetnx(String... keysvalues) {
		if (isShard) { 
			throw new UnsupportedOperationException(); 
		}
		if (keysvalues.length % 2 != 0) {
			throw new IllegalArgumentException("key value must be pair.");
		}
		
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().msetnx(keysvalues);
			return null;
		} else {
			return msetnx1(keysvalues);
		}
	}
	
	private Long msetnx1(String... keysvalues) {
		Jedis j = pool.getResource();
		try {
			return j.msetnx(keysvalues);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	// ------------------------------------------------------------------------------------------------------ hash

	@Override
	public String hget(String key, String field) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().hget(key, field);
			return null;
		} else {
			return isShard ? hgetN(key, field) : hget1(key, field);
		}
	}
	
	private String hgetN(String key, String field) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.hget(key, field);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private String hget1(String key, String field) {
		Jedis j = pool.getResource();
		try {
			return j.hget(key, field);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Long hset(String key, String field, String value) {	
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().hset(key, field, value);
			return null;
		} else {
			return isShard ? hsetN(key, field, value) : hset1(key, field, value);
		}
	}
	
	private Long hsetN(String key, String field, String value) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.hset(key, field, value);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long hset1(String key, String field, String value) {
		Jedis j = pool.getResource();
		try {
			return j.hset(key, field, value);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Long hdel(String key, String... fields) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			// TODO jedis api has bugs, fix later
			throw new UnsupportedOperationException();
		} else {
			return isShard ? hdelN(key, fields) : hdel1(key, fields);
		}
	}
	
	private Long hdelN(String key, String... fields) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.hdel(key, fields);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long hdel1(String key, String... fields) {
		Jedis j = pool.getResource();
		try {
			return j.hdel(key, fields);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public List<String> hmget(String key, String... fields) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().hmget(key, fields);
			return null;
		} else {
			return isShard ? returnList(hmgetN(key, fields)) : returnList(hmget1(key, fields));
		}
	}
	
	private List<String> hmgetN(String key, String... fields) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			List<String> list = sj.hmget(key, fields);
			if (list == null) {
				list = Collections.emptyList();
			}
			return list;
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private List<String> hmget1(String key, String... fields) {
		Jedis j = pool.getResource();
		try {
			List<String> list = j.hmget(key, fields);
			if (list == null) {
				list = Collections.emptyList();
			}
			return list;
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public void hmset(String key, Map<String, String> hash) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().hmset(key, hash);
		} else {
			if (isShard) {
				hmsetN(key, hash);
			} else {
				hmset1(key, hash);
			}
		}
	}
	
	private void hmsetN(String key, Map<String, String> hash) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			sj.hmset(key, hash);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private void hmset1(String key, Map<String, String> hash) {
		Jedis j = pool.getResource();
		try {
			j.hmset(key, hash);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public Long hincrBy(String key, String field, long value) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().hincrBy(key, field, value);
			return null;
		} else {
			return isShard ? hincrByN(key, field, value) : hincrBy1(key, field, value);
		}
	}
	
	private Long hincrByN(String key, String field, long value) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.hincrBy(key, field, value);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long hincrBy1(String key, String field, long value) {
		Jedis j = pool.getResource();
		try {
			return j.hincrBy(key, field, value);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public Long hsetnx(String key, String field, String value) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().hsetnx(key, field, value);
			return null;
		} else {
			return isShard ? hsetnxN(key, field, value) : hsetnx1(key, field, value);
		}
	}
	
	private Long hsetnxN(String key, String field, String value) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.hsetnx(key, field, value);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long hsetnx1(String key, String field, String value) {
		Jedis j = pool.getResource();
		try {
			return j.hsetnx(key, field, value);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Boolean hexists(String key, String field) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().hexists(key, field);
			return null;
		} else {
			return isShard ? hexistsN(key, field) : hexists1(key, field);
		}
	}
	
	private Boolean hexistsN(String key, String field) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.hexists(key, field);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Boolean hexists1(String key, String field) {
		Jedis j = pool.getResource();
		try {
			return j.hexists(key, field);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().hgetAll(key);
			return null;
		} else {
			return isShard ? returnMap(hgetAllN(key)) : returnMap(hgetAll1(key));
		}
	}
	
	private Map<String, String> hgetAllN(String key) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.hgetAll(key);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Map<String, String> hgetAll1(String key) {
		Jedis j = pool.getResource();
		try {
			return j.hgetAll(key);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Set<String> hkeys(String key) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().hgetAll(key);
			return null;
		} else {
			return isShard ? hkeysN(key) : hkeys1(key);
		}
	}
	
	private Set<String> hkeysN(String key) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			Set<String> set = sj.hkeys(key);
			if (set == null) {
				set = Collections.emptySet();
			}
			return set;
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Set<String> hkeys1(String key) {
		Jedis j = pool.getResource();
		try {
			Set<String> set = j.hkeys(key);
			if (set == null) {
				set = Collections.emptySet();
			}
			return set;
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Long hlen(String key) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().hlen(key);
			return null;
		} else {
			return isShard ? hlenN(key) : hlen1(key);
		}
	}
	
	private Long hlenN(String key) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.hlen(key);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long hlen1(String key) {
		Jedis j = pool.getResource();
		try {
			return j.hlen(key);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public List<String> hvals(String key) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().hvals(key);
			return null;
		} else {
			return isShard ? returnList(hvalsN(key)) : returnList(hvals1(key));
		}
	}
	
	private List<String> hvalsN(String key) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.hvals(key);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private List<String> hvals1(String key) {
		Jedis j = pool.getResource();
		try {
			return j.hvals(key);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	// ------------------------------------------------------------------------------------------------------ set

	@Override
	public Long sadd(String key, String... values) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			// TODO jedis api bugs
			throw new UnsupportedOperationException();
		} else {
			return isShard ? saddN(key, values) : sadd1(key, values);
		}
	}
	
	private Long saddN(String key, String... values) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.sadd(key, values);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long sadd1(String key, String... values) {
		Jedis j = pool.getResource();
		try {
			return j.sadd(key, values);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Long scard(String key) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().scard(key);
			return null;
		} else {
			return isShard ? scardN(key) : scard1(key);
		}
	}
	
	private Long scardN(String key) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.scard(key);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long scard1(String key) {
		Jedis j = pool.getResource();
		try {
			return j.scard(key);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Long srem(String key, String... values) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			// TODO
			throw new UnsupportedOperationException();
		} else {
			return isShard ? sremN(key, values) : srem1(key, values);
		}
	}
	
	private Long sremN(String key, String... values) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.srem(key, values);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long srem1(String key, String... values) {
		Jedis j = pool.getResource();
		try {
			return j.srem(key, values);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Set<String> smembers(String key) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().smembers(key);
			return null;
		} else {
			return isShard ? returnSet(smembersN(key)) : returnSet(smembers1(key));
		}
	}
	
	private Set<String> smembersN(String key) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.smembers(key);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Set<String> smembers1(String key) {
		Jedis j = pool.getResource();
		try {
			return j.smembers(key);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Set<String> sinter(String... keys) {
		return isShard ? returnSet(sinterN(keys)) : returnSet(sinter1(keys));
	}
	
	private Set<String> sinterN(String... keys) {
		throw new UnsupportedOperationException();
	}
	
	private Set<String> sinter1(String... keys) {
		Jedis j = pool.getResource();
		try {
			AbstractTransaction rt = getTransaction();
			if (rt != null) {
				rt.getDelegate().sinter(keys);
				return null;
			} else {
				return j.sinter(keys);
			}
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public Boolean sismember(String key, String member) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().sismember(key, member);
			return null;
		} else {
			return isShard ? sismemberN(key, member) : sismember1(key, member);
		}
	}
	
	private Boolean sismemberN(String key, String member) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.sismember(key, member);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Boolean sismember1(String key, String member) {
		Jedis j = pool.getResource();
		try {
			return j.sismember(key, member);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public String spop(String key) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().spop(key);
			return null;
		} else {
			return isShard ? spopN(key) : spop1(key);
		}
	}
	
	private String spopN(String key) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.spop(key);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private String spop1(String key) {
		Jedis j = pool.getResource();
		try {
			return j.spop(key);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	// ----------------------------------------------------------------------------------------------------- sorted set

	@Override
	public Long zadd(String key, double score, String member) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().zadd(key, score, member);
			return null;
		} else {
			return isShard ? zaddN(key, score, member) : zadd1(key, score, member);
		}
	}
	
	private Long zaddN(String key, double score, String member) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.zadd(key, score, member);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long zadd1(String key, double score, String member) {
		Jedis j = pool.getResource();
		try {
			return j.zadd(key, score, member);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public Long zcard(String key) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().zcard(key);
			return null;
		} else {
			return isShard ? zcardN(key) : zcard1(key);
		}
	}
	
	private Long zcardN(String key) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.zcard(key);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long zcard1(String key) {
		Jedis j = pool.getResource();
		try {
			return j.zcard(key);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Long zadd(String key, Map<Double, String> scoreMembers) {
		if (scoreMembers == null) { 
			return 0L; 
		}
		
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			// TODO jedis api has bugs, fix later
			throw new UnsupportedOperationException();
		} else {
			return isShard ? zaddN(key, scoreMembers) : zadd1(key, scoreMembers);
		}
	}
	
	private Long zaddN(String key, Map<Double, String> scoreMembers) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.zadd(key, scoreMembers);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long zadd1(String key, Map<Double, String> scoreMembers) {
		Jedis j = pool.getResource();
		try {
			return j.zadd(key, scoreMembers);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Set<String> zrange(String key, long start, long end) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			// TODO jedis api bugs, have to convert to int.
			rt.getDelegate().zrange(key, (int) start, (int) end);
			return null;
		} else {
			return isShard ? returnSet(zrangeN(key, start, end)) : returnSet(zrange1(key, start, end));
		}
	}
	
	private Set<String> zrangeN(String key, long start, long end) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			Set<String> set = sj.zrange(key, start, end);
			if (set == null) {
				set = Collections.emptySet();
			}
			return set;
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Set<String> zrange1(String key, long start, long end) {
		Jedis j = pool.getResource();
		try {
			Set<String> set = j.zrange(key, start, end);
			if (set == null) {
				set = Collections.emptySet();
			}
			return set;
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Map<String, Double> zrangeWithScores(String key, long start, long end) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			// TODO jedis api bugs, have to convert to int.
			rt.getDelegate().zrangeWithScores(key, (int) start, (int) end);
			return null;
		} else {
			return isShard ? returnMap(zrangeWithScoresN(key, start, end)) : returnMap(zrangeWithScores1(key, start, end));
		}
	}
	
	private Map<String, Double> zrangeWithScoresN(String key, long start, long end) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			Set<Tuple> set = sj.zrangeWithScores(key, start, end);
			Map<String, Double> map = new HashMap<String, Double>();
			if (set != null) {
				for (Tuple tuple : set) {
					map.put(tuple.getElement(), tuple.getScore());
				}
			}
			return map;
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Map<String, Double> zrangeWithScores1(String key, long start, long end) {
		Jedis j = pool.getResource();
		try {
			Set<Tuple> set = j.zrangeWithScores(key, start, end);
			Map<String, Double> map = new HashMap<String, Double>();
			if (set != null) {
				for (Tuple tuple : set) {
					map.put(tuple.getElement(), tuple.getScore());
				}
			}
			return map;
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Long zcount(String key, double min, double max) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().zcount(key, min, max);
			return null;
		} else {
			return isShard ? zcountN(key, min, max) : zcount1(key, min, max);
		}
	}
	
	private Long zcountN(String key, double min, double max) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.zcount(key, min, max);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long zcount1(String key, double min, double max) {
		Jedis j = pool.getResource();
		try {
			return j.zcount(key, min, max);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Long zcount(String key, String min, String max) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			// TODO jedis api bug
			throw new UnsupportedOperationException();
		} else {
			return isShard ? zcountN(key, min, max) : zcount1(key, min, max);
		}
	}
	
	private Long zcountN(String key, String min, String max) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.zcount(key, min, max);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long zcount1(String key, String min, String max) {
		Jedis j = pool.getResource();
		try {
			return j.zcount(key, min, max);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Double zscore(String key, String member) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().zscore(key, member);
			return null;
		} else {
			return isShard ? zscoreN(key, member) : zscore1(key, member);
		}
	}
	
	private Double zscoreN(String key, String member) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.zscore(key, member);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Double zscore1(String key, String member) {
		Jedis j = pool.getResource();
		try {
			return j.zscore(key, member);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Long zrank(String key, String member) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().zrank(key, member);
			return null;
		} else {
			return isShard ? zrankN(key, member) : zrank1(key, member);
		}
	}
	
	private Long zrankN(String key, String member) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.zrank(key, member);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long zrank1(String key, String member) {
		Jedis j = pool.getResource();
		try {
			return j.zrank(key, member);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Long zrem(String key, String... members) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			// TODO jedis api has bugs, have to in a loop to zrem
			for (String member : members) {
				rt.getDelegate().zrem(key, member);
			}
			return null;
		} else {
			return isShard ? zremN(key, members) : zrem1(key, members);
		}
	}
	
	private Long zremN(String key, String... members) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.zrem(key, members);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long zrem1(String key, String... members) {
		Jedis j = pool.getResource();
		try {
			return j.zrem(key, members);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public Set<String> zrangeByScore(String key, double min, double max) {
		return zrangeByScore(key, Double.toString(min), Double.toString(max));
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
		return zrangeByScore(key, Double.toString(min), Double.toString(max), offset, count);
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().zrangeByScore(key, min, max);
			return null;
		} else {
			return isShard ? returnSet(zrangeByScoreN(key, min, max)) : returnSet(zrangeByScore1(key, min, max));
		}
	}
	
	private Set<String> zrangeByScoreN(String key, String min, String max) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.zrangeByScore(key, min, max);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Set<String> zrangeByScore1(String key, String min, String max) {
		Jedis j = pool.getResource();
		try {
			return j.zrangeByScore(key, min, max);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			// TODO jedis api bug
			throw new UnsupportedOperationException();
		} else {
			return isShard ? returnSet(zrangeByScoreN(key, min, max, offset, count)) : returnSet(zrangeByScore1(key, min, max, offset, count));
		}
	}
	
	private Set<String> zrangeByScoreN(String key, String min, String max, int offset, int count) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.zrangeByScore(key, min, max, offset, count);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Set<String> zrangeByScore1(String key, String min, String max, int offset, int count) {
		Jedis j = pool.getResource();
		try {
			return j.zrangeByScore(key, min, max, offset, count);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public Map<String, Double> zrangeByScoreWithScores(String key, double min, double max) {
		return zrangeByScoreWithScores(key, Double.toString(min), Double.toString(max));
	}

	@Override
	public Map<String, Double> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
		return zrangeByScoreWithScores(key, Double.toString(min), Double.toString(max), offset, count);
	}
	
	@Override
	public Map<String, Double> zrangeByScoreWithScores(String key, String min, String max) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			// TODO jedis api bug
			throw new UnsupportedOperationException();
		} else {
			return isShard ? returnMap(zrangeByScoreWithScoresN(key, min, max)) : returnMap(zrangeByScoreWithScores1(key, min, max));
		}
	}
	
	private Map<String, Double> zrangeByScoreWithScoresN(String key, String min, String max) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			Set<Tuple> set = sj.zrangeByScoreWithScores(key, min, max);
			Map<String, Double> map = new HashMap<String, Double>();
			if (set != null) {
				for (Tuple tuple : set) {
					map.put(tuple.getElement(), tuple.getScore());
				}
			}
			return map;
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Map<String, Double> zrangeByScoreWithScores1(String key, String min, String max) {
		Jedis j = pool.getResource();
		try {
			Set<Tuple> set = j.zrangeByScoreWithScores(key, min, max);
			Map<String, Double> map = new HashMap<String, Double>();
			if (set != null) {
				for (Tuple tuple : set) {
					map.put(tuple.getElement(), tuple.getScore());
				}
			}
			return map;
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Map<String, Double> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			// TODO jedis api bug
			throw new UnsupportedOperationException();
		} else {
			return isShard ? returnMap(zrangeByScoreWithScoresN(key, min, max, offset, count)) : returnMap(zrangeByScoreWithScores1(key, min, max, offset, count));
		}
	}
	
	private Map<String, Double> zrangeByScoreWithScoresN(String key, String min, String max, int offset, int count) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			Set<Tuple> set = sj.zrangeByScoreWithScores(key, min, max, offset, count);
			Map<String, Double> map = new HashMap<String, Double>();
			if (set != null) {
				for (Tuple tuple : set) {
					map.put(tuple.getElement(), tuple.getScore());
				}
			}
			return map;
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Map<String, Double> zrangeByScoreWithScores1(String key, String min, String max, int offset, int count) {
		Jedis j = pool.getResource();
		try {
			Set<Tuple> set = j.zrangeByScoreWithScores(key, min, max, offset, count);
			Map<String, Double> map = new HashMap<String, Double>();
			if (set != null) {
				for (Tuple tuple : set) {
					map.put(tuple.getElement(), tuple.getScore());
				}
			}
			return map;
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Long zremrangeByScore(String key, double start, double end) {
		return zremrangeByScore(key, Double.toString(start), Double.toString(end));
	}

	@Override
	public Long zremrangeByScore(String key, String start, String end) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			// TODO jedis api bugs
			throw new UnsupportedOperationException();
		} else {
			return isShard ? zremrangeByScoreN(key, start, end) : zremrangeByScore1(key, start, end);
		}
	}
	
	private Long zremrangeByScoreN(String key, String start, String end) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.zremrangeByScore(key, start, end);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long zremrangeByScore1(String key, String start, String end) {
		Jedis j = pool.getResource();
		try {
			return j.zremrangeByScore(key, start, end);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	// ------------------------------------------------------------------------------------------------------ list

	@Override
	public Long lpush(String key, String... values) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			// TODO jedis api has bugs, fix later
			throw new UnsupportedOperationException();
		} else {
			return isShard ? lpushN(key, values) : lpush1(key, values);
		}
	}
	
	private Long lpushN(String key, String... values) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.lpush(key, values);
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long lpush1(String key, String... values) {
		Jedis j = pool.getResource();
		try {
			return j.lpush(key, values);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public Long rpush(String key, String... values) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			// TODO jedis api has bugs, fix later
			throw new UnsupportedOperationException();
		} else {
			return isShard ? rpushN(key, values) : rpush1(key, values);
		}
	}
	
	private Long rpushN(String key, String... values) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.rpush(key, values);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long rpush1(String key, String... values) {
		Jedis j = pool.getResource();
		try {
			return j.rpush(key, values);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public String lpop(String key) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().lpop(key);
			return null;
		} else {
			return isShard ? lpopN(key) : lpop1(key);
		}
	}
	
	private String lpopN(String key) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.lpop(key);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private String lpop1(String key) {
		Jedis j = pool.getResource();
		try {
			return j.lpop(key);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public String rpop(String key) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().rpop(key);
			return null;
		} else {
			return isShard ? rpopN(key) : rpop1(key);
		}
	}
	
	private String rpopN(String key) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.rpop(key);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private String rpop1(String key) {
		Jedis j = pool.getResource();
		try {
			return j.rpop(key);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public List<String> lrange(String key, long start, long end) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().lrange(key, start, end);
			return null;
		} else {
			return isShard ? returnList(lrangeN(key, start, end)) : returnList(lrange1(key, start, end));
		}
	}
	
	private List<String> lrangeN(String key, long start, long end) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			List<String> l = sj.lrange(key, start, end);
			if (l == null) {
				return Collections.emptyList();
			}
			return l;
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private List<String> lrange1(String key, long start, long end) {
		Jedis j = pool.getResource();
		try {
			return j.lrange(key, start, end);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public void ltrim(String key, long start, long end) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().ltrim(key, start, end);
		} else {
			if (isShard) {
				ltrimN(key, start, end); 
			} else {
				ltrim1(key, start, end);
			}
		}
	}
	
	private void ltrimN(String key, long start, long end) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			sj.ltrim(key, start, end);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private void ltrim1(String key, long start, long end) {
		Jedis j = pool.getResource();
		try {
			j.ltrim(key, start, end);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public Long llen(String key) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().llen(key);
			return null;
		} else {
			return isShard ? llenN(key) : llen1(key);
		}
	}
	
	private Long llenN(String key) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.llen(key);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long llen1(String key) {
		Jedis j = pool.getResource();
		try {
			return j.llen(key);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	@Override
	public Long lrem(String key, long count, String value) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().lrem(key, count, value);
			return null;
		} else {
			return isShard ? lremN(key, count, value) : lrem1(key, count, value);
		}
	}
	
	private Long lremN(String key, long count, String value) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.lrem(key, count, value);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private Long lrem1(String key, long count, String value) {
		Jedis j = pool.getResource();
		try {
			return j.lrem(key, count, value);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}

	@Override
	public String lindex(String key, long index) {
		AbstractTransaction rt = getTransaction();
		if (rt != null) {
			rt.getDelegate().llen(key);
			return null;
		} else {
			return isShard ? lindexN(key, index) : lindex1(key, index);
		}
	}
	
	private String lindexN(String key, long index) {
		ShardedJedis sj = shardedPool.getResource();
		try {
			return sj.lindex(key, index);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			shardedPool.returnResource(sj);
		}
	}
	
	private String lindex1(String key, long index) {
		Jedis j = pool.getResource();
		try {
			return j.lindex(key, index);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			pool.returnResource(j);
		}
	}
	
	// ---------------------------------------------------------------------------------------------------- transaction 

	@Override
	public Transaction beginTransaction() {
		if (isShard) {
			throw new UnsupportedOperationException("Use beginTransaction(key)");
		}
		
		Jedis j = THREAD_LOCAL_JEDIS.get();
		if (j == null) {
			j = pool.getResource();
		}
		AbstractTransaction rt = null;
		try {
			redis.clients.jedis.Transaction jt = j.multi();
			rt = new RedisTransaction(this, jt, j);
			THREAD_LOCAL_TRANSACTION.set(rt);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		}
		
		return rt;
	}

	@Override
	public Transaction beginTransaction(String key) {
		if (!isShard) {
			throw new UnsupportedOperationException("Use beginTransaction()");
		}
		
		ShardedJedis sj = THREAD_LOCAL_SHARDED_JEDIS.get();
		if (sj == null) {
			sj = shardedPool.getResource();
		}
		AbstractTransaction rt = null;
		try {
			Jedis j = sj.getShard(key); 
			redis.clients.jedis.Transaction jt = j.multi();
			rt = new ShardedRedisTransaction(this, jt, sj);
			THREAD_LOCAL_TRANSACTION.set(rt);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		}
		
		return rt;
	}
	
	@Override
	public void watch(String... keys) {
		if (isShard) {
			watchN(keys);
		} else {
			watch1(keys);
		}
	}
	
	private void watchN(String... keys) {
		if (keys.length > 1) {
			throw new UnsupportedOperationException("Just single key supported in sharded config.");
		}
		
		ShardedJedis sj = shardedPool.getResource();
		try {
			Jedis j = sj.getShard(keys[0]);
			j.watch(keys);
			THREAD_LOCAL_SHARDED_JEDIS.set(sj);
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		}
	}
	
	private void watch1(String... keys) {
		Jedis j = pool.getResource();
		try {
			j.watch(keys);
			THREAD_LOCAL_JEDIS.set(j);
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		}
	}

	@Override
	public void unwatch(String key) {
		if (isShard) {
			unwatchN(key);
		} else {
			unwatch1();
		}
	}
	
	private void unwatchN(String key) {
		ShardedJedis sj = THREAD_LOCAL_SHARDED_JEDIS.get();
		try {
			if (sj != null) {
				Jedis j = sj.getShard(key);
				j.unwatch();
			}
		} catch (RuntimeException e) {
			shardedPool.returnBrokenResource(sj);
			throw e;
		} finally {
			if (THREAD_LOCAL_SHARDED_JEDIS.get() != null) {
				THREAD_LOCAL_SHARDED_JEDIS.remove();
				shardedPool.returnResource(sj);
			}
		}
	}
	
	private void unwatch1() {
		Jedis j = THREAD_LOCAL_JEDIS.get();
		try {
			if (j != null) {
				j.unwatch();
			}
		} catch (RuntimeException e) {
			pool.returnBrokenResource(j);
			throw e;
		} finally {
			if (THREAD_LOCAL_JEDIS.get() != null) {
				THREAD_LOCAL_JEDIS.remove();
				pool.returnResource(j);
			}
		}
	}

	void cleanTransaction(Jedis j, boolean broken) {
		THREAD_LOCAL_TRANSACTION.remove();
		THREAD_LOCAL_JEDIS.remove();
		if (broken) {
			pool.returnBrokenResource(j);
		} else {
			pool.returnResource(j);
		}
	}
	
	void cleanTransaction(ShardedJedis sj, boolean broken) {
		THREAD_LOCAL_TRANSACTION.remove();
		THREAD_LOCAL_SHARDED_JEDIS.remove();
		if (broken) {
			shardedPool.returnBrokenResource(sj);
		} else {
			shardedPool.returnResource(sj);
		}
	}

	private AbstractTransaction getTransaction() {
		return THREAD_LOCAL_TRANSACTION.get();
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	private List<String> returnList(List<String> list) {
		if (list == null) { 
			return Collections.emptyList(); 
		}
		
		return list;
	}
	
	private Set<String> returnSet(Set<String> set) {
		if (set == null) { 
			return Collections.emptySet();
		}
		
		return set;
	}
	
	private <K, V> Map<K, V> returnMap(Map<K, V> map) {
		if (map == null) {
			return Collections.emptyMap();
		}
		
		return map;
	}
	
	// ~ ------------------------------------------------------------------------------------------------------ getter 

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isShard() {
		return isShard;
	}

	public void setShard(boolean isShard) {
		this.isShard = isShard;
	}

	public List<CacheHost> getCacheHosts() {
		return cacheHosts;
	}

	public void setCacheHosts(List<CacheHost> cacheHosts) {
		this.cacheHosts = cacheHosts;
	}

	public JedisPoolConfig getPoolConfig() {
		return poolConfig;
	}

	public void setPoolConfig(JedisPoolConfig poolConfig) {
		this.poolConfig = poolConfig;
	}

}

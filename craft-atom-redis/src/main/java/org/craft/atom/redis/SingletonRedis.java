package org.craft.atom.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.craft.atom.redis.api.RedisConnectionException;
import org.craft.atom.redis.api.RedisDataException;
import org.craft.atom.redis.api.RedisException;
import org.craft.atom.redis.api.SingletonRedisCommand;
import org.craft.atom.redis.api.Slowlog;
import org.craft.atom.redis.api.handler.RedisMonitorHandler;
import org.craft.atom.redis.api.handler.RedisPsubscribeHandler;
import org.craft.atom.redis.api.handler.RedisSubscribeHandler;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

/**
 * @author mindwind
 * @version 1.0, Jun 15, 2013
 */
public class SingletonRedis extends AbstractRedis implements SingletonRedisCommand {
	
	private static final ThreadLocal<Transaction> THREAD_LOCAL_TRANSACTION = new ThreadLocal<Transaction>();
	private static final ThreadLocal<Jedis> THREAD_LOCAL_JEDIS = new ThreadLocal<Jedis>();
	
	private String host;
	private String password;
	private int port = 6379;
	private int timeoutInMillis = 2000;
	private int database = 0;
	private Config poolConfig = poolConfig(100);
	private JedisPool pool;
	
	// ~ ---------------------------------------------------------------------------------------------------------
	
	public SingletonRedis(String host, int port) {
		this.host = host;
		this.port = port;
		init();
	}
	
	public SingletonRedis(String host, int port, int timeoutInMillis) {
		this.host = host;
		this.port = port;
		this.timeoutInMillis = timeoutInMillis;
		init();
	}
	
	public SingletonRedis(String host, int port, int timeoutInMillis, int poolSize) {
		this.host = host;
		this.port = port;
		this.timeoutInMillis = timeoutInMillis;
		this.poolConfig = poolConfig(poolSize);
		init();
	}
	
	public SingletonRedis(String host, int port, int timeoutInMillis, Config poolConfig) {
		this.host = host;
		this.port = port;
		this.timeoutInMillis = timeoutInMillis;
		this.poolConfig = poolConfig;
		init();
	}
	
	public SingletonRedis(String host, int port, int timeoutInMillis, int poolSize, String password) {
		this.host = host;
		this.port = port;
		this.timeoutInMillis = timeoutInMillis;
		this.poolConfig = poolConfig(poolSize);
		this.password = password;
		init();
	}
	
	public SingletonRedis(String host, int port, int timeoutInMillis, Config poolConfig, String password) {
		this.host = host;
		this.port = port;
		this.timeoutInMillis = timeoutInMillis;
		this.poolConfig = poolConfig;
		this.password = password;
		init();
	}
	
	public SingletonRedis(String host, int port, int timeoutInMillis, int poolSize, String password, int database) {
		this.host = host;
		this.port = port;
		this.timeoutInMillis = timeoutInMillis;
		this.poolConfig = poolConfig(poolSize);
		this.password = password;
		this.database = database;
		init();
	}

	public SingletonRedis(String host, int port, int timeoutInMillis, Config poolConfig, String password, int database) {
		this.host = host;
		this.port = port;
		this.timeoutInMillis = timeoutInMillis;
		this.poolConfig = poolConfig;
		this.password = password;
		this.database = database;
		init();
	}
	
	private Config poolConfig(int poolSize) {
		JedisPoolConfig jpc = new JedisPoolConfig();
		jpc.setMaxActive(poolSize);
		jpc.setMaxIdle(poolSize);
		jpc.setMinIdle(0);
		return jpc;
	}
	
	private void init() {
		pool = new JedisPool(poolConfig, host, port, timeoutInMillis, password, database);
	}
	
	// ~ --------------------------------------------------------------------------------------------------------- Keys
	

	@Override
	public long del(String key) {
		return (Long) executeCommand(CommandName.DEL, key);
	}
	
	private long del0(Jedis j, Transaction t, String key) {
		long r = 0;
		if (t != null) {
			t.del(key);
		} else {
			r = j.del(key);
		}
		return r;
	}

	@Override
	public String dump(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long expire(String key, int seconds) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long expireat(String key, long timestamp) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<String> keys(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String migrate(String host, int port, String key, int destinationdb,
			int timeout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long move(String key, int db) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long objectrefcount(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String objectencoding(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long objectidletime(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long persist(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long pexpire(String key, int milliseconds) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long pexpireat(String key, long millisecondstimestamp) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long pttl(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String rename(String key, String newkey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long renamenx(String key, String newkey) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String restore(String key, long ttl, String serializedvalue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key, boolean desc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key, boolean alpha, boolean desc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key, int offset, int count, boolean alpha,
			boolean desc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key, String bypattern,
			String... getpatterns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key, String bypattern, boolean desc,
			String... getpatterns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key, String bypattern, boolean alpha,
			boolean desc, String... getpatterns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key, String bypattern, int offset,
			int count, String... getpatterns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key, String bypattern, int offset,
			int count, boolean alpha, boolean desc, String... getpatterns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key, String bypattern, String destination) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key, String bypattern, boolean desc,
			String destination, String... getpatterns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key, String bypattern, boolean alpha,
			boolean desc, String destination, String... getpatterns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key, String bypattern, int offset,
			int count, String destination, String... getpatterns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key, String bypattern, int offset,
			int count, boolean alpha, boolean desc, String destination,
			String... getpatterns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long ttl(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String type(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Strings
	

	@Override
	public long append(String key, String value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long bitcount(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long bitnot(String destkey, String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long decr(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long decrby(String key, long decrement) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String get(String key) {
		return (String) executeCommand(CommandName.GET, key);
	}
	
	private String get0(Jedis j, Transaction t, String key) {
		String r = null;
		if (t != null) {
			t.get(key);
		} else {
			r = j.get(key);
		}
		return r;
	}

	@Override
	public boolean getbit(String key, long offset) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getrange(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getset(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long incr(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long incrby(String key, long increment) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double incrbyfloat(String key, double increment) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String psetex(String key, int milliseconds, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String set(String key, String value) {
		return (String) executeCommand(CommandName.SET, key, value);
	}
	
	private String set0(Jedis j, Transaction t, String key, String value) {
		String r = null;
		if (t != null) {
			t.set(key, value);
		} else {
			r = j.set(key, value);
		}
		return r;
	}

	@Override
	public String setxx(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String setnxex(String key, String value, int seconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String setnxpx(String key, String value, int milliseconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String setxxex(String key, String value, int seconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String setxxpx(String key, String value, int milliseconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setbit(String key, long offset, boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String setex(String key, int seconds, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long setnx(String key, String value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long setrange(String key, long offset, String value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long strlen(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long hdel(String key, String... fields) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hexists(String key, String field) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String hget(String key, String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> hgetall(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long hincrby(String key, String field, long value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double hincrbyfloat(String key, String field, double value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<String> hkeys(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long hlen(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String hmset(String key, Map<String, String> fieldvalues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long hset(String key, String field, String value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long hsetnx(String key, String field, String value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> hvals(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String blpop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String blpop(String key, int timeout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String brpop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String brpop(String key, int timeout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String brpoplpush(String source, String destination, int timeout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String lindex(String key, long index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long linsertbefore(String key, String pivot, String value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long linsertafter(String key, String pivot, String value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long llen(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String lpop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long lpush(String key, String... values) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long lpushx(String key, String value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> lrange(String key, long start, long stop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long lrem(String key, long count, String value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String lset(String key, long index, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String ltrim(String key, long start, long stop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String rpop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String rpoplpush(String source, String destination) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long rpush(String key, String... values) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long rpushx(String key, String value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long sadd(String key, String... members) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long scard(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean sismember(String key, String member) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> smembers(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String spop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> srandmember(String key, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String srandmember(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long srem(String key, String... members) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zadd(String key, double score, String member) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zcard(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zcount(String key, double min, double max) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double zincrby(String key, double score, String member) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<String> zrange(String key, long start, long stop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Double> zrangewithscores(String key, long start,
			long stop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangebyscore(String key, double min, double max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangebyscore(String key, String min, String max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangebyscore(String key, double min, double max,
			int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangebyscore(String key, String min, String max,
			int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String key, double min,
			double max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String key, String min,
			String max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String key, double min,
			double max, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String key, String min,
			String max, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zrank(String key, String member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long zrem(String key, String... members) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zremrangebyrank(String key, long start, long stop) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zremrangebyscore(String key, double min, double max) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zremrangebyscore(String key, String min, String max) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<String> zrevrange(String key, long start, long stop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Double> zrerangewithscores(String key, long start,
			long stop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrerangebyscore(String key, double max, double min) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrerangebyscore(String key, String max, String min) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrerangebyscore(String key, double max, double min,
			int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrerangebyscore(String key, String max, String min,
			int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zrerank(String key, String member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double zscore(String key, String member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long publish(String channel, String message) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String punsubscribe(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void subscribe(RedisSubscribeHandler handler, String channel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String unsubscribe(String channel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(String script) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(String script, List<String> keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(String script, List<String> keys, List<String> args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evalsha(String sha1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evalsha(String sha1, List<String> keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evalsha(String sha1, List<String> keys, List<String> args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean scriptexists(String sha1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String scriptload(String script) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String debugobject(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long del(String... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String randomkey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long bitand(String destkey, String... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long bitor(String destkey, String... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long bitxor(String destkey, String... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> mget(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String mset(String... keysvalues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String msetnx(String... keysvalues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> blpop(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> blpop(int timeout, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> brpop(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> brpop(int timeout, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> sdiff(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long sdiffstore(String destination, String... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<String> sinter(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long sinterstore(String destination, String... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long smove(String source, String destination, String member) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<String> sunion(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> sunionstore(String destination, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long zinterstore(String destination, String... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zinterstoremax(String destination, String... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zinterstoremin(String destination, String... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zinterstore(String destination, Map<String, Integer> weightkeys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zinterstoremax(String destination,
			Map<String, Integer> weightkeys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zinterstoremin(String destination,
			Map<String, Integer> weightkeys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zunionstore(String destination, String... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zunionstoremax(String destination, String... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zunionstoremin(String destination, String... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zunionstore(String destination, Map<String, Integer> weightkeys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zunionstoremax(String destination,
			Map<String, Integer> weightkeys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zunionstoremin(String destination,
			Map<String, Integer> weightkeys) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void psubscribe(RedisPsubscribeHandler handler, String... patterns) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> punsubscribe(String... patterns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void subscribe(RedisSubscribeHandler handler, String... channels) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> unsubscribe(String... channels) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------ Transactions
	
	
	// Transaction command execution sequence.
	// MULTI ... -> ... EXEC
	// MULTI ... -> DISCARD
	// WATCH -> MULTI ... -> ... EXEC
	// WATCH -> UNWATCH -> MULTI ... -> ... EXEC
	// WATCH -> MULTI ... -> UNWATCH -> ... EXEC
	// WATCH ... -> ... MULTI ... -> ... EXEC
	
	@Override
	public String discard() {
		return (String) executeCommand(CommandName.DISCARD, new Object[] {});
	}
	
	private String discard0(Transaction t) {
		try {
			return t.discard();
		} finally {
			unsetTransaction();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> exec() {
		return (List<Object>) executeCommand(CommandName.EXEC, new Object[] {});
	}
	
	private List<Object> exec0(Transaction t) {
		try {
			if (t == null) {
				throw new RedisDataException("ERR EXEC without MULTI");
			}
			return t.exec();
		} finally {
			unsetTransaction();
		}
	}

	@Override
	public String multi() {
		return (String) executeCommand(CommandName.MULTI, new Object[] {});
	}
	
	private String multi0(Jedis j) {
		Transaction t = j.multi();
		setTransaction(t, j);
		return OK;
	}

	@Override
	public String unwatch() {
		return (String) executeCommand(CommandName.UNWATCH, new Object[] {});
	}
	
	private String unwatch0(Jedis j, Transaction t) {
		if (t != null) {
			// TODO
			return "";
		} else {
			try {
				return j.unwatch();
			} finally {
				unsetWatch();
			}
		}
	}

	@Override
	public String watch(String... keys) {
		return (String) executeCommand(CommandName.WATCH, new Object[] { keys });
	}
	
	private String watch0(Jedis j, String... keys) {
		String r = j.watch(keys);
		setWatch(j);
		return r;
	}
	
	private void setTransaction(Transaction t, Jedis j) {
		THREAD_LOCAL_JEDIS.set(j);
		THREAD_LOCAL_TRANSACTION.set(t);
	}
	
	private void unsetTransaction() {
		THREAD_LOCAL_JEDIS.remove();
		THREAD_LOCAL_TRANSACTION.remove();
	}
	
	private void setWatch(Jedis j) {
		THREAD_LOCAL_JEDIS.set(j);
	}
	
	private void unsetWatch() {
		THREAD_LOCAL_JEDIS.remove();
	}
	
	
	// ~ --------------------------------------------------------------------------------------------------- Scripting
	

	@Override
	public boolean[] scriptexists(String... sha1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String scriptflush() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String scriptkill() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String auth(String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String echo(String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String ping() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String quit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String select(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String bgrewriteaof() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String bgsave() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clientgetname() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clientkill(String ip, int port) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> clientlist() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clientsetname(String connectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String configget(String parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String configresetstat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String configset(String parameter, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long dbsize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String debugsegfault() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String flushall() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String flushdb() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String info() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String info(String section) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long lastsave() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void monitor(RedisMonitorHandler handler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String save() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String shutdown(boolean save) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String slaveof(String host, int port) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String slaveofnoone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Slowlog> slowlogget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Slowlog> slowlogget(long no) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String slowlogreset() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long slowloglen() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void sync() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long time() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long microtime() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	private Object executeCommand(CommandName cn, Object... args) {
		Jedis j = jedis();
		Transaction t = transaction();
		
		try {
			switch (cn) {
			// Keys
			case DEL:
				return del0(j, t, (String) args[0]);
			
			// Strings
			case APPEND:
			case BITCOUNT:
			case BITOP:
			case DECR:
			case DECRBY:
			case GET:
				return get0(j, t, (String) args[0]);
			case GETBIT:
			case GETRANGE:
			case GETSET:
			case INCR:
			case INCRBY:
			case INCRBYFLOAT:
			case MGET:
			case MSET:
			case MSETNX:
			case PSETEX:
			case SET:
				return set0(j, t, (String) args[0], (String) args[1]);
			case SETBIT:
			case SETEX:
			case SETNX:
			case SETRANGE:
			case STRLEN:
				
			// Transactions
			case DISCARD:
				return discard0(t);
			case EXEC:
				return exec0(t);
			case MULTI:
				return multi0(j);
			case UNWATCH:
				return unwatch0(j, t);
			case WATCH:
				return watch0(j, (String[]) args[0]);
			default:
				throw new IllegalArgumentException();
			}
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(j);
			throw new RedisConnectionException(e);
		} catch (JedisDataException e) {
			throw new RedisDataException(e);
		} catch (RedisConnectionException e) {
			pool.returnBrokenResource(j);
			throw e;
		} catch (RedisDataException e) {
			throw e;
		} catch (Exception e) {
			throw new RedisException(e);
		} finally {
			returnJedis(cn, j);
		}
	}
	
	private Transaction transaction() {
		return THREAD_LOCAL_TRANSACTION.get();
	}
	
	private Jedis jedis() {
		Jedis j = THREAD_LOCAL_JEDIS.get();	
		
		if (j == null) {
			j = pool.getResource();
		}
		
		return j;
	}
	
	private void returnJedis(CommandName cn, Jedis j) {
		if (CommandName.MULTI == cn || CommandName.WATCH == cn) {
			return;
		} else {
			pool.returnResource(j);
		}
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------

	public String getHost() {
		return host;
	}

	public String getPassword() {
		return password;
	}

	public int getPort() {
		return port;
	}

	public int getTimeoutInMillis() {
		return timeoutInMillis;
	}

	public int getDatabase() {
		return database;
	}

	public Config getPoolConfig() {
		return poolConfig;
	}

	@Override
	public String toString() {
		return String
				.format("SingletonRedis [host=%s, password=%s, port=%s, timeoutInMillis=%s, database=%s, poolConfig=%s]",
						                 host,    password,    port,    timeoutInMillis,    database,    poolConfig);
	}

}

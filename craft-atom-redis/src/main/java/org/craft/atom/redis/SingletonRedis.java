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
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

/**
 * @author mindwind
 * @version 1.0, Jun 15, 2013
 */
@SuppressWarnings("unchecked")
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
		if (t != null) {
			t.del(key); return 0;
		}
		
		return j.del(key);
	}

	@Override
	public String dump(String key) {
		return (String) executeCommand(CommandName.DUMP, new Object[] { key });
	}
	
	private String dump0(Jedis j, Transaction t, String key) {
		if (t != null) {
			// TODO t.dump(key); return null;
		}
		
//		return j.dump(key);
		return null;
	}

	@Override
	public Boolean exists(String key) {
		return (Boolean) executeCommand(CommandName.EXISTS, new Object[] { key });
	}
	
	private Boolean exists0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.exists(key); return null;
		}
		
		return j.exists(key);
	}

	@Override
	public Long expire(String key, int seconds) {
		return (Long) executeCommand(CommandName.EXPIRE, new Object[] { key, seconds });
	}
	
	private Long expire0(Jedis j, Transaction t, String key, int seconds) {
		if (t != null) {
			t.expire(key, seconds); return null;
		}
		
		return j.expire(key, seconds);
	}

	@Override
	public Long expireat(String key, long timestamp) {
		return (Long) executeCommand(CommandName.EXPIREAT, new Object[] { key, timestamp });
	}
	
	private Long expireat0(Jedis j, Transaction t, String key, long timestamp) {
		if (t != null) {
			t.expireAt(key, timestamp); return null;
		}
		
		return j.expireAt(key, timestamp);
	}

	@Override
	public Set<String> keys(String pattern) {
		return (Set<String>) executeCommand(CommandName.KEYS, new Object[] { pattern });
	}
	
	private Set<String> keys0(Jedis j, Transaction t, String pattern) {
		if (t != null) {
			t.keys(pattern); return null;
		}
		
		return j.keys(pattern);
	}

	@Override
	public String migrate(String host, int port, String key, int destinationdb, int timeout) {
		return (String) executeCommand(CommandName.MIGRATE, new Object[] { host, port, key, destinationdb, timeout });
	}
	
	private String migrate0(Jedis j, Transaction t, String host, int port, String key, int destinationdb, int timeout) {
		return null; // TODO
	}

	@Override
	public Long move(String key, int db) {
		return (Long) executeCommand(CommandName.MOVE, new Object[] { key, db });
	}
	
	private Long move0(Jedis j, Transaction t, String key, int db) {
		if (t != null) {
			t.move(key, db); return null;
		}
		
		return j.move(key, db);
	}

	@Override
	public Long objectrefcount(String key) {
		return (Long) executeCommand(CommandName.OBJECT_REFCOUNT, new Object[] { key });
	}
	
	private Long objectrefcount0(Jedis j, Transaction t, String key) {
		if (t != null) {
			// TODO
		}
		
		return j.objectRefcount(key);
	}

	@Override
	public String objectencoding(String key) {
		return (String) executeCommand(CommandName.OBJECT_ENCODING, new Object[] { key });
	}
	
	private String objectencoding0(Jedis j, Transaction t, String key) {
		if (t != null) {
			// TODO
		}
		
		return j.objectEncoding(key);
	}

	@Override
	public Long objectidletime(String key) {
		return (Long) executeCommand(CommandName.OBJECT_IDLETIME, new Object[] { key });
	}
	
	private Long objectidletime0(Jedis j, Transaction t, String key) {
		if (t != null) {
			// TODO
		}
		
		return j.objectIdletime(key);
	}

	@Override
	public Long persist(String key) {
		return (Long) executeCommand(CommandName.PERSIST, new Object[] { key });
	}
	
	private Long persist0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.persist(key); return null;
		}
		
		return j.persist(key);
	}

	@Override
	public Long pexpire(String key, int milliseconds) {
		return (Long) executeCommand(CommandName.PEXPIRE, new Object[] { key });
	}
	
	private Long pexpire0(Jedis j, Transaction t, String key, int milliseconds) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public Long pexpireat(String key, long millisecondstimestamp) {
		return (Long) executeCommand(CommandName.PEXPIREAT, new Object[] { key, millisecondstimestamp });
	}
	
	private Long pexpireat0(Jedis j, Transaction t, String key, long millisecondstimestamp) {
		if (t != null) {
			// TODO return null;
		}
		
		return null;
	}

	@Override
	public Long pttl(String key) {
		return (Long) executeCommand(CommandName.PTTL, new Object[] { key });
	}
	
	private Long pttl0(Jedis j, Transaction t, String key) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}
	
	@Override
	public String randomkey() {
		return (String) executeCommand(CommandName.RENAME, new Object[] {});
	}
	
	private String randomkey0(Jedis j, Transaction t) {
		if (t != null) {
			t.randomKey(); return null;
		}
		
		return j.randomKey();
	}
	

	@Override
	public String rename(String key, String newkey) {
		return (String) executeCommand(CommandName.RENAME, new Object[] { key, newkey });
	}
	
	private String rename0(Jedis j, Transaction t, String key, String newkey) {
		if (t != null) {
			t.rename(key, newkey); return null;
		}
		
		return j.rename(key, newkey);
	}

	@Override
	public Long renamenx(String key, String newkey) {
		return (Long) executeCommand(CommandName.RENAMENX, new Object[] { key, newkey });
	}
	
	private Long renamenx0(Jedis j, Transaction t, String key, String newkey) {
		if (t != null) {
			t.renamenx(key, newkey); return null;
		}
		
		return j.renamenx(key, newkey);
	}

	@Override
	public String restore(String key, long ttl, String serializedvalue) {
		return (String) executeCommand(CommandName.RESTORE, new Object[] { key, ttl, serializedvalue });
	}
	
	private String restore0(Jedis j, Transaction t, String key, long ttl, String serializedvalue) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public List<String> sort(String key) {
		return (List<String>) executeCommand(CommandName.SORT, new Object[] { key });
	}
	
	private List<String> sort0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.sort(key); return null;
		}
		
		return j.sort(key);
	}

	@Override
	public List<String> sort(String key, boolean desc) {
		return (List<String>) executeCommand(CommandName.SORT_DESC, new Object[] { key, desc });
	}
	
	private List<String> sort0(Jedis j, Transaction t, String key, boolean desc) {
		SortingParams sp = new SortingParams();
		if (desc) {
			sp.desc();
		}
		
		if (t != null) {
			t.sort(key, sp); return null;
		}
		
		return j.sort(key, sp);
	}

	@Override
	public List<String> sort(String key, boolean alpha, boolean desc) {
		return (List<String>) executeCommand(CommandName.SORT_ALPHA_DESC, new Object[] { key, alpha, desc });
	}
	
	private List<String> sort0(Jedis j, Transaction t, String key, boolean alpha, boolean desc) {
		SortingParams sp = new SortingParams();
		if (desc) {
			sp.desc();
		}
		if (alpha) { 
			sp.alpha() ;
		}
		
		if (t != null) {
			t.sort(key, sp); return null;
		}
		
		return j.sort(key, sp);
	}

	@Override
	public List<String> sort(String key, int offset, int count) {
		return (List<String>) executeCommand(CommandName.SORT_OFFSET_COUNT, new Object[] { key, offset, count });
	}
	
	private List<String> sort0(Jedis j, Transaction t, String key, int offset, int count) {
		if (t != null) {
			t.sort(key, new SortingParams().limit(offset, count)); return null;
		}
		
		return j.sort(key, new SortingParams().limit(offset, count));
	}

	@Override
	public List<String> sort(String key, int offset, int count, boolean alpha, boolean desc) {
		return (List<String>) executeCommand(CommandName.SORT_OFFSET_COUNT_ALPHA_DESC, new Object[] { key, offset, count, alpha, desc });
	}
	
	private List<String> sort0(Jedis j, Transaction t, String key, int offset, int count, boolean alpha, boolean desc) {
		SortingParams sp = new SortingParams();
		if (desc) {
			sp.desc();
		}
		if (alpha) { 
			sp.alpha() ;
		}
		sp.limit(offset, count);
		
		if (t != null) {
			t.sort(key, sp); return null;
		}
		
		return j.sort(key, sp);
	}

	@Override
	public List<String> sort(String key, String bypattern, String... getpatterns) {
		return (List<String>) executeCommand(CommandName.SORT_BY_GET, new Object[] { key, bypattern, getpatterns });
	}
	
	private List<String> sort0(Jedis j, Transaction t, String key, String bypattern, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		
		if (t != null) {
			t.sort(key, sp); return null;
		}
		
		return j.sort(key, sp);
	}

	@Override
	public List<String> sort(String key, String bypattern, boolean desc, String... getpatterns) {
		return (List<String>) executeCommand(CommandName.SORT_BY_GET_DESC, new Object[] { key, bypattern, desc, getpatterns });
	}
	
	private List<String> sort0(Jedis j, Transaction t, String key, String bypattern, boolean desc, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		if (desc) {
			sp.desc();
		}
		
		if (t != null) {
			t.sort(key, sp); return null;
		}
		
		return j.sort(key, sp);
	}

	@Override
	public List<String> sort(String key, String bypattern, boolean alpha, boolean desc, String... getpatterns) {
		return (List<String>) executeCommand(CommandName.SORT_BY_GET_ALPHA_DESC, new Object[] { key, bypattern, alpha, desc, getpatterns });
	}
	
	private List<String> sort0(Jedis j, Transaction t, String key, String bypattern, boolean alpha, boolean desc, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		if (alpha) {
			sp.alpha();
		}
		if (desc) {
			sp.desc();
		}
		
		if (t != null) {
			t.sort(key, sp); return null;
		}
		
		return j.sort(key, sp);
	}

	@Override
	public List<String> sort(String key, String bypattern, int offset, int count, String... getpatterns) {
		return (List<String>) executeCommand(CommandName.SORT_BY_GET_OFFSET_COUNT, new Object[] { key, bypattern, offset, count, getpatterns });
	}
	
	private List<String> sort0(Jedis j, Transaction t, String key, String bypattern, int offset, int count, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		sp.limit(offset, count);
		
		if (t != null) {
			t.sort(key, sp); return null;
		}
		
		return j.sort(key, sp);
	}

	@Override
	public List<String> sort(String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String... getpatterns) {
		return (List<String>) executeCommand(CommandName.SORT_BY_GET_OFFSET_COUNT_ALPHA_DESC, new Object[] { key, bypattern, offset, count, alpha, desc, getpatterns });
	}
	
	private List<String> sort0(Jedis j, Transaction t, String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		sp.limit(offset, count);
		if (alpha) {
			sp.alpha();
		}
		if (desc) {
			sp.desc();
		}
		
		if (t != null) {
			t.sort(key, sp); return null;
		}
		
		return j.sort(key, sp);
	}

	@Override
	public Long sort(String key, String bypattern, String destination) {
		return (Long) executeCommand(CommandName.SORT_BY_DESTINATION, new Object[] { key, bypattern, destination });
	}
	
	private Long sort0(Jedis j, Transaction t, String key, String bypattern, String destination) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		
		if (t != null) {
			t.sort(key, sp, destination); return null;
		}
		
		return j.sort(key, sp, destination);
	}

	@Override
	public Long sort(String key, String bypattern, boolean desc, String destination, String... getpatterns) {
		return (Long) executeCommand(CommandName.SORT_BY_GET_DESC_DESTINATION, new Object[] { key, bypattern, desc, destination, getpatterns });
	}
	
	private Long sort0(Jedis j, Transaction t, String key, String bypattern, boolean desc, String destination, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		if (desc) {
			sp.desc();
		}
		
		if (t != null) {
			t.sort(key, sp, destination); return null;
		}
		
		return j.sort(key, sp, destination);
	}

	@Override
	public Long sort(String key, String bypattern, boolean alpha, boolean desc, String destination, String... getpatterns) {
		return (Long) executeCommand(CommandName.SORT_BY_GET_ALPHA_DESC_DESTINATION, new Object[] { key, bypattern, alpha, desc, destination, getpatterns });
	}
	
	private Long sort0(Jedis j, Transaction t, String key, String bypattern, boolean alpha, boolean desc, String destination, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		if (alpha) {
			sp.alpha();
		}
		if (desc) {
			sp.desc();
		}
		
		if (t != null) {
			t.sort(key, sp, destination); return null;
		}
		
		return j.sort(key, sp, destination);
	}

	@Override
	public Long sort(String key, String bypattern, int offset, int count, String destination, String... getpatterns) {
		return (Long) executeCommand(CommandName.SORT_BY_GET_OFFSET_COUNT_DESTINATION, new Object[] { key, bypattern, offset, count, destination, getpatterns });
	}
	
	private Long sort0(Jedis j, Transaction t, String key, String bypattern, int offset, int count, String destination, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		sp.limit(offset, count);
		
		if (t != null) {
			t.sort(key, sp, destination); return null;
		}
		
		return j.sort(key, sp, destination);
	}

	@Override
	public Long sort(String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String destination, String... getpatterns) {
		return (Long) executeCommand(CommandName.SORT_BY_GET_OFFSET_COUNT_ALPHA_DESC_DESTINATION, new Object[] { key, bypattern, offset, count, alpha, desc, destination, getpatterns });
	}
	
	private Long sort0(Jedis j, Transaction t, String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String destination, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		sp.limit(offset, count);
		if (alpha) {
			sp.alpha();
		}
		if (desc) {
			sp.desc();
		}
		
		if (t != null) {
			t.sort(key, sp, destination); return null;
		}
		
		return j.sort(key, sp, destination);
	}

	@Override
	public Long ttl(String key) {
		return (Long) executeCommand(CommandName.TTL, new Object[] { key });
	}
	
	private Long ttl0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.ttl(key); return null;
		}
		
		return j.ttl(key);
	}

	@Override
	public String type(String key) {
		return (String) executeCommand(CommandName.TYPE, new Object[] { key });
	}
	
	private String type0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.type(key); return null;
		}
		
		return j.type(key);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Strings
	

	@Override
	public Long append(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long bitcount(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long bitnot(String destkey, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long decr(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long decrby(String key, long decrement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get(String key) {
		return (String) executeCommand(CommandName.GET, key);
	}
	
	private String get0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.get(key); return null;
		}
		
		return j.get(key);
	}

	@Override
	public Boolean getbit(String key, long offset) {
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
	public Long incr(String key) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long incrby(String key, long increment) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Double incrbyfloat(String key, double increment) {
		// TODO Auto-generated method stub
		return null;
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
		if (t != null) {
			t.set(key, value); return null;
		}
		
		return j.set(key, value);
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
	public Boolean setbit(String key, long offset, boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String setex(String key, int seconds, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long setnx(String key, String value) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long setrange(String key, long offset, String value) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long strlen(String key) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long hdel(String key, String... fields) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Boolean hexists(String key, String field) {
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
	public Long hincrby(String key, String field, long value) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Double hincrbyfloat(String key, String field, double value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> hkeys(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hlen(String key) {
		// TODO Auto-generated method stub
		return 0L;
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
	public Long hset(String key, String field, String value) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long hsetnx(String key, String field, String value) {
		// TODO Auto-generated method stub
		return 0L;
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
	public Long linsertbefore(String key, String pivot, String value) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long linsertafter(String key, String pivot, String value) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long llen(String key) {
		return (Long) executeCommand(CommandName.LLEN, key);
	}
	
	private Long llen0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.llen(key); return null;
		}
		
		return j.llen(key);
	}

	@Override
	public String lpop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long lpush(String key, String... values) {
		return (Long) executeCommand(CommandName.LPUSH, key, values);
	}
	
	private Long lpush0(Jedis j, Transaction t, String key, String... values) {
		if (t != null) {
			t.lpush(key, values); return null;
		}
		
		return j.lpush(key, values);
	}

	@Override
	public Long lpushx(String key, String value) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public List<String> lrange(String key, long start, long stop) {
		return (List<String>) executeCommand(CommandName.LRANGE, key, start, stop);
	}
	
	private List<String> lrange0(Jedis j, Transaction t, String key, long start, long stop) {
		if (t != null) {
			t.lrange(key, start, stop); return null;
		}
		
		return j.lrange(key, start, stop);
	}

	@Override
	public Long lrem(String key, long count, String value) {
		// TODO Auto-generated method stub
		return 0L;
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
	public Long rpush(String key, String... values) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long rpushx(String key, String value) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long sadd(String key, String... members) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long scard(String key) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Boolean sismember(String key, String member) {
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
	public Long srem(String key, String... members) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long zadd(String key, double score, String member) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long zcard(String key) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long zcount(String key, double min, double max) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Double zincrby(String key, double score, String member) {
		// TODO Auto-generated method stub
		return null;
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
	public Long zrem(String key, String... members) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long zremrangebyrank(String key, long start, long stop) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long zremrangebyscore(String key, double min, double max) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long zremrangebyscore(String key, String min, String max) {
		// TODO Auto-generated method stub
		return 0L;
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
	public Long publish(String channel, String message) {
		// TODO Auto-generated method stub
		return 0L;
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
	public Boolean scriptexists(String sha1) {
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
	public Long del(String... keys) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long bitand(String destkey, String... keys) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long bitor(String destkey, String... keys) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long bitxor(String destkey, String... keys) {
		// TODO Auto-generated method stub
		return 0L;
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
	public Long sdiffstore(String destination, String... keys) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Set<String> sinter(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long sinterstore(String destination, String... keys) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long smove(String source, String destination, String member) {
		// TODO Auto-generated method stub
		return 0L;
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
	public Long zinterstore(String destination, String... keys) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long zinterstoremax(String destination, String... keys) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long zinterstoremin(String destination, String... keys) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long zinterstore(String destination, Map<String, Integer> weightkeys) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long zinterstoremax(String destination,
			Map<String, Integer> weightkeys) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long zinterstoremin(String destination,
			Map<String, Integer> weightkeys) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long zunionstore(String destination, String... keys) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long zunionstoremax(String destination, String... keys) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long zunionstoremin(String destination, String... keys) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long zunionstore(String destination, Map<String, Integer> weightkeys) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long zunionstoremax(String destination,
			Map<String, Integer> weightkeys) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long zunionstoremin(String destination,
			Map<String, Integer> weightkeys) {
		// TODO Auto-generated method stub
		return 0L;
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
	public Boolean[] scriptexists(String... sha1) {
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
	public Long dbsize() {
		// TODO Auto-generated method stub
		return 0L;
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
	public Long lastsave() {
		// TODO Auto-generated method stub
		return 0L;
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
	public Long slowloglen() {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public void sync() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Long time() {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Long microtime() {
		// TODO Auto-generated method stub
		return 0L;
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
			case DUMP: // TODO                 
				return dump0(j, t, (String) args[0]);   
			case EXISTS:
				return exists0(j, t, (String) args[0]);
			case EXPIRE:
				return expire0(j, t, (String) args[0], (Integer) args[1]);
			case EXPIREAT:
				return expireat0(j, t, (String) args[0], (Long) args[1]);
			case KEYS:
				return keys0(j, t, (String) args[0]);
			case MIGRATE: // TODO
				return migrate0(j, t, (String) args[0], (Integer) args[1], (String) args[2], (Integer) args[3], (Integer) args[4]);
			case MOVE:
				return move0(j, t, (String) args[0], (Integer) args[1]);
			case OBJECT_REFCOUNT:
				return objectrefcount0(j, t, (String) args[0]);
			case OBJECT_ENCODING:
				return objectencoding0(j, t, (String) args[0]);
			case OBJECT_IDLETIME:
				return objectidletime0(j, t, (String) args[0]);
			case PERSIST:
				return persist0(j, t, (String) args[0]);
			case PEXPIRE: // TODO
				return pexpire0(j, t, (String) args[0], (Integer) args[1]);
			case PEXPIREAT: // TODO
				return pexpireat0(j, t, (String) args[0], (Long) args[1]);
			case PTTL: // TODO
				return pttl0(j, t, (String) args[0]);
			case RANDOMKEY:
				return randomkey0(j, t);
			case RENAME:
				return rename0(j, t, (String) args[0], (String) args[1]);
			case RENAMENX:
				return renamenx0(j, t, (String) args[0], (String) args[1]);
			case RESTORE:
				return restore0(j, t, (String) args[0], (Long) args[1], (String) args[2]);
			case SORT:
				return sort0(j, t, (String) args[0]);
			case SORT_DESC:
				return sort0(j, t, (String) args[0], (Boolean) args[1]);
			case SORT_ALPHA_DESC:
				return sort0(j, t, (String) args[0], (Boolean) args[1], (Boolean) args[2]);
			case SORT_OFFSET_COUNT:
				return sort0(j, t, (String) args[0], (Integer) args[1], (Integer) args[2]);
			case SORT_OFFSET_COUNT_ALPHA_DESC:
				return sort0(j, t, (String) args[0], (Integer) args[1], (Integer) args[2], (Boolean) args[3], (Boolean) args[4]);
			case SORT_BY_GET:
				return sort0(j, t, (String) args[0], (String) args[1], (String[]) args[2]);
			case SORT_BY_GET_DESC:
				return sort0(j, t, (String) args[0], (String) args[1], (Boolean) args[2], (String[]) args[3]);
			case SORT_BY_GET_ALPHA_DESC:
				return sort0(j, t, (String) args[0], (String) args[1], (Boolean) args[2], (Boolean) args[3], (String[]) args[4]);
			case SORT_BY_GET_OFFSET_COUNT:
				return sort0(j, t, (String) args[0], (String) args[1], (Integer) args[2], (Integer) args[3], (String[]) args[4]);
			case SORT_BY_GET_OFFSET_COUNT_ALPHA_DESC:
				return sort0(j, t, (String) args[0], (String) args[1], (Integer) args[2], (Integer) args[3], (Boolean) args[4], (Boolean) args[5], (String[]) args[6]);
			case SORT_BY_DESTINATION:
				return sort0(j, t, (String) args[0], (String) args[1], (String) args[2]);
			case SORT_BY_GET_DESC_DESTINATION:
				return sort0(j, t, (String) args[0], (String) args[1], (Boolean) args[2], (String) args[3], (String[]) args[4]);
			case SORT_BY_GET_ALPHA_DESC_DESTINATION:
				return sort0(j, t, (String) args[0], (String) args[1], (Boolean) args[2], (Boolean) args[3], (String) args[4], (String[]) args[5]);
			case SORT_BY_GET_OFFSET_COUNT_DESTINATION:
				return sort0(j, t, (String) args[0], (String) args[1], (Integer) args[2], (Integer) args[3], (String) args[4], (String[]) args[5]);
			case SORT_BY_GET_OFFSET_COUNT_ALPHA_DESC_DESTINATION:				
				return sort0(j, t, (String) args[0], (String) args[1], (Integer) args[2], (Integer) args[3], (Boolean) args[4], (Boolean) args[5], (String) args[6], (String[]) args[7]);
			case TTL:
				return ttl0(j, t, (String) args[0]);
			case TYPE:
				return type0(j, t, (String) args[0]);
				
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
			
			// Hashes
				
			// Lists
			case LLEN:
				return llen0(j, t, (String) args[0]);
			case LPOP:
			case LPUSH:
				return lpush0(j, t, (String) args[0], (String[]) args[1]);
			case LPUSHX:
			case LRANGE:
				return lrange0(j, t, (String) args[0], (Long) args[1], (Long) args[2]);
				
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
				throw new IllegalArgumentException("Wrong command");
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

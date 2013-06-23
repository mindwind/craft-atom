package org.craft.atom.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.craft.atom.redis.api.RedisConnectionException;
import org.craft.atom.redis.api.RedisDataException;
import org.craft.atom.redis.api.RedisException;
import org.craft.atom.redis.api.RedisOperationException;
import org.craft.atom.redis.api.SingletonRedisCommand;
import org.craft.atom.redis.api.Slowlog;
import org.craft.atom.redis.api.handler.RedisMonitorHandler;
import org.craft.atom.redis.api.handler.RedisPsubscribeHandler;
import org.craft.atom.redis.api.handler.RedisSubscribeHandler;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.DebugParams;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisMonitor;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.ZParams.Aggregate;
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
	public Long del(String key) {
		return (Long) del(new String[] { key });
	}
	
	@Override
	public Long del(String... keys) {
		return (Long) executeCommand(CommandEnum.DEL, new Object[] { keys });
	}
	
	private Long del0(Jedis j, Transaction t, String... keys) {
		if (t != null) {
			t.del(keys); return null;
		}
		
		return j.del(keys);
	}

	@Override
	public String dump(String key) {
		return (String) executeCommand(CommandEnum.DUMP, new Object[] { key });
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
		return (Boolean) executeCommand(CommandEnum.EXISTS, new Object[] { key });
	}
	
	private Boolean exists0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.exists(key); return null;
		}
		
		return j.exists(key);
	}

	@Override
	public Long expire(String key, int seconds) {
		return (Long) executeCommand(CommandEnum.EXPIRE, new Object[] { key, seconds });
	}
	
	private Long expire0(Jedis j, Transaction t, String key, int seconds) {
		if (t != null) {
			t.expire(key, seconds); return null;
		}
		
		return j.expire(key, seconds);
	}

	@Override
	public Long expireat(String key, long timestamp) {
		return (Long) executeCommand(CommandEnum.EXPIREAT, new Object[] { key, timestamp });
	}
	
	private Long expireat0(Jedis j, Transaction t, String key, long timestamp) {
		if (t != null) {
			t.expireAt(key, timestamp); return null;
		}
		
		return j.expireAt(key, timestamp);
	}

	@Override
	public Set<String> keys(String pattern) {
		return (Set<String>) executeCommand(CommandEnum.KEYS, new Object[] { pattern });
	}
	
	private Set<String> keys0(Jedis j, Transaction t, String pattern) {
		if (t != null) {
			t.keys(pattern); return null;
		}
		
		return j.keys(pattern);
	}

	@Override
	public String migrate(String host, int port, String key, int destinationdb, int timeout) {
		return (String) executeCommand(CommandEnum.MIGRATE, new Object[] { host, port, key, destinationdb, timeout });
	}
	
	private String migrate0(Jedis j, Transaction t, String host, int port, String key, int destinationdb, int timeout) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public Long move(String key, int db) {
		return (Long) executeCommand(CommandEnum.MOVE, new Object[] { key, db });
	}
	
	private Long move0(Jedis j, Transaction t, String key, int db) {
		if (t != null) {
			t.move(key, db); return null;
		}
		
		return j.move(key, db);
	}

	@Override
	public Long objectrefcount(String key) {
		return (Long) executeCommand(CommandEnum.OBJECT_REFCOUNT, new Object[] { key });
	}
	
	private Long objectrefcount0(Jedis j, Transaction t, String key) {
		if (t != null) {
			// TODO
		}
		
		return j.objectRefcount(key);
	}

	@Override
	public String objectencoding(String key) {
		return (String) executeCommand(CommandEnum.OBJECT_ENCODING, new Object[] { key });
	}
	
	private String objectencoding0(Jedis j, Transaction t, String key) {
		if (t != null) {
			// TODO
		}
		
		return j.objectEncoding(key);
	}

	@Override
	public Long objectidletime(String key) {
		return (Long) executeCommand(CommandEnum.OBJECT_IDLETIME, new Object[] { key });
	}
	
	private Long objectidletime0(Jedis j, Transaction t, String key) {
		if (t != null) {
			// TODO
		}
		
		return j.objectIdletime(key);
	}

	@Override
	public Long persist(String key) {
		return (Long) executeCommand(CommandEnum.PERSIST, new Object[] { key });
	}
	
	private Long persist0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.persist(key); return null;
		}
		
		return j.persist(key);
	}

	@Override
	public Long pexpire(String key, int milliseconds) {
		return (Long) executeCommand(CommandEnum.PEXPIRE, new Object[] { key });
	}
	
	private Long pexpire0(Jedis j, Transaction t, String key, int milliseconds) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public Long pexpireat(String key, long millisecondstimestamp) {
		return (Long) executeCommand(CommandEnum.PEXPIREAT, new Object[] { key, millisecondstimestamp });
	}
	
	private Long pexpireat0(Jedis j, Transaction t, String key, long millisecondstimestamp) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public Long pttl(String key) {
		return (Long) executeCommand(CommandEnum.PTTL, new Object[] { key });
	}
	
	private Long pttl0(Jedis j, Transaction t, String key) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}
	
	@Override
	public String randomkey() {
		return (String) executeCommand(CommandEnum.RANDOMKEY, new Object[] {});
	}
	
	private String randomkey0(Jedis j, Transaction t) {
		if (t != null) {
			t.randomKey(); return null;
		}
		
		return j.randomKey();
	}
	

	@Override
	public String rename(String key, String newkey) {
		return (String) executeCommand(CommandEnum.RENAME, new Object[] { key, newkey });
	}
	
	private String rename0(Jedis j, Transaction t, String key, String newkey) {
		if (t != null) {
			t.rename(key, newkey); return null;
		}
		
		return j.rename(key, newkey);
	}

	@Override
	public Long renamenx(String key, String newkey) {
		return (Long) executeCommand(CommandEnum.RENAMENX, new Object[] { key, newkey });
	}
	
	private Long renamenx0(Jedis j, Transaction t, String key, String newkey) {
		if (t != null) {
			t.renamenx(key, newkey); return null;
		}
		
		return j.renamenx(key, newkey);
	}

	@Override
	public String restore(String key, long ttl, String serializedvalue) {
		return (String) executeCommand(CommandEnum.RESTORE, new Object[] { key, ttl, serializedvalue });
	}
	
	private String restore0(Jedis j, Transaction t, String key, long ttl, String serializedvalue) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public List<String> sort(String key) {
		return (List<String>) executeCommand(CommandEnum.SORT, new Object[] { key });
	}
	
	private List<String> sort0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.sort(key); return null;
		}
		
		return j.sort(key);
	}

	@Override
	public List<String> sort(String key, boolean desc) {
		return (List<String>) executeCommand(CommandEnum.SORT_DESC, new Object[] { key, desc });
	}
	
	private List<String> sort_desc(Jedis j, Transaction t, String key, boolean desc) {
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
		return (List<String>) executeCommand(CommandEnum.SORT_ALPHA_DESC, new Object[] { key, alpha, desc });
	}
	
	private List<String> sort_alpha_desc(Jedis j, Transaction t, String key, boolean alpha, boolean desc) {
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
		return (List<String>) executeCommand(CommandEnum.SORT_OFFSET_COUNT, new Object[] { key, offset, count });
	}
	
	private List<String> sort_offset_count(Jedis j, Transaction t, String key, int offset, int count) {
		if (t != null) {
			t.sort(key, new SortingParams().limit(offset, count)); return null;
		}
		
		return j.sort(key, new SortingParams().limit(offset, count));
	}

	@Override
	public List<String> sort(String key, int offset, int count, boolean alpha, boolean desc) {
		return (List<String>) executeCommand(CommandEnum.SORT_OFFSET_COUNT_ALPHA_DESC, new Object[] { key, offset, count, alpha, desc });
	}
	
	private List<String> sort_offset_count_alpha_desc(Jedis j, Transaction t, String key, int offset, int count, boolean alpha, boolean desc) {
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
		return (List<String>) executeCommand(CommandEnum.SORT_BY_GET, new Object[] { key, bypattern, getpatterns });
	}
	
	private List<String> sort_by_get(Jedis j, Transaction t, String key, String bypattern, String... getpatterns) {
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
		return (List<String>) executeCommand(CommandEnum.SORT_BY_DESC_GET, new Object[] { key, bypattern, desc, getpatterns });
	}
	
	private List<String> sort_by_desc_get(Jedis j, Transaction t, String key, String bypattern, boolean desc, String... getpatterns) {
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
		return (List<String>) executeCommand(CommandEnum.SORT_BY_ALPHA_DESC_GET, new Object[] { key, bypattern, alpha, desc, getpatterns });
	}
	
	private List<String> sort_by_alpha_desc_get(Jedis j, Transaction t, String key, String bypattern, boolean alpha, boolean desc, String... getpatterns) {
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
		return (List<String>) executeCommand(CommandEnum.SORT_BY_OFFSET_COUNT_GET, new Object[] { key, bypattern, offset, count, getpatterns });
	}
	
	private List<String> sort_by_offset_count_get(Jedis j, Transaction t, String key, String bypattern, int offset, int count, String... getpatterns) {
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
		return (List<String>) executeCommand(CommandEnum.SORT_BY_OFFSET_COUNT_ALPHA_DESC_GET, new Object[] { key, bypattern, offset, count, alpha, desc, getpatterns });
	}
	
	private List<String> sort_by_offset_count_alpha_desc_get(Jedis j, Transaction t, String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String... getpatterns) {
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
		return (Long) executeCommand(CommandEnum.SORT_BY_DESTINATION, new Object[] { key, bypattern, destination });
	}
	
	private Long sort_by_destination(Jedis j, Transaction t, String key, String bypattern, String destination) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		
		if (t != null) {
			t.sort(key, sp, destination); return null;
		}
		
		return j.sort(key, sp, destination);
	}

	@Override
	public Long sort(String key, String bypattern, boolean desc, String destination, String... getpatterns) {
		return (Long) executeCommand(CommandEnum.SORT_BY_DESC_DESTINATION_GET, new Object[] { key, bypattern, desc, destination, getpatterns });
	}
	
	private Long sort_by_desc_destination_get(Jedis j, Transaction t, String key, String bypattern, boolean desc, String destination, String... getpatterns) {
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
		return (Long) executeCommand(CommandEnum.SORT_BY_ALPHA_DESC_DESTINATION_GET, new Object[] { key, bypattern, alpha, desc, destination, getpatterns });
	}
	
	private Long sort_by_alpha_desc_destination_get(Jedis j, Transaction t, String key, String bypattern, boolean alpha, boolean desc, String destination, String... getpatterns) {
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
		return (Long) executeCommand(CommandEnum.SORT_BY_OFFSET_COUNT_DESTINATION_GET, new Object[] { key, bypattern, offset, count, destination, getpatterns });
	}
	
	private Long sort_by_offset_count_destination_get(Jedis j, Transaction t, String key, String bypattern, int offset, int count, String destination, String... getpatterns) {
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
		return (Long) executeCommand(CommandEnum.SORT_BY_OFFSET_COUNT_ALPHA_DESC_DESTINATION_GET, new Object[] { key, bypattern, offset, count, alpha, desc, destination, getpatterns });
	}
	
	private Long sort_by_offset_count_alpha_desc_destination_get(Jedis j, Transaction t, String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String destination, String... getpatterns) {
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
		return (Long) executeCommand(CommandEnum.TTL, new Object[] { key });
	}
	
	private Long ttl0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.ttl(key); return null;
		}
		
		return j.ttl(key);
	}

	@Override
	public String type(String key) {
		return (String) executeCommand(CommandEnum.TYPE, new Object[] { key });
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
		return (Long) executeCommand(CommandEnum.APPEND, key, value);
	}
	
	private Long append0(Jedis j, Transaction t, String key, String value) {
		if (t != null) {
			t.append(key, value); return null;
		}
		
		return j.append(key, value);
	}

	@Override
	public Long bitcount(String key) {
		return (Long) executeCommand(CommandEnum.BITCOUNT, key);
	}
	
	private Long bitcount0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.bitcount(key); return null;
		}
		
		return j.bitcount(key);
	}

	@Override
	public Long bitnot(String destkey, String key) {
		return (Long) executeCommand(CommandEnum.BITNOT, destkey, key);
	}
	
	private Long bitnot0(Jedis j, Transaction t, String destkey, String key) {
		if (t != null) {
			t.bitop(BitOP.NOT, destkey, key); return null;
		}
		
		return j.bitop(BitOP.NOT, destkey, key);
	}
	
	@Override
	public Long bitand(String destkey, String... keys) {
		return (Long) executeCommand(CommandEnum.BITAND, destkey, keys);
	}
	
	private Long bitand0(Jedis j, Transaction t, String destkey, String... keys) {
		if (t != null) {
			t.bitop(BitOP.AND, destkey, keys); return null;
		}
		
		return j.bitop(BitOP.AND, destkey, keys);
	}

	@Override
	public Long bitor(String destkey, String... keys) {
		return (Long) executeCommand(CommandEnum.BITOR, destkey, keys);
	}
	
	private Long bitor0(Jedis j, Transaction t, String destkey, String... keys) {
		if (t != null) {
			t.bitop(BitOP.OR, destkey, keys); return null;
		}
		
		return j.bitop(BitOP.OR, destkey, keys);
	}

	@Override
	public Long bitxor(String destkey, String... keys) {
		return (Long) executeCommand(CommandEnum.BITXOR, destkey, keys);
	}
	
	private Long bitxor0(Jedis j, Transaction t, String destkey, String... keys) {
		if (t != null) {
			t.bitop(BitOP.XOR, destkey, keys); return null;
		}
		
		return j.bitop(BitOP.XOR, destkey, keys);
	}

	@Override
	public Long decr(String key) {
		return (Long) executeCommand(CommandEnum.DECR, key);
	}
	
	private Long decr0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.decr(key); return null;
		}
		
		return j.decr(key);
	}

	@Override
	public Long decrby(String key, long decrement) {
		return (Long) executeCommand(CommandEnum.DECRBY, key, decrement);
	}
	
	private Long decrby0(Jedis j, Transaction t, String key, long decrement) {
		if (t != null) {
			t.decrBy(key, decrement);
		}
		
		return j.decrBy(key, decrement);
	}

	@Override
	public String get(String key) {
		return (String) executeCommand(CommandEnum.GET, key);
	}
	
	private String get0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.get(key); return null;
		}
		
		return j.get(key);
	}

	@Override
	public Boolean getbit(String key, long offset) {
		return (Boolean) executeCommand(CommandEnum.GETBIT, key, offset);
	}
	
	private Boolean getbit0(Jedis j, Transaction t, String key, long offset) {
		if (t != null) {
			t.getbit(key, offset); return null;
		}
		
		return j.getbit(key, offset);
	}

	@Override
	public String getrange(String key, long start, long end) {
		return (String) executeCommand(CommandEnum.GETRANGE, key, start, end);
	}
	
	private String getrange0(Jedis j, Transaction t, String key, long start, long end) {
		if (t != null) {
			t.getrange(key, start, end); return null;
		}
		
		return j.getrange(key, start, end);
	}

	@Override
	public String getset(String key, String value) {
		return (String) executeCommand(CommandEnum.GETSET, key, value);
	}
	
	private String getset0(Jedis j, Transaction t, String key, String value) {
		if (t != null) {
			t.getSet(key, value); return null;
		}
		
		return j.getSet(key, value);
	}

	@Override
	public Long incr(String key) {
		return (Long) executeCommand(CommandEnum.INCR, key);
	}
	
	private Long incr0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.incr(key); return null;
		}
		
		return j.incr(key);
	}

	@Override
	public Long incrby(String key, long increment) {
		return (Long) executeCommand(CommandEnum.INCRBY, key, increment);
	}
	
	private Long incrby0(Jedis j, Transaction t, String key, long increment) {
		if (t != null) {
			t.incrBy(key, increment); return null;
		}
		
		return j.incrBy(key, increment);
	}

	@Override
	public Double incrbyfloat(String key, double increment) {
		return (Double) executeCommand(CommandEnum.INCRBYFLOAT, key, increment);
	}
	
	private Double incrbyfloat(Jedis j, Transaction t, String key, double increment) {
		if (t != null) {
//			return TODO
		}
		
		return null;
	}
	
	@Override
	public List<String> mget(String... keys) {
		return (List<String>) executeCommand(CommandEnum.MGET, new Object[] { keys });
	}
	
	private List<String> mget0(Jedis j, Transaction t, String... keys) {
		if (t != null) {
			t.mget(keys); return null;
		}
		
		return j.mget(keys);
	}

	@Override
	public String mset(String... keysvalues) {
		return (String) executeCommand(CommandEnum.MSET, new Object[] { keysvalues });
	}
	
	private String mset0(Jedis j, Transaction t, String... keysvalues) {
		if (t != null) {
			t.mset(keysvalues); return null;
		}
		
		return j.mset(keysvalues);
	}

	@Override
	public Long msetnx(String... keysvalues) {
		return (Long) executeCommand(CommandEnum.MSETNX, new Object[] { keysvalues });
	}
	
	private Long msetnx0(Jedis j, Transaction t, String... keysvalues) {
		if (t != null) {
			t.msetnx(keysvalues); return null;
		}
		
		return j.msetnx(keysvalues);
	}


	@Override
	public String psetex(String key, int milliseconds, String value) {
		return (String) executeCommand(CommandEnum.PSETEX, milliseconds, value);
	}
	
	private String psetex0(Jedis j, Transaction t, String key, int milliseconds, String value) {
		if (t != null) {
			// TOOD
		}
		
		return null;
	}

	@Override
	public String set(String key, String value) {
		return (String) executeCommand(CommandEnum.SET, key, value);
	}
	
	private String set0(Jedis j, Transaction t, String key, String value) {
		if (t != null) {
			t.set(key, value); return null;
		}
		
		return j.set(key, value);
	}

	@Override
	public String setxx(String key, String value) {
		return (String) executeCommand(CommandEnum.SETXX, key, value);
	}
	
	private String setxx0(Jedis j, Transaction t, String key, String value) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public String setnxex(String key, String value, int seconds) {
		return (String) executeCommand(CommandEnum.SETNXEX, key, value, seconds);
	}
	
	private String setnxex0(Jedis j, Transaction t, String key, String value, int seconds) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public String setnxpx(String key, String value, int milliseconds) {
		return (String) executeCommand(CommandEnum.SETNXPX, key, value, milliseconds);
	}
	
	private String setnxpx0(Jedis j, Transaction t, String key, String value, int milliseconds) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public String setxxex(String key, String value, int seconds) {
		return (String) executeCommand(CommandEnum.SETXXEX, key, value, seconds);
	}
	
	private String setxxex0(Jedis j, Transaction t, String key, String value, int seconds) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public String setxxpx(String key, String value, int milliseconds) {
		return (String) executeCommand(CommandEnum.SETXXPX, key, value, milliseconds);
	}
	
	private String setxxpx0(Jedis j, Transaction t, String key, String value, int milliseconds) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}
	
	@Override
	public Boolean setbit(String key, long offset, boolean value) {
		return (Boolean) executeCommand(CommandEnum.SETBIT, key, offset, value);
	}
	
	private Boolean setbit0(Jedis j, Transaction t, String key, long offset, boolean value) {
		if (t != null) {
			t.setbit(key, offset, value); return null;
		}
		
		return j.setbit(key, offset, value);
	}

	@Override
	public String setex(String key, int seconds, String value) {
		return (String) executeCommand(CommandEnum.SETEX, key, seconds, value);
	}
	
	private String setex0(Jedis j, Transaction t, String key, int seconds, String value) {
		if (t != null) {
			t.setex(key, seconds, value); return null;
		}
		
		return j.setex(key, seconds, value);
	}

	@Override
	public Long setnx(String key, String value) {
		return (Long) executeCommand(CommandEnum.SETNX, key, value);
	}
	
	private Long setnx0(Jedis j, Transaction t, String key, String value) {
		if (t != null) {
			t.setnx(key, value); return null;
		}
		
		return j.setnx(key, value);
	}

	@Override
	public Long setrange(String key, long offset, String value) {
		return (Long) executeCommand(CommandEnum.SETRANGE, key, offset, value);
	}
	
	private Long setrange0(Jedis j, Transaction t, String key, long offset, String value) {
		if (t != null) {
			t.setrange(key, offset, value); return null;
		}
		
		return j.setrange(key, offset, value);
	}

	@Override
	public Long strlen(String key) {
		return (Long) executeCommand(CommandEnum.STRLEN, key);
	}
	
	private Long strlen0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.strlen(key); return null;
		}
		
		return j.strlen(key);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Hashes
	
	

	@Override
	public Long hdel(String key, String... fields) {
		return (Long) executeCommand(CommandEnum.HDEL, key, fields);
	}
	
	private Long hdel0(Jedis j, Transaction t, String key, String... fields) {
		if (t != null) {
			// TODO
		}
		
		return j.hdel(key, fields);
	}

	@Override
	public Boolean hexists(String key, String field) {
		return (Boolean) executeCommand(CommandEnum.HEXISTS, key, field);
	}
	
	private Boolean hexists0(Jedis j, Transaction t, String key, String field) {
		if (t != null) {
			t.hexists(key, field); return null;
		}
		
		return j.hexists(key, field);
	}

	@Override
	public String hget(String key, String field) {
		return (String) executeCommand(CommandEnum.HGET, key, field);
	}
	
	private String hget0(Jedis j, Transaction t, String key, String field) {
		if (t != null) {
			t.hget(key, field); return null;
		}
		
		return j.hget(key, field);
	}

	@Override
	public Map<String, String> hgetall(String key) {
		return (Map<String, String>) executeCommand(CommandEnum.HGETALL, key);
	}
	
	private Map<String, String> hgetall0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.hgetAll(key); return null;
		}
		
		return j.hgetAll(key);
	}

	@Override
	public Long hincrby(String key, String field, long increment) {
		return (Long) executeCommand(CommandEnum.HINCRBY, key, field, increment);
	}
	
	private Long hincrby(Jedis j, Transaction t, String key, String field, long increment) {
		if (t != null) {
			t.hincrBy(key, field, increment); return null;
		}
		
		return j.hincrBy(key, field, increment);
 	}

	@Override
	public Double hincrbyfloat(String key, String field, double increment) {
		return (Double) executeCommand(CommandEnum.HINCRBYFLOAT, key, field, increment);
	}
	
	private Double hincrbyfloat0(Jedis j, Transaction t, String key, String field, double increment) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public Set<String> hkeys(String key) {
		return (Set<String>) executeCommand(CommandEnum.HKEYS, key);
	}
	
	private Set<String> hkeys0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.hkeys(key); return null;
		}
		
		return j.hkeys(key);
	}

	@Override
	public Long hlen(String key) {
		return (Long) executeCommand(CommandEnum.HLEN, key);
	}
	
	private Long hlen0(Jedis j, Transaction t, String key, String field) {
		if (t != null) {
			t.hlen(key); return null;
		}
		
		return j.hlen(key);
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		return (List<String>) executeCommand(CommandEnum.HMGET, key, fields);
	}
	
	private List<String> hmget0(Jedis j, Transaction t, String key, String... fields) {
		if (t != null) {
			t.hmget(key, fields); return null;
		}
		
		return j.hmget(key, fields);
	}

	@Override
	public String hmset(String key, Map<String, String> fieldvalues) {
		return (String) executeCommand(CommandEnum.HMSET, fieldvalues);
	}
	
	private String hmset0(Jedis j, Transaction t, String key, Map<String, String> fieldvalues) {
		if (t != null) {
			t.hmset(key, fieldvalues); return null;
		}
		
		return j.hmset(key, fieldvalues);
	}

	@Override
	public Long hset(String key, String field, String value) {
		return (Long) executeCommand(CommandEnum.HSET, key, field, value);
	}
	
	private Long hset0(Jedis j, Transaction t, String key, String field, String value) {
		if (t != null) {
			t.hset(key, field, value); return null;
		}
		
		return j.hset(key, field, value);
	}

	@Override
	public Long hsetnx(String key, String field, String value) {
		return (Long) executeCommand(CommandEnum.HSETNX, key, field, value);
	}
	
	private Long hsetnx0(Jedis j, Transaction t, String key, String field, String value) {
		if (t != null) {
			t.hsetnx(key, field, value); return null;
		}
		
		return j.hsetnx(key, field, value);
	}

	@Override
	public List<String> hvals(String key) {
		return (List<String>) executeCommand(CommandEnum.HVALS, key);
	}
	
	private List<String> hvals0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.hvals(key); return null;
		}
		
		return j.hvals(key);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Lists
	
	

	@Override
	public String blpop(String key) {
		return blpop(key, 0);
	}

	@Override
	public String blpop(String key, int timeout) {
		return (String) executeCommand(CommandEnum.BLPOP, key, timeout);
	}
	
	private String blpop0(Jedis j, Transaction t, String key, int timeout) {
		if (t != null) {
			// TODO
		}
		
		List<String> l = j.blpop(timeout, key);
		Map<String, String> map = convert4bpop(l);
		return map.get(key);
	}
	
	@Override
	public Map<String, String> blpop(String... keys) {
		return blpop(0, keys);
 	}
	
	@Override
	public Map<String, String> blpop(int timeout, String... keys) {
		return (Map<String, String>) executeCommand(CommandEnum.BLPOP_KEYS, new Object[] { keys });
	}
	
	private Map<String, String> blpop0(Jedis j, Transaction t, int timeout, String... keys) {
		if (t != null) {
			// TODO
		}
		
		List<String> l = j.blpop(timeout, keys);
		return convert4bpop(l);
	}
	
	private Map<String, String> convert4bpop(List<String> l) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (int i = 0; i < l.size(); i += 2) {
			String key = l.get(i);
			String value = l.get(i + 1);
			map.put(key, value);
		}
		return map;
	}

	@Override
	public String brpop(String key) {
		return brpop(key, 0);
	}

	@Override
	public String brpop(String key, int timeout) {
		return (String) executeCommand(CommandEnum.BRPOP, timeout);
	}
	
	private String brpop0(Jedis j, Transaction t, String key, int timeout) {
		if (t != null) {
			// TODO
		}
		
		List<String> l = j.brpop(timeout, key);
		Map<String, String> map = convert4bpop(l);
		return map.get(key);
	}
	
	@Override
	public Map<String, String> brpop(String... keys) {
		return brpop(0, keys);
	}

	@Override
	public Map<String, String> brpop(int timeout, String... keys) {
		return (Map<String, String>) executeCommand(CommandEnum.BRPOP_KEYS, timeout, keys);
	}
	
	private Map<String, String> brpop0(Jedis j, Transaction t, int timeout, String... keys) {
		if (t != null) {
			// TODO
		}
		
		List<String> l = j.brpop(timeout, keys);
		return convert4bpop(l);
	}
	

	@Override
	public String brpoplpush(String source, String destination, int timeout) {
		return (String) executeCommand(CommandEnum.BRPOPLPUSH, source, destination, timeout);
	}
	
	private String brpoplpush0(Jedis j, Transaction t, String source, String  destination, int timeout) {
		if (t != null) {
			t.brpoplpush(source, destination, timeout); return null;
		}
		
		return j.brpoplpush(source, destination, timeout);
	}

	@Override
	public String lindex(String key, long index) {
		return (String) executeCommand(CommandEnum.LINDEX, key, index);
	}
	
	private String lindex0(Jedis j, Transaction t, String key, long index) {
		if (t != null) {
//			t.lindex(key, index); TODO
		}
		
		return j.lindex(key, index);
	}

	@Override
	public Long linsertbefore(String key, String pivot, String value) {
		return (Long) executeCommand(CommandEnum.LINSERT_BEFORE, key, pivot, value);
	}
	
	private Long linsertbefore0(Jedis j, Transaction t, String key, String pivot, String value) {
		if (t != null) {
			t.linsert(key, LIST_POSITION.BEFORE, pivot, value); return null;
		}
		
		return j.linsert(key, LIST_POSITION.BEFORE, pivot, value);
	}

	@Override
	public Long linsertafter(String key, String pivot, String value) {
		return (Long) executeCommand(CommandEnum.LINSERT_AFTER, key, pivot, value);
	}
	
	private Long linsertafter0(Jedis j, Transaction t, String key, String pivot, String value) {
		if (t != null) {
			t.linsert(key, LIST_POSITION.AFTER, pivot, value); return null;
		}
		
		return j.linsert(key, LIST_POSITION.AFTER, pivot, value);
	}

	@Override
	public Long llen(String key) {
		return (Long) executeCommand(CommandEnum.LLEN, key);
	}
	
	private Long llen0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.llen(key); return null;
		}
		
		return j.llen(key);
	}

	@Override
	public String lpop(String key) {
		return (String) executeCommand(CommandEnum.LPOP, key);
	}
	
	private String lpop0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.lpop(key); return null;
		}
		
		return j.lpop(key);
	}

	@Override
	public Long lpush(String key, String... values) {
		return (Long) executeCommand(CommandEnum.LPUSH, key, values);
	}
	
	private Long lpush0(Jedis j, Transaction t, String key, String... values) {
		if (t != null) {
			t.lpush(key, values); return null;
		}
		
		return j.lpush(key, values);
	}

	@Override
	public Long lpushx(String key, String value) {
		return (Long) executeCommand(CommandEnum.LPUSHX, key, value);
	}
	
	private Long lpushx0(Jedis j, Transaction t, String key, String value) {
		if (t != null) {
			t.lpushx(key, value); return null;
		}
		
		return j.lpushx(key, value);
	}

	@Override
	public List<String> lrange(String key, long start, long stop) {
		return (List<String>) executeCommand(CommandEnum.LRANGE, key, start, stop);
	}
	
	private List<String> lrange0(Jedis j, Transaction t, String key, long start, long stop) {
		if (t != null) {
			t.lrange(key, start, stop); return null;
		}
		
		return j.lrange(key, start, stop);
	}

	@Override
	public Long lrem(String key, long count, String value) {
		return (Long) executeCommand(CommandEnum.LREM, key, count, value);
	}
	
	private Long lrem0(Jedis j, Transaction t, String key, long count, String value) {
		if (t != null) {
			t.lrem(key, count, value); return null;
		}
		
		return j.lrem(key, count, value);
	}

	@Override
	public String lset(String key, long index, String value) {
		return (String) executeCommand(CommandEnum.LSET, key, index, value);
	}
	
	private String lset0(Jedis j, Transaction t, String key, long index, String value) {
		if (t != null) {
			t.lset(key, index, value); return null;
		}
		
		return j.lset(key, index, value);
	}

	@Override
	public String ltrim(String key, long start, long stop) {
		return (String) executeCommand(CommandEnum.LTRIM, key, start, stop);
	}
	
	private String ltrim0(Jedis j, Transaction t, String key, long start, long stop) {
		if (t != null) {
			t.ltrim(key, start, stop); return null;
		}
		
		return j.ltrim(key, start, stop);
	}

	@Override
	public String rpop(String key) {
		return (String) executeCommand(CommandEnum.RPOP, key);
	}
	
	private String rpop0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.rpop(key); return null;
		}
		
		return j.rpop(key);
	}

	@Override
	public String rpoplpush(String source, String destination) {
		return (String) executeCommand(CommandEnum.RPOPLPUSH, source, destination);
	}
	
	private String rpoplpush0(Jedis j, Transaction t, String source, String destination) {
		if (t != null) {
			t.rpoplpush(source, destination); return null;
		}
		
		return j.rpoplpush(source, destination);
	}

	@Override
	public Long rpush(String key, String... values) {
		return (Long) executeCommand(CommandEnum.RPUSH, key, values);
	}
	
	private Long rpush0(Jedis j, Transaction t, String key, String... values) {
		if (t != null) {
			t.rpush(key, values); return null;
		}
		
		return j.rpush(key, values);
	}

	@Override
	public Long rpushx(String key, String value) {
		return (Long) executeCommand(CommandEnum.RPUSHX, key, value);
	}
	
	private Long rpushx0(Jedis j, Transaction t, String key, String value) {
		if (t != null) {
			t.rpushx(key, value); return null;
		}
		
		return j.rpushx(key, value);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Sets
	

	@Override
	public Long sadd(String key, String... members) {
		return (Long) executeCommand(CommandEnum.SADD, new Object[] { members });
	}
	
	private Long sadd0(Jedis j, Transaction t, String key, String... members) {
		if (t != null) {
			t.sadd(key, members); return null;
		}
		
		return j.sadd(key, members);
	}

	@Override
	public Long scard(String key) {
		return (Long) executeCommand(CommandEnum.SCARD, key);
	}
	
	private Long scard0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.scard(key); return null;
		}
		
		return j.scard(key);
	}
	
	@Override
	public Set<String> sdiff(String... keys) {
		return (Set<String>) executeCommand(CommandEnum.SDIFF, keys);
	}
	
	private Set<String> sdiff0(Jedis j, Transaction t, String... keys) {
		if (t != null) {
			t.sdiff(keys); return null;
		}
		
		return j.sdiff(keys);
	}

	@Override
	public Long sdiffstore(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.SDIFFSTORE, destination, keys);
	}
	
	private Long sdiffstore0(Jedis j, Transaction t, String destination, String... keys) {
		if (t != null) {
			t.sdiffstore(destination, keys); return null;
		}
		
		return j.sdiffstore(destination, keys);
	}

	@Override
	public Set<String> sinter(String... keys) {
		return (Set<String>) executeCommand(CommandEnum.SINTER, new Object[] { keys });
	}
	
	private Set<String> sinter0(Jedis j, Transaction t, String... keys) {
		if (t != null) {
			t.sinter(keys); return null;
		}
		
		return j.sinter(keys);
	}

	@Override
	public Long sinterstore(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.SINTERSTORE, destination, keys);
	}
	
	private Long sinterstore0(Jedis j, Transaction t, String destination, String... keys) {
		if (t != null) {
			t.sinterstore(destination, keys); return null;
		}
		
		return j.sinterstore(destination, keys);
	}

	@Override
	public Boolean sismember(String key, String member) {
		return (Boolean) executeCommand(CommandEnum.SISMEMBER, key, member);
	}
	
	private Boolean sismember0(Jedis j, Transaction t, String key, String member) {
		if (t != null) {
			t.sismember(key, member); return null;
		}
		
		return j.sismember(key, member);
	}

	@Override
	public Set<String> smembers(String key) {
		return (Set<String>) executeCommand(CommandEnum.SMEMBERS, key);
	}
	
	private Set<String> smembers0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.smembers(key); return null;
		}
		
		return j.smembers(key);
	}
	
	@Override
	public Long smove(String source, String destination, String member) {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public String spop(String key) {
		return (String) executeCommand(CommandEnum.SPOP, key);
	}
	
	private String spop0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.spop(key); return null;
		}
		
		return j.spop(key);
	}

	@Override
	public Set<String> srandmember(String key, int count) {
		return (Set<String>) executeCommand(CommandEnum.SRANDMEMBER_COUNT, key, count);
	}
	
	private Set<String> srandmember0(Jedis j, Transaction t, String key, int count) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public String srandmember(String key) {
		return (String) executeCommand(CommandEnum.SRANDMEMBER_COUNT, key);
	}
	
	private String srandmember0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.srandmember(key); return null;
		}
		
		return j.srandmember(key);
	}

	@Override
	public Long srem(String key, String... members) {
		return (Long) executeCommand(CommandEnum.SREM, key, members);
	}
	
	private Long srem0(Jedis j, Transaction t, String key, String... members) {
		if (t != null) {
//			t.srem(key, members); return null; TODO
		}
		
		return j.srem(key, members);
	}
	
	@Override
	public Set<String> sunion(String... keys) {
		return (Set<String>) executeCommand(CommandEnum.SUNION, new Object[] { keys });
	}
	
	private Set<String> sunion0(Jedis j, Transaction t, String... keys) {
		if (t != null) {
			t.sunion(keys); return null;
		}
		
		return j.sunion(keys);
	}

	@Override
	public Long sunionstore(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.SUNIONSTORE, destination, keys);
	}
	
	private Long sunionstore(Jedis j, Transaction t, String destination, String... keys) {
		if (t != null) {
			t.sunionstore(destination, keys); return null;
		}
		
		return j.sunionstore(destination, keys);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------- Sorted Sets
	
	

	@Override
	public Long zadd(String key, double score, String member) {
		return (Long) executeCommand(CommandEnum.ZADD, key, score, member);
	}
	
	private Long zadd(Jedis j, Transaction t, String key, double score, String member) {
		if (t != null) {
			t.zadd(key, score, member); return null;
		}
		
		return j.zadd(key, score, member);
	}

	@Override
	public Long zcard(String key) {
		return (Long) executeCommand(CommandEnum.ZCARD, key);
	}
	
	private Long zcard0(Jedis j, Transaction t, String key) {
		if (t != null) {
			t.zcard(key); return null;
		}
		
		return j.zcard(key);
	}

	@Override
	public Long zcount(String key, double min, double max) {
		return (Long) executeCommand(CommandEnum.ZCOUNT, key, min, max);
	}
	
	private Long zcount(Jedis j, Transaction t, String key, double min, double max) {
		if (t != null) {
			t.zcount(key, min, max); return null;
		}
		
		return j.zcount(key, min, max);
	}

	@Override
	public Double zincrby(String key, double score, String member) {
		return (Double) executeCommand(CommandEnum.ZINCRBY, key, score, member);
	}
	
	private Double zincrby0(Jedis j, Transaction t, String key, double score, String member) {
		if (t != null) {
			t.zincrby(key, score, member); return null;
		}
		
		return j.zincrby(key, score, member);
	}
	
	@Override
	public Long zinterstore(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.ZINTERSTORE, destination, keys);
	}
	
	private Long zinterstore0(Jedis j, Transaction t, String destination, String... keys) {
		if (t != null) {
			t.zinterstore(destination, keys); return null;
		}
		
		return j.zinterstore(destination, keys);
	}

	@Override
	public Long zinterstoremax(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.ZINTERSTORE_MAX, destination, keys);
	}
	
	private Long zinterstoremax0(Jedis j, Transaction t, String destination, String... keys) {
		if (t != null) {
			t.zinterstore(destination, new ZParams().aggregate(Aggregate.MAX), keys);
		}
		
		return j.zinterstore(destination, new ZParams().aggregate(Aggregate.MAX), keys);
	}

	@Override
	public Long zinterstoremin(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.ZINTERSTORE_MIN, destination, keys);
	}
	
	private Long zinterstoremin0(Jedis j, Transaction t, String destination, String... keys) {
		if (t != null) {
			t.zinterstore(destination, new ZParams().aggregate(Aggregate.MIN), keys);
		}
		
		return j.zinterstore(destination, new ZParams().aggregate(Aggregate.MIN), keys);
	}

	@Override
	public Long zinterstore(String destination, Map<String, Integer> weightkeys) {
		return (Long) executeCommand(CommandEnum.ZINTERSTORE_WEIGHTS, destination, weightkeys);
	}
	
	private Long zinterstore0(Jedis j, Transaction t, String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
		
		if (t != null) {
			t.zinterstore(destination, new ZParams().weights(weights), keys); return null;
		}
		
		return j.zinterstore(destination, new ZParams().weights(weights), keys);
	}
	
	private Object[] convert4zstore(Map<String, Integer> weightkeys) {
		int size = weightkeys.size();
		String[] keys = new String[size];
		int[] weights = new int[size];
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(weightkeys.entrySet());
		for (int i = 0; i < size; i++) {
			Entry<String, Integer> entry = list.get(i);
			keys[i] = entry.getKey();
			weights[i] = entry.getValue();
		}
		
		return new Object[] { keys, weights };
	}

	@Override
	public Long zinterstoremax(String destination, Map<String, Integer> weightkeys) {
		return (Long) executeCommand(CommandEnum.ZINTERSTORE_WEIGHTS_MAX, destination, weightkeys);
	}
	
	private Long zinterstoremax0(Jedis j, Transaction t, String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
		
		if (t != null) {
			t.zinterstore(destination, new ZParams().weights(weights).aggregate(Aggregate.MAX), keys); return null;
		}
		
		return j.zinterstore(destination, new ZParams().weights(weights).aggregate(Aggregate.MAX), keys);
	}

	@Override
	public Long zinterstoremin(String destination, Map<String, Integer> weightkeys) {
		return (Long) executeCommand(CommandEnum.ZINTERSTORE_WEIGHTS_MIN, destination, weightkeys);
	}
	
	private Long zinterstoremin0(Jedis j, Transaction t, String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
		
		if (t != null) {
			t.zinterstore(destination, new ZParams().weights(weights).aggregate(Aggregate.MIN), keys); return null;
		}
		
		return j.zinterstore(destination, new ZParams().weights(weights).aggregate(Aggregate.MIN), keys);
	}

	@Override
	public Set<String> zrange(String key, long start, long stop) {
		return (Set<String>) executeCommand(CommandEnum.ZRANGE, key, start, stop);
	}
	
	private Set<String> zrange0(Jedis j, Transaction t, String key, long start, long stop) {
		if (t != null) {
//			t.zrange(key, start, end) return null; TODO
		}
		
		return j.zrange(key, start, stop);
	}

	@Override
	public Map<String, Double> zrangewithscores(String key, long start, long stop) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZRANGE_WITHSCORES, key, start, stop);
	}
	
	private Map<String, Double> zrangewithscores0(Jedis j, Transaction t, String key, long start, long stop) {
		if (t != null) {
//			t.zrangeWithScores(key, start, stop); return null;  TODO
		}
		
		Set<Tuple> set = j.zrangeWithScores(key, start, stop);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}
	
	private Map<String, Double> convert4zrangewithscores(Set<Tuple> set) {
		Map<String, Double> map = new LinkedHashMap<String, Double>(set.size());
		for (Tuple tuple : set) {
			map.put(tuple.getElement(), tuple.getScore());
		}
		return map;
	}

	@Override
	public Set<String> zrangebyscore(String key, double min, double max) {
		return (Set<String>) executeCommand(CommandEnum.ZRANGEBYSCORE, key, min, max);
	}
	
	private Set<String> zrangebyscore0(Jedis j, Transaction t, String key, double min, double max) {
		if (t != null) {
			t.zrangeByScore(key, min, max); return null;
		}
		
		return j.zrangeByScore(key, min, max);
	}

	@Override
	public Set<String> zrangebyscore(String key, String min, String max) {
		return (Set<String>) executeCommand(CommandEnum.ZRANGEBYSCORE_STRING, key, min, max);
	}
	
	private Set<String> zrangebyscore0(Jedis j, Transaction t, String key, String min, String max) {
		if (t != null) {
			t.zrangeByScore(key, min, max); return null;
		}
		
		return j.zrangeByScore(key, min, max);
	}

	@Override
	public Set<String> zrangebyscore(String key, double min, double max, int offset, int count) {
		return (Set<String>) executeCommand(CommandEnum.ZRANGEBYSCORE_OFFSET_COUNT, key, min, max, offset, count);
	}
	
	private Set<String> zrangebyscore0(Jedis j, Transaction t, String key, double min, double max, int offset, int count) {
		if (t != null) {
			t.zrangeByScore(key, min, max, offset, count); return null;
		}
		
		return j.zrangeByScore(key, min, max, offset, count);
	}

	@Override
	public Set<String> zrangebyscore(String key, String min, String max, int offset, int count) {
		return (Set<String>) executeCommand(CommandEnum.ZRANGEBYSCORE_OFFSET_COUNT_STRING, key, min, max, offset, count);
	}
	
	private Set<String> zrangebyscore0(Jedis j, Transaction t, String key, String min, String max, int offset, int count) {
		if (t != null) {
//			t.zrangeByScore(key, min, max, offset, count); return null; TODO
		}
		
		return j.zrangeByScore(key, min, max, offset, count);
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String key, double min, double max) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZRANGEBYSCORE_WITHSCORES, key, min, max);
	}
	
	private Map<String, Double> zrangebyscorewithscores0(Jedis j, Transaction t, String key, double min, double max) {
		if (t != null) {
			// TODO
		}
		
		Set<Tuple> set = j.zrangeByScoreWithScores(key, min, max);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String key, String min, String max) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZRANGEBYSCORE_WITHSCORES_STRING, key, min, max);
	}
	
	private Map<String, Double> zrangebyscorewithscores0(Jedis j, Transaction t, String key, String min, String max) {
		if (t != null) {
			// TODO
		}
		
		Set<Tuple> set = j.zrangeByScoreWithScores(key, min, max);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String key, double min, double max, int offset, int count) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZRANGEBYSCORE_WITHSCORES_OFFSET_COUNT, key, min, max);
	}
	
	private Map<String, Double> zrangebyscorewithscores0(Jedis j, Transaction t, String key, double min, double max, int offset, int count) {
		if (t != null) {
			// TODO
		}
		
		Set<Tuple> set = j.zrangeByScoreWithScores(key, min, max, offset, count);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String key, String min, String max, int offset, int count) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZRANGEBYSCORE_WITHSCORES_OFFSET_COUNT_STRING, key, min, max);
	}
	
	private Map<String, Double> zrangebyscorewithscores0(Jedis j, Transaction t, String key, String min, String max, int offset, int count) {
		if (t != null) {
			// TODO
		}
		
		Set<Tuple> set = j.zrangeByScoreWithScores(key, min, max, offset, count);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Long zrank(String key, String member) {
		return (Long) executeCommand(CommandEnum.ZRANK, key, member);
	}
	
	private Long zrank0(Jedis j, Transaction t, String key, String member) {
		if (t != null) {
			t.zrank(key, member); return null;
		}
		
		return j.zrank(key, member);
	}

	@Override
	public Long zrem(String key, String... members) {
		return (Long) executeCommand(CommandEnum.ZREM, key, members);
	}
	
	private Long zrem0(Jedis j, Transaction t, String key, String... members) {
		if (t != null) {
			// TODO
		}
		
		return j.zrem(key, members);
	}

	@Override
	public Long zremrangebyrank(String key, long start, long stop) {
		return (Long) executeCommand(CommandEnum.ZREMRANGEBYRANK, key, start, stop);
	}
	
	private Long zremrangebyrank0(Jedis j, Transaction t, String key, long start, long stop) {
		if (t != null) {
//			t.zremrangeByRank(key, start, stop); return null; TODO
		}
		
		return j.zremrangeByRank(key, start, stop);
	}

	@Override
	public Long zremrangebyscore(String key, double min, double max) {
		return (Long) executeCommand(CommandEnum.ZREMRANGEBYSCORE, key, min, max);
	}
	
	private Long zremrangebyscore0(Jedis j, Transaction t, String key, double min, double max) {
		if (t != null) {
			t.zremrangeByScore(key, min, max); return null;
		}
		
		return j.zremrangeByScore(key, min, max);
	}

	@Override
	public Long zremrangebyscore(String key, String min, String max) {
		return (Long) executeCommand(CommandEnum.ZREMRANGEBYSCORE_STRING, key, min, max);
	}
	
	private Long zremrangebyscore0(Jedis j, Transaction t, String key, String min, String max) {
		if (t != null) {
//			t.zremrangeByScore(key, min, max); return null; TODO
		}
		
		return j.zremrangeByScore(key, min, max);
	}

	@Override
	public Set<String> zrevrange(String key, long start, long stop) {
		return (Set<String>) executeCommand(CommandEnum.ZREVRANGE, key, start, stop);
	}
	
	private Set<String> zrevrange(Jedis j, Transaction t, String key, long start, long stop) {
		if (t != null) {
//			t.zrevrange(key, start, stop); return null; TODO
		}
		
		return j.zrevrange(key, start, stop);
	}

	@Override
	public Map<String, Double> zrerangewithscores(String key, long start, long stop) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZREVRANGE_WITHSCORES, key, start, stop);
	}
	
	private Map<String, Double> zrerangewithscores0(Jedis j, Transaction t, String key, long start, long stop) {
		if (t != null) {
			// TODO
		}
		
		Set<Tuple> set = j.zrevrangeWithScores(key, start, stop);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Set<String> zrevrangebyscore(String key, double max, double min) {
		return (Set<String>) executeCommand(CommandEnum.ZREVRANGEBYSCORE, key, max, min);
	}
	
	private Set<String> zrevrangebyscore0(Jedis j, Transaction t, String key, double max, double min) {
		if (t != null) {
			t.zrevrangeByScore(key, max, min); return null;
		}
		
		return j.zrevrangeByScore(key, max, min);
	}

	@Override
	public Set<String> zrevrangebyscore(String key, String max, String min) {
		return (Set<String>) executeCommand(CommandEnum.ZREVRANGEBYSCORE_STRING, key, max, min);
	}
	
	private Set<String> zrevrangebyscore(Jedis j, Transaction t, String key, String max, String min) {
		if (t != null) {
			t.zrevrangeByScore(key, max, min); return null;
		}
		
		return j.zrevrangeByScore(key, max, min);
	}

	@Override
	public Set<String> zrevrangebyscore(String key, double max, double min, int offset, int count) {
		return (Set<String>) executeCommand(CommandEnum.ZREVRANGEBYSCORE_OFFSET_COUNT, key, max, min, offset, count);
	}
	
	private Set<String> zrevrangebyscore(Jedis j, Transaction t, String key, double max, double min, int offset, int count) {
		if (t != null) {
			t.zrevrangeByScore(key, max, min, offset, count); return null;
		}
		
		return j.zrevrangeByScore(key, max, min, offset, count);
	}

	@Override
	public Set<String> zrevrangebyscore(String key, String max, String min, int offset, int count) {
		return (Set<String>) executeCommand(CommandEnum.ZREVRANGEBYSCORE_OFFSET_COUNT_STRING, key, max, min, offset, count);
	}
	
	private Set<String> zrevrangebyscore(Jedis j, Transaction t, String key, String max, String min, int offset, int count) {
		if (t != null) {
//			t.zrevrangeByScore(key, max, min, offset, count); return null; TODO
		}
		
		return j.zrevrangeByScore(key, max, min, offset, count);
	}
	
	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String key, double min, double max) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZREVRANGEBYSCORE_WITHSCORES, key, min, max);
	}
	
	private Map<String, Double> zrevrangebyscorewithscores0(Jedis j, Transaction t, String key, double min, double max) {
		if (t != null) {
			// TODO
		}
		
		Set<Tuple> set = j.zrevrangeByScoreWithScores(key, min, max);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String key, String min, String max) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZREVRANGEBYSCORE_WITHSCORES_STRING, key, min, max);
	}
	
	private Map<String, Double> zrevrangebyscorewithscores0(Jedis j, Transaction t, String key, String min, String max) {
		if (t != null) {
			// TODO
		}
		
		Set<Tuple> set = j.zrangeByScoreWithScores(key, min, max);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String key, double min, double max, int offset, int count) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZREVRANGEBYSCORE_WITHSCORES_OFFSET_COUNT, key, min, max);
	}
	
	private Map<String, Double> zrevrangebyscorewithscores0(Jedis j, Transaction t, String key, double min, double max, int offset, int count) {
		if (t != null) {
			// TODO
		}
		
		Set<Tuple> set = j.zrevrangeByScoreWithScores(key, min, max, offset, count);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String key, String min, String max, int offset, int count) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZREVRANGEBYSCORE_WITHSCORES_OFFSET_COUNT_STRING, key, min, max);
	}
	
	private Map<String, Double> zrevrangebyscorewithscores0(Jedis j, Transaction t, String key, String min, String max, int offset, int count) {
		if (t != null) {
			// TODO
		}
		
		Set<Tuple> set = j.zrevrangeByScoreWithScores(key, min, max, offset, count);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Long zrevrank(String key, String member) {
		return (Long) executeCommand(CommandEnum.ZREVRANK, key, member);
	}
	
	private Long zrerank0(Jedis j, Transaction t, String key, String member) {
		if (t != null) {
			t.zrevrank(key, member); return null;
		}
		
		return j.zrevrank(key, member);
	}

	@Override
	public Double zscore(String key, String member) {
		return (Double) executeCommand(CommandEnum.ZSCORE, key, member);
	}
	
	private Double zscore0(Jedis j, Transaction t, String key, String member) {
		if (t != null) {
			t.zscore(key, member); return null;
		}
		
		return j.zscore(key, member);
	}
	
	@Override
	public Long zunionstore(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.ZUNIONSTORE, destination, keys);
	}
	
	private Long zunionstore0(Jedis j, Transaction t, String destination, String... keys) {
		if (t != null) {
			t.zunionstore(destination, keys); return null;
		}
		
		return j.zunionstore(destination, keys);
	}

	@Override
	public Long zunionstoremax(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.ZUNIONSTORE_MAX, destination, keys);
	}
	
	private Long zunionstoremax0(Jedis j, Transaction t, String destination, String... keys) {
		if (t != null) {
			t.zunionstore(destination, new ZParams().aggregate(Aggregate.MAX), keys);
		}
		
		return j.zunionstore(destination, new ZParams().aggregate(Aggregate.MAX), keys);
	}

	@Override
	public Long zunionstoremin(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.ZUNIONSTORE_MIN, destination, keys);
	}
	
	private Long zunionstoremin0(Jedis j, Transaction t, String destination, String... keys) {
		if (t != null) {
			t.zunionstore(destination, new ZParams().aggregate(Aggregate.MIN), keys);
		}
		
		return j.zunionstore(destination, new ZParams().aggregate(Aggregate.MIN), keys);
	}

	@Override
	public Long zunionstore(String destination, Map<String, Integer> weightkeys) {
		return (Long) executeCommand(CommandEnum.ZUNIONSTORE_WEIGHTS, destination, weightkeys);
	}
	
	private Long zunionstore0(Jedis j, Transaction t, String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
		
		if (t != null) {
			t.zunionstore(destination, new ZParams().weights(weights), keys); return null;
		}
		
		return j.zunionstore(destination, new ZParams().weights(weights), keys);
	}

	@Override
	public Long zunionstoremax(String destination, Map<String, Integer> weightkeys) {
		return (Long) executeCommand(CommandEnum.ZUNIONSTORE_WEIGHTS_MAX, destination, weightkeys);
	}
	
	private Long zunionstoremax0(Jedis j, Transaction t, String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
		
		if (t != null) {
			t.zunionstore(destination, new ZParams().weights(weights).aggregate(Aggregate.MAX), keys); return null;
		}
		
		return j.zunionstore(destination, new ZParams().weights(weights).aggregate(Aggregate.MAX), keys);
	}

	@Override
	public Long zunionstoremin(String destination, Map<String, Integer> weightkeys) {
		return (Long) executeCommand(CommandEnum.ZUNIONSTORE_WEIGHTS_MIN, destination, weightkeys);
	}
	
	private Long zunionstoremin0(Jedis j, Transaction t, String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
		
		if (t != null) {
			t.zunionstore(destination, new ZParams().weights(weights).aggregate(Aggregate.MIN), keys); return null;
		}
		
		return j.zunionstore(destination, new ZParams().weights(weights).aggregate(Aggregate.MIN), keys);
	}
	
	
	// ~ ----------------------------------------------------------------------------------------------------- Pub/Sub
	
	
	@Override
	public void psubscribe(RedisPsubscribeHandler handler, String pattern) {
		psubscribe(handler, new String[] { pattern });
	}
	
	@Override
	public void psubscribe(RedisPsubscribeHandler handler, String... patterns) {
		executeCommand(CommandEnum.PSUBSCRIBE, handler, patterns);
	}
	
	private void psubscribe0(Jedis j, Transaction t, final RedisPsubscribeHandler handler, String... patterns) {
		if (t != null) {
			throw new RedisOperationException(String.format(TRANSACTION_UNSUPPORTED, "PSUBSCRIBE"));
		}
		
		JedisPubSub jps = new JedisPubSubAdapter() {
			@Override
			public void onPSubscribe(String pattern, int subscribedChannels) {
				handler.onPsubscribe(pattern, subscribedChannels);
			}
			
			@Override
			public void onPMessage(String pattern, String channel, String message) {
				handler.onMessage(pattern, channel, message);
			}
		};
		j.psubscribe(jps, patterns);
	}

	@Override
	public Long publish(String channel, String message) {
		return (Long) executeCommand(CommandEnum.PUBLISH, channel, message);
	}
	
	private Long publish0(Jedis j, Transaction t, String channel, String message) {
		if (t != null) {
			t.publish(channel, message); return null;
		}
		
		return j.publish(channel, message);
	}
	
	@Override
	public String punsubscribe(String pattern) {
		List<String> l = punsubscribe(new String[] { pattern });
		if (l.size() > 0) {
			return l.get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public List<String> punsubscribe(String... patterns) {
		return (List<String>) executeCommand(CommandEnum.PUNSUBSCRIBE, patterns);
	}
	
	private List<String> punsubscribe0(Jedis j, Transaction t, String... patterns) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public void subscribe(RedisSubscribeHandler handler, String channel) {
		subscribe(handler, new String[] { channel });
	}
	
	@Override
	public void subscribe(RedisSubscribeHandler handler, String... channels) {
		executeCommand(CommandEnum.SUBSCRIBE, handler, channels);
	}
	
	private void subscribe0(Jedis j, Transaction t, final RedisSubscribeHandler handler, String... channels) {
		if (t != null) {
			throw new RedisOperationException(String.format(TRANSACTION_UNSUPPORTED, "SUBSCRIBE"));
		}
		
		JedisPubSub jps = new JedisPubSubAdapter() {

			@Override
			public void onMessage(String channel, String message) {
				handler.onMessage(channel, message);
			}

			@Override
			public void onSubscribe(String channel, int subscribedChannels) {
				handler.onSubscribe(channel, subscribedChannels);
			}
			
		};
		j.subscribe(jps, channels);
	}

	@Override
	public String unsubscribe(String channel) {
		List<String> l = unsubscribe(new String[] { channel });
		if (l.size() > 0) {
			return l.get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public List<String> unsubscribe(String... channels) {
		return (List<String>) executeCommand(CommandEnum.UNSUBSCRIBE, channels);
	}
	
	private List<String> unsubscribe0(Jedis j, Transaction t, String... channels) {
		if (t != null) {
			// TODO
		}
		
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
		return (String) executeCommand(CommandEnum.DISCARD, new Object[] {});
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
		return (List<Object>) executeCommand(CommandEnum.EXEC, new Object[] {});
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
		return (String) executeCommand(CommandEnum.MULTI, new Object[] {});
	}
	
	private String multi0(Jedis j) {
		Transaction t = j.multi();
		setTransaction(t, j);
		return OK;
	}

	@Override
	public String unwatch() {
		return (String) executeCommand(CommandEnum.UNWATCH, new Object[] {});
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
		return (String) executeCommand(CommandEnum.WATCH, new Object[] { keys });
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
	public Object eval(String script) {
		List<String> el = Collections.emptyList();
		return eval(script, el, el);
	}

	@Override
	public Object eval(String script, List<String> keys) {
		return eval(script, keys, new ArrayList<String>(0));
	}

	@Override
	public Object eval(String script, List<String> keys, List<String> args) {
		return executeCommand(CommandEnum.EVAL, script, keys, args);
	}
	
	private Object eval0(Jedis j, Transaction t, String script, List<String> keys, List<String> args) {
		if (t != null) {
			// TODO
		}
		
		return j.eval(script, keys, args);
	}

	@Override
	public Object evalsha(String sha1) {
		List<String> el = Collections.emptyList();
		return evalsha(sha1, el, el);
	}

	@Override
	public Object evalsha(String sha1, List<String> keys) {
		return evalsha(sha1, keys, new ArrayList<String>(0));
	}

	@Override
	public Object evalsha(String sha1, List<String> keys, List<String> args) {
		return executeCommand(CommandEnum.EVALSHA, sha1, keys, args);
	}
	
	private Object evalsha0(Jedis j, Transaction t, String script, List<String> keys, List<String> args) {
		if (t != null) {
			// TODO
		}
		
		return j.evalsha(script, keys, args);
	}

	@Override
	public Boolean scriptexists(String sha1) {
		return scriptexists(new String[] { sha1 })[0];
	}
	
	@Override
	public Boolean[] scriptexists(String... sha1) {
		return (Boolean[]) executeCommand(CommandEnum.SCRIPT_EXISTS, sha1);
	}
	
	private Boolean[] scriptexists(Jedis j, Transaction t, String... sha1) {
		if (t != null) {
			// TODO
		}
		
		return j.scriptExists(sha1).toArray(new Boolean[sha1.length]);
	}

	@Override
	public String scriptflush() {
		return (String) executeCommand(CommandEnum.SCRIPT_FLUSH);
	}
	
	private String scriptflush0(Jedis j, Transaction t) {
		if (t != null) {
			// TODO
		}
		
		return j.scriptFlush();
	}

	@Override
	public String scriptkill() {
		return (String) executeCommand(CommandEnum.SCRIPT_KILL);
	}
	
	private String scriptkill0(Jedis j, Transaction t) {
		if (t != null) {
			// TODO
		}
		
		return j.scriptKill();
	}
	
	@Override
	public String scriptload(String script) {
		return (String) executeCommand(CommandEnum.SCRIPT_LOAD);
	}
	
	private String scriptload0(Jedis j, Transaction t, String script) {
		if (t != null) {
			// TODO
		}
		
		return j.scriptLoad(script);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------- Connection
	

	@Override
	public String auth(String password) {
		return (String) executeCommand(CommandEnum.AUTH, password);
	}
	
	private String auth0(Jedis j, Transaction t, String password) {
		if (t != null) {
			// TODO
		}
		
		return j.auth(password);
	}

	@Override
	public String echo(String message) {
		return (String) executeCommand(CommandEnum.ECHO, message);
	}
	
	private String echo0(Jedis j, Transaction t, String message) {
		if (t != null) {
			t.echo(message); return null;
		}
		
		return j.echo(message);
	}

	@Override
	public String ping() {
		return (String) executeCommand(CommandEnum.PING);
	}
	
	private String ping0(Jedis j, Transaction t) {
		if (t != null) {
			t.ping(); return null;
		}
		
		return j.ping();
	}

	@Override
	public String quit() {
		return (String) executeCommand(CommandEnum.QUIT);
	}
	
	private String quit0(Jedis j, Transaction t) {
		if (t != null) {
			throw new RedisOperationException(String.format(TRANSACTION_UNSUPPORTED, "QUIT"));
		}
		
		return j.quit();
	}

	@Override
	public String select(int index) {
		return (String) executeCommand(CommandEnum.SELECT, index);
	}
	
	private String select(Jedis j, Transaction t, int index) {
		if (t != null) {
			t.select(index); return null;
		}
		
		return j.select(index);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Server
	

	@Override
	public String bgrewriteaof() {
		return (String) executeCommand(CommandEnum.BGREWRITEAOF);
	}
	
	private String bgrewriteaof0(Jedis j, Transaction t) {
		if (t != null) {
			t.bgrewriteaof(); return null;
		}
		
		return j.bgrewriteaof();
	}

	@Override
	public String bgsave() {
		return (String) executeCommand(CommandEnum.BGSAVE);
	}
	
	private String bgsave0(Jedis j, Transaction t) {
		if (t != null) {
			t.bgsave(); return null;
		}
		
		return j.bgsave();
	}

	@Override
	public String clientgetname() {
		return (String) executeCommand(CommandEnum.CLIENT_GETNAME);
	}
	
	private String clientgetname0(Jedis j, Transaction t) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public String clientkill(String ip, int port) {
		return (String) executeCommand(CommandEnum.CLIENT_KILL, ip, port);
	}
	
	private String clientkill0(Jedis j, Transaction t, String ip, int port) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public List<String> clientlist() {
		return (List<String>) executeCommand(CommandEnum.CLIENT_LIST);
	}
	
	private List<String> clientlist0(Jedis j, Transaction t) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public String clientsetname(String connectionname) {
		return (String) executeCommand(CommandEnum.CLIENT_SETNAME, connectionname);
	}
	
	private String clientsetname0(Jedis j, Transaction t, String connectionname) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public List<String> configget(String parameter) {
		return (List<String>) executeCommand(CommandEnum.CONFIG_GET, parameter);
	}
	
	private List<String> configget0(Jedis j, Transaction t, String parameter) {
		if (t != null) {
			t.configGet(parameter); return null;
		}
		
		return j.configGet(parameter);
	}

	@Override
	public String configresetstat() {
		return (String) executeCommand(CommandEnum.CONFIG_RESETSTAT);
	}
	
	private String configresetstat0(Jedis j, Transaction t) {
		if (t != null) {
			t.configResetStat(); return null;
		}
		
		return j.configResetStat();
	}

	@Override
	public String configset(String parameter, String value) {
		return (String) executeCommand(CommandEnum.CONFIG_SET, parameter, value);
	}
	
	private String configset0(Jedis j, Transaction t, String parameter, String value) {
		if (t != null) {
			t.configSet(parameter, value); return null;
		}
		
		return j.configSet(parameter, value);
	}

	@Override
	public Long dbsize() {
		return (Long) executeCommand(CommandEnum.DBSIZE);
	}
	
	private Long dbsize0(Jedis j, Transaction t) {
		if (t != null) {
			t.dbSize(); return null;
		}
		
		return j.dbSize();
	}
	
	@Override
	public String debugobject(String key) {
		return debug(DebugParams.OBJECT(key));
	}

	@Override
	public String debugsegfault() {
		return debug(DebugParams.SEGFAULT());
	}
	
	private String debug(DebugParams params) {
		return (String) executeCommand(CommandEnum.DEBUG_OBJECT, params);
	}
	
	private String debug0(Jedis j, Transaction t, DebugParams params) {
		if (t != null) {
			// TODO
		}
		
		return j.debug(params);
	}

	@Override
	public String flushall() {
		return (String) executeCommand(CommandEnum.FLUSH_ALL);
	}
	
	private String flushall0(Jedis j, Transaction t) {
		if (t != null) {
			t.flushAll(); return null;
		}
		
		return j.flushAll();
	}

	@Override
	public String flushdb() {
		return (String) executeCommand(CommandEnum.FLUSH_DB);
	}
	
	private String flushdb0(Jedis j, Transaction t) {
		if (t != null) {
			t.flushDB(); return null;
		}
		
		return j.flushDB();
	}

	@Override
	public String info() {
		return info(null);
	}

	@Override
	public String info(String section) {
		return (String) executeCommand(CommandEnum.INFO, section);
	}
	
	private String info0(Jedis j, Transaction t, String section) {
		if (t != null) {
			// TODO
		}
		
		if (section == null) {
			return j.info();
		} else {
			return j.info(section);
		}
	}

	@Override
	public Long lastsave() {
		return (Long) executeCommand(CommandEnum.LAST_SAVE);
	}
	
	private Long lastsave0(Jedis j, Transaction t) {
		if (t != null) {
			t.lastsave(); return null;
		}
		
		return j.lastsave();
	}

	@Override
	public void monitor(RedisMonitorHandler handler) {
		executeCommand(CommandEnum.MONITOR, handler);
	}
	
	private void monitor0(Jedis j, Transaction t, final RedisMonitorHandler handler) {
		if (t != null) {
			throw new RedisOperationException(String.format(TRANSACTION_UNSUPPORTED, "MONITOR"));
		}
		
		JedisMonitor jm = new JedisMonitor() {
			@Override
			public void onCommand(String command) {
				handler.onCommand(command);
			}
			
		};
		j.monitor(jm);
	}

	@Override
	public String save() {
		return (String) executeCommand(CommandEnum.SAVE);
	}
	
	private String save0(Jedis j, Transaction t) {
		if (t != null) {
			t.save(); return null;
		}
		
		return j.save();
	}

	@Override
	public String shutdown(boolean save) {
		return (String) executeCommand(CommandEnum.SHUTDOWN, save);
	}
	
	private String shutdown0(Jedis j, Transaction t, boolean save) {
		if (t != null) {
			t.shutdown(); return null;
		}
		
		return j.shutdown();
	}

	@Override
	public String slaveof(String host, int port) {
		return (String) executeCommand(CommandEnum.SLAVEOF, host, port);
	}
	
	private String slaveof0(Jedis j, Transaction t, String host, int port) {
		if (t != null) {
			// TODO
		}
		
		return j.slaveof(host, port);
	}

	@Override
	public String slaveofnoone() {
		return (String) executeCommand(CommandEnum.SLAVEOF_NONOE);
	}
	
	private String slaveofnoone(Jedis j, Transaction t) {
		if (t != null) {
			// TODO
		}
		
		return j.slaveofNoOne();
	}

	@Override
	public List<Slowlog> slowlogget() {
		return (List<Slowlog>) executeCommand(CommandEnum.SLOWLOG_GET);
	}
	
	private List<Slowlog> slowlogget0(Jedis j, Transaction t) {
		if (t != null) {
			// TODO
		}
		
		List<redis.clients.util.Slowlog> logs = j.slowlogGet();
		return convert4slowlog(logs);
	}
	
	private List<Slowlog> convert4slowlog(List<redis.clients.util.Slowlog> logs) {
		List<Slowlog> slist = new ArrayList<Slowlog>(logs.size());
		for (redis.clients.util.Slowlog log : logs) {
			Slowlog sl = new Slowlog(log.getId(), log.getTimeStamp(), log.getExecutionTime(), log.getArgs());
			slist.add(sl);
		}
		
		return slist;
	}

	@Override
	public List<Slowlog> slowlogget(long len) {
		return (List<Slowlog>) executeCommand(CommandEnum.SLOWLOG_GET_LEN);
	}
	
	private List<Slowlog> slowlogget0(Jedis j, Transaction t, long len) {
		if (t != null) {
			// TODO
		}
		
		List<redis.clients.util.Slowlog> logs = j.slowlogGet(len);
		return convert4slowlog(logs);
	}

	@Override
	public String slowlogreset() {
		return (String) executeCommand(CommandEnum.SLOWLOG_RESET);
	}
	
	private String slowlogreset0(Jedis j, Transaction t) {
		if (t != null) {
			// TODO
		}
		
		return j.slowlogReset();
	}

	@Override
	public Long slowloglen() {
		return (Long) executeCommand(CommandEnum.SLOWLOG_LEN);
	}
	
	private Long slowloglen0(Jedis j, Transaction t) {
		if (t != null) {
			// TODO
		}
		
		return j.slowlogLen();
	}

	@Override
	public void sync() {
		executeCommand(CommandEnum.SYNC);
	}
	
	private void sync0(Jedis j, Transaction t) {
		if (t != null) {
			// TODO
		}
		
		j.sync();
	}

	@Override
	public Long time() {
		return (Long) executeCommand(CommandEnum.TIME);
	}
	
	private Long time0(Jedis j, Transaction t) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}

	@Override
	public Long microtime() {
		return (Long) executeCommand(CommandEnum.TIME_MICRO);
	}
	
	private Long microtime0(Jedis j, Transaction t) {
		if (t != null) {
			// TODO
		}
		
		return null;
	}
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	private Object executeCommand(CommandEnum cmd, Object... args) {
		Jedis j = jedis();
		Transaction t = transaction();
		
		try {
			switch (cmd) {
			// Keys
			case DEL:
				return del0(j, t, (String[]) args[0]);
			case DUMP:         
				return dump0(j, t, (String) args[0]);   
			case EXISTS:
				return exists0(j, t, (String) args[0]);
			case EXPIRE:
				return expire0(j, t, (String) args[0], (Integer) args[1]);
			case EXPIREAT:
				return expireat0(j, t, (String) args[0], (Long) args[1]);
			case KEYS:
				return keys0(j, t, (String) args[0]);
			case MIGRATE: 
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
			case PEXPIRE: 
				return pexpire0(j, t, (String) args[0], (Integer) args[1]);
			case PEXPIREAT: 
				return pexpireat0(j, t, (String) args[0], (Long) args[1]);
			case PTTL: 
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
				return sort_desc(j, t, (String) args[0], (Boolean) args[1]);
			case SORT_ALPHA_DESC:
				return sort_alpha_desc(j, t, (String) args[0], (Boolean) args[1], (Boolean) args[2]);
			case SORT_OFFSET_COUNT:
				return sort_offset_count(j, t, (String) args[0], (Integer) args[1], (Integer) args[2]);
			case SORT_OFFSET_COUNT_ALPHA_DESC:
				return sort_offset_count_alpha_desc(j, t, (String) args[0], (Integer) args[1], (Integer) args[2], (Boolean) args[3], (Boolean) args[4]);
			case SORT_BY_GET:
				return sort_by_get(j, t, (String) args[0], (String) args[1], (String[]) args[2]);
			case SORT_BY_DESC_GET:
				return sort_by_desc_get(j, t, (String) args[0], (String) args[1], (Boolean) args[2], (String[]) args[3]);
			case SORT_BY_ALPHA_DESC_GET:
				return sort_by_alpha_desc_get(j, t, (String) args[0], (String) args[1], (Boolean) args[2], (Boolean) args[3], (String[]) args[4]);
			case SORT_BY_OFFSET_COUNT_GET:
				return sort_by_offset_count_get(j, t, (String) args[0], (String) args[1], (Integer) args[2], (Integer) args[3], (String[]) args[4]);
			case SORT_BY_OFFSET_COUNT_ALPHA_DESC_GET:
				return sort_by_offset_count_alpha_desc_get(j, t, (String) args[0], (String) args[1], (Integer) args[2], (Integer) args[3], (Boolean) args[4], (Boolean) args[5], (String[]) args[6]);
			case SORT_BY_DESTINATION:
				return sort_by_destination(j, t, (String) args[0], (String) args[1], (String) args[2]);
			case SORT_BY_DESC_DESTINATION_GET:
				return sort_by_desc_destination_get(j, t, (String) args[0], (String) args[1], (Boolean) args[2], (String) args[3], (String[]) args[4]);
			case SORT_BY_ALPHA_DESC_DESTINATION_GET:
				return sort_by_alpha_desc_destination_get(j, t, (String) args[0], (String) args[1], (Boolean) args[2], (Boolean) args[3], (String) args[4], (String[]) args[5]);
			case SORT_BY_OFFSET_COUNT_DESTINATION_GET:
				return sort_by_offset_count_destination_get(j, t, (String) args[0], (String) args[1], (Integer) args[2], (Integer) args[3], (String) args[4], (String[]) args[5]);
			case SORT_BY_OFFSET_COUNT_ALPHA_DESC_DESTINATION_GET:				
				return sort_by_offset_count_alpha_desc_destination_get(j, t, (String) args[0], (String) args[1], (Integer) args[2], (Integer) args[3], (Boolean) args[4], (Boolean) args[5], (String) args[6], (String[]) args[7]);
			case TTL:
				return ttl0(j, t, (String) args[0]);
			case TYPE:
				return type0(j, t, (String) args[0]);
				
			// Strings
				
			case APPEND:
				return append0(j, t, (String) args[0], (String) args[1]);
			case BITCOUNT:
				return bitcount0(j, t, (String) args[0]);
			case BITNOT:
				return bitnot0(j, t, (String) args[0], (String) args[1]);
			case BITAND:
				return bitand0(j, t, (String) args[0], (String) args[1]);
			case BITOR:
				return bitor0(j, t, (String) args[0], (String) args[1]);
			case BITXOR:
				return bitxor0(j, t, (String) args[0], (String) args[1]);
			case DECR:
				return decr0(j, t, (String) args[0]);
			case DECRBY:
				return decrby0(j, t, (String) args[0], (Long) args[1]);
			case GET:
				return get0(j, t, (String) args[0]);
			case GETBIT:
				return getbit0(j, t, (String) args[0], (Long) args[1]);
			case GETRANGE:
				return getrange0(j, t, (String) args[0], (Long) args[1], (Long) args[2]);
			case GETSET:
				return getset0(j, t, (String) args[0], (String) args[1]);
			case INCR:
				return incr0(j, t, (String) args[0]);
			case INCRBY:
				return incrby0(j, t, (String) args[0], (Long) args[1]);
			case INCRBYFLOAT:
				return incrbyfloat(j, t, (String) args[0], (Long) args[1]);
			case MGET:
				return mget0(j, t, (String[]) args[0]);
			case MSET:
				return mset0(j, t, (String[]) args[0]);
			case MSETNX:
				return msetnx0(j, t, (String[]) args[0]);
			case PSETEX:
				return psetex0(j, t, (String) args[0], (Integer) args[1], (String) args[2]);
			case SET:
				return set0(j, t, (String) args[0], (String) args[1]);
			case SETXX:
				return setxx0(j, t, (String) args[0], (String) args[1]);
			case SETNXEX:
				return setnxex0(j, t, (String) args[0], (String) args[1], (Integer) args[2]);
			case SETNXPX:
				return setnxpx0(j, t, (String) args[0], (String) args[1], (Integer) args[2]);
			case SETXXEX:
				return setxxex0(j, t, (String) args[0], (String) args[1], (Integer) args[2]);
			case SETXXPX:
				return setxxpx0(j, t, (String) args[0], (String) args[1], (Integer) args[2]);
			case SETBIT:
				return setbit0(j, t, (String) args[0], (Long) args[1], (Boolean) args[2]);
			case SETEX:
				return setex0(j, t, (String) args[0], (Integer) args[1], (String) args[2]);
			case SETNX:
				return setnx0(j, t, (String) args[0], (String) args[1]);
			case SETRANGE:
				return setrange0(j, t, (String) args[0], (Long) args[1], (String) args[2]);
			case STRLEN:
				return strlen0(j, t, (String) args[0]);
				
			// Hashes
			case HDEL:
				return hdel0(j, t, (String) args[0], (String[]) args[1]);
				
			// Lists
			case BLPOP:
				return blpop0(j, t, (String) args[0], (Integer) args[1]);
			case BLPOP_KEYS:
			case BRPOP:
			case BRPOP_KEYS:
			case BRPOPLPUSH:
			case LINDEX:
			case LINSERT_BEFORE:
			case LINSERT_AFTER:
			case LLEN:
				return llen0(j, t, (String) args[0]);
			case LPOP:
			case LPUSH:
				return lpush0(j, t, (String) args[0], (String[]) args[1]);
			case LPUSHX:
			case LRANGE:
				return lrange0(j, t, (String) args[0], (Long) args[1], (Long) args[2]);
			case LREM:
			case LSET:
			case LTRIM:
			case RPOP:
			case RPOPLPUSH:
			case RPUSH:
			case RPUSHX:
				
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
		} catch (Exception e) {
			RedisException re = handleException(e, j);
			throw re;
		} finally {
			release(cmd, j);
		}
	}
	
	private RedisException handleException(Exception e, Jedis j) {
		unsetTransaction();
		
		if (e instanceof JedisConnectionException) {
			pool.returnBrokenResource(j);
			return new RedisConnectionException(e);
		}
		
		if (e instanceof JedisDataException) {
			return new RedisDataException(e);
		}
		
		if (e instanceof RedisConnectionException) {
			pool.returnBrokenResource(j);
			return (RedisConnectionException) e;
		}
		
		if (e instanceof RedisDataException) {
			return (RedisDataException) e;
		}
		
		if (e instanceof RedisOperationException) {
			return (RedisOperationException) e;
		}
		
		return new RedisException(e);
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
	
	private void release(CommandEnum cn, Jedis j) {
		if (CommandEnum.MULTI == cn || CommandEnum.WATCH == cn) {
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

package org.craft.atom.redis;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Semaphore;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.craft.atom.redis.api.Redis;
import org.craft.atom.redis.api.RedisConnectionException;
import org.craft.atom.redis.api.RedisDataException;
import org.craft.atom.redis.api.RedisException;
import org.craft.atom.redis.api.RedisPoolConfig;
import org.craft.atom.redis.api.RedisPubSub;
import org.craft.atom.redis.api.RedisTransaction;
import org.craft.atom.redis.api.ScanResult;
import org.craft.atom.redis.api.Slowlog;
import org.craft.atom.redis.api.handler.RedisMonitorHandler;
import org.craft.atom.redis.api.handler.RedisPsubscribeHandler;
import org.craft.atom.redis.api.handler.RedisSubscribeHandler;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.DebugParams;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisMonitor;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.ScanParams;
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
@EqualsAndHashCode(of = { "host", "port" })
@ToString(of = { "host", "port" })
@SuppressWarnings("unchecked")
public class DefaultRedis implements Redis {
	
	
	private static final String             OK                 = "OK"                    ;
	private static final ThreadLocal<Jedis> THREAD_LOCAL_JEDIS = new ThreadLocal<Jedis>();
	
	
	private          String          host                             ;
	private          String          password                         ;
	private          int             port            = 6379           ;
	private          int             timeoutInMillis = 2000           ;
	private          int             database        = 0              ;
	private          RedisPoolConfig poolConfig      = poolConfig(100);
	private volatile JedisPool       pool                             ;
	
	
	// ~ ---------------------------------------------------------------------------------------------------------
	
	
	public DefaultRedis(String host, int port) {
		this.host = host;
		this.port = port;
		init();
	}
	
	public DefaultRedis(String host, int port, int timeout) {
		this.host            = host;
		this.port            = port;
		this.timeoutInMillis = timeout;
		init();
	}
	
	public DefaultRedis(String host, int port, int timeout, int poolSize) {
		this.host            = host;
		this.port            = port;
		this.timeoutInMillis = timeout;
		this.poolConfig      = poolConfig(poolSize);
		init();
	}
	
	public DefaultRedis(String host, int port, int timeout, int poolSize, String password) {
		this.host            = host;
		this.port            = port;
		this.timeoutInMillis = timeout;
		this.poolConfig      = poolConfig(poolSize);
		this.password        = password;
		init();
	}
	
	public DefaultRedis(String host, int port, int timeout, int poolSize, String password, int database) {
		this.host            = host;
		this.port            = port;
		this.timeoutInMillis = timeout;
		this.poolConfig      = poolConfig(poolSize);
		this.password        = password;
		this.database        = database;
		init();
	}
	
	public DefaultRedis(String host, int port, int timeout, RedisPoolConfig poolConfig) {
		this.host            = host;
		this.port            = port;
		this.timeoutInMillis = timeout;
		this.poolConfig      = poolConfig;
		init();
	}
	
	public DefaultRedis(String host, int port, int timeout, RedisPoolConfig poolConfig, String password) {
		this.host            = host;
		this.port            = port;
		this.timeoutInMillis = timeout;
		this.poolConfig      = poolConfig;
		this.password        = password;
		init();
	}

	public DefaultRedis(String host, int port, int timeout, RedisPoolConfig poolConfig, String password, int database) {
		this.host            = host;
		this.port            = port;
		this.timeoutInMillis = timeout;
		this.poolConfig      = poolConfig;
		this.password        = password;
		this.database        = database;
		init();
	}
	
	private RedisPoolConfig poolConfig(int poolSize) {
		if (poolSize <= 0) {
			throw new IllegalArgumentException(String.format("Redis init <poolSize=%s> must > 0", poolSize));
		}
		
		RedisPoolConfig cfg = new RedisPoolConfig();
		cfg.setMaxTotal(poolSize);
		cfg.setMaxIdle(poolSize);
		cfg.setMinIdle(0);
		return cfg;
	}
	
	private void init() {
		if (timeoutInMillis < 0) {
			throw new IllegalArgumentException(String.format("Redis init [timeoutInMillis=%s] must >= 0", timeoutInMillis));
		}
		
		pool = new JedisPool(convert(poolConfig), host, port, timeoutInMillis, password, database);
	}
	
	
	private JedisPoolConfig convert(RedisPoolConfig cfg) {
		JedisPoolConfig jpc = new JedisPoolConfig();
		jpc.setBlockWhenExhausted(cfg.isBlockWhenExhausted());
		jpc.setLifo(cfg.isLifo());
		jpc.setMaxIdle(cfg.getMaxIdle());
		jpc.setMaxTotal(cfg.getMaxTotal());
		jpc.setMaxWaitMillis(cfg.getMaxWaitMillis());
		jpc.setMinEvictableIdleTimeMillis(cfg.getMinEvictableIdleTimeMillis());
		jpc.setMinIdle(cfg.getMinIdle());
		jpc.setNumTestsPerEvictionRun(cfg.getNumTestsPerEvictionRun());
		jpc.setTestOnBorrow(cfg.isTestOnBorrow());
		jpc.setTestOnReturn(cfg.isTestOnReturn());
		jpc.setTestWhileIdle(cfg.isTestWhileIdle());
		jpc.setTimeBetweenEvictionRunsMillis(cfg.getTimeBetweenEvictionRunsMillis());
		return jpc;
	}
	
	
	// ~ ---------------------------------------------------------------------------------------------------------
	
	
	@Override
	public String host() {
		return host;
	}
	
	@Override
	public int port() {
		return port;
	}
	
	@Override
	public String password() {
		return password;
	}
	
	@Override
	public int timeoutInMillis() {
		return timeoutInMillis;
	}
	
	@Override
	public int database() {
		return database;
	}

	@Override
	public RedisPoolConfig poolConfig() {
		return poolConfig;
	}
	
	
	// ~ --------------------------------------------------------------------------------------------------------- Keys
	
	
	@Override
	public Long del(String... keys) {
		return (Long) executeCommand(CommandEnum.DEL, new Object[] { keys });
	}
	
	private Long del0(Jedis j, String... keys) {
		return j.del(keys);
	}

	@Override
	public byte[] dump(String key) {
		return (byte[]) executeCommand(CommandEnum.DUMP, new Object[] { key });
	}
	
	private byte[] dump0(Jedis j, String key) {
		return j.dump(key);
	}

	@Override
	public Boolean exists(String key) {
		return (Boolean) executeCommand(CommandEnum.EXISTS, new Object[] { key });
	}
	
	private Boolean exists0(Jedis j, String key) {
		return j.exists(key);
	}

	@Override
	public Long expire(String key, int seconds) {
		return (Long) executeCommand(CommandEnum.EXPIRE, new Object[] { key, seconds });
	}
	
	private Long expire0(Jedis j, String key, int seconds) {
		return j.expire(key, seconds);
	}

	@Override
	public Long expireat(String key, long timestamp) {
		return (Long) executeCommand(CommandEnum.EXPIREAT, new Object[] { key, timestamp });
	}
	
	private Long expireat0(Jedis j, String key, long timestamp) {
		return j.expireAt(key, timestamp);
	}

	@Override
	public Set<String> keys(String pattern) {
		return (Set<String>) executeCommand(CommandEnum.KEYS, new Object[] { pattern });
	}
	
	private Set<String> keys0(Jedis j, String pattern) {
		return j.keys(pattern);
	}

	@Override
	public String migrate(String host, int port, String key, int destinationdb, int timeout) {
		return (String) executeCommand(CommandEnum.MIGRATE, new Object[] { host, port, key, destinationdb, timeout });
	}
	
	private String migrate0(Jedis j, String host, int port, String key, int destinationdb, int timeout) {
		return j.migrate(host, port, key, destinationdb, timeout);
	}

	@Override
	public Long move(String key, int db) {
		return (Long) executeCommand(CommandEnum.MOVE, new Object[] { key, db });
	}
	
	private Long move0(Jedis j, String key, int db) {
		return j.move(key, db);
	}

	@Override
	public Long objectrefcount(String key) {
		return (Long) executeCommand(CommandEnum.OBJECT_REFCOUNT, new Object[] { key });
	}
	
	private Long objectrefcount0(Jedis j, String key) {
		return j.objectRefcount(key);
	}

	@Override
	public String objectencoding(String key) {
		return (String) executeCommand(CommandEnum.OBJECT_ENCODING, new Object[] { key });
	}
	
	private String objectencoding0(Jedis j, String key) {
		return j.objectEncoding(key);
	}

	@Override
	public Long objectidletime(String key) {
		return (Long) executeCommand(CommandEnum.OBJECT_IDLETIME, new Object[] { key });
	}
	
	private Long objectidletime0(Jedis j, String key) {
		return j.objectIdletime(key);
	}

	@Override
	public Long persist(String key) {
		return (Long) executeCommand(CommandEnum.PERSIST, new Object[] { key });
	}
	
	private Long persist0(Jedis j, String key) {
		return j.persist(key);
	}

	@Override
	public Long pexpire(String key, long milliseconds) {
		return (Long) executeCommand(CommandEnum.PEXPIRE, new Object[] { key, milliseconds });
	}
	
	private Long pexpire0(Jedis j, String key, long milliseconds) {
		return j.pexpire(key, milliseconds);
	}

	@Override
	public Long pexpireat(String key, long millisecondstimestamp) {
		return (Long) executeCommand(CommandEnum.PEXPIREAT, new Object[] { key, millisecondstimestamp });
	}
	
	private Long pexpireat0(Jedis j, String key, long millisecondstimestamp) {
		return j.pexpireAt(key, millisecondstimestamp);
	}

	@Override
	public Long pttl(String key) {
		return (Long) executeCommand(CommandEnum.PTTL, new Object[] { key });
	}
	
	private Long pttl0(Jedis j, String key) {
		return j.pttl(key);
	}
	
	@Override
	public String randomkey() {
		return (String) executeCommand(CommandEnum.RANDOMKEY, new Object[] {});
	}
	
	private String randomkey0(Jedis j) {
		return j.randomKey();
	}

	@Override
	public String rename(String key, String newkey) {
		return (String) executeCommand(CommandEnum.RENAME, new Object[] { key, newkey });
	}
	
	private String rename0(Jedis j, String key, String newkey) {
		return j.rename(key, newkey);
	}

	@Override
	public Long renamenx(String key, String newkey) {
		return (Long) executeCommand(CommandEnum.RENAMENX, new Object[] { key, newkey });
	}
	
	private Long renamenx0(Jedis j, String key, String newkey) {
		return j.renamenx(key, newkey);
	}

	@Override
	public String restore(String key, int ttl, byte[] serializedvalue) {
		return (String) executeCommand(CommandEnum.RESTORE, new Object[] { key, ttl, serializedvalue });
	}
	
	private String restore0(Jedis j, String key, int ttl, byte[] serializedvalue) {
		return j.restore(key, ttl, serializedvalue);
	}

	@Override
	public List<String> sort(String key) {
		return (List<String>) executeCommand(CommandEnum.SORT, new Object[] { key });
	}
	
	private List<String> sort0(Jedis j, String key) {
		return j.sort(key);
	}

	@Override
	public List<String> sort(String key, boolean desc) {
		return (List<String>) executeCommand(CommandEnum.SORT_DESC, new Object[] { key, desc });
	}
	
	private List<String> sort_desc(Jedis j, String key, boolean desc) {
		SortingParams sp = new SortingParams();
		if (desc) {
			sp.desc();
		}
		
		return j.sort(key, sp);
	}

	@Override
	public List<String> sort(String key, boolean alpha, boolean desc) {
		return (List<String>) executeCommand(CommandEnum.SORT_ALPHA_DESC, new Object[] { key, alpha, desc });
	}
	
	private List<String> sort_alpha_desc(Jedis j, String key, boolean alpha, boolean desc) {
		SortingParams sp = new SortingParams();
		if (desc) {
			sp.desc();
		}
		if (alpha) { 
			sp.alpha() ;
		}

		return j.sort(key, sp);
	}

	@Override
	public List<String> sort(String key, int offset, int count) {
		return (List<String>) executeCommand(CommandEnum.SORT_OFFSET_COUNT, new Object[] { key, offset, count });
	}
	
	private List<String> sort_offset_count(Jedis j, String key, int offset, int count) {
		return j.sort(key, new SortingParams().limit(offset, count));
	}

	@Override
	public List<String> sort(String key, int offset, int count, boolean alpha, boolean desc) {
		return (List<String>) executeCommand(CommandEnum.SORT_OFFSET_COUNT_ALPHA_DESC, new Object[] { key, offset, count, alpha, desc });
	}
	
	private List<String> sort_offset_count_alpha_desc(Jedis j, String key, int offset, int count, boolean alpha, boolean desc) {
		SortingParams sp = new SortingParams();
		if (desc) {
			sp.desc();
		}
		if (alpha) { 
			sp.alpha() ;
		}
		sp.limit(offset, count);
		
		return j.sort(key, sp);
	}

	@Override
	public List<String> sort(String key, String bypattern, String... getpatterns) {
		return (List<String>) executeCommand(CommandEnum.SORT_BY_GET, new Object[] { key, bypattern, getpatterns });
	}
	
	private List<String> sort_by_get(Jedis j, String key, String bypattern, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		
		return j.sort(key, sp);
	}

	@Override
	public List<String> sort(String key, String bypattern, boolean desc, String... getpatterns) {
		return (List<String>) executeCommand(CommandEnum.SORT_BY_DESC_GET, new Object[] { key, bypattern, desc, getpatterns });
	}
	
	private List<String> sort_by_desc_get(Jedis j, String key, String bypattern, boolean desc, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		if (desc) {
			sp.desc();
		}
		
		return j.sort(key, sp);
	}

	@Override
	public List<String> sort(String key, String bypattern, boolean alpha, boolean desc, String... getpatterns) {
		return (List<String>) executeCommand(CommandEnum.SORT_BY_ALPHA_DESC_GET, new Object[] { key, bypattern, alpha, desc, getpatterns });
	}
	
	private List<String> sort_by_alpha_desc_get(Jedis j, String key, String bypattern, boolean alpha, boolean desc, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		if (alpha) {
			sp.alpha();
		}
		if (desc) {
			sp.desc();
		}
		
		return j.sort(key, sp);
	}

	@Override
	public List<String> sort(String key, String bypattern, int offset, int count, String... getpatterns) {
		return (List<String>) executeCommand(CommandEnum.SORT_BY_OFFSET_COUNT_GET, new Object[] { key, bypattern, offset, count, getpatterns });
	}
	
	private List<String> sort_by_offset_count_get(Jedis j, String key, String bypattern, int offset, int count, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		sp.limit(offset, count);
		return j.sort(key, sp);
	}

	@Override
	public List<String> sort(String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String... getpatterns) {
		return (List<String>) executeCommand(CommandEnum.SORT_BY_OFFSET_COUNT_ALPHA_DESC_GET, new Object[] { key, bypattern, offset, count, alpha, desc, getpatterns });
	}
	
	private List<String> sort_by_offset_count_alpha_desc_get(Jedis j, String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String... getpatterns) {
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
		
		return j.sort(key, sp);
	}

	@Override
	public Long sort(String key, String destination) {
		return (Long) executeCommand(CommandEnum.SORT_DESTINATION, key, destination);
	}
	
	private Long sort_destination(Jedis j, String key, String destination) {
		return j.sort(key, destination);
	}

	@Override
	public Long sort(String key, boolean desc, String destination) {
		return (Long) executeCommand(CommandEnum.SORT_DESC_DESTINATION, key, desc, destination);
	}
	
	private Long sort_desc_destination(Jedis j, String key, boolean desc, String destination) {
		SortingParams sp = new SortingParams();
		if (desc) {
			sp.desc();
		}
		return j.sort(key, sp, destination);
	}

	@Override
	public Long sort(String key, boolean alpha, boolean desc, String destination) {
		return (Long) executeCommand(CommandEnum.SORT_ALPHA_DESC_DESTINATION, key, alpha, desc, destination);
	}
	
	private Long sort_alpha_desc_destination(Jedis j, String key, boolean alpha, boolean desc, String destination) {
		SortingParams sp = new SortingParams();
		if (desc) {
			sp.desc();
		}
		if (alpha) {
			sp.alpha();
		}
		
		return j.sort(key, sp, destination);
	}

	@Override
	public Long sort(String key, int offset, int count, String destination) {
		return (Long) executeCommand(CommandEnum.SORT_OFFSET_COUNT_DESTINATION, key, offset, count, destination);
	}
	
	private Long sort_offset_count_destination(Jedis j, String key, int offset, int count, String destination) {
		SortingParams sp = new SortingParams();
		sp.limit(offset, count);
		return j.sort(key, sp, destination);
	}

	@Override
	public Long sort(String key, int offset, int count, boolean alpha, boolean desc, String destination) {
		return (Long) executeCommand(CommandEnum.SORT_OFFSET_COUNT_ALPHA_DESC_DESTINATION, key, offset, count, alpha, desc, destination);
	}
	
	private Long sort_offset_count_alpha_desc_destination(Jedis j, String key, int offset, int count, boolean alpha, boolean desc, String destination) {
		SortingParams sp = new SortingParams();
		sp.limit(offset, count);
		if (desc) {
			sp.desc();
		}
		if (alpha) {
			sp.alpha();
		}
		return j.sort(key, sp, destination);
	}

	@Override
	public Long sort(String key, String bypattern, String destination, String... getpatterns) {
		return (Long) executeCommand(CommandEnum.SORT_BY_DESTINATION_GET, new Object[] { key, bypattern, destination, getpatterns });
	}
	
	private Long sort_by_destination_get(Jedis j, String key, String bypattern, String destination, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		
		return j.sort(key, sp, destination);
	}

	@Override
	public Long sort(String key, String bypattern, boolean desc, String destination, String... getpatterns) {
		return (Long) executeCommand(CommandEnum.SORT_BY_DESC_DESTINATION_GET, new Object[] { key, bypattern, desc, destination, getpatterns });
	}
	
	private Long sort_by_desc_destination_get(Jedis j, String key, String bypattern, boolean desc, String destination, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		if (desc) {
			sp.desc();
		}
		
		return j.sort(key, sp, destination);
	}

	@Override
	public Long sort(String key, String bypattern, boolean alpha, boolean desc, String destination, String... getpatterns) {
		return (Long) executeCommand(CommandEnum.SORT_BY_ALPHA_DESC_DESTINATION_GET, new Object[] { key, bypattern, alpha, desc, destination, getpatterns });
	}
	
	private Long sort_by_alpha_desc_destination_get(Jedis j, String key, String bypattern, boolean alpha, boolean desc, String destination, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		if (alpha) {
			sp.alpha();
		}
		if (desc) {
			sp.desc();
		}
		
		return j.sort(key, sp, destination);
	}

	@Override
	public Long sort(String key, String bypattern, int offset, int count, String destination, String... getpatterns) {
		return (Long) executeCommand(CommandEnum.SORT_BY_OFFSET_COUNT_DESTINATION_GET, new Object[] { key, bypattern, offset, count, destination, getpatterns });
	}
	
	private Long sort_by_offset_count_destination_get(Jedis j, String key, String bypattern, int offset, int count, String destination, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		sp.limit(offset, count);
		
		return j.sort(key, sp, destination);
	}

	@Override
	public Long sort(String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String destination, String... getpatterns) {
		return (Long) executeCommand(CommandEnum.SORT_BY_OFFSET_COUNT_ALPHA_DESC_DESTINATION_GET, new Object[] { key, bypattern, offset, count, alpha, desc, destination, getpatterns });
	}
	
	private Long sort_by_offset_count_alpha_desc_destination_get(Jedis j, String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String destination, String... getpatterns) {
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
		
		return j.sort(key, sp, destination);
	}

	@Override
	public Long ttl(String key) {
		return (Long) executeCommand(CommandEnum.TTL, new Object[] { key });
	}
	
	private Long ttl0(Jedis j, String key) {
		return j.ttl(key);
	}

	@Override
	public String type(String key) {
		return (String) executeCommand(CommandEnum.TYPE, new Object[] { key });
	}
	
	private String type0(Jedis j, String key) {
		return j.type(key);
	}
	
	@Override
	public ScanResult<String> scan(String cursor) {
		return (ScanResult<String>) executeCommand(CommandEnum.SCAN, new Object[] { cursor });
	}
	
	private ScanResult<String> scan0(Jedis j, String cursor) {
		redis.clients.jedis.ScanResult<String> sr = j.scan(cursor);
		return new ScanResult<String>(sr.getStringCursor(), sr.getResult());
	}

	@Override
	public ScanResult<String> scan(String cursor, int count) {
		return (ScanResult<String>) executeCommand(CommandEnum.SCAN_COUNT, new Object[] { cursor, count });
	}
	
	private ScanResult<String> scan_count(Jedis j, String cursor, int count) {
		ScanParams param = new ScanParams();
		param.count(count);
		redis.clients.jedis.ScanResult<String> sr = j.scan(cursor, param);
		return new ScanResult<String>(sr.getStringCursor(), sr.getResult());
	}

	@Override
	public ScanResult<String> scan(String cursor, String pattern) {
		return (ScanResult<String>) executeCommand(CommandEnum.SCAN_MATCH, new Object[] { cursor, pattern });
	}
	
	private ScanResult<String> scan_match(Jedis j, String cursor, String pattern) {
		ScanParams param = new ScanParams();
		param.match(pattern);
		redis.clients.jedis.ScanResult<String> sr = j.scan(cursor, param);
		return new ScanResult<String>(sr.getStringCursor(), sr.getResult());
	}

	@Override
	public ScanResult<String> scan(String cursor, String pattern, int count) {
		return (ScanResult<String>) executeCommand(CommandEnum.SCAN_MATCH_COUNT, new Object[] { cursor, pattern, count });
	}
	
	private ScanResult<String> scan_match_count(Jedis j, String cursor, String pattern, int count) {
		ScanParams param = new ScanParams();
		param.match(pattern);
		param.count(count);
		redis.clients.jedis.ScanResult<String> sr = j.scan(cursor, param);
		return new ScanResult<String>(sr.getStringCursor(), sr.getResult());
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Strings
	

	@Override
	public Long append(String key, String value) {
		return (Long) executeCommand(CommandEnum.APPEND, key, value);
	}
	
	private Long append0(Jedis j, String key, String value) {
		return j.append(key, value);
	}

	@Override
	public Long bitcount(String key) {
		return (Long) executeCommand(CommandEnum.BITCOUNT, key);
	}
	
	private Long bitcount0(Jedis j, String key) {
		return j.bitcount(key);
	}
	
	@Override
	public Long bitcount(String key, long start, long end) {
		return (Long) executeCommand(CommandEnum.BITCOUNT_START_END, key, start, end);
	}

	private Long bitcount0(Jedis j, String key, long start, long end) {
		return j.bitcount(key, start, end);
	}

	@Override
	public Long bitnot(String destkey, String key) {
		return (Long) executeCommand(CommandEnum.BITNOT, destkey, key);
	}
	
	private Long bitnot0(Jedis j, String destkey, String key) {
		return j.bitop(BitOP.NOT, destkey, key);
	}
	
	@Override
	public Long bitand(String destkey, String... keys) {
		return (Long) executeCommand(CommandEnum.BITAND, destkey, keys);
	}
	
	private Long bitand0(Jedis j, String destkey, String... keys) {
		return j.bitop(BitOP.AND, destkey, keys);
	}

	@Override
	public Long bitor(String destkey, String... keys) {
		return (Long) executeCommand(CommandEnum.BITOR, destkey, keys);
	}
	
	private Long bitor0(Jedis j, String destkey, String... keys) {
		return j.bitop(BitOP.OR, destkey, keys);
	}

	@Override
	public Long bitxor(String destkey, String... keys) {
		return (Long) executeCommand(CommandEnum.BITXOR, destkey, keys);
	}
	
	private Long bitxor0(Jedis j, String destkey, String... keys) {
		return j.bitop(BitOP.XOR, destkey, keys);
	}
	
	@Override
	public Long bitpos(String key, boolean value) {
		return (Long) executeCommand(CommandEnum.BITPOS, key, value);
	}
	
	private Long bitpos0(Jedis j, String key, boolean value) {
		return j.bitpos(key, value);
	}
	
	@Override
	public Long bitpos(String key, boolean value, long start) {
		return (Long) executeCommand(CommandEnum.BITPOS_START, key, value, start);
	}
	
	private Long bitpos0(Jedis j, String key, boolean value, long start) {
		return j.bitpos(key, value, new BitPosParams(start));
	}

	@Override
	public Long bitpos(String key, boolean value, long start, long end) {
		return (Long) executeCommand(CommandEnum.BITPOS_START_END, key, value, start, end);
	}
	
	private Long bitpos0(Jedis j, String key, boolean value, long start, long end) {
		return j.bitpos(key, value, new BitPosParams(start, end));
	}

	@Override
	public Long decr(String key) {
		return (Long) executeCommand(CommandEnum.DECR, key);
	}
	
	private Long decr0(Jedis j, String key) {
		return j.decr(key);
	}

	@Override
	public Long decrby(String key, long decrement) {
		return (Long) executeCommand(CommandEnum.DECRBY, key, decrement);
	}
	
	private Long decrby0(Jedis j, String key, long decrement) {
		return j.decrBy(key, decrement);
	}

	@Override
	public String get(String key) {
		return (String) executeCommand(CommandEnum.GET, key);
	}
	
	private String get0(Jedis j, String key) {
		return j.get(key);
	}

	@Override
	public Boolean getbit(String key, long offset) {
		return (Boolean) executeCommand(CommandEnum.GETBIT, key, offset);
	}
	
	private Boolean getbit0(Jedis j, String key, long offset) {
		return j.getbit(key, offset);
	}

	@Override
	public String getrange(String key, long start, long end) {
		return (String) executeCommand(CommandEnum.GETRANGE, key, start, end);
	}
	
	private String getrange0(Jedis j, String key, long start, long end) {
		return j.getrange(key, start, end);
	}

	@Override
	public String getset(String key, String value) {
		return (String) executeCommand(CommandEnum.GETSET, key, value);
	}
	
	private String getset0(Jedis j, String key, String value) {
		return j.getSet(key, value);
	}

	@Override
	public Long incr(String key) {
		return (Long) executeCommand(CommandEnum.INCR, key);
	}
	
	private Long incr0(Jedis j, String key) {
		return j.incr(key);
	}

	@Override
	public Long incrby(String key, long increment) {
		return (Long) executeCommand(CommandEnum.INCRBY, key, increment);
	}
	
	private Long incrby0(Jedis j, String key, long increment) {
		return j.incrBy(key, increment);
	}

	@Override
	public Double incrbyfloat(String key, double increment) {
		return (Double) executeCommand(CommandEnum.INCRBYFLOAT, key, increment);
	}
	
	private Double incrbyfloat0(Jedis j, String key, double increment) {
		return j.incrByFloat(key, increment);
	}
	
	@Override
	public List<String> mget(String... keys) {
		return (List<String>) executeCommand(CommandEnum.MGET, new Object[] { keys });
	}
	
	private List<String> mget0(Jedis j, String... keys) {
		return j.mget(keys);
	}

	@Override
	public String mset(String... keysvalues) {
		return (String) executeCommand(CommandEnum.MSET, new Object[] { keysvalues });
	}
	
	private String mset0(Jedis j, String... keysvalues) {
		return j.mset(keysvalues);
	}

	@Override
	public Long msetnx(String... keysvalues) {
		return (Long) executeCommand(CommandEnum.MSETNX, new Object[] { keysvalues });
	}
	
	private Long msetnx0(Jedis j, String... keysvalues) {
		return j.msetnx(keysvalues);
	}


	@Override
	public String psetex(String key, int milliseconds, String value) {
		return (String) executeCommand(CommandEnum.PSETEX, key, milliseconds, value);
	}
	
	private String psetex0(Jedis j, String key, int milliseconds, String value) {
		return j.psetex(key, milliseconds, value);
	}

	@Override
	public String set(String key, String value) {
		return (String) executeCommand(CommandEnum.SET, key, value);
	}
	
	private String set0(Jedis j, String key, String value) {
		return j.set(key, value);
	}

	@Override
	public String setxx(String key, String value) {
		return (String) executeCommand(CommandEnum.SETXX, key, value);
	}
	
	private String setxx0(Jedis j, String key, String value) {
		return j.set(key, value, "XX");
	}

	@Override
	public String setnxex(String key, String value, int seconds) {
		return (String) executeCommand(CommandEnum.SETNXEX, key, value, seconds);
	}
	
	private String setnxex0(Jedis j, String key, String value, int seconds) {
		return j.set(key, value, "NX", "EX", seconds);
	}

	@Override
	public String setnxpx(String key, String value, int milliseconds) {
		return (String) executeCommand(CommandEnum.SETNXPX, key, value, milliseconds);
	}
	
	private String setnxpx0(Jedis j, String key, String value, int milliseconds) {
		return j.set(key, value, "NX", "PX", milliseconds);
	}

	@Override
	public String setxxex(String key, String value, int seconds) {
		return (String) executeCommand(CommandEnum.SETXXEX, key, value, seconds);
	}
	
	private String setxxex0(Jedis j, String key, String value, int seconds) {
		return j.set(key, value, "XX", "EX", seconds);
	}

	@Override
	public String setxxpx(String key, String value, int milliseconds) {
		return (String) executeCommand(CommandEnum.SETXXPX, key, value, milliseconds);
	}
	
	private String setxxpx0(Jedis j, String key, String value, int milliseconds) {
		return j.set(key, value, "XX", "PX", milliseconds);
	}
	
	@Override
	public Boolean setbit(String key, long offset, boolean value) {
		return (Boolean) executeCommand(CommandEnum.SETBIT, key, offset, value);
	}
	
	private Boolean setbit0(Jedis j, String key, long offset, boolean value) {
		return j.setbit(key, offset, value);
	}

	@Override
	public String setex(String key, int seconds, String value) {
		return (String) executeCommand(CommandEnum.SETEX, key, seconds, value);
	}
	
	private String setex0(Jedis j, String key, int seconds, String value) {
		return j.setex(key, seconds, value);
	}

	@Override
	public Long setnx(String key, String value) {
		return (Long) executeCommand(CommandEnum.SETNX, key, value);
	}
	
	private Long setnx0(Jedis j, String key, String value) {
		return j.setnx(key, value);
	}

	@Override
	public Long setrange(String key, long offset, String value) {
		return (Long) executeCommand(CommandEnum.SETRANGE, key, offset, value);
	}
	
	private Long setrange0(Jedis j, String key, long offset, String value) {
		return j.setrange(key, offset, value);
	}

	@Override
	public Long strlen(String key) {
		return (Long) executeCommand(CommandEnum.STRLEN, key);
	}
	
	private Long strlen0(Jedis j, String key) {
		return j.strlen(key);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Hashes
	
	

	@Override
	public Long hdel(String key, String... fields) {
		return (Long) executeCommand(CommandEnum.HDEL, key, fields);
	}
	
	private Long hdel0(Jedis j, String key, String... fields) {
		return j.hdel(key, fields);
	}

	@Override
	public Boolean hexists(String key, String field) {
		return (Boolean) executeCommand(CommandEnum.HEXISTS, key, field);
	}
	
	private Boolean hexists0(Jedis j, String key, String field) {
		return j.hexists(key, field);
	}

	@Override
	public String hget(String key, String field) {
		return (String) executeCommand(CommandEnum.HGET, key, field);
	}
	
	private String hget0(Jedis j, String key, String field) {
		return j.hget(key, field);
	}

	@Override
	public Map<String, String> hgetall(String key) {
		return (Map<String, String>) executeCommand(CommandEnum.HGETALL, key);
	}
	
	private Map<String, String> hgetall0(Jedis j, String key) {
		return j.hgetAll(key);
	}

	@Override
	public Long hincrby(String key, String field, long increment) {
		return (Long) executeCommand(CommandEnum.HINCRBY, key, field, increment);
	}
	
	private Long hincrby0(Jedis j, String key, String field, long increment) {
		return j.hincrBy(key, field, increment);
 	}

	@Override
	public Double hincrbyfloat(String key, String field, double increment) {
		return (Double) executeCommand(CommandEnum.HINCRBYFLOAT, key, field, increment);
	}
	
	private Double hincrbyfloat0(Jedis j, String key, String field, double increment) {
		return j.hincrByFloat(key, field, increment);
	}

	@Override
	public Set<String> hkeys(String key) {
		return (Set<String>) executeCommand(CommandEnum.HKEYS, key);
	}
	
	private Set<String> hkeys0(Jedis j, String key) {
		return j.hkeys(key);
	}

	@Override
	public Long hlen(String key) {
		return (Long) executeCommand(CommandEnum.HLEN, key);
	}
	
	private Long hlen0(Jedis j, String key) {
		return j.hlen(key);
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		return (List<String>) executeCommand(CommandEnum.HMGET, key, fields);
	}
	
	private List<String> hmget0(Jedis j, String key, String... fields) {
		return j.hmget(key, fields);
	}

	@Override
	public String hmset(String key, Map<String, String> fieldvalues) {
		return (String) executeCommand(CommandEnum.HMSET, key, fieldvalues);
	}
	
	private String hmset0(Jedis j, String key, Map<String, String> fieldvalues) {
		return j.hmset(key, fieldvalues);
	}

	@Override
	public Long hset(String key, String field, String value) {
		return (Long) executeCommand(CommandEnum.HSET, key, field, value);
	}
	
	private Long hset0(Jedis j, String key, String field, String value) {
		return j.hset(key, field, value);
	}

	@Override
	public Long hsetnx(String key, String field, String value) {
		return (Long) executeCommand(CommandEnum.HSETNX, key, field, value);
	}
	
	private Long hsetnx0(Jedis j, String key, String field, String value) {
		return j.hsetnx(key, field, value);
	}

	@Override
	public List<String> hvals(String key) {
		return (List<String>) executeCommand(CommandEnum.HVALS, key);
	}
	
	private List<String> hvals0(Jedis j, String key) {
		return j.hvals(key);
	}
	
	@Override
	public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor) {
		return (ScanResult<Map.Entry<String, String>>) executeCommand(CommandEnum.HSCAN, new Object[] { key, cursor });
	}
	
	private ScanResult<Map.Entry<String, String>> hscan0(Jedis j, String key, String cursor) {
		redis.clients.jedis.ScanResult<Map.Entry<String, String>> sr = j.hscan(key, cursor);
		return new ScanResult<Map.Entry<String, String>>(sr.getStringCursor(), sr.getResult());
	}

	@Override
	public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, int count) {
		return (ScanResult<Map.Entry<String, String>>) executeCommand(CommandEnum.HSCAN_COUNT, new Object[] { key, cursor, count });
	}
	
	private ScanResult<Map.Entry<String, String>> hscan_count(Jedis j, String key, String cursor, int count) {
		ScanParams param = new ScanParams();
		param.count(count);
		redis.clients.jedis.ScanResult<Map.Entry<String, String>> sr = j.hscan(key, cursor, param);
		return new ScanResult<Map.Entry<String, String>>(sr.getStringCursor(), sr.getResult());
	}

	@Override
	public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, String pattern) {
		return (ScanResult<Map.Entry<String, String>>) executeCommand(CommandEnum.HSCAN_MATCH, new Object[] { key, cursor, pattern });
	}
	
	private ScanResult<Map.Entry<String, String>> hscan_match(Jedis j, String key, String cursor, String pattern) {
		ScanParams param = new ScanParams();
		param.match(pattern);
		redis.clients.jedis.ScanResult<Map.Entry<String, String>> sr = j.hscan(key, cursor, param);
		return new ScanResult<Map.Entry<String, String>>(sr.getStringCursor(), sr.getResult());
	}

	@Override
	public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, String pattern, int count) {
		return (ScanResult<Map.Entry<String, String>>) executeCommand(CommandEnum.HSCAN_MATCH_COUNT, new Object[] { key, cursor, pattern, count });
	}
	
	private ScanResult<Map.Entry<String, String>> hscan_match_count(Jedis j, String key, String cursor, String pattern, int count) {
		ScanParams param = new ScanParams();
		param.match(pattern);
		param.count(count);
		redis.clients.jedis.ScanResult<Map.Entry<String, String>> sr = j.hscan(key, cursor, param);
		return new ScanResult<Map.Entry<String, String>>(sr.getStringCursor(), sr.getResult());
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Lists
	

	@Override
	public String blpop(String key) {
		return blpop(key, 0);
	}

	@Override
	public String blpop(String key, int timeout) {
		Map<String, String> map = blpop(0, key);
		return map.get(key);
	}
	
	@Override
	public Map<String, String> blpop(String... keys) {
		return blpop(0, keys);
 	}
	
	@Override
	public Map<String, String> blpop(int timeout, String... keys) {
		return (Map<String, String>) executeCommand(CommandEnum.BLPOP, new Object[] { timeout, keys });
	}
	
	private Map<String, String> blpop0(Jedis j, int timeout, String... keys) {
		List<String> l = j.blpop(timeout, keys);
		return convert4bpop(l);
	}
	
	private Map<String, String> convert4bpop(List<String> l) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		if (l == null) {
			return map;
		}
		
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
		Map<String, String> map = brpop(0, key);
		return map.get(key);
	}
	
	@Override
	public Map<String, String> brpop(String... keys) {
		return brpop(0, keys);
	}

	@Override
	public Map<String, String> brpop(int timeout, String... keys) {
		return (Map<String, String>) executeCommand(CommandEnum.BRPOP, timeout, keys);
	}
	
	private Map<String, String> brpop0(Jedis j, int timeout, String... keys) {
		List<String> l = j.brpop(timeout, keys);
		return convert4bpop(l);
	}
	

	@Override
	public String brpoplpush(String source, String destination, int timeout) {
		return (String) executeCommand(CommandEnum.BRPOPLPUSH, source, destination, timeout);
	}
	
	private String brpoplpush0(Jedis j, String source, String  destination, int timeout) {
		return j.brpoplpush(source, destination, timeout);
	}

	@Override
	public String lindex(String key, long index) {
		return (String) executeCommand(CommandEnum.LINDEX, key, index);
	}
	
	private String lindex0(Jedis j, String key, long index) {
		return j.lindex(key, index);
	}

	@Override
	public Long linsertbefore(String key, String pivot, String value) {
		return (Long) executeCommand(CommandEnum.LINSERT_BEFORE, key, pivot, value);
	}
	
	private Long linsertbefore0(Jedis j, String key, String pivot, String value) {
		return j.linsert(key, LIST_POSITION.BEFORE, pivot, value);
	}

	@Override
	public Long linsertafter(String key, String pivot, String value) {
		return (Long) executeCommand(CommandEnum.LINSERT_AFTER, key, pivot, value);
	}
	
	private Long linsertafter0(Jedis j, String key, String pivot, String value) {
		return j.linsert(key, LIST_POSITION.AFTER, pivot, value);
	}

	@Override
	public Long llen(String key) {
		return (Long) executeCommand(CommandEnum.LLEN, key);
	}
	
	private Long llen0(Jedis j, String key) {
		return j.llen(key);
	}

	@Override
	public String lpop(String key) {
		return (String) executeCommand(CommandEnum.LPOP, key);
	}
	
	private String lpop0(Jedis j, String key) {
		return j.lpop(key);
	}

	@Override
	public Long lpush(String key, String... values) {
		return (Long) executeCommand(CommandEnum.LPUSH, key, values);
	}
	
	private Long lpush0(Jedis j, String key, String... values) {
		return j.lpush(key, values);
	}

	@Override
	public Long lpushx(String key, String value) {
		return (Long) executeCommand(CommandEnum.LPUSHX, key, value);
	}
	
	private Long lpushx0(Jedis j, String key, String value) {
		return j.lpushx(key, value);
	}

	@Override
	public List<String> lrange(String key, long start, long stop) {
		return (List<String>) executeCommand(CommandEnum.LRANGE, key, start, stop);
	}
	
	private List<String> lrange0(Jedis j, String key, long start, long stop) {
		return j.lrange(key, start, stop);
	}

	@Override
	public Long lrem(String key, long count, String value) {
		return (Long) executeCommand(CommandEnum.LREM, key, count, value);
	}
	
	private Long lrem0(Jedis j, String key, long count, String value) {
		return j.lrem(key, count, value);
	}

	@Override
	public String lset(String key, long index, String value) {
		return (String) executeCommand(CommandEnum.LSET, key, index, value);
	}
	
	private String lset0(Jedis j, String key, long index, String value) {
		return j.lset(key, index, value);
	}

	@Override
	public String ltrim(String key, long start, long stop) {
		return (String) executeCommand(CommandEnum.LTRIM, key, start, stop);
	}
	
	private String ltrim0(Jedis j, String key, long start, long stop) {
		return j.ltrim(key, start, stop);
	}

	@Override
	public String rpop(String key) {
		return (String) executeCommand(CommandEnum.RPOP, key);
	}
	
	private String rpop0(Jedis j, String key) {
		return j.rpop(key);
	}

	@Override
	public String rpoplpush(String source, String destination) {
		return (String) executeCommand(CommandEnum.RPOPLPUSH, source, destination);
	}
	
	private String rpoplpush0(Jedis j, String source, String destination) {
		return j.rpoplpush(source, destination);
	}

	@Override
	public Long rpush(String key, String... values) {
		return (Long) executeCommand(CommandEnum.RPUSH, key, values);
	}
	
	private Long rpush0(Jedis j, String key, String... values) {
		return j.rpush(key, values);
	}

	@Override
	public Long rpushx(String key, String value) {
		return (Long) executeCommand(CommandEnum.RPUSHX, key, value);
	}
	
	private Long rpushx0(Jedis j, String key, String value) {
		return j.rpushx(key, value);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Sets
	

	@Override
	public Long sadd(String key, String... members) {
		return (Long) executeCommand(CommandEnum.SADD, new Object[] { key, members });
	}
	
	private Long sadd0(Jedis j, String key, String... members) {
		return j.sadd(key, members);
	}

	@Override
	public Long scard(String key) {
		return (Long) executeCommand(CommandEnum.SCARD, key);
	}
	
	private Long scard0(Jedis j, String key) {
		return j.scard(key);
	}
	
	@Override
	public Set<String> sdiff(String... keys) {
		return (Set<String>) executeCommand(CommandEnum.SDIFF, new Object[] { keys });
	}
	
	private Set<String> sdiff0(Jedis j, String... keys) {
		return j.sdiff(keys);
	}

	@Override
	public Long sdiffstore(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.SDIFFSTORE, destination, keys);
	}
	
	private Long sdiffstore0(Jedis j, String destination, String... keys) {
		return j.sdiffstore(destination, keys);
	}

	@Override
	public Set<String> sinter(String... keys) {
		return (Set<String>) executeCommand(CommandEnum.SINTER, new Object[] { keys });
	}
	
	private Set<String> sinter0(Jedis j, String... keys) {
		return j.sinter(keys);
	}

	@Override
	public Long sinterstore(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.SINTERSTORE, destination, keys);
	}
	
	private Long sinterstore0(Jedis j, String destination, String... keys) {
		return j.sinterstore(destination, keys);
	}

	@Override
	public Boolean sismember(String key, String member) {
		return (Boolean) executeCommand(CommandEnum.SISMEMBER, key, member);
	}
	
	private Boolean sismember0(Jedis j, String key, String member) {
		return j.sismember(key, member);
	}

	@Override
	public Set<String> smembers(String key) {
		return (Set<String>) executeCommand(CommandEnum.SMEMBERS, key);
	}
	
	private Set<String> smembers0(Jedis j, String key) {
		return j.smembers(key);
	}
	
	@Override
	public Long smove(String source, String destination, String member) {
		return (Long) executeCommand(CommandEnum.SMOVE, source, destination, member);
	}
	
	private Long smove0(Jedis j, String source, String destination, String member) {
		return j.smove(source, destination, member);
	}

	@Override
	public String spop(String key) {
		return (String) executeCommand(CommandEnum.SPOP, key);
	}
	
	private String spop0(Jedis j, String key) {
		return j.spop(key);
	}
	
	@Override
	public String srandmember(String key) {
		List<String> list = srandmember(key, 1);
		if (list.isEmpty()) {
			return null;
		} else {
			return list.iterator().next();
		}
	}

	@Override
	public List<String> srandmember(String key, int count) {
		return (List<String>) executeCommand(CommandEnum.SRANDMEMBER, key, count);
	}
	
	private List<String> srandmember0(Jedis j, String key, int count) {
		return j.srandmember(key, count);
	}

	@Override
	public Long srem(String key, String... members) {
		return (Long) executeCommand(CommandEnum.SREM, key, members);
	}
	
	private Long srem0(Jedis j, String key, String... members) {
		return j.srem(key, members);
	}
	
	@Override
	public Set<String> sunion(String... keys) {
		return (Set<String>) executeCommand(CommandEnum.SUNION, new Object[] { keys });
	}
	
	private Set<String> sunion0(Jedis j, String... keys) {
		return j.sunion(keys);
	}

	@Override
	public Long sunionstore(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.SUNIONSTORE, destination, keys);
	}
	
	private Long sunionstore0(Jedis j, String destination, String... keys) {
		return j.sunionstore(destination, keys);
	}
	
	@Override
	public ScanResult<String> sscan(String key, String cursor) {
		return (ScanResult<String>) executeCommand(CommandEnum.SSCAN, new Object[] { key, cursor });
	}
	
	private ScanResult<String> sscan0(Jedis j, String key, String cursor) {
		redis.clients.jedis.ScanResult<String> sr = j.sscan(key, cursor);
		return new ScanResult<String>(sr.getStringCursor(), sr.getResult());
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor, int count) {
		return (ScanResult<String>) executeCommand(CommandEnum.SSCAN_COUNT, new Object[] { key, cursor, count });
	}
	
	private ScanResult<String> sscan_count(Jedis j, String key, String cursor, int count) {
		ScanParams param = new ScanParams();
		param.count(count);
		redis.clients.jedis.ScanResult<String> sr = j.sscan(key, cursor, param);
		return new ScanResult<String>(sr.getStringCursor(), sr.getResult());
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor, String pattern) {
		return (ScanResult<String>) executeCommand(CommandEnum.SSCAN_MATCH, new Object[] { key, cursor, pattern });
	}
	
	private ScanResult<String> sscan_match(Jedis j, String key, String cursor, String pattern) {
		ScanParams param = new ScanParams();
		param.match(pattern);
		redis.clients.jedis.ScanResult<String> sr = j.sscan(key, cursor, param);
		return new ScanResult<String>(sr.getStringCursor(), sr.getResult());
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor, String pattern, int count) {
		return (ScanResult<String>) executeCommand(CommandEnum.SSCAN_MATCH_COUNT, new Object[] { key, cursor, pattern, count });
	}
	
	private ScanResult<String> sscan_match_count(Jedis j, String key, String cursor, String pattern, int count) {
		ScanParams param = new ScanParams();
		param.match(pattern);
		param.count(count);
		redis.clients.jedis.ScanResult<String> sr = j.sscan(key, cursor, param);
		return new ScanResult<String>(sr.getStringCursor(), sr.getResult());
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------- Sorted Sets
	
	

	@Override
	public Long zadd(String key, double score, String member) {
		Map<String, Double> scoremembers = new HashMap<String, Double>();
		scoremembers.put(member, score);
		return zadd(key, scoremembers);
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoremembers) {
		return (Long) executeCommand(CommandEnum.ZADD, key, scoremembers);
	}
	
	private Long zadd0(Jedis j, String key, Map<String, Double> scoremembers) {
		return j.zadd(key, scoremembers);
	}

	@Override
	public Long zcard(String key) {
		return (Long) executeCommand(CommandEnum.ZCARD, key);
	}
	
	private Long zcard0(Jedis j, String key) {
		return j.zcard(key);
	}

	@Override
	public Long zcount(String key, double min, double max) {
		return (Long) executeCommand(CommandEnum.ZCOUNT, key, min, max);
	}
	
	private Long zcount0(Jedis j, String key, double min, double max) {
		return j.zcount(key, min, max);
	}

	@Override
	public Long zcount(String key, String min, String max) {
		return (Long) executeCommand(CommandEnum.ZCOUNT_STRING, key, min, max);
	}
	
	private Long zcount0(Jedis j, String key, String min, String max) {
		return j.zcount(key, min, max);
	}

	@Override
	public Double zincrby(String key, double score, String member) {
		return (Double) executeCommand(CommandEnum.ZINCRBY, key, score, member);
	}
	
	private Double zincrby0(Jedis j, String key, double score, String member) {
		return j.zincrby(key, score, member);
	}
	
	@Override
	public Long zinterstore(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.ZINTERSTORE, destination, keys);
	}
	
	private Long zinterstore0(Jedis j, String destination, String... keys) {
		return j.zinterstore(destination, keys);
	}

	@Override
	public Long zinterstoremax(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.ZINTERSTORE_MAX, destination, keys);
	}
	
	private Long zinterstoremax0(Jedis j, String destination, String... keys) {
		return j.zinterstore(destination, new ZParams().aggregate(Aggregate.MAX), keys);
	}

	@Override
	public Long zinterstoremin(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.ZINTERSTORE_MIN, destination, keys);
	}
	
	private Long zinterstoremin0(Jedis j, String destination, String... keys) {
		return j.zinterstore(destination, new ZParams().aggregate(Aggregate.MIN), keys);
	}

	@Override
	public Long zinterstore(String destination, Map<String, Integer> weightkeys) {
		return (Long) executeCommand(CommandEnum.ZINTERSTORE_WEIGHTS, destination, weightkeys);
	}
	
	private Long zinterstore_weights(Jedis j, String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
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
	
	private Long zinterstore_weights_max(Jedis j, String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
		return j.zinterstore(destination, new ZParams().weights(weights).aggregate(Aggregate.MAX), keys);
	}

	@Override
	public Long zinterstoremin(String destination, Map<String, Integer> weightkeys) {
		return (Long) executeCommand(CommandEnum.ZINTERSTORE_WEIGHTS_MIN, destination, weightkeys);
	}
	
	private Long zinterstore_weights_min(Jedis j, String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
		return j.zinterstore(destination, new ZParams().weights(weights).aggregate(Aggregate.MIN), keys);
	}

	@Override
	public Set<String> zrange(String key, long start, long stop) {
		return (Set<String>) executeCommand(CommandEnum.ZRANGE, key, start, stop);
	}
	
	private Set<String> zrange0(Jedis j, String key, long start, long stop) {
		return j.zrange(key, start, stop);
	}

	@Override
	public Map<String, Double> zrangewithscores(String key, long start, long stop) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZRANGE_WITHSCORES, key, start, stop);
	}
	
	private Map<String, Double> zrangewithscores0(Jedis j, String key, long start, long stop) {
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
	
	private Set<String> zrangebyscore0(Jedis j, String key, double min, double max) {
		return j.zrangeByScore(key, min, max);
	}

	@Override
	public Set<String> zrangebyscore(String key, String min, String max) {
		return (Set<String>) executeCommand(CommandEnum.ZRANGEBYSCORE_STRING, key, min, max);
	}
	
	private Set<String> zrangebyscore_string(Jedis j, String key, String min, String max) {
		return j.zrangeByScore(key, min, max);
	}

	@Override
	public Set<String> zrangebyscore(String key, double min, double max, int offset, int count) {
		return (Set<String>) executeCommand(CommandEnum.ZRANGEBYSCORE_OFFSET_COUNT, key, min, max, offset, count);
	}
	
	private Set<String> zrangebyscore_offset_count(Jedis j, String key, double min, double max, int offset, int count) {
		return j.zrangeByScore(key, min, max, offset, count);
	}

	@Override
	public Set<String> zrangebyscore(String key, String min, String max, int offset, int count) {
		return (Set<String>) executeCommand(CommandEnum.ZRANGEBYSCORE_OFFSET_COUNT_STRING, key, min, max, offset, count);
	}
	
	private Set<String> zrangebyscore_offset_count_string(Jedis j, String key, String min, String max, int offset, int count) {
		return j.zrangeByScore(key, min, max, offset, count);
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String key, double min, double max) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZRANGEBYSCORE_WITHSCORES, key, min, max);
	}
	
	private Map<String, Double> zrangebyscorewithscores0(Jedis j, String key, double min, double max) {
		Set<Tuple> set = j.zrangeByScoreWithScores(key, min, max);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String key, String min, String max) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZRANGEBYSCORE_WITHSCORES_STRING, key, min, max);
	}
	
	private Map<String, Double> zrangebyscorewithscores_string(Jedis j, String key, String min, String max) {
		Set<Tuple> set = j.zrangeByScoreWithScores(key, min, max);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String key, double min, double max, int offset, int count) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZRANGEBYSCORE_WITHSCORES_OFFSET_COUNT, key, min, max, offset, count);
	}
	
	private Map<String, Double> zrangebyscorewithscores_offset_count(Jedis j, String key, double min, double max, int offset, int count) {
		Set<Tuple> set = j.zrangeByScoreWithScores(key, min, max, offset, count);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String key, String min, String max, int offset, int count) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZRANGEBYSCORE_WITHSCORES_OFFSET_COUNT_STRING, key, min, max, offset, count);
	}
	
	private Map<String, Double> zrangebyscorewithscores_offset_count_string(Jedis j, String key, String min, String max, int offset, int count) {
		Set<Tuple> set = j.zrangeByScoreWithScores(key, min, max, offset, count);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Long zrank(String key, String member) {
		return (Long) executeCommand(CommandEnum.ZRANK, key, member);
	}
	
	private Long zrank0(Jedis j, String key, String member) {
		return j.zrank(key, member);
	}

	@Override
	public Long zrem(String key, String... members) {
		return (Long) executeCommand(CommandEnum.ZREM, key, members);
	}
	
	private Long zrem0(Jedis j, String key, String... members) {
		return j.zrem(key, members);
	}

	@Override
	public Long zremrangebyrank(String key, long start, long stop) {
		return (Long) executeCommand(CommandEnum.ZREMRANGEBYRANK, key, start, stop);
	}
	
	private Long zremrangebyrank0(Jedis j, String key, long start, long stop) {
		return j.zremrangeByRank(key, start, stop);
	}

	@Override
	public Long zremrangebyscore(String key, double min, double max) {
		return (Long) executeCommand(CommandEnum.ZREMRANGEBYSCORE, key, min, max);
	}
	
	private Long zremrangebyscore0(Jedis j, String key, double min, double max) {
		return j.zremrangeByScore(key, min, max);
	}

	@Override
	public Long zremrangebyscore(String key, String min, String max) {
		return (Long) executeCommand(CommandEnum.ZREMRANGEBYSCORE_STRING, key, min, max);
	}
	
	private Long zremrangebyscore_string(Jedis j, String key, String min, String max) {
		return j.zremrangeByScore(key, min, max);
	}

	@Override
	public Set<String> zrevrange(String key, long start, long stop) {
		return (Set<String>) executeCommand(CommandEnum.ZREVRANGE, key, start, stop);
	}
	
	private Set<String> zrevrange0(Jedis j, String key, long start, long stop) {
		return j.zrevrange(key, start, stop);
	}

	@Override
	public Map<String, Double> zrevrangewithscores(String key, long start, long stop) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZREVRANGE_WITHSCORES, key, start, stop);
	}
	
	private Map<String, Double> zrevrangewithscores0(Jedis j, String key, long start, long stop) {
		Set<Tuple> set = j.zrevrangeWithScores(key, start, stop);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Set<String> zrevrangebyscore(String key, double max, double min) {
		return (Set<String>) executeCommand(CommandEnum.ZREVRANGEBYSCORE, key, max, min);
	}
	
	private Set<String> zrevrangebyscore0(Jedis j, String key, double max, double min) {
		return j.zrevrangeByScore(key, max, min);
	}

	@Override
	public Set<String> zrevrangebyscore(String key, String max, String min) {
		return (Set<String>) executeCommand(CommandEnum.ZREVRANGEBYSCORE_STRING, key, max, min);
	}
	
	private Set<String> zrevrangebyscore_string(Jedis j, String key, String max, String min) {
		return j.zrevrangeByScore(key, max, min);
	}

	@Override
	public Set<String> zrevrangebyscore(String key, double max, double min, int offset, int count) {
		return (Set<String>) executeCommand(CommandEnum.ZREVRANGEBYSCORE_OFFSET_COUNT, key, max, min, offset, count);
	}
	
	private Set<String> zrevrangebyscore_offset_count(Jedis j, String key, double max, double min, int offset, int count) {
		return j.zrevrangeByScore(key, max, min, offset, count);
	}

	@Override
	public Set<String> zrevrangebyscore(String key, String max, String min, int offset, int count) {
		return (Set<String>) executeCommand(CommandEnum.ZREVRANGEBYSCORE_OFFSET_COUNT_STRING, key, max, min, offset, count);
	}
	
	private Set<String> zrevrangebyscore_offset_count_string(Jedis j, String key, String max, String min, int offset, int count) {
		return j.zrevrangeByScore(key, max, min, offset, count);
	}
	
	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String key, double max, double min) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZREVRANGEBYSCORE_WITHSCORES, key, max, min);
	}
	
	private Map<String, Double> zrevrangebyscorewithscores0(Jedis j, String key, double max, double min) {
		Set<Tuple> set = j.zrevrangeByScoreWithScores(key, max, min);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String key, String max, String min) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZREVRANGEBYSCORE_WITHSCORES_STRING, key, max, min);
	}
	
	private Map<String, Double> zrevrangebyscorewithscores_string(Jedis j, String key, String max, String min) {
		Set<Tuple> set = j.zrevrangeByScoreWithScores(key, max, min);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String key, double max, double min, int offset, int count) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZREVRANGEBYSCORE_WITHSCORES_OFFSET_COUNT, key, max, min, offset, count);
	}
	
	private Map<String, Double> zrevrangebyscorewithscores_offset_count(Jedis j, String key, double max, double min, int offset, int count) {
		Set<Tuple> set = j.zrevrangeByScoreWithScores(key, max, min, offset, count);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String key, String max, String min, int offset, int count) {
		return (Map<String, Double>) executeCommand(CommandEnum.ZREVRANGEBYSCORE_WITHSCORES_OFFSET_COUNT_STRING, key, max, min, offset, count);
	}
	
	private Map<String, Double> zrevrangebyscorewithscores_offset_count_string(Jedis j, String key, String max, String min, int offset, int count) {
		Set<Tuple> set = j.zrevrangeByScoreWithScores(key, max, min, offset, count);
		Map<String, Double> map = convert4zrangewithscores(set);
		return map;
	}

	@Override
	public Long zrevrank(String key, String member) {
		return (Long) executeCommand(CommandEnum.ZREVRANK, key, member);
	}
	
	private Long zrevrank0(Jedis j, String key, String member) {
		return j.zrevrank(key, member);
	}

	@Override
	public Double zscore(String key, String member) {
		return (Double) executeCommand(CommandEnum.ZSCORE, key, member);
	}
	
	private Double zscore0(Jedis j, String key, String member) {
		return j.zscore(key, member);
	}
	
	@Override
	public Long zunionstore(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.ZUNIONSTORE, destination, keys);
	}
	
	private Long zunionstore0(Jedis j, String destination, String... keys) {
		return j.zunionstore(destination, keys);
	}

	@Override
	public Long zunionstoremax(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.ZUNIONSTORE_MAX, destination, keys);
	}
	
	private Long zunionstoremax0(Jedis j, String destination, String... keys) {
		return j.zunionstore(destination, new ZParams().aggregate(Aggregate.MAX), keys);
	}

	@Override
	public Long zunionstoremin(String destination, String... keys) {
		return (Long) executeCommand(CommandEnum.ZUNIONSTORE_MIN, destination, keys);
	}
	
	private Long zunionstoremin0(Jedis j, String destination, String... keys) {
		return j.zunionstore(destination, new ZParams().aggregate(Aggregate.MIN), keys);
	}

	@Override
	public Long zunionstore(String destination, Map<String, Integer> weightkeys) {
		return (Long) executeCommand(CommandEnum.ZUNIONSTORE_WEIGHTS, destination, weightkeys);
	}
	
	private Long zunionstore_weights(Jedis j, String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
		return j.zunionstore(destination, new ZParams().weights(weights), keys);
	}

	@Override
	public Long zunionstoremax(String destination, Map<String, Integer> weightkeys) {
		return (Long) executeCommand(CommandEnum.ZUNIONSTORE_WEIGHTS_MAX, destination, weightkeys);
	}
	
	private Long zunionstore_weights_max(Jedis j, String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
		return j.zunionstore(destination, new ZParams().weights(weights).aggregate(Aggregate.MAX), keys);
	}

	@Override
	public Long zunionstoremin(String destination, Map<String, Integer> weightkeys) {
		return (Long) executeCommand(CommandEnum.ZUNIONSTORE_WEIGHTS_MIN, destination, weightkeys);
	}
	
	private Long zunionstore_weights_min(Jedis j, String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
		return j.zunionstore(destination, new ZParams().weights(weights).aggregate(Aggregate.MIN), keys);
	}
	
	@Override
	public ScanResult<Map.Entry<String, Double>> zscan(String key, String cursor) {
		return (ScanResult<Map.Entry<String, Double>>) executeCommand(CommandEnum.ZSCAN, new Object[] { key, cursor });
	}
	
	private ScanResult<Map.Entry<String, Double>> zscan0(Jedis j, String key, String cursor) {
		redis.clients.jedis.ScanResult<Tuple> sr = j.zscan(key, cursor);
		return new ScanResult<Map.Entry<String, Double>>(sr.getStringCursor(), convert(sr.getResult()));
	}
	
	@Override
	public ScanResult<Map.Entry<String, Double>> zscan(String key, String cursor, int count) {
		return (ScanResult<Map.Entry<String, Double>>) executeCommand(CommandEnum.ZSCAN_COUNT, new Object[] { key, cursor, count });
	}
	
	private ScanResult<Map.Entry<String, Double>> zscan_count(Jedis j, String key, String cursor, int count) {
		ScanParams param = new ScanParams();
		param.count(count);
		redis.clients.jedis.ScanResult<Tuple> sr = j.zscan(key, cursor, param);
		return new ScanResult<Map.Entry<String, Double>>(sr.getStringCursor(), convert(sr.getResult()));
	}
	
	@Override
	public ScanResult<Map.Entry<String, Double>> zscan(String key, String cursor, String pattern) {
		return (ScanResult<Map.Entry<String, Double>>) executeCommand(CommandEnum.ZSCAN_MATCH, new Object[] { key, cursor, pattern });
	}
	
	private ScanResult<Map.Entry<String, Double>> zscan_match(Jedis j, String key, String cursor, String pattern) {
		ScanParams param = new ScanParams();
		param.match(pattern);
		redis.clients.jedis.ScanResult<Tuple> sr = j.zscan(key, cursor, param);
		return new ScanResult<Map.Entry<String, Double>>(sr.getStringCursor(), convert(sr.getResult()));
	}
	
	@Override
	public ScanResult<Map.Entry<String, Double>> zscan(String key, String cursor, String pattern, int count) {
		return (ScanResult<Map.Entry<String, Double>>) executeCommand(CommandEnum.ZSCAN_MATCH_COUNT, new Object[] { key, cursor, pattern, count });
	}
	
	private ScanResult<Map.Entry<String, Double>> zscan_match_count(Jedis j, String key, String cursor, String pattern, int count) {
		ScanParams param = new ScanParams();
		param.match(pattern);
		param.count(count);
		redis.clients.jedis.ScanResult<Tuple> sr = j.zscan(key, cursor, param);
		return new ScanResult<Map.Entry<String, Double>>(sr.getStringCursor(), convert(sr.getResult()));
	}
	
	private List<Map.Entry<String, Double>> convert(List<Tuple> list) {
		if (list == null || list.isEmpty()) return Collections.emptyList();
		
		List<Map.Entry<String, Double>> l = new ArrayList<Map.Entry<String,Double>>(list.size());
		for (Tuple tuple : list) {
			Map.Entry<String, Double> entry = new AbstractMap.SimpleEntry<String, Double>(tuple.getElement(), tuple.getScore());
			l.add(entry);
		}
		return l;
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------- HyperLogLog
	
	
	@Override
	public Long pfadd(String key, String... elements) {
		return (Long) executeCommand(CommandEnum.PFADD, new Object[] { key, elements });
	}
	
	private Long pfadd0(Jedis j, String key, String... elements) {
		return j.pfadd(key, elements);
	}
	
	@Override
	public Long pfcount(String... keys) {
		return (Long) executeCommand(CommandEnum.PFCOUNT, new Object[] { keys });
	}
	
	private Long pfcount0(Jedis j, String... keys) {
		return j.pfcount(keys);
	}
	
	
	// ~ ----------------------------------------------------------------------------------------------------- Pub/Sub
	
	
	@Override
	public RedisPubSub psubscribe(RedisPsubscribeHandler handler, String... patterns) {
		return psubscribe0(handler, patterns);
	}
	
	private RedisPubSub psubscribe0(final RedisPsubscribeHandler handler, final String... patterns) {
		final int permits = patterns.length;
		final Semaphore s = new Semaphore(permits);
		s.drainPermits();
		
		final JedisPubSub jps = new JedisPubSubAdapter() {

			@Override
			public void onPMessage(String pattern, String channel, String message) {
				handler.onMessage(pattern, channel, message);
			}

			@Override
			public void onPSubscribe(String pattern, int subscribedChannels) {
				s.release();
				handler.onPsubscribe(pattern, subscribedChannels);
			}
		};
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Jedis j = null;
				try {
					j = jedis();
					j.psubscribe(jps, patterns);
				} catch (Exception e) {
					RedisException re = handleException(e, j);
					handler.onException(re);
				} finally {
					s.release(permits);
				}
			}
		}, "redis-psubscribe-" + Thread.currentThread().getId());
		t.start();
		
		// wait all channels be subscribed
		try { s.acquire(permits); } catch (InterruptedException e) {}
		
		RedisPubSub rps = new DefaultRedisPubSub(jps);
		return rps;
	}

	@Override
	public Long publish(String channel, String message) {
		return (Long) executeCommand(CommandEnum.PUBLISH, channel, message);
	}
	
	private Long publish0(Jedis j, String channel, String message) {
		return j.publish(channel, message);
	}
	
	@Override
	public void punsubscribe(RedisPubSub pubsub, String... patterns) {
		executeCommand(CommandEnum.PUNSUBSCRIBE, new Object[] { pubsub, patterns });
	}
	
	private String punsubscribe0(DefaultRedisPubSub pubsub, String... patterns) {
		pubsub.punsubscribe(patterns);
		return OK;
	}
	
	@Override
	public RedisPubSub subscribe(RedisSubscribeHandler handler, String... channels) {
		return subscribe0(handler, channels);
	}
	
	private RedisPubSub subscribe0(final RedisSubscribeHandler handler, final String... channels) {
		final int permits = channels.length;
		final Semaphore s = new Semaphore(permits);
		s.drainPermits();
		
		final JedisPubSub jps = new JedisPubSubAdapter() {
			@Override
			public void onMessage(String channel, String message) {
				handler.onMessage(channel, message);
			}

			@Override
			public void onSubscribe(String channel, int subscribedChannels) {
				s.release();
				handler.onSubscribe(channel, subscribedChannels);
			}
		};
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Jedis j = null;
				try {
					j = jedis();
					j.subscribe(jps, channels);
				} catch (Exception e) {
					RedisException re = handleException(e, j);
					handler.onException(re);
				} finally {
					s.release(permits);
				}
			}
		}, "redis-subscribe-" + Thread.currentThread().getId());
		t.start();
		
		// wait all channels be subscribed
		try { s.acquire(permits); } catch (InterruptedException e) {}
		
		RedisPubSub rps = new DefaultRedisPubSub(jps);
		return rps;
	}
	
	@Override
	public void unsubscribe(RedisPubSub pubsub, String... channels) {
		executeCommand(CommandEnum.UNSUBSCRIBE, new Object[] { pubsub, channels });
	}
	
	private String unsubscribe0(DefaultRedisPubSub pubsub, String... channels) {
		pubsub.unsubscribe(channels);
		return OK;
	}
	
	@Override
	public List<String> pubsubchannels(String pattern) {
		if (pattern == null || pattern.equals("")) pattern = "*";
		return (List<String>) executeCommand(CommandEnum.PUBSUB_CHANNELS, new Object[] { pattern });
	}
	
	private List<String> pubsubchannels0(Jedis j, String pattern) {
		return j.pubsubChannels(pattern);
	}

	@Override
	public Long pubsubnumpat() {
		return (Long) executeCommand(CommandEnum.PUBSUB_NUMPAT);
	}
	
	private Long pubsubnumpat0(Jedis j) {
		return j.pubsubNumPat();
	}

	@Override
	public Map<String, String> pubsubnumsub(String... channels) {
		return (Map<String, String>) executeCommand(CommandEnum.PUBSUB_NUMSUB, new Object[] { channels });
	}
	
	private Map<String, String> pubsubnumsub0(Jedis j, String... channels) {
		return j.pubsubNumSub(channels);
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
	public String discard(RedisTransaction t) {
		return (String) executeCommand(CommandEnum.DISCARD, new Object[] { t });
	}
	
	private String discard0(DefaultRedisTransaction t) {
		try {
			return t.discard();
		} finally {
			unbind();
		}
	}

	@Override
	public List<Object> exec(RedisTransaction t) {
		return (List<Object>) executeCommand(CommandEnum.EXEC, new Object[] { t });
	}
	
	private List<Object> exec0(DefaultRedisTransaction t) {
		try {
			return t.exec();
		} finally {
			unbind();
		}
	}

	@Override
	public RedisTransaction multi() {
		return (RedisTransaction) executeCommand(CommandEnum.MULTI, new Object[] {});
	}
	
	private RedisTransaction multi0(Jedis j) {
		Transaction t = j.multi();
		bind(j);
		return new DefaultRedisTransaction(j, t, this);
	}

	@Override
	public String unwatch() {
		return (String) executeCommand(CommandEnum.UNWATCH, new Object[] {});
	}
	
	private String unwatch0(Jedis j) {
		try {
			return j.unwatch();
		} finally {
			unbind();
		}
	}

	@Override
	public String watch(String... keys) {
		return (String) executeCommand(CommandEnum.WATCH, new Object[] { keys });
	}
	
	private String watch0(Jedis j, String... keys) {
		String r = j.watch(keys);
		bind(j);
		return r;
	}
	
	private void bind(Jedis j) {
		THREAD_LOCAL_JEDIS.set(j);
	}
	
	private void unbind() {
		THREAD_LOCAL_JEDIS.remove();
	}
	
	private boolean isBound() {
		return THREAD_LOCAL_JEDIS.get() != null;
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
	
	private Object eval0(Jedis j, String script, List<String> keys, List<String> args) {
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
	
	private Object evalsha0(Jedis j, String script, List<String> keys, List<String> args) {
		return j.evalsha(script, keys, args);
	}

	@Override
	public Boolean scriptexists(String sha1) {
		return scriptexists(new String[] { sha1 })[0];
	}
	
	@Override
	public Boolean[] scriptexists(String... sha1) {
		return (Boolean[]) executeCommand(CommandEnum.SCRIPT_EXISTS, new Object[] { sha1 });
	}
	
	private Boolean[] scriptexists0(Jedis j, String... sha1) {
		return j.scriptExists(sha1).toArray(new Boolean[sha1.length]);
	}

	@Override
	public String scriptflush() {
		return (String) executeCommand(CommandEnum.SCRIPT_FLUSH);
	}
	
	private String scriptflush0(Jedis j) {
		return j.scriptFlush();
	}

	@Override
	public String scriptkill() {
		return (String) executeCommand(CommandEnum.SCRIPT_KILL);
	}
	
	private String scriptkill0(Jedis j) {
		return j.scriptKill();
	}
	
	@Override
	public String scriptload(String script) {
		return (String) executeCommand(CommandEnum.SCRIPT_LOAD, script);
	}
	
	private String scriptload0(Jedis j, String script) {
		return j.scriptLoad(script);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------- Connection
	

	@Override
	public String auth(String password) {
		return auth0(password);
	}
	
	private String auth0(String password) {
		pool.destroy();
		unbind();
		this.password = password;
		pool = new JedisPool(convert(poolConfig), host, port, timeoutInMillis, password, database);
		return OK;
	}
	
	@Override
	public String echo(String message) {
		return (String) executeCommand(CommandEnum.ECHO, message);
	}

	private String echo0(Jedis j, String message) {
		return j.echo(message);
	}

	@Override
	public String ping() {
		return (String) executeCommand(CommandEnum.PING);
	}
	
	private String ping0(Jedis j) {
		return j.ping();
	}

	@Override
	public String quit() {
		return quit0();
	}
	
	private String quit0() {
		pool.destroy();
		return OK;
	}

	@Override
	public String select(int index) {
		return select0(index);
	}
	
	private String select0(int index) {
		if (database == index) {
			return OK;
		}
		
		this.database = index;
		pool = new JedisPool(convert(poolConfig), host, port, timeoutInMillis, password, database);
		return OK;
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Server
	

	@Override
	public String bgrewriteaof() {
		return (String) executeCommand(CommandEnum.BGREWRITEAOF);
	}
	
	private String bgrewriteaof0(Jedis j) {
		return j.bgrewriteaof();
	}

	@Override
	public String bgsave() {
		return (String) executeCommand(CommandEnum.BGSAVE);
	}
	
	private String bgsave0(Jedis j) {
		return j.bgsave();
	}

	@Override
	public String clientgetname() {
		return (String) executeCommand(CommandEnum.CLIENT_GETNAME);
	}
	
	private String clientgetname0(Jedis j) {
		return j.clientGetname();
	}

	@Override
	public String clientkill(String ip, int port) {
		return (String) executeCommand(CommandEnum.CLIENT_KILL, ip, port);
	}
	
	private String clientkill0(Jedis j, String ip, int port) {
		return j.clientKill(ip + ":" + port);
	}

	@Override
	public List<String> clientlist() {
		return (List<String>) executeCommand(CommandEnum.CLIENT_LIST);
	}
	
	private List<String> clientlist0(Jedis j) {
		String lines = j.clientList();
		String[] sarr = lines.split("\n");
		return Arrays.asList(sarr);
	}

	@Override
	public String clientsetname(String connectionname) {
		return (String) executeCommand(CommandEnum.CLIENT_SETNAME, connectionname);
	}
	
	private String clientsetname0(Jedis j, String connectionname) {
		return j.clientSetname(connectionname);
	}

	@Override
	public Map<String, String> configget(String parameter) {
		return (Map<String, String>) executeCommand(CommandEnum.CONFIG_GET, parameter);
	}
	
	private Map<String, String> configget0(Jedis j, String parameter) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		List<String> l = j.configGet(parameter);
		for (int i = 0; i < l.size(); i += 2) {
			String name = l.get(i);
			String value = null;
			if (i + 1 < l.size()) {
				value = l.get(i + 1);
				value = ("".equals(value) ? null : value);
			}
			map.put(name, value);
		}
		return map;
	}

	@Override
	public String configresetstat() {
		return (String) executeCommand(CommandEnum.CONFIG_RESETSTAT);
	}
	
	private String configresetstat0(Jedis j) {
		return j.configResetStat();
	}

	@Override
	public String configset(String parameter, String value) {
		return (String) executeCommand(CommandEnum.CONFIG_SET, parameter, value);
	}
	
	private String configset0(Jedis j, String parameter, String value) {
		return j.configSet(parameter, value);
	}

	@Override
	public Long dbsize() {
		return (Long) executeCommand(CommandEnum.DBSIZE);
	}
	
	private Long dbsize0(Jedis j) {
		return j.dbSize();
	}
	
	@Override
	public String debugobject(String key) {
		return (String) executeCommand(CommandEnum.DEBUG_OBJECT, key);
	}
	
	private String debugobject0(Jedis j, String key) {
		return debug0(j, DebugParams.OBJECT(key));
	}

	@Override
	public String debugsegfault() {
		return (String) executeCommand(CommandEnum.DEBUG_SEGFAULT);
	}
	
	private String debugsegfault0(Jedis j) {
		return debug0(j, DebugParams.SEGFAULT());
	}
	
	private String debug0(Jedis j, DebugParams params) {
		return j.debug(params);
	}

	@Override
	public String flushall() {
		return (String) executeCommand(CommandEnum.FLUSH_ALL);
	}
	
	private String flushall0(Jedis j) {
		return j.flushAll();
	}

	@Override
	public String flushdb() {
		return (String) executeCommand(CommandEnum.FLUSH_DB);
	}
	
	private String flushdb0(Jedis j) {
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
	
	private String info0(Jedis j, String section) {
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
	
	private Long lastsave0(Jedis j) {
		return j.lastsave();
	}

	@Override
	public void monitor(RedisMonitorHandler handler) {
		executeCommand(CommandEnum.MONITOR, handler);
	}
	
	private String monitor0(Jedis j, final RedisMonitorHandler handler) {
		JedisMonitor jm = new JedisMonitor() {
			@Override
			public void onCommand(String command) {
				handler.onCommand(command);
			}
			
		};
		j.monitor(jm);
		return OK;
	}

	@Override
	public String save() {
		return (String) executeCommand(CommandEnum.SAVE);
	}
	
	private String save0(Jedis j) {
		return j.save();
	}

	@Override
	public String shutdown(boolean save) {
		return (String) executeCommand(CommandEnum.SHUTDOWN, save);
	}
	
	private String shutdown0(Jedis j, boolean save) {
		return j.shutdown();
	}

	@Override
	public String slaveof(String host, int port) {
		return (String) executeCommand(CommandEnum.SLAVEOF, host, port);
	}
	
	private String slaveof0(Jedis j, String host, int port) {
		return j.slaveof(host, port);
	}

	@Override
	public String slaveofnoone() {
		return (String) executeCommand(CommandEnum.SLAVEOF_NONOE);
	}
	
	private String slaveofnoone(Jedis j) {
		return j.slaveofNoOne();
	}

	@Override
	public List<Slowlog> slowlogget() {
		return (List<Slowlog>) executeCommand(CommandEnum.SLOWLOG_GET);
	}
	
	private List<Slowlog> slowlogget0(Jedis j) {
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
		return (List<Slowlog>) executeCommand(CommandEnum.SLOWLOG_GET_LEN, len);
	}
	
	private List<Slowlog> slowlogget0(Jedis j, long len) {
		List<redis.clients.util.Slowlog> logs = j.slowlogGet(len);
		return convert4slowlog(logs);
	}

	@Override
	public String slowlogreset() {
		return (String) executeCommand(CommandEnum.SLOWLOG_RESET);
	}
	
	private String slowlogreset0(Jedis j) {
		return j.slowlogReset();
	}

	@Override
	public Long slowloglen() {
		return (Long) executeCommand(CommandEnum.SLOWLOG_LEN);
	}
	
	private Long slowloglen0(Jedis j) {
		return j.slowlogLen();
	}

	@Override
	public void sync() {
		executeCommand(CommandEnum.SYNC);
	}
	
	private String sync0(Jedis j) {
		j.sync();
		return OK;
	}

	@Override
	public Long time() {
		return (Long) executeCommand(CommandEnum.TIME);
	}
	
	private Long time0(Jedis j) {
		List<String> l = j.time();
		return Long.parseLong(l.get(0)) * 1000 + Long.parseLong(l.get(1)) / 1000;
	}

	@Override
	public Long microtime() {
		return (Long) executeCommand(CommandEnum.TIME_MICRO);
	}
	
	private Long microtime0(Jedis j) {
		List<String> l = j.time();
		return Long.parseLong(l.get(0)) * 1000 * 1000 + Long.parseLong(l.get(1));
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	private Object executeCommand(CommandEnum cmd, Object... args) {
		Jedis j = null;
		
		try {
			j = jedis();
			switch (cmd) {
			// Keys
			case DEL:
				return del0(j, (String[]) args[0]);
			case DUMP:         
				return dump0(j, (String) args[0]);   
			case EXISTS:
				return exists0(j, (String) args[0]);
			case EXPIRE:
				return expire0(j, (String) args[0], (Integer) args[1]);
			case EXPIREAT:
				return expireat0(j, (String) args[0], (Long) args[1]);
			case KEYS:
				return keys0(j, (String) args[0]);
			case MIGRATE: 
				return migrate0(j, (String) args[0], (Integer) args[1], (String) args[2], (Integer) args[3], (Integer) args[4]);
			case MOVE:
				return move0(j, (String) args[0], (Integer) args[1]);
			case OBJECT_REFCOUNT:
				return objectrefcount0(j, (String) args[0]);
			case OBJECT_ENCODING:
				return objectencoding0(j, (String) args[0]);
			case OBJECT_IDLETIME:
				return objectidletime0(j, (String) args[0]);
			case PERSIST:
				return persist0(j, (String) args[0]);
			case PEXPIRE: 
				return pexpire0(j, (String) args[0], (Long) args[1]);
			case PEXPIREAT: 
				return pexpireat0(j, (String) args[0], (Long) args[1]);
			case PTTL: 
				return pttl0(j, (String) args[0]);
			case RANDOMKEY:
				return randomkey0(j);
			case RENAME:
				return rename0(j, (String) args[0], (String) args[1]);
			case RENAMENX:
				return renamenx0(j, (String) args[0], (String) args[1]);
			case RESTORE:
				return restore0(j, (String) args[0], (Integer) args[1], (byte[]) args[2]);
			case SORT:
				return sort0(j, (String) args[0]);
			case SORT_DESC:
				return sort_desc(j, (String) args[0], (Boolean) args[1]);
			case SORT_ALPHA_DESC:
				return sort_alpha_desc(j, (String) args[0], (Boolean) args[1], (Boolean) args[2]);
			case SORT_OFFSET_COUNT:
				return sort_offset_count(j, (String) args[0], (Integer) args[1], (Integer) args[2]);
			case SORT_OFFSET_COUNT_ALPHA_DESC:
				return sort_offset_count_alpha_desc(j, (String) args[0], (Integer) args[1], (Integer) args[2], (Boolean) args[3], (Boolean) args[4]);
			case SORT_BY_GET:
				return sort_by_get(j, (String) args[0], (String) args[1], (String[]) args[2]);
			case SORT_BY_DESC_GET:
				return sort_by_desc_get(j, (String) args[0], (String) args[1], (Boolean) args[2], (String[]) args[3]);
			case SORT_BY_ALPHA_DESC_GET:
				return sort_by_alpha_desc_get(j, (String) args[0], (String) args[1], (Boolean) args[2], (Boolean) args[3], (String[]) args[4]);
			case SORT_BY_OFFSET_COUNT_GET:
				return sort_by_offset_count_get(j, (String) args[0], (String) args[1], (Integer) args[2], (Integer) args[3], (String[]) args[4]);
			case SORT_BY_OFFSET_COUNT_ALPHA_DESC_GET:
				return sort_by_offset_count_alpha_desc_get(j, (String) args[0], (String) args[1], (Integer) args[2], (Integer) args[3], (Boolean) args[4], (Boolean) args[5], (String[]) args[6]);
			case SORT_DESTINATION:
				return sort_destination(j, (String) args[0], (String) args[1]);
			case SORT_DESC_DESTINATION:
				return sort_desc_destination(j, (String) args[0], (Boolean) args[1], (String) args[2]);
			case SORT_ALPHA_DESC_DESTINATION:
				return sort_alpha_desc_destination(j, (String) args[0], (Boolean) args[1], (Boolean) args[2], (String) args[3]);
			case SORT_OFFSET_COUNT_DESTINATION:
				return sort_offset_count_destination(j, (String) args[0], (Integer) args[1], (Integer) args[2], (String) args[3]);
			case SORT_OFFSET_COUNT_ALPHA_DESC_DESTINATION:
				return sort_offset_count_alpha_desc_destination(j, (String) args[0], (Integer) args[1], (Integer) args[2], (Boolean) args[3], (Boolean) args[4], (String) args[5]);
			case SORT_BY_DESTINATION_GET:
				return sort_by_destination_get(j, (String) args[0], (String) args[1], (String) args[2], (String[]) args[3]);
			case SORT_BY_DESC_DESTINATION_GET:
				return sort_by_desc_destination_get(j, (String) args[0], (String) args[1], (Boolean) args[2], (String) args[3], (String[]) args[4]);
			case SORT_BY_ALPHA_DESC_DESTINATION_GET:
				return sort_by_alpha_desc_destination_get(j, (String) args[0], (String) args[1], (Boolean) args[2], (Boolean) args[3], (String) args[4], (String[]) args[5]);
			case SORT_BY_OFFSET_COUNT_DESTINATION_GET:
				return sort_by_offset_count_destination_get(j, (String) args[0], (String) args[1], (Integer) args[2], (Integer) args[3], (String) args[4], (String[]) args[5]);
			case SORT_BY_OFFSET_COUNT_ALPHA_DESC_DESTINATION_GET:				
				return sort_by_offset_count_alpha_desc_destination_get(j, (String) args[0], (String) args[1], (Integer) args[2], (Integer) args[3], (Boolean) args[4], (Boolean) args[5], (String) args[6], (String[]) args[7]);
			case TTL:
				return ttl0(j, (String) args[0]);
			case TYPE:
				return type0(j, (String) args[0]);
			case SCAN:
				return scan0(j, (String) args[0]);
			case SCAN_COUNT:
				return scan_count(j, (String) args[0], (Integer) args[1]);
			case SCAN_MATCH:
				return scan_match(j, (String) args[0], (String) args[1]);
			case SCAN_MATCH_COUNT:
				return scan_match_count(j, (String) args[0], (String) args[1], (Integer) args[2]);
				
				
			// Strings			
			case APPEND:
				return append0(j, (String) args[0], (String) args[1]);
			case BITCOUNT:
				return bitcount0(j, (String) args[0]);
			case BITCOUNT_START_END:
				return bitcount0(j, (String) args[0], (Long) args[1], (Long) args[2]);
			case BITNOT:
				return bitnot0(j, (String) args[0], (String) args[1]);
			case BITAND:
				return bitand0(j, (String) args[0], (String[]) args[1]);
			case BITOR:
				return bitor0(j, (String) args[0], (String[]) args[1]);
			case BITXOR:
				return bitxor0(j, (String) args[0], (String[]) args[1]);
			case BITPOS:
				return bitpos0(j, (String) args[0], (Boolean) args[1]);
			case BITPOS_START:
				return bitpos0(j, (String) args[0], (Boolean) args[1], (Long) args[2]);
			case BITPOS_START_END:
				return bitpos0(j, (String) args[0], (Boolean) args[1], (Long) args[2], (Long) args[3]); 
			case DECR:
				return decr0(j, (String) args[0]);
			case DECRBY:
				return decrby0(j, (String) args[0], (Long) args[1]);
			case GET:
				return get0(j, (String) args[0]);
			case GETBIT:
				return getbit0(j, (String) args[0], (Long) args[1]);
			case GETRANGE:
				return getrange0(j, (String) args[0], (Long) args[1], (Long) args[2]);
			case GETSET:
				return getset0(j, (String) args[0], (String) args[1]);
			case INCR:
				return incr0(j, (String) args[0]);
			case INCRBY:
				return incrby0(j, (String) args[0], (Long) args[1]);
			case INCRBYFLOAT:
				return incrbyfloat0(j, (String) args[0], (Double) args[1]);
			case MGET:
				return mget0(j, (String[]) args[0]);
			case MSET:
				return mset0(j, (String[]) args[0]);
			case MSETNX:
				return msetnx0(j, (String[]) args[0]);
			case PSETEX:
				return psetex0(j, (String) args[0], (Integer) args[1], (String) args[2]);
			case SET:
				return set0(j, (String) args[0], (String) args[1]);
			case SETXX:
				return setxx0(j, (String) args[0], (String) args[1]);
			case SETNXEX:
				return setnxex0(j, (String) args[0], (String) args[1], (Integer) args[2]);
			case SETNXPX:
				return setnxpx0(j, (String) args[0], (String) args[1], (Integer) args[2]);
			case SETXXEX:
				return setxxex0(j, (String) args[0], (String) args[1], (Integer) args[2]);
			case SETXXPX:
				return setxxpx0(j, (String) args[0], (String) args[1], (Integer) args[2]);
			case SETBIT:
				return setbit0(j, (String) args[0], (Long) args[1], (Boolean) args[2]);
			case SETEX:
				return setex0(j, (String) args[0], (Integer) args[1], (String) args[2]);
			case SETNX:
				return setnx0(j, (String) args[0], (String) args[1]);
			case SETRANGE:
				return setrange0(j, (String) args[0], (Long) args[1], (String) args[2]);
			case STRLEN:
				return strlen0(j, (String) args[0]);
				
			// Hashes
			case HDEL:
				return hdel0(j, (String) args[0], (String[]) args[1]);
			case HEXISTS:
				return hexists0(j, (String) args[0], (String) args[1]);
			case HGET:
				return hget0(j, (String) args[0], (String) args[1]);
			case HGETALL:
				return hgetall0(j, (String) args[0]);
			case HINCRBY:
				return hincrby0(j, (String) args[0], (String) args[1], (Long) args[2]);
			case HINCRBYFLOAT:
				return hincrbyfloat0(j, (String) args[0], (String) args[1], (Double) args[2]);
			case HKEYS:
				return hkeys0(j, (String) args[0]);
			case HLEN:
				return hlen0(j, (String) args[0]);
			case HMGET:
				return hmget0(j, (String) args[0], (String[]) args[1]);
			case HMSET:
				return hmset0(j, (String) args[0], (Map<String, String>) args[1]);
			case HSET:
				return hset0(j, (String) args[0], (String) args[1], (String) args[2]);
			case HSETNX:
				return hsetnx0(j, (String) args[0], (String) args[1], (String) args[2]);
			case HVALS:
				return hvals0(j, (String) args[0]);
			case HSCAN:
				return hscan0(j, (String) args[0], (String) args[1]);
			case HSCAN_COUNT:
				return hscan_count(j, (String) args[0], (String) args[1], (Integer) args[2]);
			case HSCAN_MATCH:
				return hscan_match(j, (String) args[0], (String) args[1], (String) args[2]);
			case HSCAN_MATCH_COUNT:
				return hscan_match_count(j, (String) args[0], (String) args[1], (String) args[2], (Integer) args[3]);
				
			// Lists
			case BLPOP:
				return blpop0(j, (Integer) args[0], (String[]) args[1]);
			case BRPOP:
				return brpop0(j, (Integer) args[0], (String[]) args[1]);
			case BRPOPLPUSH:
				return brpoplpush0(j, (String) args[0], (String) args[1], (Integer) args[2]);
			case LINDEX:
				return lindex0(j, (String) args[0], (Long) args[1]);
			case LINSERT_BEFORE:
				return linsertbefore0(j, (String) args[0], (String) args[1], (String) args[2]);
			case LINSERT_AFTER:
				return linsertafter0(j, (String) args[0], (String) args[1], (String) args[2]);
			case LLEN:
				return llen0(j, (String) args[0]);
			case LPOP:
				return lpop0(j, (String) args[0]);
			case LPUSH:
				return lpush0(j, (String) args[0], (String[]) args[1]);
			case LPUSHX:
				return lpushx0(j, (String) args[0], (String) args[1]);
			case LRANGE:
				return lrange0(j, (String) args[0], (Long) args[1], (Long) args[2]);
			case LREM:
				return lrem0(j, (String) args[0], (Long) args[1], (String) args[2]);
			case LSET:
				return lset0(j, (String) args[0], (Long) args[1], (String) args[2]);
			case LTRIM:
				return ltrim0(j, (String) args[0], (Long) args[1], (Long) args[2]);
			case RPOP:
				return rpop0(j, (String) args[0]);
			case RPOPLPUSH:
				return rpoplpush0(j, (String) args[0], (String) args[1]);
			case RPUSH:
				return rpush0(j, (String) args[0], (String[]) args[1]);
			case RPUSHX:
				return rpushx0(j, (String) args[0], (String) args[1]);
				
			// Sets
			case SADD:
				return sadd0(j, (String) args[0], (String[]) args[1]);
			case SCARD:
				return scard0(j, (String) args[0]);
			case SDIFF:
				return sdiff0(j, (String[]) args[0]);
			case SDIFFSTORE:
				return sdiffstore0(j, (String) args[0], (String[]) args[1]);
			case SINTER:
				return sinter0(j, (String[]) args[0]);
			case SINTERSTORE:
				return sinterstore0(j, (String) args[0], (String[]) args[1]);
			case SISMEMBER:
				return sismember0(j, (String) args[0], (String) args[1]);
			case SMEMBERS:
				return smembers0(j, (String) args[0]);
			case SMOVE:
				return smove0(j, (String) args[0], (String) args[1], (String) args[2]);
			case SPOP:
				return spop0(j, (String) args[0]);
			case SRANDMEMBER:
				return srandmember0(j, (String) args[0], (Integer) args[1]);
			case SREM:
				return srem0(j, (String) args[0], (String[]) args[1]);
			case SUNION:
				return sunion0(j, (String[]) args[0]);
			case SUNIONSTORE:
				return sunionstore0(j, (String) args[0], (String[]) args[1]);
			case SSCAN:
				return sscan0(j, (String) args[0], (String) args[1]);
			case SSCAN_COUNT:
				return sscan_count(j, (String) args[0], (String) args[1], (Integer) args[2]);
			case SSCAN_MATCH:
				return sscan_match(j, (String) args[0], (String) args[1], (String) args[2]);
			case SSCAN_MATCH_COUNT:
				return sscan_match_count(j, (String) args[0], (String) args[1], (String) args[2], (Integer) args[3]);
				
			// Sorted Set
			case ZADD:
				return zadd0(j, (String) args[0], (Map<String, Double>) args[1]);
			case ZCARD:
				return zcard0(j, (String) args[0]);
			case ZCOUNT:
				return zcount0(j, (String) args[0], (Double) args[1], (Double) args[2]);
			case ZCOUNT_STRING:
				return zcount0(j, (String) args[0], (String) args[1], (String) args[2]);
			case ZINCRBY:
				return zincrby0(j, (String) args[0], (Double) args[1], (String) args[2]);
			case ZINTERSTORE:
				return zinterstore0(j, (String) args[0], (String[]) args[1]);
			case ZINTERSTORE_MAX:
				return zinterstoremax0(j, (String) args[0], (String[]) args[1]);
			case ZINTERSTORE_MIN:
				return zinterstoremin0(j, (String) args[0], (String[]) args[1]);
			case ZINTERSTORE_WEIGHTS:
				return zinterstore_weights(j, (String) args[0], (Map<String, Integer>) args[1]);
			case ZINTERSTORE_WEIGHTS_MAX:
				return zinterstore_weights_max(j, (String) args[0], (Map<String, Integer>) args[1]);
			case ZINTERSTORE_WEIGHTS_MIN:
				return zinterstore_weights_min(j, (String) args[0], (Map<String, Integer>) args[1]);
			case ZRANGE:
				return zrange0(j, (String) args[0], (Long) args[1], (Long) args[2]);
			case ZRANGE_WITHSCORES:
				return zrangewithscores0(j, (String) args[0], (Long) args[1], (Long) args[2]);
			case ZRANGEBYSCORE:
				return zrangebyscore0(j, (String) args[0], (Double) args[1], (Double) args[2]);
			case ZRANGEBYSCORE_STRING:
				return zrangebyscore_string(j, (String) args[0], (String) args[1], (String) args[2]);
			case ZRANGEBYSCORE_OFFSET_COUNT:
				return zrangebyscore_offset_count(j, (String) args[0], (Double) args[1], (Double) args[2], (Integer) args[3], (Integer) args[4]);
			case ZRANGEBYSCORE_OFFSET_COUNT_STRING:
				return zrangebyscore_offset_count_string(j, (String) args[0], (String) args[1], (String) args[2], (Integer) args[3], (Integer) args[4]);
			case ZRANGEBYSCORE_WITHSCORES:
				return zrangebyscorewithscores0(j, (String) args[0], (Double) args[1], (Double) args[2]);
			case ZRANGEBYSCORE_WITHSCORES_STRING:
				return zrangebyscorewithscores_string(j, (String) args[0], (String) args[1], (String) args[2]);
			case ZRANGEBYSCORE_WITHSCORES_OFFSET_COUNT:
				return zrangebyscorewithscores_offset_count(j, (String) args[0], (Double) args[1], (Double) args[2], (Integer) args[3], (Integer) args[4]);
			case ZRANGEBYSCORE_WITHSCORES_OFFSET_COUNT_STRING:
				return zrangebyscorewithscores_offset_count_string(j, (String) args[0], (String) args[1], (String) args[2], (Integer) args[3], (Integer) args[4]);
			case ZRANK:
				return zrank0(j, (String) args[0], (String) args[1]);
			case ZREM:
				return zrem0(j, (String) args[0], (String[]) args[1]);
			case ZREMRANGEBYRANK:
				return zremrangebyrank0(j, (String) args[0], (Long) args[1], (Long) args[2]);
			case ZREMRANGEBYSCORE:
				return zremrangebyscore0(j, (String) args[0], (Double) args[1], (Double) args[2]);
			case ZREMRANGEBYSCORE_STRING:
				return zremrangebyscore_string(j, (String) args[0], (String) args[1], (String) args[2]);
			case ZREVRANGE:
				return zrevrange0(j, (String) args[0], (Long) args[1], (Long) args[2]);
			case ZREVRANGE_WITHSCORES:
				return zrevrangewithscores0(j, (String) args[0], (Long) args[1], (Long) args[2]);
			case ZREVRANGEBYSCORE:
				return zrevrangebyscore0(j, (String) args[0], (Double) args[1], (Double) args[2]);
			case ZREVRANGEBYSCORE_STRING:
				return zrevrangebyscore_string(j, (String) args[0], (String) args[1], (String) args[2]);
			case ZREVRANGEBYSCORE_OFFSET_COUNT:
				return zrevrangebyscore_offset_count(j, (String) args[0], (Double) args[1], (Double) args[2], (Integer) args[3], (Integer) args[4]);
			case ZREVRANGEBYSCORE_OFFSET_COUNT_STRING:
				return zrevrangebyscore_offset_count_string(j, (String) args[0], (String) args[1], (String) args[2], (Integer) args[3], (Integer) args[4]);
			case ZREVRANGEBYSCORE_WITHSCORES:
				return zrevrangebyscorewithscores0(j, (String) args[0], (Double) args[1], (Double) args[2]);
			case ZREVRANGEBYSCORE_WITHSCORES_STRING:
				return zrevrangebyscorewithscores_string(j, (String) args[0], (String) args[1], (String) args[2]);
			case ZREVRANGEBYSCORE_WITHSCORES_OFFSET_COUNT:
				return zrevrangebyscorewithscores_offset_count(j, (String) args[0], (Double) args[1], (Double) args[2], (Integer) args[3], (Integer) args[4]);
			case ZREVRANGEBYSCORE_WITHSCORES_OFFSET_COUNT_STRING:
				return zrevrangebyscorewithscores_offset_count_string(j, (String) args[0], (String) args[1], (String) args[2], (Integer) args[3], (Integer) args[4]);
			case ZREVRANK:
				return zrevrank0(j, (String) args[0], (String) args[1]);
			case ZSCORE:
				return zscore0(j, (String) args[0], (String) args[1]);
			case ZUNIONSTORE:
				return zunionstore0(j, (String) args[0], (String[]) args[1]);
			case ZUNIONSTORE_MAX:
				return zunionstoremax0(j, (String) args[0], (String[]) args[1]);
			case ZUNIONSTORE_MIN:
				return zunionstoremin0(j, (String) args[0], (String[]) args[1]);
			case ZUNIONSTORE_WEIGHTS:
				return zunionstore_weights(j, (String) args[0], (Map<String, Integer>) args[1]);
			case ZUNIONSTORE_WEIGHTS_MAX:
				return zunionstore_weights_max(j, (String) args[0], (Map<String, Integer>) args[1]);
			case ZUNIONSTORE_WEIGHTS_MIN:
				return zunionstore_weights_min(j, (String) args[0], (Map<String, Integer>) args[1]);
			case ZSCAN:
				return zscan0(j, (String) args[0], (String) args[1]);
			case ZSCAN_COUNT:
				return zscan_count(j, (String) args[0], (String) args[1], (Integer) args[2]);
			case ZSCAN_MATCH:
				return zscan_match(j, (String) args[0], (String) args[1], (String) args[2]);
			case ZSCAN_MATCH_COUNT:
				return zscan_match_count(j, (String) args[0], (String) args[1], (String) args[2], (Integer) args[3]);
				
			// HyperLogLog
			case PFADD:
				return pfadd0(j, (String) args[0], (String[]) args[1]);
			case PFCOUNT:
				return pfcount0(j, (String[]) args[0]);
			
			// Pub/Sub
			case PUBLISH:
				return publish0(j, (String) args[0], (String) args[1]);
			case PUNSUBSCRIBE:
				return punsubscribe0((DefaultRedisPubSub) args[0], (String[]) args[1]);
			case UNSUBSCRIBE:
				return unsubscribe0((DefaultRedisPubSub) args[0], (String[]) args[1]);
			case PUBSUB_CHANNELS:
				return pubsubchannels0(j, (String) args[0]);
			case PUBSUB_NUMSUB:
				return pubsubnumsub0(j, (String[]) args[0]);
			case PUBSUB_NUMPAT:
				return pubsubnumpat0(j);
				
			// Transactions
			case DISCARD:
				return discard0((DefaultRedisTransaction) args[0]);
			case EXEC:
				return exec0((DefaultRedisTransaction) args[0]);
			case MULTI:
				return multi0(j);
			case UNWATCH:
				return unwatch0(j);
			case WATCH:
				return watch0(j, (String[]) args[0]);
				
			// Scripting
			case EVAL:
				return eval0(j, (String) args[0], (List<String>) args[1], (List<String>) args[2]);
			case EVALSHA:
				return evalsha0(j, (String) args[0], (List<String>) args[1], (List<String>) args[2]);
			case SCRIPT_EXISTS:
				return scriptexists0(j, (String[]) args[0]);
			case SCRIPT_FLUSH:
				return scriptflush0(j);
			case SCRIPT_KILL:
				return scriptkill0(j);
			case SCRIPT_LOAD:
				return scriptload0(j, (String) args[0]);
				
			// Connection
			case ECHO:
				return echo0(j, (String) args[0]);
			case PING:
				return ping0(j);
				
			// Server
			case BGREWRITEAOF:
				return bgrewriteaof0(j);
			case BGSAVE:
				return bgsave0(j);
			case CLIENT_KILL:
				return clientkill0(j, (String) args[0], (Integer) args[1]);
			case CLIENT_LIST:
				return clientlist0(j);
			case CLIENT_GETNAME:
				return clientgetname0(j);
			case CLIENT_SETNAME:
				return clientsetname0(j, (String) args[0]);
			case CONFIG_GET:
				return configget0(j, (String) args[0]);
			case CONFIG_SET:
				return configset0(j, (String) args[0], (String) args[1]);
			case CONFIG_RESETSTAT:
				return configresetstat0(j);
			case DBSIZE:
				return dbsize0(j);
			case DEBUG_OBJECT:
				return debugobject0(j, (String) args[0]);
			case DEBUG_SEGFAULT:
				return debugsegfault0(j);
			case FLUSH_ALL:
				return flushall0(j);
			case FLUSH_DB:
				return flushdb0(j);
			case INFO:
				return info0(j, (String) args[0]);
			case LAST_SAVE:
				return lastsave0(j);
			case MONITOR:
				return monitor0(j, (RedisMonitorHandler) args[0]);
			case SAVE:
				return save0(j);
			case SHUTDOWN:
				return shutdown0(j, (Boolean) args[0]);
			case SLAVEOF:
				return slaveof0(j, (String) args[0], (Integer) args[1]);
			case SLAVEOF_NONOE:
				return slaveofnoone(j);
			case SLOWLOG_LEN:
				return slowloglen0(j);
			case SLOWLOG_GET:
				return slowlogget0(j);
			case SLOWLOG_GET_LEN:
				return slowlogget0(j, (Long) args[0]);
			case SLOWLOG_RESET:
				return slowlogreset0(j);
			case SYNC:
				return sync0(j);
			case TIME: 
				return time0(j);
			case TIME_MICRO:
				return microtime0(j);
			default:
				throw new IllegalArgumentException("Wrong command");
			}
		} catch (Exception e) {
			RedisException re = handleException(e, j, args);
			if (re instanceof RedisConnectionException) {
				j = null;
			}
			throw re;
		} finally {
			release(j);
		}
	}
	
	RedisException handleException(Exception e, Jedis j, Object... args) {
		unbind();
		
		if (e instanceof JedisConnectionException) {
			if (j != null) {
				pool.returnBrokenResource(j);
			}
			return new RedisConnectionException(String.format("Connect to redis server<host=%s, port=%s> failed.", host, port), e);
		}
		
		if (e instanceof JedisDataException) {
			return new RedisDataException(String.format("Redis data <args=%s> process failed.", Arrays.toString(args)), e);
		}
		
		return new RedisException(e);
	}
	
	private Jedis jedis() {
		Jedis j = THREAD_LOCAL_JEDIS.get();	
		
		if (j == null) {
			j = pool.getResource();
		}
		
		return j;
	}
	
	private void release(Jedis j) {
		if (isBound()) {
			// in transaction context don't return jedis connection to pool.
			return;
		} else {
			if (j != null) {
				pool.returnResource(j);
			}
		}
	}

}

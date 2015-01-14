package io.craft.atom.redis;

import io.craft.atom.redis.api.MasterSlaveRedis;
import io.craft.atom.redis.api.Redis;
import io.craft.atom.redis.api.RedisConnectionException;
import io.craft.atom.redis.api.RedisPubSub;
import io.craft.atom.redis.api.RedisTransaction;
import io.craft.atom.redis.api.ScanResult;
import io.craft.atom.redis.api.Slowlog;
import io.craft.atom.redis.api.handler.RedisMonitorHandler;
import io.craft.atom.redis.api.handler.RedisPsubscribeHandler;
import io.craft.atom.redis.api.handler.RedisSubscribeHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.ToString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author mindwind
 * @version 1.0, Jun 26, 2013
 */
@ToString
public class DefaultMasterSlaveRedis implements MasterSlaveRedis {


	private static final Logger LOG = LoggerFactory.getLogger(DefaultMasterSlaveRedis.class);


	private          List<Redis>  chain    ;
	private volatile int          index    ;
	private volatile boolean      readSlave;


	// ~ ---------------------------------------------------------------------------------------------------------


	public DefaultMasterSlaveRedis(List<Redis> chain) {
		this(chain, 0);
	}

	public DefaultMasterSlaveRedis(List<Redis> chain, int index) {
		if (chain == null) {
			throw new IllegalArgumentException("Master-slave chain list is null.");
		}

		int size = chain.size();
		if (size < 2) {
			throw new IllegalArgumentException("Master-slave chain list must have 2 redis node at lease.");
		}

		Set<Redis> set = new HashSet<Redis>();
		set.addAll(chain);
		if (set.size() != size) {
			throw new IllegalArgumentException("Invalid master-slave chain, because repeated redis node, chain=" + chain);
		}

		check(index, size);

		this.chain = chain;
		this.index = index;
		rebuild();
	}

	private void check(int index, int size) {
		if (index < 0 || index >= size) {
			throw new IllegalArgumentException("Master index should be in [0," + (size - 1) + "]");
		}
	}


	// ~ ---------------------------------------------------------------------------------------------------------


	@Override
	public void master(int index) {
		master(index, true);
	}

	private void master(int index, boolean rebuild) {
		check(index, chain.size());

		this.index = index;
		if (rebuild) {
			rebuild();
		}
	}

	@Override
	public Redis master() {
		return chain.get(index);
	}

	@Override
	public int index() {
		return index;
	}

	@Override
	public List<Redis> chain() {
		List<Redis> l = new ArrayList<Redis>(chain.size());
		int c = index;
		for (int i = 0; i < chain.size(); i++) {
			l.add(chain.get(c));
			c++;
			if (c == chain.size()) {
				c = 0;
			}
		}
		return l;
	}

	private void rebuild() {
		Redis master = chain.get(index);
		master.slaveofnoone();

		// after master
		for (int i = index + 1; i < chain.size(); i++) {
			Redis m = chain.get(i - 1);
			Redis s = chain.get(i);
			link(m, s);
		}

		// before master except index 0
		for (int i = 1; i < index; i++) {
			Redis m = chain.get(i - 1);
			Redis s = chain.get(i);
			link(m, s);
		}

		// index 0
		if (index != 0) {
			Redis m = chain.get(chain.size() - 1);
			Redis s = chain.get(0);
			link(m, s);
		}
	}

	@Override
	public void reset() {
		index = 0;
		rebuild();
	}

	private void link(Redis m, Redis s) {
		try {
			s.slaveof(m.host(), m.port());
		} catch (RedisConnectionException e) {
			LOG.warn("[CRAFT-ATOM-REDIS] {} slaveof {} failed", s.toString(), m.toString());
		}
	}

	private Redis firstSlave() {
		int slaveIndex = (index + 1) % chain.size();
		return chain.get(slaveIndex);
	}

	@Override
	public void enableReadSlave() {
		this.readSlave = true;
	}

	@Override
	public void disableReadSlave() {
		this.readSlave = false;
	}


	// ~ --------------------------------------------------------------------------------------------------------- Keys


	@Override
	public Long del(String... keys) {
		return master().del(keys);
	}

	@Override
	public byte[] dump(String key) {
		return master().dump(key);
	}

	@Override
	public Boolean exists(String key) {
		return readSlave ? firstSlave().exists(key) : master().exists(key);
	}

	@Override
	public Long expire(String key, int seconds) {
		return master().expire(key, seconds);
	}

	@Override
	public Long expireat(String key, long timestamp) {
		return master().expireat(key, timestamp);
	}

	@Override
	public Set<String> keys(String pattern) {
		return readSlave ? firstSlave().keys(pattern) : master().keys(pattern);
	}

	@Override
	public String migrate(String host, int port, String key, int destinationdb, int timeout) {
		return master().migrate(host, port, key, destinationdb, timeout);
	}

	@Override
	public Long move(String key, int db) {
		return master().move(key, db);
	}

	@Override
	public Long objectrefcount(String key) {
		return readSlave ? firstSlave().objectrefcount(key) : master().objectrefcount(key);
	}

	@Override
	public String objectencoding(String key) {
		return readSlave ? firstSlave().objectencoding(key) : master().objectencoding(key);
	}

	@Override
	public Long objectidletime(String key) {
		return readSlave ? firstSlave().objectidletime(key) : master().objectidletime(key);
	}

	@Override
	public Long persist(String key) {
		return master().persist(key);
	}

	@Override
	public Long pexpire(String key, long milliseconds) {
		return master().pexpire(key, milliseconds);
	}

	@Override
	public Long pexpireat(String key, long millisecondstimestamp) {
		return master().pexpireat(key, millisecondstimestamp);
	}

	@Override
	public Long pttl(String key) {
		return readSlave ? firstSlave().pttl(key) : master().pttl(key);
	}

	@Override
	public String randomkey() {
		return readSlave ? firstSlave().randomkey() : master().randomkey();
	}

	@Override
	public String rename(String key, String newkey) {
		return master().rename(key, newkey);
	}

	@Override
	public Long renamenx(String key, String newkey) {
		return master().renamenx(key, newkey);
	}

	@Override
	public String restore(String key, int ttl, byte[] serializedvalue) {
		return master().restore(key, ttl, serializedvalue);
	}

	@Override
	public List<String> sort(String key) {
		return readSlave ? firstSlave().sort(key) : master().sort(key);
	}

	@Override
	public List<String> sort(String key, boolean desc) {
		return readSlave ? firstSlave().sort(key, desc) : master().sort(key, desc);
	}

	@Override
	public List<String> sort(String key, boolean alpha, boolean desc) {
		return readSlave ? firstSlave().sort(key, alpha, desc) : master().sort(key, alpha, desc);
	}

	@Override
	public List<String> sort(String key, int offset, int count) {
		return readSlave ? firstSlave().sort(key, offset, count) : master().sort(key, offset, count);
	}

	@Override
	public List<String> sort(String key, int offset, int count, boolean alpha, boolean desc) {
		return readSlave ? firstSlave().sort(key, offset, count, alpha, desc) : master().sort(key, offset, count, alpha, desc);
	}

	@Override
	public List<String> sort(String key, String bypattern, String... getpatterns) {
		return readSlave ? firstSlave().sort(key, bypattern, getpatterns) : master().sort(key, bypattern, getpatterns);
	}

	@Override
	public List<String> sort(String key, String bypattern, boolean desc, String... getpatterns) {
		return readSlave ? firstSlave().sort(key, bypattern, desc, getpatterns) : master().sort(key, bypattern, desc, getpatterns);
	}

	@Override
	public List<String> sort(String key, String bypattern, boolean alpha, boolean desc, String... getpatterns) {
		return readSlave ? firstSlave().sort(key, bypattern, alpha, desc, getpatterns) : master().sort(key, bypattern, alpha, desc, getpatterns);
	}

	@Override
	public List<String> sort(String key, String bypattern, int offset, int count, String... getpatterns) {
		return readSlave ? firstSlave().sort(key, bypattern, offset, count, getpatterns) : master().sort(key, bypattern, offset, count, getpatterns);
	}

	@Override
	public List<String> sort(String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String... getpatterns) {
		return readSlave ? firstSlave().sort(key, bypattern, offset, count, alpha, desc, getpatterns) : master().sort(key, bypattern, offset, count, alpha, desc, getpatterns);
	}

	@Override
	public Long sort(String key, String destination) {
		return readSlave ? firstSlave().sort(key, destination) : master().sort(key, destination);
	}

	@Override
	public Long sort(String key, boolean desc, String destination) {
		return readSlave ? firstSlave().sort(key, desc, destination) : master().sort(key, desc, destination);
	}

	@Override
	public Long sort(String key, boolean alpha, boolean desc, String destination) {
		return readSlave ? firstSlave().sort(key, alpha, desc, destination) : master().sort(key, alpha, desc, destination);
	}

	@Override
	public Long sort(String key, int offset, int count, String destination) {
		return readSlave ? firstSlave().sort(key, offset, count, destination) : master().sort(key, offset, count, destination);
	}

	@Override
	public Long sort(String key, int offset, int count, boolean alpha, boolean desc, String destination) {
		return readSlave ? firstSlave().sort(key, offset, count, alpha, desc, destination) : master().sort(key, offset, count, alpha, desc, destination);
	}

	@Override
	public Long sort(String key, String bypattern, String destination, String... getpatterns) {
		return readSlave ? firstSlave().sort(key, bypattern, destination, getpatterns) : master().sort(key, bypattern, destination, getpatterns);
	}

	@Override
	public Long sort(String key, String bypattern, boolean desc, String destination, String... getpatterns) {
		return readSlave ? firstSlave().sort(key, bypattern, desc, destination, getpatterns) : master().sort(key, bypattern, desc, destination, getpatterns);
	}

	@Override
	public Long sort(String key, String bypattern, boolean alpha, boolean desc, String destination, String... getpatterns) {
		return readSlave ? firstSlave().sort(key, bypattern, alpha, desc, destination, getpatterns) : master().sort(key, bypattern, alpha, desc, destination, getpatterns);
	}

	@Override
	public Long sort(String key, String bypattern, int offset, int count, String destination, String... getpatterns) {
		return readSlave ? firstSlave().sort(key, bypattern, offset, count, destination, getpatterns) : master().sort(key, bypattern, offset, count, destination, getpatterns);
	}

	@Override
	public Long sort(String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String destination, String... getpatterns) {
		return readSlave ? firstSlave().sort(key, bypattern, offset, count, alpha, desc, destination, getpatterns) : master().sort(key, bypattern, offset, count, alpha, desc, destination, getpatterns);
	}

	@Override
	public Long ttl(String key) {
		return readSlave ? firstSlave().ttl(key) : master().ttl(key);
	}

	@Override
	public String type(String key) {
		return readSlave ? firstSlave().type(key) : master().type(key);
	}

	@Override
	public ScanResult<String> scan(String cursor) {
		return readSlave ? firstSlave().scan(cursor) : master().scan(cursor);
	}

	@Override
	public ScanResult<String> scan(String cursor, int count) {
		return readSlave ? firstSlave().scan(cursor, count) : master().scan(cursor, count);
	}

	@Override
	public ScanResult<String> scan(String cursor, String pattern) {
		return readSlave ? firstSlave().scan(cursor, pattern) : master().scan(cursor, pattern);
	}

	@Override
	public ScanResult<String> scan(String cursor, String pattern, int count) {
		return readSlave ? firstSlave().scan(cursor, pattern, count) : master().scan(cursor, pattern, count);
	}


	// ~ ------------------------------------------------------------------------------------------------------ Strings


	@Override
	public Long append(String key, String value) {
		return master().append(key, value);
	}

	@Override
	public Long bitcount(String key) {
		return readSlave ? firstSlave().bitcount(key) : master().bitcount(key);
	}

	@Override
	public Long bitcount(String key, long start, long end) {
		return readSlave ? firstSlave().bitcount(key) : master().bitcount(key, start, end);
	}

	@Override
	public Long bitnot(String destkey, String key) {
		return master().bitnot(destkey, key);
	}

	@Override
	public Long bitand(String destkey, String... keys) {
		return master().bitand(destkey, keys);
	}

	@Override
	public Long bitor(String destkey, String... keys) {
		return master().bitor(destkey, keys);
	}

	@Override
	public Long bitxor(String destkey, String... keys) {
		return master().bitxor(destkey, keys);
	}

	@Override
	public Long bitpos(String key, boolean value) {
		return readSlave ? firstSlave().bitpos(key, value) : master().bitpos(key, value);
	}

	@Override
	public Long bitpos(String key, boolean value, long start) {
		return readSlave ? firstSlave().bitpos(key, value, start) : master().bitpos(key, value, start);
	}

	@Override
	public Long bitpos(String key, boolean value, long start, long end) {
		return readSlave ? firstSlave().bitpos(key, value, start, end) : master().bitpos(key, value, start, end);
	}

	@Override
	public Long decr(String key) {
		return master().decr(key);
	}

	@Override
	public Long decrby(String key, long decrement) {
		return master().decrby(key, decrement);
	}

	@Override
	public String get(String key) {
		return readSlave ? firstSlave().get(key) : master().get(key);
	}

	@Override
	public Boolean getbit(String key, long offset) {
		return readSlave ? firstSlave().getbit(key, offset) : master().getbit(key, offset);
	}

	@Override
	public String getrange(String key, long start, long end) {
		return readSlave ? firstSlave().getrange(key, start, end) : master().getrange(key, start, end);
	}

	@Override
	public String getset(String key, String value) {
		return master().getset(key, value);
	}

	@Override
	public Long incr(String key) {
		return master().incr(key);
	}

	@Override
	public Long incrby(String key, long increment) {
		return master().incrby(key, increment);
	}

	@Override
	public Double incrbyfloat(String key, double increment) {
		return master().incrbyfloat(key, increment);
	}

	@Override
	public List<String> mget(String... keys) {
		return readSlave ? firstSlave().mget(keys) : master().mget(keys);
	}

	@Override
	public String mset(String... keysvalues) {
		return master().mset(keysvalues);
	}

	@Override
	public Long msetnx(String... keysvalues) {
		return master().msetnx(keysvalues);
	}

	@Override
	public String psetex(String key, int milliseconds, String value) {
		return master().psetex(key, milliseconds, value);
	}

	@Override
	public String set(String key, String value) {
		return master().set(key, value);
	}

	@Override
	public String setxx(String key, String value) {
		return master().setxx(key, value);
	}

	@Override
	public String setnxex(String key, String value, int seconds) {
		return master().setnxex(key, value, seconds);
	}

	@Override
	public String setnxpx(String key, String value, int milliseconds) {
		return master().setnxpx(key, value, milliseconds);
	}

	@Override
	public String setxxex(String key, String value, int seconds) {
		return master().setxxex(key, value, seconds);
	}

	@Override
	public String setxxpx(String key, String value, int milliseconds) {
		return master().setxxpx(key, value, milliseconds);
	}

	@Override
	public Boolean setbit(String key, long offset, boolean value) {
		return master().setbit(key, offset, value);
	}

	@Override
	public String setex(String key, int seconds, String value) {
		return master().setex(key, seconds, value);
	}

	@Override
	public Long setnx(String key, String value) {
		return master().setnx(key, value);
	}

	@Override
	public Long setrange(String key, long offset, String value) {
		return master().setrange(key, offset, value);
	}

	@Override
	public Long strlen(String key) {
		return readSlave ? firstSlave().strlen(key) : master().strlen(key);
	}


	// ~ ------------------------------------------------------------------------------------------------------ Hashes


	@Override
	public Long hdel(String key, String... fields) {
		return master().hdel(key, fields);
	}

	@Override
	public Boolean hexists(String key, String field) {
		return readSlave ? firstSlave().hexists(key, field) : master().hexists(key, field);
	}

	@Override
	public String hget(String key, String field) {
		return readSlave ? firstSlave().hget(key, field) : master().hget(key, field);
	}

	@Override
	public Map<String, String> hgetall(String key) {
		return readSlave ? firstSlave().hgetall(key) : master().hgetall(key);
	}

	@Override
	public Long hincrby(String key, String field, long increment) {
		return master().hincrby(key, field, increment);
	}

	@Override
	public Double hincrbyfloat(String key, String field, double increment) {
		return master().hincrbyfloat(key, field, increment);
	}

	@Override
	public Set<String> hkeys(String key) {
		return readSlave ? firstSlave().hkeys(key) : master().hkeys(key);
	}

	@Override
	public Long hlen(String key) {
		return readSlave ? firstSlave().hlen(key) : master().hlen(key);
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		return readSlave ? firstSlave().hmget(key, fields) : master().hmget(key, fields);
	}

	@Override
	public String hmset(String key, Map<String, String> fieldvalues) {
		return master().hmset(key, fieldvalues);
	}

	@Override
	public Long hset(String key, String field, String value) {
		return master().hset(key, field, value);
	}

	@Override
	public Long hsetnx(String key, String field, String value) {
		return master().hsetnx(key, field, value);
	}

	@Override
	public List<String> hvals(String key) {
		return readSlave ? firstSlave().hvals(key) : master().hvals(key);
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor) {
		return readSlave ? firstSlave().hscan(key, cursor) : master().hscan(key, cursor);
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor, int count) {
		return readSlave ? firstSlave().hscan(key, cursor, count) : master().hscan(key, cursor, count);
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor, String pattern) {
		return readSlave ? firstSlave().hscan(key, cursor, pattern) : master().hscan(key, cursor, pattern);
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor, String pattern, int count) {
		return readSlave ? firstSlave().hscan(key, cursor, pattern, count) : master().hscan(key, cursor, pattern, count);
	}


	// ~ ------------------------------------------------------------------------------------------------------- Lists


	@Override
	public String blpop(String key) {
		return master().blpop(key);
	}

	@Override
	public String blpop(String key, int timeout) {
		return master().blpop(key, timeout);
	}

	@Override
	public Map<String, String> blpop(String... keys) {
		return master().blpop(keys);
	}

	@Override
	public Map<String, String> blpop(int timeout, String... keys) {
		return master().blpop(timeout, keys);
	}

	@Override
	public String brpop(String key) {
		return master().brpop(key);
	}

	@Override
	public String brpop(String key, int timeout) {
		return master().brpop(key, timeout);
	}

	@Override
	public Map<String, String> brpop(String... keys) {
		return master().brpop(keys);
	}

	@Override
	public Map<String, String> brpop(int timeout, String... keys) {
		return master().brpop(timeout, keys);
	}

	@Override
	public String brpoplpush(String source, String destination, int timeout) {
		return master().brpoplpush(source, destination, timeout);
	}

	@Override
	public String lindex(String key, long index) {
		return readSlave ? firstSlave().lindex(key, index) : master().lindex(key, index);
	}

	@Override
	public Long linsertbefore(String key, String pivot, String value) {
		return master().linsertbefore(key, pivot, value);
	}

	@Override
	public Long linsertafter(String key, String pivot, String value) {
		return master().linsertafter(key, pivot, value);
	}

	@Override
	public Long llen(String key) {
		return readSlave ? firstSlave().llen(key) : master().llen(key);
	}

	@Override
	public String lpop(String key) {
		return master().lpop(key);
	}

	@Override
	public Long lpush(String key, String... values) {
		return master().lpush(key, values);
	}

	@Override
	public Long lpushx(String key, String value) {
		return master().lpushx(key, value);
	}

	@Override
	public List<String> lrange(String key, long start, long stop) {
		return readSlave ? firstSlave().lrange(key, start, stop) : master().lrange(key, start, stop);
	}

	@Override
	public Long lrem(String key, long count, String value) {
		return master().lrem(key, count, value);
	}

	@Override
	public String lset(String key, long index, String value) {
		return master().lset(key, index, value);
	}

	@Override
	public String ltrim(String key, long start, long stop) {
		return master().ltrim(key, start, stop);
	}

	@Override
	public String rpop(String key) {
		return master().rpop(key);
	}

	@Override
	public String rpoplpush(String source, String destination) {
		return master().rpoplpush(source, destination);
	}

	@Override
	public Long rpush(String key, String... values) {
		return master().rpush(key, values);
	}

	@Override
	public Long rpushx(String key, String value) {
		return master().rpushx(key, value);
	}


	// ~ ------------------------------------------------------------------------------------------------------- Sets


	@Override
	public Long sadd(String key, String... members) {
		return master().sadd(key, members);
	}

	@Override
	public Long scard(String key) {
		return readSlave ? firstSlave().scard(key) : master().scard(key);
	}

	@Override
	public Set<String> sdiff(String... keys) {
		return readSlave ? firstSlave().sdiff(keys) : master().sdiff(keys);
	}

	@Override
	public Long sdiffstore(String destination, String... keys) {
		return master().sdiffstore(destination, keys);
	}

	@Override
	public Set<String> sinter(String... keys) {
		return readSlave ? firstSlave().sinter(keys) : master().sinter(keys);
	}

	@Override
	public Long sinterstore(String destination, String... keys) {
		return master().sinterstore(destination, keys);
	}

	@Override
	public Boolean sismember(String key, String member) {
		return readSlave ? firstSlave().sismember(key, member) : master().sismember(key, member);
	}

	@Override
	public Set<String> smembers(String key) {
		return readSlave ? firstSlave().smembers(key) : master().smembers(key);
	}

	@Override
	public Long smove(String source, String destination, String member) {
		return master().smove(source, destination, member);
	}

	@Override
	public String spop(String key) {
		return readSlave ? firstSlave().spop(key) : master().spop(key);
	}

	@Override
	public List<String> srandmember(String key, int count) {
		return readSlave ? firstSlave().srandmember(key, count) : master().srandmember(key, count);
	}

	@Override
	public String srandmember(String key) {
		return readSlave ? firstSlave().srandmember(key) : master().srandmember(key);
	}

	@Override
	public Long srem(String key, String... members) {
		return master().srem(key, members);
	}

	@Override
	public Set<String> sunion(String... keys) {
		return readSlave ? firstSlave().sunion(keys) : master().sunion(keys);
	}

	@Override
	public Long sunionstore(String destination, String... keys) {
		return master().sunionstore(destination, keys);
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor) {
		return readSlave ? firstSlave().sscan(key, cursor) : master().sscan(key, cursor);
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor, int count) {
		return readSlave ? firstSlave().sscan(key, cursor, count) : master().sscan(key, cursor, count);
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor, String pattern) {
		return readSlave ? firstSlave().sscan(key, cursor, pattern) : master().sscan(key, cursor, pattern);
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor, String pattern, int count) {
		return readSlave ? firstSlave().sscan(key, cursor, pattern, count) : master().sscan(key, cursor, pattern, count);
	}


	// ~ ------------------------------------------------------------------------------------------------- Sorted Sets


	@Override
	public Long zadd(String key, double score, String member) {
		return master().zadd(key, score, member);
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoremembers) {
		return master().zadd(key, scoremembers);
	}

	@Override
	public Long zcard(String key) {
		return readSlave ? firstSlave().zcard(key) : master().zcard(key);
	}

	@Override
	public Long zcount(String key, double min, double max) {
		return readSlave ? firstSlave().zcount(key, min, max) : master().zcount(key, min, max);
	}

	@Override
	public Long zcount(String key, String min, String max) {
		return readSlave ? firstSlave().zcount(key, min, max) : master().zcount(key, min, max);
	}

	@Override
	public Double zincrby(String key, double score, String member) {
		return master().zincrby(key, score, member);
	}

	@Override
	public Long zinterstore(String destination, String... keys) {
		return master().zinterstore(destination, keys);
	}

	@Override
	public Long zinterstoremax(String destination, String... keys) {
		return master().zinterstoremax(destination, keys);
	}

	@Override
	public Long zinterstoremin(String destination, String... keys) {
		return master().zinterstoremin(destination, keys);
	}

	@Override
	public Long zinterstore(String destination, Map<String, Integer> weightkeys) {
		return master().zinterstore(destination, weightkeys);
	}

	@Override
	public Long zinterstoremax(String destination, Map<String, Integer> weightkeys) {
		return master().zinterstoremax(destination, weightkeys);
	}

	@Override
	public Long zinterstoremin(String destination, Map<String, Integer> weightkeys) {
		return master().zinterstoremin(destination, weightkeys);
	}

	@Override
	public Long zlexcount(String key, String min, String max) {
		return readSlave ? firstSlave().zlexcount(key, min, max) : master().zlexcount(key, min, max);
	}

	@Override
	public Set<String> zrange(String key, long start, long stop) {
		return readSlave ? firstSlave().zrange(key, start, stop) : master().zrange(key, start, stop);
	}

	@Override
	public Map<String, Double> zrangewithscores(String key, long start, long stop) {
		return readSlave ? firstSlave().zrangewithscores(key, start, stop) : master().zrangewithscores(key, start, stop);
	}

	@Override
	public Set<String> zrangebyscore(String key, double min, double max) {
		return readSlave ? firstSlave().zrangebyscore(key, min, max) : master().zrangebyscore(key, min, max);
	}

	@Override
	public Set<String> zrangebyscore(String key, String min, String max) {
		return readSlave ? firstSlave().zrangebyscore(key, min, max) : master().zrangebyscore(key, min, max);
	}

	@Override
	public Set<String> zrangebyscore(String key, double min, double max, int offset, int count) {
		return readSlave ? firstSlave().zrangebyscore(key, min, max, offset, count) : master().zrangebyscore(key, min, max, offset, count);
	}

	@Override
	public Set<String> zrangebyscore(String key, String min, String max, int offset, int count) {
		return readSlave ? firstSlave().zrangebyscore(key, min, max, offset, count) : master().zrangebyscore(key, min, max, offset, count);
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String key, double min, double max) {
		return readSlave ? firstSlave().zrangebyscorewithscores(key, min, max) : master().zrangebyscorewithscores(key, min, max);
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String key, String min, String max) {
		return readSlave ? firstSlave().zrangebyscorewithscores(key, min, max) : master().zrangebyscorewithscores(key, min, max);
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String key, double min, double max, int offset, int count) {
		return readSlave ? firstSlave().zrangebyscorewithscores(key, min, max, offset, count) : master().zrangebyscorewithscores(key, min, max, offset, count);
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String key, String min, String max, int offset, int count) {
		return readSlave ? firstSlave().zrangebyscorewithscores(key, min, max, offset, count) : master().zrangebyscorewithscores(key, min, max, offset, count);
	}
	
	@Override
	public Set<String> zrangebylex(String key, String min, String max) {
		return readSlave ? firstSlave().zrangebylex(key, min, max) : master().zrangebylex(key, min, max);
	}

	@Override
	public Set<String> zrangebylex(String key, String min, String max, int offset, int count) {
		return readSlave ? firstSlave().zrangebylex(key, min, max, offset, count) : master().zrangebylex(key, min, max, offset, count);
	}

	@Override
	public Long zrank(String key, String member) {
		return readSlave ? firstSlave().zrank(key, member) : master().zrank(key, member);
	}

	@Override
	public Long zrem(String key, String... members) {
		return master().zrem(key, members);
	}

	@Override
	public Long zremrangebyrank(String key, long start, long stop) {
		return master().zremrangebyrank(key, start, stop);
	}

	@Override
	public Long zremrangebyscore(String key, double min, double max) {
		return master().zremrangebyscore(key, min, max);
	}

	@Override
	public Long zremrangebyscore(String key, String min, String max) {
		return master().zremrangebyscore(key, min, max);
	}

	@Override
	public Set<String> zrevrange(String key, long start, long stop) {
		return readSlave ? firstSlave().zrevrange(key, start, stop) : master().zrevrange(key, start, stop);
	}

	@Override
	public Map<String, Double> zrevrangewithscores(String key, long start, long stop) {
		return readSlave ? firstSlave().zrevrangewithscores(key, start, stop) : master().zrevrangewithscores(key, start, stop);
	}

	@Override
	public Set<String> zrevrangebyscore(String key, double max, double min) {
		return readSlave ? firstSlave().zrevrangebyscore(key, max, min) : master().zrevrangebyscore(key, max, min);
	}

	@Override
	public Set<String> zrevrangebyscore(String key, String max, String min) {
		return readSlave ? firstSlave().zrevrangebyscore(key, max, min) : master().zrevrangebyscore(key, max, min);
	}

	@Override
	public Set<String> zrevrangebyscore(String key, double max, double min, int offset, int count) {
		return readSlave ? firstSlave().zrevrangebyscore(key, max, min, offset, count) : master().zrevrangebyscore(key, max, min, offset, count);
	}

	@Override
	public Set<String> zrevrangebyscore(String key, String max, String min, int offset, int count) {
		return readSlave ? firstSlave().zrevrangebyscore(key, max, min, offset, count) : master().zrevrangebyscore(key, max, min, offset, count);
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String key, double max, double min) {
		return readSlave ? firstSlave().zrevrangebyscorewithscores(key, max, min) : master().zrevrangebyscorewithscores(key, max, min);
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String key, String max, String min) {
		return readSlave ? firstSlave().zrevrangebyscorewithscores(key, max, min) : master().zrevrangebyscorewithscores(key, max, min);
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String key, double max, double min, int offset, int count) {
		return readSlave ? firstSlave().zrevrangebyscorewithscores(key, max, min, offset, count) : master().zrevrangebyscorewithscores(key, max, min, offset, count);
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String key, String max, String min, int offset, int count) {
		return readSlave ? firstSlave().zrevrangebyscorewithscores(key, max, min, offset, count) : master().zrevrangebyscorewithscores(key, max, min, offset, count);
	}

	@Override
	public Long zrevrank(String key, String member) {
		return readSlave ? firstSlave().zrevrank(key, member) : master().zrevrank(key, member);
	}

	@Override
	public Double zscore(String key, String member) {
		return readSlave ? firstSlave().zscore(key, member) : master().zscore(key, member);
	}

	@Override
	public Long zunionstore(String destination, String... keys) {
		return master().zunionstore(destination, keys);
	}

	@Override
	public Long zunionstoremax(String destination, String... keys) {
		return master().zunionstoremax(destination, keys);
	}

	@Override
	public Long zunionstoremin(String destination, String... keys) {
		return master().zunionstoremin(destination, keys);
	}

	@Override
	public Long zunionstore(String destination, Map<String, Integer> weightkeys) {
		return master().zunionstore(destination, weightkeys);
	}

	@Override
	public Long zunionstoremax(String destination, Map<String, Integer> weightkeys) {
		return master().zunionstoremax(destination, weightkeys);
	}

	@Override
	public Long zunionstoremin(String destination, Map<String, Integer> weightkeys) {
		return master().zunionstoremin(destination, weightkeys);
	}

	@Override
	public ScanResult<Entry<String, Double>> zscan(String key, String cursor) {
		return readSlave ? firstSlave().zscan(key, cursor) : master().zscan(key, cursor);
	}

	@Override
	public ScanResult<Entry<String, Double>> zscan(String key, String cursor, int count) {
		return readSlave ? firstSlave().zscan(key, cursor, count) : master().zscan(key, cursor, count);
	}

	@Override
	public ScanResult<Entry<String, Double>> zscan(String key, String cursor, String pattern) {
		return readSlave ? firstSlave().zscan(key, cursor, pattern) : master().zscan(key, cursor, pattern);
	}

	@Override
	public ScanResult<Entry<String, Double>> zscan(String key, String cursor, String pattern, int count) {
		return readSlave ? firstSlave().zscan(key, cursor, pattern, count) : master().zscan(key, cursor, pattern, count);
	}


	// ~ -------------------------------------------------------------------------------------------------- HyperLogLog


	@Override
	public Long pfadd(String key, String... elements) {
		return master().pfadd(key, elements);
	}

	@Override
	public Long pfcount(String... keys) {
		return master().pfcount(keys);
	}

	@Override
	public String pfmerge(String destkey, String... sourcekeys) {
		return master().pfmerge(destkey, sourcekeys);
	}


	// ~ ----------------------------------------------------------------------------------------------------- Pub/Sub


	@Override
	public RedisPubSub psubscribe(RedisPsubscribeHandler handler, String... patterns) {
		return readSlave ? firstSlave().psubscribe(handler, patterns) : master().psubscribe(handler, patterns);
	}

	@Override
	public Long publish(String channel, String message) {
		return master().publish(channel, message);
	}

	@Override
	public void punsubscribe(RedisPubSub pubsub, String... patterns) {
		if (readSlave)
			firstSlave().punsubscribe(pubsub, patterns);
		else
			master().punsubscribe(pubsub, patterns);
	}

	@Override
	public RedisPubSub subscribe(RedisSubscribeHandler handler, String... channels) {
		return readSlave ? firstSlave().subscribe(handler, channels) : master().subscribe(handler, channels);
	}

	@Override
	public void unsubscribe(RedisPubSub pubsub, String... channels) {
		if (readSlave)
			firstSlave().unsubscribe(pubsub, channels);
		else
			master().unsubscribe(pubsub, channels);
	}

	@Override
	public List<String> pubsubchannels(String pattern) {
		return readSlave ? firstSlave().pubsubchannels(pattern) : master().pubsubchannels(pattern);
	}

	@Override
	public Long pubsubnumpat() {
		return readSlave ? firstSlave().pubsubnumpat() : master().pubsubnumpat();
	}

	@Override
	public Map<String, String> pubsubnumsub(String... channels) {
		return readSlave ? firstSlave().pubsubnumsub(channels) : master().pubsubnumsub(channels);
	}


	// ~ ------------------------------------------------------------------------------------------------ Transactions


	@Override
	public String discard(RedisTransaction t) {
		return master().discard(t);
	}

	@Override
	public List<Object> exec(RedisTransaction t) {
		return master().exec(t);
	}

	@Override
	public RedisTransaction multi() {
		return master().multi();
	}

	@Override
	public String unwatch() {
		return master().unwatch();
	}

	@Override
	public String watch(String... keys) {
		return master().watch(keys);
	}


	// ~ --------------------------------------------------------------------------------------------------- Scripting


	@Override
	public Object eval(String script) {
		return master().eval(script);
	}

	@Override
	public Object eval(String script, List<String> keys) {
		return master().eval(script, keys);
	}

	@Override
	public Object eval(String script, List<String> keys, List<String> args) {
		return master().eval(script, keys, args);
	}

	@Override
	public Object evalsha(String sha1) {
		return master().evalsha(sha1);
	}

	@Override
	public Object evalsha(String sha1, List<String> keys) {
		return master().evalsha(sha1, keys);
	}

	@Override
	public Object evalsha(String sha1, List<String> keys, List<String> args) {
		return master().evalsha(sha1);
	}

	@Override
	public Boolean scriptexists(String sha1) {
		return readSlave ? firstSlave().scriptexists(sha1) : master().scriptexists(sha1);
	}

	@Override
	public Boolean[] scriptexists(String... sha1s) {
		return readSlave ? firstSlave().scriptexists(sha1s) : master().scriptexists(sha1s);
	}

	@Override
	public String scriptflush() {
		return master().scriptflush();
	}

	@Override
	public String scriptkill() {
		return master().scriptkill();
	}

	@Override
	public String scriptload(String script) {
		return master().scriptload(script);
	}


	// ~ ------------------------------------------------------------------------------------------------- Connection


	@Override
	public String auth(String password) {
		return master().auth(password);
	}

	@Override
	public String echo(String message) {
		return master().echo(message);
	}

	@Override
	public String ping() {
		return master().ping();
	}

	@Override
	public String quit() {
		return master().quit();
	}

	@Override
	public String select(int index) {
		return master().select(index);
	}


	// ~ ------------------------------------------------------------------------------------------------------ Server


	@Override
	public String bgrewriteaof() {
		return master().bgrewriteaof();
	}

	@Override
	public String bgsave() {
		return master().bgsave();
	}

	@Override
	public String clientgetname() {
		return master().clientgetname();
	}

	@Override
	public String clientkill(String ip, int port) {
		return master().clientkill(ip, port);
	}

	@Override
	public List<String> clientlist() {
		return master().clientlist();
	}

	@Override
	public String clientsetname(String connectionname) {
		return master().clientsetname(connectionname);
	}

	@Override
	public Map<String, String> configget(String parameter) {
		return master().configget(parameter);
	}

	@Override
	public String configset(String parameter, String value) {
		return master().configset(parameter, value);
	}

	@Override
	public String configresetstat() {
		return master().configresetstat();
	}

	@Override
	public Long dbsize() {
		return master().dbsize();
	}

	@Override
	public String debugobject(String key) {
		return master().debugobject(key);
	}

	@Override
	public String debugsegfault() {
		return master().debugsegfault();
	}

	@Override
	public String flushall() {
		return master().flushall();
	}

	@Override
	public String flushdb() {
		return master().flushdb();
	}

	@Override
	public String info() {
		return master().info();
	}

	@Override
	public String info(String section) {
		return master().info(section);
	}

	@Override
	public Long lastsave() {
		return master().lastsave();
	}

	@Override
	public void monitor(RedisMonitorHandler handler) {
		master().monitor(handler);
	}

	@Override
	public String save() {
		return master().save();
	}

	@Override
	public String shutdown(boolean save) {
		return master().shutdown(save);
	}

	@Override
	public String slaveof(String host, int port) {
		return master().slaveof(host, port);
	}

	@Override
	public String slaveofnoone() {
		return master().slaveofnoone();
	}

	@Override
	public List<Slowlog> slowlogget() {
		return master().slowlogget();
	}

	@Override
	public List<Slowlog> slowlogget(long len) {
		return master().slowlogget(len);
	}

	@Override
	public String slowlogreset() {
		return master().slowlogreset();
	}

	@Override
	public Long slowloglen() {
		return master().slowloglen();
	}

	@Override
	public void sync() {
		master().sync();
	}

	@Override
	public Long time() {
		return master().time();
	}

	@Override
	public Long microtime() {
		return master().microtime();
	}

}

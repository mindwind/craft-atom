package org.craft.atom.redis;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.ToString;

import org.craft.atom.redis.api.RedisCommand;
import org.craft.atom.redis.api.RedisPubSub;
import org.craft.atom.redis.api.RedisTransaction;
import org.craft.atom.redis.api.ScanResult;
import org.craft.atom.redis.api.ShardedRedisCommand;
import org.craft.atom.redis.api.handler.RedisPsubscribeHandler;
import org.craft.atom.redis.api.handler.RedisSubscribeHandler;
import org.craft.atom.redis.spi.Sharded;

/**
 * @author mindwind
 * @version 1.0, Jun 18, 2013
 */
@ToString
public abstract class AbstractShardedRedis<R extends RedisCommand> implements ShardedRedisCommand {
	
	
	protected Sharded<R> sharded;
	
	
	// ~ --------------------------------------------------------------------------------------------------------- Keys
	
	
	@Override
	public Long del(String shardkey, String... keys) {
		return sharded.shard(shardkey).del(keys);
	}
	
	@Override
	public byte[] dump(String shardkey, String key) {
		return sharded.shard(shardkey).dump(key);
	}

	@Override
	public Boolean exists(String shardkey, String key) {
		return sharded.shard(shardkey).exists(key);
	}
	
	@Override
	public Long expire(String shardkey, String key, int seconds) {
		return sharded.shard(shardkey).expire(key, seconds);
	}

	@Override
	public Long expireat(String shardkey, String key, long timestamp) {
		return sharded.shard(shardkey).expireat(key, timestamp);
	}

	@Override
	public Set<String> keys(String shardkey, String pattern) {
		return sharded.shard(shardkey).keys(pattern);
	}

	@Override
	public String migrate(String shardkey, String host, int port, String key, int destinationdb, int timeout) {
		return sharded.shard(shardkey).migrate(host, port, key, destinationdb, timeout);
	}

	@Override
	public Long move(String shardkey, String key, int db) {
		return sharded.shard(shardkey).move(key, db);
	}

	@Override
	public Long objectrefcount(String shardkey, String key) {
		return sharded.shard(shardkey).objectrefcount(key);
	}

	@Override
	public String objectencoding(String shardkey, String key) {
		return sharded.shard(shardkey).objectencoding(key);
	}

	@Override
	public Long objectidletime(String shardkey, String key) {
		return sharded.shard(shardkey).objectidletime(key);
	}

	@Override
	public Long persist(String shardkey, String key) {
		return sharded.shard(shardkey).persist(key);
	}

	@Override
	public Long pexpire(String shardkey, String key, int milliseconds) {
		return sharded.shard(shardkey).pexpire(key, milliseconds);
	}

	@Override
	public Long pexpireat(String shardkey, String key, long millisecondstimestamp) {
		return sharded.shard(shardkey).pexpireat(key, millisecondstimestamp);
	}

	@Override
	public Long pttl(String shardkey, String key) {
		return sharded.shard(shardkey).pttl(key);
	}

	@Override
	public String randomkey(String shardkey) {
		return sharded.shard(shardkey).randomkey();
	}

	@Override
	public String rename(String shardkey, String key, String newkey) {
		return sharded.shard(shardkey).rename(key, newkey);
	}

	@Override
	public Long renamenx(String shardkey, String key, String newkey) {
		return sharded.shard(shardkey).renamenx(key, newkey);
	}

	@Override
	public String restore(String shardkey, String key, int ttl, byte[] serializedvalue) {
		return sharded.shard(shardkey).restore(key, ttl, serializedvalue);
	}

	@Override
	public List<String> sort(String shardkey, String key) {
		return sharded.shard(shardkey).sort(key);
	}

	@Override
	public List<String> sort(String shardkey, String key, boolean desc) {
		return sharded.shard(shardkey).sort(key, desc);
	}

	@Override
	public List<String> sort(String shardkey, String key, boolean alpha, boolean desc) {
		return sharded.shard(shardkey).sort(key, alpha, desc);
	}

	@Override
	public List<String> sort(String shardkey, String key, int offset, int count) {
		return sharded.shard(shardkey).sort(key, offset, count);
	}

	@Override
	public List<String> sort(String shardkey, String key, int offset, int count, boolean alpha, boolean desc) {
		return sharded.shard(shardkey).sort(key, offset, count, alpha, desc);
	}

	@Override
	public List<String> sort(String shardkey, String key, String bypattern, String... getpatterns) {
		return sharded.shard(shardkey).sort(key, bypattern, getpatterns);
	}

	@Override
	public List<String> sort(String shardkey, String key, String bypattern, boolean desc, String... getpatterns) {
		return sharded.shard(shardkey).sort(key, bypattern, desc, getpatterns);
	}

	@Override
	public List<String> sort(String shardkey, String key, String bypattern, boolean alpha, boolean desc, String... getpatterns) {
		return sharded.shard(shardkey).sort(key, bypattern, alpha, desc, getpatterns);
	}

	@Override
	public List<String> sort(String shardkey, String key, String bypattern, int offset, int count, String... getpatterns) {
		return sharded.shard(shardkey).sort(key, bypattern, offset, count, getpatterns);
	}

	@Override
	public List<String> sort(String shardkey, String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String... getpatterns) {
		return sharded.shard(shardkey).sort(key, bypattern, offset, count, alpha, desc, getpatterns);
	}

	@Override
	public Long sort(String shardkey, String key, String destination) {
		return sharded.shard(shardkey).sort(key, destination);
	}

	@Override
	public Long sort(String shardkey, String key, boolean desc, String destination) {
		return sharded.shard(shardkey).sort(key, desc, destination);
	}

	@Override
	public Long sort(String shardkey, String key, boolean alpha, boolean desc, String destination) {
		return sharded.shard(shardkey).sort(key, alpha, desc, destination);
	}

	@Override
	public Long sort(String shardkey, String key, int offset, int count, String destination) {
		return sharded.shard(shardkey).sort(key, offset, count, destination);
	}

	@Override
	public Long sort(String shardkey, String key, int offset, int count, boolean alpha, boolean desc, String destination) {
		return sharded.shard(shardkey).sort(key, offset, count, alpha, desc, destination);
	}

	@Override
	public Long sort(String shardkey, String key, String bypattern, String destination, String... getpatterns) {
		return sharded.shard(shardkey).sort(key, bypattern, destination, getpatterns);
	}

	@Override
	public Long sort(String shardkey, String key, String bypattern, boolean desc, String destination, String... getpatterns) {
		return sharded.shard(shardkey).sort(key, bypattern, desc, destination, getpatterns);
	}

	@Override
	public Long sort(String shardkey, String key, String bypattern, boolean alpha, boolean desc, String destination, String... getpatterns) {
		return sharded.shard(shardkey).sort(key, bypattern, alpha, desc, destination, getpatterns);
	}

	@Override
	public Long sort(String shardkey, String key, String bypattern, int offset, int count, String destination, String... getpatterns) {
		return sharded.shard(shardkey).sort(key, bypattern, offset, count, destination, getpatterns);
	}

	@Override
	public Long sort(String shardkey, String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String destination, String... getpatterns) {
		return sharded.shard(shardkey).sort(key, bypattern, offset, count, alpha, desc, destination, getpatterns);
	}

	@Override
	public Long ttl(String shardkey, String key) {
		return sharded.shard(shardkey).ttl(key);
	}

	@Override
	public String type(String shardkey, String key) {
		return sharded.shard(shardkey).type(key);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Strings
	

	@Override
	public Long append(String shardkey, String key, String value) {
		return sharded.shard(shardkey).append(key, value);
	}

	@Override
	public Long bitcount(String shardkey, String key) {
		return sharded.shard(shardkey).bitcount(key);
	}
	
	@Override
	public Long bitcount(String shardkey, String key, long start, long end) {
		return sharded.shard(shardkey).bitcount(key, start, end);
	}

	@Override
	public Long bitnot(String shardkey, String destkey, String key) {
		return sharded.shard(shardkey).bitnot(destkey, key);
	}

	@Override
	public Long bitand(String shardkey, String destkey, String... keys) {
		return sharded.shard(shardkey).bitand(destkey, keys);
	}

	@Override
	public Long bitor(String shardkey, String destkey, String... keys) {
		return sharded.shard(shardkey).bitor(destkey, keys);
	}

	@Override
	public Long bitxor(String shardkey, String destkey, String... keys) {
		return sharded.shard(shardkey).bitxor(destkey, keys);
	}
	
	@Override
	public Long bitpos(String shardkey, String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long bitpos(String shardkey, String key, String value, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long decr(String shardkey, String key) {
		return sharded.shard(shardkey).decr(key);
	}

	@Override
	public Long decrby(String shardkey, String key, long decrement) {
		return sharded.shard(shardkey).decrby(key, decrement);
	}

	@Override
	public String get(String shardkey, String key) {
		return sharded.shard(shardkey).get(key);
	}

	@Override
	public Boolean getbit(String shardkey, String key, long offset) {
		return sharded.shard(shardkey).getbit(key, offset);
	}

	@Override
	public String getrange(String shardkey, String key, long start, long end) {
		return sharded.shard(shardkey).getrange(key, start, end);
	}

	@Override
	public String getset(String shardkey, String key, String value) {
		return sharded.shard(shardkey).getset(key, value);
	}

	@Override
	public Long incr(String shardkey, String key) {
		return sharded.shard(shardkey).incr(key);
	}

	@Override
	public Long incrby(String shardkey, String key, long increment) {
		return sharded.shard(shardkey).incrby(key, increment);
	}

	@Override
	public Double incrbyfloat(String shardkey, String key, double increment) {
		return sharded.shard(shardkey).incrbyfloat(key, increment);
	}

	@Override
	public List<String> mget(String shardkey, String... keys) {
		return sharded.shard(shardkey).mget(keys);
	}

	@Override
	public String mset(String shardkey, String... keysvalues) {
		return sharded.shard(shardkey).mset(keysvalues);
	}

	@Override
	public Long msetnx(String shardkey, String... keysvalues) {
		return sharded.shard(shardkey).msetnx(keysvalues);
	}

	@Override
	public String psetex(String shardkey, String key, int milliseconds, String value) {
		return sharded.shard(shardkey).psetex(key, milliseconds, value);
	}

	@Override
	public String set(String shardkey, String key, String value) {
		return sharded.shard(shardkey).set(key, value);
	}

	@Override
	public String setxx(String shardkey, String key, String value) {
		return sharded.shard(shardkey).setxx(key, value);
	}

	@Override
	public String setnxex(String shardkey, String key, String value, int seconds) {
		return sharded.shard(shardkey).setnxex(key, value, seconds);
	}

	@Override
	public String setnxpx(String shardkey, String key, String value, int milliseconds) {
		return sharded.shard(shardkey).setnxpx(key, value, milliseconds);
	}

	@Override
	public String setxxex(String shardkey, String key, String value, int seconds) {
		return sharded.shard(shardkey).setxxex(key, value, seconds);
	}

	@Override
	public String setxxpx(String shardkey, String key, String value, int milliseconds) {
		return sharded.shard(shardkey).setxxpx(key, value, milliseconds);
	}

	@Override
	public Boolean setbit(String shardkey, String key, long offset, boolean value) {
		return sharded.shard(shardkey).setbit(key, offset, value);
	}

	@Override
	public String setex(String shardkey, String key, int seconds, String value) {
		return sharded.shard(shardkey).setex(key, seconds, value);
	}

	@Override
	public Long setnx(String shardkey, String key, String value) {
		return sharded.shard(shardkey).setnx(key, value);
	}

	@Override
	public Long setrange(String shardkey, String key, long offset, String value) {
		return sharded.shard(shardkey).setrange(key, offset, value);
	}

	@Override
	public Long strlen(String shardkey, String key) {
		return sharded.shard(shardkey).strlen(key);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Hashes
	

	@Override
	public Long hdel(String shardkey, String key, String... fields) {
		return sharded.shard(shardkey).hdel(key, fields);
	}

	@Override
	public Boolean hexists(String shardkey, String key, String field) {
		return sharded.shard(shardkey).hexists(key, field);
	}

	@Override
	public String hget(String shardkey, String key, String field) {
		return sharded.shard(shardkey).hget(key, field);
	}

	@Override
	public Map<String, String> hgetall(String shardkey, String key) {
		return sharded.shard(shardkey).hgetall(key);
	}

	@Override
	public Long hincrby(String shardkey, String key, String field, long increment) {
		return sharded.shard(shardkey).hincrby(key, field, increment);
	}

	@Override
	public Double hincrbyfloat(String shardkey, String key, String field, double increment) {
		return sharded.shard(shardkey).hincrbyfloat(key, field, increment);
	}

	@Override
	public Set<String> hkeys(String shardkey, String key) {
		return sharded.shard(shardkey).hkeys(key);
	}

	@Override
	public Long hlen(String shardkey, String key) {
		return sharded.shard(shardkey).hlen(key);
	}

	@Override
	public List<String> hmget(String shardkey, String key, String... fields) {
		return sharded.shard(shardkey).hmget(key, fields);
	}

	@Override
	public String hmset(String shardkey, String key, Map<String, String> fieldvalues) {
		return sharded.shard(shardkey).hmset(key, fieldvalues);
	}

	@Override
	public Long hset(String shardkey, String key, String field, String value) {
		return sharded.shard(shardkey).hset(key, field, value);
	}

	@Override
	public Long hsetnx(String shardkey, String key, String field, String value) {
		return sharded.shard(shardkey).hsetnx(key, field, value);
	}

	@Override
	public List<String> hvals(String shardkey, String key) {
		return sharded.shard(shardkey).hvals(key);
	}
	
	@Override
	public ScanResult<Entry<String, String>> hscan(String shardkey, String key, String cursor) {
		return sharded.shard(shardkey).hscan(key, cursor);
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String shardkey, String key, String cursor, int count) {
		return sharded.shard(shardkey).hscan(key, cursor, count);
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String shardkey, String key, String cursor, String pattern) {
		return sharded.shard(shardkey).hscan(key, cursor, pattern);
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String shardkey, String key, String cursor, String pattern, int count) {
		return sharded.shard(shardkey).hscan(key, cursor, pattern, count);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Lists
	

	@Override
	public String blpop(String shardkey, String key) {
		return sharded.shard(shardkey).blpop(key);
	}

	@Override
	public String blpop(String shardkey, String key, int timeout) {
		return sharded.shard(shardkey).blpop(key, timeout);
	}

	@Override
	public Map<String, String> blpop(String shardkey, String... keys) {
		return sharded.shard(shardkey).blpop(keys);
	}

	@Override
	public Map<String, String> blpop(String shardkey, int timeout, String... keys) {
		return sharded.shard(shardkey).blpop(timeout, keys);
	}

	@Override
	public String brpop(String shardkey, String key) {
		return sharded.shard(shardkey).brpop(key);
	}

	@Override
	public String brpop(String shardkey, String key, int timeout) {
		return sharded.shard(shardkey).brpop(key, timeout);
	}

	@Override
	public Map<String, String> brpop(String shardkey, String... keys) {
		return sharded.shard(shardkey).brpop(keys);
	}

	@Override
	public Map<String, String> brpop(String shardkey, int timeout, String... keys) {
		return sharded.shard(shardkey).brpop(timeout, keys);
	}

	@Override
	public String brpoplpush(String shardkey, String source, String destination, int timeout) {
		return sharded.shard(shardkey).brpoplpush(source, destination, timeout);
	}

	@Override
	public String lindex(String shardkey, String key, long index) {
		return sharded.shard(shardkey).lindex(key, index);
	}

	@Override
	public Long linsertbefore(String shardkey, String key, String pivot, String value) {
		return sharded.shard(shardkey).linsertbefore(key, pivot, value);
	}

	@Override
	public Long linsertafter(String shardkey, String key, String pivot, String value) {
		return sharded.shard(shardkey).linsertafter(key, pivot, value);
	}

	@Override
	public Long llen(String shardkey, String key) {
		return sharded.shard(shardkey).llen(key);
	}

	@Override
	public String lpop(String shardkey, String key) {
		return sharded.shard(shardkey).lpop(key);
	}

	@Override
	public Long lpush(String shardkey, String key, String... values) {
		return sharded.shard(shardkey).lpush(key, values);
	}

	@Override
	public Long lpushx(String shardkey, String key, String value) {
		return sharded.shard(shardkey).lpushx(key, value);
	}

	@Override
	public List<String> lrange(String shardkey, String key, long start, long stop) {
		return sharded.shard(shardkey).lrange(key, start, stop);
	}

	@Override
	public Long lrem(String shardkey, String key, long count, String value) {
		return sharded.shard(shardkey).lrem(key, count, value);
	}

	@Override
	public String lset(String shardkey, String key, long index, String value) {
		return sharded.shard(shardkey).lset(key, index, value);
	}

	@Override
	public String ltrim(String shardkey, String key, long start, long stop) {
		return sharded.shard(shardkey).ltrim(key, start, stop);
	}

	@Override
	public String rpop(String shardkey, String key) {
		return sharded.shard(shardkey).rpop(key);
	}

	@Override
	public String rpoplpush(String shardkey, String source, String destination) {
		return sharded.shard(shardkey).rpoplpush(source, destination);
	}

	@Override
	public Long rpush(String shardkey, String key, String... values) {
		return sharded.shard(shardkey).rpush(key, values);
	}

	@Override
	public Long rpushx(String shardkey, String key, String value) {
		return sharded.shard(shardkey).rpushx(key, value);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Sets
	

	@Override
	public Long sadd(String shardkey, String key, String... members) {
		return sharded.shard(shardkey).sadd(key, members);
	}

	@Override
	public Long scard(String shardkey, String key) {
		return sharded.shard(shardkey).scard(key);
	}

	@Override
	public Set<String> sdiff(String shardkey, String... keys) {
		return sharded.shard(shardkey).sdiff(keys);
	}

	@Override
	public Long sdiffstore(String shardkey, String destination, String... keys) {
		return sharded.shard(shardkey).sdiffstore(destination, keys);
	}

	@Override
	public Set<String> sinter(String shardkey, String... keys) {
		return sharded.shard(shardkey).sinter(keys);
	}

	@Override
	public Long sinterstore(String shardkey, String destination, String... keys) {
		return sharded.shard(shardkey).sinterstore(destination, keys);
	}

	@Override
	public Boolean sismember(String shardkey, String key, String member) {
		return sharded.shard(shardkey).sismember(key, member);
	}

	@Override
	public Set<String> smembers(String shardkey, String key) {
		return sharded.shard(shardkey).smembers(key);
	}

	@Override
	public Long smove(String shardkey, String source, String destination, String member) {
		return sharded.shard(shardkey).smove(source, destination, member);
	}

	@Override
	public String spop(String shardkey, String key) {
		return sharded.shard(shardkey).spop(key);
	}

	@Override
	public List<String> srandmember(String shardkey, String key, int count) {
		return sharded.shard(shardkey).srandmember(key, count);
	}

	@Override
	public String srandmember(String shardkey, String key) {
		return sharded.shard(shardkey).srandmember(key);
	}

	@Override
	public Long srem(String shardkey, String key, String... members) {
		return sharded.shard(shardkey).srem(key, members);
	}
	
	@Override
	public Set<String> sunion(String shardkey, String... keys) {
		return sharded.shard(shardkey).sunion(keys);
	}

	@Override
	public Long sunionstore(String shardkey, String destination, String... keys) {
		return sharded.shard(shardkey).sunionstore(destination, keys);
	}
	
	@Override
	public ScanResult<String> sscan(String shardkey, String key, String cursor) {
		return sharded.shard(shardkey).sscan(key, cursor);
	}

	@Override
	public ScanResult<String> sscan(String shardkey, String key, String cursor, int count) {
		return sharded.shard(shardkey).sscan(key, cursor, count);
	}

	@Override
	public ScanResult<String> sscan(String shardkey, String key, String cursor, String pattern) {
		return sharded.shard(shardkey).sscan(key, cursor, pattern);
	}

	@Override
	public ScanResult<String> sscan(String shardkey, String key, String cursor, String pattern, int count) {
		return sharded.shard(shardkey).sscan(key, cursor, pattern, count);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------- Sorted Sets
	

	@Override
	public Long zadd(String shardkey, String key, double score, String member) {
		return sharded.shard(shardkey).zadd(key, score, member);
	}

	@Override
	public Long zcard(String shardkey, String key) {
		return sharded.shard(shardkey).zcard(key);
	}

	@Override
	public Long zcount(String shardkey, String key, double min, double max) {
		return sharded.shard(shardkey).zcount(key, min, max);
	}

	@Override
	public Long zcount(String shardkey, String key, String min, String max) {
		return sharded.shard(shardkey).zcount(key, min, max);
	}

	@Override
	public Double zincrby(String shardkey, String key, double score, String member) {
		return sharded.shard(shardkey).zincrby(key, score, member);
	}

	@Override
	public Long zinterstore(String shardkey, String destination, String... keys) {
		return sharded.shard(shardkey).zinterstore(destination, keys);
	}

	@Override
	public Long zinterstoremax(String shardkey, String destination, String... keys) {
		return sharded.shard(shardkey).zinterstoremax(destination, keys);
	}

	@Override
	public Long zinterstoremin(String shardkey, String destination, String... keys) {
		return sharded.shard(shardkey).zinterstoremin(destination, keys);
	}

	@Override
	public Long zinterstore(String shardkey, String destination, Map<String, Integer> weightkeys) {
		return sharded.shard(shardkey).zinterstore(destination, weightkeys);
	}

	@Override
	public Long zinterstoremax(String shardkey, String destination, Map<String, Integer> weightkeys) {
		return sharded.shard(shardkey).zinterstoremax(destination, weightkeys);
	}

	@Override
	public Long zinterstoremin(String shardkey, String destination, Map<String, Integer> weightkeys) {
		return sharded.shard(shardkey).zinterstoremin(destination, weightkeys);
	}

	@Override
	public Set<String> zrange(String shardkey, String key, long start, long stop) {
		return sharded.shard(shardkey).zrange(key, start, stop);
	}

	@Override
	public Map<String, Double> zrangewithscores(String shardkey, String key, long start, long stop) {
		return sharded.shard(shardkey).zrangewithscores(key, start, stop);
	}

	@Override
	public Set<String> zrangebyscore(String shardkey, String key, double min, double max) {
		return sharded.shard(shardkey).zrangebyscore(key, min, max);
	}

	@Override
	public Set<String> zrangebyscore(String shardkey, String key, String min, String max) {
		return sharded.shard(shardkey).zrangebyscore(key, min, max);
	}

	@Override
	public Set<String> zrangebyscore(String shardkey, String key, double min, double max, int offset, int count) {
		return sharded.shard(shardkey).zrangebyscore(key, min, max, offset, count);
	}

	@Override
	public Set<String> zrangebyscore(String shardkey, String key, String min, String max, int offset, int count) {
		return sharded.shard(shardkey).zrangebyscore(key, min, max, offset, count);
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String shardkey, String key, double min, double max) {
		return sharded.shard(shardkey).zrangebyscorewithscores(key, min, max);
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String shardkey, String key, String min, String max) {
		return sharded.shard(shardkey).zrangebyscorewithscores(key, min, max);
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String shardkey, String key, double min, double max, int offset, int count) {
		return sharded.shard(shardkey).zrangebyscorewithscores(key, min, max, offset, count);
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String shardkey, String key, String min, String max, int offset, int count) {
		return sharded.shard(shardkey).zrangebyscorewithscores(key, min, max, offset, count);
	}

	@Override
	public Long zrank(String shardkey, String key, String member) {
		return sharded.shard(shardkey).zrank(key, member);
	}

	@Override
	public Long zrem(String shardkey, String key, String... members) {
		return sharded.shard(shardkey).zrem(key, members);
	}

	@Override
	public Long zremrangebyrank(String shardkey, String key, long start, long stop) {
		return sharded.shard(shardkey).zremrangebyrank(key, start, stop);
	}

	@Override
	public Long zremrangebyscore(String shardkey, String key, double min, double max) {
		return sharded.shard(shardkey).zremrangebyscore(key, min, max);
	}

	@Override
	public Long zremrangebyscore(String shardkey, String key, String min, String max) {
		return sharded.shard(shardkey).zremrangebyscore(key, min, max);
	}

	@Override
	public Set<String> zrevrange(String shardkey, String key, long start, long stop) {
		return sharded.shard(shardkey).zrevrange(key, start, stop);
	}

	@Override
	public Map<String, Double> zrevrangewithscores(String shardkey, String key, long start, long stop) {
		return sharded.shard(shardkey).zrevrangewithscores(key, start, stop);
	}

	@Override
	public Set<String> zrevrangebyscore(String shardkey, String key, double max, double min) {
		return sharded.shard(shardkey).zrevrangebyscore(key, max, min);
	}

	@Override
	public Set<String> zrevrangebyscore(String shardkey, String key, String max, String min) {
		return sharded.shard(shardkey).zrevrangebyscore(key, max, min);
	}

	@Override
	public Set<String> zrevrangebyscore(String shardkey, String key, double max, double min, int offset, int count) {
		return sharded.shard(shardkey).zrevrangebyscore(key, max, min, offset, count);
	}

	@Override
	public Set<String> zrevrangebyscore(String shardkey, String key, String max, String min, int offset, int count) {
		return sharded.shard(shardkey).zrevrangebyscore(key, max, min, offset, count);
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String shardkey, String key, double max, double min) {
		return sharded.shard(shardkey).zrevrangebyscorewithscores(key, max, min);
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String shardkey, String key, String max, String min) {
		return sharded.shard(shardkey).zrevrangebyscorewithscores(key, max, min);
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String shardkey, String key, double max, double min, int offset, int count) {
		return sharded.shard(shardkey).zrevrangebyscorewithscores(key, max, min, offset, count);
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String shardkey, String key, String max, String min, int offset, int count) {
		return sharded.shard(shardkey).zrevrangebyscorewithscores(key, max, min, offset, count);
	}

	@Override
	public Long zrevrank(String shardkey, String key, String member) {
		return sharded.shard(shardkey).zrevrank(key, member);
	}

	@Override
	public Double zscore(String shardkey, String key, String member) {
		return sharded.shard(shardkey).zscore(key, member);
	}

	@Override
	public Long zunionstore(String shardkey, String destination, String... keys) {
		return sharded.shard(shardkey).zunionstore(destination, keys);
	}

	@Override
	public Long zunionstoremax(String shardkey, String destination, String... keys) {
		return sharded.shard(shardkey).zunionstoremax(destination, keys);
	}

	@Override
	public Long zunionstoremin(String shardkey, String destination, String... keys) {
		return sharded.shard(shardkey).zunionstoremin(destination, keys);
	}

	@Override
	public Long zunionstore(String shardkey, String destination, Map<String, Integer> weightkeys) {
		return sharded.shard(shardkey).zunionstore(destination, weightkeys);
	}

	@Override
	public Long zunionstoremax(String shardkey, String destination, Map<String, Integer> weightkeys) {
		return sharded.shard(shardkey).zunionstoremax(destination, weightkeys);
	}

	@Override
	public Long zunionstoremin(String shardkey, String destination, Map<String, Integer> weightkeys) {
		return sharded.shard(shardkey).zunionstoremin(destination, weightkeys);
	}
	
	@Override
	public ScanResult<Entry<String, Double>> zscan(String shardkey, String key, String cursor) {
		return sharded.shard(shardkey).zscan(key, cursor);
	}

	@Override
	public ScanResult<Entry<String, Double>> zscan(String shardkey, String key, String cursor, int count) {
		return sharded.shard(shardkey).zscan(key, cursor, count);
	}

	@Override
	public ScanResult<Entry<String, Double>> zscan(String shardkey, String key, String cursor, String pattern) {
		return sharded.shard(shardkey).zscan(key, cursor, pattern);
	}

	@Override
	public ScanResult<Entry<String, Double>> zscan(String shardkey, String key, String cursor, String pattern, int count) {
		return sharded.shard(shardkey).zscan(key, cursor, pattern, count);
	}
	
	
	// ~ ----------------------------------------------------------------------------------------------------- Pub/Sub

	
	@Override
	public RedisPubSub psubscribe(String shardkey, RedisPsubscribeHandler handler, String... patterns) {
		return sharded.shard(shardkey).psubscribe(handler, patterns);
	}

	@Override
	public Long publish(String shardkey, String channel, String message) {
		return sharded.shard(shardkey).publish(channel, message);
	}

	@Override
	public void punsubscribe(String shardkey, RedisPubSub pubsub, String... patterns) {
		sharded.shard(shardkey).punsubscribe(pubsub, patterns);
	}

	@Override
	public RedisPubSub subscribe(String shardkey, RedisSubscribeHandler handler, String... channels) {
		return sharded.shard(shardkey).subscribe(handler, channels);
	}

	@Override
	public void unsubscribe(String shardkey, RedisPubSub pubsub, String... channels) {
		sharded.shard(shardkey).unsubscribe(pubsub, channels);
	}
	
	@Override
	public List<String> pubsubchannels(String shardkey, String pattern) {
		return sharded.shard(shardkey).pubsubchannels(pattern);
	}

	@Override
	public Long pubsubnumpat(String shardkey) {
		return sharded.shard(shardkey).pubsubnumpat();
	}

	@Override
	public Map<String, String> pubsubnumsub(String shardkey, String... channels) {
		return sharded.shard(shardkey).pubsubnumsub(channels);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------ Transactions

	
	@Override
	public String discard(String shardkey, RedisTransaction t) {
		return sharded.shard(shardkey).discard(t);
	}

	@Override
	public List<Object> exec(String shardkey, RedisTransaction t) {
		return sharded.shard(shardkey).exec(t);
	}

	@Override
	public RedisTransaction multi(String shardkey) {
		return sharded.shard(shardkey).multi();
	}

	@Override
	public String unwatch(String shardkey) {
		return sharded.shard(shardkey).unwatch();
	}

	@Override
	public String watch(String shardkey, String... keys) {
		return sharded.shard(shardkey).watch(keys);
	}
	
	
	// ~ --------------------------------------------------------------------------------------------------- Scripting
	

	@Override
	public Object eval(String shardkey, String script) {
		return sharded.shard(shardkey).eval(script);
	}

	@Override
	public Object eval(String shardkey, String script, List<String> keys) {
		return sharded.shard(shardkey).eval(script, keys);
	}

	@Override
	public Object eval(String shardkey, String script, List<String> keys, List<String> args) {
		return sharded.shard(shardkey).eval(script, keys, args);
	}

	@Override
	public Object evalsha(String shardkey, String sha1) {
		return sharded.shard(shardkey).evalsha(sha1);
	}

	@Override
	public Object evalsha(String shardkey, String sha1, List<String> keys) {
		return sharded.shard(shardkey).evalsha(sha1, keys);
	}

	@Override
	public Object evalsha(String shardkey, String sha1, List<String> keys, List<String> args) {
		return sharded.shard(shardkey).evalsha(sha1, keys, args);
	}

	@Override
	public Boolean scriptexists(String shardkey, String sha1) {
		return sharded.shard(shardkey).scriptexists(sha1);
	}

	@Override
	public Boolean[] scriptexists(String shardkey, String... sha1s) {
		return sharded.shard(shardkey).scriptexists(sha1s);
	}

	@Override
	public String scriptflush(String shardkey) {
		return sharded.shard(shardkey).scriptflush();
	}

	@Override
	public String scriptkill(String shardkey) {
		return sharded.shard(shardkey).scriptkill();
	}

	@Override
	public String scriptload(String shardkey, String script) {
		return sharded.shard(shardkey).scriptload(script);
	}
	
}

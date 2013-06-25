package org.craft.atom.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.craft.atom.redis.api.Redis;
import org.craft.atom.redis.api.ShardedRedis;
import org.craft.atom.redis.api.handler.RedisPsubscribeHandler;
import org.craft.atom.redis.api.handler.RedisSubscribeHandler;
import org.craft.atom.redis.spi.Sharded;

/**
 * @author mindwind
 * @version 1.0, Jun 25, 2013
 */
public class DefaultShardedRedis extends AbstractRedis implements ShardedRedis {
	
	private Sharded sharded;
	
	
	// ~ ---------------------------------------------------------------------------------------------------------
	

	public DefaultShardedRedis(List<Redis> shards) {
		this.sharded = new MurmurHashSharded(shards);
	}
	
	public DefaultShardedRedis(Sharded sharded) {
		this.sharded = sharded;
	}
	
	
	// ~ --------------------------------------------------------------------------------------------------------- Keys
	
	
	@Override
	public Long del(String shardkey, String... keys) {
		return sharded.getShard(shardkey).del(keys);
	}
	
	@Override
	public String dump(String shardkey, String key) {
		return sharded.getShard(shardkey).dump(key);
	}

	@Override
	public Boolean exists(String shardkey, String key) {
		return sharded.getShard(shardkey).exists(key);
	}
	
	@Override
	public Long expire(String shardkey, String key, int seconds) {
		return sharded.getShard(shardkey).expire(key, seconds);
	}

	@Override
	public Long expireat(String shardkey, String key, long timestamp) {
		return sharded.getShard(shardkey).expireat(key, timestamp);
	}

	@Override
	public Set<String> keys(String shardkey, String pattern) {
		return sharded.getShard(shardkey).keys(pattern);
	}

	@Override
	public String migrate(String shardkey, String host, int port, String key, int destinationdb, int timeout) {
		return sharded.getShard(shardkey).migrate(host, port, key, destinationdb, timeout);
	}

	@Override
	public Long move(String shardkey, String key, int db) {
		return sharded.getShard(shardkey).move(key, db);
	}

	@Override
	public Long objectrefcount(String shardkey, String key) {
		return sharded.getShard(shardkey).objectrefcount(key);
	}

	@Override
	public String objectencoding(String shardkey, String key) {
		return sharded.getShard(shardkey).objectencoding(key);
	}

	@Override
	public Long objectidletime(String shardkey, String key) {
		return sharded.getShard(shardkey).objectidletime(key);
	}

	@Override
	public Long persist(String shardkey, String key) {
		return sharded.getShard(shardkey).persist(key);
	}

	@Override
	public Long pexpire(String shardkey, String key, int milliseconds) {
		return sharded.getShard(shardkey).pexpire(key, milliseconds);
	}

	@Override
	public Long pexpireat(String shardkey, String key, long millisecondstimestamp) {
		return sharded.getShard(shardkey).pexpireat(key, millisecondstimestamp);
	}

	@Override
	public Long pttl(String shardkey, String key) {
		return sharded.getShard(shardkey).pttl(key);
	}

	@Override
	public String randomkey(String shardkey) {
		return sharded.getShard(shardkey).randomkey();
	}

	@Override
	public String rename(String shardkey, String key, String newkey) {
		return sharded.getShard(shardkey).rename(key, newkey);
	}

	@Override
	public Long renamenx(String shardkey, String key, String newkey) {
		return sharded.getShard(shardkey).renamenx(key, newkey);
	}

	@Override
	public String restore(String shardkey, String key, long ttl, String serializedvalue) {
		return sharded.getShard(shardkey).restore(key, ttl, serializedvalue);
	}

	@Override
	public List<String> sort(String shardkey, String key) {
		return sharded.getShard(shardkey).sort(key);
	}

	@Override
	public List<String> sort(String shardkey, String key, boolean desc) {
		return sharded.getShard(shardkey).sort(key, desc);
	}

	@Override
	public List<String> sort(String shardkey, String key, boolean alpha, boolean desc) {
		return sharded.getShard(shardkey).sort(key, alpha, desc);
	}

	@Override
	public List<String> sort(String shardkey, String key, int offset, int count) {
		return sharded.getShard(shardkey).sort(key, offset, count);
	}

	@Override
	public List<String> sort(String shardkey, String key, int offset, int count, boolean alpha, boolean desc) {
		return sharded.getShard(shardkey).sort(key, offset, count, alpha, desc);
	}

	@Override
	public List<String> sort(String shardkey, String key, String bypattern, String... getpatterns) {
		return sharded.getShard(shardkey).sort(key, bypattern, getpatterns);
	}

	@Override
	public List<String> sort(String shardkey, String key, String bypattern, boolean desc, String... getpatterns) {
		return sharded.getShard(shardkey).sort(key, bypattern, desc, getpatterns);
	}

	@Override
	public List<String> sort(String shardkey, String key, String bypattern, boolean alpha, boolean desc, String... getpatterns) {
		return sharded.getShard(shardkey).sort(key, bypattern, alpha, desc, getpatterns);
	}

	@Override
	public List<String> sort(String shardkey, String key, String bypattern, int offset, int count, String... getpatterns) {
		return sharded.getShard(shardkey).sort(key, bypattern, offset, count, getpatterns);
	}

	@Override
	public List<String> sort(String shardkey, String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String... getpatterns) {
		return sharded.getShard(shardkey).sort(key, bypattern, offset, count, alpha, desc, getpatterns);
	}

	@Override
	public Long sort(String shardkey, String key, String bypattern, String destination) {
		return sharded.getShard(shardkey).sort(key, bypattern, destination);
	}

	@Override
	public Long sort(String shardkey, String key, String bypattern, boolean desc, String destination, String... getpatterns) {
		return sharded.getShard(shardkey).sort(key, bypattern, desc, destination, getpatterns);
	}

	@Override
	public Long sort(String shardkey, String key, String bypattern, boolean alpha, boolean desc, String destination, String... getpatterns) {
		return sharded.getShard(shardkey).sort(key, bypattern, alpha, desc, destination, getpatterns);
	}

	@Override
	public Long sort(String shardkey, String key, String bypattern, int offset, int count, String destination, String... getpatterns) {
		return sharded.getShard(shardkey).sort(key, bypattern, offset, count, destination, getpatterns);
	}

	@Override
	public Long sort(String shardkey, String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String destination, String... getpatterns) {
		return sharded.getShard(shardkey).sort(key, bypattern, offset, count, alpha, desc, destination, getpatterns);
	}

	@Override
	public Long ttl(String shardkey, String key) {
		return sharded.getShard(shardkey).ttl(key);
	}

	@Override
	public String type(String shardkey, String key) {
		return sharded.getShard(shardkey).type(key);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Strings
	

	@Override
	public Long append(String shardkey, String key, String value) {
		return sharded.getShard(shardkey).append(key, value);
	}

	@Override
	public Long bitcount(String shardkey, String key) {
		return sharded.getShard(shardkey).bitcount(key);
	}

	@Override
	public Long bitnot(String shardkey, String destkey, String key) {
		return sharded.getShard(shardkey).bitnot(destkey, key);
	}

	@Override
	public Long bitand(String shardkey, String destkey, String... keys) {
		return sharded.getShard(shardkey).bitand(destkey, keys);
	}

	@Override
	public Long bitor(String shardkey, String destkey, String... keys) {
		return sharded.getShard(shardkey).bitor(destkey, keys);
	}

	@Override
	public Long bitxor(String shardkey, String destkey, String... keys) {
		return sharded.getShard(shardkey).bitxor(destkey, keys);
	}

	@Override
	public Long decr(String shardkey, String key) {
		return sharded.getShard(shardkey).decr(key);
	}

	@Override
	public Long decrby(String shardkey, String key, long decrement) {
		return sharded.getShard(shardkey).decrby(key, decrement);
	}

	@Override
	public String get(String shardkey, String key) {
		return sharded.getShard(shardkey).get(key);
	}

	@Override
	public Boolean getbit(String shardkey, String key, long offset) {
		return sharded.getShard(shardkey).getbit(key, offset);
	}

	@Override
	public String getrange(String shardkey, String key, long start, long end) {
		return sharded.getShard(shardkey).getrange(key, start, end);
	}

	@Override
	public String getset(String shardkey, String key, String value) {
		return sharded.getShard(shardkey).getset(key, value);
	}

	@Override
	public Long incr(String shardkey, String key) {
		return sharded.getShard(shardkey).incr(key);
	}

	@Override
	public Long incrby(String shardkey, String key, long increment) {
		return sharded.getShard(shardkey).incrby(key, increment);
	}

	@Override
	public Double incrbyfloat(String shardkey, String key, double increment) {
		return sharded.getShard(shardkey).incrbyfloat(key, increment);
	}

	@Override
	public List<String> mget(String shardkey, String... keys) {
		return sharded.getShard(shardkey).mget(keys);
	}

	@Override
	public String mset(String shardkey, String... keysvalues) {
		return sharded.getShard(shardkey).mset(keysvalues);
	}

	@Override
	public Long msetnx(String shardkey, String... keysvalues) {
		return sharded.getShard(shardkey).msetnx(keysvalues);
	}

	@Override
	public String psetex(String shardkey, String key, int milliseconds, String value) {
		return sharded.getShard(shardkey).psetex(key, milliseconds, value);
	}

	@Override
	public String set(String shardkey, String key, String value) {
		return sharded.getShard(shardkey).set(key, value);
	}

	@Override
	public String setxx(String shardkey, String key, String value) {
		return sharded.getShard(shardkey).setxx(key, value);
	}

	@Override
	public String setnxex(String shardkey, String key, String value, int seconds) {
		return sharded.getShard(shardkey).setnxex(key, value, seconds);
	}

	@Override
	public String setnxpx(String shardkey, String key, String value, int milliseconds) {
		return sharded.getShard(shardkey).setnxpx(key, value, milliseconds);
	}

	@Override
	public String setxxex(String shardkey, String key, String value, int seconds) {
		return sharded.getShard(shardkey).setxxex(key, value, seconds);
	}

	@Override
	public String setxxpx(String shardkey, String key, String value, int milliseconds) {
		return sharded.getShard(shardkey).setxxpx(key, value, milliseconds);
	}

	@Override
	public Boolean setbit(String shardkey, String key, long offset, boolean value) {
		return sharded.getShard(shardkey).setbit(key, offset, value);
	}

	@Override
	public String setex(String shardkey, String key, int seconds, String value) {
		return sharded.getShard(shardkey).setex(key, seconds, value);
	}

	@Override
	public Long setnx(String shardkey, String key, String value) {
		return sharded.getShard(shardkey).setnx(key, value);
	}

	@Override
	public Long setrange(String shardkey, String key, long offset, String value) {
		return sharded.getShard(shardkey).setrange(key, offset, value);
	}

	@Override
	public Long strlen(String shardkey, String key) {
		return sharded.getShard(shardkey).strlen(key);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Hashes
	

	@Override
	public Long hdel(String shardkey, String key, String... fields) {
		return sharded.getShard(shardkey).hdel(key, fields);
	}

	@Override
	public Boolean hexists(String shardkey, String key, String field) {
		return sharded.getShard(shardkey).hexists(key, field);
	}

	@Override
	public String hget(String shardkey, String key, String field) {
		return sharded.getShard(shardkey).hget(key, field);
	}

	@Override
	public Map<String, String> hgetall(String shardkey, String key) {
		return sharded.getShard(shardkey).hgetall(key);
	}

	@Override
	public Long hincrby(String shardkey, String key, String field, long increment) {
		return sharded.getShard(shardkey).hincrby(key, field, increment);
	}

	@Override
	public Double hincrbyfloat(String shardkey, String key, String field, double increment) {
		return sharded.getShard(shardkey).hincrbyfloat(key, field, increment);
	}

	@Override
	public Set<String> hkeys(String shardkey, String key) {
		return sharded.getShard(shardkey).hkeys(key);
	}

	@Override
	public Long hlen(String shardkey, String key) {
		return sharded.getShard(shardkey).hlen(key);
	}

	@Override
	public List<String> hmget(String shardkey, String key, String... fields) {
		return sharded.getShard(shardkey).hmget(key, fields);
	}

	@Override
	public String hmset(String shardkey, String key, Map<String, String> fieldvalues) {
		return sharded.getShard(shardkey).hmset(key, fieldvalues);
	}

	@Override
	public Long hset(String shardkey, String key, String field, String value) {
		return sharded.getShard(shardkey).hset(key, field, value);
	}

	@Override
	public Long hsetnx(String shardkey, String key, String field, String value) {
		return sharded.getShard(shardkey).hsetnx(key, field, value);
	}

	@Override
	public List<String> hvals(String shardkey, String key) {
		return sharded.getShard(shardkey).hvals(key);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Lists
	

	@Override
	public String blpop(String shardkey, String key) {
		return sharded.getShard(shardkey).blpop(key);
	}

	@Override
	public String blpop(String shardkey, String key, int timeout) {
		return sharded.getShard(shardkey).blpop(key, timeout);
	}

	@Override
	public Map<String, String> blpop(String shardkey, String... keys) {
		return sharded.getShard(shardkey).blpop(keys);
	}

	@Override
	public Map<String, String> blpop(String shardkey, int timeout, String... keys) {
		return sharded.getShard(shardkey).blpop(timeout, keys);
	}

	@Override
	public String brpop(String shardkey, String key) {
		return sharded.getShard(shardkey).brpop(key);
	}

	@Override
	public String brpop(String shardkey, String key, int timeout) {
		return sharded.getShard(shardkey).brpop(key, timeout);
	}

	@Override
	public Map<String, String> brpop(String shardkey, String... keys) {
		return sharded.getShard(shardkey).brpop(keys);
	}

	@Override
	public Map<String, String> brpop(String shardkey, int timeout, String... keys) {
		return sharded.getShard(shardkey).brpop(timeout, keys);
	}

	@Override
	public String brpoplpush(String shardkey, String source, String destination, int timeout) {
		return sharded.getShard(shardkey).brpoplpush(source, destination, timeout);
	}

	@Override
	public String lindex(String shardkey, String key, long index) {
		return sharded.getShard(shardkey).lindex(key, index);
	}

	@Override
	public Long linsertbefore(String shardkey, String key, String pivot, String value) {
		return sharded.getShard(shardkey).linsertbefore(key, pivot, value);
	}

	@Override
	public Long linsertafter(String shardkey, String key, String pivot, String value) {
		return sharded.getShard(shardkey).linsertafter(key, pivot, value);
	}

	@Override
	public Long llen(String shardkey, String key) {
		return sharded.getShard(shardkey).llen(key);
	}

	@Override
	public String lpop(String shardkey, String key) {
		return sharded.getShard(shardkey).lpop(key);
	}

	@Override
	public Long lpush(String shardkey, String key, String... values) {
		return sharded.getShard(shardkey).lpush(key, values);
	}

	@Override
	public Long lpushx(String shardkey, String key, String value) {
		return sharded.getShard(shardkey).lpushx(key, value);
	}

	@Override
	public List<String> lrange(String shardkey, String key, long start, long stop) {
		return sharded.getShard(shardkey).lrange(key, start, stop);
	}

	@Override
	public Long lrem(String shardkey, String key, long count, String value) {
		return sharded.getShard(shardkey).lrem(key, count, value);
	}

	@Override
	public String lset(String shardkey, String key, long index, String value) {
		return sharded.getShard(shardkey).lset(key, index, value);
	}

	@Override
	public String ltrim(String shardkey, String key, long start, long stop) {
		return sharded.getShard(shardkey).ltrim(key, start, stop);
	}

	@Override
	public String rpop(String shardkey, String key) {
		return sharded.getShard(shardkey).rpop(key);
	}

	@Override
	public String rpoplpush(String shardkey, String source, String destination) {
		return sharded.getShard(shardkey).rpoplpush(source, destination);
	}

	@Override
	public Long rpush(String shardkey, String key, String... values) {
		return sharded.getShard(shardkey).rpush(key, values);
	}

	@Override
	public Long rpushx(String shardkey, String key, String value) {
		return sharded.getShard(shardkey).rpushx(key, value);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Sets
	

	@Override
	public Long sadd(String shardkey, String key, String... members) {
		return sharded.getShard(shardkey).sadd(key, members);
	}

	@Override
	public Long scard(String shardkey, String key) {
		return sharded.getShard(shardkey).scard(key);
	}

	@Override
	public Set<String> sdiff(String shardkey, String... keys) {
		return sharded.getShard(shardkey).sdiff(keys);
	}

	@Override
	public Long sdiffstore(String shardkey, String destination, String... keys) {
		return sharded.getShard(shardkey).sdiffstore(destination, keys);
	}

	@Override
	public Set<String> sinter(String shardkey, String... keys) {
		return sharded.getShard(shardkey).sinter(keys);
	}

	@Override
	public Long sinterstore(String shardkey, String destination, String... keys) {
		return sharded.getShard(shardkey).sinterstore(destination, keys);
	}

	@Override
	public Boolean sismember(String shardkey, String key, String member) {
		return sharded.getShard(shardkey).sismember(key, member);
	}

	@Override
	public Set<String> smembers(String shardkey, String key) {
		return sharded.getShard(shardkey).smembers(key);
	}

	@Override
	public Long smove(String shardkey, String source, String destination, String member) {
		return sharded.getShard(shardkey).smove(source, destination, member);
	}

	@Override
	public String spop(String shardkey, String key) {
		return sharded.getShard(shardkey).spop(key);
	}

	@Override
	public Set<String> srandmember(String shardkey, String key, int count) {
		return sharded.getShard(shardkey).srandmember(key, count);
	}

	@Override
	public String srandmember(String shardkey, String key) {
		return sharded.getShard(shardkey).srandmember(key);
	}

	@Override
	public Long srem(String shardkey, String key, String... members) {
		return sharded.getShard(shardkey).srem(key, members);
	}
	
	@Override
	public Set<String> sunion(String shardkey, String... keys) {
		return sharded.getShard(shardkey).sunion(keys);
	}

	@Override
	public Long sunionstore(String shardkey, String destination, String... keys) {
		return sharded.getShard(shardkey).sunionstore(destination, keys);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------- Sorted Sets
	

	@Override
	public Long zadd(String shardkey, String key, double score, String member) {
		return sharded.getShard(shardkey).zadd(key, score, member);
	}

	@Override
	public Long zcard(String shardkey, String key) {
		return sharded.getShard(shardkey).zcard(key);
	}

	@Override
	public Long zcount(String shardkey, String key, double min, double max) {
		return sharded.getShard(shardkey).zcount(key, min, max);
	}

	@Override
	public Double zincrby(String shardkey, String key, double score, String member) {
		return sharded.getShard(shardkey).zincrby(key, score, member);
	}

	@Override
	public Long zinterstore(String shardkey, String destination, String... keys) {
		return sharded.getShard(shardkey).zinterstore(destination, keys);
	}

	@Override
	public Long zinterstoremax(String shardkey, String destination, String... keys) {
		return sharded.getShard(shardkey).zinterstoremax(destination, keys);
	}

	@Override
	public Long zinterstoremin(String shardkey, String destination, String... keys) {
		return sharded.getShard(shardkey).zinterstoremin(destination, keys);
	}

	@Override
	public Long zinterstore(String shardkey, String destination, Map<String, Integer> weightkeys) {
		return sharded.getShard(shardkey).zinterstore(destination, weightkeys);
	}

	@Override
	public Long zinterstoremax(String shardkey, String destination, Map<String, Integer> weightkeys) {
		return sharded.getShard(shardkey).zinterstoremax(destination, weightkeys);
	}

	@Override
	public Long zinterstoremin(String shardkey, String destination, Map<String, Integer> weightkeys) {
		return sharded.getShard(shardkey).zinterstoremin(destination, weightkeys);
	}

	@Override
	public Set<String> zrange(String shardkey, String key, long start, long stop) {
		return sharded.getShard(shardkey).zrange(key, start, stop);
	}

	@Override
	public Map<String, Double> zrangewithscores(String shardkey, String key, long start, long stop) {
		return sharded.getShard(shardkey).zrangewithscores(key, start, stop);
	}

	@Override
	public Set<String> zrangebyscore(String shardkey, String key, double min, double max) {
		return sharded.getShard(shardkey).zrangebyscore(key, min, max);
	}

	@Override
	public Set<String> zrangebyscore(String shardkey, String key, String min, String max) {
		return sharded.getShard(shardkey).zrangebyscore(key, min, max);
	}

	@Override
	public Set<String> zrangebyscore(String shardkey, String key, double min, double max, int offset, int count) {
		return sharded.getShard(shardkey).zrangebyscore(key, min, max, offset, count);
	}

	@Override
	public Set<String> zrangebyscore(String shardkey, String key, String min, String max, int offset, int count) {
		return sharded.getShard(shardkey).zrangebyscore(key, min, max, offset, count);
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String shardkey, String key, double min, double max) {
		return sharded.getShard(shardkey).zrangebyscorewithscores(key, min, max);
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String shardkey, String key, String min, String max) {
		return sharded.getShard(shardkey).zrangebyscorewithscores(key, min, max);
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String shardkey, String key, double min, double max, int offset, int count) {
		return sharded.getShard(shardkey).zrangebyscorewithscores(key, min, max, offset, count);
	}

	@Override
	public Map<String, Double> zrangebyscorewithscores(String shardkey, String key, String min, String max, int offset, int count) {
		return sharded.getShard(shardkey).zrangebyscorewithscores(key, min, max, offset, count);
	}

	@Override
	public Long zrank(String shardkey, String key, String member) {
		return sharded.getShard(shardkey).zrank(key, member);
	}

	@Override
	public Long zrem(String shardkey, String key, String... members) {
		return sharded.getShard(shardkey).zrem(key, members);
	}

	@Override
	public Long zremrangebyrank(String shardkey, String key, long start, long stop) {
		return sharded.getShard(shardkey).zremrangebyrank(key, start, stop);
	}

	@Override
	public Long zremrangebyscore(String shardkey, String key, double min, double max) {
		return sharded.getShard(shardkey).zremrangebyscore(key, min, max);
	}

	@Override
	public Long zremrangebyscore(String shardkey, String key, String min, String max) {
		return sharded.getShard(shardkey).zremrangebyscore(key, min, max);
	}

	@Override
	public Set<String> zrevrange(String shardkey, String key, long start, long stop) {
		return sharded.getShard(shardkey).zrevrange(key, start, stop);
	}

	@Override
	public Map<String, Double> zrevrangewithscores(String shardkey, String key, long start, long stop) {
		return sharded.getShard(shardkey).zrevrangewithscores(key, start, stop);
	}

	@Override
	public Set<String> zrevrangebyscore(String shardkey, String key, double max, double min) {
		return sharded.getShard(shardkey).zrevrangebyscore(key, max, min);
	}

	@Override
	public Set<String> zrevrangebyscore(String shardkey, String key, String max, String min) {
		return sharded.getShard(shardkey).zrevrangebyscore(key, max, min);
	}

	@Override
	public Set<String> zrevrangebyscore(String shardkey, String key, double max, double min, int offset, int count) {
		return sharded.getShard(shardkey).zrevrangebyscore(key, max, min, offset, count);
	}

	@Override
	public Set<String> zrevrangebyscore(String shardkey, String key, String max, String min, int offset, int count) {
		return sharded.getShard(shardkey).zrevrangebyscore(key, max, min, offset, count);
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String shardkey, String key, double max, double min) {
		return sharded.getShard(shardkey).zrevrangebyscorewithscores(key, max, min);
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String shardkey, String key, String max, String min) {
		return sharded.getShard(shardkey).zrevrangebyscorewithscores(key, max, min);
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String shardkey, String key, double max, double min, int offset, int count) {
		return sharded.getShard(shardkey).zrevrangebyscorewithscores(key, max, min, offset, count);
	}

	@Override
	public Map<String, Double> zrevrangebyscorewithscores(String shardkey, String key, String max, String min, int offset, int count) {
		return sharded.getShard(shardkey).zrevrangebyscorewithscores(key, max, min, offset, count);
	}

	@Override
	public Long zrevrank(String shardkey, String key, String member) {
		return sharded.getShard(shardkey).zrevrank(key, member);
	}

	@Override
	public Double zscore(String shardkey, String key, String member) {
		return sharded.getShard(shardkey).zscore(key, member);
	}

	@Override
	public Long zunionstore(String shardkey, String destination, String... keys) {
		return sharded.getShard(shardkey).zunionstore(destination, keys);
	}

	@Override
	public Long zunionstoremax(String shardkey, String destination, String... keys) {
		return sharded.getShard(shardkey).zunionstoremax(destination, keys);
	}

	@Override
	public Long zunionstoremin(String shardkey, String destination, String... keys) {
		return sharded.getShard(shardkey).zunionstoremin(destination, keys);
	}

	@Override
	public Long zunionstore(String shardkey, String destination, Map<String, Integer> weightkeys) {
		return sharded.getShard(shardkey).zunionstore(destination, weightkeys);
	}

	@Override
	public Long zunionstoremax(String shardkey, String destination, Map<String, Integer> weightkeys) {
		return sharded.getShard(shardkey).zunionstoremax(destination, weightkeys);
	}

	@Override
	public Long zunionstoremin(String shardkey, String destination, Map<String, Integer> weightkeys) {
		return sharded.getShard(shardkey).zunionstoremin(destination, weightkeys);
	}
	
	
	// ~ ----------------------------------------------------------------------------------------------------- Pub/Sub

	
	@Override
	public void psubscribe(String shardkey, RedisPsubscribeHandler handler, String... patterns) {
		sharded.getShard(shardkey).psubscribe(handler, patterns);
	}

	@Override
	public Long publish(String shardkey, String channel, String message) {
		return sharded.getShard(shardkey).publish(channel, message);
	}

	@Override
	public List<String> punsubscribe(String shardkey, String... patterns) {
		return sharded.getShard(shardkey).punsubscribe(patterns);
	}

	@Override
	public void subscribe(String shardkey, RedisSubscribeHandler handler, String... channels) {
		sharded.getShard(shardkey).subscribe(handler, channels);
	}

	@Override
	public List<String> unsubscribe(String shardkey, String... channels) {
		return sharded.getShard(shardkey).unsubscribe(channels);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------ Transactions

	
	@Override
	public String discard(String shardkey) {
		return sharded.getShard(shardkey).discard();
	}

	@Override
	public List<Object> exec(String shardkey) {
		return sharded.getShard(shardkey).exec();
	}

	@Override
	public String multi(String shardkey) {
		return sharded.getShard(shardkey).multi();
	}

	@Override
	public String unwatch(String shardkey) {
		return sharded.getShard(shardkey).unwatch();
	}

	@Override
	public String watch(String shardkey, String... keys) {
		return sharded.getShard(shardkey).watch(keys);
	}
	
	
	// ~ --------------------------------------------------------------------------------------------------- Scripting
	

	@Override
	public Object eval(String shardkey, String script) {
		return sharded.getShard(shardkey).eval(script);
	}

	@Override
	public Object eval(String shardkey, String script, List<String> keys) {
		return sharded.getShard(shardkey).eval(script, keys);
	}

	@Override
	public Object eval(String shardkey, String script, List<String> keys, List<String> args) {
		return sharded.getShard(shardkey).eval(script, keys, args);
	}

	@Override
	public Object evalsha(String shardkey, String sha1) {
		return sharded.getShard(shardkey).evalsha(sha1);
	}

	@Override
	public Object evalsha(String shardkey, String sha1, List<String> keys) {
		return sharded.getShard(shardkey).evalsha(sha1, keys);
	}

	@Override
	public Object evalsha(String shardkey, String sha1, List<String> keys, List<String> args) {
		return sharded.getShard(shardkey).evalsha(sha1, keys, args);
	}

	@Override
	public Boolean scriptexists(String shardkey, String sha1) {
		return sharded.getShard(shardkey).scriptexists(sha1);
	}

	@Override
	public Boolean[] scriptexists(String shardkey, String... sha1) {
		return sharded.getShard(shardkey).scriptexists(sha1);
	}

	@Override
	public String scriptflush(String shardkey) {
		return sharded.getShard(shardkey).scriptflush();
	}

	@Override
	public String scriptkill(String shardkey) {
		return sharded.getShard(shardkey).scriptkill();
	}

	@Override
	public String scriptload(String shardkey, String script) {
		return sharded.getShard(shardkey).scriptload(script);
	}

	@Override
	public String toString() {
		return String.format("ShardedRedis [sharded=%s]", sharded.getAllShards());
	}
	
	

}

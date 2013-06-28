package org.craft.atom.redis.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.craft.atom.redis.api.handler.RedisPsubscribeHandler;
import org.craft.atom.redis.api.handler.RedisSubscribeHandler;

/**
 * All sharded redis commands.
 * 
 * @author mindwind
 * @version 1.0, Jun 26, 2013
 */
public interface ShardedRedisCommand {

	
	// ~ --------------------------------------------------------------------------------------------------------- Keys
	
	
	Long del(String shardkey, String... keys);
	String dump(String shardkey, String key);
	Boolean exists(String shardkey, String key);
	Long expire(String shardkey, String key, int seconds);
	Long expireat(String shardkey, String key, long timestamp);
	Set<String> keys(String shardkey, String pattern);
	String migrate(String shardkey, String host, int port, String key, int destinationdb, int timeout);
	Long move(String shardkey, String key, int db);
	Long objectrefcount(String shardkey, String key);
	String objectencoding(String shardkey, String key);
	Long objectidletime(String shardkey, String key);
	Long persist(String shardkey, String key);
	Long pexpire(String shardkey, String key, int milliseconds);
	Long pexpireat(String shardkey, String key, long millisecondstimestamp);
	Long pttl(String shardkey, String key);
	String randomkey(String shardkey);
	String rename(String shardkey, String key, String newkey);
	Long renamenx(String shardkey, String key, String newkey);
	String restore(String shardkey, String key, long ttl, String serializedvalue);
	List<String> sort(String shardkey, String key);
	List<String> sort(String shardkey, String key, boolean desc);
	List<String> sort(String shardkey, String key, boolean alpha, boolean desc);
	List<String> sort(String shardkey, String key, int offset, int count);
	List<String> sort(String shardkey, String key, int offset, int count, boolean alpha, boolean desc);
	List<String> sort(String shardkey, String key, String bypattern, String... getpatterns);
	List<String> sort(String shardkey, String key, String bypattern, boolean desc, String... getpatterns);
	List<String> sort(String shardkey, String key, String bypattern, boolean alpha, boolean desc, String... getpatterns);
	List<String> sort(String shardkey, String key, String bypattern, int offset, int count, String... getpatterns);
	List<String> sort(String shardkey, String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String... getpatterns);
	Long sort(String shardkey, String key, String bypattern, String destination);
	Long sort(String shardkey, String key, String bypattern, boolean desc, String destination, String... getpatterns);
	Long sort(String shardkey, String key, String bypattern, boolean alpha, boolean desc, String destination, String... getpatterns);
	Long sort(String shardkey, String key, String bypattern, int offset, int count, String destination, String... getpatterns);
	Long sort(String shardkey, String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String destination, String... getpatterns);
	Long ttl(String shardkey, String key);
	String type(String shardkey, String key);
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Strings
	
	
	Long append(String shardkey, String key, String value);
	Long bitcount(String shardkey, String key);
	Long bitnot(String shardkey, String destkey, String key);
	Long bitand(String shardkey, String destkey, String... keys);
	Long bitor(String shardkey, String destkey, String... keys);
	Long bitxor(String shardkey, String destkey, String... keys);
	Long decr(String shardkey, String key);
	Long decrby(String shardkey, String key, long decrement);
	String get(String shardkey, String key);
	Boolean getbit(String shardkey, String key, long offset);
	String getrange(String shardkey, String key, long start, long end);
	String getset(String shardkey, String key, String value);
	Long incr(String shardkey, String key);
	Long incrby(String shardkey, String key, long increment);
	Double incrbyfloat(String shardkey, String key, double increment);
	List<String> mget(String shardkey, String... keys);
	String mset(String shardkey, String... keysvalues);
	Long msetnx(String shardkey, String... keysvalues);
	String psetex(String shardkey, String key, int milliseconds, String value);
	String set(String shardkey, String key, String value);
	String setxx(String shardkey, String key, String value);
	String setnxex(String shardkey, String key, String value, int seconds);
	String setnxpx(String shardkey, String key, String value, int milliseconds);
	String setxxex(String shardkey, String key, String value, int seconds);
	String setxxpx(String shardkey, String key, String value, int milliseconds);
	Boolean setbit(String shardkey, String key, long offset, boolean value);
	String setex(String shardkey, String key, int seconds, String value);
	Long setnx(String shardkey, String key, String value);
	Long setrange(String shardkey, String key, long offset, String value);
	Long strlen(String shardkey, String key);
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Hashes
	
	
	Long hdel(String shardkey, String key, String... fields);
	Boolean hexists(String shardkey, String key, String field);
	String hget(String shardkey, String key, String field);
	Map<String, String> hgetall(String shardkey, String key);
	Long hincrby(String shardkey, String key, String field, long increment);
	Double hincrbyfloat(String shardkey, String key, String field, double increment);
	Set<String> hkeys(String shardkey, String key);
	Long hlen(String shardkey, String key);
	List<String> hmget(String shardkey, String key, String... fields);
	String hmset(String shardkey, String key, Map<String, String> fieldvalues);
	Long hset(String shardkey, String key, String field, String value);
	Long hsetnx(String shardkey, String key, String field, String value);
	List<String> hvals(String shardkey, String key);
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Lists
	
	
	String blpop(String shardkey, String key);
	String blpop(String shardkey, String key, int timeout);
	Map<String, String> blpop(String shardkey, String... keys);
	Map<String, String> blpop(String shardkey, int timeout, String... keys);
	String brpop(String shardkey, String key);
	String brpop(String shardkey, String key, int timeout);
	Map<String, String> brpop(String shardkey, String... keys);
	Map<String, String> brpop(String shardkey, int timeout, String... keys);
	String brpoplpush(String shardkey, String source, String destination, int timeout);
	String lindex(String shardkey, String key, long index);
	Long linsertbefore(String shardkey, String key, String pivot, String value);
	Long linsertafter(String shardkey, String key, String pivot, String value);
	Long llen(String shardkey, String key);
	String lpop(String shardkey, String key);
	Long lpush(String shardkey, String key, String... values);
	Long lpushx(String shardkey, String key, String value);
	List<String> lrange(String shardkey, String key, long start, long stop);
	Long lrem(String shardkey, String key, long count, String value);
	String lset(String shardkey, String key, long index, String value);
	String ltrim(String shardkey, String key, long start, long stop);
	String rpop(String shardkey, String key);
	String rpoplpush(String shardkey, String source, String destination); 
	Long rpush(String shardkey, String key, String... values);
	Long rpushx(String shardkey, String key, String value);
	
	
	// ~ -------------------------------------------------------------------------------------------------------- Sets
	

	Long sadd(String shardkey, String key, String... members);
	Long scard(String shardkey, String key);
	Set<String> sdiff(String shardkey, String... keys);
	Long sdiffstore(String shardkey, String destination, String... keys);
	Set<String> sinter(String shardkey, String... keys);
	Long sinterstore(String shardkey, String destination, String... keys);
	Boolean sismember(String shardkey, String key, String member);
	Set<String> smembers(String shardkey, String key);
	Long smove(String shardkey, String source, String destination, String member);
	String spop(String shardkey, String key);
	Set<String> srandmember(String shardkey, String key, int count);
	String srandmember(String shardkey, String key);
	Long srem(String shardkey, String key, String... members);
	Set<String> sunion(String shardkey, String... keys);
	Long sunionstore(String shardkey, String destination, String... keys);
	
	
	// ~ -------------------------------------------------------------------------------------------------- Sorted Sets
	
	
	Long zadd(String shardkey, String key, double score, String member);
	Long zcard(String shardkey, String key);
	Long zcount(String shardkey, String key, double min, double max);
	Double zincrby(String shardkey, String key, double score, String member);
	Long zinterstore(String shardkey, String destination, String... keys);
	Long zinterstoremax(String shardkey, String destination, String... keys);
	Long zinterstoremin(String shardkey, String destination, String... keys);
	Long zinterstore(String shardkey, String destination, Map<String, Integer> weightkeys);
	Long zinterstoremax(String shardkey, String destination, Map<String, Integer> weightkeys);
	Long zinterstoremin(String shardkey, String destination, Map<String, Integer> weightkeys);
	Set<String> zrange(String shardkey, String key, long start, long stop);
	Map<String, Double> zrangewithscores(String shardkey, String key, long start, long stop);
	Set<String> zrangebyscore(String shardkey, String key, double min, double max);
	Set<String> zrangebyscore(String shardkey, String key, String min, String max);
	Set<String> zrangebyscore(String shardkey, String key, double min, double max, int offset, int count);
	Set<String> zrangebyscore(String shardkey, String key, String min, String max, int offset, int count);
	Map<String, Double> zrangebyscorewithscores(String shardkey, String key, double min, double max);
	Map<String, Double> zrangebyscorewithscores(String shardkey, String key, String min, String max);
	Map<String, Double> zrangebyscorewithscores(String shardkey, String key, double min, double max, int offset, int count);
	Map<String, Double> zrangebyscorewithscores(String shardkey, String key, String min, String max, int offset, int count);
	Long zrank(String shardkey, String key, String member);
	Long zrem(String shardkey, String key, String... members);
	Long zremrangebyrank(String shardkey, String key, long start, long stop);
	Long zremrangebyscore(String shardkey, String key, double min, double max);
	Long zremrangebyscore(String shardkey, String key, String min, String max);
	Set<String> zrevrange(String shardkey, String key, long start, long stop);
	Map<String, Double> zrevrangewithscores(String shardkey, String key, long start, long stop);
	Set<String> zrevrangebyscore(String shardkey, String key, double max, double min);
	Set<String> zrevrangebyscore(String shardkey, String key, String max, String min);
	Set<String> zrevrangebyscore(String shardkey, String key, double max, double min, int offset, int count);
	Set<String> zrevrangebyscore(String shardkey, String key, String max, String min, int offset, int count);
	Map<String, Double> zrevrangebyscorewithscores(String shardkey, String key, double max, double min);
	Map<String, Double> zrevrangebyscorewithscores(String shardkey, String key, String max, String min);
	Map<String, Double> zrevrangebyscorewithscores(String shardkey, String key, double max, double min, int offset, int count);
	Map<String, Double> zrevrangebyscorewithscores(String shardkey, String key, String max, String min, int offset, int count);
	Long zrevrank(String shardkey, String key, String member);
	Double zscore(String shardkey, String key, String member);
	Long zunionstore(String shardkey, String destination, String... keys);
	Long zunionstoremax(String shardkey, String destination, String... keys);
	Long zunionstoremin(String shardkey, String destination, String... keys);
	Long zunionstore(String shardkey, String destination, Map<String, Integer> weightkeys);
	Long zunionstoremax(String shardkey, String destination, Map<String, Integer> weightkeys);
	Long zunionstoremin(String shardkey, String destination, Map<String, Integer> weightkeys);
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Pub/Sub
	
	
	void psubscribe(String shardkey, RedisPsubscribeHandler handler, String... patterns);
	Long publish(String shardkey, String channel, String message);
	List<String> punsubscribe(String shardkey, String... patterns);
	void subscribe(String shardkey, RedisSubscribeHandler handler, String... channels);
	List<String> unsubscribe(String shardkey, String... channels);
	
	
	// ~ ------------------------------------------------------------------------------------------------- Transactions
	

	String discard(String shardkey, RedisTransaction t);
	List<Object> exec(String shardkey, RedisTransaction t);
	RedisTransaction multi(String shardkey);
	String unwatch(String shardkey);
	String watch(String shardkey, String... keys);
	
	
	// ~ --------------------------------------------------------------------------------------------------- Scripting
	
	
	Object eval(String shardkey, String script);
	Object eval(String shardkey, String script, List<String> keys);
	Object eval(String shardkey, String script, List<String> keys, List<String> args);
	Object evalsha(String shardkey, String sha1);
	Object evalsha(String shardkey, String sha1, List<String> keys);
	Object evalsha(String shardkey, String sha1, List<String> keys, List<String> args);
	Boolean scriptexists(String shardkey, String sha1);
	Boolean[] scriptexists(String shardkey, String... sha1s);
	String scriptflush(String shardkey);
	String scriptkill(String shardkey);
	String scriptload(String shardkey, String script);
	
}

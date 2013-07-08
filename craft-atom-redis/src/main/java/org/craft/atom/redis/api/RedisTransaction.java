package org.craft.atom.redis.api;

import java.util.Map;


/**
 *<b>Redis Transaction</b><br>
 * - You can begin a transaction by invoke <code>multi()</code> method.<br>
 * - A transaction execution must in one thread.<br>
 * - A transaction should be end with <code>discard()</code> or <code>exec()</code><br>
 * - <code>watch()</code> should be paired with <code>exec</code>, <code>discard()</code> or <code>unwatch()</code>
 * In most cases, transaction should be used as follows:
 * <pre><tt>
 *     RedisTransaction t = redis.multi();
 *     t.xx1()
 *     t.xx2()
 *     redis.exec(t);
 * </tt></pre>
 * 
 * @author mindwind
 * @version 1.0, Jun 27, 2013
 */
public interface RedisTransaction {
	
	
	// ~ --------------------------------------------------------------------------------------------------------- Keys
	
	
	void del(String... keys);
	void dump(String key);
	void exists(String key);
	void expire(String key, int seconds);
	void expireat(String key, long timestamp);
	void keys(String pattern);
	void migrate(String host, int port, String key, int destinationdb, int timeout);
	void move(String key, int db);
	void objectrefcount(String key);
	void objectencoding(String key);
	void objectidletime(String key);
	void persist(String key);
	void pexpire(String key, int milliseconds);
	void pexpireat(String key, long millisecondstimestamp);
	void pttl(String key);
	void randomkey();
	void rename(String key, String newkey);
	void renamenx(String key, String newkey);
	void restore(String key, int ttl, byte[] serializedvalue);
	void sort(String key);
	void sort(String key, boolean desc);
	void sort(String key, boolean alpha, boolean desc);
	void sort(String key, int offset, int count);
	void sort(String key, int offset, int count, boolean alpha, boolean desc);
	void sort(String key, String bypattern, String... getpatterns);
	void sort(String key, String bypattern, boolean desc, String... getpatterns);
	void sort(String key, String bypattern, boolean alpha, boolean desc, String... getpatterns);
	void sort(String key, String bypattern, int offset, int count, String... getpatterns);
	void sort(String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String... getpatterns);
	void sort(String key, String destination);
	void sort(String key, boolean desc, String destination);
	void sort(String key, boolean alpha, boolean desc, String destination);
	void sort(String key, int offset, int count, String destination);
	void sort(String key, int offset, int count, boolean alpha, boolean desc, String destination);
	void sort(String key, String bypattern, String destination, String... getpatterns);
	void sort(String key, String bypattern, boolean desc, String destination, String... getpatterns);
	void sort(String key, String bypattern, boolean alpha, boolean desc, String destination, String... getpatterns);
	void sort(String key, String bypattern, int offset, int count, String destination, String... getpatterns);
	void sort(String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String destination, String... getpatterns);
	void ttl(String key);
	void type(String key);
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Strings
	
	
	void append(String key, String value);
	void bitcount(String key);
	void bitnot(String destkey, String key);
	void bitand(String destkey, String... keys);
	void bitor(String destkey, String... keys);
	void bitxor(String destkey, String... keys);
	void decr(String key);
	void decrby(String key, long decrement);
	void get(String key);
	void getbit(String key, long offset);
	void getrange(String key, long start, long end);
	void getset(String key, String value);
	void incr(String key);
	void incrby(String key, long increment);
	void incrbyfloat(String key, double increment);
	void mget(String... keys);
	void mset(String... keysvalues);
	void msetnx(String... keysvalues);
	void psetex(String key, int milliseconds, String value);
	void set(String key, String value);
	void setxx(String key, String value);
	void setnxex(String key, String value, int seconds);
	void setnxpx(String key, String value, int milliseconds);
	void setxxex(String key, String value, int seconds);
	void setxxpx(String key, String value, int milliseconds);
	void setbit(String key, long offset, boolean value);
	void setex(String key, int seconds, String value);
	void setnx(String key, String value);
	void setrange(String key, long offset, String value);
	void strlen(String key);
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Hashes
	
	
	void hdel(String key, String... fields);
	void hexists(String key, String field);
	void hget(String key, String field);
	void hgetall(String key);
	void hincrby(String key, String field, long increment);
	void hincrbyfloat(String key, String field, double increment);
	void hkeys(String key);
	void hlen(String key);
	void hmget(String key, String... fields);
	void hmset(String key, Map<String, String> fieldvalues);
	void hset(String key, String field, String value);
	void hsetnx(String key, String field, String value);
	void hvals(String key);
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Lists
	
	
	void blpop(String key);
	void blpop(String key, int timeout);
	void blpop(String... keys);
	void blpop(int timeout, String... keys);
	void brpop(String key);
	void brpop(String key, int timeout);
	void brpop(String... keys);
	void brpop(int timeout, String... keys);
	void brpoplpush(String source, String destination, int timeout);
	void lindex(String key, long index);
	void linsertbefore(String key, String pivot, String value);
	void linsertafter(String key, String pivot, String value);
	void llen(String key);
	void lpop(String key);
	void lpush(String key, String... values);
	void lpushx(String key, String value);
	void lrange(String key, long start, long stop);
	void lrem(String key, long count, String value);
	void lset(String key, long index, String value);
	void ltrim(String key, long start, long stop);
	void rpop(String key);
	void rpoplpush(String source, String destination); 
	void rpush(String key, String... values);
	void rpushx(String key, String value);
	
	
	// ~ -------------------------------------------------------------------------------------------------------- Sets
	

	void sadd(String key, String... members);
	void scard(String key);
	void sdiff(String... keys);
	void sdiffstore(String destination, String... keys);
	void sinter(String... keys);
	void sinterstore(String destination, String... keys);
	void sismember(String key, String member);
	void smembers(String key);
	void smove(String source, String destination, String member);
	void spop(String key);
	void srandmember(String key, int count);
	void srandmember(String key);
	void srem(String key, String... members);
	void sunion(String... keys);
	void sunionstore(String destination, String... keys);
	
	
	// ~ -------------------------------------------------------------------------------------------------- Sorted Sets
	
	
	void zadd(String key, double score, String member);
	void zcard(String key);
	void zcount(String key, double min, double max);
	void zcount(String key, String min, String max);
	void zincrby(String key, double score, String member);
	void zinterstore(String destination, String... keys);
	void zinterstoremax(String destination, String... keys);
	void zinterstoremin(String destination, String... keys);
	void zinterstore(String destination, Map<String, Integer> weightkeys);
	void zinterstoremax(String destination, Map<String, Integer> weightkeys);
	void zinterstoremin(String destination, Map<String, Integer> weightkeys);
	void zrange(String key, long start, long stop);
	void zrangewithscores(String key, long start, long stop);
	void zrangebyscore(String key, double min, double max);
	void zrangebyscore(String key, String min, String max);
	void zrangebyscore(String key, double min, double max, int offset, int count);
	void zrangebyscore(String key, String min, String max, int offset, int count);
	void zrangebyscorewithscores(String key, double min, double max);
	void zrangebyscorewithscores(String key, String min, String max);
	void zrangebyscorewithscores(String key, double min, double max, int offset, int count);
	void zrangebyscorewithscores(String key, String min, String max, int offset, int count);
	void zrank(String key, String member);
	void zrem(String key, String... members);
	void zremrangebyrank(String key, long start, long stop);
	void zremrangebyscore(String key, double min, double max);
	void zremrangebyscore(String key, String min, String max);
	void zrevrange(String key, long start, long stop);
	void zrevrangewithscores(String key, long start, long stop);
	void zrevrangebyscore(String key, double max, double min);
	void zrevrangebyscore(String key, String max, String min);
	void zrevrangebyscore(String key, double max, double min, int offset, int count);
	void zrevrangebyscore(String key, String max, String min, int offset, int count);
	void zrevrangebyscorewithscores(String key, double max, double min);
	void zrevrangebyscorewithscores(String key, String max, String min);
	void zrevrangebyscorewithscores(String key, double max, double min, int offset, int count);
	void zrevrangebyscorewithscores(String key, String max, String min, int offset, int count);
	void zrevrank(String key, String member);
	void zscore(String key, String member);
	void zunionstore(String destination, String... keys);
	void zunionstoremax(String destination, String... keys);
	void zunionstoremin(String destination, String... keys);
	void zunionstore(String destination, Map<String, Integer> weightkeys);
	void zunionstoremax(String destination, Map<String, Integer> weightkeys);
	void zunionstoremin(String destination, Map<String, Integer> weightkeys);
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Pub/Sub
	
	
//	void psubscribe(RedisPsubscribeHandler handler, String... patterns);
	void publish(String channel, String message);
//	void punsubscribe(String... patterns);
//	void subscribe(RedisSubscribeHandler handler, String... channels);
//	void unsubscribe(String... channels);
	
	
	// ~ ------------------------------------------------------------------------------------------------- Transactions
	

//	String discard();
//	List<Object> exec();
//	String multi();
//	void unwatch();
//	void watch(String... keys);
	
	
	// ~ --------------------------------------------------------------------------------------------------- Scripting
	
	
//	void eval(String script);
//	void eval(String script, List<String> keys);
//	void eval(String script, List<String> keys, List<String> args);
//	void evalsha(String sha1);
//	void evalsha(String sha1, List<String> keys);
//	void evalsha(String sha1, List<String> keys, List<String> args);
//	void scriptexists(String sha1);
//	void scriptexists(String... sha1);
//	void scriptflush(String shardkey);
//	void scriptkill(String shardkey);
//	void scriptload(String script);
	
	
	// ~ --------------------------------------------------------------------------------------------------- Connection
	
	
//	void auth(String password);
//	void echo(String message);
//	void ping();
//	void quit();
//	void select(int index);
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Server	
	
	
//	void bgrewriteaof();
//	void bgsave();
//	void clientgetname();
//	void clientkill(String ip, int port);
//	void clientlist();
//	void clientsetname(String connectionname);
//	void configget(String parameter);
//	void configset(String parameter, String value);
//	void configresetstat();
//	void dbsize();
//	void debugobject(String key);
//	void debugsegfault();
//	void flushall();
//	void flushdb();
//	void info();
//	void info(String section);
//	void lastsave();
//	void monitor(RedisMonitorHandler handler);
//	void save();
//	void shutdown(boolean save);
//	void slaveof(String host, int port);
//	void slaveofnoone();
//	void slowlogget();
//	void slowlogget(long len);
//	void slowlogreset();
//	void slowloglen();
//	void sync();
//	void time();
//	void microtime();
	
}

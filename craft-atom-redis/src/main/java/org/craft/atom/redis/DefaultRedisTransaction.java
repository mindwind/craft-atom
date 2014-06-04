package org.craft.atom.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.ToString;

import org.craft.atom.redis.api.RedisException;
import org.craft.atom.redis.api.RedisTransaction;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.ZParams.Aggregate;

/**
 * @author mindwind
 * @version 1.0, Jun 27, 2013
 */
@ToString(of = "r")
@SuppressWarnings("unchecked")
public class DefaultRedisTransaction implements RedisTransaction {
	
	
	private Jedis        j;
	private Transaction  t;
	private DefaultRedis r;
	
	
	DefaultRedisTransaction(Jedis j, Transaction t, DefaultRedis r) {
		this.j = j;
		this.t = t;
		this.r = r;
	}
	
	
	// ~ ---------------------------------------------------------------------------------------------------------
	
	
	String discard() {
		return t.discard();
	}
		
	List<Object> exec() {
		List<Object> l = t.exec();
		if (l == null) {
			return Collections.emptyList();
		}
		
		List<Object> r = new ArrayList<Object>(l.size());
		for (Object o : l) {
			if (o instanceof Set<?>) {
				Set<?> set = (Set<?>) o;
				Map<String, Double> map = null;
				for (Object e : set) {
					if (e instanceof Tuple) {
						Tuple tuple = (Tuple) e;
						if (map == null) {
							map = new LinkedHashMap<String, Double>(set.size());
						}
						map.put(tuple.getElement(), tuple.getScore());
					} 
				}
				if (map != null) {
					r.add(map);
				} else {
					r.add(o);
				}
			} else {
				r.add(o);
			}
		}
		
		return r;
	}
	
	
	// ~ --------------------------------------------------------------------------------------------------------- Keys
	
	
	@Override
	public void del(String... keys) {
		executeCommand(CommandEnum.DEL, new Object[] { keys });
	}
	
	private void del0(String... keys) {
		t.del(keys);
	}

	@Override
	public void dump(String key) {
		executeCommand(CommandEnum.DUMP, new Object[] { key });
	}
	
	private void dump0(String key) {
		t.dump(key);
	}

	@Override
	public void exists(String key) {
		executeCommand(CommandEnum.EXISTS, new Object[] { key });
	}
	
	private void exists0(String key) {
		t.exists(key);
	}

	@Override
	public void expire(String key, int seconds) {
		executeCommand(CommandEnum.EXPIRE, new Object[] { key, seconds });
	}
	
	private void expire0(String key, int seconds) {
		t.expire(key, seconds);
	}

	@Override
	public void expireat(String key, long timestamp) {
		executeCommand(CommandEnum.EXPIREAT, new Object[] { key, timestamp });
	}
	
	private void expireat0(String key, long timestamp) {
		t.expireAt(key, timestamp);
	}

	@Override
	public void keys(String pattern) {
		executeCommand(CommandEnum.KEYS, new Object[] { pattern });
	}
	
	private void keys0(String pattern) {
		t.keys(pattern);
	}

	@Override
	public void migrate(String host, int port, String key, int destinationdb, int timeout) {
		executeCommand(CommandEnum.MIGRATE, new Object[] { host, port, key, destinationdb, timeout });
	}
	
	private void migrate0(String host, int port, String key, int destinationdb, int timeout) {
		t.migrate(host, port, key, destinationdb, timeout);
	}

	@Override
	public void move(String key, int db) {
		executeCommand(CommandEnum.MOVE, new Object[] { key, db });
	}
	
	private void move0(String key, int db) {
		t.move(key, db);
	}

	@Override
	public void objectrefcount(String key) {
		executeCommand(CommandEnum.OBJECT_REFCOUNT, new Object[] { key });
	}
	
	private void objectrefcount0(String key) {
		t.objectRefcount(key);
	}

	@Override
	public void objectencoding(String key) {
		executeCommand(CommandEnum.OBJECT_ENCODING, new Object[] { key });
	}
	
	private void objectencoding0(String key) {
		t.objectEncoding(key);
	}

	@Override
	public void objectidletime(String key) {
		executeCommand(CommandEnum.OBJECT_IDLETIME, new Object[] { key });
	}
	
	private void objectidletime0(String key) {
		t.objectIdletime(key);
	}

	@Override
	public void persist(String key) {
		executeCommand(CommandEnum.PERSIST, new Object[] { key });
	}
	
	private void persist0(String key) {
		t.persist(key);
	}

	@Override
	public void pexpire(String key, long milliseconds) {
		executeCommand(CommandEnum.PEXPIRE, new Object[] { key, milliseconds });
	}
	
	private void pexpire0(String key, long milliseconds) {
		t.pexpire(key, milliseconds);
	}

	@Override
	public void pexpireat(String key, long millisecondstimestamp) {
		executeCommand(CommandEnum.PEXPIREAT, new Object[] { key, millisecondstimestamp });
	}
	
	private void pexpireat0(String key, long millisecondstimestamp) {
		t.pexpireAt(key, millisecondstimestamp);
	}

	@Override
	public void pttl(String key) {
		executeCommand(CommandEnum.PTTL, new Object[] { key });
	}
	
	private void pttl0(String key) {
		t.pttl(key);
	}
	
	@Override
	public void randomkey() {
		executeCommand(CommandEnum.RANDOMKEY, new Object[] {});
	}
	
	private void randomkey0() {
		t.randomKey();
	}

	@Override
	public void rename(String key, String newkey) {
		executeCommand(CommandEnum.RENAME, new Object[] { key, newkey });
	}
	
	private void rename0(String key, String newkey) {
		t.rename(key, newkey);
	}

	@Override
	public void renamenx(String key, String newkey) {
		executeCommand(CommandEnum.RENAMENX, new Object[] { key, newkey });
	}
	
	private void renamenx0(String key, String newkey) {
		t.renamenx(key, newkey);
	}

	@Override
	public void restore(String key, int ttl, byte[] serializedvalue) {
		executeCommand(CommandEnum.RESTORE, new Object[] { key, ttl, serializedvalue });
	}
	
	private void restore0(String key, int ttl, byte[] serializedvalue) {
		t.restore(key, ttl, serializedvalue);
	}

	@Override
	public void sort(String key) {
		executeCommand(CommandEnum.SORT, new Object[] { key });
	}
	
	private void sort0(String key) {
		t.sort(key);
	}

	@Override
	public void sort(String key, boolean desc) {
		executeCommand(CommandEnum.SORT_DESC, new Object[] { key, desc });
	}
	
	private void sort_desc(String key, boolean desc) {
		SortingParams sp = new SortingParams();
		if (desc) {
			sp.desc();
		}
		
		t.sort(key, sp);
	}

	@Override
	public void sort(String key, boolean alpha, boolean desc) {
		executeCommand(CommandEnum.SORT_ALPHA_DESC, new Object[] { key, alpha, desc });
	}
	
	private void sort_alpha_desc(String key, boolean alpha, boolean desc) {
		SortingParams sp = new SortingParams();
		if (desc) {
			sp.desc();
		}
		if (alpha) { 
			sp.alpha() ;
		}

		t.sort(key, sp);
	}

	@Override
	public void sort(String key, int offset, int count) {
		executeCommand(CommandEnum.SORT_OFFSET_COUNT, new Object[] { key, offset, count });
	}
	
	private void sort_offset_count(String key, int offset, int count) {
		t.sort(key, new SortingParams().limit(offset, count));
	}

	@Override
	public void sort(String key, int offset, int count, boolean alpha, boolean desc) {
		executeCommand(CommandEnum.SORT_OFFSET_COUNT_ALPHA_DESC, new Object[] { key, offset, count, alpha, desc });
	}
	
	private void sort_offset_count_alpha_desc(String key, int offset, int count, boolean alpha, boolean desc) {
		SortingParams sp = new SortingParams();
		if (desc) {
			sp.desc();
		}
		if (alpha) { 
			sp.alpha() ;
		}
		sp.limit(offset, count);
		
		t.sort(key, sp);
	}

	@Override
	public void sort(String key, String bypattern, String... getpatterns) {
		executeCommand(CommandEnum.SORT_BY_GET, new Object[] { key, bypattern, getpatterns });
	}
	
	private void sort_by_get(String key, String bypattern, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		
		t.sort(key, sp);
	}

	@Override
	public void sort(String key, String bypattern, boolean desc, String... getpatterns) {
		executeCommand(CommandEnum.SORT_BY_DESC_GET, new Object[] { key, bypattern, desc, getpatterns });
	}
	
	private void sort_by_desc_get(String key, String bypattern, boolean desc, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		if (desc) {
			sp.desc();
		}
		
		t.sort(key, sp);
	}

	@Override
	public void sort(String key, String bypattern, boolean alpha, boolean desc, String... getpatterns) {
		executeCommand(CommandEnum.SORT_BY_ALPHA_DESC_GET, new Object[] { key, bypattern, alpha, desc, getpatterns });
	}
	
	private void sort_by_alpha_desc_get(String key, String bypattern, boolean alpha, boolean desc, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		if (alpha) {
			sp.alpha();
		}
		if (desc) {
			sp.desc();
		}
		
		t.sort(key, sp);
	}

	@Override
	public void sort(String key, String bypattern, int offset, int count, String... getpatterns) {
		executeCommand(CommandEnum.SORT_BY_OFFSET_COUNT_GET, new Object[] { key, bypattern, offset, count, getpatterns });
	}
	
	private void sort_by_offset_count_get(String key, String bypattern, int offset, int count, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		sp.limit(offset, count);
		t.sort(key, sp);
	}

	@Override
	public void sort(String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String... getpatterns) {
		executeCommand(CommandEnum.SORT_BY_OFFSET_COUNT_ALPHA_DESC_GET, new Object[] { key, bypattern, offset, count, alpha, desc, getpatterns });
	}
	
	private void sort_by_offset_count_alpha_desc_get(String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String... getpatterns) {
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
		
		t.sort(key, sp);
	}

	@Override
	public void sort(String key, String destination) {
		executeCommand(CommandEnum.SORT_DESTINATION, key, destination);
	}
	
	private void sort_destination(String key, String destination) {
		t.sort(key, destination);
	}

	@Override
	public void sort(String key, boolean desc, String destination) {
		executeCommand(CommandEnum.SORT_DESC_DESTINATION, key, desc, destination);
	}
	
	private void sort_desc_destination(String key, boolean desc, String destination) {
		SortingParams sp = new SortingParams();
		if (desc) {
			sp.desc();
		}
		t.sort(key, sp, destination);
	}

	@Override
	public void sort(String key, boolean alpha, boolean desc, String destination) {
		executeCommand(CommandEnum.SORT_ALPHA_DESC_DESTINATION, key, alpha, desc, destination);
	}
	
	private void sort_alpha_desc_destination(String key, boolean alpha, boolean desc, String destination) {
		SortingParams sp = new SortingParams();
		if (desc) {
			sp.desc();
		}
		if (alpha) {
			sp.alpha();
		}
		
		t.sort(key, sp, destination);
	}

	@Override
	public void sort(String key, int offset, int count, String destination) {
		executeCommand(CommandEnum.SORT_OFFSET_COUNT_DESTINATION, key, offset, count, destination);
	}
	
	private void sort_offset_count_destination(String key, int offset, int count, String destination) {
		SortingParams sp = new SortingParams();
		sp.limit(offset, count);
		t.sort(key, sp, destination);
	}

	@Override
	public void sort(String key, int offset, int count, boolean alpha, boolean desc, String destination) {
		executeCommand(CommandEnum.SORT_OFFSET_COUNT_ALPHA_DESC_DESTINATION, key, offset, count, alpha, desc, destination);
	}
	
	private void sort_offset_count_alpha_desc_destination(String key, int offset, int count, boolean alpha, boolean desc, String destination) {
		SortingParams sp = new SortingParams();
		sp.limit(offset, count);
		if (desc) {
			sp.desc();
		}
		if (alpha) {
			sp.alpha();
		}
		t.sort(key, sp, destination);
	}

	@Override
	public void sort(String key, String bypattern, String destination, String... getpatterns) {
		executeCommand(CommandEnum.SORT_BY_DESTINATION_GET, new Object[] { key, bypattern, destination, getpatterns });
	}
	
	private void sort_by_destination_get(String key, String bypattern, String destination, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		
		t.sort(key, sp, destination);
	}

	@Override
	public void sort(String key, String bypattern, boolean desc, String destination, String... getpatterns) {
		executeCommand(CommandEnum.SORT_BY_DESC_DESTINATION_GET, new Object[] { key, bypattern, desc, destination, getpatterns });
	}
	
	private void sort_by_desc_destination_get(String key, String bypattern, boolean desc, String destination, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		if (desc) {
			sp.desc();
		}
		
		t.sort(key, sp, destination);
	}

	@Override
	public void sort(String key, String bypattern, boolean alpha, boolean desc, String destination, String... getpatterns) {
		executeCommand(CommandEnum.SORT_BY_ALPHA_DESC_DESTINATION_GET, new Object[] { key, bypattern, alpha, desc, destination, getpatterns });
	}
	
	private void sort_by_alpha_desc_destination_get(String key, String bypattern, boolean alpha, boolean desc, String destination, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		if (alpha) {
			sp.alpha();
		}
		if (desc) {
			sp.desc();
		}
		
		t.sort(key, sp, destination);
	}

	@Override
	public void sort(String key, String bypattern, int offset, int count, String destination, String... getpatterns) {
		executeCommand(CommandEnum.SORT_BY_OFFSET_COUNT_DESTINATION_GET, new Object[] { key, bypattern, offset, count, destination, getpatterns });
	}
	
	private void sort_by_offset_count_destination_get(String key, String bypattern, int offset, int count, String destination, String... getpatterns) {
		SortingParams sp = new SortingParams();
		sp.by(bypattern);
		sp.get(getpatterns);
		sp.limit(offset, count);
		
		t.sort(key, sp, destination);
	}

	@Override
	public void sort(String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String destination, String... getpatterns) {
		executeCommand(CommandEnum.SORT_BY_OFFSET_COUNT_ALPHA_DESC_DESTINATION_GET, new Object[] { key, bypattern, offset, count, alpha, desc, destination, getpatterns });
	}
	
	private void sort_by_offset_count_alpha_desc_destination_get(String key, String bypattern, int offset, int count, boolean alpha, boolean desc, String destination, String... getpatterns) {
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
		
		t.sort(key, sp, destination);
	}

	@Override
	public void ttl(String key) {
		executeCommand(CommandEnum.TTL, new Object[] { key });
	}
	
	private void ttl0(String key) {
		t.ttl(key);
	}

	@Override
	public void type(String key) {
		executeCommand(CommandEnum.TYPE, new Object[] { key });
	}
	
	private void type0(String key) {
		t.type(key);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Strings
	

	@Override
	public void append(String key, String value) {
		executeCommand(CommandEnum.APPEND, key, value);
	}
	
	private void append0(String key, String value) {
		t.append(key, value);
	}

	@Override
	public void bitcount(String key) {
		executeCommand(CommandEnum.BITCOUNT, key);
	}
	
	private void bitcount0(String key) {
		t.bitcount(key);
	}

	@Override
	public void bitcount(String key, long start, long end) {
		executeCommand(CommandEnum.BITCOUNT_START_END, key, start, end);
	}

	public void bitcount0(String key, long start, long end) {
		t.bitcount(key, start, end);
	}

	@Override
	public void bitnot(String destkey, String key) {
		executeCommand(CommandEnum.BITNOT, destkey, key);
	}
	
	private void bitnot0(String destkey, String key) {
		t.bitop(BitOP.NOT, destkey, key);
	}
	
	@Override
	public void bitand(String destkey, String... keys) {
		executeCommand(CommandEnum.BITAND, destkey, keys);
	}
	
	private void bitand0(String destkey, String... keys) {
		t.bitop(BitOP.AND, destkey, keys);
	}

	@Override
	public void bitor(String destkey, String... keys) {
		executeCommand(CommandEnum.BITOR, destkey, keys);
	}
	
	private void bitor0(String destkey, String... keys) {
		t.bitop(BitOP.OR, destkey, keys);
	}

	@Override
	public void bitxor(String destkey, String... keys) {
		executeCommand(CommandEnum.BITXOR, destkey, keys);
	}
	
	private void bitxor0(String destkey, String... keys) {
		t.bitop(BitOP.XOR, destkey, keys);
	}

	@Override
	public void decr(String key) {
		executeCommand(CommandEnum.DECR, key);
	}
	
	private void decr0(String key) {
		t.decr(key);
	}

	@Override
	public void decrby(String key, long decrement) {
		executeCommand(CommandEnum.DECRBY, key, decrement);
	}
	
	private void decrby0(String key, long decrement) {
		t.decrBy(key, decrement);
	}

	@Override
	public void get(String key) {
		executeCommand(CommandEnum.GET, key);
	}
	
	private void get0(String key) {
		t.get(key);
	}

	@Override
	public void getbit(String key, long offset) {
		executeCommand(CommandEnum.GETBIT, key, offset);
	}
	
	private void getbit0(String key, long offset) {
		t.getbit(key, offset);
	}

	@Override
	public void getrange(String key, long start, long end) {
		executeCommand(CommandEnum.GETRANGE, key, start, end);
	}
	
	private void getrange0(String key, long start, long end) {
		t.getrange(key, start, end);
	}

	@Override
	public void getset(String key, String value) {
		executeCommand(CommandEnum.GETSET, key, value);
	}
	
	private void getset0(String key, String value) {
		t.getSet(key, value);
	}

	@Override
	public void incr(String key) {
		executeCommand(CommandEnum.INCR, key);
	}
	
	private void incr0(String key) {
		t.incr(key);
	}

	@Override
	public void incrby(String key, long increment) {
		executeCommand(CommandEnum.INCRBY, key, increment);
	}
	
	private void incrby0(String key, long increment) {
		t.incrBy(key, increment);
	}

	@Override
	public void incrbyfloat(String key, double increment) {
		executeCommand(CommandEnum.INCRBYFLOAT, key, increment);
	}
	
	private void incrbyfloat0(String key, double increment) {
		t.incrByFloat(key, increment);
	}
	
	@Override
	public void mget(String... keys) {
		executeCommand(CommandEnum.MGET, new Object[] { keys });
	}
	
	private void mget0(String... keys) {
		t.mget(keys);
	}

	@Override
	public void mset(String... keysvalues) {
		executeCommand(CommandEnum.MSET, new Object[] { keysvalues });
	}
	
	private void mset0(String... keysvalues) {
		t.mset(keysvalues);
	}

	@Override
	public void msetnx(String... keysvalues) {
		executeCommand(CommandEnum.MSETNX, new Object[] { keysvalues });
	}
	
	private void msetnx0(String... keysvalues) {
		t.msetnx(keysvalues);
	}


	@Override
	public void psetex(String key, int milliseconds, String value) {
		executeCommand(CommandEnum.PSETEX, key, milliseconds, value);
	}
	
	private void psetex0(String key, int milliseconds, String value) {
		t.psetex(key, milliseconds, value);
	}

	@Override
	public void set(String key, String value) {
		executeCommand(CommandEnum.SET, key, value);
	}
	
	private void set0(String key, String value) {
		t.set(key, value);
	}

	@Override
	public void setxx(String key, String value) {
		executeCommand(CommandEnum.SETXX, key, value);
	}
	
	private void setxx0(String key, String value) {
		t.set(key, value, "XX");
	}

	@Override
	public void setnxex(String key, String value, int seconds) {
		executeCommand(CommandEnum.SETNXEX, key, value, seconds);
	}
	
	private void setnxex0(String key, String value, int seconds) {
		t.set(key, value, "NX", "EX", seconds);
	}

	@Override
	public void setnxpx(String key, String value, int milliseconds) {
		executeCommand(CommandEnum.SETNXPX, key, value, milliseconds);
	}
	
	private void setnxpx0(String key, String value, int milliseconds) {
		t.set(key, value, "NX", "PX", milliseconds);
	}

	@Override
	public void setxxex(String key, String value, int seconds) {
		executeCommand(CommandEnum.SETXXEX, key, value, seconds);
	}
	
	private void setxxex0(String key, String value, int seconds) {
		t.set(key, value, "XX", "EX", seconds);
	}

	@Override
	public void setxxpx(String key, String value, int milliseconds) {
		executeCommand(CommandEnum.SETXXPX, key, value, milliseconds);
	}
	
	private void setxxpx0(String key, String value, int milliseconds) {
		t.set(key, value, "XX", "PX", milliseconds);
	}
	
	@Override
	public void setbit(String key, long offset, boolean value) {
		executeCommand(CommandEnum.SETBIT, key, offset, value);
	}
	
	private void setbit0(String key, long offset, boolean value) {
		t.setbit(key, offset, value);
	}

	@Override
	public void setex(String key, int seconds, String value) {
		executeCommand(CommandEnum.SETEX, key, seconds, value);
	}
	
	private void setex0(String key, int seconds, String value) {
		t.setex(key, seconds, value);
	}

	@Override
	public void setnx(String key, String value) {
		executeCommand(CommandEnum.SETNX, key, value);
	}
	
	private void setnx0(String key, String value) {
		t.setnx(key, value);
	}

	@Override
	public void setrange(String key, long offset, String value) {
		executeCommand(CommandEnum.SETRANGE, key, offset, value);
	}
	
	private void setrange0(String key, long offset, String value) {
		t.setrange(key, offset, value);
	}

	@Override
	public void strlen(String key) {
		executeCommand(CommandEnum.STRLEN, key);
	}
	
	private void strlen0(String key) {
		t.strlen(key);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Hashes
	
	

	@Override
	public void hdel(String key, String... fields) {
		executeCommand(CommandEnum.HDEL, key, fields);
	}
	
	private void hdel0(String key, String... fields) {
		t.hdel(key, fields);
	}

	@Override
	public void hexists(String key, String field) {
		executeCommand(CommandEnum.HEXISTS, key, field);
	}
	
	private void hexists0(String key, String field) {
		t.hexists(key, field);
	}

	@Override
	public void hget(String key, String field) {
		executeCommand(CommandEnum.HGET, key, field);
	}
	
	private void hget0(String key, String field) {
		t.hget(key, field);
	}

	@Override
	public void hgetall(String key) {
		executeCommand(CommandEnum.HGETALL, key);
	}
	
	private void hgetall0(String key) {
		t.hgetAll(key);
	}

	@Override
	public void hincrby(String key, String field, long increment) {
		executeCommand(CommandEnum.HINCRBY, key, field, increment);
	}
	
	private void hincrby0(String key, String field, long increment) {
		t.hincrBy(key, field, increment);
 	}

	@Override
	public void hincrbyfloat(String key, String field, double increment) {
		executeCommand(CommandEnum.HINCRBYFLOAT, key, field, increment);
	}
	
	private void hincrbyfloat0(String key, String field, double increment) {
		t.hincrByFloat(key, field, increment);
	}

	@Override
	public void hkeys(String key) {
		executeCommand(CommandEnum.HKEYS, key);
	}
	
	private void hkeys0(String key) {
		t.hkeys(key);
	}

	@Override
	public void hlen(String key) {
		executeCommand(CommandEnum.HLEN, key);
	}
	
	private void hlen0(String key) {
		t.hlen(key);
	}

	@Override
	public void hmget(String key, String... fields) {
		executeCommand(CommandEnum.HMGET, key, fields);
	}
	
	private void hmget0(String key, String... fields) {
		t.hmget(key, fields);
	}

	@Override
	public void hmset(String key, Map<String, String> fieldvalues) {
		executeCommand(CommandEnum.HMSET, key, fieldvalues);
	}
	
	private void hmset0(String key, Map<String, String> fieldvalues) {
		t.hmset(key, fieldvalues);
	}

	@Override
	public void hset(String key, String field, String value) {
		executeCommand(CommandEnum.HSET, key, field, value);
	}
	
	private void hset0(String key, String field, String value) {
		t.hset(key, field, value);
	}

	@Override
	public void hsetnx(String key, String field, String value) {
		executeCommand(CommandEnum.HSETNX, key, field, value);
	}
	
	private void hsetnx0(String key, String field, String value) {
		t.hsetnx(key, field, value);
	}

	@Override
	public void hvals(String key) {
		executeCommand(CommandEnum.HVALS, key);
	}
	
	private void hvals0(String key) {
		t.hvals(key);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Lists
	
	
	@Override
	public void blpop(String... keys) {
		blpop(0, keys);
 	}
	
	@Override
	public void blpop(int timeout, String... keys) {
		executeCommand(CommandEnum.BLPOP, timeout, keys);
	}
	
	private void blpop0(int timeout, String... keys) {
		t.blpopMap(timeout, keys);
	}
	
	@Override
	public void brpop(String... keys) {
		brpop(0, keys);
	}

	@Override
	public void brpop(int timeout, String... keys) {
		executeCommand(CommandEnum.BRPOP, timeout, keys);
	}
	
	private void brpop0(int timeout, String... keys) {
		t.brpopMap(timeout, keys);
	}

	@Override
	public void brpoplpush(String source, String destination, int timeout) {
		executeCommand(CommandEnum.BRPOPLPUSH, source, destination, timeout);
	}
	
	private void brpoplpush0(String source, String destination, int timeout) {
		t.brpoplpush(source, destination, timeout);
	}

	@Override
	public void lindex(String key, long index) {
		executeCommand(CommandEnum.LINDEX, key, index);
	}
	
	private void lindex0(String key, long index) {
		t.lindex(key, index);
	}

	@Override
	public void linsertbefore(String key, String pivot, String value) {
		executeCommand(CommandEnum.LINSERT_BEFORE, key, pivot, value);
	}
	
	private void linsertbefore0(String key, String pivot, String value) {
		t.linsert(key, LIST_POSITION.BEFORE, pivot, value);
	}

	@Override
	public void linsertafter(String key, String pivot, String value) {
		executeCommand(CommandEnum.LINSERT_AFTER, key, pivot, value);
	}
	
	private void linsertafter0(String key, String pivot, String value) {
		t.linsert(key, LIST_POSITION.AFTER, pivot, value);
	}

	@Override
	public void llen(String key) {
		executeCommand(CommandEnum.LLEN, key);
	}
	
	private void llen0(String key) {
		t.llen(key);
	}

	@Override
	public void lpop(String key) {
		executeCommand(CommandEnum.LPOP, key);
	}
	
	private void lpop0(String key) {
		t.lpop(key);
	}

	@Override
	public void lpush(String key, String... values) {
		executeCommand(CommandEnum.LPUSH, key, values);
	}
	
	private void lpush0(String key, String... values) {
		t.lpush(key, values);
	}

	@Override
	public void lpushx(String key, String value) {
		executeCommand(CommandEnum.LPUSHX, key, value);
	}
	
	private void lpushx0(String key, String value) {
		t.lpushx(key, value);
	}

	@Override
	public void lrange(String key, long start, long stop) {
		executeCommand(CommandEnum.LRANGE, key, start, stop);
	}
	
	private void lrange0(String key, long start, long stop) {
		t.lrange(key, start, stop);
	}

	@Override
	public void lrem(String key, long count, String value) {
		executeCommand(CommandEnum.LREM, key, count, value);
	}
	
	private void lrem0(String key, long count, String value) {
		t.lrem(key, count, value);
	}

	@Override
	public void lset(String key, long index, String value) {
		executeCommand(CommandEnum.LSET, key, index, value);
	}
	
	private void lset0(String key, long index, String value) {
		t.lset(key, index, value);
	}

	@Override
	public void ltrim(String key, long start, long stop) {
		executeCommand(CommandEnum.LTRIM, key, start, stop);
	}
	
	private void ltrim0(String key, long start, long stop) {
		t.ltrim(key, start, stop);
	}

	@Override
	public void rpop(String key) {
		executeCommand(CommandEnum.RPOP, key);
	}
	
	private void rpop0(String key) {
		t.rpop(key);
	}

	@Override
	public void rpoplpush(String source, String destination) {
		executeCommand(CommandEnum.RPOPLPUSH, source, destination);
	}
	
	private void rpoplpush0(String source, String destination) {
		t.rpoplpush(source, destination);
	}

	@Override
	public void rpush(String key, String... values) {
		executeCommand(CommandEnum.RPUSH, key, values);
	}
	
	private void rpush0(String key, String... values) {
		t.rpush(key, values);
	}

	@Override
	public void rpushx(String key, String value) {
		executeCommand(CommandEnum.RPUSHX, key, value);
	}
	
	private void rpushx0(String key, String value) {
		t.rpushx(key, value);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Sets
	

	@Override
	public void sadd(String key, String... members) {
		executeCommand(CommandEnum.SADD, new Object[] { key, members });
	}
	
	private void sadd0(String key, String... members) {
		t.sadd(key, members);
	}

	@Override
	public void scard(String key) {
		executeCommand(CommandEnum.SCARD, key);
	}
	
	private void scard0(String key) {
		t.scard(key);
	}
	
	@Override
	public void sdiff(String... keys) {
		executeCommand(CommandEnum.SDIFF, new Object[] { keys });
	}
	
	private void sdiff0(String... keys) {
		t.sdiff(keys);
	}

	@Override
	public void sdiffstore(String destination, String... keys) {
		executeCommand(CommandEnum.SDIFFSTORE, destination, keys);
	}
	
	private void sdiffstore0(String destination, String... keys) {
		t.sdiffstore(destination, keys);
	}

	@Override
	public void sinter(String... keys) {
		executeCommand(CommandEnum.SINTER, new Object[] { keys });
	}
	
	private void sinter0(String... keys) {
		t.sinter(keys);
	}

	@Override
	public void sinterstore(String destination, String... keys) {
		executeCommand(CommandEnum.SINTERSTORE, destination, keys);
	}
	
	private void sinterstore0(String destination, String... keys) {
		t.sinterstore(destination, keys);
	}

	@Override
	public void sismember(String key, String member) {
		executeCommand(CommandEnum.SISMEMBER, key, member);
	}
	
	private void sismember0(String key, String member) {
		t.sismember(key, member);
	}

	@Override
	public void smembers(String key) {
		executeCommand(CommandEnum.SMEMBERS, key);
	}
	
	private void smembers0(String key) {
		t.smembers(key);
	}
	
	@Override
	public void smove(String source, String destination, String member) {
		executeCommand(CommandEnum.SMOVE, source, destination, member);
	}
	
	private void smove0(String source, String destination, String member) {
		t.smove(source, destination, member);
	}

	@Override
	public void spop(String key) {
		executeCommand(CommandEnum.SPOP, key);
	}
	
	private void spop0(String key) {
		t.spop(key);
	}
	
	@Override
	public void srandmember(String key) {
		executeCommand(CommandEnum.SRANDMEMBER, key);
	}
	
	private void srandmember0(String key) {
		t.srandmember(key);
	}

	@Override
	public void srandmember(String key, int count) {
		executeCommand(CommandEnum.SRANDMEMBER_COUNT, key, count);
	}
	
	private void srandmember0(String key, int count) {
		t.srandmember(key, count);
	}

	@Override
	public void srem(String key, String... members) {
		executeCommand(CommandEnum.SREM, key, members);
	}
	
	private void srem0(String key, String... members) {
		t.srem(key, members);
	}
	
	@Override
	public void sunion(String... keys) {
		executeCommand(CommandEnum.SUNION, new Object[] { keys });
	}
	
	private void sunion0(String... keys) {
		t.sunion(keys);
	}

	@Override
	public void sunionstore(String destination, String... keys) {
		executeCommand(CommandEnum.SUNIONSTORE, destination, keys);
	}
	
	private void sunionstore0(String destination, String... keys) {
		t.sunionstore(destination, keys);
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------- Sorted Sets
	
	

	@Override
	public void zadd(String key, double score, String member) {
		Map<String, Double> scoremembers = new HashMap<String, Double>();
		scoremembers.put(member, score);
		zadd(key, scoremembers);
	}
	
	@Override
	public void zadd(String key, Map<String, Double> scoremembers) {
		executeCommand(CommandEnum.ZADD, key, scoremembers);
	}

	private void zadd0(String key, Map<String, Double> scoremembers) {
		t.zadd(key, scoremembers);
	}

	@Override
	public void zcard(String key) {
		executeCommand(CommandEnum.ZCARD, key);
	}
	
	private void zcard0(String key) {
		t.zcard(key);
	}

	@Override
	public void zcount(String key, double min, double max) {
		executeCommand(CommandEnum.ZCOUNT, key, min, max);
	}
	
	private void zcount0(String key, double min, double max) {
		t.zcount(key, min, max);
	}

	@Override
	public void zcount(String key, String min, String max) {
		executeCommand(CommandEnum.ZCOUNT_STRING, key, min, max);
	}
	
	private void zcount0(String key, String min, String max) {
		t.zcount(key, min, max);
	}

	@Override
	public void zincrby(String key, double score, String member) {
		executeCommand(CommandEnum.ZINCRBY, key, score, member);
	}
	
	private void zincrby0(String key, double score, String member) {
		t.zincrby(key, score, member);
	}
	
	@Override
	public void zinterstore(String destination, String... keys) {
		executeCommand(CommandEnum.ZINTERSTORE, destination, keys);
	}
	
	private void zinterstore0(String destination, String... keys) {
		t.zinterstore(destination, keys);
	}

	@Override
	public void zinterstoremax(String destination, String... keys) {
		executeCommand(CommandEnum.ZINTERSTORE_MAX, destination, keys);
	}
	
	private void zinterstoremax0(String destination, String... keys) {
		t.zinterstore(destination, new ZParams().aggregate(Aggregate.MAX), keys);
	}

	@Override
	public void zinterstoremin(String destination, String... keys) {
		executeCommand(CommandEnum.ZINTERSTORE_MIN, destination, keys);
	}
	
	private void zinterstoremin0(String destination, String... keys) {
		t.zinterstore(destination, new ZParams().aggregate(Aggregate.MIN), keys);
	}

	@Override
	public void zinterstore(String destination, Map<String, Integer> weightkeys) {
		executeCommand(CommandEnum.ZINTERSTORE_WEIGHTS, destination, weightkeys);
	}
	
	private void zinterstore_weights(String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
		t.zinterstore(destination, new ZParams().weights(weights), keys);
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
	public void zinterstoremax(String destination, Map<String, Integer> weightkeys) {
		executeCommand(CommandEnum.ZINTERSTORE_WEIGHTS_MAX, destination, weightkeys);
	}
	
	private void zinterstore_weights_max(String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
		t.zinterstore(destination, new ZParams().weights(weights).aggregate(Aggregate.MAX), keys);
	}

	@Override
	public void zinterstoremin(String destination, Map<String, Integer> weightkeys) {
		executeCommand(CommandEnum.ZINTERSTORE_WEIGHTS_MIN, destination, weightkeys);
	}
	
	private void zinterstore_weights_min(String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
		t.zinterstore(destination, new ZParams().weights(weights).aggregate(Aggregate.MIN), keys);
	}

	@Override
	public void zrange(String key, long start, long stop) {
		executeCommand(CommandEnum.ZRANGE, key, start, stop);
	}
	
	private void zrange0(String key, long start, long stop) {
		t.zrange(key, start, stop); 
	}

	@Override
	public void zrangewithscores(String key, long start, long stop) {
		executeCommand(CommandEnum.ZRANGE_WITHSCORES, key, start, stop);
	}
	
	private void zrangewithscores0(String key, long start, long stop) {
		t.zrangeWithScores(key, start, stop);
	}

	@Override
	public void zrangebyscore(String key, double min, double max) {
		executeCommand(CommandEnum.ZRANGEBYSCORE, key, min, max);
	}
	
	private void zrangebyscore0(String key, double min, double max) {
		t.zrangeByScore(key, min, max);
	}

	@Override
	public void zrangebyscore(String key, String min, String max) {
		executeCommand(CommandEnum.ZRANGEBYSCORE_STRING, key, min, max);
	}
	
	private void zrangebyscore_string(String key, String min, String max) {
		t.zrangeByScore(key, min, max);
	}

	@Override
	public void zrangebyscore(String key, double min, double max, int offset, int count) {
		executeCommand(CommandEnum.ZRANGEBYSCORE_OFFSET_COUNT, key, min, max, offset, count);
	}
	
	private void zrangebyscore_offset_count(String key, double min, double max, int offset, int count) {
		t.zrangeByScore(key, min, max, offset, count);
	}

	@Override
	public void zrangebyscore(String key, String min, String max, int offset, int count) {
		executeCommand(CommandEnum.ZRANGEBYSCORE_OFFSET_COUNT_STRING, key, min, max, offset, count);
	}
	
	private void zrangebyscore_offset_count_string(String key, String min, String max, int offset, int count) {
		t.zrangeByScore(key, min, max, offset, count);
	}

	@Override
	public void zrangebyscorewithscores(String key, double min, double max) {
		executeCommand(CommandEnum.ZRANGEBYSCORE_WITHSCORES, key, min, max);
	}
	
	private void zrangebyscorewithscores0(String key, double min, double max) {
		t.zrangeByScoreWithScores(key, min, max);
	}

	@Override
	public void zrangebyscorewithscores(String key, String min, String max) {
		executeCommand(CommandEnum.ZRANGEBYSCORE_WITHSCORES_STRING, key, min, max);
	}
	
	private void zrangebyscorewithscores_string(String key, String min, String max) {
		t.zrangeByScoreWithScores(key, min, max);
	}

	@Override
	public void zrangebyscorewithscores(String key, double min, double max, int offset, int count) {
		executeCommand(CommandEnum.ZRANGEBYSCORE_WITHSCORES_OFFSET_COUNT, key, min, max, offset, count);
	}
	
	private void zrangebyscorewithscores_offset_count(String key, double min, double max, int offset, int count) {
		t.zrangeByScoreWithScores(key, min, max, offset, count);
	}

	@Override
	public void zrangebyscorewithscores(String key, String min, String max, int offset, int count) {
		executeCommand(CommandEnum.ZRANGEBYSCORE_WITHSCORES_OFFSET_COUNT_STRING, key, min, max, offset, count);
	}
	
	private void zrangebyscorewithscores_offset_count_string(String key, String min, String max, int offset, int count) {
		t.zrangeByScoreWithScores(key, min, max, offset, count);
	}

	@Override
	public void zrank(String key, String member) {
		executeCommand(CommandEnum.ZRANK, key, member);
	}
	
	private void zrank0(String key, String member) {
		t.zrank(key, member);
	}

	@Override
	public void zrem(String key, String... members) {
		executeCommand(CommandEnum.ZREM, key, members);
	}
	
	private void zrem0(String key, String... members) {
		t.zrem(key, members);
	}

	@Override
	public void zremrangebyrank(String key, long start, long stop) {
		executeCommand(CommandEnum.ZREMRANGEBYRANK, key, start, stop);
	}
	
	private void zremrangebyrank0(String key, long start, long stop) {
		t.zremrangeByRank(key, start, stop);
	}

	@Override
	public void zremrangebyscore(String key, double min, double max) {
		executeCommand(CommandEnum.ZREMRANGEBYSCORE, key, min, max);
	}
	
	private void zremrangebyscore0(String key, double min, double max) {
		t.zremrangeByScore(key, min, max);
	}

	@Override
	public void zremrangebyscore(String key, String min, String max) {
		executeCommand(CommandEnum.ZREMRANGEBYSCORE_STRING, key, min, max);
	}
	
	private void zremrangebyscore_string(String key, String min, String max) {
		t.zremrangeByScore(key, min, max);
	}

	@Override
	public void zrevrange(String key, long start, long stop) {
		executeCommand(CommandEnum.ZREVRANGE, key, start, stop);
	}
	
	private void zrevrange0(String key, long start, long stop) {
		t.zrevrange(key, start, stop); 
	}

	@Override
	public void zrevrangewithscores(String key, long start, long stop) {
		executeCommand(CommandEnum.ZREVRANGE_WITHSCORES, key, start, stop);
	}
	
	private void zrevrangewithscores0(String key, long start, long stop) {
		t.zrevrangeWithScores(key, start, stop);
	}

	@Override
	public void zrevrangebyscore(String key, double max, double min) {
		executeCommand(CommandEnum.ZREVRANGEBYSCORE, key, max, min);
	}
	
	private void zrevrangebyscore0(String key, double max, double min) {
		t.zrevrangeByScore(key, max, min);
	}

	@Override
	public void zrevrangebyscore(String key, String max, String min) {
		executeCommand(CommandEnum.ZREVRANGEBYSCORE_STRING, key, max, min);
	}
	
	private void zrevrangebyscore_string(String key, String max, String min) {
		t.zrevrangeByScore(key, max, min);
	}

	@Override
	public void zrevrangebyscore(String key, double max, double min, int offset, int count) {
		executeCommand(CommandEnum.ZREVRANGEBYSCORE_OFFSET_COUNT, key, max, min, offset, count);
	}
	
	private void zrevrangebyscore_offset_count(String key, double max, double min, int offset, int count) {
		t.zrevrangeByScore(key, max, min, offset, count);
	}

	@Override
	public void zrevrangebyscore(String key, String max, String min, int offset, int count) {
		executeCommand(CommandEnum.ZREVRANGEBYSCORE_OFFSET_COUNT_STRING, key, max, min, offset, count);
	}
	
	private void zrevrangebyscore_offset_count_string(String key, String max, String min, int offset, int count) {
		t.zrevrangeByScore(key, max, min, offset, count);
	}
	
	@Override
	public void zrevrangebyscorewithscores(String key, double max, double min) {
		executeCommand(CommandEnum.ZREVRANGEBYSCORE_WITHSCORES, key, max, min);
	}
	
	private void zrevrangebyscorewithscores0(String key, double max, double min) {
		t.zrevrangeByScoreWithScores(key, max, min);
	}

	@Override
	public void zrevrangebyscorewithscores(String key, String max, String min) {
		executeCommand(CommandEnum.ZREVRANGEBYSCORE_WITHSCORES_STRING, key, max, min);
	}
	
	private void zrevrangebyscorewithscores_string(String key, String max, String min) {
		t.zrevrangeByScoreWithScores(key, max, min);
	}

	@Override
	public void zrevrangebyscorewithscores(String key, double max, double min, int offset, int count) {
		executeCommand(CommandEnum.ZREVRANGEBYSCORE_WITHSCORES_OFFSET_COUNT, key, max, min, offset, count);
	}
	
	private void zrevrangebyscorewithscores_offset_count(String key, double max, double min, int offset, int count) {
		t.zrevrangeByScoreWithScores(key, max, min, offset, count);
	}

	@Override
	public void zrevrangebyscorewithscores(String key, String max, String min, int offset, int count) {
		executeCommand(CommandEnum.ZREVRANGEBYSCORE_WITHSCORES_OFFSET_COUNT_STRING, key, max, min, offset, count);
	}
	
	private void zrevrangebyscorewithscores_offset_count_string(String key, String max, String min, int offset, int count) {
		t.zrevrangeByScoreWithScores(key, max, min, offset, count);
	}

	@Override
	public void zrevrank(String key, String member) {
		executeCommand(CommandEnum.ZREVRANK, key, member);
	}
	
	private void zrevrank0(String key, String member) {
		t.zrevrank(key, member);
	}

	@Override
	public void zscore(String key, String member) {
		executeCommand(CommandEnum.ZSCORE, key, member);
	}
	
	private void zscore0(String key, String member) {
		t.zscore(key, member);
	}
	
	@Override
	public void zunionstore(String destination, String... keys) {
		executeCommand(CommandEnum.ZUNIONSTORE, destination, keys);
	}
	
	private void zunionstore0(String destination, String... keys) {
		t.zunionstore(destination, keys);
	}

	@Override
	public void zunionstoremax(String destination, String... keys) {
		executeCommand(CommandEnum.ZUNIONSTORE_MAX, destination, keys);
	}
	
	private void zunionstoremax0(String destination, String... keys) {
		t.zunionstore(destination, new ZParams().aggregate(Aggregate.MAX), keys);
	}

	@Override
	public void zunionstoremin(String destination, String... keys) {
		executeCommand(CommandEnum.ZUNIONSTORE_MIN, destination, keys);
	}
	
	private void zunionstoremin0(String destination, String... keys) {
		t.zunionstore(destination, new ZParams().aggregate(Aggregate.MIN), keys);
	}

	@Override
	public void zunionstore(String destination, Map<String, Integer> weightkeys) {
		executeCommand(CommandEnum.ZUNIONSTORE_WEIGHTS, destination, weightkeys);
	}
	
	private void zunionstore_weights(String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
		t.zunionstore(destination, new ZParams().weights(weights), keys);
	}

	@Override
	public void zunionstoremax(String destination, Map<String, Integer> weightkeys) {
		executeCommand(CommandEnum.ZUNIONSTORE_WEIGHTS_MAX, destination, weightkeys);
	}
	
	private void zunionstore_weights_max(String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
		t.zunionstore(destination, new ZParams().weights(weights).aggregate(Aggregate.MAX), keys);
	}

	@Override
	public void zunionstoremin(String destination, Map<String, Integer> weightkeys) {
		executeCommand(CommandEnum.ZUNIONSTORE_WEIGHTS_MIN, destination, weightkeys);
	}
	
	private void zunionstore_weights_min(String destination, Map<String, Integer> weightkeys) {
		Object[] objs = convert4zstore(weightkeys);
		String[] keys = (String[]) objs[0];
		int [] weights = (int[]) objs[1];
		t.zunionstore(destination, new ZParams().weights(weights).aggregate(Aggregate.MIN), keys);
	}	
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Pub/Sub
	
	
	@Override
	public void publish(String channel, String message) {
		executeCommand(CommandEnum.PUBLISH, channel, message);
	}
	
	private void publish0(String channel, String message) {
		t.publish(channel, message);
	}

	
	// ~ -------------------------------------------------------------------------------------------------------------

	
	private void executeCommand(CommandEnum cmd, Object... args) {
		try {
			switch (cmd) {
			// Keys
			case DEL:
				del0((String[]) args[0]); break;
			case DUMP:         
				dump0((String) args[0]); break;  
			case EXISTS:
				exists0((String) args[0]); break;
			case EXPIRE:
				expire0((String) args[0], (Integer) args[1]); break;
			case EXPIREAT:
				expireat0((String) args[0], (Long) args[1]); break;
			case KEYS:
				keys0((String) args[0]); break;
			case MIGRATE: 
				migrate0((String) args[0], (Integer) args[1], (String) args[2], (Integer) args[3], (Integer) args[4]); break;
			case MOVE:
				move0((String) args[0], (Integer) args[1]); break;
			case OBJECT_REFCOUNT:
				objectrefcount0((String) args[0]); break;
			case OBJECT_ENCODING:
				objectencoding0((String) args[0]); break;
			case OBJECT_IDLETIME:
				objectidletime0((String) args[0]); break;
			case PERSIST:
				persist0((String) args[0]); break;
			case PEXPIRE: 
				pexpire0((String) args[0], (Long) args[1]); break;
			case PEXPIREAT: 
				pexpireat0((String) args[0], (Long) args[1]); break;
			case PTTL: 
				pttl0((String) args[0]); break;
			case RANDOMKEY:
				randomkey0(); break;
			case RENAME:
				rename0((String) args[0], (String) args[1]); break;
			case RENAMENX:
				renamenx0((String) args[0], (String) args[1]); break;
			case RESTORE:
				restore0((String) args[0], (Integer) args[1], (byte[]) args[2]); break;
			case SORT:
				sort0((String) args[0]); break;
			case SORT_DESC:
				sort_desc((String) args[0], (Boolean) args[1]); break;
			case SORT_ALPHA_DESC:
				sort_alpha_desc((String) args[0], (Boolean) args[1], (Boolean) args[2]); break;
			case SORT_OFFSET_COUNT:
				sort_offset_count((String) args[0], (Integer) args[1], (Integer) args[2]); break;
			case SORT_OFFSET_COUNT_ALPHA_DESC:
				sort_offset_count_alpha_desc((String) args[0], (Integer) args[1], (Integer) args[2], (Boolean) args[3], (Boolean) args[4]); break;
			case SORT_BY_GET:
				sort_by_get((String) args[0], (String) args[1], (String[]) args[2]); break;
			case SORT_BY_DESC_GET:
				sort_by_desc_get((String) args[0], (String) args[1], (Boolean) args[2], (String[]) args[3]); break;
			case SORT_BY_ALPHA_DESC_GET:
				sort_by_alpha_desc_get((String) args[0], (String) args[1], (Boolean) args[2], (Boolean) args[3], (String[]) args[4]); break;
			case SORT_BY_OFFSET_COUNT_GET:
				sort_by_offset_count_get((String) args[0], (String) args[1], (Integer) args[2], (Integer) args[3], (String[]) args[4]); break;
			case SORT_BY_OFFSET_COUNT_ALPHA_DESC_GET:
				sort_by_offset_count_alpha_desc_get((String) args[0], (String) args[1], (Integer) args[2], (Integer) args[3], (Boolean) args[4], (Boolean) args[5], (String[]) args[6]); break;
			case SORT_DESTINATION:
				sort_destination((String) args[0], (String) args[1]); break;
			case SORT_DESC_DESTINATION:
				sort_desc_destination((String) args[0], (Boolean) args[1], (String) args[2]); break;
			case SORT_ALPHA_DESC_DESTINATION:
				sort_alpha_desc_destination((String) args[0], (Boolean) args[1], (Boolean) args[2], (String) args[3]); break;
			case SORT_OFFSET_COUNT_DESTINATION:
				sort_offset_count_destination((String) args[0], (Integer) args[1], (Integer) args[2], (String) args[3]); break;
			case SORT_OFFSET_COUNT_ALPHA_DESC_DESTINATION:
				sort_offset_count_alpha_desc_destination((String) args[0], (Integer) args[1], (Integer) args[2], (Boolean) args[3], (Boolean) args[4], (String) args[5]); break;
			case SORT_BY_DESTINATION_GET:
				sort_by_destination_get((String) args[0], (String) args[1], (String) args[2], (String[]) args[3]); break;
			case SORT_BY_DESC_DESTINATION_GET:
				sort_by_desc_destination_get((String) args[0], (String) args[1], (Boolean) args[2], (String) args[3], (String[]) args[4]); break;
			case SORT_BY_ALPHA_DESC_DESTINATION_GET:
				sort_by_alpha_desc_destination_get((String) args[0], (String) args[1], (Boolean) args[2], (Boolean) args[3], (String) args[4], (String[]) args[5]); break;
			case SORT_BY_OFFSET_COUNT_DESTINATION_GET:
				sort_by_offset_count_destination_get((String) args[0], (String) args[1], (Integer) args[2], (Integer) args[3], (String) args[4], (String[]) args[5]); break;
			case SORT_BY_OFFSET_COUNT_ALPHA_DESC_DESTINATION_GET:				
				sort_by_offset_count_alpha_desc_destination_get((String) args[0], (String) args[1], (Integer) args[2], (Integer) args[3], (Boolean) args[4], (Boolean) args[5], (String) args[6], (String[]) args[7]); break;
			case TTL:
				ttl0((String) args[0]); break;
			case TYPE:
				type0((String) args[0]); break;
				
			// Strings			
			case APPEND:
				append0((String) args[0], (String) args[1]); break;
			case BITCOUNT:
				bitcount0((String) args[0]); break;
			case BITCOUNT_START_END:
				bitcount0((String) args[0], (Long) args[1], (Long) args[2]); break;
			case BITNOT:
				bitnot0((String) args[0], (String) args[1]); break;
			case BITAND:
				bitand0((String) args[0], (String[]) args[1]); break;
			case BITOR:
				bitor0((String) args[0], (String[]) args[1]); break;
			case BITXOR:
				bitxor0((String) args[0], (String[]) args[1]); break;
			case DECR:
				decr0((String) args[0]); break;
			case DECRBY:
				decrby0((String) args[0], (Long) args[1]); break;
			case GET:
				get0((String) args[0]); break;
			case GETBIT:
				getbit0((String) args[0], (Long) args[1]); break;
			case GETRANGE:
				getrange0((String) args[0], (Long) args[1], (Long) args[2]); break;
			case GETSET:
				getset0((String) args[0], (String) args[1]); break;
			case INCR:
				incr0((String) args[0]); break;
			case INCRBY:
				incrby0((String) args[0], (Long) args[1]); break;
			case INCRBYFLOAT:
				incrbyfloat0((String) args[0], (Double) args[1]); break;
			case MGET:
				mget0((String[]) args[0]); break;
			case MSET:
				mset0((String[]) args[0]); break;
			case MSETNX:
				msetnx0((String[]) args[0]); break;
			case PSETEX:
				psetex0((String) args[0], (Integer) args[1], (String) args[2]); break;
			case SET:
				set0((String) args[0], (String) args[1]); break;
			case SETXX:
				setxx0((String) args[0], (String) args[1]); break;
			case SETNXEX:
				setnxex0((String) args[0], (String) args[1], (Integer) args[2]); break;
			case SETNXPX:
				setnxpx0((String) args[0], (String) args[1], (Integer) args[2]); break;
			case SETXXEX:
				setxxex0((String) args[0], (String) args[1], (Integer) args[2]); break;
			case SETXXPX:
				setxxpx0((String) args[0], (String) args[1], (Integer) args[2]); break;
			case SETBIT:
				setbit0((String) args[0], (Long) args[1], (Boolean) args[2]); break;
			case SETEX:
				setex0((String) args[0], (Integer) args[1], (String) args[2]); break;
			case SETNX:
				setnx0((String) args[0], (String) args[1]); break;
			case SETRANGE:
				setrange0((String) args[0], (Long) args[1], (String) args[2]); break;
			case STRLEN:
				strlen0((String) args[0]); break;
				
			// Hashes
			case HDEL:
				hdel0((String) args[0], (String[]) args[1]); break;
			case HEXISTS:
				hexists0((String) args[0], (String) args[1]); break;
			case HGET:
				hget0((String) args[0], (String) args[1]); break;
			case HGETALL:
				hgetall0((String) args[0]); break;
			case HINCRBY:
				hincrby0((String) args[0], (String) args[1], (Long) args[2]); break;
			case HINCRBYFLOAT:
				hincrbyfloat0((String) args[0], (String) args[1], (Double) args[2]); break;
			case HKEYS:
				hkeys0((String) args[0]); break;
			case HLEN:
				hlen0((String) args[0]); break;
			case HMGET:
				hmget0((String) args[0], (String[]) args[1]); break;
			case HMSET:
				hmset0((String) args[0], (Map<String, String>) args[1]); break;
			case HSET:
				hset0((String) args[0], (String) args[1], (String) args[2]); break;
			case HSETNX:
				hsetnx0((String) args[0], (String) args[1], (String) args[2]); break;
			case HVALS:
				hvals0((String) args[0]); break;
				
			// Lists
			case BLPOP:
				blpop0((Integer) args[0], (String[]) args[1]); break;
			case BRPOP:
				brpop0((Integer) args[0], (String[]) args[1]); break;
			case BRPOPLPUSH:
				brpoplpush0((String) args[0], (String) args[1], (Integer) args[2]); break;
			case LINDEX:
				lindex0((String) args[0], (Long) args[1]); break;
			case LINSERT_BEFORE:
				linsertbefore0((String) args[0], (String) args[1], (String) args[2]); break;
			case LINSERT_AFTER:
				linsertafter0((String) args[0], (String) args[1], (String) args[2]); break;
			case LLEN:
				llen0((String) args[0]); break;
			case LPOP:
				lpop0((String) args[0]); break;
			case LPUSH:
				lpush0((String) args[0], (String[]) args[1]); break;
			case LPUSHX:
				lpushx0((String) args[0], (String) args[1]); break;
			case LRANGE:
				lrange0((String) args[0], (Long) args[1], (Long) args[2]); break;
			case LREM:
				lrem0((String) args[0], (Long) args[1], (String) args[2]); break;
			case LSET:
				lset0((String) args[0], (Long) args[1], (String) args[2]); break;
			case LTRIM:
				ltrim0((String) args[0], (Long) args[1], (Long) args[2]); break;
			case RPOP:
				rpop0((String) args[0]); break;
			case RPOPLPUSH:
				rpoplpush0((String) args[0], (String) args[1]); break;
			case RPUSH:
				rpush0((String) args[0], (String[]) args[1]); break;
			case RPUSHX:
				rpushx0((String) args[0], (String) args[1]); break;
				
			// Sets
			case SADD:
				sadd0((String) args[0], (String[]) args[1]); break;
			case SCARD:
				scard0((String) args[0]); break;
			case SDIFF:
				sdiff0((String[]) args[0]); break;
			case SDIFFSTORE:
				sdiffstore0((String) args[0], (String[]) args[1]); break;
			case SINTER:
				sinter0((String[]) args[0]); break;
			case SINTERSTORE:
				sinterstore0((String) args[0], (String[]) args[1]); break;
			case SISMEMBER:
				sismember0((String) args[0], (String) args[1]); break;
			case SMEMBERS:
				smembers0((String) args[0]); break;
			case SMOVE:
				smove0((String) args[0], (String) args[1], (String) args[2]); break;
			case SPOP:
				spop0((String) args[0]); break;
			case SRANDMEMBER:
				srandmember0((String) args[0]); break;
			case SRANDMEMBER_COUNT:
				srandmember0((String) args[0], (Integer) args[1]); break;
			case SREM:
				srem0((String) args[0], (String[]) args[1]); break;
			case SUNION:
				sunion0((String[]) args[0]); break;
			case SUNIONSTORE:
				sunionstore0((String) args[0], (String[]) args[1]); break;
				
			// Sorted Set
			case ZADD:
				zadd0((String) args[0], (Map<String, Double>) args[1]); break;
			case ZCARD:
				zcard0((String) args[0]); break;
			case ZCOUNT:
				zcount0((String) args[0], (Double) args[1], (Double) args[2]); break;
			case ZCOUNT_STRING:
				zcount0((String) args[0], (String) args[1], (String) args[2]); break;
			case ZINCRBY:
				zincrby0((String) args[0], (Double) args[1], (String) args[2]); break;
			case ZINTERSTORE:
				zinterstore0((String) args[0], (String[]) args[1]); break;
			case ZINTERSTORE_MAX:
				zinterstoremax0((String) args[0], (String[]) args[1]); break;
			case ZINTERSTORE_MIN:
				zinterstoremin0((String) args[0], (String[]) args[1]); break;
			case ZINTERSTORE_WEIGHTS:
				zinterstore_weights((String) args[0], (Map<String, Integer>) args[1]); break;
			case ZINTERSTORE_WEIGHTS_MAX:
				zinterstore_weights_max((String) args[0], (Map<String, Integer>) args[1]); break;
			case ZINTERSTORE_WEIGHTS_MIN:
				zinterstore_weights_min((String) args[0], (Map<String, Integer>) args[1]); break;
			case ZRANGE:
				zrange0((String) args[0], (Long) args[1], (Long) args[2]); break;
			case ZRANGE_WITHSCORES:
				zrangewithscores0((String) args[0], (Long) args[1], (Long) args[2]); break;
			case ZRANGEBYSCORE:
				zrangebyscore0((String) args[0], (Double) args[1], (Double) args[2]); break;
			case ZRANGEBYSCORE_STRING:
				zrangebyscore_string((String) args[0], (String) args[1], (String) args[2]); break;
			case ZRANGEBYSCORE_OFFSET_COUNT:
				zrangebyscore_offset_count((String) args[0], (Double) args[1], (Double) args[2], (Integer) args[3], (Integer) args[4]); break;
			case ZRANGEBYSCORE_OFFSET_COUNT_STRING:
				zrangebyscore_offset_count_string((String) args[0], (String) args[1], (String) args[2], (Integer) args[3], (Integer) args[4]); break;
			case ZRANGEBYSCORE_WITHSCORES:
				zrangebyscorewithscores0((String) args[0], (Double) args[1], (Double) args[2]); break;
			case ZRANGEBYSCORE_WITHSCORES_STRING:
				zrangebyscorewithscores_string((String) args[0], (String) args[1], (String) args[2]); break;
			case ZRANGEBYSCORE_WITHSCORES_OFFSET_COUNT:
				zrangebyscorewithscores_offset_count((String) args[0], (Double) args[1], (Double) args[2], (Integer) args[3], (Integer) args[4]); break;
			case ZRANGEBYSCORE_WITHSCORES_OFFSET_COUNT_STRING:
				zrangebyscorewithscores_offset_count_string((String) args[0], (String) args[1], (String) args[2], (Integer) args[3], (Integer) args[4]); break;
			case ZRANK:
				zrank0((String) args[0], (String) args[1]); break;
			case ZREM:
				zrem0((String) args[0], (String[]) args[1]); break;
			case ZREMRANGEBYRANK:
				zremrangebyrank0((String) args[0], (Long) args[1], (Long) args[2]); break;
			case ZREMRANGEBYSCORE:
				zremrangebyscore0((String) args[0], (Double) args[1], (Double) args[2]); break;
			case ZREMRANGEBYSCORE_STRING:
				zremrangebyscore_string((String) args[0], (String) args[1], (String) args[2]); break;
			case ZREVRANGE:
				zrevrange0((String) args[0], (Long) args[1], (Long) args[2]); break;
			case ZREVRANGE_WITHSCORES:
				zrevrangewithscores0((String) args[0], (Long) args[1], (Long) args[2]); break;
			case ZREVRANGEBYSCORE:
				zrevrangebyscore0((String) args[0], (Double) args[1], (Double) args[2]); break;
			case ZREVRANGEBYSCORE_STRING:
				zrevrangebyscore_string((String) args[0], (String) args[1], (String) args[2]); break;
			case ZREVRANGEBYSCORE_OFFSET_COUNT:
				zrevrangebyscore_offset_count((String) args[0], (Double) args[1], (Double) args[2], (Integer) args[3], (Integer) args[4]); break;
			case ZREVRANGEBYSCORE_OFFSET_COUNT_STRING:
				zrevrangebyscore_offset_count_string((String) args[0], (String) args[1], (String) args[2], (Integer) args[3], (Integer) args[4]); break;
			case ZREVRANGEBYSCORE_WITHSCORES:
				zrevrangebyscorewithscores0((String) args[0], (Double) args[1], (Double) args[2]); break;
			case ZREVRANGEBYSCORE_WITHSCORES_STRING:
				zrevrangebyscorewithscores_string((String) args[0], (String) args[1], (String) args[2]); break;
			case ZREVRANGEBYSCORE_WITHSCORES_OFFSET_COUNT:
				zrevrangebyscorewithscores_offset_count((String) args[0], (Double) args[1], (Double) args[2], (Integer) args[3], (Integer) args[4]); break;
			case ZREVRANGEBYSCORE_WITHSCORES_OFFSET_COUNT_STRING:
				zrevrangebyscorewithscores_offset_count_string((String) args[0], (String) args[1], (String) args[2], (Integer) args[3], (Integer) args[4]); break;
			case ZREVRANK:
				zrevrank0((String) args[0], (String) args[1]); break;
			case ZSCORE:
				zscore0((String) args[0], (String) args[1]); break;
			case ZUNIONSTORE:
				zunionstore0((String) args[0], (String[]) args[1]); break;
			case ZUNIONSTORE_MAX:
				zunionstoremax0((String) args[0], (String[]) args[1]); break;
			case ZUNIONSTORE_MIN:
				zunionstoremin0((String) args[0], (String[]) args[1]); break;
			case ZUNIONSTORE_WEIGHTS:
				zunionstore_weights((String) args[0], (Map<String, Integer>) args[1]); break;
			case ZUNIONSTORE_WEIGHTS_MAX:
				zunionstore_weights_max((String) args[0], (Map<String, Integer>) args[1]); break;
			case ZUNIONSTORE_WEIGHTS_MIN:
				zunionstore_weights_min((String) args[0], (Map<String, Integer>) args[1]); break;
			
			// Pub/Sub
			case PUBLISH:
				publish0((String) args[0], (String) args[1]); break;
			default:
				throw new IllegalArgumentException("Wrong command");
			}
		} catch (Exception e) {
			RedisException re = r.handleException(e, j);
			throw re;
		}
	}

}

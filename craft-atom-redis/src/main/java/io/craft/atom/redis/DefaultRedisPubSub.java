package io.craft.atom.redis;

import io.craft.atom.redis.api.RedisPubSub;
import lombok.ToString;


import redis.clients.jedis.JedisPubSub;

/**
 * @author mindwind
 * @version 1.0, Jul 1, 2013
 */
@ToString
public class DefaultRedisPubSub implements RedisPubSub {
	
	
	private JedisPubSub jps;
	
	
	DefaultRedisPubSub(JedisPubSub jps) {
		this.jps = jps;
	}
	
	void subscribe(String... channels) {
		jps.subscribe(channels);
	}
	
	void unsubscribe(String... channels) {
		if (channels == null || channels.length == 0) {
			jps.unsubscribe();
		} else {
			jps.unsubscribe(channels);
		}
	}
	
	void psubscribe(String... patterns) {
		jps.psubscribe(patterns);
	}
	
	void punsubscribe(String... patterns) {
		if (patterns == null || patterns.length == 0) {
			jps.punsubscribe();
		} else {
			jps.punsubscribe(patterns);
		}
	}
	
}

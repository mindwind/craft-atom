package org.craft.atom.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import lombok.ToString;

import org.craft.atom.redis.api.RedisCommand;

/**
 * @author mindwind
 * @version 1.0, Jun 25, 2013
 */
@ToString(of = {"shards"})
public abstract class AbstractMurmurHashSharded<R extends RedisCommand> {
	
	
	private TreeMap<Long, R> nodes ;
	private List<R>          shards; 
	private MurmurHash       murmur;
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	public AbstractMurmurHashSharded(List<R> shards) {
		this.shards = shards;
		this.murmur = new MurmurHash();
		init(shards);
	}
	
	private void init(List<R> shards) {
		nodes = new TreeMap<Long, R>();
		for (int i = 0; i < shards.size(); i++) {
			R redis = shards.get(i);
            for (int n = 0; n < 160; n++) {
            	Long k = murmur.hash("SHARD-" + i + "-NODE-" + n);
            	nodes.put(k, redis);
            }
        }
	}

	public R shard(String shardkey) {
		SortedMap<Long, R> tail = nodes.tailMap(murmur.hash(shardkey));
		if (tail.isEmpty()) {
			return nodes.get(nodes.firstKey());
		}
		return tail.get(tail.firstKey());
	}

	public List<R> shards() {
		return new ArrayList<R>(shards);
	}

}

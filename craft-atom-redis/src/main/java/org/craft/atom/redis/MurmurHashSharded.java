package org.craft.atom.redis;

import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.craft.atom.redis.api.Redis;
import org.craft.atom.redis.spi.Sharded;

/**
 * @author mindwind
 * @version 1.0, Jun 25, 2013
 */
public class MurmurHashSharded implements Sharded {
	
	private TreeMap<Long, Redis> nodes;
	private List<Redis> shards; 
	private MurmurHash murmur;
	
	public MurmurHashSharded(List<Redis> shards) {
		this.shards = shards;
		init(shards);
	}
	
	private void init(List<Redis> shards) {
		nodes = new TreeMap<Long, Redis>();
		for (int i = 0; i < shards.size(); i++) {
            Redis redis = shards.get(i);
            for (int n = 0; n < 160; n++) {
            	nodes.put(murmur.hash("SHARD-" + i + "-NODE-" + n), redis);
            }
        }
	}

	@Override
	public Redis getShard(String shardkey) {
		SortedMap<Long, Redis> tail = nodes.tailMap(murmur.hash(shardkey));
		if (tail.isEmpty()) {
			return nodes.get(nodes.firstKey());
		}
		return tail.get(tail.firstKey());
	}

	@Override
	public List<Redis> getAllShards() {
		return Collections.unmodifiableList(shards);
	}
	

}

package io.craft.atom.rpc;

import io.craft.atom.rpc.api.RpcServerX;
import io.craft.atom.rpc.spi.RpcApi;

import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author mindwind
 * @version 1.0, Oct 13, 2014
 */
@ToString
public class DefaultRpcServerX implements RpcServerX {
	
	
	@Getter @Setter private int                 connectionCount;
	@Getter @Setter private Set<RpcApi>         apis           ;
	@Getter @Setter private Map<String, long[]> counts         ;
	

	@Override
	public int connectionCount() {
		return connectionCount;
	}

	@Override
	public Set<RpcApi> apis() {
		return apis;
	}

	@Override
	public int waitCount(RpcApi api) {
		return (int) counts.get(api.getKey())[0];
	}

	@Override
	public int processingCount(RpcApi api) {
		return (int) counts.get(api.getKey())[1];
	}

	@Override
	public long completeCount(RpcApi api) {
		return counts.get(api.getKey())[2];
	}

}

package org.craft.atom.rpc;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.craft.atom.rpc.api.RpcClientX;

/**
 * @author mindwind
 * @version 1.0, Oct 14, 2014
 */
@ToString
public class DefaultRpcClientX implements RpcClientX {
	
	
	@Getter @Setter private int waitCount;
	
	@Override
	public int waitCount() {
		return waitCount;
	}

}

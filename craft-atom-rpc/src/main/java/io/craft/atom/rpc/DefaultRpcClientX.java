package io.craft.atom.rpc;

import io.craft.atom.rpc.api.RpcClientX;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


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

package org.craft.atom.rpc;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.craft.atom.protocol.rpc.model.RpcMethod;
import org.craft.atom.rpc.api.RpcParameter;
import org.craft.atom.rpc.spi.RpcApi;

/**
 * @author mindwind
 * @version 1.0, Aug 12, 2014
 */
@ToString
public class DefaultRpcApi implements RpcApi {
	
	
	@Getter @Setter private String       rpcId       ;
	@Getter @Setter private Class<?>     rpcInterface;
	@Getter @Setter private RpcMethod    rpcMethod   ;
	@Getter @Setter private Object       rpcObject   ;
	@Getter @Setter private RpcParameter rpcParameter;
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	public DefaultRpcApi() {}
	
	public DefaultRpcApi(String rpcId, Class<?> rpcInterface, RpcMethod rpcMethod) {
		this.rpcId        = rpcId       ;
		this.rpcInterface = rpcInterface;
		this.rpcMethod    = rpcMethod   ;
	}
	
	public DefaultRpcApi(String rpcId, Class<?> rpcInterface, RpcMethod rpcMethod, Object rpcObject, RpcParameter rpcParameter) {
		this(rpcId, rpcInterface, rpcMethod);
		this.rpcObject    = rpcObject   ;
		this.rpcParameter = rpcParameter;
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public String getKey() {
		if (rpcId == null) {
			return Integer.toString(rpcInterface.hashCode()) + Integer.toString(rpcMethod.hashCode());
		} else {
			return rpcId + "-" + Integer.toString(rpcInterface.hashCode()) + Integer.toString(rpcMethod.hashCode());
		}
	}

	@Override
	public String getName() {
		String format = "{rpc-id=%s, rpc-interface=%s, rpc-method-name=%s, rpc-method-parameter-types=%s, rpc-object=%s, rpc-parameter=%s}";
		return String.format(format, rpcId, rpcInterface.getName(), rpcMethod.getName(), rpcMethod.getParameterTypes().toString(), rpcObject.toString(), rpcParameter.toString());
	}

}

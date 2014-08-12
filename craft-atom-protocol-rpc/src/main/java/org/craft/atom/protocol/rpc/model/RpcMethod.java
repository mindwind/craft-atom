package org.craft.atom.protocol.rpc.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A <code>RpcMethod</code> provides information about a RPC interface exposed method.
 * 
 * @author mindwind
 * @version 1.0, Aug 8, 2014
 */
@ToString
@EqualsAndHashCode(of = { "name", "parameterTypes", "returnType" })
public class RpcMethod {
	
	
	@Getter @Setter private String              name          ;
	@Getter         private Class<?>[]          parameterTypes;
	@Getter         private Object[]            parameters    ;
	@Getter @Setter private Class<?>		    returnType    ;
	
	
	public void setParameterTypes(Class<?>... parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public void setParameters(Object... parameters) {
		this.parameters = parameters;
	}

}

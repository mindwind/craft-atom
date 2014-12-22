package io.craft.atom.protocol.rpc.model;

import java.io.Serializable;

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
@EqualsAndHashCode(of = { "name", "parameterTypes" })
public class RpcMethod implements Serializable {
	
	
	private static final long serialVersionUID = -4302065109637231162L;
	
	
	@Getter @Setter private String     name          ;
	@Getter         private Class<?>[] parameterTypes;
	@Getter         private Object[]   parameters    ;
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public RpcMethod() {}
	
	public RpcMethod(String name, Class<?>[] parameterTypes) {
		this.name = name;
		this.parameterTypes = parameterTypes;
	}
	
	public RpcMethod(String name, Class<?>[] parameterTypes, Object[] parameters) {
		this(name, parameterTypes);
		this.parameters = parameters;
	}
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	public void setParameterTypes(Class<?>... parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public void setParameters(Object... parameters) {
		this.parameters = parameters;
	}

}

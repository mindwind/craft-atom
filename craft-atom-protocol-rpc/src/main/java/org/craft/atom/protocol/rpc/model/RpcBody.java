package org.craft.atom.protocol.rpc.model;

import java.io.Serializable;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a RPC header field.
 * <p>
 * A body that can be sent or received with a Rpc message, 
 * but not all messages contain a body, it is optional.
 * The body contains a block of arbitrary data and can be serialized by specific serializer.
 * 
 * @author mindwind
 * @version 1.0, Jul 18, 2014
 */
@ToString
public class RpcBody implements Serializable {

	
	private static final long serialVersionUID = 5138100956693144357L;
	
	
	@Getter @Setter private Class<?>               clazz      ;
	@Getter @Setter private String                 method     ;
	@Getter         private Class<?>[]             argsTypes  ;
	@Getter         private Object[]               args       ;
	@Getter @Setter private Map<String, Object>    attachments;
	
	
	public void setArgsTypes(Class<?>... argsTypes) {
		this.argsTypes = argsTypes;
	}

	public void setArgs(Object... args) {
		this.args = args;
	}

}

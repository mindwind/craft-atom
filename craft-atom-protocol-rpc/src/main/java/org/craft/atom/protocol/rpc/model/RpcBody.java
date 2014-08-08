package org.craft.atom.protocol.rpc.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a RPC body field.
 * <p>
 * A body that can be sent or received with a rpc message, 
 * but not all messages contain a body, it is optional.
 * The body contains a block of arbitrary data and can be serialized by specific serializer.
 * 
 * @author mindwind
 * @version 1.0, Jul 18, 2014
 */
@ToString
@EqualsAndHashCode(of = { "rpcInterface", "rpcMethod", "returnObject", "thrownObject", "attachments" })
public class RpcBody implements Serializable {

	
	private static final long serialVersionUID = 5138100956693144357L;
	
	
	@Getter @Setter private Class<?>            rpcInterface;
	@Getter @Setter private RpcMethod           rpcMethod   ;
	@Getter @Setter private Object              returnObject;
	@Getter @Setter private Throwable           thrownObject;
	@Getter @Setter private Map<String, Object> attachments ;
	
	
	public void addAttachment(String key, String value) {
		if (attachments == null) {
			attachments = new HashMap<String, Object>();
		}
		attachments.put(key, value);
	}

}

package org.craft.atom.protocol.rpc.model;

import java.io.Serializable;

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
public class RpcBody implements Serializable {

	
	private static final long serialVersionUID = 5138100956693144357L;

}

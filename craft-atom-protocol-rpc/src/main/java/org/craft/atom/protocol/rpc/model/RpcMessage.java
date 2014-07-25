package org.craft.atom.protocol.rpc.model;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * RPC messages use generic message format for transferring data.
 * <pre>
 *      rpc-message = rpc-header + [ rpc-body ]
 * </pre>
 * 
 * @param <T> the type of the interface class.
 * 
 * @author mindwind
 * @version 1.0, Jul 17, 2014
 */
@ToString
@EqualsAndHashCode(of = { "header", "body" })
public class RpcMessage implements Serializable {

	
	private static final long serialVersionUID = 5138100956693144357L;
	
	
	@Getter @Setter private RpcHeader  header;
	@Getter @Setter private RpcBody    body  ;

}

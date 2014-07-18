package org.craft.atom.protocol.rpc.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a RPC header field.
 * <p>
 * 
 * The RPC header fields use the generic format as follows:
 * <pre>
 * 000-------------------------------------------------------------------015-------------------------------------------------------------------031
 * |                                 magic                                |                                header size                           |                      
 * 032--------------------------------039-----------044----045----046----047--------------------------------055--------------------------------063
 * |                version            |      st     |  hb  |  tw  |  rr  |        status code               |            reserved               |                
 * 064-----------------------------------------------------------------------------------------------------------------------------------------095
 * |                                                                 message id                                                                  |
 * |                                                                                                                                             |
 * 096-----------------------------------------------------------------------------------------------------------------------------------------127
 * |                                                                  body size                                                                  |
 * 128-----------------------------------------------------------------------------------------------------------------------------------------159
 * 
 * st = serialization type.
 * hb = heartbeat flag, set 1 means it is a heatbeat message.
 * tw = two way  flag, set 1 means it is tway message, the client wait for a response.
 * rr = request or response flag, set 1 means it is response message, otherwise it's a request message.
 * </pre>
 * 
 * @author mindwind
 * @version 1.0, Jul 18, 2014
 */
@ToString
public class RpcHeader implements Serializable {

	
	private static final long  serialVersionUID   = -67119913240566784L;
	private static final int   ST_MASK            = 0x1f               ;
	private static final int   HB_MASK            = 0x20               ;
	private static final int   TW_MASK            = 0x40               ;
	private static final int   RR_MASK            = 0x80               ;
	
	
	@Getter @Setter private short magic      = (short) 0xcaf6     ;
	@Getter @Setter private short headerSize = (short) 20         ;
	@Getter @Setter private byte  version    = (byte)  1          ;
	@Getter @Setter private byte  st         = (byte)  1 & ST_MASK;
	@Getter @Setter private byte  hb         = (byte)  0 & HB_MASK;
	@Getter @Setter private byte  tw         = (byte)  0 & TW_MASK;
	@Getter @Setter private byte  rr         = (byte)  0 & RR_MASK;
	@Getter @Setter private byte  statusCode = (byte)  0          ;
	@Getter @Setter private byte  reserved   = (byte)  0          ;
	@Getter @Setter private long  id         = (long)  0          ;
	@Getter @Setter private int   bodySize   = (int)   0          ;

}

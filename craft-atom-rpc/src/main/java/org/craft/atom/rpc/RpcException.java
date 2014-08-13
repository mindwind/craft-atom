package org.craft.atom.rpc;

import lombok.Getter;

/**
 * @author mindwind
 * @version 1.0, Aug 8, 2014
 */
public final class RpcException extends RuntimeException {

	
	private static final long serialVersionUID  = -4168884981656035910L;
	
	
	public static final byte UNKNOWN         = 0 ;
    public static final byte SERVER_ERROR    = 50;
    public static final byte SERVER_TIMEOUT  = 54;
    public static final byte SERVER_OVERLOAD = 57;
	
	
	@Getter private byte code;
	
	
	public RpcException(byte code, Throwable cause) {
        super(cause);
        this.code = code;
    }
	
}

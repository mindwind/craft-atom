package org.craft.atom.rpc;

import lombok.Getter;

/**
 * @author mindwind
 * @version 1.0, Aug 8, 2014
 */
public final class RpcException extends RuntimeException {

	
	private static final long serialVersionUID  = -4168884981656035910L;
	
	
	public static final byte UNKNOWN  = 0;
    public static final byte TIMEOUT  = 1;
    public static final byte BUSINESS = 2;
    public static final byte OVERLOAD = 3;
	
	
	@Getter private byte code;
	
	
	public RpcException(byte code, Throwable cause) {
        super(cause);
        this.code = code;
    }
	
}

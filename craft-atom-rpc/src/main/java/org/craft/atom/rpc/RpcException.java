package org.craft.atom.rpc;

import lombok.Getter;

/**
 * @author mindwind
 * @version 1.0, Aug 8, 2014
 */
public final class RpcException extends RuntimeException {

	
	private static final long serialVersionUID  = -4168884981656035910L;
	
	
	public  static final byte UNKNOWN_EXCEPTION = 0;
    public  static final byte TIMEOUT_EXCEPTION = 1;
    public  static final byte BIZ_EXCEPTION     = 2;
	
	
	@Getter private byte code;
	
	
	public RpcException(byte code, Throwable cause) {
        super(cause);
        this.code = code;
    }
	
}

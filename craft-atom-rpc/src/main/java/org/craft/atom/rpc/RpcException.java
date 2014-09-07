package org.craft.atom.rpc;

import lombok.Getter;

/**
 * @author mindwind
 * @version 1.0, Aug 8, 2014
 */
public final class RpcException extends RuntimeException {

	
	private static final long serialVersionUID  = -4168884981656035910L;
	
	
	public static final byte UNKNOWN         = 0 ;
	public static final byte NET_IO          = 10;
	public static final byte CLIENT_BAD_REQ  = 40;
	public static final byte CLIENT_CONNECT  = 41;
	public static final byte CLIENT_TIMEOUT  = 42;
    public static final byte SERVER_ERROR    = 50;
    public static final byte SERVER_TIMEOUT  = 51;
    public static final byte SERVER_OVERLOAD = 52;
	
	
	@Getter private byte code;
	
	
	public RpcException() {}
	
	public RpcException(byte code) {
        this.code = code;
    }
	
	public RpcException(byte code, Throwable cause) {
        super(cause);
        this.code = code;
    }
	
}

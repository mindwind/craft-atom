package org.craft.atom.redis.api;

/**
 * @author mindwind
 * @version 1.0, Jun 18, 2013
 */
public class RedisException extends RuntimeException {
	
    private static final long serialVersionUID = -2946266495682282677L;

    public RedisException(String message) {
        super(message);
    }

    public RedisException(Throwable e) {
        super(e);
    }

    public RedisException(String message, Throwable cause) {
        super(message, cause);
    }
}

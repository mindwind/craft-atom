package org.craft.atom.redis.api;

/**
 * @author mindwind
 * @version 1.0, Jun 18, 2013
 */
public class RedisOperationException extends RedisException {
	
    private static final long serialVersionUID = 3878126572474819403L;

    public RedisOperationException(String message) {
        super(message);
    }

    public RedisOperationException(Throwable cause) {
        super(cause);
    }

    public RedisOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

package org.craft.atom.redis.api;

/**
 * @author mindwind
 * @version 1.0, Jun 18, 2013
 */
public class RedisDataException extends RedisException {
	
    private static final long serialVersionUID = 3878126572474819403L;

    public RedisDataException(String message) {
        super(message);
    }

    public RedisDataException(Throwable cause) {
        super(cause);
    }

    public RedisDataException(String message, Throwable cause) {
        super(message, cause);
    }
}

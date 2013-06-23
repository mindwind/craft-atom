package org.craft.atom.redis;

/**
 * @author mindwind
 * @version 1.0, Jun 18, 2013
 */
public abstract class AbstractRedis {
	
	protected static final String OK = "OK";
	protected static final String TRANSACTION_UNSUPPORTED = "Command [%s] is not supported in transaction context.";
	
}

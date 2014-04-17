package org.craft.atom.redis.api;

import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * @author mindwind
 * @version 1.0, Apr 15, 2014
 */
public abstract class AbstractRedisBuilder<T> {
	
	
	/**
	 * <pre>
	 * timeoutInMillis                  : socket connect/read timeout in milliseconds
	 * database                         : redis database num
	 * poolMinIdle                      : see {@link GenericObjectPool#setMinIdle}
	 * poolMaxIdle                      : see {@link GenericObjectPool#setMaxIdle}
	 * poolMaxTotal                     : see {@link GenericObjectPool#setMaxTotal}
	 * poolNumTestsPerEvictionRun       : see {@link GenericObjectPool#setNumTestsPerEvictionRun}
	 * poolBlockWhenExhausted           : see {@link GenericObjectPool#setBlockWhenExhausted}
	 * poolTestOnBorrow                 : see {@link GenericObjectPool#setTestOnBorrow}
	 * poolTestOnReturn                 : see {@link GenericObjectPool#setTestOnReturn}
	 * poolTestWhileIdle                : see {@link GenericObjectPool#setTestWhileIdle}
	 * poolLifo                         : see {@link GenericObjectPool#setLifo}
	 * poolMaxWait                      : see {@link GenericObjectPool#setMaxWaitMillis}
	 * poolTimeBetweenEvictionRunsMillis: see {@link GenericObjectPool#setTimeBetweenEvictionRunsMillis}
	 * poolMinEvictableIdleTimeMillis   : see {@link GenericObjectPool#setMinEvictableIdleTimeMillis} 
	 * password                         : redis password
	 * </pre>
	 */
	protected int     timeoutInMillis;
	protected int     database;
	protected int     poolMinIdle;
	protected int     poolMaxIdle; 
	protected int     poolMaxTotal;
	protected int     poolNumTestsPerEvictionRun;
	protected byte    poolBlockWhenExhausted;
	protected boolean poolTestOnBorrow;
	protected boolean poolTestOnReturn;
	protected boolean poolTestWhileIdle;
	protected boolean poolLifo;
	protected long    poolMaxWaitMillis;
	protected long    poolTimeBetweenEvictionRunsMillis;
	protected long    poolMinEvictableIdleTimeMillis;
	protected String  password;
	 

	abstract public T build();
	
	
}

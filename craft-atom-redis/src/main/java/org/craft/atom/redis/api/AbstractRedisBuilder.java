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
	protected int     timeoutInMillis                   = 1000 ;
	protected int     database                          = 0    ;
	protected int     poolMinIdle                       = 0    ;
	protected int     poolMaxIdle                       = 100  ; 
	protected int     poolMaxTotal                      = 100  ;
	protected int     poolNumTestsPerEvictionRun        = -1   ;
	protected boolean poolBlockWhenExhausted            = true ;
	protected boolean poolTestOnBorrow                  = false;
	protected boolean poolTestOnReturn                  = false;
	protected boolean poolTestWhileIdle                 = true ;
	protected boolean poolLifo                          = true ;
	protected long    poolMaxWaitMillis                 = -1L  ;
	protected long    poolTimeBetweenEvictionRunsMillis = 30000;
	protected long    poolMinEvictableIdleTimeMillis    = 60000;
	protected String  password                                 ;
	
	
	
	public AbstractRedisBuilder<T> timeoutInMillis                  (int timeout)     { this.timeoutInMillis                   = timeout ; return this; }
	public AbstractRedisBuilder<T> database                         (int database)    { this.database                          = database; return this; }
	public AbstractRedisBuilder<T> poolMinIdle                      (int minIdle)     { this.poolMinIdle                       = minIdle ; return this; }
	public AbstractRedisBuilder<T> poolMaxIdle                      (int maxIdle)     { this.poolMaxIdle                       = maxIdle ; return this; }
	public AbstractRedisBuilder<T> poolMaxTotal                     (int maxTotal)    { this.poolMaxTotal                      = maxTotal; return this; }
	public AbstractRedisBuilder<T> poolNumTestsPerEvictionRun       (int num)         { this.poolNumTestsPerEvictionRun        = num     ; return this; }
	public AbstractRedisBuilder<T> poolBlockWhenExhausted           (boolean block)   { this.poolBlockWhenExhausted            = block   ; return this; }
	public AbstractRedisBuilder<T> poolTestOnBorrow                 (boolean test)    { this.poolTestOnBorrow                  = test    ; return this; }
	public AbstractRedisBuilder<T> poolTestOnReturn                 (boolean test)    { this.poolTestOnReturn                  = test    ; return this; }
	public AbstractRedisBuilder<T> poolTestWhileIdle                (boolean test)    { this.poolTestWhileIdle                 = test    ; return this; }
	public AbstractRedisBuilder<T> poolLifo                         (boolean lifo)    { this.poolLifo                          = lifo    ; return this; }
	public AbstractRedisBuilder<T> poolMaxWaitMillis                (long maxWait)    { this.poolMaxWaitMillis                 = maxWait ; return this; }
	public AbstractRedisBuilder<T> poolTimeBetweenEvictionRunsMillis(long time)       { this.poolTimeBetweenEvictionRunsMillis = time    ; return this; }
	public AbstractRedisBuilder<T> poolMinEvictableIdleTimeMillis   (long time)       { this.poolMinEvictableIdleTimeMillis    = time    ; return this; }
	public AbstractRedisBuilder<T> password                         (String password) { this.password                          = password; return this; }
	
	
	public AbstractRedisBuilder<T> redisPoolConfig(RedisPoolConfig cfg) {
		this.poolMinIdle                       = cfg.minIdle                      ;
		this.poolMaxIdle                       = cfg.maxIdle                      ;
		this.poolMaxTotal                      = cfg.maxTotal                     ;
		this.poolNumTestsPerEvictionRun        = cfg.numTestsPerEvictionRun       ;
		this.poolBlockWhenExhausted            = cfg.blockWhenExhausted           ;
		this.poolTestOnBorrow                  = cfg.testOnBorrow                 ;
		this.poolTestOnReturn                  = cfg.testOnReturn                 ;
		this.poolTestWhileIdle                 = cfg.testWhileIdle                ;
		this.poolLifo                          = cfg.lifo                         ;
		this.poolMaxWaitMillis                 = cfg.maxWaitMillis                ;
		this.poolTimeBetweenEvictionRunsMillis = cfg.timeBetweenEvictionRunsMillis;
		this.poolMinEvictableIdleTimeMillis    = cfg.minEvictableIdleTimeMillis   ;
		return this;
	}

	
	
	protected AbstractRedisBuilder<T> copy(AbstractRedisBuilder<?> builder) {
		this.timeoutInMillis                   = builder.timeoutInMillis                  ; 
		this.database                          = builder.database                         ; 
		this.poolMinIdle                       = builder.poolMinIdle                      ; 
		this.poolMaxIdle                       = builder.poolMaxIdle                      ; 
		this.poolMaxTotal                      = builder.poolMaxTotal                     ; 
		this.poolNumTestsPerEvictionRun        = builder.poolNumTestsPerEvictionRun       ; 
		this.poolBlockWhenExhausted            = builder.poolBlockWhenExhausted           ; 
		this.poolTestOnBorrow                  = builder.poolTestOnBorrow                 ; 
		this.poolTestOnReturn                  = builder.poolTestOnReturn                 ; 
		this.poolTestWhileIdle                 = builder.poolTestWhileIdle                ; 
		this.poolLifo                          = builder.poolLifo                         ; 
		this.poolMaxWaitMillis                 = builder.poolMaxWaitMillis                ; 
		this.poolTimeBetweenEvictionRunsMillis = builder.poolTimeBetweenEvictionRunsMillis; 
		this.poolMinEvictableIdleTimeMillis    = builder.poolMinEvictableIdleTimeMillis   ; 
		this.password                          = builder.password                         ; 
		return this;
	}
	
	
	protected void set(RedisPoolConfig cfg) {
		cfg.setMinIdle(poolMinIdle);
		cfg.setMaxIdle(poolMaxIdle);
		cfg.setMaxTotal(poolMaxTotal);
		cfg.setNumTestsPerEvictionRun(poolNumTestsPerEvictionRun);
		cfg.setBlockWhenExhausted(poolBlockWhenExhausted);
		cfg.setTestOnBorrow(poolTestOnBorrow);
		cfg.setTestOnReturn(poolTestOnReturn);
		cfg.setTestWhileIdle(poolTestWhileIdle);
		cfg.setLifo(poolLifo);
		cfg.setMaxWaitMillis(poolMaxWaitMillis);
		cfg.setTimeBetweenEvictionRunsMillis(poolTimeBetweenEvictionRunsMillis);
		cfg.setMinEvictableIdleTimeMillis(poolMinEvictableIdleTimeMillis);
	}
	
	
	abstract public T build();
	
}

package org.craft.atom.redis.api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Redis connection pool config
 * 
 * @author mindwind
 * @version 1.0, Apr 17, 2014
 */
@ToString
public class RedisPoolConfig {
	
	
	@Getter @Setter private int     minIdle                       = 0    ;
	@Getter @Setter private int     maxIdle                       = 100  ;
	@Getter @Setter private int     maxTotal                      = 100  ;
	@Getter @Setter private int     numTestsPerEvictionRun        = -1   ;
	@Getter @Setter private boolean lifo                          = true ;
	@Getter @Setter private boolean testOnBorrow                  = false;
	@Getter @Setter private boolean testOnReturn                  = false;
	@Getter @Setter private boolean testWhileIdle                 = true ;
	@Getter @Setter private boolean blockWhenExhausted            = true ;
	@Getter @Setter private long    maxWaitMillis                 = -1L  ;
	@Getter @Setter private long    minEvictableIdleTimeMillis    = 60000;
	@Getter @Setter private long    timeBetweenEvictionRunsMillis = 30000;

}

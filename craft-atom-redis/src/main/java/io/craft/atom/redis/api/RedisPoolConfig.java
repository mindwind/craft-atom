package io.craft.atom.redis.api;

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
	
	                     
	static final int     DEFAULT_MIN_IDLE                          = 0    ;
	static final int     DEFAULT_MAX_IDLE                          = 100  ;
	static final int     DEFAULT_MAX_TOTAL                         = 100  ;
	static final int     DEFAULT_NUM_TESTS_PER_EVICTION_RUN        = -1   ;
	static final boolean DEFAULT_LIFO                              = true ;
	static final boolean DEFAULT_TEST_ON_BORROW                    = false;
	static final boolean DEFAULT_TEST_ON_RETURN                    = false;
	static final boolean DEFAULT_TEST_WHILE_IDLE                   = true ;
	static final boolean DEFAULT_BLOCK_WHEN_EXHAUSTED              = true ;
	static final long    DEFAULT_MAX_WAIT_MILLIS                   = -1L  ;
	static final long    DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS    = 60000;
	static final long    DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = 30000;
	
	
	@Getter @Setter int     minIdle                       = DEFAULT_MIN_IDLE                         ;
	@Getter @Setter int     maxIdle                       = DEFAULT_MAX_IDLE                         ;
	@Getter @Setter int     maxTotal                      = DEFAULT_MAX_TOTAL                        ;
	@Getter @Setter int     numTestsPerEvictionRun        = DEFAULT_NUM_TESTS_PER_EVICTION_RUN       ;
	@Getter @Setter boolean lifo                          = DEFAULT_LIFO                             ;
	@Getter @Setter boolean testOnBorrow                  = DEFAULT_TEST_ON_BORROW                   ;
	@Getter @Setter boolean testOnReturn                  = DEFAULT_TEST_ON_RETURN                   ;
	@Getter @Setter boolean testWhileIdle                 = DEFAULT_TEST_WHILE_IDLE                  ;
	@Getter @Setter boolean blockWhenExhausted            = DEFAULT_BLOCK_WHEN_EXHAUSTED             ;
	@Getter @Setter long    maxWaitMillis                 = DEFAULT_MAX_WAIT_MILLIS                  ;
	@Getter @Setter long    minEvictableIdleTimeMillis    = DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS   ;
	@Getter @Setter long    timeBetweenEvictionRunsMillis = DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

}

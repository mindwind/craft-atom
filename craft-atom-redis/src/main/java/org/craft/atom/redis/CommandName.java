package org.craft.atom.redis;

/**
 * Redis command name enum.
 * 
 * @author mindwind
 * @version 1.0, Jun 19, 2013
 */
public enum CommandName {
	
	// ~ -------------------------------------------------------------------------------------------------------- Keys
	
	
	DEL,
	DUMP,
	EXISTS,
	EXPIRE,
	EXPIREAT,
	KEYS,
	MIGRATE,
	MOVE,
	OBJECT,
	PERSIST,
	PEXPIRE,
	PEXPIREAT,
	PTTL,
	RANDOMKEY,
	RENAME,
	RENAMENX,
	RESTORE,
	SORT,
	TTL,
	TYPE,
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Strings
	
	
	APPEND,
	BITCOUNT,
	BITOP,
	DECR,
	DECRBY,
	GET,
	GETBIT,
	GETRANGE,
	GETSET,
	INCR,
	INCRBY,
	INCRBYFLOAT,
	MGET,
	MSET,
	MSETNX,
	PSETEX,
	SET,
	SETBIT,
	SETEX,
	SETNX,
	SETRANGE,
	STRLEN,
	
	
	// ~ ------------------------------------------------------------------------------------------------------ Hashes
	
	
	HDEL,
	HEXISTS,
	HGET,
	HGETALL,
	HINCRBY,
	HINCRBYFLOAT,
	HKEYS,
	HLEN,
	HMGET,
	HMSET,
	HSET,
	HSETNX,
	HVALS,
	
	
	// ~ ------------------------------------------------------------------------------------------------ Transactions
	
	
	DISCARD,
	EXEC,
	MULTI,
	UNWATCH,
	WATCH
	
}

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
	OBJECT_REFCOUNT,
	OBJECT_ENCODING,
	OBJECT_IDLETIME,
	PERSIST,
	PEXPIRE,
	PEXPIREAT,
	PTTL,
	RANDOMKEY,
	RENAME,
	RENAMENX,
	RESTORE,
	SORT,
	SORT_DESC,
	SORT_ALPHA_DESC,
	SORT_OFFSET_COUNT,
	SORT_OFFSET_COUNT_ALPHA_DESC,
	SORT_BY_GET,
	SORT_BY_GET_DESC,
	SORT_BY_GET_ALPHA_DESC,
	SORT_BY_GET_OFFSET_COUNT,
	SORT_BY_GET_OFFSET_COUNT_ALPHA_DESC,
	SORT_BY_DESTINATION,
	SORT_BY_GET_DESC_DESTINATION,
	SORT_BY_GET_ALPHA_DESC_DESTINATION,
	SORT_BY_GET_OFFSET_COUNT_DESTINATION,
	SORT_BY_GET_OFFSET_COUNT_ALPHA_DESC_DESTINATION,
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
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Lists
	
	
	BLPOP,
	BRPOP,
	BRPOPLPUSH,
	LINDEX,
	LINSERT,
	LLEN,
	LPOP,
	LPUSH,
	LPUSHX,
	LRANGE,
	LREM,
	LSET,
	LTRIM,
	RPOP,
	RPOPLPUSH,
	RPUSH,
	RPUSHX,
	
	
	// ~ ------------------------------------------------------------------------------------------------------- Sets
	
	
	SADD,
	SCARD,
	SDIFF,
	SDIFFSTORE,
	SINTER,
	SINTERSTORE,
	SISMEMBER,
	SMEMBERS,
	SMOVE,
	SPOP,
	SRANDMEMBER,
	SREM,
	SUNION,
	SUNIONSTORE,
	
	
	// ~ ------------------------------------------------------------------------------------------------ Transactions
	
	
	DISCARD,
	EXEC,
	MULTI,
	UNWATCH,
	WATCH
	
}

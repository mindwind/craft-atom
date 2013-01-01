package org.craft.atom.cache.impl;

import java.util.List;

import org.craft.atom.cache.Transaction;

/**
 * @author Hu Feng
 * @version 1.0, Sep 27, 2012
 */
abstract public class AbstractTransaction implements Transaction {
	
	protected volatile boolean closed = false;
	protected redis.clients.jedis.Transaction delegate;
	protected RedisCache redisCache;

	@Override
	public List<Object> commit() {
		if (closed) {
			throw new IllegalStateException("transaction already closed!");
		}
		
		List<Object> list = delegate.exec();
		if (list != null && list.size() == 0) {
			list = null;
		}
		
		return list;
	}

	@Override
	public void abort() {
		delegate.discard();
	}

	public redis.clients.jedis.Transaction getDelegate() {
		return delegate;
	}

	public void setDelegate(redis.clients.jedis.Transaction delegate) {
		this.delegate = delegate;
	}

}

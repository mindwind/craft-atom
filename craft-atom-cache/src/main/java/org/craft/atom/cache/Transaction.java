package org.craft.atom.cache;

import java.util.List;

/**
 * Allows the application to define units of work, while maintaining abstraction from the underlying transaction implementation (e.g. Redis). 
 * <br>
 * A transaction is associated with a <tt>Thread</tt> and is usually instantiated by a call to <tt>cache.beginTransaction()</tt>
 * or <tt>cache.beginTransaction(key)</tt> in a sharded environment. So transaction can not span multiple threads, it must begin
 * in a thread and commit or abort in the same thread.
 * <br>
 * If you have a relational databases background, maybe you see there is no rollback operation for this transaction interface.
 * Yes, most use case for cache operation, it's unnecessary for rollback.
 * <p>
 * Typically usage:
 * <pre>
 * Transaction tx = null;
 * try {
 *     tx = cache.beginTransaction(key);
 *     cache.xx1(key);
 *     cache.xx2(key);
 *     List<Object> result = tx.commit();
 * } finally {
 *     if (tx != null) { tx.close(); }
 * }
 * </pre>
 * 
 * <b>NOTE:</b>
 * Cache transaction does not allow to use intermediate results of a transaction within that same transaction.<br>
 * This does not work like this:
 * <pre>
 *     Transaction tx = cache.beginTransaction(key);
 *     String v = cache.get(key)
 *     if (v.equals("something") {
 *         cache.set(key, value1);
 *     } else {
 *         cache.set(key, value2);
 *     }
 *     tx.commit();
 * </pre>
 * 
 * @author Hu Feng
 * @version 1.0, Sep 27, 2012
 */
public interface Transaction {

	/**
	 * This method will commit the underlying transaction if and only if the
	 * underlying transaction was initiated by this object.
	 * 
	 * @return all results of operations in this transaction by execution sequence, if commit failed return <tt>null</tt>.
	 */
	List<Object> commit();
	
	/**
	 * Abort all previously operation in a transaction.
	 */
	void abort();
	
	/**
	 * Close the transaction, if transaction already closed invoke this has no effect.
	 */
	void close();

}

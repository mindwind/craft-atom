package org.craft.atom.cache;

/**
 * General cache abstraction.
 * 
 * @author Hu Feng
 * @version 1.0, Sep 4, 2012
 */
public interface Cache {

	/**
	 * Set a timeout on key. After the timeout has expired, the key will
	 * automatically be deleted. A key with an associated timeout is often said
	 * to be volatile in this terminology. The timeout is cleared only when the
	 * key is removed using the {@link #del(key)} command or overwritten using
	 * the {@link #set(key)} or {@link #getset(key)} commands.
	 * <p>
	 * This means that all the operations that conceptually alter the value
	 * stored at the key without replacing it with a new one will leave the
	 * timeout untouched. For instance, incrementing the value of a key with
	 * {@link #incr(key)}, pushing a new value into a list with
	 * {@link #lpush(key, values)}, or altering the field value of a hash with
	 * {@link #hset(key)} are all operations that will leave the timeout
	 * untouched.
	 * <p>
	 * The timeout can also be cleared, turning the key back into a persistent
	 * key, using the {@link #persist(key)} command. If a key is renamed with
	 * {@link #rename(key, newKey)} , the associated time to live is transferred
	 * to the new key name. If a key is overwritten by RENAME, like in the case
	 * of an existing key Key_A that is overwritten by a call like RENAME Key_B
	 * Key_A, it does not matter if the original Key_A had a timeout associated
	 * or not, the new key Key_A will inherit all the characteristics of Key_B.
	 * 
	 * @param key
	 * @param ttl
	 *            seconds of time to live, a positive number, negative number or zero means delete the key.
	 * @return 1 if the timeout was set. <br>
	 *         0 if key does not exist or the timeout could not be set.
	 */
	Long expire(String key, int ttl);

	/**
	 * Removes the specified keys. A key is ignored if it does not exist. 
	 * <p>
	 * <b>NOTE:</b> <br>
	 * It can't be an atomic operation in a sharded cache deployment condition with multiple keys.<br>
	 * In a sharded cache deployment, accept only one key in a transaction.
	 * 
	 * @param keys
	 * @return The number of keys that were removed.
	 */
	Long del(String... keys);

	/**
	 * Returns the remaining time to live of a key that has a timeout. This
	 * introspection capability allows a client to check how many seconds
	 * a given key will continue to be part of the data set.
	 * 
	 * @param key
	 * @return TTL in seconds or -1 when key does not exist or does not have a timeout.
	 */
	Long ttl(String key);
	
	/**
	 * Remove the existing timeout on key, turning the key from volatile (a key with an expire set) 
	 * to persistent (a key that will never expire as no timeout is associated).<br>
	 * Undo {@link #expire(String, int) expire}.
	 * 
	 * @param key
	 * @return
	 */
	Boolean persist(String key);
	
	/**
	 * Returns if key exists.
	 * 
	 * @param key
	 * @return
	 */
	Boolean exists(String key);

	// ~  -----------------------------------------------------------------------------------------------------------

	/**
	 * Begin a unit of work and return the associated <tt>Transaction</tt>
	 * object.
	 * <p>
	 * NOTE: Once begin a new transaction object, it must be committed or
	 * aborted by invoke commit() or abort() method, to avoid resource leak.
	 * 
	 * @return
	 */
	Transaction beginTransaction();

	/**
	 * Begin a unit of work and return the associated <tt>Transaction</tt> object.<br>
	 * In a sharded cache deployment environment, only these operations upon same key can support transaction.
	 * <p>
	 * <b>NOTE:</b><br>
	 * Once begin a new transaction, it must be committed or aborted by invoking commit() or abort() method to avoid 
	 * resource leak.
	 * 
	 * @param key
	 *            cache key
	 * @return
	 */
	Transaction beginTransaction(String key);
	
	/**
	 * Time complexity: O(1) for every key.
	 * <p>
	 * Marks the given keys to be watched for conditional execution of a transaction.
	 * watch() is used to provide a check-and-set (CAS) behavior to cache transactions.
	 * Watched keys are monitored in order to detect changes against them. If at least one watched key is modified before 
	 * the transaction commit, the whole transaction aborts, and transaction commit returns a <tt>null</tt> to notify that 
	 * the transaction failed.
	 * <p>
	 * <b>NOTE:</b><br>
	 * In a sharded cache deployment environment, only single key are supported.
	 * @param keys
	 */
	void watch(String... keys);
	
	/**
	 * Flushes all the previously watched keys for a transaction.<br>
	 * If you call <code>transaction.commit()</code> or <code>transaction.abort()</code>, 
	 * there's no need to manually call <code>unwatch(key)</code>.
	 * <p>
	 * <b>NOTE:</b><br>
	 * Must have a key in a shared cache deployment environment, otherwise can be <tt>null</tt>
	 * @param key 
	 */
	void unwatch(String key);
}

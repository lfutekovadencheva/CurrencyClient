package cache;

/**
 * A simple cache interface
 * 
 * @author Luba Dencheva
 *
 * @param <T>
 */
public interface ICache<T> {
	/**
	 * Stores a new entry to the cache
	 */
	void put(String key, T value);

	/**
	 * Retrieves a value from the cache
	 */
	T get(String key);

	/**
	 * Checks if a given key is already stored in the cache
	 */
	boolean containsKey(String key);

	/**
	 * Removes an entry associated with a given key from the cache
	 */
	void remove(String key);

	/**
	 * Removes all entries from the cache
	 */
	void clear();

	/**
	 * Removes all expired cache entries
	 */
	void clean();
	
	/**
	 * Sets the expiration interval
	 */
	void setExpirationInterval(long seconds);
}

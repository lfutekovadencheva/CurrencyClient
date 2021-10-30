package cache;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A simple implementation of the ICache interface.
 * 
 * @author Luba Dencheva
 *
 */
public class SimpleCache<T> implements ICache<T> {
	private long expirationInSeconds = 5 * 60;  // 5 mins
	
	private Map<String, LocalDateTime> timeMap; 
	private Map<String, T> dataMap;
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:SSS");
		
	public SimpleCache() {
		timeMap = new ConcurrentHashMap<String, LocalDateTime>();
		dataMap = new ConcurrentHashMap<String, T>();
	}

	public SimpleCache(long expirationInSeconds) {
		this();
		
		this.expirationInSeconds = expirationInSeconds;
	}
	
	/**
	 * Puts a key - value pair in the cache. 
	 * 
	 * The method stores the key-value pair in a hashmap, and a key-timestamp pair in another hashmap.
	 * The timestamp is lately used the decision to be taken if a record in the cache is already obsolete or not 
	 */
	public void put(String key, T value) {
		if (key == null || key.isEmpty() || value == null) 
			throw new IllegalArgumentException();
		
		LocalDateTime currentDateTime = LocalDateTime.now();
 
		System.out.println("[CACHE] Inserting entry: " + FORMATTER.format(currentDateTime) + " : " + key + " : " + value);
		timeMap.put(key, currentDateTime);
		dataMap.put(key, value);
	}

	/**
	 * Returns a stored in the cache entry based on its key.
	 * 
	 * @return the stored object or null if the key doesn't exist or the entry has expired.
	 */
	public T get(String key) {
		if (key == null || key.isEmpty()) 
			throw new IllegalArgumentException();

		if(containsKey(key)) {
			System.out.println("[CACHE] Retrieving entry: " + FORMATTER.format(timeMap.get(key)) + " : " + key);

			return dataMap.get(key);
		} else {
			return null;
		}
	}

	/**
	 * Verify if the cache contains an entry based on its key. 
	 * If there is an expired entry in the cache, the method removes it and return false.
	 * 
	 * @return true, if the entry exist and didn't expire; otherwise it returns false
	 */
	public boolean containsKey(String key) {
		if (key == null || key.isEmpty()) 
			throw new IllegalArgumentException();
		
		LocalDateTime currentDateTime = LocalDateTime.now();

		LocalDateTime keyDateTime = timeMap.get(key);
	
		boolean isAvailable = false;
		if (keyDateTime != null) {
			if (Duration.between(keyDateTime, currentDateTime).toSeconds() <= expirationInSeconds) {
				isAvailable = true;
			} else {
				remove(key);
			}
		}

		if (isAvailable) {
			System.out.println("[CACHE] Found entry for key: " + key);
		} else {
			System.out.println("[CACHE] NO entry found for key: " + key);			
		}
		
		return isAvailable;
	}

	public void remove(String key) {
		if (key == null || key.isEmpty()) 
			throw new IllegalArgumentException();

		System.out.println("[CACHE] Remove entry for key: " + key);

		timeMap.remove(key);
		dataMap.remove(key);
	}

	public void clear() {
		System.out.println("[CACHE] Clear all cache entries");

		timeMap.clear();
		dataMap.clear();
	}

	public void clean() {
		System.out.println("[CACHE] Clean expired cache entries");

		LocalDateTime currentDateTime = LocalDateTime.now();

		Set<String> expiredKeys = timeMap
				.entrySet()
	            .stream()
	            .filter(entry -> (Duration.between(entry.getValue(), currentDateTime).toSeconds() > expirationInSeconds))
	            .map(Map.Entry::getKey)
	            .collect(Collectors.toSet());

		for(String keys: expiredKeys) {
			remove(keys);
		}
	}

	public void setExpirationInterval(long seconds) {
		expirationInSeconds = seconds;	
	}
}

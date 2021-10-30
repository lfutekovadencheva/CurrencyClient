package currency;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import cache.ICache;
import cache.SimpleCache;

public class CurrencyClient {
	public static final String API_KEY = "40dddfd0e8e055479f811c169416a45f";
	public static final String BASE_URI = "https://api.exchangeratesapi.io/v1";
	public static final String BASE_PATH = "/latest";

	private ICache<CurrencyRates> cacheImpl;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CurrencyClient(ICache cacheImpl) {
		this.cacheImpl = cacheImpl;
	}

	public CurrencyClient(long expirationInSeconds) {
		this(new SimpleCache<CurrencyRates>(expirationInSeconds));
	}

	public CurrencyClient() {
		this(5 * 60); // 5 minutes expiration interval
	}
	
	
	/**
	 * Returns the exchange rates of the currency.
	 * 
	 * First, the method looks for the currency exchange rates in the cache. If the currency rates are not available in the cache,
	 * the method makes a call to the REST API
	 * 
	 * @param base currency
	 * @return a CurrencyRates object, null if the rates cannot be retrieved from the cache and via API call
	 */
	public CurrencyRates getCurrencyRates(String base) throws IOException {
		if (base == null || base.isEmpty()) 
			throw new IllegalArgumentException();

		System.out.println("Request exchange rates for currency: " + base);
		
		// get exchange rates from the cache, if available
		CurrencyRates curr = cacheImpl.get(base);
		 
		// if there are no rates in the cache, request the rates via the API and store them in the cache
		if (curr == null) {
			curr = requestCurrencyRates(base);
			
			// store the new rates in the cache
			if(curr != null) {
				cacheImpl.put(base, curr);
			}
		}
		
		return curr;
	}
	
	/**
	 * Request exchange rates by an API call
	 *  
	 * @param base
	 * @return a CurrencyRates object, null if the rates cannot be retrieved via API call 
	 * @throws IOException
	 */
	private CurrencyRates requestCurrencyRates(String base) throws IOException {
		if (base == null || base.isEmpty()) 
			throw new IllegalArgumentException();
		
		System.out.println("[API] Request rates for currency: " + base);
		
		 String url = String.format("%s%s?access_key=%s&base=%s", BASE_URI, BASE_PATH, API_KEY, base);
			
		 System.out.println("[API] Requesting " + url);

		 OkHttpClient client = new OkHttpClient();
		 Request request = new Request.Builder()
	       .url(url)
	       .build();
		 	 
		 Response response = client.newCall(request).execute();
		 String resp = response.body().string();
		 
		 System.out.println("[API] Receiving " + resp);
		 
		 if (response.code() == 200) {
			 JsonObject rootObj = JsonParser.parseString(resp).getAsJsonObject();
		 
			 return (new Gson()).fromJson(rootObj, CurrencyRates.class);
		 } else {
			 return null;
		 }
	}
}

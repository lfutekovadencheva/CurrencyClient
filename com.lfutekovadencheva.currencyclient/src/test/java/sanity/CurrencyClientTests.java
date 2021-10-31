package sanity;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import cache.SimpleCache;
import currency.CurrencyClient;
import currency.CurrencyRates;

public class CurrencyClientTests {

	@BeforeAll
	/**
	 * Verify the API is available and working before start testing
	 */
	public static void checkApi() throws IOException {
		SimpleCache<CurrencyRates> cache = new SimpleCache<>(5); // 5 seconds expiration interval
		CurrencyClient cc = new CurrencyClient(cache);

		CurrencyRates ratesEuro = cc.getCurrencyRates("EUR");

		Assertions.assertNotNull(ratesEuro);
	}
	
	@Test
	/**
	 * Test steps: 
	 * * make sure there isn't EUR entry in the cache and request EUR rates 
	 * * make sure there is EUR entry already in the cache and request EUR rates once again 
	 * * both requested euroRates objects must be equal
	 */
	public void basicTests() throws IOException {
		SimpleCache<CurrencyRates> cache = new SimpleCache<>(5); // 5 seconds expiration interval
		CurrencyClient cc = new CurrencyClient(cache);

		Assertions.assertFalse(cache.containsKey("EUR"));

		CurrencyRates ratesEuro1 = cc.getCurrencyRates("EUR");

		Assertions.assertTrue(cache.containsKey("EUR"));

		CurrencyRates ratesEuro2 = cc.getCurrencyRates("EUR");

		Assertions.assertEquals(ratesEuro1, ratesEuro2);
	}

	@Test
	/**
	 * Test steps
	 * * make sure there aren't EUR and USD entries in the cache and request EUR and USD rates 
	 * * make sure there are EUR and USD entries already in the cache and request EUR and USD rates once again 
	 * * requested euroRates and usdRates objects must be equal
	 */
	public void requestDifferentBaseRatesTests() throws IOException {
		SimpleCache<CurrencyRates> cache = new SimpleCache<>(5); // 5 seconds expiration interval
		CurrencyClient cc = new CurrencyClient(cache);

		Assertions.assertFalse(cache.containsKey("EUR"));
		Assertions.assertFalse(cache.containsKey("USD"));

		CurrencyRates ratesEuro1 = cc.getCurrencyRates("EUR");
		CurrencyRates ratesUSD1 = cc.getCurrencyRates("USD");

		Assertions.assertTrue(cache.containsKey("EUR"));
		Assertions.assertTrue(cache.containsKey("USD"));

		CurrencyRates ratesEuro2 = cc.getCurrencyRates("EUR");
		CurrencyRates ratesUSD2 = cc.getCurrencyRates("USD");

		Assertions.assertEquals(ratesEuro1, ratesEuro2);
		Assertions.assertEquals(ratesUSD1, ratesUSD2);
	}

	@Test
	/**
	 * Test steps: 
	 * * use a cache with 5 seconds expiration interval
	 * * make sure there isn't EUR entry in the cache and request EUR rates 
	 * * make sure there is EUR entry already in the cache 
	 * * wait for 3sec and make sure the EUR entry is still available in the cache
	 * * wait for 3 more sec and make sure the EUR entry is NO MORE available in the cache
	 */
	public void cacheExpirationTests() throws IOException, InterruptedException {
		SimpleCache<CurrencyRates> cache = new SimpleCache<>(5); // 5 seconds expiration interval
		CurrencyClient cc = new CurrencyClient(cache);

		Assertions.assertFalse(cache.containsKey("EUR"));

		cc.getCurrencyRates("EUR");

		Assertions.assertTrue(cache.containsKey("EUR"));

		Thread.sleep(3000);
		Assertions.assertTrue(cache.containsKey("EUR"));

		Thread.sleep(3000);
		Assertions.assertFalse(cache.containsKey("EUR"));
	}

	@Test	
	/**
	 * Test steps: 
	 * * make sure there isn't EUR entry in the cache and request EUR rates 
	 * * make sure there is EUR entry already in the cache 
	 * * call immediately the cache clean() method - the EUR rate should continue existing in the cache 
	 * * wait for 6 seconds and call the cache clean() method - the EUR rate should disappear from the cache 
	 * * request EUR rates once again and immediately call the clear() method - the EUR rate should disappear from the cache 
	 */
	public void cacheCleanupTests() throws IOException, InterruptedException {
		SimpleCache<CurrencyRates> cache = new SimpleCache<>(5); // 5 seconds expiration interval
		CurrencyClient cc = new CurrencyClient(cache);

		Assertions.assertFalse(cache.containsKey("EUR"));

		cc.getCurrencyRates("EUR");
		Assertions.assertTrue(cache.containsKey("EUR"));

		cache.clean();
		Assertions.assertTrue(cache.containsKey("EUR"));

		Thread.sleep(6000);
		cache.clean();
		Assertions.assertFalse(cache.containsKey("EUR"));

		cc.getCurrencyRates("EUR");
		Assertions.assertTrue(cache.containsKey("EUR"));

		cache.clear();
		Assertions.assertFalse(cache.containsKey("EUR"));
	}
	
	@Test
	/**
	 * Test steps: 
	 * * use a cache with 5 seconds expiration interval
	 * * request EUR rates, check it goes to the cache, it's still available in 3 sec and disappears in 6 sec 
	 * * change the cache expiration interval to 2 sec
	 * * request EUR rates, check it goes to the cache and then disappears in 3 sec 
	 */
	public void changeExpirationIntervalTests() throws IOException, InterruptedException {
		SimpleCache<CurrencyRates> cache = new SimpleCache<>(5); // 5 seconds expiration interval
		CurrencyClient cc = new CurrencyClient(cache);

		cc.getCurrencyRates("EUR");
		Assertions.assertTrue(cache.containsKey("EUR"));

		Thread.sleep(3000);
		Assertions.assertTrue(cache.containsKey("EUR"));

		Thread.sleep(3000);
		Assertions.assertFalse(cache.containsKey("EUR"));
		
		cache.setExpirationInterval(2);
		cc.getCurrencyRates("EUR");
		Assertions.assertTrue(cache.containsKey("EUR"));

		Thread.sleep(3000);
		Assertions.assertFalse(cache.containsKey("EUR"));
	}

	@Test
	/**
	 * Test steps: 
	 * * request an invalid currency and make sure the system returns null but doesn't crash
	 */
	public void invalidCurrencyTests() throws IOException {
		SimpleCache<CurrencyRates> cache = new SimpleCache<>(5); // 5 seconds expiration interval
		CurrencyClient cc = new CurrencyClient(cache);

		CurrencyRates invalid = cc.getCurrencyRates("TEST");
		Assertions.assertNull(invalid);
	}

	
}

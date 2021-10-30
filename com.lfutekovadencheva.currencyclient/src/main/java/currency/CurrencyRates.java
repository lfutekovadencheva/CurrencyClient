package currency;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

/**
 * 
 * A class representation of the currency rates JSON returned by https://api.exchangeratesapi.io/v1/latest
 * 
 * @author Luba Dencheva
 *
 */
public class CurrencyRates {
	private long timestamp;
	private String base;
	private Date date;
	private HashMap<String, Double> rates;
	
	public Instant getTimestamp() {
		return Instant.ofEpochSecond(timestamp);
	}
	public String getBase() {
		return base;
	}
	public Date getDate() {
		return date;
	}
	public HashMap<String, Double> getRates() {
		return rates;
	}
	
	public String toString() {
		return String.format("%s rates: %s", base, rates);
	}	
	
	public boolean equals(CurrencyRates cc) {
	    if (cc == null) 
	    	return false;
	    
	    if (this == cc)
	    	return true;
		
	    return getTimestamp().equals(cc.getTimestamp()) && getBase().equals(cc.getBase());
	}
}

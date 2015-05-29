package de.pb92.snapshotmeter.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Meter")
public class Meter extends ParseObject {
	
	private static final String COLUMN_METER_NAME = "meterName";
	private static final String COLUMN_METER_TYPE = "meterType";
	private static final String COLUMN_PROVIDER = "provider";
	
	public String getMeterName() {
		return getString(COLUMN_METER_NAME);
	}
	
	public String getMeterType() {
		return getString(COLUMN_METER_TYPE);
	}
	
	public String getProvider() {
		return getString(COLUMN_PROVIDER);
	}
	
	public void setMeterName(String meterName) {
		put(COLUMN_METER_NAME, meterName);
	}
	
	public void setMeterType(String meterType) {
		put(COLUMN_METER_TYPE, meterType);
	}
	
	public void setProvider(String provider) {
		put(COLUMN_PROVIDER, provider);
	}
	
	public static ParseQuery<Meter> getQuery() {
		return ParseQuery.getQuery(Meter.class);
	}
	
}

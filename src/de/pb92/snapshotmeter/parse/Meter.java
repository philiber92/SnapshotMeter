package de.pb92.snapshotmeter.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Meter")
public class Meter extends ParseObject {
	
	public static final String COLUMN_METER_NAME = "meterName";
	public static final String COLUMN_METER_NUMBER = "meterNumber";
	public static final String COLUMN_METER_TYPE = "meterType";
	public static final String COLUMN_PROVIDER = "provider";
	
	public String getMeterName() {
		return getString(COLUMN_METER_NAME);
	}
	
	public long getMeterNumber() {
		return getLong(COLUMN_METER_NUMBER);
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
	
	public void setMeterNumber(long number) {
		put(COLUMN_METER_NUMBER, number);
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

package de.pb92.snapshotmeter.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("MeterReading")
public class MeterReading extends ParseObject {
	
	public static final String COLUMN_VALUE = "value";
	public static final String COLUMN_METER_NUMBER = "meterNumber";
	public static final String COLUMN_LAST_NAME = "lastName";
	public static final String COLUMN_PROVIDER = "provider";
	public static final String COLUMN_TRANSMITTED = "transmitted";
	
	public long getValue() {
		return getLong(COLUMN_VALUE);
	}
	
	public long getMeterNumber() {
		return getLong(COLUMN_METER_NUMBER);
	}
	
	public String getLastName() {
		return getString(COLUMN_LAST_NAME);
	}
	
	public String getProvider() {
		return getString(COLUMN_PROVIDER);
	}
	
	public boolean isTransmitted() {
		return getBoolean(COLUMN_TRANSMITTED);
	}
	
	public void setValue(long value) {
		put(COLUMN_VALUE, value);
	}
	
	public void setMeterNumber(long number) {
		put(COLUMN_METER_NUMBER, number);
	}
	
	public void setLastName(String lastName) {
		put(COLUMN_LAST_NAME, lastName);
	}
	
	public void setProvider(String provider) {
		put(COLUMN_PROVIDER, provider);
	}
	
	public void setTransmitted(boolean transmitted) {
		put(COLUMN_TRANSMITTED, transmitted);
	}
	
	public static ParseQuery<MeterReading> getQuery() {
		return ParseQuery.getQuery(MeterReading.class);
	}

}

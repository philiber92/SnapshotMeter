package de.pb92.snapshotmeter.parse;

import java.util.UUID;

import android.content.Context;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import de.pb92.snapshotmeter.R;

@ParseClassName("Meter")
public class Meter extends ParseObject {
	
	public static final String COLUMN_METER_NAME = "meterName";
	public static final String COLUMN_METER_NUMBER = "meterNumber";
	public static final String COLUMN_METER_TYPE = "meterType";
	public static final String COLUMN_PROVIDER = "provider";
	public static final String COLUMN_ID = "uuid";
	
	public String getMeterUnit(Context context) {
		String water   = context.getString(R.string.meter_reading_unit_water);
		String gas     = context.getString(R.string.meter_reading_unit_gas);
		String current = context.getString(R.string.meter_reading_unit_current);
		String heating = context.getString(R.string.meter_reading_unit_heating);
		
		String meterType = this.getMeterType();
		String unit;
		if(meterType.equals("Strom")) {
			unit = current;
		} else if(meterType.equals("Wasser")) {
			unit = water;
		} else if(meterType.equals("Gas")) {
			unit = gas;
		} else {
			unit = heating;
		}
		return unit;
	}
	
	public String getMeterName() {
		return getString(COLUMN_METER_NAME);
	}
	
	public String getID() {
		return getString(COLUMN_ID);
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
	
	public void setID() {
		put(COLUMN_ID, UUID.randomUUID().toString());
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

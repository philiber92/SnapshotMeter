package de.pb92.snapshotmeter.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Settings")
public class Settings extends ParseObject{
	
	private static final String COLUMN_LAST_NAME = "lastName";
	
	public String getLastName() {
		return getString(COLUMN_LAST_NAME);
	}
	
	public void setLastName(String lastName) {
		put(COLUMN_LAST_NAME, lastName);
	}
	
	public static ParseQuery<Settings> getQuery() {
		return ParseQuery.getQuery(Settings.class);
	}
	
}

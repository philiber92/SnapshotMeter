package de.pb92.snapshotmeter.parse;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Settings")
public class Settings extends ParseObject{
	
	public static final String COLUMN_LAST_NAME = "lastName";
	
	public String getLastName() {
		return getString(COLUMN_LAST_NAME);
	}
	
	public void setLastName(String lastName) {
		put(COLUMN_LAST_NAME, lastName);
	}
	
	public static ParseQuery<Settings> getQuery() {
		return ParseQuery.getQuery(Settings.class);
	}
	
	public static String getLastNameByQuery() {
		ParseQuery<Settings> query = getQuery();
		query.fromLocalDatastore();
		List<Settings> result;
		try {
			result = query.find();
		} catch (ParseException e) {
			result = new ArrayList<Settings>();
		}
		if(!result.isEmpty()) {
			return result.get(0).getLastName();
		}
		return null;
	}
	
}

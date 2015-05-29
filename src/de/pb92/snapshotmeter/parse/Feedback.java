package de.pb92.snapshotmeter.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Feedback")
public class Feedback extends ParseObject {
	
	public static final String COLUMN_RATING = "rating";
	public static final String COLUMN_READING_SUCCESS = "readingSuccess";
	public static final String COLUMN_USABILITY = "usability";
	public static final String COLUMN_DESIRE = "desire";
	public static final String COLUMN_PROVIDER = "provider";
	public static final String COLUMN_COMMENT = "comment";
	
	public float getRating() {
		return (Float) getNumber(COLUMN_RATING);
	}
	
	public boolean getReadingSuccess() {
		return getBoolean(COLUMN_READING_SUCCESS);
	}
	
	public String getUsability() {
		return getString(COLUMN_USABILITY);
	}
	
	public boolean getDesire() {
		return getBoolean(COLUMN_DESIRE);
	}
	
	public String getProvider() {
		return getString(COLUMN_PROVIDER);
	}
	
	public String getComment() {
		return getString(COLUMN_COMMENT);
	}
	
	public void setRating(float rating) {
		put(COLUMN_RATING, rating);
	}
	
	public void setReadingSuccess(boolean success) {
		put(COLUMN_READING_SUCCESS, success);
	}
	
	public void setUsability(String usability) {
		put(COLUMN_USABILITY, usability);
	}
	
	public void setDesire(boolean desire) {
		put(COLUMN_DESIRE, desire);
	}
	
	public void setProvider(String provider) {
		put(COLUMN_PROVIDER, provider);
	}
	
	public void setComment(String comment) {
		put(COLUMN_COMMENT, comment);
	}
	
	public static ParseQuery<Feedback> getQuery() {
		return ParseQuery.getQuery(Feedback.class);
	}
	
}

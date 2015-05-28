package de.pb92.snapshotmeter;

import com.parse.Parse;

import android.app.Application;

public class SnapshotMeter extends Application{
	
	@Override
	public void onCreate() {
		super.onCreate();
		Parse.enableLocalDatastore(getApplicationContext());
		// enable parse
		Parse.initialize(this, "gjAeDkcZxwcYIUcTRgjwb9S1zr9guqCbHsncD2SW", 
				"F3LZazEuN0XEUOlQVRIxeCTjvnonWO00PO0lleJb");
	}
	
}

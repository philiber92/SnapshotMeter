package de.pb92.snapshotmeter;

import com.parse.Parse;
import com.parse.ParseObject;

import de.pb92.snapshotmeter.parse.Feedback;
import de.pb92.snapshotmeter.parse.Meter;
import de.pb92.snapshotmeter.parse.MeterReading;
import de.pb92.snapshotmeter.parse.Settings;

import android.app.Application;

public class SnapshotMeter extends Application{
	
	@Override
	public void onCreate() {
		super.onCreate();
		ParseObject.registerSubclass(Feedback.class);
		ParseObject.registerSubclass(MeterReading.class);
		ParseObject.registerSubclass(Meter.class);
		ParseObject.registerSubclass(Settings.class);
		Parse.enableLocalDatastore(getApplicationContext());
		Parse.initialize(this, "gjAeDkcZxwcYIUcTRgjwb9S1zr9guqCbHsncD2SW", 
				"F3LZazEuN0XEUOlQVRIxeCTjvnonWO00PO0lleJb");
	}
	
}

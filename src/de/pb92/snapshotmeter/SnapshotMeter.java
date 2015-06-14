package de.pb92.snapshotmeter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import de.pb92.snapshotmeter.parse.Feedback;
import de.pb92.snapshotmeter.parse.Meter;
import de.pb92.snapshotmeter.parse.MeterReading;
import de.pb92.snapshotmeter.parse.Settings;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

public class SnapshotMeter extends Application{
	
	private static final String TESS_TRAINED_DATA = "tessdata/eng.traineddata";
	static String tessPath;
	
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
		ParsePush.subscribeInBackground("general", new SaveCallback() {
			  @Override
			  public void done(ParseException e) {
			    if (e == null) {
			      Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
			    } else {
			      Log.e("com.parse.push", "failed to subscribe for push", e);
			    }
			  }
		});
		writeTessTrainedData();
//		try {
//			MeterReading.unpinAll();
//			Settings.unpinAll();
//			Meter.unpinAll();
//		} catch(ParseException e) {
//			//ignore
//		}
	}
	
	public static boolean isNetworkOnline(Context context) {
		boolean online = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(0);
			if(netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
				online = true;
			} else {
				netInfo = cm.getNetworkInfo(1);
				if(netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
					online = true;
				}
			}
		} catch(Exception e) {
			return false;
		}
		return online;
	}
	
	private void writeTessTrainedData() {
		String smpath = Environment.getExternalStorageDirectory().toString() + "/SnapshotMeter/";
		
		Log.w("test", smpath);
		
		String[] paths = new String[] { smpath, smpath + "tessdata/" };
		
		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					return;
				}
			}
		}
		
		
		File tessFile = new File(smpath + "tessdata/eng.traineddata");
		if (!tessFile.exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open(TESS_TRAINED_DATA);
				//GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(smpath + TESS_TRAINED_DATA);
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
				tessPath = smpath;
			} catch (Exception e) {
				tessPath = null;
			}
		} else {
			tessPath = smpath;
		}
	}
	
	public static String getTessPath() {
		return tessPath;
	}
	
	
	
}

package de.pb92.snapshotmeter;

import java.util.Date;
import java.util.List;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import de.pb92.snapshotmeter.parse.Meter;
import de.pb92.snapshotmeter.parse.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MeterReading extends ActionBarActivity {
	
	String objectID;
	String idLocally = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meter_reading);
		Intent intent = getIntent();
		objectID = intent.getStringExtra(MainMenu.EXTRA_METER_ID);
		String value = intent.getStringExtra(CameraSnapshot.EXTRA_METER_VALUE);
		if(value != null) {
			EditText editText = (EditText) findViewById(R.id.editText1);
			editText.setText(value);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.meter_reading, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void abort(View view) {
		Intent intent = new Intent(getBaseContext(), MeterReadingOverview.class);
		intent.putExtra(MainMenu.EXTRA_METER_ID, objectID);
		startActivity(intent);
	}
	
	public void saveValue(View view) {
		if(saveLocally(1) == true) {
			Toast.makeText(getBaseContext(), getString(R.string.meter_reading_normal_save_success), Toast.LENGTH_LONG)
				 .show();
			abort(view);
		}
	}
	
	
	public void sendValue(final View view) {
		if(!SnapshotMeter.isNetworkOnline(getBaseContext())) {
			Toast.makeText(getBaseContext(), getString(R.string.meter_no_internet), Toast.LENGTH_LONG)
				 .show();
			return;
		}
		
		if(saveLocally(2) == true) {
			EditText valueField = (EditText) findViewById(R.id.editText1);
			long value = Long.parseLong(valueField.getText().toString().trim());
			
			String lastName = Settings.getLastNameByQuery();
			ParseQuery<Meter> query = Meter.getQuery();
			query.fromLocalDatastore();
			query.whereEqualTo(Meter.COLUMN_ID, objectID);
			Meter meter = null;
			try {
				List<Meter> results = query.find();
				meter = results.get(0);
			} catch (ParseException e) {
				//ignore
			}
			
			de.pb92.snapshotmeter.parse.MeterReading meterReading = 
					new de.pb92.snapshotmeter.parse.MeterReading();
			meterReading.setLastName(lastName);
			meterReading.setMeterNumber(meter.getMeterNumber());
			meterReading.setProvider(meter.getProvider());
			meterReading.setTransmitted(true);
			meterReading.setValue(value);
			ParseACL acl = new ParseACL();
			acl.setPublicReadAccess(false);
			acl.setPublicWriteAccess(false);
			meterReading.setACL(acl);
			
			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.show();
			
			meterReading.saveInBackground(new SaveCallback() {
				
				@Override
				public void done(ParseException arg0) {
					dialog.dismiss();
					if(arg0 == null) {
						ParseQuery<de.pb92.snapshotmeter.parse.MeterReading> query = 
								de.pb92.snapshotmeter.parse.MeterReading.getQuery();
						query.fromLocalDatastore();
						query.whereEqualTo(Meter.COLUMN_ID, idLocally);
						List<de.pb92.snapshotmeter.parse.MeterReading> results;
						try {
							results = query.find();
							de.pb92.snapshotmeter.parse.MeterReading meterReading = results.get(0);
							meterReading.setTransmitted(true);
							meterReading.pin();
						} catch (ParseException e) {
							//ignore
						}
						Toast.makeText(getBaseContext(), getString(R.string.meter_reading_normal_send_success), Toast.LENGTH_LONG)
						 .show();
						abort(view);
					} else {
						Toast.makeText(getBaseContext(), getString(R.string.meter_reading_normal_error_save), Toast.LENGTH_LONG)
							 .show();
					}
				}
			});
		}
	}
	
	
	private boolean saveLocally(int type) {
		EditText valueField = (EditText) findViewById(R.id.editText1);
		String valueText = valueField.getText().toString().trim();
		
		if(valueText.isEmpty()) {
			Toast.makeText(getBaseContext(), getString(R.string.meter_reading_normal_no_value), Toast.LENGTH_LONG)
				 .show();
			return false;
		}
		
		long value = Long.parseLong(valueText);
		String lastName = Settings.getLastNameByQuery();
		
		if(lastName == null) {
			NoLastNameDialogFragment dialog = new NoLastNameDialogFragment(type);
			dialog.show(getSupportFragmentManager(), "last name dialog");
			return false;
		}
		
		ParseQuery<Meter> query = Meter.getQuery();
		query.fromLocalDatastore();
		query.whereEqualTo(Meter.COLUMN_ID, objectID);
		Meter meter;
		try {
			List<Meter> results = query.find();
			meter = results.get(0);
			de.pb92.snapshotmeter.parse.MeterReading meterReading = 
					new de.pb92.snapshotmeter.parse.MeterReading();
			meterReading.setLastName(lastName);
			meterReading.setMeterNumber(meter.getMeterNumber());
			meterReading.setProvider(meter.getProvider());
			meterReading.setTransmitted(false);
			meterReading.setID();
			meterReading.setReadingDate(new Date());
			idLocally = meterReading.getID();
			meterReading.setValue(value);
			meterReading.pin();
		} catch (ParseException e) {
			Toast.makeText(getBaseContext(), getString(R.string.meter_reading_normal_error_save), Toast.LENGTH_LONG)
				 .show();
			return false;
		}
		return true;
	}
	
	class NoLastNameDialogFragment extends DialogFragment {
		
		int type;
		
		public NoLastNameDialogFragment(int type) {
			this.type = type;
		}
		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    	final EditText input = new EditText(getActivity());
	    	builder.setTitle(getString(R.string.meter_reading_quick_prompt_title))
	    		   .setMessage(getString(R.string.meter_reading_quick_prompt))
	    		   .setPositiveButton(getString(R.string.meter_reading_quick_prompt_ok), 
	    				   new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String lastName = input.getText().toString().trim();
						if(!lastName.isEmpty()) {
							Settings settings = new Settings();
							settings.setLastName(lastName);
							try {
								settings.pin();
							} catch (ParseException e) {
								//error
							}
							if(type == 1) {
								saveValue(getView());
							} else {
								sendValue(getView());
							}
							dialog.dismiss();
						} else {
							Toast.makeText(getBaseContext(), getString(R.string.meter_reading_quick_no_lastname), Toast.LENGTH_LONG)
						     	 .show();
						}
					}
				})
				   .setNegativeButton(getString(R.string.meter_reading_quick_prompt_abort), 
						   new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
					.setView(input);
	    	return builder.create();
	    }
	}
}

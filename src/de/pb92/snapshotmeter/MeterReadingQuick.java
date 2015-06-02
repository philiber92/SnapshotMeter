package de.pb92.snapshotmeter;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.SaveCallback;

import de.pb92.snapshotmeter.parse.MeterReading;
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
import android.widget.Spinner;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MeterReadingQuick extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meter_reading_quick);
		Intent intent = getIntent();
		String value = intent.getStringExtra(CameraSnapshot.EXTRA_METER_VALUE);
		if(value != null) {
			EditText editText = (EditText) findViewById(R.id.editText2);
			editText.setText(value);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.meter_reading_quick, menu);
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
		Intent intent = new Intent(getBaseContext(), MainMenu.class);
		startActivity(intent);
	}
	
	public void sendValue(View view) {
		if(!SnapshotMeter.isNetworkOnline(getBaseContext())) {
			Toast.makeText(getBaseContext(), getString(R.string.meter_no_internet), Toast.LENGTH_LONG)
				 .show();
			return;
		}
		
		EditText numberField = (EditText) findViewById(R.id.editText1);
		Spinner providerField = (Spinner) findViewById(R.id.spinner1);
		EditText valueField = (EditText) findViewById(R.id.editText2);
		
		String numberText = numberField.getText().toString().trim();
		String valueText = valueField.getText().toString().trim();
		
		if(numberText.isEmpty() || valueText.isEmpty()) {
			Toast.makeText(getBaseContext(), R.string.meter_reading_quick_no_input, Toast.LENGTH_LONG)
				 .show();
			return;
		}
		
		String lastName = Settings.getLastNameByQuery();
		
		if(lastName == null) {
			NoLastNameDialogFragment dialog = new NoLastNameDialogFragment();
			dialog.show(getSupportFragmentManager(), "last name dialog");
			return;
		}
		
		Long number = Long.parseLong(numberText);
		Long value = Long.parseLong(valueText);
		String provider = providerField.getSelectedItem().toString();
		
		MeterReading mReading = new MeterReading();
		mReading.setLastName(lastName);
		mReading.setMeterNumber(number);
		mReading.setProvider(provider);
		mReading.setValue(value);
		mReading.setTransmitted(true);
		ParseACL acl = new ParseACL();
		acl.setPublicReadAccess(false);
		acl.setPublicWriteAccess(false);
		mReading.setACL(acl);
		
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.show();
		
		mReading.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException arg0) {
				dialog.dismiss();
				if(arg0 == null) {
					Toast.makeText(getBaseContext(), getString(R.string.meter_reading_quick_success), Toast.LENGTH_LONG)
						 .show();
					Intent intent = new Intent(getBaseContext(), MainMenu.class);
					startActivity(intent);
				} else {
					Toast.makeText(getBaseContext(), getString(R.string.meter_reading_quick_failure), Toast.LENGTH_LONG)
					     .show();
				}
			}
		});
		
	}
	
	class NoLastNameDialogFragment extends DialogFragment {
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
							sendValue(getView());
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

package de.pb92.snapshotmeter;

import java.text.SimpleDateFormat;
import java.util.List;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseQuery;

import de.pb92.snapshotmeter.parse.Meter;
import de.pb92.snapshotmeter.parse.MeterReading;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
@SuppressWarnings("deprecation")
public class MeterDetail extends ActionBarActivity {
	
	String valueID;
	String meterID;

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meter_detail);
		Intent intent = getIntent();
		valueID = intent.getStringExtra(MeterReadingOverview.EXTRA_METER_VALUE_ID);
		meterID = intent.getStringExtra(MeterReadingOverview.EXTRA_METER_ID);
		
		if(valueID == null || meterID == null) {
			try {
				this.finalize();
				this.finish();
			} catch (Throwable e) {
				//ignore
			}
		}
		
		ParseQuery<MeterReading> query = MeterReading.getQuery();
		ParseQuery<Meter> query2 = Meter.getQuery();
		query.fromLocalDatastore();
		query2.fromLocalDatastore();
		query.whereEqualTo(MeterReading.COLUMN_ID, valueID);
		query2.whereEqualTo(Meter.COLUMN_ID, meterID);
		MeterReading meterReading = null;
		Meter meter = null;
		try {
			List<MeterReading> results = query.find();
			List<Meter> results2 = query2.find();
			meterReading = results.get(0);
			meter = results2.get(0);
		} catch (ParseException e) {
			abort(findViewById(R.layout.activity_meter_detail).getRootView());
		}
		
		if(meterReading.isTransmitted()) {
			Button sendButton = (Button) findViewById(R.id.sendButton);
			sendButton.setText(getString(R.string.meter_reading_sent));
			sendButton.setEnabled(false);
		}
		
		TextView valueField = (TextView) findViewById(R.id.meterValue);
		TextView unitField = (TextView) findViewById(R.id.meterUnit);
		TextView descriptionField = (TextView) findViewById(R.id.meterDescription);
		TextView numberField = (TextView) findViewById(R.id.meterNumber);
		TextView dateField = (TextView) findViewById(R.id.meterDate);
		
		valueField.setText(String.valueOf(meterReading.getValue()));
		descriptionField.setText(meter.getMeterName());
		numberField.setText(String.valueOf(meterReading.getMeterNumber()));
		unitField.setText(meter.getMeterUnit(this));
		
		SimpleDateFormat day = new SimpleDateFormat("dd.MM.yyyy");
		SimpleDateFormat clock = new SimpleDateFormat("HH:mm");
		dateField.setText(day.format(meterReading.getReadingDate()) + " um " + clock.format(meterReading.getReadingDate()));	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.meter_detail, menu);
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
		intent.putExtra(MeterReadingOverview.EXTRA_METER_ID, meterID);
		startActivity(intent);
	}
	
	public void deleteValue(View view) {
		DeleteDialogFragment deleteDialog = new DeleteDialogFragment();
		deleteDialog.show(getSupportFragmentManager(), "delete dialog");
	}
	
	public void sendValue(View view) {
		SendDialogFragment sendDialog = new SendDialogFragment();
		sendDialog.show(getSupportFragmentManager(), "send dialog");
	}
	
	class DeleteDialogFragment extends DialogFragment {
		
		@Override
		@NonNull
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(getString(R.string.meter_detail_prompt_delete_title))
				   .setMessage(getString(R.string.meter_detail_prompt_delete_message))
				   .setPositiveButton(getString(R.string.meter_detail_prompt_delete_yes), 
						   new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ParseQuery<MeterReading> query = MeterReading.getQuery();
						query.fromLocalDatastore();
						query.whereEqualTo(MeterReading.COLUMN_ID, valueID);
						try {
							List<MeterReading> results = query.find();
							MeterReading meterReading = results.get(0);
							meterReading.unpin();
						} catch (ParseException e) {
							Toast.makeText(getBaseContext(), getString(R.string.meter_detail_prompt_delete_failure), Toast.LENGTH_LONG)
								 .show();
							dialog.dismiss();
							return;
						}
						Toast.makeText(getBaseContext(), getString(R.string.meter_detail_prompt_delete_success), Toast.LENGTH_LONG)
							 .show();
						Intent intent = new Intent(getBaseContext(), MeterReadingOverview.class);
						intent.putExtra(MeterReadingOverview.EXTRA_METER_ID, meterID);
						startActivity(intent);
					}
				})
				  .setNegativeButton(getString(R.string.meter_detail_prompt_delete_no), 
						  new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
			
			return builder.create();
		}
	}
	
	class SendDialogFragment extends DialogFragment {
		@Override
		@NonNull
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(getString(R.string.meter_detail_prompt_send_title))
				   .setMessage(getString(R.string.meter_detail_prompt_send_message))
				   .setPositiveButton(getString(R.string.meter_detail_prompt_send_yes), 
						   new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ParseQuery<MeterReading> query = MeterReading.getQuery();
						query.fromLocalDatastore();
						query.whereEqualTo(MeterReading.COLUMN_ID, valueID);
						MeterReading meterReading = null;
						boolean error = false;
						try {
							List<MeterReading> results = query.find();
							meterReading = results.get(0);
						} catch (ParseException e) {
							error = true;
						}
						
						if(error == true || meterReading.isTransmitted()) {
							Toast.makeText(getBaseContext(), getString(R.string.meter_detail_prompt_send_failure), 
									Toast.LENGTH_LONG).show();
							dialog.dismiss();
							return;
						}
						
						final ProgressDialog pd = new ProgressDialog(getActivity());
						pd.show();
						
						MeterReading server = new MeterReading();
						server.setLastName(meterReading.getLastName());
						server.setMeterNumber(meterReading.getMeterNumber());
						server.setProvider(meterReading.getProvider());
						server.setReadingDate(meterReading.getReadingDate());
						server.setTransmitted(true);
						server.setValue(meterReading.getValue());
						ParseACL acl = new ParseACL();
						acl.setPublicReadAccess(false);
						acl.setPublicWriteAccess(false);
						server.setACL(acl);
						
						try {
							meterReading.setTransmitted(true);
							meterReading.pin();
							server.save();
							Toast.makeText(getBaseContext(), getString(R.string.meter_detail_prompt_send_success), 
									Toast.LENGTH_LONG).show();
							pd.dismiss();
							Intent intent = new Intent(getBaseContext(), MeterReadingOverview.class);
							intent.putExtra(MeterReadingOverview.EXTRA_METER_ID, meterID);
							startActivity(intent);
						} catch (ParseException e) {
							Toast.makeText(getBaseContext(), getString(R.string.meter_detail_prompt_send_failure), 
									Toast.LENGTH_LONG).show();
							pd.dismiss();
						}
					}
				})
					.setNegativeButton(getString(R.string.meter_detail_prompt_send_no), 
							new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			return builder.create();
		}
	}
}

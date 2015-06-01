package de.pb92.snapshotmeter;


import com.parse.ParseException;
import com.parse.ParseQuery;

import de.pb92.snapshotmeter.parse.Meter;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MeterReadingOverview extends ActionBarActivity {
	
	public static final String EXTRA_METER_VALUE_ID = "de.pb92.snapshotmeter.METER_VALUE";
	public static final String EXTRA_METER_ID = "de.pb92.snapshotmeter.METER";
	
	String objectID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meter_reading_overview);
		Intent intent = getIntent();
		objectID = intent.getStringExtra(MainMenu.EXTRA_METER_ID);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.meter_reading_overview, menu);
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
	
	public void goBack(View view) {
		Intent intent = new Intent(getBaseContext(), MainMenu.class);
		startActivity(intent);
	}
	
	public void deleteMeter(View view) {
		DeleteMeterDialog deleteDialog = new DeleteMeterDialog(objectID);
		deleteDialog.show(getSupportFragmentManager(), "deleteMeterDialog");
	}
	
	class DeleteMeterDialog extends DialogFragment {
		
		String objectID;
		
		public DeleteMeterDialog(String objectID) {
			this.objectID = objectID;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.meter_reading_dialog_title)
			       .setMessage(R.string.meter_reading_dialog_description)
			       .setNegativeButton(R.string.meter_reading_dialog_abort, 
			    		   new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//ignore
				}
			});
			builder.setPositiveButton(R.string.meter_reading_dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ParseQuery<Meter> query = Meter.getQuery();
					query.fromLocalDatastore();
					try {
						Meter result = query.get(objectID);
						result.unpin();
					} catch (ParseException e) {
						Toast.makeText(getBaseContext(), R.string.meter_reading_dialog_error, Toast.LENGTH_LONG)
							 .show();
						return;
					}
					Toast.makeText(getBaseContext(), R.string.meter_reading_dialog_success, Toast.LENGTH_LONG)
						 .show();
					Intent intent = new Intent(getBaseContext(), MainMenu.class);
					startActivity(intent);
				}
			});
			return builder.create();
		}
		
	}
}

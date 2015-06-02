package de.pb92.snapshotmeter;


import java.util.ArrayList;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseQuery;

import de.pb92.snapshotmeter.adapter.MeterReadingListAdapter;
import de.pb92.snapshotmeter.parse.Meter;
import de.pb92.snapshotmeter.parse.MeterReading;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

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
		
		if(objectID == null) {
			Intent startMain = new Intent(getBaseContext(), MainMenu.class);
			startActivity(startMain);
		}
		
		ParseQuery<Meter> meter = Meter.getQuery();
		meter.fromLocalDatastore();
		meter.whereEqualTo(Meter.COLUMN_ID, objectID);
		long number = 0;
		try {
			List<Meter> results = meter.find();
			number = results.get(0).getMeterNumber();
			
		} catch (ParseException e1) {
			this.finish();
		}
		
		ParseQuery<MeterReading> query = MeterReading.getQuery();
		query.fromLocalDatastore();
		query.whereEqualTo(Meter.COLUMN_METER_NUMBER, number);
		List<MeterReading> queryList;
		
		try {
			queryList = query.find();
		} catch (ParseException e) {
			queryList = new ArrayList<MeterReading>();
		}
		
		MeterReadingListAdapter listAdapter = new MeterReadingListAdapter(this, new ArrayList<MeterReading>(queryList));
		ListView listView = (ListView)findViewById(R.id.listView1);
		listView.setAdapter(listAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Button show = (Button) view.findViewById(R.id.meterShow);
				show.setVisibility(View.VISIBLE);
			}
		});
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
		if (id == R.id.action_property) {
			ParseQuery<Meter> query = Meter.getQuery();
			query.fromLocalDatastore();
			query.whereEqualTo(Meter.COLUMN_ID, objectID);
			try {
				List<Meter> result = query.find();
				PropertyDialogFragment pd = new PropertyDialogFragment(result.get(0));
				pd.show(getSupportFragmentManager(), "property dialog");
			} catch (ParseException e) {
				//ignore
			}
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
					query.whereEqualTo(Meter.COLUMN_ID, objectID);
					long meterNumber;
					
					try {
						List<Meter> results = query.find();
						Meter result = results.get(0);
						meterNumber = result.getMeterNumber();
						result.unpin();
					} catch (ParseException e) {
						Toast.makeText(getBaseContext(), R.string.meter_reading_dialog_error, Toast.LENGTH_LONG)
							 .show();
						return;
					}
					
					ParseQuery<MeterReading> mquery = MeterReading.getQuery();
					mquery.fromLocalDatastore();
					mquery.whereEqualTo(MeterReading.COLUMN_METER_NUMBER, meterNumber);
					
					try {
						List<MeterReading> results = mquery.find();
						MeterReading.unpinAll(results);
					} catch(ParseException e) {
						//ignore
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
	
	class PropertyDialogFragment extends DialogFragment {
		
		Meter meter;
		
		public PropertyDialogFragment(Meter meter) {
			this.meter = meter;
		}
		
		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	TextView numberText = new TextView(getActivity());
	    	TextView number = new TextView(getActivity());
	    	TextView providerText = new TextView(getActivity());
	    	TextView provider = new TextView(getActivity());
	    	
	    	numberText.setText(getString(R.string.meter_reading_overview_property_number));
	    	number.setText(String.valueOf(meter.getMeterNumber()));
	    	providerText.setText(getString(R.string.meter_reading_overview_property_provider));
	    	provider.setText(meter.getProvider());
	    	
	    	number.setTextColor(getResources().getColor(R.color.black));
	    	provider.setTextColor(getResources().getColor(R.color.black));
	    	
	    	RelativeLayout rl = new RelativeLayout(getBaseContext());
	    	rl.addView(numberText);
	    	rl.addView(number);
	    	rl.addView(providerText);
	    	rl.addView(provider);
	    	
	    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    	builder.setTitle(getString(R.string.meter_reading_overview_property_title))
	    		   .setView(rl)
	    		   .setPositiveButton(R.string.meter_reading_overview_property_ok, 
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

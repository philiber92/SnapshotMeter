package de.pb92.snapshotmeter;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseQuery;

import de.pb92.snapshotmeter.parse.Meter;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MeterAdd extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meter_add);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.meter_add, menu);
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
	
	public void addMeter(View view) {
		EditText editText1 = (EditText)findViewById(R.id.editText1);
		EditText editText2 = (EditText)findViewById(R.id.editText2);
		Spinner spinner1 = (Spinner)findViewById(R.id.spinner1);
		Spinner spinner2 = (Spinner)findViewById(R.id.spinner2);
		
		String name = editText1.getText().toString().trim();
		String numberText = editText2.getText().toString().trim();
		long number = Long.parseLong(numberText);
		String type = String.valueOf(spinner1.getSelectedItem());
		String provider = String.valueOf(spinner2.getSelectedItem());
		
		if(name.isEmpty() || numberText.isEmpty()) {
			Toast.makeText(getBaseContext(), R.string.meter_add_no_name, Toast.LENGTH_LONG)
				 .show();
			return;
		}
		
		ParseQuery<Meter> query = Meter.getQuery();
		query.fromLocalDatastore();
		query.whereEqualTo(Meter.COLUMN_METER_NUMBER, number);
		List<Meter> result;
		try {
			result = query.find();
		} catch (ParseException e) {
			// not found
			result = new ArrayList<Meter>();
		}
		
		
		//meter already exists
		if(!result.isEmpty()) {
			Toast.makeText(getBaseContext(), R.string.meter_add_number_exists, Toast.LENGTH_LONG)
			 .show();
			return;
		}
		
		Meter meter = new Meter();
		meter.setMeterName(name);
		meter.setMeterNumber(number);
		meter.setMeterType(type);
		meter.setID();
		meter.setProvider(provider);
		meter.pinInBackground();
		
		Toast.makeText(getBaseContext(), R.string.meter_add_success, Toast.LENGTH_LONG)
			 .show();
		Intent intent = new Intent(getBaseContext(), MainMenu.class);
		startActivity(intent);
	}
	
	public void abbort(View view) {
		this.finish();
	}
}

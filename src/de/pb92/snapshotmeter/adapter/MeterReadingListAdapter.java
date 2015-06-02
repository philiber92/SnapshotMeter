package de.pb92.snapshotmeter.adapter;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseQuery;

import android.content.Intent;

import de.pb92.snapshotmeter.MainMenu;
import de.pb92.snapshotmeter.MeterDetail;
import de.pb92.snapshotmeter.MeterReadingOverview;
import de.pb92.snapshotmeter.R;
import de.pb92.snapshotmeter.parse.Meter;
import de.pb92.snapshotmeter.parse.MeterReading;

import android.content.Context;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

public class MeterReadingListAdapter extends BaseAdapter implements ListAdapter{

	ArrayList<MeterReading> meterReading;
	Context context;
	String unit;
	String objectID;
	
	public MeterReadingListAdapter(Context context, ArrayList<MeterReading> meterReading) {
		this.context = context;
		this.meterReading = meterReading;
		this.unit = "";
		
		Intent intent = ((Activity) context).getIntent();
		objectID = intent.getStringExtra(MainMenu.EXTRA_METER_ID);
		
		try {
			ParseQuery<Meter> query = Meter.getQuery();
			query.fromLocalDatastore();
			query.whereEqualTo(Meter.COLUMN_ID, objectID);
			List<Meter> result = query.find();
			unit = result.get(0).getMeterUnit(context);
		} catch (ParseException e) {
			unit = context.getString(R.string.meter_reading_unit_current);
		}
	}
	
	@Override
	public int getCount() {
		return meterReading.size();
	}

	@Override
	public Object getItem(int position) {
		return meterReading.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		View view = convertView;
		if(view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.meter_reading_list_layout, null);
		}
		
		TextView listItemText = (TextView) view.findViewById(R.id.meterItem);
		listItemText.setText(Long.toString(meterReading.get(position).getValue()) + " " + unit);
		
		Button showButton = (Button) view.findViewById(R.id.meterShow);
		
		showButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(parent.getContext(), MeterDetail.class);
				intent.putExtra(MeterReadingOverview.EXTRA_METER_VALUE_ID, 
									meterReading.get(position).getID());
				intent.putExtra(MeterReadingOverview.EXTRA_METER_ID, objectID);
				parent.getContext().startActivity(intent);		
			}
		});
		
		return view;
	}

}

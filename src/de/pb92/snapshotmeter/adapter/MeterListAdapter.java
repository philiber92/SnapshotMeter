package de.pb92.snapshotmeter.adapter;

import java.util.ArrayList;
import java.util.List;

import de.pb92.snapshotmeter.CameraSnapshot;
import de.pb92.snapshotmeter.MainMenu;
import de.pb92.snapshotmeter.MeterReadingOverview;
import de.pb92.snapshotmeter.R;
import de.pb92.snapshotmeter.parse.Meter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

public class MeterListAdapter extends BaseAdapter implements ListAdapter {

	private ArrayList<Meter> list = new ArrayList<Meter>();
	private Context context;
	
	public MeterListAdapter(List<Meter> meterList, Context context) {
		list.addAll(meterList);
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
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
			view = inflater.inflate(R.layout.meter_list_layout, null);
		}
		
		TextView listItemText = (TextView) view.findViewById(R.id.meterItem);
		listItemText.setText(list.get(position).getMeterName());
		
		Button insertButton = (Button) view.findViewById(R.id.meterInsert);
		Button overviewButton = (Button) view.findViewById(R.id.meterAll);
		
		insertButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(parent.getContext(), CameraSnapshot.class);
				intent.putExtra(MainMenu.EXTRA_METER_ID, list.get(position).getObjectId());
				parent.getContext().startActivity(intent);
			}
		});
		
		overviewButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(parent.getContext(), MeterReadingOverview.class);
				intent.putExtra(MainMenu.EXTRA_METER_ID, list.get(position).getObjectId());
				parent.getContext().startActivity(intent);
			}
		});
		
		return view;
	}

}

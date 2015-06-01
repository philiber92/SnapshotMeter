package de.pb92.snapshotmeter;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseQuery;

import de.pb92.snapshotmeter.adapter.MeterListAdapter;
import de.pb92.snapshotmeter.parse.Meter;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class OverviewFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static OverviewFragment newInstance(int sectionNumber) {
		OverviewFragment fragment = new OverviewFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public OverviewFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_overview,
				container, false);
		
		ParseQuery<Meter> query = Meter.getQuery();
		query.fromLocalDatastore();
		List<Meter> queryList;
		
		try {
			queryList = query.find();
		} catch (ParseException e) {
			queryList = new ArrayList<Meter>();
		}
		
		MeterListAdapter listAdapter = new MeterListAdapter(new ArrayList<Meter>(queryList), getActivity());
		ListView listView = (ListView)rootView.findViewById(R.id.listView1);
		listView.setAdapter(listAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Button insert = (Button) view.findViewById(R.id.meterInsert);
				Button show = (Button) view.findViewById(R.id.meterAll);
				insert.setVisibility(View.VISIBLE);
				show.setVisibility(View.VISIBLE);
			}
		});
		
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainMenu) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}
}

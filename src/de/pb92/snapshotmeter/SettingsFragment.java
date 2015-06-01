package de.pb92.snapshotmeter;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseQuery;

import de.pb92.snapshotmeter.parse.Settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class SettingsFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static SettingsFragment newInstance(int sectionNumber) {
		SettingsFragment fragment = new SettingsFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public SettingsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings,
				container, false);
		
		ParseQuery<Settings> settings = Settings.getQuery();
		settings.fromLocalDatastore();
		
		List<Settings> result;
		try {
			result = settings.find();
		} catch (ParseException e) {
			result = new ArrayList<Settings>();
		}
		
		if(!result.isEmpty()) {
			EditText editText = (EditText) rootView.findViewById(R.id.editText1);
			editText.setText(result.get(0).getLastName());
		}
		
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainMenu) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}
}

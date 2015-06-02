package de.pb92.snapshotmeter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseQuery;

import de.pb92.snapshotmeter.parse.Feedback;
import de.pb92.snapshotmeter.parse.Settings;


import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.v4.widget.DrawerLayout;

@SuppressWarnings("deprecation")
public class MainMenu extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {
	
	public static final String EXTRA_METER_ID = "de.pb92.snapshotmeter.METER";
	
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		switch(position) {
		case 0: 
			fragmentManager
				.beginTransaction()
				.replace(R.id.container, 
						OverviewFragment.newInstance(position + 1)).commit();
			break;
		case 1:
			fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						SettingsFragment.newInstance(position + 1)).commit();
			break;
		default:
			fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						FeedbackFragment.newInstance(position + 1)).commit();
			break;
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main_menu, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
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
		Intent intent = new Intent(MainMenu.this, MeterAdd.class);
		startActivity(intent);
	}
	
	public void snapMeter(View view) {
		Intent intent = new Intent(MainMenu.this, CameraSnapshot.class);
		startActivity(intent);
	}
	
	public void saveSettings(View view) {
		EditText editText = (EditText) findViewById(R.id.editText1);
		String lastName = editText.getText().toString().trim();
		
		if(lastName.isEmpty()) {
			Toast.makeText(getBaseContext(), R.string.settings_no_last_name, Toast.LENGTH_LONG)
				 .show();
			return;
		}
		
		ParseQuery<Settings> settingsQuery = Settings.getQuery();
		settingsQuery.fromLocalDatastore();
		List<Settings> settings;
		
		try {
			settings = settingsQuery.find();
		} catch (ParseException e) {
			settings = new ArrayList<Settings>();
		}
		
		if(!settings.isEmpty()) {
			settings.get(0).setLastName(lastName);
			settings.get(0).pinInBackground();
		} else {
			Settings newSettings = new Settings();
			newSettings.setLastName(lastName);
			newSettings.pinInBackground();
		}
		
		Toast.makeText(getBaseContext(), R.string.settings_save_success, Toast.LENGTH_LONG)
			 .show();
	}
	
	public void sendFeedback(View view) {
		ParseQuery<Feedback> query = Feedback.getQuery();
		query.fromLocalDatastore();
		query.orderByDescending("createdAt");
		try {
			List<Feedback> result = query.find();
			if(!result.isEmpty()) {
				Calendar cal1 = Calendar.getInstance();
				Calendar cal2 = Calendar.getInstance();
				cal1.setTime(result.get(0).getCreatedAt());
				cal2.setTime(new Date());
				
				if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
					cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
					Toast.makeText(getBaseContext(), getString(R.string.feedback_toast_error), 
							Toast.LENGTH_SHORT)
						 .show();
					return;
				}
			}
		} catch (ParseException e) {
			//ignore
		}
		
		RadioGroup desireGroup = (RadioGroup)findViewById(R.id.feedbackDesireGroup);
		RadioGroup readingGroup = (RadioGroup)findViewById(R.id.feedbackReadingGroup);
		RatingBar rBar = (RatingBar) findViewById(R.id.feedbackRatingBar);
		float rating = rBar.getRating();
		
		//check if all not optional fields are set
		if(Float.compare(rating, 0.0f) == 0 
				|| desireGroup.getCheckedRadioButtonId() == -1 
				|| readingGroup.getCheckedRadioButtonId() == -1) {
			AlertFeedbackDialogFragment alertDialog = new AlertFeedbackDialogFragment();
			alertDialog.show(getSupportFragmentManager(), "alertFeedbackDialog");
			return;
		}
		
		SendFeedbackDialogFragment dialog = new SendFeedbackDialogFragment();
		dialog.show(getSupportFragmentManager(), "sendFeedbackDialog");
	}

	public class SendFeedbackDialogFragment extends DialogFragment {
		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    	builder.setTitle(R.string.feedback_push_title)
	    		   .setMessage(R.string.feedback_push_description)
	    		   .setPositiveButton(R.string.feedback_push_send, 
	    				   new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						RatingBar rBar = (RatingBar) findViewById(R.id.feedbackRatingBar);
						RadioButton rButton1 = (RadioButton) findViewById(R.id.feedbackReadingYes);
						Spinner spinner = (Spinner) findViewById(R.id.feedbackSpinner);
						RadioButton rButton2 = (RadioButton) findViewById(R.id.feedbackDesireYes);
						EditText providerText = (EditText) findViewById(R.id.feedbackProvider);
						EditText textBox = (EditText) findViewById(R.id.feedbackComment);
						
						float rating = rBar.getRating();
						boolean reading = rButton1.isChecked();
						String usability = String.valueOf(spinner.getSelectedItem());
						boolean desire = rButton2.isChecked();
						String provider = providerText.getText().toString();
						String comment = textBox.getText().toString();
						
						Feedback feedback = new Feedback();
						feedback.setRating(rating);
						feedback.setReadingSuccess(reading);
						feedback.setUsability(usability);
						feedback.setDesire(desire);
						feedback.setProvider(provider);
						feedback.setComment(comment);
						feedback.pinInBackground();
						feedback.saveInBackground();
						
						Toast.makeText(getActivity(), getString(R.string.feedback_toast_sent), 
								Toast.LENGTH_SHORT)
							 .show();
						Intent intent = new Intent(getBaseContext(), MainMenu.class);
						startActivity(intent);
					}
				})
				.setNegativeButton(R.string.feedback_push_abort, 
						new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//ignore
					}
				});
	    	return builder.create();
	    }
	}
	
	public class AlertFeedbackDialogFragment extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    	builder.setTitle(R.string.feedback_alert_title)
	    		   .setMessage(R.string.feedback_alert_description)
	    		   .setPositiveButton(R.string.feedback_alert_ok, 
	    				   new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//ignore
					}
				});
	    	return builder.create();
	    }
	}
}

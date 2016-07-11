package com.viks.sire2;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.app.Activity;

public class ResultsActivity extends Activity {

	@Override
	public void onCreate (Bundle savedInstanceState) {

		super.onCreate (savedInstanceState);

		Bundle extras = getIntent ().getExtras ();
		int theme_id = extras.getInt ("theme", R.style.LightTheme);
		setTheme (theme_id);

		setContentView (R.layout.results);
		
		TextView wrong_attempts = (TextView) findViewById (R.id.wrong_attempts);
		TextView total_time     = (TextView) findViewById (R.id.total_time);
		TextView avg_note_time  = (TextView) findViewById (R.id.avg_note_time);

		wrong_attempts.setText (String.format ("%d", extras.getInt ("num_wrong", 0)));
	
		long time = extras.getLong ("total_time", 0);
		
		int minutes = (int) (time / (1000 * 60));
		time = time % (1000 * 60);
		
		int seconds  = (int) (time / 1000);
		int mseconds = (int) (time % 1000);
		
		if (minutes > 0) 
			total_time.setText (String.format (getResources ().getString (R.string.minutes_seconds_mseconds), minutes, seconds, mseconds));
		else
			total_time.setText (String.format (getResources ().getString (R.string.seconds_mseconds), seconds, mseconds));


		time = extras.getLong ("avg_note_time", 0);

		minutes = (int) (time / (1000 * 60));
		time = time % (1000 * 60);

		seconds  = (int) (time / 1000);
		mseconds = (int) (time % 1000);

		if (minutes > 0)
			avg_note_time.setText (String.format (getResources ().getString (R.string.minutes_seconds_mseconds), minutes, seconds, mseconds));
		else
			avg_note_time.setText (String.format (getResources ().getString (R.string.seconds_mseconds), seconds, mseconds));

	}
	
	public void onReturn (View view) {
		finish ();
	}
}

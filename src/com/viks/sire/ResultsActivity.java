package com.viks.sire;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.app.Activity;

public class ResultsActivity extends Activity {

	@Override
	public void onCreate (Bundle savedInstanceState) {
		
		super.onCreate (savedInstanceState);
		setContentView (R.layout.results);
		
		Bundle extras = getIntent ().getExtras ();
		
		TextView wrong_attempts = (TextView) findViewById (R.id.wrong_attempts);
		TextView total_time     = (TextView) findViewById (R.id.total_time);
		
		wrong_attempts.setText (String.format ("%d", extras.getInt ("num_wrong", 0)));
	
		long time = extras.getLong ("total_time", 0);
		
		int minutes = (int) (time / (1000 * 60));
		time = time % (1000 * 60);
		
		int seconds  = (int) (time / 1000);
		int mseconds = (int) (time % 1000);
		
		if (minutes > 0) 
			total_time.setText (String.format ("%d:%02d.%03ds", minutes, seconds, mseconds));
		else
			total_time.setText (String.format ("%d.%03ds", seconds, mseconds));
	}
	
	public void onReturn (View view) {
		finish ();
	}

}

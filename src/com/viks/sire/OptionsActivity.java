package com.viks.sire;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.SharedPreferences;

public class OptionsActivity extends Activity {

    private SharedPreferences options;
    private SharedPreferences.Editor edit_options;

    private CheckBox enable_treble_clef_check; 
    private CheckBox enable_bass_clef_check; 
    private CheckBox enable_alto_clef_check;
    private CheckBox enable_tenor_clef_check;
    private CheckBox enable_show_correct_check;

    private RadioGroup naming_style_radiogroup;
    private RadioButton naming_english_radio;
    private RadioButton naming_italian_radio;
    
    private SeekBar difficulty_level_bar;
    private TextView difficulty_level_text;
    
    private EditText practice_limit;
    private EditText quiz_limit;

    @Override
    public void onCreate (Bundle savedInstanceState) {
    	
        super.onCreate (savedInstanceState);
        setContentView (R.layout.options);
 
        options = getSharedPreferences ("sire", 0);
		edit_options = options.edit ();

        enable_treble_clef_check  = (CheckBox) findViewById (R.id.enable_treble_clef); 
        enable_bass_clef_check    = (CheckBox) findViewById (R.id.enable_bass_clef);
        enable_alto_clef_check    = (CheckBox) findViewById (R.id.enable_alto_clef);
        enable_tenor_clef_check   = (CheckBox) findViewById (R.id.enable_tenor_clef);
        enable_show_correct_check = (CheckBox) findViewById (R.id.enable_show_correct);

        naming_style_radiogroup = (RadioGroup)  findViewById (R.id.naming_style_radiogroup);
        naming_english_radio    = (RadioButton) findViewById (R.id.note_naming_english);
        naming_italian_radio    = (RadioButton) findViewById (R.id.note_naming_italian);
        
        difficulty_level_bar  = (SeekBar)  findViewById (R.id.difficulty_level);
        difficulty_level_text = (TextView) findViewById (R.id.difficulty_level_text);
        
        practice_limit = (EditText) findViewById (R.id.practice_limit);
        quiz_limit     = (EditText) findViewById (R.id.quiz_limit);

        if (savedInstanceState == null) {
	        enable_treble_clef_check.setChecked (options.getBoolean ("enable_treble_clef", true));
	        enable_bass_clef_check.setChecked (options.getBoolean ("enable_bass_clef", true));
	        enable_alto_clef_check.setChecked (options.getBoolean ("enable_alto_clef", false));
	        enable_tenor_clef_check.setChecked (options.getBoolean ("enable_tenor_clef", false));
	        enable_show_correct_check.setChecked (options.getBoolean ("enable_show_correct", true));
	
			switch (options.getInt ("note_naming_style", 0)) {
				case 1:
					naming_italian_radio.setChecked (true);
					break;
				case 0: // use English note naming style by default
				default:
					naming_english_radio.setChecked (true);
			}
			
			difficulty_level_bar.setMax (8);
			difficulty_level_bar.setProgress (options.getInt ("difficulty_level", 0));
			
			practice_limit.setText (String.format ("%d", options.getInt ("practice_limit", 50)));
			quiz_limit.setText (String.format ("%d", options.getInt ("quiz_limit", 10)));
        }

		difficulty_level_text.setText (String.format ("%d", difficulty_level_bar.getProgress ()+1));

        difficulty_level_bar.setOnSeekBarChangeListener (new SeekBar.OnSeekBarChangeListener () {       
	        @Override       
	        public void onProgressChanged (SeekBar seekBar, int progress,boolean fromUser) {
				difficulty_level_text.setText (String.format ("%d", difficulty_level_bar.getProgress ()+1));
	        }
			
	        @Override
			public void onStartTrackingTouch(SeekBar arg0) {}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {} 
        });
    }

    public void onSaveClicked (View view) {
    	
		save_options_block:
		{
			if (!enable_treble_clef_check.isChecked () && 
			    !enable_bass_clef_check.isChecked ()   &&
			    !enable_alto_clef_check.isChecked ()   &&
			    !enable_tenor_clef_check.isChecked ())
			{
			    Toast.makeText (getApplicationContext (), "Must select at least one clef!", Toast.LENGTH_SHORT).show ();
			    break save_options_block;
			}
			
			int plimit = Integer.parseInt (practice_limit.getText ().toString ());
			int qlimit = Integer.parseInt (quiz_limit.getText ().toString ());

			if ((plimit > 200) || (plimit < 2)) {
			    Toast.makeText (getApplicationContext (), "Number of practice notes must be in range [2;200]!", Toast.LENGTH_SHORT).show ();
			    break save_options_block;
			}
			if ((qlimit > 200) || (qlimit < 2)) {
			    Toast.makeText (getApplicationContext (), "Number of quiz notes must be in range [2;200]!", Toast.LENGTH_SHORT).show ();
			    break save_options_block;
			}
		
			edit_options.putBoolean ("enable_treble_clef",  enable_treble_clef_check.isChecked ());
			edit_options.putBoolean ("enable_bass_clef",    enable_bass_clef_check.isChecked ());
			edit_options.putBoolean ("enable_alto_clef",    enable_alto_clef_check.isChecked ());
			edit_options.putBoolean ("enable_tenor_clef",   enable_tenor_clef_check.isChecked ());
			edit_options.putBoolean ("enable_show_correct", enable_show_correct_check.isChecked ());
			
			switch (naming_style_radiogroup.getCheckedRadioButtonId ()) {
			
				case R.id.note_naming_italian: 
					edit_options.putInt ("note_naming_style", 1);
					break;
				case R.id.note_naming_english: 
				default:
					edit_options.putInt ("note_naming_style", 0);	
			}
			
			edit_options.putInt ("difficulty_level", difficulty_level_bar.getProgress ());

			edit_options.putInt ("practice_limit", plimit);
			edit_options.putInt ("quiz_limit", qlimit);
			
			practice_limit.setText (String.format ("%d", options.getInt ("practice_limit", 50)));
			quiz_limit.setText (String.format ("%d", options.getInt ("quiz_limit", 10)));

			
			edit_options.commit ();
	    	finish ();
	    	
		} // end save_options_block:
    }
    
    public void onCancelClicked (View view) {
    	finish ();
    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {
		super.onSaveInstanceState (savedInstanceState);  
    }  
    
	@Override  
	public void onRestoreInstanceState (Bundle savedInstanceState) {  
		super.onRestoreInstanceState (savedInstanceState);  
	}    
}

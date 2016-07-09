package com.viks.sire2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Random;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class QuizActivity extends Activity {

	private final static int NUM_CLEF_NOTES = 25;

    private boolean enable_show_correct;
    private boolean enable_vibration_on_wrong;

    private ImageView clef_imageview;
    private ImageView note_imageview;

    private Button buttons[];
    private int default_color;

    private final int[][] levels = {{ 8,16 }, { 7,17 }, { 6,18 }, { 5,19 }, { 4,20 }, { 3,21 }, { 2,22 }, { 1,23 }, { 0,24 }};

    private int[] clefs;
    private int[] notes;

    private final int[] first_note = { 3,5,4,2 };
/*
    private final int[] treble_notes = { 3,4,5,6,0,1,2,3,4,5,6,0,1,2,3,4,5,6,0,1,2,3,4,5,6 };
    private final int[] bass_notes   = { 5,6,0,1,2,3,4,5,6,0,1,2,3,4,5,6,0,1,2,3,4,5,6,0,1 };
    private final int[] alto_notes   = { 4,5,6,0,1,2,3,4,5,6,0,1,2,3,4,5,6,0,1,2,3,4,5,6,0 };
    private final int[] tenor_notes  = { 2,3,4,5,6,0,1,2,3,4,5,6,0,1,2,3,4,5,6,0,1,2,3,4,5 };
 */
    private LinkedList<Integer> quiz;
    private int num_right;
    private int num_wrong;
    private long time_start;
    private int limit;

    private int current_quiz;
    private int current_note;
    private Button current_button;

    private Thread time_update_thread = null;
    private TextView time_elapsed;
    private TextView progress;
    private boolean time_elapsed_terminate;
/*
    private final int[][] clef_notes = { treble_notes, bass_notes, alto_notes, tenor_notes };
 */

    @Override
    public void onCreate (Bundle savedInstanceState) {

        Bundle extras = getIntent ().getExtras ();
        int theme_id = extras.getInt ("theme", R.style.LightTheme);
        setTheme (theme_id);

        super.onCreate (savedInstanceState);
        setContentView (R.layout.quiz);

        SVG svg;
        clefs = new int [4];
        notes = new int [25];

        if (theme_id == R.style.DarkTheme) {
            svg = SVGParser.getSVGFromResource (getResources (), R.raw.dark_staff);

            clefs[0] = R.raw.dark_treble_clef;
            clefs[1] = R.raw.dark_bass_clef;
            clefs[2] = R.raw.dark_alto_clef;
            clefs[3] = R.raw.dark_tenor_clef;

            for (int i=0;i<25;i++)
                notes[i] = getResources ().getIdentifier (String.format ("%s_note%02d", "dark", i + 1), "raw", getPackageName());
        }
        else {
            svg = SVGParser.getSVGFromResource(getResources(), R.raw.light_staff);

            clefs[0] = R.raw.light_treble_clef;
            clefs[1] = R.raw.light_bass_clef;
            clefs[2] = R.raw.light_alto_clef;
            clefs[3] = R.raw.light_tenor_clef;

            for (int i = 0; i < 25; i++)
                notes[i] = getResources ().getIdentifier (String.format ("%s_note%02d", "light", i + 1), "raw", getPackageName ());
        }


        SharedPreferences options = getSharedPreferences ("sire", 0);

        boolean enable_treble_clef = options.getBoolean ("enable_treble_clef", true);
        boolean enable_bass_clef   = options.getBoolean ("enable_bass_clef",   true);
        boolean enable_alto_clef   = options.getBoolean ("enable_alto_clef",   false);
        boolean enable_tenor_clef  = options.getBoolean ("enable_tenor_clef",  false);

        int note_naming_style = options.getInt ("note_naming_style", 0);
        int difficulty_level = options.getInt ("difficulty_level", 0);


        boolean practice = extras.getBoolean ("practice", false);
        if (practice) {
        	limit                     = options.getInt ("practice_limit", 50);
        	enable_show_correct       = options.getBoolean ("enable_show_correct", true);
            enable_vibration_on_wrong = options.getBoolean ("enable_vibration_on_wrong", true);
        }
        else {
        	limit                     = options.getInt ("quiz_limit", 10);
        	enable_show_correct       = false;
            enable_vibration_on_wrong = false;
        }
        //Log.i ("info", String.format("Limit: %d", limit));

        // widgets
        clef_imageview  = (ImageView) findViewById (R.id.clef);
        note_imageview  = (ImageView) findViewById (R.id.note);
        ImageView staff = (ImageView) findViewById (R.id.staff);

        try {
            Method setLayerTypeMethod;
            setLayerTypeMethod = clef_imageview.getClass ().getMethod ("setLayerType", new Class[] {int.class, Paint.class});
            setLayerTypeMethod.invoke (clef_imageview, new Object[] {View.LAYER_TYPE_SOFTWARE, null});

            setLayerTypeMethod = note_imageview.getClass ().getMethod ("setLayerType", new Class[] {int.class, Paint.class});
            setLayerTypeMethod.invoke (note_imageview, new Object[] {View.LAYER_TYPE_SOFTWARE, null});

            setLayerTypeMethod = staff.getClass ().getMethod ("setLayerType", new Class[] {int.class, Paint.class});
            setLayerTypeMethod.invoke (staff, new Object[] {View.LAYER_TYPE_SOFTWARE, null});
        } catch (NoSuchMethodException e) {
            // Older OS, no HW acceleration anyway
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }

        staff.setImageDrawable (svg.createPictureDrawable ());

        time_elapsed = (TextView) findViewById (R.id.time_elapsed);
        progress     = (TextView) findViewById (R.id.progress);
        time_elapsed.setText (getResources ().getString (R.string.zero_seconds));
        progress.setText ("0 / 0");


        buttons = new Button[7];
        buttons[0] = (Button) findViewById (R.id.keyA);
        buttons[1] = (Button) findViewById (R.id.keyB);
        buttons[2] = (Button) findViewById (R.id.keyC);
        buttons[3] = (Button) findViewById (R.id.keyD);
        buttons[4] = (Button) findViewById (R.id.keyE);
        buttons[5] = (Button) findViewById (R.id.keyF);
        buttons[6] = (Button) findViewById (R.id.keyG);


        // note naming style
        String[] note_names;
        Resources res = getResources ();

        switch (note_naming_style) {
	        case 1:
	        	note_names = res.getStringArray (R.array.note_names_italian);
	        	break;
	        case 0:
	        default:
	        	note_names = res.getStringArray (R.array.note_names_english);
        }

        // set button captions from string resource
        for (int i=0;i<7;++i)
        	buttons[i].setText (note_names[i]);

        default_color = buttons[0].getTextColors ().getDefaultColor ();

        quiz = new LinkedList<Integer> ();

        //Log.i ("info", String.format("Difficulty: %d", difficulty_level));
        if (savedInstanceState == null) { // previous state not saved

        	// get bounds for selected difficulty level
        	int a = levels [difficulty_level][0];
        	int b = levels [difficulty_level][1];

        	int rand_clefs[] = new int[4];
        	int rand_clefs_count = 0;

	        if (enable_treble_clef) rand_clefs[rand_clefs_count++] = 0;
	        if (enable_bass_clef)   rand_clefs[rand_clefs_count++] = 1;
	        if (enable_alto_clef)   rand_clefs[rand_clefs_count++] = 2;
	        if (enable_tenor_clef)  rand_clefs[rand_clefs_count++] = 3;

	       	Random rand = new Random ();

	       	int c,n,q,lq = -1;

	       	while (quiz.size () < limit) {

	       		c = rand_clefs[rand.nextInt (rand_clefs_count)]; // random clef
	       		n = rand.nextInt (b-a+1) + a; // random note
	       		q = NUM_CLEF_NOTES*c + n;

	       		if (lq != q) {
	       			quiz.add (q);
	       			lq = q;
	       		}
	       	}

	        time_start = System.currentTimeMillis ();
	       	next_note ();
	       	start ();
        }
    }

    private void update_time_elapsed () {
		long time_end = System.currentTimeMillis ();

		long time = (long)(time_end - time_start);
		int minutes = (int) (time / (1000 * 60));
		time = time % (1000 * 60);

		int seconds  = (int) (time / 1000);

		if (minutes > 0)
			time_elapsed.setText (String.format (getResources ().getString (R.string.minutes_seconds), minutes, seconds));
		else
			time_elapsed.setText (String.format (getResources ().getString (R.string.seconds), seconds));
    }

    private void start () {
        update_time_elapsed ();

        time_update_thread = new Thread (new Runnable () {
			public void run () {

				do {
					time_elapsed.post (new Runnable () {

						public void run () {
					        update_time_elapsed ();
						}
					});

					try {
						Thread.sleep (1000);
					} catch (InterruptedException e) {}

				} while (!time_elapsed_terminate);
			}
		});

        time_elapsed_terminate = false;
		time_update_thread.start ();
    }

    private void stop () {
    	if (time_update_thread != null) {
    		time_elapsed_terminate = true;
	    	time_update_thread.interrupt ();
	    	try {
				time_update_thread.join ();
			} catch (InterruptedException e) {}
			time_update_thread = null;
    	}
    }
/*
    @Override
    public void onBackPressed () {
    	stop ();
    	super.onBackPressed ();
    }
*/
    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            stop ();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onKeyA (View view) { clicked (0); }
    public void onKeyB (View view) { clicked (1); }
    public void onKeyC (View view) { clicked (2); }
    public void onKeyD (View view) { clicked (3); }
    public void onKeyE (View view) { clicked (4); }
    public void onKeyF (View view) { clicked (5); }
    public void onKeyG (View view) { clicked (6); }

    private void clicked (int note_id)
    {
        if (note_id != current_note)
        {
            ++num_wrong;

            if (enable_show_correct) {
                current_button.setTextColor (Color.BLUE);
                current_button.setTypeface (Typeface.DEFAULT_BOLD);
            }

            if (enable_vibration_on_wrong) {
                ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(250L);
            }
        }
        else {
            ++num_right;

            if (enable_show_correct) {
                current_button.setTextColor (default_color);
                current_button.setTypeface (Typeface.DEFAULT);
            }

            if (!next_note ()) {
                long time_end = System.currentTimeMillis ();

                Intent intent = new Intent (this, ResultsActivity.class);
                intent.putExtra ("num_right", num_right);
                intent.putExtra ("num_wrong", num_wrong);
                intent.putExtra ("total_time", (long)(time_end - time_start));
                startActivity (intent);
                stop ();
                finish ();
            }
        }
    }

    private boolean next_note ()
    {
        if (quiz.size () <= 0) return false;

        current_quiz   = quiz.removeFirst ();

        progress.setText (String.format ("%d / %d", limit - quiz.size (), limit));

        int clef = current_quiz / NUM_CLEF_NOTES;
        int note = current_quiz % NUM_CLEF_NOTES;

        current_note   = (first_note[clef] + note) % 7;
        current_button = buttons[current_note];

        SVG svg;
        svg = SVGParser.getSVGFromResource (getResources (), clefs[clef]);
        clef_imageview.setImageDrawable (svg.createPictureDrawable ());

        svg = SVGParser.getSVGFromResource (getResources (), notes[note]);
        note_imageview.setImageDrawable (svg.createPictureDrawable ());


        return true;
    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {

		savedInstanceState.putLong ("time_start", time_start);
		savedInstanceState.putInt ("num_right", num_right);
		savedInstanceState.putInt ("num_wrong", num_wrong);
		savedInstanceState.putInt ("limit", limit);

		int array[] = new int [quiz.size()+1];
		array[0] = current_quiz;
		for (int i=0;i<quiz.size();++i)
			array[i+1] = quiz.get(i);
		savedInstanceState.putIntArray ("quiz", array);

		super.onSaveInstanceState (savedInstanceState);
    }

	@Override
	public void onRestoreInstanceState (Bundle savedInstanceState) {

		super.onRestoreInstanceState (savedInstanceState);

		time_start = savedInstanceState.getLong ("time_start");
		num_right = savedInstanceState.getInt ("num_right");
		num_wrong = savedInstanceState.getInt ("num_wrong");
		limit = savedInstanceState.getInt ("limit");

		int array[] = savedInstanceState.getIntArray ("quiz");

		for (int i=0;i<array.length;++i)
			quiz.add (array[i]);

		next_note ();
		start ();
	}
}

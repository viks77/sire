package com.viks.sire2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    private int theme_id;
    private int old_theme_id;

    @Override
    public void onCreate (Bundle savedInstanceState) {

        theme_id = getThemeOption();
        setTheme (theme_id);

        super.onCreate (savedInstanceState);
        setContentView (R.layout.main);

        String version;
        try {
			String versionName = getPackageManager ().getPackageInfo (getPackageName (), 0).versionName;
			version = String.format (getResources ().getString (R.string.version), versionName);
		} catch (NameNotFoundException e) {
			version = "";
		}

        TextView version_label = (TextView) findViewById (R.id.version);
        version_label.setText (version);
    }

    public void onPracticeClicked (View view) {
 		Intent intent = new Intent (this, QuizActivity.class);
        intent.putExtra ("theme", theme_id);
 		intent.putExtra ("practice", true);
		startActivity (intent);
    }

    public void onQuizClicked (View view) {
 		Intent intent = new Intent (this, QuizActivity.class);
        intent.putExtra ("theme", theme_id);
		intent.putExtra ("practice", false);
 		startActivity (intent);
    }

    public void onOptionsClicked (View view) {
    	old_theme_id = theme_id;
        Intent intent = new Intent (this, OptionsActivity.class);
        intent.putExtra ("theme", theme_id);
 		startActivityForResult (intent, 1);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        theme_id = getThemeOption();

        if (old_theme_id != theme_id) {
            finish ();
            startActivity (new Intent (this, getClass ()));
        }
    }

    private int getThemeOption() {
        SharedPreferences options = getSharedPreferences ("sire", 0);
        int id = options.getInt ("theme", 0);
        switch (id) {
            case 1:
                return R.style.DarkTheme;
            case 0:
            default:
                return R.style.LightTheme;
        }
    }


}

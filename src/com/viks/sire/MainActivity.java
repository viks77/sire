package com.viks.sire;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.main);
    }

	@Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater ().inflate (R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
        
            case R.id.menu_settings: 
                onOptionsClicked (null);
                return true;
                
            default:
                return super.onOptionsItemSelected (item);
        }
    }
    
    public void onPracticeClicked (View view) {
 		Intent intent = new Intent (this, QuizActivity.class);
 		intent.putExtra ("practice", true);
		startActivity (intent);
    }
    
    public void onQuizClicked (View view) {
 		Intent intent = new Intent (this, QuizActivity.class);
		intent.putExtra ("practice", false);
 		startActivity (intent);
    }
    
    public void onStatsClicked (View view) {
        Toast.makeText (getApplicationContext (), 
                        "This feature is currently unavailable.",
                        Toast.LENGTH_SHORT).show ();
    }
    
    public void onOptionsClicked (View view) {
    	Intent intent = new Intent (this, OptionsActivity.class);
 		startActivity (intent);
    }
    
}

package com.cachirulop.wifireset.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.cachirulop.wifireset.R;
import com.cachirulop.wifireset.adapter.HistoryAdapter;
import com.cachirulop.wifireset.manager.HistoryManager;
import com.cachirulop.wifireset.manager.WifiResetManager;

/**
 * Main Activity of the WifiReset app.
 * 
 * @author david
 */
public class MainActivity extends Activity {

	/**
	 * Creates the activity from the activity_main layout
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        fillHistory ();
        
        LocalBroadcastManager.getInstance(this).registerReceiver(_historyModifiedReceiver,
        	      new IntentFilter(HistoryManager.BROADCAST_HISTORY_MODIFIED));
        
        // TODO: Test the configuration
        WifiResetManager.startService(this);
    }

    /**
     * Load the menu from the main.xml file.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /**
     * Menu option selected. 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                showSettings();
                return true;

            case R.id.action_reset:
            	resetWifi();
            	return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }    
    
    /**
     * Load the settings activity with an Intent.
     */
    private void showSettings () {
    	Intent intent = new Intent(this, SettingsActivity.class);

    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
    }
    
    /**
     * Force a reset wifi connection
     */
    private void resetWifi () {
    	WifiResetManager.reset(this);
    	refreshHistory();
    }
    
	public void refreshHistory() {
		ListView listView;

		listView = (ListView) findViewById(R.id.lvHistory);
		((HistoryAdapter) listView.getAdapter()).refresh();
	}

    /**
     * Fill the history listview with the contents of the table History 
     * of the database.
     */
    private void fillHistory() {
    	ListView lv;
    	
		lv = (ListView) findViewById(R.id.lvHistory);
		lv.setAdapter(new HistoryAdapter(this));
    }
    
    private BroadcastReceiver _historyModifiedReceiver = new BroadcastReceiver() {
    	  @Override
    	  public void onReceive(Context context, Intent intent) {
    		  refreshHistory();
    	  }
    };
}

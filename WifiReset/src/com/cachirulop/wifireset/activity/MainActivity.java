package com.cachirulop.wifireset.activity;

import java.text.DateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;

import com.cachirulop.wifireset.R;
import com.cachirulop.wifireset.adapter.HistoryAdapter;
import com.cachirulop.wifireset.broadcast.IWifiResetBroadcastReceiver;
import com.cachirulop.wifireset.broadcast.WifiResetBroadcastReceiverManager;
import com.cachirulop.wifireset.manager.HistoryManager;
import com.cachirulop.wifireset.manager.SettingsManager;
import com.cachirulop.wifireset.manager.WifiResetManager;

/**
 * Main Activity of the WifiReset app.
 * 
 * @author david
 */
public class MainActivity extends Activity 
	implements IWifiResetBroadcastReceiver {
	
	WifiResetBroadcastReceiverManager _broadcastManager;

	/**
	 * Creates the activity from the activity_main layout
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        fillHistory ();
        
        _broadcastManager = new WifiResetBroadcastReceiverManager(this, this);
        
        if (SettingsManager.isActive(this)) {
        	WifiResetManager.startService(this);
        }
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
            	
            case R.id.action_clean_history:
            	cleanHistory();
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
    
    /**
     * Clean the history listview
     */
    public void cleanHistory () {
    	HistoryManager.clean(this);
    	
    	refreshHistory ();
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
    
    public void swStatusClicked (View v) {
    	boolean on;
    	
    	on = ((Switch) v).isChecked();
    	if (on) {
    		HistoryManager.add(this, R.string.wifireset_activated);
        	WifiResetManager.startService(this);
    	}
    	else {
    		HistoryManager.add(this, R.string.wifireset_deactivated);
        	WifiResetManager.stopService(this);
    	}
    }

	@Override
	public void wifiIsEnabled() {
		HistoryManager.add(this, R.string.wifi_is_enabled);		
	}

	@Override
	public void wifiIsDisabled() {
		HistoryManager.add(this, R.string.wifi_is_disabled);		
	}

	@Override
	public void wifiIsInUse() {
		HistoryManager.add(this, R.string.wifi_is_in_use);		
	}

	@Override
	public void wifiIsIdle() {
		HistoryManager.add(this, R.string.wifi_is_idle);		
	}

	@Override
	public void wifiRestarted() {
		HistoryManager.add(this, R.string.wifi_restarted);		
	}

	@Override
	public void historyModified() {
		refreshHistory();
	}

	@Override
	public void wifiResetReseting() {
		HistoryManager.add(this, R.string.wifireset_reseting);
	}

	@Override
	public void wifiResetStarting() {
		HistoryManager.add(this, R.string.wifireset_starting);
	}

	@Override
	public void wifiREsetStopping() {
		HistoryManager.add(this, R.string.wifireset_stopping);
	}

	@Override
	public void wifiResetNextReset(Calendar nextReset) {
		HistoryManager.add(this, String.format(getString (R.string.wifireset_reseting), 
				DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(nextReset.getTime())));
	}   
}

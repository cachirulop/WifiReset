package com.cachirulop.wifireset.activity;

import java.util.Calendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.cachirulop.wifireset.R;
import com.cachirulop.wifireset.adapter.HistoryAdapter;
import com.cachirulop.wifireset.common.Util;
import com.cachirulop.wifireset.manager.BroadcastManager;
import com.cachirulop.wifireset.manager.HistoryManager;
import com.cachirulop.wifireset.manager.SettingsManager;
import com.cachirulop.wifireset.manager.WifiResetManager;
import com.cachirulop.wifireset.service.WifiResetService;

/**
 * Main Activity of the WifiReset application.
 * 
 * @author dmagrom
 */
public class MainActivity extends Activity {
	private BroadcastReceiver _broadcastReceiver;

	/**
	 * Creates the activity from the activity_main layout
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		createBroadcastReceiver();
		
		if (SettingsManager.isActive(this)) {
			startWifiResetService();
		}
	}

	@Override
	protected void onStart() {
		initControls();

		super.onStart();
	}

	@Override
	protected void onStop() {
		unregisterBroadcasts();

		super.onStop();
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
	private void showSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	/**
	 * Force a reset wifi connection
	 */
	private void resetWifi() {
		WifiResetManager.reset(this);
		refreshHistory();
	}

	/**
	 * Clean the history listview
	 */
	public void cleanHistory() {
		HistoryManager.clean(this);

		refreshHistory();
	}

	public void refreshHistory() {
		ListView listView;

		listView = (ListView) findViewById(R.id.lvHistory);
		((HistoryAdapter) listView.getAdapter()).refresh();
	}

	/**
	 * Initialize the status of the activity controls.
	 */
	private void initControls() {
		Switch btn;
		Calendar nextReset;

		// Active button
		btn = (Switch) findViewById(R.id.swStatus);
		btn.setChecked(SettingsManager.isActive(this));

		// Next reset time 
		nextReset = SettingsManager.getNextResetTime(this);
		setNextReset(nextReset);
		
		// History
		fillHistory();
	}

	private void createBroadcastReceiver() {
		Log.d(MainActivity.class.getSimpleName(), "Creating broadcast receiver");
		
		_broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context ctx, Intent intent) {
				String action;

				action = intent.getAction();

				Log.d(MainActivity.class.getSimpleName(), 
						String.format ("Receiving broadcast: %s", action));

				if (BroadcastManager.BROADCAST_HISTORY_ADDED.equals(action)
						|| BroadcastManager.BROADCAST_HISTORY_CLEANED
								.equals(action)) {
					fillHistory();
				} else if (BroadcastManager.BROADCAST_WIFIRESET_NEXT_RESET
						.equals(action)) {
					Calendar cal = null;
					long nextResetTime;

					nextResetTime = intent.getLongExtra(
							BroadcastManager.PARAMETER_NEXT_RESET_TIME, 0);
					
					if (nextResetTime != 0) {
						cal = Calendar.getInstance();
						cal.setTimeInMillis(nextResetTime);
					}
	
					setNextReset(cal);
				}
			}
		};

		String[] broadcastList = new String[] {
				BroadcastManager.BROADCAST_HISTORY_ADDED,
				BroadcastManager.BROADCAST_HISTORY_CLEANED,
				BroadcastManager.BROADCAST_WIFIRESET_NEXT_RESET };

		BroadcastManager.registerReceiverList(this, broadcastList,
				_broadcastReceiver);
	}

	/**
	 * Removes the broadcasts registered before to stop of receiving the
	 * messages
	 */
	private void unregisterBroadcasts() {
		BroadcastManager.unregisterReceiver(this, _broadcastReceiver);
	}

	/**
	 * Set the text of the text view with the next reset time
	 * 
	 * @param value
	 */
	private void setNextReset(Calendar value) {
		TextView tv;

		tv = (TextView) findViewById(R.id.tvNextResetValue);

		if (value != null) {
			tv.setText(Util.getCalendarFormatted(value));
		} else {
			tv.setText(this.getText(R.string.wifireset_deactivated));
		}
	}

	/**
	 * Fill the history list view with the contents of the table History of the
	 * database.
	 */
	private void fillHistory() {
		ListView lv;

		lv = (ListView) findViewById(R.id.lvHistory);
		lv.setAdapter(new HistoryAdapter(this));
	}

	/**
	 * Fired when the Active switch button is clicked
	 * 
	 * @param v
	 *            View that launches the event
	 */
	public void swStatusClicked(View v) {
		boolean on;

		on = ((Switch) v).isChecked();
		if (on) {
			startWifiResetService();
			HistoryManager.add(this, R.string.wifireset_activated);
		} else {
			HistoryManager.add(this, R.string.wifireset_deactivated);
			//WifiResetService.deactivate(this);
		}

		SettingsManager.setActive(this, on);
	}
	
	public void startWifiResetService () {
		Intent i;
		
		i = new Intent(this, WifiResetService.class);
		i.setAction(WifiResetService.ACTION_START);
		
		startService(i);
		// WifiResetService.activate(this);
	}
}

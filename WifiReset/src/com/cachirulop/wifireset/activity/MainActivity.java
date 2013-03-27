package com.cachirulop.wifireset.activity;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.cachirulop.wifireset.R;
import com.cachirulop.wifireset.adapter.HistoryAdapter;
import com.cachirulop.wifireset.broadcast.IWifiResetBroadcastReceiver;
import com.cachirulop.wifireset.broadcast.WifiResetBroadcastReceiverManager;
import com.cachirulop.wifireset.common.Util;
import com.cachirulop.wifireset.manager.HistoryManager;
import com.cachirulop.wifireset.manager.SettingsManager;
import com.cachirulop.wifireset.manager.WifiResetManager;
import com.cachirulop.wifireset.manager.WifiResetNotificationManager;

/**
 * Main Activity of the WifiReset app.
 * 
 * @author david
 */
public class MainActivity extends Activity implements
		IWifiResetBroadcastReceiver {

	WifiResetBroadcastReceiverManager _broadcastManager;

	/**
	 * Creates the activity from the activity_main layout
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//WifiResetNotificationManager.register(this);

		if (SettingsManager.isActive(this)) {
			WifiResetManager.startService(this);
		}
	}

	@Override
	protected void onStart() {
		fillHistory();
		initControls();
		registerBroadcasts();

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
		WifiResetManager.reset(this, false);
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

		btn = (Switch) findViewById(R.id.swStatus);
		btn.setChecked(SettingsManager.isActive(this));

		Calendar nextReset;

		nextReset = SettingsManager.getNextResetTime(this);
		if (nextReset != null) {
			setNextResetText(Util.getCalendarFormatted(nextReset));
		}
	}

	/**
	 * Creates the WifiResetBroadcastReceiverManager to receive the broadcast
	 * from the service and the managers.
	 */
	private void registerBroadcasts() {
		_broadcastManager = new WifiResetBroadcastReceiverManager(this, this);
	}

	/**
	 * Removes the broadscasts registered before to stop of receiving the
	 * messages
	 */
	private void unregisterBroadcasts() {
		if (_broadcastManager != null) {
			_broadcastManager.unregister(this);
			_broadcastManager = null;
		}
	}

	/**
	 * Set the text of the text view with the next reset time
	 * 
	 * @param value
	 */
	private void setNextResetText(String value) {
		TextView tv;

		tv = (TextView) findViewById(R.id.tvNextResetValue);
		tv.setText(value);
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
			HistoryManager.add(this, R.string.wifireset_activated);
			WifiResetManager.startService(this);
		} else {
			HistoryManager.add(this, R.string.wifireset_deactivated);
			WifiResetManager.stopService(this);
		}

		SettingsManager.setActive(this, on);
	}

	@Override
	public void wifiIsEnabled(Context ctx) {
		HistoryManager.add(this, R.string.wifi_is_enabled);
	}

	@Override
	public void wifiIsDisabled(Context ctx) {
		HistoryManager.add(this, R.string.wifi_is_disabled);
	}

	@Override
	public void wifiIsInUse(Context ctx) {
		HistoryManager.add(this, R.string.wifi_is_in_use);
	}

	@Override
	public void wifiIsIdle(Context ctx) {
		HistoryManager.add(this, R.string.wifi_is_idle);
	}

	@Override
	public void wifiRestarted(Context ctx) {
		HistoryManager.add(this, R.string.wifi_restarted);
	}

	@Override
	public void historyModified(Context ctx) {
		refreshHistory();
	}

	@Override
	public void wifiResetReseting(Context ctx) {
		HistoryManager.add(this, R.string.wifireset_reseting);
	}

	@Override
	public void wifiResetStarting(Context ctx) {
		HistoryManager.add(this, R.string.wifireset_starting);
	}

	@Override
	public void wifiResetStarted(Context ctx) {
		HistoryManager.add(this, R.string.wifireset_started);
	}

	@Override
	public void wifiResetStopping(Context ctx) {
		HistoryManager.add(this, R.string.wifireset_stopping);

		setNextResetText("");
	}

	@Override
	public void wifiResetNextReset(Context ctx, Calendar nextReset) {
		String nextResetFormatted;

		nextResetFormatted = Util.getCalendarFormatted(nextReset);

		// Add history event
		HistoryManager.add(this, String.format(
				getString(R.string.wifireset_next_reset), nextResetFormatted));

		// Update the text view
		setNextResetText(nextResetFormatted);

		// Save the value in the shared preferences
		SettingsManager.setNextResetTime(this, nextReset);
	}
}

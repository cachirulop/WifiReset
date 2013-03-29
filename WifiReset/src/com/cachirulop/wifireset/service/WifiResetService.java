package com.cachirulop.wifireset.service;

import java.util.Calendar;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.cachirulop.wifireset.R;
import com.cachirulop.wifireset.common.Util;
import com.cachirulop.wifireset.manager.BroadcastManager;
import com.cachirulop.wifireset.manager.HistoryManager;
import com.cachirulop.wifireset.manager.SettingsManager;
import com.cachirulop.wifireset.manager.WifiResetManager;
import com.cachirulop.wifireset.manager.WifiResetNotificationManager;

public class WifiResetService extends Service  {
	private BroadcastReceiver _broadcastReceiver;
	
	public WifiResetService () {
		createBroadcastReceiver ();
		registerBroadcasts();
	}
	
	private void createBroadcastReceiver () {
		_broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context ctx, Intent intent) {
				String action;

				action = intent.getAction();

				if (BroadcastManager.BROADCAST_WIFI_ENABLED.equals(action)) {
					onBroadcastWifiIsEnabled();
				} else if (BroadcastManager.BROADCAST_WIFI_DISABLED.equals(action)) {
					onBroadcastWifiIsDisabled();
				} else if (BroadcastManager.BROADCAST_WIFI_IN_USE.equals(action)) {
					onBroadcastWifiIsInUse();
				} else if (BroadcastManager.BROADCAST_WIFI_IDLE.equals(action)) {
					onBroadcastWifiIsIdle();
				} else if (BroadcastManager.BROADCAST_WIFI_RESTARTED.equals(action)) {
					onBroadcastWifiRestarted();
				} else if (BroadcastManager.BROADCAST_WIFIRESET_RESETING.equals(action)) {
					onBroadcastWifiResetReseting();
				} else if (BroadcastManager.BROADCAST_WIFIRESET_STARTING.equals(action)) {
					onBroadcastWifiResetStarting();
				} else if (BroadcastManager.BROADCAST_WIFIRESET_STARTED.equals(action)) {
					onBroadcastWifiResetStarted();
				} else if (BroadcastManager.BROADCAST_WIFIRESET_STOPPING.equals(action)) {
					onBroadcastWifiResetStopping();
				} else if (BroadcastManager.BROADCAST_WIFIRESET_NEXT_RESET.equals(action)) {
					Calendar cal;
					long nextResetTime;
				
					nextResetTime = intent.getLongExtra(BroadcastManager.PARAMETER_NEXT_RESET_TIME, 0);
					cal = Calendar.getInstance();
					cal.setTimeInMillis(nextResetTime);
				
					onBroadcastWifiResetNextReset(cal);
				}
			}
		};
	}
	
	private void registerBroadcasts() {
		String [] broadcastList = new String [] { BroadcastManager.BROADCAST_WIFI_ENABLED, 
				BroadcastManager.BROADCAST_WIFI_DISABLED, 
				BroadcastManager.BROADCAST_WIFI_IN_USE, 
				BroadcastManager.BROADCAST_WIFI_IDLE, 
				BroadcastManager.BROADCAST_WIFI_RESTARTED,
				BroadcastManager.BROADCAST_WIFIRESET_RESETING, 
				BroadcastManager.BROADCAST_WIFIRESET_STARTING, 
				BroadcastManager.BROADCAST_WIFIRESET_STARTED, 
				BroadcastManager.BROADCAST_WIFIRESET_STOPPING, 
				BroadcastManager.BROADCAST_WIFIRESET_NEXT_RESET };
		
		BroadcastManager.registerReceiverList(this, broadcastList, _broadcastReceiver);
	}
			
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		WifiResetManager.reset(this, true);
	
	    return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		BroadcastManager.unregisterReceiver(this, _broadcastReceiver);
		
		super.onDestroy();
	}

	// Broadcast 
	
	public void onBroadcastWifiIsEnabled() {
		HistoryManager.add(this, R.string.wifi_is_enabled);
		WifiResetNotificationManager.sendNotification(this, R.string.wifi_is_enabled);
	}

	public void onBroadcastWifiIsDisabled() {
		HistoryManager.add(this, R.string.wifi_is_disabled);
		WifiResetNotificationManager.sendNotification(this, R.string.wifi_is_disabled);
	}

	public void onBroadcastWifiIsInUse() {
		HistoryManager.add(this, R.string.wifi_is_in_use);
		WifiResetNotificationManager.sendNotification(this, R.string.wifi_is_in_use);
	}

	public void onBroadcastWifiIsIdle() {
		HistoryManager.add(this, R.string.wifi_is_idle);
		WifiResetNotificationManager.sendNotification(this, R.string.wifi_is_idle);
	}

	public void onBroadcastWifiRestarted() {
		HistoryManager.add(this, R.string.wifi_restarted);
		WifiResetNotificationManager.sendNotification(this, R.string.wifireset_reseting);
	}

	public void onBroadcastWifiResetReseting() {
		HistoryManager.add(this, R.string.wifireset_reseting);
		WifiResetNotificationManager.sendNotification(this, R.string.wifireset_reseting);
	}

	public void onBroadcastWifiResetStarting() {
		HistoryManager.add(this, R.string.wifireset_starting);
		WifiResetNotificationManager.sendNotification(this, R.string.wifireset_starting);
	}

	public void onBroadcastWifiResetStarted() {
		HistoryManager.add(this, R.string.wifireset_started);
		WifiResetNotificationManager.sendNotification(this, R.string.wifireset_started);
	}

	public void onBroadcastWifiResetStopping() {
		HistoryManager.add(this, R.string.wifireset_stopping);
		WifiResetNotificationManager.sendNotification(this, R.string.wifireset_stopping);
	}

	public void onBroadcastWifiResetNextReset(Calendar nextReset) {
		String nextResetFormatted;

		nextResetFormatted = Util.getCalendarFormatted(nextReset);

		HistoryManager.add(this, String.format(
				getString(R.string.wifireset_next_reset), nextResetFormatted));

		SettingsManager.setNextResetTime(this, nextReset);
		
		WifiResetNotificationManager.sendNotification(this, nextReset);
	}
}

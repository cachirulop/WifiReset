package com.cachirulop.wifireset.broadcast;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class WifiResetBroadcastReceiverManager {
	private IWifiResetBroadcastReceiver _receiver;

	// Wifi status messages
	private static final String BROADCAST_WIFI_ENABLED = "broadcast.wifi.enabled";
	private static final String BROADCAST_WIFI_DISABLED = "broadcast.wifi.disabled";
	private static final String BROADCAST_WIFI_IN_USE = "broadcast.wifi.inUse";
	private static final String BROADCAST_WIFI_IDLE = "broadcast.wifi.idle";
	private static final String BROADCAST_WIFI_RESTARTED = "broadcast.wifi.restarted";

	// History messages
	private static final String BROADCAST_HISTORY_MODIFIED = "broadcast.history.modified";
	
	// Wifi Reset service
	private static final String BROADCAST_WIFIRESET_RESETING = "broadcast.wifireset.reseting";
	private static final String BROADCAST_WIFIRESET_STARTING = "broadcast.wifireset.starting";
	private static final String BROADCAST_WIFIRESET_STARTED = "broadcast.wifireset.started";
	private static final String BROADCAST_WIFIRESET_STOPPING = "broadcast.wifireset.stopping";
	private static final String BROADCAST_WIFIRESET_NEXT_RESET = "broadcast.wifireset.nextReset";
	
	// Parameters
	private static final String PARAMETER_NEXT_RESET_TIME = "nextResetTime";

	public WifiResetBroadcastReceiverManager(Context ctx) {
		this(ctx, (IWifiResetBroadcastReceiver) ctx);
	}

	public WifiResetBroadcastReceiverManager(Context ctx,
			IWifiResetBroadcastReceiver receiver) {
		LocalBroadcastManager mgr;

		mgr = LocalBroadcastManager.getInstance(ctx);

		// Change wifi status
		mgr.registerReceiver(_broadcastReceiver, new IntentFilter(
				BROADCAST_WIFI_ENABLED));
		mgr.registerReceiver(_broadcastReceiver, new IntentFilter(
				BROADCAST_WIFI_DISABLED));
		mgr.registerReceiver(_broadcastReceiver, new IntentFilter(
				BROADCAST_WIFI_IN_USE));
		mgr.registerReceiver(_broadcastReceiver, new IntentFilter(
				BROADCAST_WIFI_IDLE));
		mgr.registerReceiver(_broadcastReceiver, new IntentFilter(
				BROADCAST_WIFI_RESTARTED));

		// History
		mgr.registerReceiver(_broadcastReceiver, new IntentFilter(
				BROADCAST_HISTORY_MODIFIED));

		// Wifi Reset service
		mgr.registerReceiver(_broadcastReceiver, new IntentFilter(
				BROADCAST_WIFIRESET_RESETING));
		mgr.registerReceiver(_broadcastReceiver, new IntentFilter(
				BROADCAST_WIFIRESET_STARTING));
		mgr.registerReceiver(_broadcastReceiver, new IntentFilter(
				BROADCAST_WIFIRESET_STARTED));
		mgr.registerReceiver(_broadcastReceiver, new IntentFilter(
				BROADCAST_WIFIRESET_STOPPING));
		mgr.registerReceiver(_broadcastReceiver, new IntentFilter(
				BROADCAST_WIFIRESET_NEXT_RESET));

		_receiver = receiver;
	}
	
	public void unregister (Context ctx) {
		LocalBroadcastManager mgr;

		mgr = LocalBroadcastManager.getInstance(ctx);
		
		mgr.unregisterReceiver(_broadcastReceiver);
	}

	private BroadcastReceiver _broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctx, Intent intent) {
			String action;

			action = intent.getAction();

			if (BROADCAST_WIFI_ENABLED.equals(action)) {
				_receiver.wifiIsEnabled(ctx);
			} else if (BROADCAST_WIFI_DISABLED.equals(action)) {
				_receiver.wifiIsDisabled(ctx);
			} else if (BROADCAST_WIFI_IN_USE.equals(action)) {
				_receiver.wifiIsInUse(ctx);
			} else if (BROADCAST_WIFI_IDLE.equals(action)) {
				_receiver.wifiIsIdle(ctx);
			} else if (BROADCAST_WIFI_RESTARTED.equals(action)) {
				_receiver.wifiRestarted(ctx);
			} else if (BROADCAST_HISTORY_MODIFIED.equals(action)) {
				_receiver.historyModified(ctx);
			} else if (BROADCAST_WIFIRESET_RESETING.equals(action)) {
				_receiver.wifiResetReseting(ctx);
			} else if (BROADCAST_WIFIRESET_STARTING.equals(action)) {
				_receiver.wifiResetStarting(ctx);
			} else if (BROADCAST_WIFIRESET_STARTED.equals(action)) {
				_receiver.wifiResetStarted(ctx);
			} else if (BROADCAST_WIFIRESET_STOPPING.equals(action)) {
				_receiver.wifiResetStopping(ctx);
			} else if (BROADCAST_WIFIRESET_NEXT_RESET.equals(action)) {
				Calendar cal;
				long nextResetTime;
			
				nextResetTime = intent.getLongExtra(PARAMETER_NEXT_RESET_TIME, 0);
				cal = Calendar.getInstance();
				cal.setTimeInMillis(nextResetTime);
			
				_receiver.wifiResetNextReset(ctx, cal);
			}
		}
	};

	public static void sendBroadcastWifiIsEnabled(Context ctx) {
		sendBroadcast(ctx, BROADCAST_WIFI_ENABLED);
	}

	public static void sendBroadcastWifiIsDisabled(Context ctx) {
		sendBroadcast(ctx, BROADCAST_WIFI_DISABLED);
	}

	public static void sendBroadcastWifiIsInUse(Context ctx) {
		sendBroadcast(ctx, BROADCAST_WIFI_IN_USE);
	}

	public static void sendBroadcastWifiIsIdle(Context ctx) {
		sendBroadcast(ctx, BROADCAST_WIFI_IDLE);
	}

	public static void sendBroadcastWifiRestarted(Context ctx) {
		sendBroadcast(ctx, BROADCAST_WIFI_RESTARTED);
	}

	public static void sendBroadcastHistoryModified(Context ctx) {
		sendBroadcast(ctx, BROADCAST_HISTORY_MODIFIED);
	}

	public static void sendBroadcastWifiResetReseting(Context ctx) {
		sendBroadcast(ctx, BROADCAST_WIFIRESET_RESETING);
	}
	
	public static void sendBroadcastWifiResetStarting(Context ctx) {
		sendBroadcast(ctx, BROADCAST_WIFIRESET_STARTING);
	}

	public static void sendBroadcastWifiResetStarted(Context ctx) {
		sendBroadcast(ctx, BROADCAST_WIFIRESET_STARTED);
	}

	public static void sendBroadcastWifiResetStopping(Context ctx) {
		sendBroadcast(ctx, BROADCAST_WIFIRESET_STOPPING);
	}

	public static void sendBroadcastWifiResetNextReset(Context ctx, Calendar nextReset) {
		sendBroadcast(ctx, BROADCAST_WIFIRESET_NEXT_RESET, PARAMETER_NEXT_RESET_TIME, nextReset.getTimeInMillis());
	}

	private static void sendBroadcast(Context ctx, String msg) {
		Intent intent = new Intent(msg);
		LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
	}
	
	private static void sendBroadcast(Context ctx, String msg, String extraName, long extraValue) {
		Intent intent = new Intent(msg);
		
		intent.putExtra(extraName, extraValue);
		
		LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
	}
}

package com.cachirulop.wifireset.manager;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;


public class BroadcastManager {
	// Wifi status messages
	public static final String BROADCAST_WIFI_ENABLED = "broadcast.wifi.enabled";
	public static final String BROADCAST_WIFI_DISABLED = "broadcast.wifi.disabled";
	public static final String BROADCAST_WIFI_IN_USE = "broadcast.wifi.inUse";
	public static final String BROADCAST_WIFI_IDLE = "broadcast.wifi.idle";
	public static final String BROADCAST_WIFI_RESTARTED = "broadcast.wifi.restarted";

	// History events
	public static final String BROADCAST_HISTORY_ADDED = "broadcast.history.added";
	public static final String BROADCAST_HISTORY_CLEANED = "broadcast.history.cleaned";

	// Wifi Reset service
	public static final String BROADCAST_WIFIRESET_RESETING = "broadcast.wifireset.reseting";
	public static final String BROADCAST_WIFIRESET_STARTING = "broadcast.wifireset.starting";
	public static final String BROADCAST_WIFIRESET_STARTED = "broadcast.wifireset.started";
	public static final String BROADCAST_WIFIRESET_STOPPING = "broadcast.wifireset.stopping";
	public static final String BROADCAST_WIFIRESET_NEXT_RESET = "broadcast.wifireset.nextReset";
	
	// Parameters
	public static final String PARAMETER_NEXT_RESET_TIME = "nextResetTime";

	public static void registerReceiverList (Context ctx, String [] broadcastTypes, BroadcastReceiver receiver) {
		LocalBroadcastManager mgr;

		mgr = LocalBroadcastManager.getInstance(ctx);

		for (String type : broadcastTypes) {
			mgr.registerReceiver(receiver, new IntentFilter(type));
		}
	}
	
	public static void registerReceiver (Context ctx, String broadcastType, BroadcastReceiver receiver) {
		LocalBroadcastManager mgr;

		mgr = LocalBroadcastManager.getInstance(ctx);

		mgr.registerReceiver(receiver, new IntentFilter(broadcastType));
	}
	
	public static void unregisterReceiver (Context ctx, BroadcastReceiver receiver) {
		LocalBroadcastManager mgr;

		mgr = LocalBroadcastManager.getInstance(ctx);
		
		mgr.unregisterReceiver(receiver);
	}
	
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
	
	public static void sendBroadcastHistoryAdded(Context ctx) {
		sendBroadcast(ctx, BROADCAST_HISTORY_ADDED);
	}

	public static void sendBroadcastHistoryCleared(Context ctx) {
		sendBroadcast(ctx, BROADCAST_HISTORY_CLEANED);
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

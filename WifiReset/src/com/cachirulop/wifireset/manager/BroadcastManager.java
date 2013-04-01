package com.cachirulop.wifireset.manager;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.cachirulop.wifireset.entity.History;


public class BroadcastManager {
	// History events
	public static final String BROADCAST_HISTORY_ADDED = "broadcast.history.added";
	public static final String BROADCAST_HISTORY_CLEANED = "broadcast.history.cleaned";

	// Wifi Reset service
	public static final String BROADCAST_WIFIRESET_NEXT_RESET = "broadcast.wifireset.nextReset";
	
	// Parameters
	public static final String PARAMETER_NEXT_RESET_TIME = "nextResetTime";
	public static final String PARAMETER_HISTORY_MESSAGE = "historyMessage";

	public static void registerReceiverList (Context ctx, String [] broadcastTypes, BroadcastReceiver receiver) {
		LocalBroadcastManager mgr;

		mgr = LocalBroadcastManager.getInstance(ctx);

		for (String type : broadcastTypes) {
			mgr.registerReceiver(receiver, new IntentFilter(type));
		}
	}
	
	public static void registerReceiver (Context ctx, String broadcastType, BroadcastReceiver receiver) {
		registerReceiverList (ctx, new String [] { broadcastType }, receiver);
	}
	
	public static void unregisterReceiver (Context ctx, BroadcastReceiver receiver) {
		LocalBroadcastManager mgr;

		mgr = LocalBroadcastManager.getInstance(ctx);
		
		mgr.unregisterReceiver(receiver);
	}

	public static void sendBroadcastWifiResetNextReset(Context ctx, Calendar nextReset) {
		if (nextReset != null) {
			sendBroadcast(ctx, BROADCAST_WIFIRESET_NEXT_RESET, PARAMETER_NEXT_RESET_TIME, nextReset.getTimeInMillis());
		}
		else {
			sendBroadcast(ctx, BROADCAST_WIFIRESET_NEXT_RESET, PARAMETER_NEXT_RESET_TIME, 0);
		}
	}
	
	public static void sendBroadcastHistoryAdded(Context ctx, History h) {
		sendBroadcast(ctx, BROADCAST_HISTORY_ADDED, PARAMETER_HISTORY_MESSAGE, h.getMessage());
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
	
	private static void sendBroadcast(Context ctx, String msg, String extraName, String extraValue) {
		Intent intent = new Intent(msg);
		
		intent.putExtra(extraName, extraValue);
		
		LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
	}
}

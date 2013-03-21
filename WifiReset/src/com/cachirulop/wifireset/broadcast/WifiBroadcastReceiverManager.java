package com.cachirulop.wifireset.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class WifiBroadcastReceiverManager {
	private IWifiBroadcastReceiver _receiver;

	private static final String BROADCAST_WIFI_ENABLED = "broadcast.wifireset.enabled";
	private static final String BROADCAST_WIFI_DISABLED = "broadcast.wifireset.disabled";
	private static final String BROADCAST_WIFI_IN_USE = "broadcast.wifireset.inUse";
	private static final String BROADCAST_WIFI_IDLE = "broadcast.wifireset.idle";
	private static final String BROADCAST_WIFI_RESTARTED = "broadcast.wifireset.restarted";

	public WifiBroadcastReceiverManager(Context ctx) {
		this(ctx, (IWifiBroadcastReceiver) ctx);
	}

	public WifiBroadcastReceiverManager(Context ctx,
			IWifiBroadcastReceiver receiver) {
		LocalBroadcastManager mgr;

		mgr = LocalBroadcastManager.getInstance(ctx);

		mgr.registerReceiver(_wifiEnabledReceiver, new IntentFilter(BROADCAST_WIFI_ENABLED));
		mgr.registerReceiver(_wifiDisabledReceiver, new IntentFilter(BROADCAST_WIFI_DISABLED));
		mgr.registerReceiver(_wifiIsInUseReceiver, new IntentFilter(BROADCAST_WIFI_IN_USE));
		mgr.registerReceiver(_wifiIsIdleReceiver, new IntentFilter(BROADCAST_WIFI_IDLE));
		mgr.registerReceiver(_wifiRestartedReceiver, new IntentFilter(BROADCAST_WIFI_RESTARTED));

		_receiver = receiver;
	}

	private BroadcastReceiver _wifiEnabledReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			_receiver.wifiIsEnabled();
		}
	};
	private BroadcastReceiver _wifiDisabledReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			_receiver.wifiIsDisabled();
		}
	};

	private BroadcastReceiver _wifiIsInUseReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			_receiver.wifiIsInUse();
		}
	};
	private BroadcastReceiver _wifiIsIdleReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			_receiver.wifiIsIdle();
		}
	};
	private BroadcastReceiver _wifiRestartedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			_receiver.wifiRestarted();
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
	
	private static void sendBroadcast(Context ctx, String msg) {
		Intent intent = new Intent(msg);
		LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);		
	}	
}

package com.cachirulop.wifireset.service;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.cachirulop.wifireset.common.Util;
import com.cachirulop.wifireset.manager.BroadcastManager;
import com.cachirulop.wifireset.manager.SettingsManager;
import com.cachirulop.wifireset.manager.WifiResetManager;
import com.cachirulop.wifireset.manager.WifiResetNotificationManager;

public class WifiResetService extends Service {
	private BroadcastReceiver _broadcastReceiver;
	
	@Override
	public void onCreate() {
		Log.d("WifiReset", "WifiResetService.onCreate");
		
		createBroadcastReceiver();

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("WifiReset", "WifiResetService.onStartCommand");
		
		WifiResetManager.reset(this);
		
		registerAlarm (this);

		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		Log.d("WifiReset", "WifiResetService.onDestroy");

		BroadcastManager.unregisterReceiver(this, _broadcastReceiver);
		
		super.onDestroy();
	}
	
	private void createBroadcastReceiver() {
		_broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context ctx, Intent intent) {
				String action;

				action = intent.getAction();

				if (BroadcastManager.BROADCAST_HISTORY_ADDED.equals(action)) {
					String msg;
					
					msg = intent.getStringExtra(BroadcastManager.PARAMETER_HISTORY_MESSAGE);
					
					WifiResetNotificationManager.sendNotification(ctx, msg);
				} else if (BroadcastManager.BROADCAST_WIFIRESET_NEXT_RESET
						.equals(action)) {
					Calendar cal;
					long nextResetTime;

					nextResetTime = intent.getLongExtra(BroadcastManager.PARAMETER_NEXT_RESET_TIME, 0);
					cal = Calendar.getInstance();
					cal.setTimeInMillis(nextResetTime);

					WifiResetNotificationManager.sendNotification(ctx, cal);
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
	
	public static void activate(Context ctx) {
		registerAlarm(ctx);
	}
	
	public static void deactivate(Context ctx) {
		unregisterAlarm(ctx);
	}	
	
	public static void registerAlarm (Context ctx) {
		PendingIntent pIntent;
		Intent svcIntent;
		AlarmManager mgr;
		Calendar cal;
		int interval;

		Log.d("WifiReset", "Registering alarm");
		mgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		
		svcIntent = new Intent(ctx, WifiResetService.class);
		pIntent = PendingIntent.getService(ctx, 0, svcIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		interval = SettingsManager.getInterval(ctx);
		
		cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.MINUTE, interval);

		BroadcastManager.sendBroadcastWifiResetNextReset(ctx, cal);

		Log.d("WifiReset", "Next reset: " + Util.getCalendarFormatted(cal));
		
		mgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pIntent);
	}
	
	public static boolean isServiceRunning (Context ctx) {
		PendingIntent pIntent;
		Intent svcIntent;

		svcIntent = new Intent(ctx, WifiResetService.class);
		pIntent = PendingIntent.getService(ctx, 0, svcIntent, PendingIntent.FLAG_NO_CREATE);
		
		return pIntent != null;
	}
	
	public static void unregisterAlarm(Context ctx) {
		PendingIntent pIntent;
		Intent svcIntent;
		AlarmManager mgr;

		Log.d("WifiReset", "Unregistering alarm");
		
		mgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		
		svcIntent = new Intent(ctx, WifiResetService.class);
		pIntent = PendingIntent.getService(ctx, 0, svcIntent, PendingIntent.FLAG_NO_CREATE);
		if (pIntent != null) {
			Log.d("WifiReset", "Service is running, cancel alarm and intent");

			BroadcastManager.sendBroadcastWifiResetNextReset(ctx, null);

			mgr.cancel(pIntent);
			ctx.stopService(svcIntent);
			pIntent.cancel();
		}
		else {
			Log.d("WifiReset", "Service is not running, do nothing");
		}
	}
}

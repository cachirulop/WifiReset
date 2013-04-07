package com.cachirulop.wifireset.service;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
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

public class WifiResetService extends IntentService {
	public static final String ACTION_START = "action.start";
	public static final String ACTION_ALARM = "action.alarm";

	public WifiResetService(String name) {
		super(name);
	}
	
	public WifiResetService() {
		super(WifiResetService.class.getSimpleName());		
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		String action;
		
		action = intent.getAction();
		
		Log.d(WifiResetService.class.getSimpleName(), 
				String.format("WifiResetService.onStartCommand: %s", action));
		
		if (ACTION_START.equals(action)) {
			if (!isServiceRunning()) {
				registerAlarm();
			}
		}
		else if (ACTION_ALARM.equals(action)) {
			WifiResetManager.reset(this);
			registerAlarm();
		}
	}
	
	public void registerAlarm () {
		PendingIntent pIntent;
		Intent svcIntent;
		AlarmManager mgr;
		Calendar cal;
		int interval;

		Log.d(WifiResetService.class.getSimpleName(), "Registering alarm");
		mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		
		svcIntent = new Intent(this, WifiResetService.class);
		svcIntent.setAction(ACTION_ALARM);
		
		pIntent = PendingIntent.getService(this, 0, svcIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		interval = SettingsManager.getInterval(this);
		
		cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.MINUTE, interval);

		BroadcastManager.sendBroadcastWifiResetNextReset(this, cal);
		SettingsManager.setNextResetTime(this, cal);

		Log.d(WifiResetService.class.getSimpleName(), "Next reset: " + Util.getCalendarFormatted(cal));
		
		mgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pIntent);
	}
	
	public static void unregisterAlarm(Context ctx) {
		PendingIntent pIntent;
		Intent svcIntent;
		AlarmManager mgr;

		Log.d(WifiResetService.class.getSimpleName(), "Unregistering alarm");
		
		mgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		
		svcIntent = new Intent(ctx, WifiResetService.class);
		pIntent = PendingIntent.getService(ctx, 0, svcIntent, PendingIntent.FLAG_NO_CREATE);

		BroadcastManager.sendBroadcastWifiResetNextReset(ctx, null);
		SettingsManager.setNextResetTime(ctx, null);

		mgr.cancel(pIntent);
		ctx.stopService(svcIntent);
		pIntent.cancel();
	}
	
	public boolean isServiceRunning () {
		Calendar nextReset;
		
		nextReset = SettingsManager.getNextResetTime(this);
		
		return (nextReset != null) & (Calendar.getInstance().before(nextReset));				
	}
}

package com.cachirulop.wifireset.manager;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Handler;

import com.cachirulop.wifireset.broadcast.WifiResetBroadcastReceiverManager;
import com.cachirulop.wifireset.common.Util;
import com.cachirulop.wifireset.service.WifiResetService;

/**
 * Manage the wifi connection to make resets.
 * 
 * @author david
 */
public class WifiResetManager {
	/** Intent executed by the android AlarmManager to reset the wifi connection */
	private static PendingIntent _alarmIntent;
	
	/**
	 * Reset the wifi connection if it is inactive.
	 */
	public static void reset (final Context ctx) {
		WifiResetBroadcastReceiverManager.sendBroadcastWifiResetReseting(ctx);
		
		resetWifi(ctx);
	}
	
	/**
	 * Reset the wifi connection if the is idle. 
	 * Test the wifi status taking statistics of use, waiting 5 seconds
	 * and reading the statistics again. 
	 * 
	 * @param ctx Execution context of the method (e.g. the Activity)
	 */
	private static void resetWifi(final Context ctx) {
		final WifiManager mgr;
		
		mgr = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		if (mgr.isWifiEnabled() || Util.isEmulator()) {
			final long rxBytes;
			final long txBytes;
			
			WifiResetBroadcastReceiverManager.sendBroadcastWifiIsEnabled(ctx);

			rxBytes = TrafficStats.getTotalRxBytes();
			txBytes = TrafficStats.getTotalTxBytes();

			// Wait 5 seconds to test again the statistics
			Handler h;
			
			h = new Handler();
			
			h.postDelayed(new Runnable () {
				public void run () {
					long newRxBytes;
					long newTxBytes;

					newRxBytes = TrafficStats.getTotalRxBytes();
					newTxBytes = TrafficStats.getTotalTxBytes();
					
					if (rxBytes != newRxBytes || txBytes != newTxBytes) {
						WifiResetBroadcastReceiverManager.sendBroadcastWifiIsInUse(ctx);
					}
					else {
						WifiResetBroadcastReceiverManager.sendBroadcastWifiIsIdle(ctx);
						
						mgr.setWifiEnabled(false);
						mgr.setWifiEnabled(true);
						
						WifiResetBroadcastReceiverManager.sendBroadcastWifiRestarted(ctx);
					}
				}
			}
			, 5000);
		}
		else {
			WifiResetBroadcastReceiverManager.sendBroadcastWifiIsDisabled(ctx);
		}
	}
	
	/**
	 * Start the service to reset the wifi connection after a time interval
	 */
	public static void startService (Context ctx) {
    	Intent wifiIntent;
    	AlarmManager mgr;
        Calendar cal;
        int interval;
        
		WifiResetBroadcastReceiverManager.sendBroadcastWifiResetStarting(ctx);
    	
        interval = SettingsManager.getInterval(ctx);
    	wifiIntent = new Intent(ctx, WifiResetService.class);
    	_alarmIntent = PendingIntent.getService(ctx, 0, wifiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	
    	mgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
    	
    	cal = Calendar.getInstance();
    	cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.SECOND, interval);
        
		WifiResetBroadcastReceiverManager.sendBroadcastWifiResetNextReset(ctx, cal);
/*
        HistoryManager.add(ctx, String.format("%s %s", 
        		ctx.getString(R.string.next_reset), 
        		DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(cal.getTime())));
*/        
        mgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), interval * 1000, _alarmIntent);
	}
	
	/**
	 * Stop the wifi service, stopping the alarm
	 * 
	 * @param ctx Execution context (e.g. Activity)
	 */
	public static void stopService (Context ctx) {
		WifiResetBroadcastReceiverManager.sendBroadcastWifiResetStopping(ctx);
	}
}

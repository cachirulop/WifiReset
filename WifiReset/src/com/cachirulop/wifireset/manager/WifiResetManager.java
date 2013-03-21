package com.cachirulop.wifireset.manager;

import java.text.DateFormat;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import com.cachirulop.wifireset.R;
import com.cachirulop.wifireset.broadcast.WifiBroadcastReceiverManager;
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
		HistoryManager.add(ctx, R.string.wifi_reseting);
		
		resetWifi(ctx);
	}
	
	private static void resetWifi(final Context ctx) {
		final WifiManager mgr;
		
		mgr = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		if (mgr.isWifiEnabled() || Util.isEmulator()) {
			final long rxBytes;
			final long txBytes;
			
			WifiBroadcastReceiverManager.sendBroadcastWifiIsEnabled(ctx);

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
						WifiBroadcastReceiverManager.sendBroadcastWifiIsInUse(ctx);
					}
					else {
						WifiBroadcastReceiverManager.sendBroadcastWifiIsIdle(ctx);
						
						mgr.reassociate();
						
						WifiBroadcastReceiverManager.sendBroadcastWifiRestarted(ctx);
					}
				}
			}
			, 5000);
		}
		else {
			WifiBroadcastReceiverManager.sendBroadcastWifiIsDisabled(ctx);
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
        
        HistoryManager.add(ctx, R.string.starting_service);
    	
        interval = SettingsManager.getInterval(ctx);
    	wifiIntent = new Intent(ctx, WifiResetService.class);
    	_alarmIntent = PendingIntent.getService(ctx, 0, wifiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	
    	mgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
    	
    	cal = Calendar.getInstance();
    	cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.SECOND, interval);
        
        HistoryManager.add(ctx, String.format("%s %s", 
        		ctx.getString(R.string.next_reset), 
        		DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(cal.getTime())));
        
        mgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), interval * 1000, _alarmIntent);
	}
	
}

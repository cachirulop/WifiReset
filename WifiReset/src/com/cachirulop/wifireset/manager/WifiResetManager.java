package com.cachirulop.wifireset.manager;

import java.text.DateFormat;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;

import com.cachirulop.wifireset.R;
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
	public static void reset (Context ctx) {
		HistoryManager.add(ctx, R.string.wifi_reseting);

		WifiManager mgr;
		
		mgr = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		if (mgr.isWifiEnabled()) {
			long rxBytes;
			long txBytes;
			
			HistoryManager.add(ctx, "Wifi is enabled");

			rxBytes = TrafficStats.getTotalRxBytes();
			txBytes = TrafficStats.getTotalTxBytes();
			
			HistoryManager.add(ctx, String.format("Bytes: %d received, %d sended", rxBytes, txBytes));
			
			try {
				Thread.sleep(5000);
			}
			catch (InterruptedException ex) { }
			
			long newRxBytes;
			long newTxBytes;

			newRxBytes = TrafficStats.getTotalRxBytes();
			newTxBytes = TrafficStats.getTotalTxBytes();
			
			HistoryManager.add(ctx, String.format("Bytes: %d received, %d sended", newRxBytes, newTxBytes));

			if (rxBytes != newRxBytes || txBytes != newTxBytes) {
				HistoryManager.add(ctx, "Wifi in usage");
			}
			else {
				HistoryManager.add(ctx, "Wifi is idle, restarting");
				
				mgr.reassociate();
				
				HistoryManager.add(ctx, "RESTARTED");
			}
		}
		else {
			HistoryManager.add(ctx, "Wifi is not enabled");
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

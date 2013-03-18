package com.cachirulop.wifireset.manager;

import java.text.DateFormat;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;

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
	
	public static final String BROADCAST_WIFIRESET_ENABLED = "broadcast.wifireset.enabled";
	public static final String BROADCAST_WIFIRESET_DISABLED = "broadcast.wifireset.disabled";
	public static final String BROADCAST_WIFIRESET_IN_USE = "broadcast.wifireset.inUse";
	public static final String BROADCAST_WIFIRESET_IDLE = "broadcast.wifireset.idle";
	public static final String BROADCAST_WIFIRESET_RESTARTED = "broadcast.wifireset.restarted";

	/**
	 * Reset the wifi connection if it is inactive.
	 */
	public static void reset (final Context ctx) {
		HistoryManager.add(ctx, R.string.wifi_reseting);
		Thread resetThread;
		
		resetThread = new Thread(new Runnable () {
			public void run () {
				resetWifi (ctx);
			}
		});
		
		resetThread.run();
	}
	
	private static void resetWifi(Context ctx) {
		WifiManager mgr;
		
		mgr = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		if (mgr.isWifiEnabled()) {
			long rxBytes;
			long txBytes;
			long newRxBytes;
			long newTxBytes;
			
			sendBroadcast(ctx, BROADCAST_WIFIRESET_ENABLED);

			rxBytes = TrafficStats.getTotalRxBytes();
			txBytes = TrafficStats.getTotalTxBytes();
			
			try {
				// Wait 5 seconds to read the traffic statistics again
				Thread.sleep(5000);
			}
			catch (InterruptedException ex) { }
			
			newRxBytes = TrafficStats.getTotalRxBytes();
			newTxBytes = TrafficStats.getTotalTxBytes();
			
			if (rxBytes != newRxBytes || txBytes != newTxBytes) {
				sendBroadcast(ctx, BROADCAST_WIFIRESET_IN_USE);
			}
			else {
				sendBroadcast(ctx, BROADCAST_WIFIRESET_IDLE);
				
				mgr.reassociate();
				
				sendBroadcast(ctx, BROADCAST_WIFIRESET_RESTARTED);
			}
		}
		else {
			sendBroadcast(ctx, BROADCAST_WIFIRESET_DISABLED);
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
	
	private static void sendBroadcast(Context ctx, String msg) {
		Intent intent = new Intent(msg);
		LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);		
	}	
}

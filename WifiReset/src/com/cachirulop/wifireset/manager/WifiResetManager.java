package com.cachirulop.wifireset.manager;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Handler;

import com.cachirulop.wifireset.common.Util;
import com.cachirulop.wifireset.service.WifiResetService;

/**
 * Manage the wifi connection to make resets.
 * 
 * @author David
 */
public class WifiResetManager {
	/**
	 * Reset the wifi connection if it is inactive.
	 */
	public static void reset(final Context ctx, boolean registerNextAlarm) {
		final WifiManager mgr;

		BroadcastManager.sendBroadcastWifiResetReseting(ctx);

		mgr = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		if (mgr.isWifiEnabled() || Util.isEmulator()) {
			BroadcastManager.sendBroadcastWifiIsEnabled(ctx);

			resetWifi(ctx, mgr);
		} else {
			BroadcastManager.sendBroadcastWifiIsDisabled(ctx);
		}

		if (registerNextAlarm) {
			registerNextAlarm(ctx);
		}
	}

	/**
	 * Reset the wifi connection if the is idle. Test the wifi status taking
	 * statistics of use, waiting 5 seconds and reading the statistics again.
	 * 
	 * @param ctx
	 *            Execution context of the method (e.g. the Activity)
	 */
	private static void resetWifi(final Context ctx, final WifiManager mgr) {
		final long rxBytes;
		final long txBytes;

		rxBytes = TrafficStats.getTotalRxBytes();
		txBytes = TrafficStats.getTotalTxBytes();

		// Wait 5 seconds to test again the statistics
		Handler h;

		h = new Handler();

		h.postDelayed(new Runnable() {
			public void run() {
				long newRxBytes;
				long newTxBytes;

				newRxBytes = TrafficStats.getTotalRxBytes();
				newTxBytes = TrafficStats.getTotalTxBytes();

				if (rxBytes != newRxBytes || txBytes != newTxBytes) {
					BroadcastManager.sendBroadcastWifiIsInUse(ctx);
				} else {
					BroadcastManager.sendBroadcastWifiIsIdle(ctx);

					// mgr.setWifiEnabled(false);
					// mgr.setWifiEnabled(true);
					mgr.reassociate();

					BroadcastManager.sendBroadcastWifiRestarted(ctx);
				}
			}
		}, 5000);
	}

	/**
	 * Start the service to reset the wifi connection after a time interval
	 */
	public static void startService(Context ctx) {
		if (!isServiceStarted(ctx)) {
			BroadcastManager.sendBroadcastWifiResetStarting(ctx);

			reset(ctx, true);
		} else {
			BroadcastManager.sendBroadcastWifiResetStarted(ctx);
		}
	}

	/**
	 * Register next alarm in the android system to reset the wifi after the
	 * configured interval
	 * 
	 * @param ctx
	 *            Execution context (e.g. the Activity)
	 */
	private static void registerNextAlarm(Context ctx) {
		PendingIntent intent;
		AlarmManager mgr;
		Calendar cal;
		int interval;

		interval = SettingsManager.getInterval(ctx);
		intent = getAlarmIntent(ctx);
		mgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

		cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.add(Calendar.MINUTE, interval);

		BroadcastManager.sendBroadcastWifiResetNextReset(ctx, cal);

		mgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), intent);
	}

	/**
	 * Test if the wifi reset service is running or not
	 * 
	 * @param ctx
	 *            Execution context (e.g. the Activity)
	 * @return true if the service is running
	 */
	public static boolean isServiceStarted(Context ctx) {
		Intent wifiIntent;
		PendingIntent intent;

		wifiIntent = new Intent(ctx, WifiResetService.class);
/*
		intent = PendingIntent.getService(ctx, 0, wifiIntent,
				PendingIntent.FLAG_NO_CREATE);
*/
		intent = PendingIntent.getBroadcast(ctx, 0, wifiIntent, PendingIntent.FLAG_NO_CREATE);

		return (intent != null);
	}

	/**
	 * Stop the wifi service, stopping the alarm
	 * 
	 * @param ctx
	 *            Execution context (e.g. Activity)
	 */
	public static void stopService(Context ctx) {
		BroadcastManager.sendBroadcastWifiResetStopping(ctx);
		PendingIntent intent;
		AlarmManager mgr;

		intent = getAlarmIntent(ctx);

		mgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

		mgr.cancel(intent);
		intent.cancel();
	}

	/**
	 * Creates the intent executed by the Alarm. It will be a
	 * {@link PendingIntent}PendingIntent based on the {@link WifiResetService}
	 * WifiResetService class.
	 * 
	 * @param ctx
	 *            Execution context
	 * @return New PendingIntent based on the {@link WifiResetService}
	 *         WifiResetService class
	 */
	private static PendingIntent getAlarmIntent(Context ctx) {
		Intent wifiIntent;

		wifiIntent = new Intent(ctx, WifiResetService.class);
/*
		return PendingIntent.getService(ctx, 0, wifiIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
*/
		return PendingIntent.getBroadcast(ctx, 0, wifiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
}

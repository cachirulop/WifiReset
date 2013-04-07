package com.cachirulop.wifireset.manager;

import android.content.Context;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Handler;

import com.cachirulop.wifireset.R;
import com.cachirulop.wifireset.common.Util;

/**
 * Manage the wifi connection to make resets.
 * 
 * @author David
 */
public class WifiResetManager {
	/**
	 * Reset the wifi connection if it is inactive.
	 */
	public static void reset(final Context ctx) {
		final WifiManager mgr;

		HistoryManager.add(ctx, R.string.wifireset_reseting);

		mgr = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		if (mgr.isWifiEnabled() || Util.isEmulator()) {
			HistoryManager.add(ctx, R.string.wifi_is_enabled);

			resetWifi(ctx, mgr);
		} else {
			HistoryManager.add(ctx, R.string.wifi_is_disabled);
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
					HistoryManager.add(ctx, R.string.wifi_is_in_use);
				} else {
					HistoryManager.add(ctx, R.string.wifi_is_idle);

					mgr.reassociate();

					HistoryManager.add(ctx, R.string.wifi_restarted);
				}
				
				BroadcastManager.sendBroadcastWifiResetDone(ctx);
			}
		}, 5000);
	}
}

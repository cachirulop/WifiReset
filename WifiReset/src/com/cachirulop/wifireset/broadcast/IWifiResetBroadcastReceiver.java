package com.cachirulop.wifireset.broadcast;

import java.util.Calendar;

import android.content.Context;

public interface IWifiResetBroadcastReceiver {
	// Wifi status 
	void wifiIsEnabled(Context ctx);
	void wifiIsDisabled(Context ctx);
	void wifiIsInUse(Context ctx);
	void wifiIsIdle(Context ctx);
	void wifiRestarted(Context ctx);
	
	// History
	void historyModified(Context ctx);
	
	// Wifi Reset service
	void wifiResetReseting(Context ctx);
	void wifiResetStarting(Context ctx);
	void wifiResetStarted(Context ctx);
	void wifiResetStopping(Context ctx);
	void wifiResetNextReset(Context ctx, Calendar nextReset);
}

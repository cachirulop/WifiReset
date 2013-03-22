package com.cachirulop.wifireset.broadcast;

import java.util.Calendar;

public interface IWifiResetBroadcastReceiver {
	// Wifi status 
	void wifiIsEnabled();
	void wifiIsDisabled();
	void wifiIsInUse();
	void wifiIsIdle();
	void wifiRestarted();
	
	// History
	void historyModified();
	
	// Wifi Reset service
	void wifiResetReseting();
	void wifiResetStarting();
	void wifiREsetStopping();
	void wifiResetNextReset(Calendar nextReset);
}

package com.cachirulop.wifireset.broadcast;

public interface IWifiBroadcastReceiver {
	void wifiIsEnabled();
	void wifiIsDisabled();
	void wifiIsInUse();
	void wifiIsIdle();
	void wifiRestarted();
}

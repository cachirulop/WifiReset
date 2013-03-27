package com.cachirulop.wifireset.service;

import java.util.Calendar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.cachirulop.wifireset.R;
import com.cachirulop.wifireset.broadcast.IWifiResetBroadcastReceiver;
import com.cachirulop.wifireset.broadcast.WifiResetBroadcastReceiverManager;
import com.cachirulop.wifireset.common.Util;
import com.cachirulop.wifireset.manager.WifiResetManager;
import com.cachirulop.wifireset.manager.WifiResetNotificationManager;

public class WifiResetService extends Service  implements IWifiResetBroadcastReceiver {
	WifiResetBroadcastReceiverManager _broadcastManager;
	
	public WifiResetService () {
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		_broadcastManager = new WifiResetBroadcastReceiverManager(this, this);
	    
		WifiResetManager.reset(this, true);
	
	    return super.onStartCommand(intent, flags, startId);
	}
		
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		if (_broadcastManager != null) {
			_broadcastManager.unregister(this);
			_broadcastManager = null;
		}

		super.onDestroy();
	}

	/**********************************************************************/
	/* IWifiResetBroadcastReceiver implementation
	/**********************************************************************/
	
	@Override
	public void wifiIsEnabled(Context ctx) {
		WifiResetNotificationManager.sendNotification(this, null, this.getString(R.string.wifi_is_enabled));
	}

	@Override
	public void wifiIsDisabled(Context ctx) {
		WifiResetNotificationManager.sendNotification(this, null, this.getString(R.string.wifi_is_disabled));
	}

	@Override
	public void wifiIsInUse(Context ctx) {
		WifiResetNotificationManager.sendNotification(this, null, this.getString(R.string.wifi_is_in_use));
	}

	@Override
	public void wifiIsIdle(Context ctx) {
		WifiResetNotificationManager.sendNotification(this, null, this.getString(R.string.wifi_is_idle));
	}

	@Override
	public void wifiRestarted(Context ctx) {
		WifiResetNotificationManager.sendNotification(this, null, this.getString(R.string.wifireset_reseting));
	}

	@Override
	public void historyModified(Context ctx) {
		// Nothing to do
	}

	@Override
	public void wifiResetReseting(Context ctx) {
		WifiResetNotificationManager.sendNotification(this, null, this.getString(R.string.wifireset_reseting));
	}

	@Override
	public void wifiResetStarting(Context ctx) {
		WifiResetNotificationManager.sendNotification(this, null, this.getString(R.string.wifireset_starting));
	}

	@Override
	public void wifiResetStarted(Context ctx) {
		WifiResetNotificationManager.sendNotification(this, null, this.getString(R.string.wifireset_started));
	}

	@Override
	public void wifiResetStopping(Context ctx) {
		WifiResetNotificationManager.sendNotification(this, null, this.getString(R.string.wifireset_stopping));
	}

	@Override
	public void wifiResetNextReset(Context ctx, Calendar nextReset) {
		WifiResetNotificationManager.sendNotification(this, 
				getNextResetString(this, nextReset), this.getString(R.string.wifireset_reseting));
	}

	private static String getNextResetString (Context ctx, Calendar nextReset) {
		// return String.format (ctx.getString(R.string.wifireset_next_reset), 
		//		   Util.getCalendarFormatted(nextReset));
		return Util.getCalendarFormatted(nextReset);
	}

}

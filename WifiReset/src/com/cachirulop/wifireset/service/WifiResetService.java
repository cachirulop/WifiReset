package com.cachirulop.wifireset.service;

import com.cachirulop.wifireset.R;
import com.cachirulop.wifireset.manager.HistoryManager;
import com.cachirulop.wifireset.manager.WifiResetManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class WifiResetService extends Service {
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "WifiResetService.onStartCommand()", Toast.LENGTH_LONG).show();
		
	    WifiResetManager.reset(this);
	
	    return super.onStartCommand(intent,flags,startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Toast.makeText(this, "WifiResetService.onBind()", Toast.LENGTH_LONG)
				.show();

		return null;
	}	
	
/*
	@Override
	public void onCreate() {
		Toast.makeText(this, "WifiResetService.onCreate()", Toast.LENGTH_LONG)
				.show();

	}



	@Override
	public void onDestroy() {
		super.onDestroy();

		Toast.makeText(this, "WifiResetService.onDestroy()", Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		Toast.makeText(this, "WifiResetService.onStart()", Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Toast.makeText(this, "WifiResetService.onUnbind()", Toast.LENGTH_LONG)
				.show();

		return super.onUnbind(intent);
	}
*/
}

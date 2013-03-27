package com.cachirulop.wifireset.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.cachirulop.wifireset.R;

public class WifiResetNotificationManager {
	private final static int NOTIFICATION_WIFIRESET = 1; 


	/*	
	public static void register (Context ctx) {
		sendFirstNotification (ctx);		
	}

	private static void sendFirstNotification (Context ctx) {
		sendNotification (ctx, getNextResetString(ctx, SettingsManager.getNextResetTime(ctx)), null);
	}
*/	
	public static void sendNotification(Context ctx, String title, String content) {
		Notification.Builder builder;
		NotificationManager notificationManager;
		
		Intent intent = new Intent(ctx, ctx.getClass());
		PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, intent, 0);
		
		builder = new Notification.Builder(ctx);
		
		if (title != null) {
			builder.setContentTitle(title);
		}
	
		if (content != null) {
			builder.setContentText(content);
		}
		else {
			builder.setContentText("OEOEO");
		}
		
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentIntent(pIntent);
		
		notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_WIFIRESET, builder.getNotification());
	}
	

}

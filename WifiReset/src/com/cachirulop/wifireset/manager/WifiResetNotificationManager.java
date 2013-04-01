package com.cachirulop.wifireset.manager;

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.cachirulop.wifireset.R;
import com.cachirulop.wifireset.activity.MainActivity;
import com.cachirulop.wifireset.common.Util;

public class WifiResetNotificationManager {
	private final static int NOTIFICATION_WIFIRESET = 1; 

	public static void start (Context ctx) {
		int resId;
		
		if (SettingsManager.isActive(ctx)) {
			resId = R.string.wifireset_activated;
		}
		else {
			resId = R.string.wifireset_deactivated;
		}
		
		sendNotification (ctx, SettingsManager.getNextResetTime(ctx), resId);
	}

	private static String getNextResetString (Context ctx, Calendar nextReset) {
		// return String.format (ctx.getString(R.string.wifireset_next_reset), 
		//		   Util.getCalendarFormatted(nextReset));
		return Util.getCalendarFormatted(nextReset);
	}
	
	public static void sendNotification(Context ctx, Calendar nextReset) {
		sendNotification(ctx, nextReset, null);
	}

	public static void sendNotification(Context ctx, int resId) {
		sendNotification(ctx, null, resId);
	}

	public static void sendNotification(Context ctx, String msg) {
		sendNotification(ctx, null, msg);
	}

	public static void sendNotification(Context ctx, Calendar nextReset, int resId) {
		sendNotification (ctx, nextReset, ctx.getString(resId));
	}
	
	public static void sendNotification(Context ctx, Calendar nextReset, String msg) {
		Notification.Builder builder;
		NotificationManager notificationManager;
		
		Intent intent = new Intent(ctx, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, intent, 0);
		
		builder = new Notification.Builder(ctx);
		
		if (nextReset != null) {
			if (nextReset.getTimeInMillis() == 0) {
				builder.setContentTitle(ctx.getText(R.string.wifireset_deactivated));
			}
			else {
				builder.setContentTitle(getNextResetString(ctx, nextReset));	
			}
		}
	
		if (msg != null) {
			builder.setContentText(msg);
		}
		
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentIntent(pIntent);
		
		notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_WIFIRESET, builder.getNotification());
	}
}

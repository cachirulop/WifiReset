package com.cachirulop.wifireset.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cachirulop.wifireset.R;


/**
 * Provides methods to access to the configuration values.
 * 
 * @author david
 */
public class SettingsManager {
	
	/**
	 * Returns the global shared-preferences manager.
	 * 
	 * @param ctx Execution context
	 * @return The global shared-preferences manager returned by the 
	 * 	method PreferenceManager.getDefaultSharedPreferences(ctx).
	 */
	public static SharedPreferences getPrefs (Context ctx) {
		return PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	/**
	 * Returns if the service is active or not
	 * 
	 * @return the shared preference value of the key R.string.preferences_key_active
	 */
	public static boolean isActive (Context ctx) {
		return getPrefs(ctx).getBoolean(ctx.getString(R.string.preferences_key_active), true);
	}

	/**
	 * Returns if the service runs on start or not
	 * 
	 * @return the shared preference value of the key R.string.preferences_key_autostart
	 */
	public static boolean isAutostart(Context ctx) {
		return getPrefs(ctx).getBoolean(ctx.getString(R.string.preferences_key_autostart), true);
	}

	/**
	 * Returns if the service shows a notify icon or not
	 * 
	 * @return the shared preference value of the key R.string.preferences_key_notify
	 */
	public static boolean isNotify(Context ctx) {
		return getPrefs(ctx).getBoolean(ctx.getString(R.string.preferences_key_notify), true);
	}
	
	/**
	 * Returns the execution interval of the service
	 * 
	 * @return the shared preference value of the key R.string.preferences_key_interval
	 */
	public static int getInterval(Context ctx) {
		String value;
		
		value = getPrefs(ctx).getString(ctx.getString(R.string.preferences_key_interval), "300"); 
		
		return Integer.parseInt(value); 
	}
}

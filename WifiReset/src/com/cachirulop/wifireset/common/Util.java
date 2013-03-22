package com.cachirulop.wifireset.common;

import java.util.Calendar;

import android.os.Build;

public class Util {

	public static int compareCalendar(Calendar c1, Calendar c2) {
		return getMidnightCalendar(c1).compareTo(getMidnightCalendar(c2));
	}

	public static Calendar getMidnightCalendar(Calendar c) {
		Calendar result;

		result = (Calendar) c.clone();
		result.set(Calendar.HOUR, 0);
		result.set(Calendar.MINUTE, 0);
		result.set(Calendar.SECOND, 0);
		result.set(Calendar.MILLISECOND, 0);

		return result;
	}

	public static boolean isEmulator() {
		return Build.MODEL.contains("sdk") || Build.MODEL.contains("Emulator");  
	}
}

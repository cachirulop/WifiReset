package com.cachirulop.wifireset.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;

import com.cachirulop.wifireset.R;
import com.cachirulop.wifireset.broadcast.HistoryBroadcastReceiverManager;
import com.cachirulop.wifireset.common.Util;
import com.cachirulop.wifireset.data.WifiResetDataHelper;
import com.cachirulop.wifireset.entity.History;


/**
 * Class to access to the History table in the database.
 * 
 * Implements method for read and write records in the history table.
 * 
 * @author david
 */
public class HistoryManager {
	
	public static List<History> getAll(Context ctx) {
		Cursor c;
		SQLiteDatabase db = null;

		try {
			db = new WifiResetDataHelper(ctx).getReadableDatabase();

			c = db.rawQuery(ctx.getString(R.string.SQL_history_get_all), null);

			return createHistoryList(c);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	public static void add (Context ctx, int msgId) {
		add(ctx, ctx.getResources().getString(msgId));
	}

	public static void add (Context ctx, String msg) {
		History h;
		
		h = new History();
		h.setInsertDate(new Date());
		h.setMessage(msg);

		add(ctx, h);
	}
	
	public static void add (Context ctx, History h) {
		SQLiteDatabase db = null;

		try {
			db = new WifiResetDataHelper(ctx).getWritableDatabase();

			ContentValues values;

			values = new ContentValues();
			values.put("insert_date", h.getInsertDateDB());
			values.put("message", h.getMessage());

			db.insert("history", null, values);
			
			if (Util.compareCalendar(SettingsManager.getLastCleanDate(ctx), Calendar.getInstance()) < 0) {
				deleteOlderRow(ctx);
				
				SettingsManager.setLastCleanDate(ctx, new Date());
			}

			HistoryBroadcastReceiverManager.sendBroadcastHistoryModified(ctx);
		} finally {
			if (db != null) {
				db.close();
			}
		}		
	}
	
	private static void deleteOlderRow (Context ctx) {
		SQLiteDatabase db = null;

		try {
			db = new WifiResetDataHelper(ctx).getWritableDatabase();

			db.execSQL(ctx.getString(R.string.SQL_history_clean));
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	private static List<History> createHistoryList(Cursor c) {
		List<History> result;
		
		result = new ArrayList<History> ();
		
		if (c != null && c.moveToFirst()) {
			do {
				result.add(createHistory(c));
			} while (c.moveToNext());
		}

		return result;
	}
	
	private static History createHistory (Cursor c) {
		History result;
		
		result = new History ();
		result.setIdHistory(c.getInt(c.getColumnIndex("id_history")));
		result.setInsertDate(new Date (c.getLong(c.getColumnIndex("insert_date"))));
		result.setMessage(c.getString(c.getColumnIndex("message")));
		
		return result;
	}
	
}

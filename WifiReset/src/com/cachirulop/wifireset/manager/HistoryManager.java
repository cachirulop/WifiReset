package com.cachirulop.wifireset.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cachirulop.wifireset.R;
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
				deleteOlderRows(ctx);
				
				SettingsManager.setLastCleanDate(ctx, Calendar.getInstance());
			}

			BroadcastManager.sendBroadcastHistoryAdded(ctx);
		} finally {
			if (db != null) {
				db.close();
			}
		}		
	}
	
	/**
	 * Delete all the rows before now
	 * 
	 * @param ctx
	 */
	private static void deleteOlderRows (Context ctx) {
		executeSQL(ctx, R.string.SQL_history_delete_older_rows);
	}
	
	/**
	 * Delete all the rows in the history table
	 * 
	 * @param ctx Execution context
	 */
	public static void clean (Context ctx) {
		executeSQL(ctx, R.string.SQL_history_clean);
	}

	/**
	 * Execute an SQL statement on the database
	 * 
	 * @param ctx Execution context (e.g. the Activity)
	 * @param sqlId Identifier of the SQL to execute
	 */
	private static void executeSQL(Context ctx, int sqlId) {
		SQLiteDatabase db = null;

		try {
			db = new WifiResetDataHelper(ctx).getWritableDatabase();

			db.execSQL(ctx.getString(sqlId));
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

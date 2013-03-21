package com.cachirulop.wifireset.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;


public class HistoryBroadcastReceiverManager {
	private IHistoryBroadcastReceiver _receiver;

	private static final String BROADCAST_HISTORY_MODIFIED = "broadcast.history.modified";

	public HistoryBroadcastReceiverManager(Context ctx) {
		this(ctx, (IHistoryBroadcastReceiver) ctx);
	}

	public HistoryBroadcastReceiverManager(Context ctx,
			IHistoryBroadcastReceiver receiver) {
		LocalBroadcastManager.getInstance(ctx).registerReceiver(
				_historyModifiedReceiver,
				new IntentFilter(BROADCAST_HISTORY_MODIFIED));

		_receiver = receiver;
	}
	
	private BroadcastReceiver _historyModifiedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			_receiver.historyModified();
		}
	};
	
	public static void sendBroadcastHistoryModified (Context ctx) {
		Intent intent = new Intent(BROADCAST_HISTORY_MODIFIED);
		LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);		
	}
}

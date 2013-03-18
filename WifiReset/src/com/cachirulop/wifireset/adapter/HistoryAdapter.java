package com.cachirulop.wifireset.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cachirulop.wifireset.R;
import com.cachirulop.wifireset.entity.History;
import com.cachirulop.wifireset.manager.HistoryManager;

public class HistoryAdapter extends BaseAdapter {
	/** Execution context of the Adapter. */
	private Context _ctx;
	
	/** List of rows shows by the adapter */
	private List<History> _lstHistory;
	
	/**
	 * Creates a new HistoryAdapter within the context specified.
	 * 
	 * @param ctx Context (for example the activity) of the adapter.
	 */
	public HistoryAdapter (Context ctx) {
		_ctx = ctx;		
	}
	
	/**
	 * Returns the number of items in the list of movements.
	 */
	public int getCount() {
		if (_lstHistory == null) {
			refresh();
		}

		return _lstHistory.size();
	}	
	
	/**
	 * Update the list of history records reading from the database with the 
	 * method {@link HistoryManager#getAll}
	 */
	public void refresh() {
		_lstHistory = HistoryManager.getAll(_ctx);
		notifyDataSetChanged();
	}	
	
	/**
	 * Returns the view to show in a list row.
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater;
			
			inflater = LayoutInflater.from(_ctx);
			
			convertView = inflater.inflate(R.layout.layout_adapter_history_row, null);
		}

		History h;
		TextView tvDate;
		TextView tvMessage;

		h = _lstHistory.get(position);
		tvDate = (TextView) convertView.findViewById(R.id.tvInsertDate);
		tvMessage= (TextView) convertView.findViewById(R.id.tvMessage);

		tvDate.setText(h.getInsertDateFormatted());
		tvMessage.setText(h.getMessage());

		return convertView;
	}
	
	/**
	 * Returns the item of the specified position.
	 */
	@Override
	public Object getItem(int position) {
		if (_lstHistory == null) {
			refresh();
		}

		return _lstHistory.get(position);
	}

	/**
	 * Returns the identifier of the item in the specified position.
	 * The identifier is the field IdMovement of the movement.
	 */
	@Override
	public long getItemId(int position) {
		return ((History) getItem (position)).getIdHistory();
	}
}

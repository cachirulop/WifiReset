package com.cachirulop.wifireset.entity;

import java.text.DateFormat;
import java.util.Date;


/**
 * Represents a record of the History table in the database.
 * 
 * @author david
 */
public class History {
	private long _idHistory;
	private Date _insertDate;
	private String _message;
	
	public long getIdHistory() {
		return _idHistory;
	}
	public void setIdHistory(long idHistory) {
		this._idHistory = idHistory;
	}
	public Date getInsertDate() {
		return _insertDate;
	}
	public long getInsertDateDB() {
		return _insertDate.getTime();
	}
	public String getInsertDateFormatted() {
		return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(_insertDate);
	}	
	public void setInsertDate(Date insertDate) {
		this._insertDate = insertDate;
	}
	public String getMessage() {
		return _message;
	}
	public void setMessage(String message) {
		this._message = message;
	}
}

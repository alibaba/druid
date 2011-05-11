package com.alibaba.druid.filter.trace;

import java.util.Date;

public class TraceErrorEvent extends TraceEvent {
	private Throwable error;
	private Date errorTime = new Date();
	
	public TraceErrorEvent() {
		
	}
	
	public TraceErrorEvent(String eventType, Date eventTime, Throwable error) {
		super (eventType, eventTime);
		
		this.error = error;
	}

	public Throwable getError() {
		return error;
	}

	public void setError(Throwable error) {
		this.error = error;
	}

	public Date getErrorTime() {
		return errorTime;
	}

	public void setErrorTime(Date errorTime) {
		this.errorTime = errorTime;
	}
	
	
}

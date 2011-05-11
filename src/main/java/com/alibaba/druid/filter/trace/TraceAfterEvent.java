package com.alibaba.druid.filter.trace;

import java.util.Date;

public class TraceAfterEvent extends TraceEvent {
	private long timespan;
	
	public TraceAfterEvent() {
	}
	
	public TraceAfterEvent(String eventType, Date eventTime, long timespan) {
		super (eventType, eventTime);
		this.timespan = timespan;
	}
	
	public long getTimespan() {
		return timespan;
	}

	public void setTimespan(long timespan) {
		this.timespan = timespan;
	}
}

package com.alibaba.druid.filter.trace;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TraceEvent {
	private Date eventTime;
	private String eventType;
	
	private final Map<String, Object> context = new HashMap<String, Object>();
	
	public TraceEvent() {
		this.eventTime = new Date();
	}
	
	public TraceEvent(String eventType, Date eventTime) {
		this.eventType = eventType;
		this.eventTime = eventTime;
	}
	
	public Date getEventTime() {
		return eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public Map<String, Object> getContext() {
		return context;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	public void putContext(String name, Object value) {
		this.context.put(name, value);
	}
}

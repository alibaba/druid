package com.alibaba.druid.stat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.druid.filter.trace.TraceEvent;
import com.alibaba.druid.filter.trace.TraceEventListener;

public class JdbcTraceManager implements JdbcTraceManagerMBean {
	public final static Log LOG = LogFactory.getLog(JdbcTraceManager.class);

	private boolean traceEnable = false;

	private final static JdbcTraceManager instance = new JdbcTraceManager();

	private final List<TraceEventListener> eventListeners = new ArrayList<TraceEventListener>();

	private final AtomicLong fireEventCount = new AtomicLong();
	private final AtomicLong skipEventCount = new AtomicLong();

	public static JdbcTraceManager getInstance() {
		return instance;
	}

	public boolean isTraceEnable() {
		return traceEnable;
	}

	public void setTraceEnable(boolean traceEnable) {
		this.traceEnable = traceEnable;
	}
	
	public long getEventFiredCount() {
		return fireEventCount.get();
	}
	
	public int getEventListenerSize() {
		return this.eventListeners.size();
	}
	
	public long getEventSkipCount() {
		return skipEventCount.get();
	}

	public void fireTraceEvent(TraceEvent event) {
		fireEventCount.incrementAndGet();
		
		if (!this.traceEnable) {
			skipEventCount.incrementAndGet();
			return;
		}

		for (TraceEventListener listener : this.eventListeners) {
			try {
				listener.fireEvent(event);
			} catch (Exception e) {
				LOG.error("fireTraceEventError", e);
			}
		}
	}

	public List<TraceEventListener> getEventListeners() {
		return eventListeners;
	}
}

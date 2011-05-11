package com.alibaba.druid.filter.trace;

import java.util.EventListener;

public interface TraceEventListener extends EventListener {
	void fireEvent(TraceEvent event);
}

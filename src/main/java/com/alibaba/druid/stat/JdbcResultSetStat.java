/*
 * Copyright 2011 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.stat;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class JdbcResultSetStat implements JdbcResultSetStatMBean {
	private final AtomicInteger concurrentCount = new AtomicInteger();
	private final AtomicInteger concurrentMax = new AtomicInteger();

	private final AtomicLong count = new AtomicLong();
	private final AtomicLong errorCount = new AtomicLong();

	private final AtomicLong nanoTotal = new AtomicLong();
	private Throwable lastError;
	private long lastErrorTime;

	private long lastSampleTime = 0;

	private final AtomicLong fetchRowCounter = new AtomicLong(0); // 总共读取的行数
	private final AtomicLong closeCount = new AtomicLong(0); // ResultSet打开的计数

	public void reset() {
		concurrentCount.set(0);
		concurrentMax.set(0);
		count.set(0);
		errorCount.set(0);
		nanoTotal.set(0);
		lastError = null;
		lastErrorTime = 0;
		lastSampleTime = 0;
	}

	public void beforeOpen() {
		int invoking = concurrentCount.incrementAndGet();

		for (;;) {
			int max = concurrentMax.get();
			if (invoking > max) {
				if (concurrentMax.compareAndSet(max, invoking)) {
					break;
				} else {
					continue;
				}
			} else {
				break;
			}
		}

		count.incrementAndGet();
		lastSampleTime = System.currentTimeMillis();
	}

	public long getErrorCount() {
		return errorCount.get();
	}

	public int getRunningCount() {
		return concurrentCount.get();
	}

	public int getConcurrentMax() {
		return concurrentMax.get();
	}

	public long getOpenCount() {
		return count.get();
	}

	public Date getLastConnectTime() {
		if (lastSampleTime == 0) {
			return null;
		}

		return new Date(lastSampleTime);
	}

	public long getNanoTotal() {
		return nanoTotal.get();
	}

	public void afterClose(long nanoSpan) {
		concurrentCount.decrementAndGet();

		nanoTotal.addAndGet(nanoSpan);
	}

	public Throwable getLastError() {
		return lastError;
	}

	public Date getLastErrorTime() {
		if (lastErrorTime <= 0) {
			return null;
		}

		return new Date(lastErrorTime);
	}

	public void error(Throwable error) {
		errorCount.incrementAndGet();
		lastError = error;
		lastErrorTime = System.currentTimeMillis();
	}

	@Override
	public long getHoldMillisTotal() {
		return JdbcStatManager.getInstance().getResultSetStat().getNanoTotal() / (1000 * 1000);
	}

	@Override
	public long getFetchRowCount() {
		return fetchRowCounter.get();
	}

	@Override
	public long getCloseCount() {
		return closeCount.get();
	}
	
	public void addFetchRowCount(long fetchCount) {
		fetchRowCounter.addAndGet(fetchCount);
	}

	public void incrementCloseCounter() {
		closeCount.incrementAndGet();
	}

	public static class Entry {
		protected final long constructNano;
		protected int cusorIndex = 0;
		protected int fetchRowCount = 0;

		public Entry() {
			this.constructNano = System.nanoTime();
		}

		public void decrementCusorIndex() {
			cusorIndex--;
		}

		public long getConstructNano() {
			return constructNano;
		}

		public int getCusorIndex() {
			return cusorIndex;
		}

		public void setCusorIndex(int cusorIndex) {
			this.cusorIndex = cusorIndex;
		}

		public int getFetchRowCount() {
			return fetchRowCount;
		}

		public void setFetchRowCount(int fetchRowCount) {
			this.fetchRowCount = fetchRowCount;
		}
	}
}

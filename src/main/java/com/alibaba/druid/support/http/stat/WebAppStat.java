package com.alibaba.druid.support.http.stat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.LRUCache;

public class WebAppStat {

    private final static Log                        LOG                            = LogFactory.getLog(WebAppStat.class);

    public final static int                         DEFAULT_MAX_STAT_URI_COUNT     = 1000;
    public final static int                         DEFAULT_MAX_STAT_SESSION_COUNT = 1000 * 100;

    private volatile int                            maxStatUriCount                = DEFAULT_MAX_STAT_URI_COUNT;
    private volatile int                            maxStatSessionCount            = DEFAULT_MAX_STAT_SESSION_COUNT;

    private final AtomicInteger                     runningCount                   = new AtomicInteger();
    private final AtomicInteger                     concurrentMax                  = new AtomicInteger();
    private final AtomicLong                        requestCount                   = new AtomicLong(0);

    private final static ThreadLocal<WebAppStat>    currentLocal                   = new ThreadLocal<WebAppStat>();

    private final ConcurrentMap<String, WebURIStat> uriStatMap                     = new ConcurrentHashMap<String, WebURIStat>();
    private final Map<String, WebSessionStat>       sessionStatMap;

    private final ReadWriteLock                     sessionStatLock                = new ReentrantReadWriteLock();

    private final AtomicLong                        uriStatMapFullCount            = new AtomicLong();
    private final AtomicLong                        uriSessionMapFullCount         = new AtomicLong();

    private String                                  contextPath;

    public static WebAppStat current() {
        return currentLocal.get();
    }

    public void reset() {
        concurrentMax.set(0);
        requestCount.set(0);
        requestCount.set(0);

        sessionStatLock.writeLock().lock();
        try {
            sessionStatMap.clear();
        } finally {
            sessionStatLock.writeLock().unlock();
        }
        
        uriStatMap.clear();
        
        uriStatMapFullCount.set(0);
        uriSessionMapFullCount.set(0);
    }

    public WebAppStat(){
        this(null);
    }

    public WebAppStat(String contextPath){
        this(contextPath, DEFAULT_MAX_STAT_SESSION_COUNT);
    }

    public WebAppStat(String contextPath, int maxStatSessionCount){
        this.contextPath = contextPath;
        this.maxStatSessionCount = maxStatSessionCount;

        sessionStatMap = new LRUCache<String, WebSessionStat>(maxStatSessionCount);
    }

    public String getContextPath() {
        return contextPath;
    }

    public void beforeInvoke(String uri) {
        currentLocal.set(this);

        int running = runningCount.incrementAndGet();

        for (;;) {
            int max = concurrentMax.get();
            if (running > max) {
                if (concurrentMax.compareAndSet(max, running)) {
                    break;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }

        requestCount.incrementAndGet();

        WebURIStat uriStat = getURIStat(uri);

        if (uriStat != null) {
            uriStat.beforeInvoke(uri);
        }
    }

    public WebURIStat getURIStat(String uri) {
        if (uriStatMap.size() >= this.getMaxStatUriCount()) {
            long fullCount = uriStatMapFullCount.getAndIncrement();

            if (fullCount == 0) {
                LOG.error("uriSessionMapFullCount is full");
            }

            return null;
        }

        WebURIStat uriStat = uriStatMap.get(uri);

        if (uriStat == null) {
            uriStatMap.putIfAbsent(uri, new WebURIStat(uri));
            uriStat = uriStatMap.get(uri);
        }

        return uriStat;
    }

    public WebSessionStat getSessionStat(String sessionId) {
        return getSessionStat(sessionId, false);
    }

    public WebSessionStat getSessionStat(String sessionId, boolean create) {
        sessionStatLock.readLock().lock();
        try {
            WebSessionStat uriStat = sessionStatMap.get(sessionId);

            if (uriStat != null) {
                return uriStat;
            }
        } finally {
            sessionStatLock.readLock().unlock();
        }

        if (!create) {
            return null;
        }

        sessionStatLock.writeLock().lock();
        try {
            WebSessionStat uriStat = sessionStatMap.get(sessionId);

            if (uriStat == null) {
                if (sessionStatMap.size() >= this.getMaxStatSessionCount()) {
                    long fullCount = uriSessionMapFullCount.getAndIncrement();

                    if (fullCount == 0) {
                        LOG.error("sessionStatMap is full");
                    }
                }

                WebSessionStat newStat = new WebSessionStat(sessionId);

                sessionStatMap.put(sessionId, newStat);

                return newStat;
            }

            return uriStat;
        } finally {
            sessionStatLock.writeLock().unlock();
        }
    }

    public void afterInvoke(Throwable error, long nanoSpan) {
        runningCount.decrementAndGet();

        WebURIStat uriStat = WebURIStat.current();
        if (uriStat != null) {
            uriStat.afterInvoke(error, nanoSpan);
        }

        currentLocal.set(null);
    }

    public int getMaxStatUriCount() {
        return maxStatUriCount;
    }

    public void setMaxStatUriCount(int maxStatUriCount) {
        this.maxStatUriCount = maxStatUriCount;
    }

    public int getMaxStatSessionCount() {
        return maxStatSessionCount;
    }

    public void setMaxStatSessionCount(int maxStatSessionCount) {
        this.maxStatSessionCount = maxStatSessionCount;
    }

    public int getRunningCount() {
        return this.runningCount.get();
    }

    public long getConcurrentMax() {
        return concurrentMax.get();
    }

    public long getRequestCount() {
        return requestCount.get();
    }

    public Map<String, Object> getStatData() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();

        data.put("ContextPath", this.getContextPath());
        data.put("RunningCount", this.getRunningCount());
        data.put("ConcurrentMax", this.getConcurrentMax());
        data.put("RequestCount", this.getRequestCount());

        List<Map<String, Object>> uriStatDataList = getURIStatDataList();

        data.put("URIList", uriStatDataList);

        return data;
    }

    public List<Map<String, Object>> getURIStatDataList() {
        List<Map<String, Object>> uriStatDataList = new ArrayList<Map<String, Object>>(this.uriStatMap.size());
        for (WebURIStat uriStat : this.uriStatMap.values()) {
            Map<String, Object> uriStatData = uriStat.getStatData();

            int runningCount = ((Number) uriStatData.get("RunningCount")).intValue();
            long requestCount = (Long) uriStatData.get("RequestCount");

            if (runningCount == 0 && requestCount == 0) {
                continue;
            }

            uriStatDataList.add(uriStatData);
        }
        return uriStatDataList;
    }

    public List<Map<String, Object>> getSessionStatDataList() {
        List<Map<String, Object>> uriStatDataList = new ArrayList<Map<String, Object>>(this.sessionStatMap.size());
        for (WebSessionStat sessionStat : this.sessionStatMap.values()) {
            Map<String, Object> sessionStatData = sessionStat.getStatData();

            int runningCount = ((Number) sessionStatData.get("RunningCount")).intValue();
            long requestCount = (Long) sessionStatData.get("RequestCount");

            if (runningCount == 0 && requestCount == 0) {
                continue;
            }

            uriStatDataList.add(sessionStatData);
        }
        return uriStatDataList;
    }

}

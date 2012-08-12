package com.alibaba.druid.support.http.stat;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public class WebAppStat {

    private final static Log                        LOG                        = LogFactory.getLog(WebAppStat.class);

    public final static int                         DEFAULT_MAX_STAT_URI_COUNT = 1000;

    private volatile int                            maxStatUriCount            = DEFAULT_MAX_STAT_URI_COUNT;

    private final AtomicInteger                     runningCount               = new AtomicInteger();
    private final AtomicInteger                     concurrentMax              = new AtomicInteger();
    private final AtomicLong                        count                      = new AtomicLong(0);

    private final static ThreadLocal<WebAppStat>    currentLocal               = new ThreadLocal<WebAppStat>();

    private final ConcurrentMap<String, WebURIStat> uriStatMap                 = new ConcurrentHashMap<String, WebURIStat>();

    private final AtomicLong                        uriStatMapFullCount        = new AtomicLong();

    public static WebAppStat current() {
        return currentLocal.get();
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

        count.incrementAndGet();

        WebURIStat uriStat = getURIStat(uri);

        if (uriStat != null) {
            uriStat.beforeInvoke(uri);
        }
    }

    public WebURIStat getURIStat(String uri) {
        if (uriStatMap.size() >= this.getMaxStatUriCount()) {
            long fullCount = uriStatMapFullCount.getAndIncrement();

            if (fullCount == 0) {
                LOG.error("urlStatMap is full");
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

    public void afterInvoke(long nanoSpan) {
        runningCount.decrementAndGet();

        currentLocal.set(null);

        WebURIStat uriStat = WebURIStat.current();
        if (uriStat != null) {
            uriStat.afterInvoke(nanoSpan);
        }
    }

    public int getMaxStatUriCount() {
        return maxStatUriCount;
    }

    public void setMaxStatUriCount(int maxStatUriCount) {
        this.maxStatUriCount = maxStatUriCount;
    }

}

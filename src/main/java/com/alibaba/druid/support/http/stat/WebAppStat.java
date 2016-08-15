/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.support.http.stat;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.LRUCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.alibaba.druid.util.JdbcSqlStatUtils.get;

public class WebAppStat {

    private final static Log                        LOG                            = LogFactory.getLog(WebAppStat.class);

    public final static int                         DEFAULT_MAX_STAT_URI_COUNT     = 1000;
    public final static int                         DEFAULT_MAX_STAT_SESSION_COUNT = 1000;

    private final static ThreadLocal<WebAppStat>    currentLocal                   = new ThreadLocal<WebAppStat>();

    private volatile int                            maxStatUriCount                = DEFAULT_MAX_STAT_URI_COUNT;
    private volatile int                            maxStatSessionCount            = DEFAULT_MAX_STAT_SESSION_COUNT;

    private final AtomicInteger                     runningCount                   = new AtomicInteger();
    private final AtomicInteger                     concurrentMax                  = new AtomicInteger();
    private final AtomicLong                        requestCount                   = new AtomicLong(0);
    private final AtomicLong                        sessionCount                   = new AtomicLong(0);

    private final AtomicLong                        jdbcFetchRowCount              = new AtomicLong();
    private final AtomicLong                        jdbcUpdateCount                = new AtomicLong();
    private final AtomicLong                        jdbcExecuteCount               = new AtomicLong();
    private final AtomicLong                        jdbcExecuteTimeNano            = new AtomicLong();

    private final AtomicLong                        jdbcCommitCount                = new AtomicLong();
    private final AtomicLong                        jdbcRollbackCount              = new AtomicLong();

    private final ConcurrentMap<String, WebURIStat> uriStatMap                     = new ConcurrentHashMap<String, WebURIStat>(
                                                                                                                               16,
                                                                                                                               0.75f,
                                                                                                                               1);
    private final LRUCache<String, WebSessionStat>  sessionStatMap;

    private final ReadWriteLock                     sessionStatLock                = new ReentrantReadWriteLock();

    private final AtomicLong                        uriStatMapFullCount            = new AtomicLong();
    private final AtomicLong                        uriSessionMapFullCount         = new AtomicLong();

    private final AtomicLong                        osMacOSXCount                  = new AtomicLong(0);
    private final AtomicLong                        osWindowsCount                 = new AtomicLong(0);
    private final AtomicLong                        osLinuxCount                   = new AtomicLong(0);
    private final AtomicLong                        osSymbianCount                 = new AtomicLong(0);
    private final AtomicLong                        osFreeBSDCount                 = new AtomicLong(0);
    private final AtomicLong                        osOpenBSDCount                 = new AtomicLong(0);
    private final AtomicLong                        osAndroidCount                 = new AtomicLong(0);

    private final AtomicLong                        osWindows98Count               = new AtomicLong();
    private final AtomicLong                        osWindowsXPCount               = new AtomicLong();
    private final AtomicLong                        osWindows2000Count             = new AtomicLong();
    private final AtomicLong                        osWindowsVistaCount            = new AtomicLong();
    private final AtomicLong                        osWindows7Count                = new AtomicLong();
    private final AtomicLong                        osWindows8Count                = new AtomicLong();

    private final AtomicLong                        osAndroid15Count               = new AtomicLong(0);
    private final AtomicLong                        osAndroid16Count               = new AtomicLong(0);
    private final AtomicLong                        osAndroid20Count               = new AtomicLong(0);
    private final AtomicLong                        osAndroid21Count               = new AtomicLong(0);
    private final AtomicLong                        osAndroid22Count               = new AtomicLong(0);
    private final AtomicLong                        osAndroid23Count               = new AtomicLong(0);
    private final AtomicLong                        osAndroid30Count               = new AtomicLong(0);
    private final AtomicLong                        osAndroid31Count               = new AtomicLong(0);
    private final AtomicLong                        osAndroid32Count               = new AtomicLong(0);
    private final AtomicLong                        osAndroid40Count               = new AtomicLong(0);
    private final AtomicLong                        osAndroid41Count               = new AtomicLong(0);
    private final AtomicLong                        osAndroid42Count               = new AtomicLong(0);
    private final AtomicLong                        osAndroid43Count               = new AtomicLong(0);

    private final AtomicLong                        osLinuxUbuntuCount             = new AtomicLong(0);

    private final AtomicLong                        browserIECount                 = new AtomicLong(0);
    private final AtomicLong                        browserFirefoxCount            = new AtomicLong(0);
    private final AtomicLong                        browserChromeCount             = new AtomicLong(0);
    private final AtomicLong                        browserSafariCount             = new AtomicLong(0);
    private final AtomicLong                        browserOperaCount              = new AtomicLong(0);

    private final AtomicLong                        browserIE5Count                = new AtomicLong(0);
    private final AtomicLong                        browserIE6Count                = new AtomicLong(0);
    private final AtomicLong                        browserIE7Count                = new AtomicLong(0);
    private final AtomicLong                        browserIE8Count                = new AtomicLong(0);
    private final AtomicLong                        browserIE9Count                = new AtomicLong(0);
    private final AtomicLong                        browserIE10Count               = new AtomicLong(0);

    private final AtomicLong                        browser360SECount              = new AtomicLong(0);

    private final AtomicLong                        deviceAndroidCount             = new AtomicLong(0);
    private final AtomicLong                        deviceIpadCount                = new AtomicLong(0);
    private final AtomicLong                        deviceIphoneCount              = new AtomicLong(0);
    private final AtomicLong                        deviceWindowsPhoneCount        = new AtomicLong(0);

    private final AtomicLong                        botCount                       = new AtomicLong();
    private final AtomicLong                        botBaiduCount                  = new AtomicLong();
    private final AtomicLong                        botYoudaoCount                 = new AtomicLong();
    private final AtomicLong                        botGoogleCount                 = new AtomicLong();
    private final AtomicLong                        botMsnCount                    = new AtomicLong();
    private final AtomicLong                        botBingCount                   = new AtomicLong();
    private final AtomicLong                        botSosoCount                   = new AtomicLong();
    private final AtomicLong                        botSogouCount                  = new AtomicLong();
    private final AtomicLong                        botYahooCount                  = new AtomicLong();

    private String                                  contextPath;

    public static WebAppStat current() {
        return currentLocal.get();
    }

    public void reset() {
        concurrentMax.set(0);
        requestCount.set(0);
        requestCount.set(0);
        sessionCount.set(0);

        jdbcFetchRowCount.set(0);
        jdbcUpdateCount.set(0);
        jdbcExecuteCount.set(0);
        jdbcExecuteTimeNano.set(0);
        jdbcCommitCount.set(0);
        jdbcRollbackCount.set(0);

        sessionStatLock.readLock().lock();
        try {
            Iterator<Map.Entry<String, WebSessionStat>> iter = sessionStatMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, WebSessionStat> entry = iter.next();
                entry.getValue().reset();
            }
            sessionStatMap.clear();
        } finally {
            sessionStatLock.readLock().unlock();
        }

        uriStatMap.clear();

        uriStatMapFullCount.set(0);
        uriSessionMapFullCount.set(0);

        osMacOSXCount.set(0);
        osWindowsCount.set(0);
        osLinuxCount.set(0);
        osSymbianCount.set(0);
        osOpenBSDCount.set(0);
        osFreeBSDCount.set(0);
        osAndroidCount.set(0);

        osWindows98Count.set(0);
        osWindowsXPCount.set(0);
        osWindows2000Count.set(0);
        osWindowsVistaCount.set(0);
        osWindows7Count.set(0);
        osWindows8Count.set(0);

        osLinuxUbuntuCount.set(0);

        osAndroid15Count.set(0);
        osAndroid16Count.set(0);
        osAndroid20Count.set(0);
        osAndroid21Count.set(0);
        osAndroid22Count.set(0);
        osAndroid23Count.set(0);
        osAndroid30Count.set(0);
        osAndroid31Count.set(0);
        osAndroid32Count.set(0);
        osAndroid40Count.set(0);
        osAndroid41Count.set(0);
        osAndroid42Count.set(0);
        osAndroid43Count.set(0);

        browserIE6Count.set(0);
        browserIE7Count.set(0);
        browserIE8Count.set(0);
        browserIE9Count.set(0);
        browserIE10Count.set(0);

        browserIECount.set(0);
        browserFirefoxCount.set(0);
        browserChromeCount.set(0);
        browserSafariCount.set(0);
        browserOperaCount.set(0);

        browser360SECount.set(0);

        deviceAndroidCount.set(0);
        deviceIpadCount.set(0);
        deviceIphoneCount.set(0);
        deviceWindowsPhoneCount.set(0);
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

    public void beforeInvoke() {
        currentLocal.set(this);

        int running = runningCount.incrementAndGet();

        for (;;) {
            int max = concurrentMax.get();
            if (running > max) {
                if (concurrentMax.compareAndSet(max, running)) {
                    break;
                }
            } else {
                break;
            }
        }

        requestCount.incrementAndGet();
    }

    public WebURIStat getURIStat(String uri) {
        return getURIStat(uri, false);
    }

    public WebURIStat getURIStat(String uri, boolean create) {
        WebURIStat uriStat = uriStatMap.get(uri);

        if (uriStat != null) {
            return uriStat;
        }

        if (!create) {
            return null;
        }

        if (uriStatMap.size() >= this.getMaxStatUriCount()) {
            long fullCount = uriStatMapFullCount.getAndIncrement();

            if (fullCount == 0) {
                LOG.error("uriSessionMapFullCount is full");
            }

            return null;
        }

        uriStatMap.putIfAbsent(uri, new WebURIStat(uri));
        uriStat = uriStatMap.get(uri);

        return uriStat;
    }

    public WebSessionStat getSessionStat(String sessionId) {
        return getSessionStat(sessionId, false);
    }

    public Map<String, Object> getSessionStatData(String sessionId) {
        WebSessionStat sessionStat = sessionStatMap.get(sessionId);

        if (sessionStat == null) {
            return null;
        }

        return sessionStat.getStatData();
    }

    public Map<String, Object> getURIStatData(String uri) {
        WebURIStat uriStat = getURIStat(uri);

        if (uriStat == null) {
            return null;
        }

        return uriStat.getStatData();
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
        currentLocal.set(null);

        WebRequestStat requestStat = WebRequestStat.current();
        if (requestStat != null) {
            this.addJdbcExecuteCount(requestStat.getJdbcExecuteCount());
            this.addJdbcFetchRowCount(requestStat.getJdbcFetchRowCount());
            this.addJdbcUpdateCount(requestStat.getJdbcUpdateCount());
            this.addJdbcCommitCount(requestStat.getJdbcCommitCount());
            this.addJdbcRollbackCount(requestStat.getJdbcRollbackCount());
            this.addJdbcExecuteTimeNano(requestStat.getJdbcExecuteTimeNano());
        }
    }

    public void incrementSessionCount() {
        sessionCount.incrementAndGet();
    }

    public long getSessionCount() {
        return sessionCount.get();
    }

    public void addJdbcFetchRowCount(long delta) {
        this.jdbcFetchRowCount.addAndGet(delta);
    }

    public long getJdbcFetchRowCount() {
        return jdbcFetchRowCount.get();
    }

    public void addJdbcUpdateCount(long updateCount) {
        this.jdbcUpdateCount.addAndGet(updateCount);
    }

    public long getJdbcUpdateCount() {
        return jdbcUpdateCount.get();
    }

    public void incrementJdbcExecuteCount() {
        jdbcExecuteCount.incrementAndGet();
    }

    public void addJdbcExecuteCount(long executeCount) {
        jdbcExecuteCount.addAndGet(executeCount);
    }

    public long getJdbcExecuteCount() {
        return jdbcExecuteCount.get();
    }

    public long getJdbcExecuteTimeNano() {
        return jdbcExecuteTimeNano.get();
    }

    public void addJdbcExecuteTimeNano(long nano) {
        jdbcExecuteTimeNano.addAndGet(nano);
    }

    public void incrementJdbcCommitCount() {
        jdbcCommitCount.incrementAndGet();
    }

    public long getJdbcCommitCount() {
        return jdbcCommitCount.get();
    }

    public void addJdbcCommitCount(long commitCount) {
        this.jdbcCommitCount.addAndGet(commitCount);
    }

    public void incrementJdbcRollbackCount() {
        jdbcRollbackCount.incrementAndGet();
    }

    public long getJdbcRollbackCount() {
        return jdbcRollbackCount.get();
    }

    public void addJdbcRollbackCount(long rollbackCount) {
        this.jdbcRollbackCount.addAndGet(rollbackCount);
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
        return getStatValue(false).getStatData();
    }
    
    public List<WebURIStatValue> getURIStatValueList(boolean reset) {
        List<WebURIStatValue> list = new ArrayList<WebURIStatValue>(this.uriStatMap.size());
        
        for (WebURIStat uriStat : this.uriStatMap.values()) {
            WebURIStatValue statValue = uriStat.getValue(reset);
            
            if (statValue.getRunningCount() == 0 && statValue.getRequestCount() == 0) {
                continue;
            }
            list.add(statValue);
        }
        
        return list;
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
        List<Map<String, Object>> sessionStatDataList = new ArrayList<Map<String, Object>>(this.sessionStatMap.size());
        for (WebSessionStat sessionStat : Collections.unmodifiableCollection(this.sessionStatMap.values())) {
            Map<String, Object> sessionStatData = sessionStat.getStatData();

            int runningCount = ((Number) sessionStatData.get("RunningCount")).intValue();
            long requestCount = (Long) sessionStatData.get("RequestCount");

            if (runningCount == 0 && requestCount == 0) {
                continue;
            }

            sessionStatDataList.add(sessionStatData);
        }
        return sessionStatDataList;
    }

    public void computeUserAgent(String userAgent) {
        if (userAgent == null || userAgent.length() == 0) {
            return;
        }

        // Mozilla/5.0 (compatible;
        final int MOZILLA_COMPATIBLE_OFFSET = 25;

        boolean is360SE = userAgent.endsWith("360SE)");

        if (is360SE) {
            browser360SECount.incrementAndGet();
        }

        boolean isIE = userAgent.startsWith("MSIE", MOZILLA_COMPATIBLE_OFFSET);
        int iePrefixIndex = 30; // "Mozilla/5.0 (compatible; MSIE ".length();

        boolean isGoogleToolbar = false;

        if (!isIE) {
            isGoogleToolbar = userAgent.startsWith("GoogleToolbar", MOZILLA_COMPATIBLE_OFFSET);
            if (isGoogleToolbar) {
                // MSIE
                int tmp = userAgent.indexOf("IE ");
                if (tmp != -1) {
                    isIE = true;
                    iePrefixIndex = tmp + 3;
                }
            }
        }

        if (isIE) {

            browserIECount.incrementAndGet();

            char v1 = ' ', v2 = ' ';
            if (userAgent.length() > iePrefixIndex + 1) {
                v1 = userAgent.charAt(iePrefixIndex);
                v2 = userAgent.charAt(iePrefixIndex + 1);
            } else if (userAgent.length() > iePrefixIndex) {
                v1 = userAgent.charAt(iePrefixIndex);
            }

            switch (v1) {
                case '5':
                    browserIE5Count.incrementAndGet();
                    break;
                case '6':
                    browserIE6Count.incrementAndGet();
                    break;
                case '7':
                    browserIE7Count.incrementAndGet();
                    break;
                case '8':
                    browserIE8Count.incrementAndGet();
                    break;
                case '9':
                    browserIE9Count.incrementAndGet();
                    break;
                case '1':
                    if (v2 == '0') {
                        browserIE10Count.incrementAndGet();
                    }
                    break;
                default:
                    break;
            }

            osWindowsCount.incrementAndGet();

            computeUserAgentIEWindowsVersion(userAgent);

            if (userAgent.contains("Windows Phone")) {
                deviceWindowsPhoneCount.incrementAndGet();
            }

            return;
        }

        boolean isWindows = false;
        boolean isMac = false;
        boolean isIpad = false;
        boolean isIPhone = false;
        boolean isLinux = false;
        boolean isX11 = false;
        boolean isBSD = false;

        if (userAgent.startsWith("Windows", 13)) {
            isWindows = true;
        } else if (userAgent.startsWith("Macintosh", 13)) {
            isMac = true;
        } else if (userAgent.startsWith("iPad", 13)) {
            isIpad = true;
            isMac = true;
        } else if (userAgent.startsWith("iPhone", 13)) {
            isIPhone = true;
            isMac = true;
        } else if (userAgent.startsWith("Linux", 13)) {
            isLinux = true;
        } else if (userAgent.startsWith("X11", 13)) {
            isX11 = true;
        }

        boolean isAndroid = false;

        if (isWindows) {
            isWindows = true;

            osWindowsCount.incrementAndGet();

            if (userAgent.contains("Windows Phone")) {
                deviceWindowsPhoneCount.incrementAndGet();
            }
        } else if (isMac) {
            isMac = true;
            osMacOSXCount.incrementAndGet();
            if (isIpad && userAgent.contains("iPad")) {
                deviceIpadCount.incrementAndGet();
            } else if (isIPhone || userAgent.contains("iPhone")) {
                deviceIphoneCount.incrementAndGet();
            }
        } else if (isLinux) {
            osLinuxCount.incrementAndGet();
            isAndroid = computeUserAgentAndroid(userAgent);
        } else if (userAgent.contains("Symbian")) {
            osSymbianCount.incrementAndGet();
        } else if (userAgent.contains("Ubuntu")) {
            osLinuxCount.incrementAndGet();
            osLinuxUbuntuCount.incrementAndGet();
            isLinux = true;
        }

        if (isX11) {
            if (userAgent.contains("OpenBSD")) {
                osOpenBSDCount.incrementAndGet();
                isBSD = true;
            } else if (userAgent.contains("FreeBSD")) {
                osFreeBSDCount.incrementAndGet();
                isBSD = true;
            } else if ((!isLinux) && userAgent.contains("Linux")) {
                osLinuxCount.incrementAndGet();
                isLinux = true;
            }
        }

        boolean isOpera = userAgent.startsWith("Opera");

        if (isOpera) {
            if (userAgent.contains("Windows")) {
                osWindowsCount.incrementAndGet();
            } else if (userAgent.contains("Linux")) {
                osWindowsCount.incrementAndGet();
            } else if (userAgent.contains("Macintosh")) {
                osMacOSXCount.incrementAndGet();
            }
            browserOperaCount.incrementAndGet();
            return;
        }

        if (isWindows) {
            computeUserAgentFirefoxWindowsVersion(userAgent);
        }

        if (isWindows || isMac || isLinux || isBSD) {
            if (userAgent.contains("Chrome")) {
                browserChromeCount.incrementAndGet();
                return;
            }

            if ((!isAndroid) && userAgent.contains("Safari")) {
                browserSafariCount.incrementAndGet();
                return;
            }

            if (userAgent.contains("Firefox")) {
                browserFirefoxCount.incrementAndGet();
                return;
            }
        }

        if (userAgent.startsWith("User-Agent: ")) {
            String rest = userAgent.substring("User-Agent: ".length());
            computeUserAgent(rest);
        }

        boolean isJava = userAgent.startsWith("Java");

        if (isJava) {
            botCount.incrementAndGet();
        }

        if (userAgent.startsWith("msnbot")) {
            botCount.incrementAndGet();
            botMsnCount.incrementAndGet();
        } else if (userAgent.startsWith("Sosospider+")) {
            botCount.incrementAndGet();
            botSosoCount.incrementAndGet();
        } else if (userAgent.startsWith("Sogou")) {
            botCount.incrementAndGet();
            botSogouCount.incrementAndGet();
        } else if (userAgent.startsWith("HuaweiSymantecSpider")) {
            botCount.incrementAndGet();
        } else if (userAgent.startsWith("Yeti/")) {
            botCount.incrementAndGet();
        } else if (userAgent.startsWith("mahonie")) {
            botCount.incrementAndGet();
        } else if (userAgent.startsWith("findlinks")) {
            botCount.incrementAndGet();
        } else if (userAgent.startsWith("Updownerbot")) {
            botCount.incrementAndGet();
        } else if (userAgent.startsWith("DoCoMo/")) {
            botCount.incrementAndGet();
        } else if (userAgent.startsWith("Crawl")) {
            botCount.incrementAndGet();
        } else if (userAgent.startsWith("SkimBot")) {
            botCount.incrementAndGet();

        } else if (userAgent.startsWith("YoudaoBot", MOZILLA_COMPATIBLE_OFFSET)) {
            botCount.incrementAndGet();
            botYoudaoCount.incrementAndGet();
        } else if (userAgent.startsWith("bingbot", MOZILLA_COMPATIBLE_OFFSET)) {
            botCount.incrementAndGet();
            botBingCount.incrementAndGet();
        } else if (userAgent.startsWith("Googlebot", MOZILLA_COMPATIBLE_OFFSET)) {
            botCount.incrementAndGet();
            botGoogleCount.incrementAndGet();
        } else if (userAgent.startsWith("Baiduspider", MOZILLA_COMPATIBLE_OFFSET)) {
            botCount.incrementAndGet();
            botBaiduCount.incrementAndGet();
        } else if (userAgent.startsWith("MJ12bot", MOZILLA_COMPATIBLE_OFFSET)) {
            botCount.incrementAndGet();
            botBaiduCount.incrementAndGet();
        } else if (userAgent.startsWith("Mail.RU/", MOZILLA_COMPATIBLE_OFFSET)) {
            botCount.incrementAndGet();
        } else if (userAgent.startsWith("Yahoo!", MOZILLA_COMPATIBLE_OFFSET)) {
            botCount.incrementAndGet();
            botYahooCount.incrementAndGet();
        } else if (userAgent.startsWith("KaloogaBot", MOZILLA_COMPATIBLE_OFFSET)) {
            botCount.incrementAndGet();
        } else if (userAgent.startsWith("YandexBot", MOZILLA_COMPATIBLE_OFFSET)) {
            botCount.incrementAndGet();
        } else if (userAgent.startsWith("Ezooms/", MOZILLA_COMPATIBLE_OFFSET)) {
            botCount.incrementAndGet();
        } else if (userAgent.startsWith("Exabot/", MOZILLA_COMPATIBLE_OFFSET)) {
            botCount.incrementAndGet();
        } else if (userAgent.startsWith("AhrefsBot/", MOZILLA_COMPATIBLE_OFFSET)) {
            botCount.incrementAndGet();
        } else if (userAgent.startsWith("YodaoBot/", MOZILLA_COMPATIBLE_OFFSET)) {
            botCount.incrementAndGet();
        } else if (userAgent.startsWith("BeetleBot", MOZILLA_COMPATIBLE_OFFSET)) {
            botCount.incrementAndGet();
        } else if (userAgent.startsWith("archive.org_bot", MOZILLA_COMPATIBLE_OFFSET)) {
            botCount.incrementAndGet();
        } else if (userAgent.startsWith("aiHitBot", MOZILLA_COMPATIBLE_OFFSET)) {
            botCount.incrementAndGet();
        } else if (userAgent.startsWith("EventGuruBot", MOZILLA_COMPATIBLE_OFFSET)) {
            botCount.incrementAndGet();

        } else if (userAgent.equals("Mozilla/5.0 ()")) {
            botCount.incrementAndGet();
        } else if (userAgent.equals("\"Mozilla/5.0")) {
            botCount.incrementAndGet();
        } else if (userAgent.equals("Mozilla")) {
            botCount.incrementAndGet();
        } else if (userAgent.equals("-")) {
            botCount.incrementAndGet();
        } else if (userAgent.contains("Spider") || userAgent.contains("spider")) {
            botCount.incrementAndGet();
        } else if (userAgent.contains("crawl") || userAgent.contains("Crawl")) {
            botCount.incrementAndGet();
        } else if (userAgent.contains("Bot") || userAgent.contains("bot")) {
            botCount.incrementAndGet();
        }

        // Mozilla/5.0 ()
        // Mozilla/5.0 (compatible; Mail.RU/2.0)
        // Mozilla/5.0 (compatible; bingbot/2.0;
        // YoudaoBot

    }

    private void computeUserAgentFirefoxWindowsVersion(String userAgent) {
        if (userAgent.startsWith("Windows NT 5.1", 13)) {
            osWindowsXPCount.incrementAndGet();
        } else if (userAgent.startsWith("Windows NT 5.1", 25)) {
            osWindowsXPCount.incrementAndGet();

        } else if (userAgent.startsWith("Windows NT 6.0", 13)) {
            osWindowsVistaCount.incrementAndGet();

        } else if (userAgent.startsWith("Windows NT 6.1", 13)) {
            osWindows7Count.incrementAndGet();

        } else if (userAgent.startsWith("Windows NT 6.2", 13)) {
            osWindows8Count.incrementAndGet();

        } else if (userAgent.startsWith("Windows NT 5.0", 13)) {
            osWindows2000Count.incrementAndGet();
        } else if (userAgent.startsWith("Windows NT 5.0", 25)) {
            osWindows2000Count.incrementAndGet();
        }
    }

    private void computeUserAgentIEWindowsVersion(String userAgent) {
        if (userAgent.startsWith("Windows NT 5.1", 35)) {
            osWindowsXPCount.incrementAndGet();
        } else if (userAgent.startsWith("Windows NT 5.0", 35)) {
            osWindows2000Count.incrementAndGet();
        } else if (userAgent.startsWith("Windows NT 5.0", 36)) {
            osWindows2000Count.incrementAndGet();
        } else if (userAgent.startsWith("Windows NT 6.0", 35)) {
            osWindowsVistaCount.incrementAndGet();
        } else if (userAgent.startsWith("Windows NT 6.1", 35)) {
            osWindows7Count.incrementAndGet();
        } else if (userAgent.startsWith("Windows NT 6.2", 36)) {
            osWindows8Count.incrementAndGet();
        } else if (userAgent.startsWith("Windows 98", 36)) {
            osWindows98Count.incrementAndGet();
        } else if (userAgent.startsWith("Windows 98", 35)) {
            osWindows98Count.incrementAndGet();
        } else if (userAgent.startsWith("Windows XP", 35)) {
            osWindowsXPCount.incrementAndGet();
        } else if (userAgent.startsWith("Windows XP", 34)) {
            osWindowsXPCount.incrementAndGet();
        }
    }

    private boolean computeUserAgentAndroid(String userAgent) {
        boolean isAndroid = userAgent.startsWith("Android", 23);
        int toffset = 31;
        if (!isAndroid) {
            isAndroid = userAgent.startsWith("Android", 20);
            toffset = 28;
        }

        if (isAndroid) {
            osAndroidCount.incrementAndGet();
            deviceAndroidCount.incrementAndGet();

            if (userAgent.startsWith("1.5", toffset)) {
                osAndroid15Count.incrementAndGet();
            } else if (userAgent.startsWith("1.6", toffset)) {
                osAndroid16Count.incrementAndGet();
            } else if (userAgent.startsWith("2.0", toffset)) {
                osAndroid20Count.incrementAndGet();
            } else if (userAgent.startsWith("2.1", toffset)) {
                osAndroid21Count.incrementAndGet();
            } else if (userAgent.startsWith("2.2", toffset)) {
                osAndroid22Count.incrementAndGet();
            } else if (userAgent.startsWith("2.3.3", toffset)) {
                osAndroid23Count.incrementAndGet();
            } else if (userAgent.startsWith("2.3.4", toffset)) {
                osAndroid23Count.incrementAndGet();
            } else if (userAgent.startsWith("3.0", toffset)) {
                osAndroid30Count.incrementAndGet();
            } else if (userAgent.startsWith("3.1", toffset)) {
                osAndroid31Count.incrementAndGet();
            } else if (userAgent.startsWith("3.2", toffset)) {
                osAndroid32Count.incrementAndGet();
            } else if (userAgent.startsWith("4.0", toffset)) {
                osAndroid40Count.incrementAndGet();
            } else if (userAgent.startsWith("4.1", toffset)) {
                osAndroid41Count.incrementAndGet();
            } else if (userAgent.startsWith("4.2", toffset)) {
                osAndroid42Count.incrementAndGet();
            } else if (userAgent.startsWith("4.3", toffset)) {
                osAndroid43Count.incrementAndGet();
            }

            return true;
        }

        return false;
    }

    public long getOSMacOSXCount() {
        return osMacOSXCount.get();
    }

    public long getOSWindowsCount() {
        return osWindowsCount.get();
    }

    public long getOSLinuxCount() {
        return osLinuxCount.get();
    }

    public long getOSSymbianCount() {
        return osSymbianCount.get();
    }

    public long getOSFreeBSDCount() {
        return osFreeBSDCount.get();
    }

    public long getOSOpenBSDCount() {
        return osOpenBSDCount.get();
    }

    public long getOSAndroidCount() {
        return osAndroidCount.get();
    }

    public long getOSWindows98Count() {
        return osWindows98Count.get();
    }

    public long getOSWindowsXPCount() {
        return osWindowsXPCount.get();
    }

    public long getOSWindows2000Count() {
        return osWindows2000Count.get();
    }

    public long getOSWindowsVistaCount() {
        return osWindowsVistaCount.get();
    }

    public long getOSWindows7Count() {
        return osWindows7Count.get();
    }

    public long getOSWindows8Count() {
        return osWindows8Count.get();
    }

    public long getOSAndroid15Count() {
        return osAndroid15Count.get();
    }

    public long getOSAndroid16Count() {
        return osAndroid16Count.get();
    }

    public long getOSAndroid20Count() {
        return osAndroid20Count.get();
    }

    public long getOSAndroid21Count() {
        return osAndroid21Count.get();
    }

    public long getOSAndroid22Count() {
        return osAndroid22Count.get();
    }

    public long getOSAndroid23Count() {
        return osAndroid23Count.get();
    }

    public long getOSAndroid30Count() {
        return osAndroid30Count.get();
    }

    public long getOSAndroid31Count() {
        return osAndroid31Count.get();
    }

    public long getOSAndroid32Count() {
        return osAndroid32Count.get();
    }

    public long getOSAndroid40Count() {
        return osAndroid40Count.get();
    }

    public long getOSAndroid41Count() {
        return osAndroid41Count.get();
    }

    public long getOSAndroid42Count() {
        return osAndroid42Count.get();
    }

    public long getOSAndroid43Count() {
        return osAndroid43Count.get();
    }

    public long getOSLinuxUbuntuCount() {
        return osLinuxUbuntuCount.get();
    }

    public long getBrowserIECount() {
        return browserIECount.get();
    }

    public long getBrowserFirefoxCount() {
        return browserFirefoxCount.get();
    }

    public long getBrowserChromeCount() {
        return browserChromeCount.get();
    }

    public long getBrowserSafariCount() {
        return browserSafariCount.get();
    }

    public long getBrowserOperaCount() {
        return browserOperaCount.get();
    }

    public long getBrowserIE5Count() {
        return browserIE5Count.get();
    }

    public long getBrowserIE6Count() {
        return browserIE6Count.get();
    }

    public long getBrowserIE7Count() {
        return browserIE7Count.get();
    }

    public long getBrowserIE8Count() {
        return browserIE8Count.get();
    }

    public long getBrowserIE9Count() {
        return browserIE9Count.get();
    }

    public long getBrowserIE10Count() {
        return browserIE10Count.get();
    }

    public long getBrowser360SECount() {
        return browser360SECount.get();
    }

    public long getDeviceAndroidCount() {
        return deviceAndroidCount.get();
    }

    public long getDeviceIpadCount() {
        return deviceIpadCount.get();
    }

    public long getDeviceIphoneCount() {
        return deviceIphoneCount.get();
    }

    public long getDeviceWindowsPhoneCount() {
        return deviceWindowsPhoneCount.get();
    }

    public long getBotCount() {
        return botCount.get();
    }

    public long getBotBaiduCount() {
        return botBaiduCount.get();
    }

    public long getBotYoudaoCount() {
        return botYoudaoCount.get();
    }

    public long getBotGoogleCount() {
        return botGoogleCount.get();
    }

    public long getBotMsnCount() {
        return botMsnCount.get();
    }

    public long getBotBingCount() {
        return botBingCount.get();
    }

    public long getBotSosoCount() {
        return botSosoCount.get();
    }

    public long getBotSogouCount() {
        return botSogouCount.get();
    }

    public long getBotYahooCount() {
        return botYahooCount.get();
    }

    public WebAppStatValue getStatValue(boolean reset) {
        WebAppStatValue val = new WebAppStatValue();
        val.setContextPath(contextPath);

        val.setRunningCount(getRunningCount());
        val.concurrentMax = get(concurrentMax, reset);
        val.requestCount = get(requestCount, reset);
        val.sessionCount = get(sessionCount, reset);
        val.jdbcFetchRowCount = get(jdbcFetchRowCount, reset);
        val.jdbcUpdateCount = get(jdbcUpdateCount, reset);
        val.jdbcExecuteCount = get(jdbcExecuteCount, reset);
        val.jdbcExecuteTimeNano = get(jdbcExecuteTimeNano, reset);
        val.jdbcCommitCount = get(jdbcCommitCount, reset);
        val.jdbcRollbackCount = get(jdbcRollbackCount, reset);

        val.osMacOSXCount = get(osMacOSXCount, reset);
        val.osWindowsCount = get(osWindowsCount, reset);
        val.osLinuxCount = get(osLinuxCount, reset);
        val.osSymbianCount = get(osSymbianCount, reset);
        val.osFreeBSDCount = get(osFreeBSDCount, reset);
        val.osOpenBSDCount = get(osOpenBSDCount, reset);
        val.osAndroidCount = get(osAndroidCount, reset);

        val.osWindows98Count = get(osWindows98Count, reset);
        val.osWindowsXPCount = get(osWindowsXPCount, reset);
        val.osWindows2000Count = get(osWindows2000Count, reset);
        val.osWindowsVistaCount = get(osWindowsVistaCount, reset);
        val.osWindows7Count = get(osWindows7Count, reset);
        val.osWindows8Count = get(osWindows8Count, reset);

        val.osAndroid15Count = get(osAndroid15Count, reset);
        val.osAndroid16Count = get(osAndroid16Count, reset);
        val.osAndroid20Count = get(osAndroid20Count, reset);
        val.osAndroid21Count = get(osAndroid21Count, reset);
        val.osAndroid22Count = get(osAndroid22Count, reset);
        val.osAndroid23Count = get(osAndroid23Count, reset);
        val.osAndroid30Count = get(osAndroid30Count, reset);
        val.osAndroid31Count = get(osAndroid31Count, reset);
        val.osAndroid32Count = get(osAndroid32Count, reset);
        val.osAndroid40Count = get(osAndroid40Count, reset);
        val.osAndroid41Count = get(osAndroid41Count, reset);
        val.osAndroid42Count = get(osAndroid42Count, reset);
        val.osAndroid43Count = get(osAndroid43Count, reset);

        val.osLinuxUbuntuCount = get(osLinuxUbuntuCount, reset);

        val.browserIECount = get(browserIECount, reset);
        val.browserFirefoxCount = get(browserFirefoxCount, reset);
        val.browserChromeCount = get(browserChromeCount, reset);
        val.browserSafariCount = get(browserSafariCount, reset);
        val.browserOperaCount = get(browserOperaCount, reset);

        val.browserIE5Count = get(browserIE5Count, reset);
        val.browserIE6Count = get(browserIE6Count, reset);
        val.browserIE7Count = get(browserIE7Count, reset);
        val.browserIE8Count = get(browserIE8Count, reset);
        val.browserIE9Count = get(browserIE9Count, reset);
        val.browserIE10Count = get(browserIE10Count, reset);

        val.browser360SECount = get(browser360SECount, reset);
        val.deviceAndroidCount = get(deviceAndroidCount, reset);
        val.deviceIpadCount = get(deviceIpadCount, reset);
        val.deviceIphoneCount = get(deviceIphoneCount, reset);
        val.deviceWindowsPhoneCount = get(deviceWindowsPhoneCount, reset);

        val.botCount = get(botCount, reset);
        val.botBaiduCount = get(botBaiduCount, reset);
        val.botYoudaoCount = get(botYoudaoCount, reset);
        val.botGoogleCount = get(botGoogleCount, reset);
        val.botMsnCount = get(botMsnCount, reset);
        val.botBingCount = get(botBingCount, reset);
        val.botSosoCount = get(botSosoCount, reset);
        val.botSogouCount = get(botSogouCount, reset);
        val.botYahooCount = get(botYahooCount, reset);

        return val;
    }
}

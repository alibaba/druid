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

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.druid.support.monitor.annotation.AggregateType;
import com.alibaba.druid.support.monitor.annotation.MField;
import com.alibaba.druid.support.monitor.annotation.MTable;

@MTable(name = "druid_webapp")
public class WebAppStatValue {

    @MField(groupBy = true, aggregate=AggregateType.None)
    String contextPath;

    @MField(aggregate = AggregateType.Last)
    int    runningCount;

    @MField(aggregate = AggregateType.Max)
    int    concurrentMax;

    @MField(aggregate = AggregateType.Sum)
    long   requestCount;

    @MField(aggregate = AggregateType.Last)
    long   sessionCount;

    @MField(aggregate = AggregateType.Sum)
    long   jdbcFetchRowCount;

    @MField(aggregate = AggregateType.Sum)
    long   jdbcUpdateCount;

    @MField(aggregate = AggregateType.Sum)
    long   jdbcExecuteCount;

    @MField(aggregate = AggregateType.Sum)
    long   jdbcExecuteTimeNano;

    @MField(aggregate = AggregateType.Sum)
    long   jdbcCommitCount;

    @MField(aggregate = AggregateType.Sum)
    long   jdbcRollbackCount;

    @MField(aggregate = AggregateType.Sum)
    long   osMacOSXCount;

    @MField(aggregate = AggregateType.Sum)
    long   osWindowsCount;

    @MField(aggregate = AggregateType.Sum)
    long   osLinuxCount;

    @MField(aggregate = AggregateType.Sum)
    long   osSymbianCount;

    @MField(aggregate = AggregateType.Sum)
    long   osFreeBSDCount;

    @MField(aggregate = AggregateType.Sum)
    long   osOpenBSDCount;

    @MField(aggregate = AggregateType.Sum)
    long   osAndroidCount;

    @MField(aggregate = AggregateType.Sum)
    long   osWindows98Count;

    @MField(aggregate = AggregateType.Sum)
    long   osWindowsXPCount;

    @MField(aggregate = AggregateType.Sum)
    long   osWindows2000Count;

    @MField(aggregate = AggregateType.Sum)
    long   osWindowsVistaCount;

    @MField(aggregate = AggregateType.Sum)
    long   osWindows7Count;

    @MField(aggregate = AggregateType.Sum)
    long   osWindows8Count;
    @MField(aggregate = AggregateType.Sum)
    long   osAndroid15Count;
    @MField(aggregate = AggregateType.Sum)
    long   osAndroid16Count;
    @MField(aggregate = AggregateType.Sum)
    long   osAndroid20Count;
    @MField(aggregate = AggregateType.Sum)
    long   osAndroid21Count;
    @MField(aggregate = AggregateType.Sum)
    long   osAndroid22Count;
    @MField(aggregate = AggregateType.Sum)
    long   osAndroid23Count;
    @MField(aggregate = AggregateType.Sum)
    long   osAndroid30Count;
    @MField(aggregate = AggregateType.Sum)
    long   osAndroid31Count;
    @MField(aggregate = AggregateType.Sum)
    long   osAndroid32Count;
    @MField(aggregate = AggregateType.Sum)
    long   osAndroid40Count;
    @MField(aggregate = AggregateType.Sum)
    long   osAndroid41Count;
    @MField(aggregate = AggregateType.Sum)
    long   osAndroid42Count;
    @MField(aggregate = AggregateType.Sum)
    long   osAndroid43Count;

    @MField(aggregate = AggregateType.Sum)
    long   osLinuxUbuntuCount;

    @MField(aggregate = AggregateType.Sum)
    long   browserIECount;
    @MField(aggregate = AggregateType.Sum)
    long   browserFirefoxCount;
    @MField(aggregate = AggregateType.Sum)
    long   browserChromeCount;
    @MField(aggregate = AggregateType.Sum)
    long   browserSafariCount;
    @MField(aggregate = AggregateType.Sum)
    long   browserOperaCount;

    @MField(aggregate = AggregateType.Sum)
    long   browserIE5Count;
    @MField(aggregate = AggregateType.Sum)
    long   browserIE6Count;
    @MField(aggregate = AggregateType.Sum)
    long   browserIE7Count;
    @MField(aggregate = AggregateType.Sum)
    long   browserIE8Count;
    @MField(aggregate = AggregateType.Sum)
    long   browserIE9Count;
    @MField(aggregate = AggregateType.Sum)
    long   browserIE10Count;

    @MField(aggregate = AggregateType.Sum)
    long   browser360SECount;
    @MField(aggregate = AggregateType.Sum)
    long   deviceAndroidCount;
    @MField(aggregate = AggregateType.Sum)
    long   deviceIpadCount;
    @MField(aggregate = AggregateType.Sum)
    long   deviceIphoneCount;
    @MField(aggregate = AggregateType.Sum)
    long   deviceWindowsPhoneCount;

    @MField(aggregate = AggregateType.Sum)
    long   botCount;
    @MField(aggregate = AggregateType.Sum)
    long   botBaiduCount;
    @MField(aggregate = AggregateType.Sum)
    long   botYoudaoCount;
    @MField(aggregate = AggregateType.Sum)
    long   botGoogleCount;
    @MField(aggregate = AggregateType.Sum)
    long   botMsnCount;
    @MField(aggregate = AggregateType.Sum)
    long   botBingCount;
    @MField(aggregate = AggregateType.Sum)
    long   botSosoCount;
    @MField(aggregate = AggregateType.Sum)
    long   botSogouCount;
    @MField(aggregate = AggregateType.Sum)
    long   botYahooCount;

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public int getRunningCount() {
        return runningCount;
    }

    public void setRunningCount(int runningCount) {
        this.runningCount = runningCount;
    }

    public int getConcurrentMax() {
        return concurrentMax;
    }

    public void setConcurrentMax(int concurrentMax) {
        this.concurrentMax = concurrentMax;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(long requestCount) {
        this.requestCount = requestCount;
    }

    public long getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(long sessionCount) {
        this.sessionCount = sessionCount;
    }

    public long getJdbcFetchRowCount() {
        return jdbcFetchRowCount;
    }

    public void setJdbcFetchRowCount(long jdbcFetchRowCount) {
        this.jdbcFetchRowCount = jdbcFetchRowCount;
    }

    public long getJdbcUpdateCount() {
        return jdbcUpdateCount;
    }

    public void setJdbcUpdateCount(long jdbcUpdateCount) {
        this.jdbcUpdateCount = jdbcUpdateCount;
    }

    public long getJdbcExecuteCount() {
        return jdbcExecuteCount;
    }

    public void setJdbcExecuteCount(long jdbcExecuteCount) {
        this.jdbcExecuteCount = jdbcExecuteCount;
    }

    public long getJdbcExecuteTimeNano() {
        return jdbcExecuteTimeNano;
    }

    public void setJdbcExecuteTimeNano(long jdbcExecuteTimeNano) {
        this.jdbcExecuteTimeNano = jdbcExecuteTimeNano;
    }

    public long getJdbcCommitCount() {
        return jdbcCommitCount;
    }

    public void setJdbcCommitCount(long jdbcCommitCount) {
        this.jdbcCommitCount = jdbcCommitCount;
    }

    public long getJdbcRollbackCount() {
        return jdbcRollbackCount;
    }

    public void setJdbcRollbackCount(long jdbcRollbackCount) {
        this.jdbcRollbackCount = jdbcRollbackCount;
    }

    public long getOsMacOSXCount() {
        return osMacOSXCount;
    }

    public void setOsMacOSXCount(long osMacOSXCount) {
        this.osMacOSXCount = osMacOSXCount;
    }

    public long getOsWindowsCount() {
        return osWindowsCount;
    }

    public void setOsWindowsCount(long osWindowsCount) {
        this.osWindowsCount = osWindowsCount;
    }

    public long getOsLinuxCount() {
        return osLinuxCount;
    }

    public void setOsLinuxCount(long osLinuxCount) {
        this.osLinuxCount = osLinuxCount;
    }

    public long getOsSymbianCount() {
        return osSymbianCount;
    }

    public void setOsSymbianCount(long osSymbianCount) {
        this.osSymbianCount = osSymbianCount;
    }

    public long getOsFreeBSDCount() {
        return osFreeBSDCount;
    }

    public void setOsFreeBSDCount(long osFreeBSDCount) {
        this.osFreeBSDCount = osFreeBSDCount;
    }

    public long getOsOpenBSDCount() {
        return osOpenBSDCount;
    }

    public void setOsOpenBSDCount(long osOpenBSDCount) {
        this.osOpenBSDCount = osOpenBSDCount;
    }

    public long getOsAndroidCount() {
        return osAndroidCount;
    }

    public void setOsAndroidCount(long osAndroidCount) {
        this.osAndroidCount = osAndroidCount;
    }

    public long getOsWindows98Count() {
        return osWindows98Count;
    }

    public void setOsWindows98Count(long osWindows98Count) {
        this.osWindows98Count = osWindows98Count;
    }

    public long getOsWindowsXPCount() {
        return osWindowsXPCount;
    }

    public void setOsWindowsXPCount(long osWindowsXPCount) {
        this.osWindowsXPCount = osWindowsXPCount;
    }

    public long getOsWindows2000Count() {
        return osWindows2000Count;
    }

    public void setOsWindows2000Count(long osWindows2000Count) {
        this.osWindows2000Count = osWindows2000Count;
    }

    public long getOsWindowsVistaCount() {
        return osWindowsVistaCount;
    }

    public void setOsWindowsVistaCount(long osWindowsVistaCount) {
        this.osWindowsVistaCount = osWindowsVistaCount;
    }

    public long getOsWindows7Count() {
        return osWindows7Count;
    }

    public void setOsWindows7Count(long osWindows7Count) {
        this.osWindows7Count = osWindows7Count;
    }

    public long getOsWindows8Count() {
        return osWindows8Count;
    }

    public void setOsWindows8Count(long osWindows8Count) {
        this.osWindows8Count = osWindows8Count;
    }

    public long getOsAndroid15Count() {
        return osAndroid15Count;
    }

    public void setOsAndroid15Count(long osAndroid15Count) {
        this.osAndroid15Count = osAndroid15Count;
    }

    public long getOsAndroid16Count() {
        return osAndroid16Count;
    }

    public void setOsAndroid16Count(long osAndroid16Count) {
        this.osAndroid16Count = osAndroid16Count;
    }

    public long getOsAndroid20Count() {
        return osAndroid20Count;
    }

    public void setOsAndroid20Count(long osAndroid20Count) {
        this.osAndroid20Count = osAndroid20Count;
    }

    public long getOsAndroid21Count() {
        return osAndroid21Count;
    }

    public void setOsAndroid21Count(long osAndroid21Count) {
        this.osAndroid21Count = osAndroid21Count;
    }

    public long getOsAndroid22Count() {
        return osAndroid22Count;
    }

    public void setOsAndroid22Count(long osAndroid22Count) {
        this.osAndroid22Count = osAndroid22Count;
    }

    public long getOsAndroid23Count() {
        return osAndroid23Count;
    }

    public void setOsAndroid23Count(long osAndroid23Count) {
        this.osAndroid23Count = osAndroid23Count;
    }

    public long getOsAndroid30Count() {
        return osAndroid30Count;
    }

    public void setOsAndroid30Count(long osAndroid30Count) {
        this.osAndroid30Count = osAndroid30Count;
    }

    public long getOsAndroid31Count() {
        return osAndroid31Count;
    }

    public void setOsAndroid31Count(long osAndroid31Count) {
        this.osAndroid31Count = osAndroid31Count;
    }

    public long getOsAndroid32Count() {
        return osAndroid32Count;
    }

    public void setOsAndroid32Count(long osAndroid32Count) {
        this.osAndroid32Count = osAndroid32Count;
    }

    public long getOsAndroid40Count() {
        return osAndroid40Count;
    }

    public void setOsAndroid40Count(long osAndroid40Count) {
        this.osAndroid40Count = osAndroid40Count;
    }

    public long getOsAndroid41Count() {
        return osAndroid41Count;
    }

    public void setOsAndroid41Count(long osAndroid41Count) {
        this.osAndroid41Count = osAndroid41Count;
    }

    public long getOsAndroid42Count() {
        return osAndroid42Count;
    }

    public void setOsAndroid42Count(long osAndroid42Count) {
        this.osAndroid42Count = osAndroid42Count;
    }

    public long getOsAndroid43Count() {
        return osAndroid43Count;
    }

    public void setOsAndroid43Count(long osAndroid43Count) {
        this.osAndroid43Count = osAndroid43Count;
    }

    public long getOsLinuxUbuntuCount() {
        return osLinuxUbuntuCount;
    }

    public void setOsLinuxUbuntuCount(long osLinuxUbuntuCount) {
        this.osLinuxUbuntuCount = osLinuxUbuntuCount;
    }

    public long getBrowserIECount() {
        return browserIECount;
    }

    public void setBrowserIECount(long browserIECount) {
        this.browserIECount = browserIECount;
    }

    public long getBrowserFirefoxCount() {
        return browserFirefoxCount;
    }

    public void setBrowserFirefoxCount(long browserFirefoxCount) {
        this.browserFirefoxCount = browserFirefoxCount;
    }

    public long getBrowserChromeCount() {
        return browserChromeCount;
    }

    public void setBrowserChromeCount(long browserChromeCount) {
        this.browserChromeCount = browserChromeCount;
    }

    public long getBrowserSafariCount() {
        return browserSafariCount;
    }

    public void setBrowserSafariCount(long browserSafariCount) {
        this.browserSafariCount = browserSafariCount;
    }

    public long getBrowserOperaCount() {
        return browserOperaCount;
    }

    public void setBrowserOperaCount(long browserOperaCount) {
        this.browserOperaCount = browserOperaCount;
    }

    public long getBrowserIE5Count() {
        return browserIE5Count;
    }

    public void setBrowserIE5Count(long browserIE5Count) {
        this.browserIE5Count = browserIE5Count;
    }

    public long getBrowserIE6Count() {
        return browserIE6Count;
    }

    public void setBrowserIE6Count(long browserIE6Count) {
        this.browserIE6Count = browserIE6Count;
    }

    public long getBrowserIE7Count() {
        return browserIE7Count;
    }

    public void setBrowserIE7Count(long browserIE7Count) {
        this.browserIE7Count = browserIE7Count;
    }

    public long getBrowserIE8Count() {
        return browserIE8Count;
    }

    public void setBrowserIE8Count(long browserIE8Count) {
        this.browserIE8Count = browserIE8Count;
    }

    public long getBrowserIE9Count() {
        return browserIE9Count;
    }

    public void setBrowserIE9Count(long browserIE9Count) {
        this.browserIE9Count = browserIE9Count;
    }

    public long getBrowserIE10Count() {
        return browserIE10Count;
    }

    public void setBrowserIE10Count(long browserIE10Count) {
        this.browserIE10Count = browserIE10Count;
    }

    public long getBrowser360SECount() {
        return browser360SECount;
    }

    public void setBrowser360SECount(long browser360seCount) {
        browser360SECount = browser360seCount;
    }

    public long getDeviceAndroidCount() {
        return deviceAndroidCount;
    }

    public void setDeviceAndroidCount(long deviceAndroidCount) {
        this.deviceAndroidCount = deviceAndroidCount;
    }

    public long getDeviceIpadCount() {
        return deviceIpadCount;
    }

    public void setDeviceIpadCount(long deviceIpadCount) {
        this.deviceIpadCount = deviceIpadCount;
    }

    public long getDeviceIphoneCount() {
        return deviceIphoneCount;
    }

    public void setDeviceIphoneCount(long deviceIphoneCount) {
        this.deviceIphoneCount = deviceIphoneCount;
    }

    public long getDeviceWindowsPhoneCount() {
        return deviceWindowsPhoneCount;
    }

    public void setDeviceWindowsPhoneCount(long deviceWindowsPhoneCount) {
        this.deviceWindowsPhoneCount = deviceWindowsPhoneCount;
    }

    public long getBotCount() {
        return botCount;
    }

    public void setBotCount(long botCount) {
        this.botCount = botCount;
    }

    public long getBotBaiduCount() {
        return botBaiduCount;
    }

    public void setBotBaiduCount(long botBaiduCount) {
        this.botBaiduCount = botBaiduCount;
    }

    public long getBotYoudaoCount() {
        return botYoudaoCount;
    }

    public void setBotYoudaoCount(long botYoudaoCount) {
        this.botYoudaoCount = botYoudaoCount;
    }

    public long getBotGoogleCount() {
        return botGoogleCount;
    }

    public void setBotGoogleCount(long botGoogleCount) {
        this.botGoogleCount = botGoogleCount;
    }

    public long getBotMsnCount() {
        return botMsnCount;
    }

    public void setBotMsnCount(long botMsnCount) {
        this.botMsnCount = botMsnCount;
    }

    public long getBotBingCount() {
        return botBingCount;
    }

    public void setBotBingCount(long botBingCount) {
        this.botBingCount = botBingCount;
    }

    public long getBotSosoCount() {
        return botSosoCount;
    }

    public void setBotSosoCount(long botSosoCount) {
        this.botSosoCount = botSosoCount;
    }

    public long getBotSogouCount() {
        return botSogouCount;
    }

    public void setBotSogouCount(long botSogouCount) {
        this.botSogouCount = botSogouCount;
    }

    public long getBotYahooCount() {
        return botYahooCount;
    }

    public void setBotYahooCount(long botYahooCount) {
        this.botYahooCount = botYahooCount;
    }

    public long getJdbcExecuteTimeMillis() {
        return getJdbcExecuteTimeNano() / (1000 * 1000);
    }

    public Map<String, Object> getStatData() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();

        data.put("ContextPath", this.getContextPath());
        data.put("RunningCount", this.getRunningCount());
        data.put("ConcurrentMax", this.getConcurrentMax());
        data.put("RequestCount", this.getRequestCount());
        data.put("SessionCount", this.getSessionCount());

        data.put("JdbcCommitCount", this.getJdbcCommitCount());
        data.put("JdbcRollbackCount", this.getJdbcRollbackCount());

        data.put("JdbcExecuteCount", this.getJdbcExecuteCount());
        data.put("JdbcExecuteTimeMillis", this.getJdbcExecuteTimeMillis());
        data.put("JdbcFetchRowCount", this.getJdbcFetchRowCount());
        data.put("JdbcUpdateCount", this.getJdbcUpdateCount());

        data.put("OSMacOSXCount", this.getOsMacOSXCount());
        data.put("OSWindowsCount", this.getOsWindowsCount());
        data.put("OSLinuxCount", this.getOsLinuxCount());
        data.put("OSSymbianCount", this.getOsSymbianCount());
        data.put("OSFreeBSDCount", this.getOsFreeBSDCount());
        data.put("OSOpenBSDCount", this.getOsOpenBSDCount());
        data.put("OSAndroidCount", this.getOsAndroidCount());
        data.put("OSWindows98Count", this.getOsWindows98Count());
        data.put("OSWindowsXPCount", this.getOsWindowsXPCount());
        data.put("OSWindows2000Count", this.getOsWindows2000Count());
        data.put("OSWindowsVistaCount", this.getOsWindowsVistaCount());
        data.put("OSWindows7Count", this.getOsWindows7Count());
        data.put("OSWindows8Count", this.getOsWindows8Count());

        data.put("OSAndroid15Count", this.getOsAndroid15Count());
        data.put("OSAndroid16Count", this.getOsAndroid16Count());
        data.put("OSAndroid20Count", this.getOsAndroid20Count());
        data.put("OSAndroid21Count", this.getOsAndroid21Count());
        data.put("OSAndroid22Count", this.getOsAndroid22Count());
        data.put("OSAndroid23Count", this.getOsAndroid23Count());
        data.put("OSAndroid30Count", this.getOsAndroid30Count());
        data.put("OSAndroid31Count", this.getOsAndroid31Count());
        data.put("OSAndroid32Count", this.getOsAndroid32Count());
        data.put("OSAndroid40Count", this.getOsAndroid40Count());
        data.put("OSAndroid41Count", this.getOsAndroid41Count());
        data.put("OSAndroid42Count", this.getOsAndroid42Count());
        data.put("OSAndroid43Count", this.getOsAndroid43Count());
        data.put("OSLinuxUbuntuCount", this.getOsLinuxUbuntuCount());

        data.put("BrowserIECount", this.getBrowserIECount());
        data.put("BrowserFirefoxCount", this.getBrowserFirefoxCount());
        data.put("BrowserChromeCount", this.getBrowserChromeCount());
        data.put("BrowserSafariCount", this.getBrowserSafariCount());
        data.put("BrowserOperaCount", this.getBrowserOperaCount());

        data.put("BrowserIE5Count", this.getBrowserIE5Count());
        data.put("BrowserIE6Count", this.getBrowserIE6Count());
        data.put("BrowserIE7Count", this.getBrowserIE7Count());
        data.put("BrowserIE8Count", this.getBrowserIE8Count());
        data.put("BrowserIE9Count", this.getBrowserIE9Count());
        data.put("BrowserIE10Count", this.getBrowserIE10Count());

        data.put("Browser360SECount", this.getBrowser360SECount());
        data.put("DeviceAndroidCount", this.getDeviceAndroidCount());
        data.put("DeviceIpadCount", this.getDeviceIpadCount());
        data.put("DeviceIphoneCount", this.getDeviceIphoneCount());
        data.put("DeviceWindowsPhoneCount", this.getDeviceWindowsPhoneCount());

        data.put("BotCount", this.getBotCount());
        data.put("BotBaiduCount", this.getBotBaiduCount());
        data.put("BotYoudaoCount", this.getBotYoudaoCount());
        data.put("BotGoogleCount", this.getBotGoogleCount());
        data.put("BotMsnCount", this.getBotMsnCount());
        data.put("BotBingCount", this.getBotBingCount());
        data.put("BotSosoCount", this.getBotSosoCount());
        data.put("BotSogouCount", this.getBotSogouCount());
        data.put("BotYahooCount", this.getBotYahooCount());

        return data;
    }

}

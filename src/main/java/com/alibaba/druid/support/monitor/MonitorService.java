/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.support.monitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceStatValue;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.support.http.stat.WebAppStat;
import com.alibaba.druid.support.http.stat.WebAppStatManager;
import com.alibaba.druid.support.http.stat.WebAppStatValue;
import com.alibaba.druid.support.http.stat.WebURIStatValue;
import com.alibaba.druid.support.monitor.dao.MonitorDao;
import com.alibaba.druid.support.spring.stat.SpringMethodStatValue;
import com.alibaba.druid.support.spring.stat.SpringStat;
import com.alibaba.druid.support.spring.stat.SpringStatManager;
import com.alibaba.druid.util.IOUtils;

public class MonitorService {

    private final static long        DEFAULT_TIME_BETWEEN_COLLECT = 60 * 5;

    private ScheduledExecutorService scheduler;
    private int                      schedulerThreadSize          = 1;

    private long                     timeBeetweenSqlCollect       = DEFAULT_TIME_BETWEEN_COLLECT;
    private long                     timeBeetweenSpringCollect    = DEFAULT_TIME_BETWEEN_COLLECT;
    private long                     timeBeetweenWebUriCollect    = DEFAULT_TIME_BETWEEN_COLLECT;
    private TimeUnit                 timeUnit                     = TimeUnit.SECONDS;

    private MonitorDao               dao;

    public void start() {
        if (scheduler == null) {
            scheduler = new ScheduledThreadPoolExecutor(schedulerThreadSize);
        }

        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                collectSql();
            }
        }, timeBeetweenSqlCollect, timeBeetweenSqlCollect, timeUnit);

        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                collectSpringMethod();
            }
        }, timeBeetweenSpringCollect, timeBeetweenSpringCollect, timeUnit);

        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                collectWebURI();
            }
        }, timeBeetweenWebUriCollect, timeBeetweenWebUriCollect, timeUnit);
    }

    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    @SuppressWarnings("resource")
    private void collectSql() {
        Set<Object> dataSources = DruidDataSourceStatManager.getInstances().keySet();

        List<DruidDataSourceStatValue> statValueList = new ArrayList<DruidDataSourceStatValue>(dataSources.size());

        for (Object item : dataSources) {
            if (!(item instanceof DruidDataSource)) {
                continue;
            }

            DruidDataSource dataSource = (DruidDataSource) item;
            DruidDataSourceStatValue statValue = dataSource.getStatValueAndReset();
            statValueList.add(statValue);
        }

        MonitorContext ctx = createContext();
        dao.saveSql(ctx, statValueList);
    }

    private MonitorContext createContext() {
        MonitorContext ctx = new MonitorContext();
        ctx.setCollectTime(new Date());
        ctx.setPID(IOUtils.getPID());
        ctx.setCollectTime(IOUtils.getStartTime());
        return ctx;
    }

    private void collectSpringMethod() {
        List<SpringMethodStatValue> statValueList = new ArrayList<SpringMethodStatValue>();

        Set<Object> stats = SpringStatManager.getInstance().getSpringStatSet();

        for (Object item : stats) {
            if (!(item instanceof SpringStat)) {
                continue;
            }

            SpringStat sprintStat = (SpringStat) item;
            statValueList.addAll(sprintStat.getStatList(true));
        }

        MonitorContext ctx = createContext();
        dao.saveSpringMethod(ctx, statValueList);
    }

    private void collectWebURI() {
        List<WebURIStatValue> webURIValueList = new ArrayList<WebURIStatValue>();
        List<WebAppStatValue> webAppStatValueList = new ArrayList<WebAppStatValue>();

        Set<Object> stats = WebAppStatManager.getInstance().getWebAppStatSet();

        for (Object item : stats) {
            if (!(item instanceof WebAppStat)) {
                continue;
            }

            WebAppStat webAppStat = (WebAppStat) item;
            webURIValueList.addAll(webAppStat.getURIStatValueList(true));
        }

        MonitorContext ctx = createContext();
        dao.saveWebURI(ctx, webURIValueList);
        dao.saveWebApp(ctx, webAppStatValueList);
    }

    public MonitorDao getDao() {
        return dao;
    }

    public void setDao(MonitorDao dao) {
        this.dao = dao;
    }

    public long getTimeBeetweenSqlCollect() {
        return timeBeetweenSqlCollect;
    }

    public void setTimeBeetweenSqlCollect(long timeBeetweenSqlCollect) {
        this.timeBeetweenSqlCollect = timeBeetweenSqlCollect;
    }

    public long getTimeBeetweenSpringCollect() {
        return timeBeetweenSpringCollect;
    }

    public void setTimeBeetweenSpringCollect(long timeBeetweenSpringCollect) {
        this.timeBeetweenSpringCollect = timeBeetweenSpringCollect;
    }

    public long getTimeBeetweenWebUriCollect() {
        return timeBeetweenWebUriCollect;
    }

    public void setTimeBeetweenWebUriCollect(long timeBeetweenWebUriCollect) {
        this.timeBeetweenWebUriCollect = timeBeetweenWebUriCollect;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

}

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

import static com.alibaba.druid.util.Utils.getBoolean;
import static com.alibaba.druid.util.Utils.getInteger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceStatValue;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.druid.support.http.stat.WebAppStat;
import com.alibaba.druid.support.http.stat.WebAppStatManager;
import com.alibaba.druid.support.http.stat.WebAppStatValue;
import com.alibaba.druid.support.http.stat.WebURIStatValue;
import com.alibaba.druid.support.monitor.dao.MonitorDao;
import com.alibaba.druid.support.spring.stat.SpringMethodStatValue;
import com.alibaba.druid.support.spring.stat.SpringStat;
import com.alibaba.druid.support.spring.stat.SpringStatManager;
import com.alibaba.druid.util.Utils;
import com.alibaba.druid.wall.WallProviderStatValue;

public class MonitorClient {

    private final static long        DEFAULT_TIME_BETWEEN_COLLECT = 60 * 5;

    private ScheduledExecutorService scheduler;
    private int                      schedulerThreadSize          = 1;

    private long                     timeBeetweenSqlCollect       = DEFAULT_TIME_BETWEEN_COLLECT;
    private long                     timeBeetweenSpringCollect    = DEFAULT_TIME_BETWEEN_COLLECT;
    private long                     timeBeetweenWebUriCollect    = DEFAULT_TIME_BETWEEN_COLLECT;
    private TimeUnit                 timeUnit                     = TimeUnit.SECONDS;

    private boolean                  collectSqlEnable             = true;
    private boolean                  collectSqlWallEnable         = true;
    private boolean                  collectSpringMethodEanble    = true;
    private boolean                  collectWebAppEanble          = true;
    private boolean                  collectWebURIEnable          = true;

    private MonitorDao               dao;

    public MonitorClient(){
        configFromPropety(System.getProperties());
    }

    public void configFromPropety(Properties properties) {
        {
            Integer value = getInteger(properties, "druid.monitor.client.schedulerThreadSize");
            if (value != null) {
                this.setSchedulerThreadSize(value);
            }
        }

        {
            Integer value = getInteger(properties, "druid.monitor.client.timeBeetweenSqlCollect");
            if (value != null) {
                this.setTimeBeetweenSqlCollect(value);
            }
        }
        {
            Integer value = getInteger(properties, "druid.monitor.client.timeBeetweenSpringCollect");
            if (value != null) {
                this.setTimeBeetweenSpringCollect(value);
            }
        }
        {
            Integer value = getInteger(properties, "druid.monitor.client.timeBeetweenWebUriCollect");
            if (value != null) {
                this.setTimeBeetweenWebUriCollect(value);
            }
        }

        {
            Boolean value = getBoolean(properties, "druid.monitor.client.collectSqlEnable");
            if (value != null) {
                this.setCollectSqlEnable(value);
            }
        }

        {
            Boolean value = getBoolean(properties, "druid.monitor.client.collectSqlWallEnable");
            if (value != null) {
                this.setCollectSqlWallEnable(value);
            }
        }

        {
            Boolean value = getBoolean(properties, "druid.monitor.client.collectSpringMethodEanble");
            if (value != null) {
                this.setCollectSpringMethodEanble(value);
            }
        }

        {
            Boolean value = getBoolean(properties, "druid.monitor.client.collectWebAppEanble");
            if (value != null) {
                this.setCollectWebAppEanble(value);
            }
        }

        {
            Boolean value = getBoolean(properties, "druid.monitor.client.collectWebURIEnable");
            if (value != null) {
                this.setCollectWebURIEnable(value);
            }
        }
    }
    
    public void stop() {
        
    }

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
    public void collectSql() {
        if ((!collectSqlEnable) && !collectSqlWallEnable) {
            return;
        }

        Set<Object> dataSources = DruidDataSourceStatManager.getInstances().keySet();

        List<DruidDataSourceStatValue> statValueList = new ArrayList<DruidDataSourceStatValue>(dataSources.size());
        List<WallProviderStatValue> wallStatValueList = new ArrayList<WallProviderStatValue>();

        for (Object item : dataSources) {
            if (!(item instanceof DruidDataSource)) {
                continue;
            }

            DruidDataSource dataSource = (DruidDataSource) item;

            if (collectSqlEnable) {
                DruidDataSourceStatValue statValue = dataSource.getStatValueAndReset();
                statValueList.add(statValue);
            }

            if (collectSqlWallEnable) {
                WallProviderStatValue wallStatValue = dataSource.getWallStatValue(true);
                if (wallStatValue != null && wallStatValue.getCheckCount() > 0) {
                    wallStatValueList.add(wallStatValue);
                }
            }
        }

        MonitorContext ctx = createContext();

        if (statValueList.size() > 0) {
            dao.saveSql(ctx, statValueList);
        }

        if (wallStatValueList.size() > 0) {
            dao.saveSqlWall(ctx, wallStatValueList);
        }
    }

    private MonitorContext createContext() {
        MonitorContext ctx = new MonitorContext();
        ctx.setCollectTime(new Date());
        ctx.setPID(Utils.getPID());
        ctx.setCollectTime(Utils.getStartTime());
        return ctx;
    }

    private void collectSpringMethod() {
        if (!collectSpringMethodEanble) {
            return;
        }

        List<SpringMethodStatValue> statValueList = new ArrayList<SpringMethodStatValue>();

        Set<Object> stats = SpringStatManager.getInstance().getSpringStatSet();

        for (Object item : stats) {
            if (!(item instanceof SpringStat)) {
                continue;
            }

            SpringStat sprintStat = (SpringStat) item;
            statValueList.addAll(sprintStat.getStatList(true));
        }

        if (statValueList.size() > 0) {
            MonitorContext ctx = createContext();
            dao.saveSpringMethod(ctx, statValueList);
        }
    }

    private void collectWebURI() {
        if ((!collectWebAppEanble) && !collectWebURIEnable) {
            return;
        }

        List<WebURIStatValue> webURIValueList = new ArrayList<WebURIStatValue>();
        List<WebAppStatValue> webAppStatValueList = new ArrayList<WebAppStatValue>();

        Set<Object> stats = WebAppStatManager.getInstance().getWebAppStatSet();

        for (Object item : stats) {
            if (!(item instanceof WebAppStat)) {
                continue;
            }

            WebAppStat webAppStat = (WebAppStat) item;

            if (collectWebAppEanble) {
                WebAppStatValue webAppStatValue = webAppStat.getStatValue(true);
                webAppStatValueList.add(webAppStatValue);
            }

            if (collectWebURIEnable) {
                webURIValueList.addAll(webAppStat.getURIStatValueList(true));
            }
        }

        MonitorContext ctx = createContext();

        if (webURIValueList.size() > 0) {
            dao.saveWebURI(ctx, webURIValueList);
        }

        if (webAppStatValueList.size() > 0) {
            dao.saveWebApp(ctx, webAppStatValueList);
        }
    }

    public List<JdbcSqlStatValue> loadSqlList(Map<String, Object> filters) {
        return dao.loadSqlList(filters);
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

    public boolean isCollectSqlEnable() {
        return collectSqlEnable;
    }

    public void setCollectSqlEnable(boolean collectSqlEnable) {
        this.collectSqlEnable = collectSqlEnable;
    }

    public boolean isCollectSqlWallEnable() {
        return collectSqlWallEnable;
    }

    public void setCollectSqlWallEnable(boolean collectSqlWallEnable) {
        this.collectSqlWallEnable = collectSqlWallEnable;
    }

    public boolean isCollectSpringMethodEanble() {
        return collectSpringMethodEanble;
    }

    public void setCollectSpringMethodEanble(boolean collectSpringMethodEanble) {
        this.collectSpringMethodEanble = collectSpringMethodEanble;
    }

    public boolean isCollectWebAppEanble() {
        return collectWebAppEanble;
    }

    public void setCollectWebAppEanble(boolean collectWebAppEanble) {
        this.collectWebAppEanble = collectWebAppEanble;
    }

    public boolean isCollectWebURIEnable() {
        return collectWebURIEnable;
    }

    public void setCollectWebURIEnable(boolean collectWebURIEnable) {
        this.collectWebURIEnable = collectWebURIEnable;
    }

    public int getSchedulerThreadSize() {
        return schedulerThreadSize;
    }

    public void setSchedulerThreadSize(int schedulerThreadSize) {
        this.schedulerThreadSize = schedulerThreadSize;
    }

}

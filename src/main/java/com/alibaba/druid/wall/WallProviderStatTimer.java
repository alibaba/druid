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
package com.alibaba.druid.wall;

import static com.alibaba.druid.util.IOUtils.getInteger;
import static com.alibaba.druid.util.IOUtils.getLong;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.IOUtils;

public class WallProviderStatTimer implements Runnable {

    private final static Log             LOG         = LogFactory.getLog(WallProviderStatTimer.class);

    private ScheduledExecutorService     scheduler;
    private int                          threadCount = 1;
    private long                         period      = 60 * 5;
    private TimeUnit                     unit        = TimeUnit.SECONDS;

    private final List<WallProvider>     providers   = new CopyOnWriteArrayList<WallProvider>();

    private WallProviderStatLogger       statLogger  = new WallProviderStatLoggerImpl();

    private static WallProviderStatTimer instance;

    public WallProviderStatTimer(){
        configFromPropety(System.getProperties());
    }

    public static WallProviderStatTimer getInstance() {
        return instance;
    }

    public static void setInstance(WallProviderStatTimer instance) {
        WallProviderStatTimer.instance = instance;
    }

    public void configFromPropety(Properties properties) {
        {
            Long value = getLong(properties, "druid.wall.timer.period");
            if (value != null && value.intValue() > 0) {
                this.period = value.intValue();
            }
        }
        {
            Integer value = getInteger(properties, "druid.wall.timer.threadCount");
            if (value != null && value.intValue() > 0) {
                this.threadCount = value.intValue();
            }
        }
        {
            String value = properties.getProperty("druid.wall.timer.statLoggerClass");
            if (value != null && value.length() > 0) {
                Class<?> clazz = IOUtils.loadClass(value);
                try {
                    statLogger = (WallProviderStatLogger) clazz.newInstance();
                } catch (Exception ex) {
                    LOG.error("configStatLogger error", ex);
                }
            }
        }
    }

    public List<WallProvider> getProviders() {
        return providers;
    }
    
    public void register(WallProvider provider) {
        this.providers.add(provider);
    }

    public void setProviders(List<WallProvider> providers) {
        this.providers.clear();
        this.providers.addAll(providers);
    }

    public WallProviderStatLogger getStatLogger() {
        return statLogger;
    }

    public void setStatLogger(WallProviderStatLogger statLogger) {
        this.statLogger = statLogger;
    }

    public void start() {
        if (statLogger == null) {
            throw new IllegalStateException("statLogger is null");
        }

        scheduler = Executors.newScheduledThreadPool(threadCount);
        scheduler.scheduleAtFixedRate(this, period, period, unit);
    }

    @Override
    public void run() {
        for (WallProvider provider : this.providers) {
            WallProviderStatValue statValue = getStatValue(provider);
            log(statValue);
        }
    }

    protected WallProviderStatValue getStatValue(WallProvider provider) {
        return provider.getStatValue(true);
    }

    protected void log(WallProviderStatValue statValue) {
        statLogger.log(statValue);
    }
}

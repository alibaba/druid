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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WallProviderStatTimer implements Runnable {

    private ScheduledExecutorService scheduler;
    private int                      threadCount = 1;
    private long                     period      = 60 * 5;
    private TimeUnit                 unit        = TimeUnit.SECONDS;

    private final List<WallProvider> providers   = new CopyOnWriteArrayList<WallProvider>();

    private WallProviderStatLogger   statLogger;

    public WallProviderStatTimer(){

    }

    public List<WallProvider> getProviders() {
        return providers;
    }

    public void setProviders(List<WallProvider> providers) {
        this.providers.clear();
        this.providers.addAll(providers);
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

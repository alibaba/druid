/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.support.profile;

import static com.alibaba.druid.util.JdbcSqlStatUtils.get;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class ProfileEntryStat {

    private volatile     long                                     executeCount            = 0;
    private volatile     long                                     executeTimeNanos        = 0;
    private static final AtomicLongFieldUpdater<ProfileEntryStat> executeCountUpdater;
    private static final AtomicLongFieldUpdater<ProfileEntryStat> executeTimeNanosUpdater;

    static {
        executeCountUpdater = AtomicLongFieldUpdater.newUpdater(ProfileEntryStat.class, "executeCount");
        executeTimeNanosUpdater = AtomicLongFieldUpdater.newUpdater(ProfileEntryStat.class, "executeTimeNanos");
    }

    public ProfileEntryStatValue getValue(boolean reset) {
        ProfileEntryStatValue val = new ProfileEntryStatValue();

        val.setExecuteCount(get(this, executeCountUpdater, reset));
        val.setExecuteTimeNanos(get(this, executeTimeNanosUpdater, reset));

        return val;
    }

    public long getExecuteCount() {
        return executeCount;
    }

    public void addExecuteCount(long delta) {
        executeCountUpdater.addAndGet(this, delta);
    }

    public long getExecuteTimeNanos() {
        return executeTimeNanos;
    }

    public void addExecuteTimeNanos(long delta) {
        executeTimeNanosUpdater.addAndGet(this, delta);
    }
}

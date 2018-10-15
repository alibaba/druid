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
package com.alibaba.druid.wall;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class WallDenyStat {

    private volatile long                             denyCount;

    private volatile long                             lastDenyTimeMillis;

    private volatile long                             resetCount;

    final static AtomicLongFieldUpdater<WallDenyStat> denyCountUpdater  = AtomicLongFieldUpdater.newUpdater(WallDenyStat.class,
                                                                                                            "denyCount");

    final static AtomicLongFieldUpdater<WallDenyStat> resetCountUpdater = AtomicLongFieldUpdater.newUpdater(WallDenyStat.class,
                                                                                                            "resetCount");

    public long incrementAndGetDenyCount() {
        lastDenyTimeMillis = System.currentTimeMillis();
        return denyCountUpdater.incrementAndGet(this);
    }

    public long getDenyCount() {
        return denyCount;
    }

    public Date getLastDenyTime() {
        if (lastDenyTimeMillis <= 0) {
            return null;
        }
        return new Date(lastDenyTimeMillis);
    }

    public void reset() {
        lastDenyTimeMillis = 0;
        denyCount = 0;
        resetCountUpdater.incrementAndGet(this);
    }

    public long getResetCount() {
        return this.resetCount;
    }

}

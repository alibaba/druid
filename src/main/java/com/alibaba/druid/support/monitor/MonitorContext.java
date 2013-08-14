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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MonitorContext {

    private final Map<String, Object> atrributes = new HashMap<String, Object>();

    private Date                      collectTime;
    private int                       pid;
    private Date                      startTime;

    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Map<String, Object> getAtrributes() {
        return atrributes;
    }

    public int getPID() {
        return pid;
    }

    public void setPID(int pid) {
        this.pid = pid;
    }

}

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
package com.alibaba.druid.util;

import java.util.ArrayList;
import java.util.List;

public class TransactionInfo {

    private final long         id;
    private final List<String> sqlList = new ArrayList<String>(4);
    private final long         startTimeMillis;
    private long               endTimeMillis;

    public TransactionInfo(long id){
        this.id = id;
        this.startTimeMillis = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public List<String> getSqlList() {
        return sqlList;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public void setEndTimeMillis() {
        if (endTimeMillis == 0) {
            endTimeMillis = System.currentTimeMillis();
        }
    }

    public void setEndTimeMillis(long endTimeMillis) {
        this.endTimeMillis = endTimeMillis;
    }

}

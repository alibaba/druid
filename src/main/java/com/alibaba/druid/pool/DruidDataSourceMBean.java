/*
 * Copyright 2011 Alibaba Group. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.alibaba.druid.pool;

import java.util.List;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public interface DruidDataSourceMBean {

    int getInitialSize();

    String getUsername();

    String getPassword();

    String getUrl();

    String getDriverClassName();

    long getConnectCount();

    long getCloseCount();

    long getConnectErrorCount();

    int getPoolingSize();

    long getRecycleCount();

    int getActiveCount();

    long getCreateCount();

    long getDestroyCount();

    long getCreateTimespanMillis();

    int getQueryTimeout();

    boolean isActiveConnectionTraceEnable();

    void setActiveConnectionTraceEnable(boolean connectStackTraceEnable);

    List<String> getActiveConnectionStackTrace();
}

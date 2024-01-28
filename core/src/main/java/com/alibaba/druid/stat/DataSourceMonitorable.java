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

package com.alibaba.druid.stat;

import com.alibaba.druid.proxy.jdbc.DataSourceProxy;

import javax.management.JMException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

/**
 * 外部数据要想能被使用druid sql 监控和防火墙， 需要实现此接口.
 *
 * 并在初始化调用DruidDataSourceStatManager.addDataSource，close 时调用DruidDataSourceStatManager.removeDataSource
 */
public interface DataSourceMonitorable extends DataSourceProxy, Closeable {
    void resetStat();

    String getInitStackTrace();

    CompositeDataSupport getCompositeData() throws JMException;

    ObjectName getObjectName();

    List<Map<String, Object>> getPoolingConnectionInfo();
}

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
package com.alibaba.druid.support.monitor.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.pool.DruidDataSourceStatValue;
import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.druid.support.http.stat.WebAppStatValue;
import com.alibaba.druid.support.http.stat.WebURIStatValue;
import com.alibaba.druid.support.monitor.MonitorContext;
import com.alibaba.druid.support.spring.stat.SpringMethodStatValue;
import com.alibaba.druid.wall.WallProviderStatValue;

public interface MonitorDao {

    void saveSql(MonitorContext ctx, List<DruidDataSourceStatValue> statList);

    void saveSqlWall(MonitorContext ctx, List<WallProviderStatValue> statList);

    void saveSpringMethod(MonitorContext ctx, List<SpringMethodStatValue> methodList);

    void saveWebURI(MonitorContext ctx, List<WebURIStatValue> uriList);

    void saveWebApp(MonitorContext ctx, List<WebAppStatValue> uriList);

    List<JdbcSqlStatValue> loadSqlList(Map<String, Object> filters);

    void insertAppIfNotExits(String domain, String app) throws SQLException;

    void insertClusterIfNotExits(String domain, String app, String cluster) throws SQLException;

    void insertOrUpdateInstance(String domain, String app, String cluster, String host, String ip, Date startTime,
                                long pid) throws SQLException;
}

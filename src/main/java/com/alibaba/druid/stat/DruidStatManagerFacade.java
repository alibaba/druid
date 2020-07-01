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

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.druid.VERSION;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.support.http.stat.WebAppStatManager;
import com.alibaba.druid.support.spring.stat.SpringStatManager;
import com.alibaba.druid.util.DruidDataSourceUtils;
import com.alibaba.druid.util.JdbcSqlStatUtils;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.druid.util.Utils;

/**
 * 监控相关的对外数据暴露
 * 
 * 1. 为了支持jndi数据源本类内部调用druid相关对象均需要反射调用,返回值也应该是Object,List&lt;Object&gt;,Map&lt;String,Object&gt;等无关于druid的类型
 * 2. 对外暴露的public方法都应该先调用init()，应该有更好的方式，暂时没想到
 * 
 * @author sandzhang[sandzhangtoo@gmail.com]
 */
public final class DruidStatManagerFacade {

    private final static DruidStatManagerFacade instance    = new DruidStatManagerFacade();
    private boolean                             resetEnable = true;
    private final AtomicLong                    resetCount  = new AtomicLong();

    private DruidStatManagerFacade(){
    }

    public static DruidStatManagerFacade getInstance() {
        return instance;
    }

    private Set<Object> getDruidDataSourceInstances() {
        return DruidDataSourceStatManager.getInstances().keySet();
    }

    public Object getDruidDataSourceByName(String name) {
        for (Object o : this.getDruidDataSourceInstances()) {
            String itemName = DruidDataSourceUtils.getName(o);
            if (StringUtils.equals(name, itemName)) {
                return o;
            }
        }

        return null;
    }

    public void resetDataSourceStat() {
        DruidDataSourceStatManager.getInstance().reset();
    }

    public void resetSqlStat() {
        JdbcStatManager.getInstance().reset();
    }

    public void resetAll() {
        if (!isResetEnable()) {
            return;
        }

        SpringStatManager.getInstance().resetStat();
        WebAppStatManager.getInstance().resetStat();
        resetSqlStat();
        resetDataSourceStat();
        resetCount.incrementAndGet();
    }

    public void logAndResetDataSource() {
        if (!isResetEnable()) {
            return;
        }
        DruidDataSourceStatManager.getInstance().logAndResetDataSource();
    }

    public boolean isResetEnable() {
        return resetEnable;
    }

    public void setResetEnable(boolean resetEnable) {
        this.resetEnable = resetEnable;
    }

    public Object getSqlStatById(Integer id) {
        for (Object ds : getDruidDataSourceInstances()) {
            Object sqlStat = DruidDataSourceUtils.getSqlStat(ds, id);
            if (sqlStat != null) {
                return sqlStat;
            }
        }
        return null;
    }

    public Map<String, Object> getDataSourceStatData(Integer id) {
        if (id == null) {
            return null;
        }

        Object datasource = getDruidDataSourceById(id);
        return datasource == null ? null : dataSourceToMapData(datasource, false);
    }

    public Object getDruidDataSourceById(Integer identity) {
        if (identity == null) {
            return null;
        }

        for (Object datasource : getDruidDataSourceInstances()) {
            if (System.identityHashCode(datasource) == identity) {
                return datasource;
            }
        }
        return null;
    }

    public List<Map<String, Object>> getSqlStatDataList(Integer dataSourceId) {
        Set<Object> dataSources = getDruidDataSourceInstances();

        if (dataSourceId == null) {
            JdbcDataSourceStat globalStat = JdbcDataSourceStat.getGlobal();

            List<Map<String, Object>> sqlList = new ArrayList<Map<String, Object>>();

            DruidDataSource globalStatDataSource = null;
            for (Object datasource : dataSources) {
                if (datasource instanceof DruidDataSource) {
                    if (((DruidDataSource) datasource).getDataSourceStat() == globalStat) {
                        if (globalStatDataSource == null) {
                            globalStatDataSource = (DruidDataSource) datasource;
                        } else {
                            continue;
                        }
                    }
                }
                sqlList.addAll(getSqlStatDataList(datasource));
            }

            return sqlList;
        }

        for (Object datasource : dataSources) {
            if (dataSourceId != null && dataSourceId.intValue() != System.identityHashCode(datasource)) {
                continue;
            }

            return getSqlStatDataList(datasource);
        }

        return new ArrayList<Map<String, Object>>();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getWallStatMap(Integer dataSourceId) {
        Set<Object> dataSources = getDruidDataSourceInstances();

        if (dataSourceId == null) {
            Map<String, Object> map = new HashMap<String, Object>();

            for (Object datasource : dataSources) {
                Map<String, Object> wallStat = DruidDataSourceUtils.getWallStatMap(datasource);
                map = mergeWallStat(map, wallStat);
            }

            return map;
        }

        for (Object datasource : dataSources) {
            if (dataSourceId != null && dataSourceId.intValue() != System.identityHashCode(datasource)) {
                continue;
            }

            return DruidDataSourceUtils.getWallStatMap(datasource);
        }

        return new HashMap<String, Object>();
        //
    }

    /**
     * @deprecated
     * @return
     */
    public static Map mergWallStat(Map mapA, Map mapB) {
        return mergeWallStat(mapA, mapB);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map mergeWallStat(Map mapA, Map mapB) {
        if (mapA == null || mapA.size() == 0) {
            return mapB;
        }

        if (mapB == null || mapB.size() == 0) {
            return mapA;
        }

        Map<String, Object> newMap = new LinkedHashMap<String, Object>();
        for (Object item : mapB.entrySet()) {
            Map.Entry entry = (Map.Entry) item;
            String key = (String) entry.getKey();
            Object valueB = entry.getValue();
            Object valueA = mapA.get(key);

            if (valueA == null) {
                newMap.put(key, valueB);
            } else if (valueB == null) {
                newMap.put(key, valueA);
            } else if ("blackList".equals(key)) {
                Map<String, Map<String, Object>> newSet = new HashMap<String, Map<String, Object>>();

                Collection<Map<String, Object>> collectionA = (Collection<Map<String, Object>>) valueA;
                for (Map<String, Object> blackItem : collectionA) {
                    if (newSet.size() >= 1000) {
                        break;
                    }

                    String sql = (String) blackItem.get("sql");
                    Map<String, Object> oldItem = newSet.get(sql);
                    newSet.put(sql, mergeWallStat(oldItem, blackItem));
                }

                Collection<Map<String, Object>> collectionB = (Collection<Map<String, Object>>) valueB;
                for (Map<String, Object> blackItem : collectionB) {
                    if (newSet.size() >= 1000) {
                        break;
                    }

                    String sql = (String) blackItem.get("sql");
                    Map<String, Object> oldItem = newSet.get(sql);
                    newSet.put(sql, mergeWallStat(oldItem, blackItem));
                }
                newMap.put(key, newSet.values());
            } else {
                if (valueA instanceof Map && valueB instanceof Map) {
                    Object newValue = mergeWallStat((Map) valueA, (Map) valueB);
                    newMap.put(key, newValue);
                } else if (valueA instanceof Set && valueB instanceof Set) {
                    Set<Object> set = new HashSet<Object>();
                    set.addAll((Set) valueA);
                    set.addAll((Set) valueB);
                    newMap.put(key, set);
                } else if (valueA instanceof List && valueB instanceof List) {
                    List<Map<String, Object>> mergedList = mergeNamedList((List) valueA, (List) valueB);
                    newMap.put(key, mergedList);
                } else if (valueA instanceof long[] && valueB instanceof long[]) {
                    long[] arrayA = (long[]) valueA;
                    long[] arrayB = (long[]) valueB;

                    int len = arrayA.length >= arrayB.length ? arrayA.length : arrayB.length;
                    long[] sum = new long[len];

                    for (int i = 0; i < sum.length; ++i) {
                        if (i < arrayA.length) {
                            sum[i] += arrayA.length;
                        }
                        if (i < arrayB.length) {
                            sum[i] += arrayB.length;
                        }
                    }
                    newMap.put(key, sum);
                } else if (valueA instanceof String && valueB instanceof String) {
                    newMap.put(key, valueA);
                } else {
                    Object sum = SQLEvalVisitorUtils.add(valueA, valueB);
                    newMap.put(key, sum);
                }
            }
        }

        return newMap;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static List<Map<String, Object>> mergeNamedList(List listA, List listB) {
        Map<String, Map<String, Object>> mapped = new HashMap<String, Map<String, Object>>();
        for (Object item : (List) listA) {
            Map<String, Object> map = (Map<String, Object>) item;
            String name = (String) map.get("name");
            mapped.put(name, map);
        }

        List<Map<String, Object>> mergedList = new ArrayList<Map<String, Object>>();
        for (Object item : (List) listB) {
            Map<String, Object> mapB = (Map<String, Object>) item;
            String name = (String) mapB.get("name");
            Map<String, Object> mapA = mapped.get(name);

            Map<String, Object> mergedMap = mergeWallStat(mapA, mapB);
            mergedList.add(mergedMap);
        }

        return mergedList;
    }

    public List<Map<String, Object>> getSqlStatDataList(Object datasource) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<?, ?> sqlStatMap = DruidDataSourceUtils.getSqlStatMap(datasource);
        for (Object sqlStat : sqlStatMap.values()) {
            Map<String, Object> data = JdbcSqlStatUtils.getData(sqlStat);

            long executeCount = (Long) data.get("ExecuteCount");
            long runningCount = (Long) data.get("RunningCount");

            if (executeCount == 0 && runningCount == 0) {
                continue;
            }

            result.add(data);
        }

        return result;
    }

    public Map<String, Object> getSqlStatData(Integer id) {
        if (id == null) {
            return null;
        }

        Object sqlStat = getSqlStatById(id);

        if (sqlStat == null) {
            return null;
        }

        return JdbcSqlStatUtils.getData(sqlStat);
    }

    public List<Map<String, Object>> getDataSourceStatDataList() {
        return getDataSourceStatDataList(false);
    }

    public List<Map<String, Object>> getDataSourceStatDataList(boolean includeSqlList) {
        List<Map<String, Object>> datasourceList = new ArrayList<Map<String, Object>>();
        for (Object dataSource : getDruidDataSourceInstances()) {
            datasourceList.add(dataSourceToMapData(dataSource, includeSqlList));
        }
        return datasourceList;
    }

    public List<List<String>> getActiveConnStackTraceList() {
        List<List<String>> traceList = new ArrayList<List<String>>();
        for (Object dataSource : getDruidDataSourceInstances()) {
            List<String> stacks = ((DruidDataSource) dataSource).getActiveConnectionStackTrace();
            if (stacks.size() > 0) {
                traceList.add(stacks);
            }
        }
        return traceList;
    }

    public Map<String, Object> returnJSONBasicStat() {
        Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
        dataMap.put("Version", VERSION.getVersionNumber());
        dataMap.put("Drivers", getDriversData());
        dataMap.put("ResetEnable", isResetEnable());
        dataMap.put("ResetCount", getResetCount());
        dataMap.put("JavaVMName", System.getProperty("java.vm.name"));
        dataMap.put("JavaVersion", System.getProperty("java.version"));
        dataMap.put("JavaClassPath", System.getProperty("java.class.path"));
        dataMap.put("StartTime", Utils.getStartTime());
        return dataMap;
    }

    public long getResetCount() {
        return resetCount.get();
    }

    private List<String> getDriversData() {
        List<String> drivers = new ArrayList<String>();
        for (Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements();) {
            Driver driver = e.nextElement();
            drivers.add(driver.getClass().getName());
        }
        return drivers;
    }

    public List<Map<String, Object>> getPoolingConnectionInfoByDataSourceId(Integer id) {
        Object datasource = getDruidDataSourceById(id);

        if (datasource == null) {
            return null;
        }

        return DruidDataSourceUtils.getPoolingConnectionInfo(datasource);
    }

    public List<String> getActiveConnectionStackTraceByDataSourceId(Integer id) {
        Object datasource = getDruidDataSourceById(id);

        if (datasource == null || !DruidDataSourceUtils.isRemoveAbandoned(datasource)) {
            return null;
        }

        return DruidDataSourceUtils.getActiveConnectionStackTrace(datasource);
    }

    private Map<String, Object> dataSourceToMapData(Object dataSource, boolean includeSql) {
        Map<String, Object> map = DruidDataSourceUtils.getStatData(dataSource);

        if (includeSql) {
            List<Map<String, Object>> sqlList = getSqlStatDataList(dataSource);
            map.put("SQL", sqlList);
        }

        return map;
    }
}
